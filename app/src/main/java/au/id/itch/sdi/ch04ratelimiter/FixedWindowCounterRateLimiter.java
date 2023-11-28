package au.id.itch.sdi.ch04ratelimiter;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.MINUTES;

public class FixedWindowCounterRateLimiter implements RateLimiter {
    static final ChronoUnit FIXED_WINDOW_UNIT = MINUTES;
    final long limitPerWindow;
    final Clock clock;

    final TreeMap<Instant, Long> windows;

    public FixedWindowCounterRateLimiter(int limitPerWindow, Clock clock) {
        this.limitPerWindow = limitPerWindow;
        this.clock = clock;

        windows = new TreeMap<>();
        // Omitted: Thread to async remove old windows
    }

    @Override
    public boolean call() {
        Instant currWindow = clock.instant().truncatedTo(FIXED_WINDOW_UNIT);
        long callsInWindow = windows.getOrDefault(currWindow, 0L) + 1;

        if (callsInWindow > limitPerWindow) {
            return false;
        }

        windows.put(currWindow, callsInWindow);
        return true;
    }
}
