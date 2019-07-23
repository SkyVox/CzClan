package com.skydhs.czclan.clan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClanMap<K, V> extends HashMap<K, V> {

    public ClanMap() {
        super();
    }

    public ClanMap(final int initialCapacity) {
        super(initialCapacity);
    }

    @SuppressWarnings("unchecked")
    public ClanMap(Map<? extends K, ? extends V> map) {
        super(map == null ? Collections.EMPTY_MAP : map);
    }

    @Override
    public V put(K key, V value) {
        return super.put(key, value);
    }
}