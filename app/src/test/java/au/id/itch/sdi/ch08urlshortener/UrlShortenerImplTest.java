package au.id.itch.sdi.ch08urlshortener;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UrlShortenerImplTest {

    private final Random random = new Random();

    @Test
    void urlShorten() {
        UrlShortenerImpl urlShortener = new UrlShortenerImpl();
        String longUrl = "google.com";
        String shortUrl = urlShortener.shorten(longUrl);
        assertThat(urlShortener.lengthen(shortUrl)).isEqualTo(longUrl);
    }

    @Test
    void shouldReturnSameShortUrlForSameLongUrl() {
        UrlShortenerImpl urlShortener = new UrlShortenerImpl();
        String longUrl = "google.com";
        String shortUrl1 = urlShortener.shorten(longUrl);
        String shortUrl2 = urlShortener.shorten(longUrl);
        assertThat(shortUrl1).isEqualTo(shortUrl2);
    }

    @Test
    void b62EncodeDecode() {
        UrlShortenerImpl urlShortener = new UrlShortenerImpl();
        for (int i = 0; i < 10; i++) {
            long l = random.nextLong((long) Math.pow(62, 7));
            String encoded = urlShortener.base62Encode(l);
            long decoded = urlShortener.base62Decode(encoded);
            assertThat(decoded).isEqualTo(l);
        }
    }
}