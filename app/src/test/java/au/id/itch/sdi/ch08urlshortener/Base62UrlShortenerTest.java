package au.id.itch.sdi.ch08urlshortener;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Base62UrlShortenerTest {

    private final Random random = new Random();

    @Test
    void base62EncodeDecode() {
        Base62UrlShortener urlShortener = new Base62UrlShortener();
        for (int i = 0; i < 10; i++) {
            long l = random.nextLong((long) Math.pow(62, 7));
            String encoded = urlShortener.base62Encode(l);
            long decoded = urlShortener.base62Decode(encoded);
            assertThat(decoded).isEqualTo(l);
        }
    }
}