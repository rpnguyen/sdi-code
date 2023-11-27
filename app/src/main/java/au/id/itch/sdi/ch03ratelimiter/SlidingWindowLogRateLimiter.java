package au.id.itch.sdi.ch03ratelimiter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Queue;

public class SlidingWindowLogRateLimiter implements RateLimiter {
    private static final Duration SLIDING_WINDOW_SIZE = Duration.ofMinutes(1);
    private final long limitPerWindow;
    private final Clock clock;

    private final Queue<Instant> log;

    public SlidingWindowLogRateLimiter(int limitPerWindow, Clock clock) {
        this.limitPerWindow = limitPerWindow;
        this.clock = clock;

        log = new ArrayDeque<>();
    }

    @Override
    public boolean call() {
        Instant windowValidityThreshold = clock.instant().minus(SLIDING_WINDOW_SIZE);
        while (!log.isEmpty() && !log.peek().isAfter(windowValidityThreshold)) {
            log.poll();
        }

        int calls = log.size() + 1;
        if (calls > limitPerWindow) {
            return false;
        }
        log.offer(clock.instant());
        return true;
    }
}
