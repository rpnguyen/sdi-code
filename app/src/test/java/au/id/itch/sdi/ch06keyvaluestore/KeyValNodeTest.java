package au.id.itch.sdi.ch06keyvaluestore;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeyValNodeTest {
    @Test
    void testFlushSSTable() {
        KeyValNode keyValNode = new KeyValNode();

        // memtable[a1,b2,c3], ssTableSegments[]
        keyValNode.put("a", "1");
        keyValNode.put("b", "2");
        keyValNode.put("c", "3");
        assertThat(keyValNode.memTable.size()).isEqualTo(3);
        assertThat(keyValNode.ssTableSegments).isEmpty();

        // memtable[], ssTableSegments[[a1,b2,c3,d4]]
        keyValNode.put("d", "4");
        assertThat(keyValNode.memTable.size()).isEqualTo(0);
        assertThat(keyValNode.ssTableSegments.getFirst().size()).isEqualTo(4);

        // memtable[e5], ssTableSegments[[a1,b2,c3,d4]]
        keyValNode.put("e", "5");
        assertThat(keyValNode.memTable.size()).isEqualTo(1);
        assertThat(keyValNode.ssTableSegments.getFirst().size()).isEqualTo(4);

        assertThat(keyValNode.get("a")).isEqualTo("1");
        assertThat(keyValNode.get("e")).isEqualTo("5");

        // memtable[a🪦,e5], ssTableSegments[[a1,b2,c3,d4]]
        keyValNode.delete("a"); // adds a tombstone to the memtable
        assertThat(keyValNode.get("a")).isEqualTo(null); // returns null as expected
        assertThat(keyValNode.ssTableSegments.getFirst().get("a")).isEqualTo("1"); // even though SSTable has "a1"!
    }
}