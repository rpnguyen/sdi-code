package au.id.itch.sdi.ch08urlshortener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Base62UrlShortener implements UrlShortener {
    static final Map<Character, Integer> B62_TO_INT = new HashMap<>();
    static final Map<Integer, Character> INT_TO_B62 = new HashMap<>();

    static {
        char[] b62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i = 0; i < b62.length; i++) {
            B62_TO_INT.put(b62[i], i);
            INT_TO_B62.put(i, b62[i]);
        }
    }

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

        String shortUrl = base62Encode(id);

        db.put(id, new UrlPair(shortUrl, longUrl));
        longUrlReverseIndex.put(longUrl, id);
        return shortUrl;
    }

    @Override
    public String lengthen(String shortUrl) {
        long id = base62Decode(shortUrl);

        if (!db.containsKey(id)) throw new IllegalArgumentException(shortUrl + " does not exist");

        return db.get(id).longUrl();
    }

    String base62Encode(long l) {
        StringBuilder sb = new StringBuilder();

        long curr = l;
        while (true) {
            sb.insert(0, INT_TO_B62.get((int) (curr % 62)));
            if (curr > 62) {
                curr /= 62;
            } else {
                break;
            }
        }
        String s = sb.toString();
        System.out.println("encoded " + l + " => " + s);
        return s;
    }

    long base62Decode(String s) {
        long l = 0L;
        for (char c : s.toCharArray()) {
            l *= 62;
            l += B62_TO_INT.get(c);
        }
        System.out.println("decoded " + s + " => " + l);
        return l;
    }


    record UrlPair(String shortUrl, String longUrl) {
    }
}
