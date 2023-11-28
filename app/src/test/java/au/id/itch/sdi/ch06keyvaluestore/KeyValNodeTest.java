package au.id.itch.sdi.ch06keyvaluestore;

import org.junit.jupiter.api.Test;

import static au.id.itch.sdi.ch06keyvaluestore.KeyValNode.TOMBSTONE;
import static org.assertj.core.api.Assertions.assertThat;

class KeyValNodeTest {
    @Test
    void testFlushSSTable() {
        KeyValNode keyValNode = new KeyValNode();
        keyValNode.put("a", "1");
        keyValNode.put("b", "2");
        keyValNode.put("c", "3");
        assertThat(keyValNode.memtable).hasSize(3);
        assertThat(keyValNode.ssTableSegments).isEmpty();
        keyValNode.put("d", "4");
        assertThat(keyValNode.memtable).hasSize(1);
        assertThat(keyValNode.ssTableSegments).hasSize(1);
        assertThat(keyValNode.ssTableSegments.getFirst().entries).hasSize(3);

        assertThat(keyValNode.get("a")).isEqualTo("1");
        assertThat(keyValNode.get("d")).isEqualTo("4");

        // Test tombstoning
        keyValNode.delete("a"); // adds a tombstone to the memtable
        assertThat(keyValNode.get("a")).isEqualTo(null); // returns null as expected even though SSTable has "a=>1"
        assertThat(keyValNode.memtable.get("a")).isEqualTo(TOMBSTONE);
        assertThat(keyValNode.ssTableSegments.getFirst().entries.get("a")).isEqualTo("1");
    }
}