package au.id.itch.sdi.ch08urlshortener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UrlShortenerImpl implements UrlShortener {

    final Map<Long, UrlPair> db;
    final Map<String, Long> longUrlReverseIndex;
    final Random random;
    final Map<Character, Integer> b62ToInt;
    final Map<Integer, Character> intToB62;

    public UrlShortenerImpl() {
        db = new HashMap<>();
        longUrlReverseIndex = new HashMap<>();
        random = new Random();

        b62ToInt = new HashMap<>();
        intToB62 = new HashMap<>();
        char[] b62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i = 0; i < b62.length; i++) {
            b62ToInt.put(b62[i], i);
            intToB62.put(i, b62[i]);
        }
    }

    @Override
    public String shorten(String longUrl) {
        Long existingId = longUrlReverseIndex.get(longUrl);
        if (existingId != null) {
            return db.get(existingId).shortUrl();
        }

        long id = random.nextLong((long) Math.pow(62, 7));
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
            sb.insert(0, intToB62.get((int) (curr % 62)));
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
            l += b62ToInt.get(c);
        }
        System.out.println("decoded " + s + " => " + l);
        return l;
    }


    record UrlPair(String shortUrl, String longUrl) {
    }
}
