package au.id.itch.sdi.ch04hashing;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class CacheClusterTest {

    private static final List<String> KEYS = List.of("foo", "bar", "baz", "qux", "quux", "corge");

    @Test
    void naiveModuloHash() {
        NaiveModuloHash naiveModuloHash = new NaiveModuloHash(new ArrayList<>(asList("server_0", "server_1", "server_2")));
        naiveModuloHash.printRing(KEYS);

        naiveModuloHash.addServer("server_3");
        naiveModuloHash.printRing(KEYS);
    }

    @Test
    void consistentHash() {
        ConsistentHash consistentHash = new ConsistentHash(new ArrayList<>(asList("server_0", "server_1", "server_2")));
        consistentHash.printRing(KEYS);

        consistentHash.addServer("server_3");
        consistentHash.printRing(KEYS);
    }

    // TODO rendezvous hash
    // TODO redo consistent hash but without treemap!! just use a sortedset and regular "min gte" binary search

    // TODO: ADD THIS NOTE SOMEWHERE -> if you need a sorted list you can index into for binary search, you really want a treemap (sorts on insert) and get a list view of it
}