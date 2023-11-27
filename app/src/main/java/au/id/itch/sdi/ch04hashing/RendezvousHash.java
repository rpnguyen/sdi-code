package au.id.itch.sdi.ch04hashing;

import com.google.common.hash.Hashing;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Because of its simplicity, lower overhead, and generality (it works for any k < n), rendezvous hashing is increasingly
 * being preferred over consistent hashing. Recent examples of its use include the Github load balancer, the Apache Ignite
 * distributed database, and by the Twitter EventBus pub/sub platform.
 */
public class RendezvousHash implements CacheCluster {

    private final List<String> serverNames;

    public RendezvousHash(List<String> serverNames) {
        this.serverNames = serverNames;
    }

    @Override
    public String getServerName(String key) {
        int highestScore = Integer.MIN_VALUE;
        String highestScoringServer = null;
        for (String serverName : serverNames) {
            int score = f(serverName + "|" + key); // Assumes pipe isn't in these strings
            if (score > highestScore) {
                highestScore = score;
                highestScoringServer = serverName;
            }
        }
        return highestScoringServer;
    }

    @Override
    public void addServer(String name) {
        System.out.println("Adding " + name);
        this.serverNames.add(name);
    }

    @Override
    public void removeServer(String name) {
        System.out.println("Removing " + name);
        this.serverNames.remove(name);
    }

    public void printRing(List<String> keys) {
        List<String> lines = new ArrayList<>();
        for (String serverName : serverNames) {
            lines.add(serverName + "  ~~~");
        }
        for (String key : keys) {
            lines.add(getServerName(key) + " " + key);
        }
        Collections.sort(lines);

        for (String line : lines) {
            System.out.println(line);
        }
    }

    private static int f(String key) {
        return Hashing.murmur3_128().hashString(key, UTF_8).asInt();
    }
}
