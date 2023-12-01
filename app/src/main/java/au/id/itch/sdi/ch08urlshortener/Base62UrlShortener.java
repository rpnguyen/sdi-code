package au.id.itch.sdi.ch08urlshortener;

import au.id.itch.sdi.util.Base62;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Base62UrlShortener implements UrlShortener {
    static final Random RANDOM = new Random();
    final Map<Long, UrlPair> db;
    final Map<String, Long> longUrlReverseIndex;

    public Base62UrlShortener() {
        db = new HashMap<>();
        longUrlReverseIndex = new HashMap<>();
    }

    @Override
    public String shorten(String longUrl) {
        // Reuse an existing shortUrl=>longUrl mapping
        Long existingId = longUrlReverseIndex.get(longUrl);
        if (existingId != null) {
            return db.get(existingId).shortUrl();
        }

        long id = generateUniqueId();
        String shortUrl = Base62.encode(id);

        db.put(id, new UrlPair(shortUrl, longUrl));
        longUrlReverseIndex.put(longUrl, id);
        return shortUrl;
    }

    long generateUniqueId() {
        for (int i = 0; i < 10; i++) {
            long randomId = RANDOM.nextLong((long) Math.pow(62, 7));
            if (!db.containsKey(randomId)) {
                return randomId;
            }
        }
        throw new IllegalStateException("Couldn't find a good ID");
    }

    @Override
    public String lengthen(String shortUrl) {
        long id = Base62.decode(shortUrl);

        if (!db.containsKey(id)) throw new IllegalArgumentException(shortUrl + " does not exist");

        return db.get(id).longUrl();
    }

    record UrlPair(String shortUrl, String longUrl) {
    }
}
