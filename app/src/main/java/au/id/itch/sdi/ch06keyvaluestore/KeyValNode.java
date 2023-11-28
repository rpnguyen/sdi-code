package au.id.itch.sdi.ch06keyvaluestore;

import com.google.common.collect.ImmutableSortedMap;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeMap;

/**
 * Represents a single server that stores data via an LSM-tree structure (aka memtable + SSTables).
 * It's write-optimized (due to in-memory tree and on-disk sorted string tables) as opposed to
 * b-trees which are read-optimized (due to on-disk trees requiring random io and writing to multiple disk pages).
 * <p>
 * - MemTable (tree structured map)
 * - flushed to SSTableSegments (age-sorted list)
 * - of SSTables (key-sorted immutable map)
 */
public class KeyValNode implements KeyVal {

    static final String TOMBSTONE = "🪦";

    final MemTable memTable;

    // Ordered by time
    final Deque<SSTable> ssTableSegments;

    public KeyValNode() {
        memTable = new MemTable(new TreeMap<>());
        ssTableSegments = new ArrayDeque<>();
    }

    @Override
    public void put(String key, String value) {
        // Omitted: write-ahead log for durability

        if (memTable.size() >= 3) {
            SSTable newSSTable = new SSTable(ImmutableSortedMap.<String, String>naturalOrder()
                    .putAll(memTable.tree)
                    .build());
            ssTableSegments.push(newSSTable);
            memTable.clear();
        }

        memTable.put(key, value);

        // Omitted: SSTable compaction and leveling
    }

    // Tombstones are used to disambiguate from "key does not exist".
    // Without tombstones, we could return a value from an older SSTable instead of nuking it.
    @Override
    public void delete(String key) {
        put(key, TOMBSTONE);
    }

    @Override
    public String get(String key) {
        String val = memTable.get(key);

        for (SSTable ssTableSegment : ssTableSegments) {
            if (val != null) break;

            val = ssTableSegment.get(key);
        }

        return TOMBSTONE.equals(val)
                ? null
                : val;
    }

    @RequiredArgsConstructor
    static class MemTable implements KeyVal {
        final TreeMap<String, String> tree;

        @Override
        public void put(String key, String value) {
            tree.put(key, value);
        }

        @Override
        public void delete(String key) {
            tree.remove(key);
        }

        @Override
        public String get(String key) {
            return tree.get(key);
        }

        public void clear() {
            tree.clear();
        }

        public int size() {
            return tree.size();
        }
    }

    /**
     * Sorted strings table - immutable table of keyvals.
     * Since it's sorted, it can be done efficiently
     */
    @RequiredArgsConstructor
    static class SSTable implements KeyVal {

        /**
         * Not a tree, it's a sorted list under the covers!
         */
        final ImmutableSortedMap<String, String> sortedMap;

        @Override
        public void put(String key, String value) {
            throw new UnsupportedOperationException("SSTables are immutable!!");
        }

        @Override
        public void delete(String key) {
            throw new UnsupportedOperationException("SSTables are immutable!!");
        }

        @Override
        public String get(String key) {
            // Omitted: bloom filter to improve performance (early return null)
            // Omitted: sparse index to further improve lookup into the sorted collection
            return sortedMap.get(key); // Binary search under the covers
        }

        public int size() {
            return sortedMap.size();
        }
    }
}
