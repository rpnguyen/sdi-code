package au.id.itch.sdi.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Base62Test {

    static final Random RANDOM = new Random();

    @Test
    void base62EncodeDecode() {
        for (int i = 0; i < 10; i++) {
            long l = RANDOM.nextLong((long) Math.pow(62, 7));
            String encoded = Base62.encode(l);
            long decoded = Base62.decode(encoded);
            assertThat(decoded).isEqualTo(l);
        }
    }
}