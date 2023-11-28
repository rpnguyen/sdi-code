package au.id.itch.sdi.ch06keyvaluestore;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KeyValClusterTest {

    @Test
    void happyCase() {
        KeyValCluster keyValCluster = new KeyValCluster();

        keyValCluster.put("a", "1");
        keyValCluster.put("b", "2");
        keyValCluster.put("c", "3");
        keyValCluster.put("d", "4");

        assertThat(keyValCluster.get("a")).isEqualTo("1");
        assertThat(keyValCluster.get("b")).isEqualTo("2");
        assertThat(keyValCluster.get("c")).isEqualTo("3");
        assertThat(keyValCluster.get("d")).isEqualTo("4");
    }
}