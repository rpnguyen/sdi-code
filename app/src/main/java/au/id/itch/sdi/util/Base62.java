package au.id.itch.sdi.util;

import java.util.HashMap;
import java.util.Map;

public class Base62 {
    static final Map<Character, Integer> B62_TO_INT = new HashMap<>();
    static final Map<Integer, Character> INT_TO_B62 = new HashMap<>();

    static {
        char[] b62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i = 0; i < b62.length; i++) {
            Base62.B62_TO_INT.put(b62[i], i);
            Base62.INT_TO_B62.put(i, b62[i]);
        }
    }

    public static String encode(long l) {
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

    public static long decode(String s) {
        long l = 0L;
        for (char c : s.toCharArray()) {
            l *= 62;
            l += B62_TO_INT.get(c);
        }
        System.out.println("decoded " + s + " => " + l);
        return l;
    }
}
