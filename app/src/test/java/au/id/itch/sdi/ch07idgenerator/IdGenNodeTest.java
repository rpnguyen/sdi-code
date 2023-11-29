package au.id.itch.sdi.ch07idgenerator;

import org.junit.jupiter.api.Test;
import org.threeten.extra.MutableClock;

import java.time.temporal.ChronoField;

import static com.google.common.base.Strings.padStart;
import static java.lang.Integer.toBinaryString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IdGenNodeTest {

    @Test
    void testGet() {
        MutableClock clock = MutableClock.epochUTC();
        clock.set(ChronoField.SECOND_OF_MINUTE, 35);
        IdGenNode node1 = new IdGenNode((short) 42, clock);

        String binaryString = "";
        binaryString += "0"; // sign bit 0
        binaryString += padStart(toBinaryString(35), 36, '0'); // 36 time bits 000000000000000000000000000000100011
        binaryString += padStart(toBinaryString(42), 11, '0'); // 11 node bits 00000101010
        binaryString += padStart("1", 16, '0'); // 16 counter bits
        assertThat(padStart(Long.toBinaryString(node1.get()), 64, '0')).isEqualTo(binaryString);
    }

    @Test
    void testNodesAreUnique() {
        MutableClock clock = MutableClock.epochUTC();
        clock.set(ChronoField.SECOND_OF_MINUTE, 35);
        IdGenNode node1 = new IdGenNode((short) 42, clock);
        IdGenNode node2 = new IdGenNode((short) 2000, clock);

        assertThat(node1.get()).isNotEqualTo(node2.get());
    }
}