package au.id.itch.sdi.ch04hashing;

public interface CacheCluster {
    String getServerName(String key);
    void addServer(String name);
    void removeServer(String name);
}
