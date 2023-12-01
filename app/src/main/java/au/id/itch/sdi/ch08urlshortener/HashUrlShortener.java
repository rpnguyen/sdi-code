package au.id.itch.sdi.ch08urlshortener;

import com.google.common.hash.Hashing;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HashUrlShortener implements UrlShortener {

    /**
     * shortUrl->longUrl
     */
    final Map<String, String> db;

    public HashUrlShortener() {
        db = new HashMap<>();
    }

    @Override
    public String shorten(String longUrl) {
        // Reuse an existing shortUrl=>longUrl mapping
        String existingShortUrl = getExistingShortUrl(longUrl);
        if (existingShortUrl != null) return existingShortUrl;

        String shortUrl = tryGenerateShortUrl(longUrl);

        db.put(shortUrl, longUrl);
        return shortUrl;
    }

    @Override
    public String lengthen(String shortUrl) {
        return db.get(shortUrl);
    }

    String getExistingShortUrl(String longUrl) {
        for (int i = 0; i < 10; i++) {
            String candidateShortUrl = f(longUrl);
            String candidateLongUrl = db.get(candidateShortUrl);
            if (candidateLongUrl == null) {
                return null;
            } else if (candidateLongUrl.equals(longUrl)) { // found a match
                return candidateShortUrl;
            }

            // HASH collision! Append an arbitrary string to generate a new hash
            System.out.println("getExistingShortUrl - Hash collision detected, appending a tombstone - " + longUrl + "🪦");
            longUrl = longUrl + "🪦";
        }
        throw new IllegalStateException("Have so many short URL collisions etc");
    }

    String tryGenerateShortUrl(String longUrl) {
        for (int i = 0; i < 10; i++) {
            String candidateShortUrl = f(longUrl);
            if (!db.containsKey(candidateShortUrl)) {
                return candidateShortUrl;
            }

            // HASH collision! Append an arbitrary string to generate a new hash
            System.out.println("tryGenerateShortUrl - Hash collision detected, appending a tombstone - " + longUrl + "🪦");
            longUrl = longUrl + "🪦";
        }
        throw new IllegalStateException("Couldn't generate a good shortUrl");
    }

    static String f(String s) {
        return Hashing.crc32().hashString(s, UTF_8).toString()
                .substring(0, 7);  // take the first 7 characters only
    }
}
