package au.id.itch.sdi.ch07idgenerator;

import java.time.Clock;
import java.time.Instant;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.YEAR;

public class IdGenNode implements IdGenerator {
    final short nodeId;
    final Clock clock;
    short localCounter;

    public IdGenNode(short nodeId, Clock clock) {
        if (nodeId < 0 || nodeId >= 2048) {
            throw new IllegalArgumentException("nodeId must be 0-2047, we only have 11 bits");
        }
        this.nodeId = nodeId;
        this.clock = clock;
    }

    /**
     * <pre>
     * 64 bit (long) identifiers
     * ||----------------------------------||---------||--------------|
     * ±|               seconds            || nodeId  ||    counter   |
     * </pre>
     * <ul>
     * <li>1 bit for sign (always 0 but makes life easier with 2's complement)
     * <li>36 bits for epoch time seconds (as long as you're ok with this dying in a few millennia)
     * <li>11 bits for NodeId (assuming <= 2^11 nodes aka 2048)
     * <li>16 bits for localCounter (assuming no more than 2^16 requests per timestamp aka 64K)
     * </ul>
     */
    @Override
    public long get() {
        long id = 0L;
        id <<= 36;

        Instant instant = clock.instant();
        if (instant.atZone(UTC).get(YEAR) >= 4140) {
            throw new IllegalStateException("Too far in the future, can't represent in 36 bits");
        }
        id |= instant.getEpochSecond();
        id <<= 11;

        id |= nodeId;
        id <<= 16;

        synchronized (this) {
            id |= ++localCounter;
        }

        System.out.println("generated ID " + id + " - b" + Long.toBinaryString(id));

        return id;
    }
}
