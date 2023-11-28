package au.id.itch.sdi.ch04ratelimiter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class TokenBucketRateLimiter implements RateLimiter {
    final int bucketSize;
    final int refillRate;
    final Clock clock;

    long currTokens;
    /**
     * Optimization: instead of requiring a poller, just track last updated time and refill accordingly
     */
    Instant lastUpdatedTime;

    public TokenBucketRateLimiter(int bucketSize, int refillRate, Clock clock) {
        this.bucketSize = bucketSize;
        this.refillRate = refillRate;
        this.clock = clock;

        currTokens = bucketSize;
        lastUpdatedTime = clock.instant();
        // Omitted: Thread to async refill tokens
    }

    @Override
    public boolean call() {
        long minutesSinceLastUpdate = Duration.between(lastUpdatedTime, clock.instant()).toMinutes(); // TODO: ArithmeticException - duration too large
        if (minutesSinceLastUpdate > 0) {
            long newTokenCount = currTokens + (refillRate * minutesSinceLastUpdate); // TODO: long overflow
            currTokens = Math.min(bucketSize, newTokenCount); // prevent overflowing the bucket
        }

        if (currTokens <= 0) {
            return false;
        }

        currTokens--;
        lastUpdatedTime = clock.instant();
        return true;
    }

}
