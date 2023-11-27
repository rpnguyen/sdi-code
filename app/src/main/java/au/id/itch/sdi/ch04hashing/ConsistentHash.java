package au.id.itch.sdi.ch04hashing;

import com.google.common.hash.Hashing;

import java.util.*;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ConsistentHash implements CacheCluster {


    /**
     * serverHash => serverName.
     * Keeping it sorted by serverHash allows for O(logn) lookup
     */
    private final TreeMap<Integer, String> ring;

    public ConsistentHash(List<String> serverNames) {
        ring = new TreeMap<>();

        for (String serverName : serverNames) {
            addServer(serverName);
        }
    }

    @Override
    public String getServerName(String key) {
        int keyHash = f(key);
        ring.navigableKeySet().toArray();

        return ring.get(
                // wrap around to start
                firstNonNull(ring.ceilingKey(keyHash), ring.firstKey())
        );
    }

    @Override
    public void addServer(String name) {
        System.out.println("Adding " + name);
        int serverHash = f(name + "_A");
        ring.put(serverHash, name);
        serverHash = f(name + "_B");
        ring.put(serverHash, name);
    }

    @Override
    public void removeServer(String name) {
        System.out.println("Removing " + name);
        int serverHash = f(name + "_A");
        ring.remove(serverHash);
        serverHash = f(name + "_B");
        ring.remove(serverHash);
    }

    public void printRing(List<String> keys) {
        TreeMap<Integer, String> newRing = new TreeMap<>(ring);

        for (Map.Entry<Integer, String> e : ring.entrySet()) {
            newRing.put(e.getKey(), e.getKey() + " ~~~ " + e.getValue());
        }

        for (String key : keys) {
            newRing.put(f(key), f(key) + " " + key);
        }

        for (String line : newRing.values()) {
            System.out.println(line);
        }
    }

    private static int f(String key) {
        return Hashing.murmur3_128().hashString(key, UTF_8).asInt();
    }
}
