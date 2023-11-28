package au.id.itch.sdi.ch05hashing;

public interface ServerCluster {
    String getServerName(String key);
    void addServer(String name);
    void removeServer(String name);
}
