package au.id.itch.sdi.ch08urlshortener;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerImplTest {

    private Random random = new Random();

    @Test
    void b62EncodeDecode() {
        UrlShortenerImpl urlShortener = new UrlShortenerImpl();
        for (int i = 0; i < 10; i++) {
            long l = random.nextLong((long) Math.pow(62, 7));
            String encoded = urlShortener.base62Encode(l);
            long decoded = urlShortener.base62Decode(encoded);
            System.out.println(l + " => " + encoded + " => " + decoded);
            assertThat(decoded).isEqualTo(l);
        }
    }
}