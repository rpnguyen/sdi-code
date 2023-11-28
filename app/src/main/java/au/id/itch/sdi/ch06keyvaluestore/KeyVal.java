package au.id.itch.sdi.ch06keyvaluestore;

public interface KeyVal {
    void put(String key, String value);
    void delete(String key);
    String get(String key);
}
