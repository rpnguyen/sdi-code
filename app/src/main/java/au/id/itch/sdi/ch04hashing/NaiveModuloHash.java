package au.id.itch.sdi.ch04hashing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaiveModuloHash implements CacheCluster {

    private final List<String> serverNames;

    public NaiveModuloHash(List<String> serverNames) {
        this.serverNames = serverNames;
    }

    @Override
    public String getServerName(String key) {
        return serverNames.get(Math.floorMod(key.hashCode(), serverNames.size()));
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
}
