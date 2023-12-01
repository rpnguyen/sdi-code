package au.id.itch.sdi.ch08urlshortener;

import au.id.itch.sdi.util.Base62;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Base62UrlShortener implements UrlShortener {

    final Map<Long, UrlPair> db;
    final Map<String, Long> longUrlReverseIndex;
    final Random random;

    public Base62UrlShortener() {
        db = new HashMap<>();
        longUrlReverseIndex = new HashMap<>();
        random = new Random();
    }

    @Override
    public String shorten(String longUrl) {
        // Reuse an existing shortUrl=>longUrl mapping
        Long existingId = longUrlReverseIndex.get(longUrl);
        if (existingId != null) {
            return db.get(existingId).shortUrl();
        }

        Long id = null;
        int i = 0;
        while (i < 10 && id == null) {
            long randomId = random.nextLong((long) Math.pow(62, 7));
            if (!db.containsKey(randomId)) {
                id = randomId;
            }
            i++;
        }
        if (id == null) {
            throw new IllegalStateException("Couldn't find a good ID");
        }

        String shortUrl = Base62.encode(id);

        db.put(id, new UrlPair(shortUrl, longUrl));
        longUrlReverseIndex.put(longUrl, id);
        return shortUrl;
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
