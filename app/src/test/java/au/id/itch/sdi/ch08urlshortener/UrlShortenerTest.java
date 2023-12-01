package au.id.itch.sdi.ch08urlshortener;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UrlShortenerTest {

    @Test
    void hashUrlShorten() {
        HashUrlShortener urlShortener = new HashUrlShortener();
        String longUrl = "google.com";
        String shortUrl = urlShortener.shorten(longUrl);
        assertThat(urlShortener.lengthen(shortUrl)).isEqualTo(longUrl);
    }

    @Test
    void hashShouldReturnSameShortUrlForSameLongUrl() {
        HashUrlShortener urlShortener = new HashUrlShortener();
        String longUrl = "google.com";
        String shortUrl1 = urlShortener.shorten(longUrl);
        String shortUrl2 = urlShortener.shorten(longUrl);
        assertThat(shortUrl1).isEqualTo(shortUrl2);
    }

    @Test
    void hashShouldHandleCollisionsGracefully() {
        HashUrlShortener urlShortener = new HashUrlShortener();
        // These strings hash to the same value
        String longUrl1 = "codding";
        String longUrl2 = "gnu";
        assertThat(HashUrlShortener.f(longUrl1)).isEqualTo(HashUrlShortener.f(longUrl2));

        String shortUrl1 = urlShortener.shorten(longUrl1);
        String shortUrl2 = urlShortener.shorten(longUrl2);
        assertThat(urlShortener.lengthen(shortUrl1)).isEqualTo(longUrl1);
        assertThat(urlShortener.lengthen(shortUrl2)).isEqualTo(longUrl2);
    }

    @Test
    void base62UrlShorten() {
        Base62UrlShortener urlShortener = new Base62UrlShortener();
        String longUrl = "google.com";
        String shortUrl = urlShortener.shorten(longUrl);
        assertThat(urlShortener.lengthen(shortUrl)).isEqualTo(longUrl);
    }

    @Test
    void base62ShouldReturnSameShortUrlForSameLongUrl() {
        Base62UrlShortener urlShortener = new Base62UrlShortener();
        String longUrl = "google.com";
        String shortUrl1 = urlShortener.shorten(longUrl);
        String shortUrl2 = urlShortener.shorten(longUrl);
        assertThat(shortUrl1).isEqualTo(shortUrl2);
    }
}