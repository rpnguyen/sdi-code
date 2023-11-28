package au.id.itch.sdi.ch04ratelimiter;

import org.junit.jupiter.api.Test;
import org.threeten.extra.MutableClock;

import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <table border="1">
 * <tr><td>Algorithm</td><td>Pros</td><td>Cons</td></tr>
 * <tr><td>Token bucket</td><td>Efficient</td><td>Parameters may be hard to tune?</td></tr>
 * <tr><td>Fixed window counter</td><td>Efficient</td><td>Allows spikes at edges of a window</td></tr>
 * <tr><td>Sliding window log</td><td>Accurate</td><td>Storing each explicit timestamp is wasteful</td></tr>
 * <tr><td>Sliding window counter</td><td>Efficient (counters, not timestamps), probabilistically accurate</td><td>Hard to understand</td></tr>
 * </table>
 */
class RateLimiterTest {

    /*
    Bucket size 3, refill rate 2
    +-----+-----+-----+-----+
    X                       X
    ·     X                 ·
    ·     ·                 ·
    ·     ·                 ·
    +-----+-----+-----+-----+
    0     1     2     3     4
     */
    @Test
    void tokenBucket() {
        MutableClock clock = MutableClock.epochUTC();
        TokenBucketRateLimiter tokenBucket = new TokenBucketRateLimiter(3, 2, clock);

        // bucket starts with three tokens, so three (bucketSize) requests go through
        clock.set(time("00:00:00"));
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isFalse();

        // one minute passes, replenishing exactly 2 (refillRate) tokens
        clock.set(time("00:01:00"));
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isFalse();

        // more minutes pass and yet token count never exceeds the max bucket size of three
        clock.set(time("00:04:00"));
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isTrue();
        assertThat(tokenBucket.call()).isFalse();
    }

    /*
    2 per window, window = minute boundary
    +-----+-----+-----+-----+
    X     X                XX
    ·     ·                ··
    ·     ·                ·· <- this is bad
    +-----+-----+-----+-----+
    0     1     2     3     4
     */
    @Test
    void fixedWindowCounter() {
        MutableClock clock = MutableClock.epochUTC();
        FixedWindowCounterRateLimiter fixedWindowCounter = new FixedWindowCounterRateLimiter(2, clock);

        // First two succeed
        clock.set(time("00:00:00"));
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isFalse();

        // one minute passes, so the next 2 succeed
        clock.set(time("00:01:00"));
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isFalse();

        // Pathological case => can experience high rates around edges of a window (i.e. here, 4 calls around the same time)
        clock.set(time("00:03:59"));
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isFalse();
        clock.set(time("00:04:00"));
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isTrue();
        assertThat(fixedWindowCounter.call()).isFalse();
    }

    /*
    2 per window, sliding window size = 1 minute
    +-----+-----+-----+-----+
          X     X          X
          ·     ·          ·
          ·     ·          ·X <- fixed!
    +-----+-----+-----+-----+
    0     1     2     3     4
     */
    @Test
    void slidingWindowLog() {
        MutableClock clock = MutableClock.epochUTC();
        SlidingWindowLogRateLimiter slidingWindowLog = new SlidingWindowLogRateLimiter(2, clock);

        // First two succeed
        clock.set(time("00:01:01"));
        assertThat(slidingWindowLog.call()).isTrue();
        assertThat(slidingWindowLog.call()).isTrue();
        assertThat(slidingWindowLog.call()).isFalse();

        // one minute passes, so the next 2 succeed
        clock.set(time("00:02:01"));
        assertThat(slidingWindowLog.call()).isTrue();
        assertThat(slidingWindowLog.call()).isTrue();
        assertThat(slidingWindowLog.call()).isFalse();

        // FIXED ISSUE of the window boundary
        clock.set(time("00:03:59"));
        assertThat(slidingWindowLog.call()).isTrue();
        assertThat(slidingWindowLog.call()).isTrue();
        assertThat(slidingWindowLog.call()).isFalse();
        clock.set(time("00:04:00"));
        assertThat(slidingWindowLog.call()).isFalse();
    }

    /*
    (same as sliding window log but more efficient since it stores counters and not full timestamps)

    2 per window, window = minute boundary
    +-----+-----+-----+-----+-----+
    X                      X
    ·        X             ·    X
    ·        .             ·    . <- not strictly correct (3 in last 31s) but it's probabilistic
    +-----+-----+-----+-----+-----+
    0     1     2     3     4
     */
    @Test
    void slidingWindowCounter() {
        MutableClock clock = MutableClock.epochUTC();
        SlidingWindowCounterRateLimiter slidingWindowCounter = new SlidingWindowCounterRateLimiter(2, clock);

        // First two succeed
        clock.set(time("00:00:00"));
        assertThat(slidingWindowCounter.call()).isTrue();
        assertThat(slidingWindowCounter.call()).isTrue();
        assertThat(slidingWindowCounter.call()).isFalse();

        // Based on the window 00:00:30-00:01:30, the previous window is halved => thus allowing 3 in 90 seconds :)
        clock.set(time("00:01:30"));
        assertThat(slidingWindowCounter.call()).isTrue();
        assertThat(slidingWindowCounter.call()).isFalse();

        // Kind-of pathological case. Based on the window 00:03:30-00:04:30, the previous window is halved => thus allowing 3 in 30s :/
        clock.set(time("00:03:59"));
        assertThat(slidingWindowCounter.call()).isTrue();
        assertThat(slidingWindowCounter.call()).isTrue();
        assertThat(slidingWindowCounter.call()).isFalse();
        clock.set(time("00:04:30"));
        assertThat(slidingWindowCounter.call()).isTrue();
        assertThat(slidingWindowCounter.call()).isFalse();
    }

    private static TemporalAdjuster time(String text) {
        return t -> t.with(LocalTime.parse(text));
    }

}