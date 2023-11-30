package au.id.itch.sdi.ch08urlshortener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UrlShortenerImpl implements UrlShortener {

    final Map<Long, UrlPair> db;
    final Random random;
    final HashMap<Character, Integer> b62ToInt;
    final HashMap<Integer, Character> intToB62;

    public UrlShortenerImpl() {
        db = new HashMap<>();
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
        long id = random.nextLong((long) Math.pow(62, 7));
        String shortUrl = base62Encode(id);

        db.put(id, new UrlPair(shortUrl, longUrl));
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

        while (true) {
            sb.insert(0, intToB62.get((int) (l % 62)));
            if (l > 62) {
                l /= 62;
            } else {
                break;
            }
        }
        return sb.toString();
    }

    long base62Decode(String s) {
        long l = 0L;
        for (char c : s.toCharArray()) {
            l *= 62;
            l += b62ToInt.get(c);
        }
        return l;
    }


    record UrlPair(String shortUrl, String longUrl) {
    }
}
