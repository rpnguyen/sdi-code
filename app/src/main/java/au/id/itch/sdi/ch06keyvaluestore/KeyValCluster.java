package au.id.itch.sdi.ch06keyvaluestore;

import au.id.itch.sdi.ch05hashing.ConsistentHash;

import java.util.ArrayList;
import java.util.HashMap;

public class KeyValCluster implements KeyVal {
    final ConsistentHash consistentHash;
    final HashMap<String, KeyValNode> nodeMap;

    public KeyValCluster() {
        nodeMap = new HashMap<>();
        nodeMap.put("server_0", new KeyValNode());
        nodeMap.put("server_1", new KeyValNode());
        nodeMap.put("server_2", new KeyValNode());
        nodeMap.put("server_3", new KeyValNode());
        consistentHash = new ConsistentHash(new ArrayList<>(nodeMap.keySet()));
    }

    @Override
    public void put(String key, String value) {
        nodeMap.get(server(key)).put(key, value);
    }

    @Override
    public void delete(String key) {
        nodeMap.get(server(key)).delete(key);
    }

    @Override
    public String get(String key) {
        return nodeMap.get(server(key)).get(key);
    }

    String server(String key) {
        String server = consistentHash.getServerName(key);
        System.out.println("Key " + key + " mapped to " + server);
        return server;
    }
}
