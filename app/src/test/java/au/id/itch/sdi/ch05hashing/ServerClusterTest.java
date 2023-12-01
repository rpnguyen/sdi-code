package au.id.itch.sdi.ch05hashing;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class ServerClusterTest {

    static final List<String> KEYS = List.of("foo", "bar", "baz", "qux", "quux", "corge");

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

    @Test
    void rendezvousHash() {
        RendezvousHash rendezvousHash = new RendezvousHash(new ArrayList<>(asList("server_0", "server_1", "server_2")));
        rendezvousHash.printRing(KEYS);

        rendezvousHash.addServer("server_3");
        rendezvousHash.printRing(KEYS);
    }
}