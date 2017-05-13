package cn.gy.test.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by gy on 17-5-13.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    private int capacity;

    LRUCache(int capacity) {
        super(16, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
