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
        assertThat(keyValNode.memTable.size()).isEqualTo(3);
        assertThat(keyValNode.ssTableSegments).isEmpty();
        keyValNode.put("d", "4");
        assertThat(keyValNode.memTable.size()).isEqualTo(1);
        assertThat(keyValNode.ssTableSegments).hasSize(1);
        assertThat(keyValNode.ssTableSegments.getFirst().size()).isEqualTo(3);

        assertThat(keyValNode.get("a")).isEqualTo("1");
        assertThat(keyValNode.get("d")).isEqualTo("4");

        // Test tombstoning
        keyValNode.delete("a"); // adds a tombstone to the memtable
        assertThat(keyValNode.get("a")).isEqualTo(null); // returns null as expected even though SSTable has "a=>1"
        assertThat(keyValNode.memTable.get("a")).isEqualTo(TOMBSTONE);
        assertThat(keyValNode.ssTableSegments.getFirst().get("a")).isEqualTo("1");
    }
}