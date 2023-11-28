package au.id.itch.sdi.ch04ratelimiter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.MINUTES;

public class SlidingWindowCounterRateLimiter implements RateLimiter {
    static final ChronoUnit FIXED_WINDOW_UNIT = MINUTES;
    final long limitPerWindow;
    final Clock clock;

    final TreeMap<Instant, Long> windows;

    public SlidingWindowCounterRateLimiter(int limitPerWindow, Clock clock) {
        this.limitPerWindow = limitPerWindow;
        this.clock = clock;

        windows = new TreeMap<>();
        // Omitted: Thread to async remove old windows
    }

    @Override
    public boolean call() {
        Instant currWindow = clock.instant().truncatedTo(FIXED_WINDOW_UNIT);
        long callsInCurrWindow = windows.getOrDefault(currWindow, 0L) + 1;

        Instant prevWindow = clock.instant().truncatedTo(FIXED_WINDOW_UNIT).minus(Duration.of(1, FIXED_WINDOW_UNIT));
        double prevWindowFraction = 1 - (clock.instant().getEpochSecond() % 60) / 60.0;
        long callsInPrevWindow = windows.getOrDefault(prevWindow, 0L);

        double callsInWindow = callsInCurrWindow + (callsInPrevWindow * prevWindowFraction);
        if (callsInWindow > limitPerWindow) {
            return false;
        }

        windows.put(currWindow, callsInCurrWindow);
        return true;

    }
}
