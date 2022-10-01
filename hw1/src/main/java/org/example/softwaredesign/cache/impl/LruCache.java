package org.example.softwaredesign.cache.impl;

import org.example.softwaredesign.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LruCache<K, V> implements Cache<K, V> {
    private final int maxCapacity;
    private int cachedValuesCount;
    private final Map<K, CachedNode<K, V>> keyValueStorage;
    private CachedNode<K, V> recentlyUsedNode, longAgoUsedNode;

    public LruCache(final int maxCapacity) {
        assert maxCapacity > 0;

        this.maxCapacity = maxCapacity;
        keyValueStorage = new HashMap<>();
    }

    public void put(final K key, final V value) {
        // Pre
        assert key != null && value != null;

        // Inv
        assert cachedValuesCount <= maxCapacity;

        var cachedNode = keyValueStorage.get(key);
        if (cachedNode != null) {
            removeCachedNodeFromList(cachedNode);
            cachedNode.value = value;
            prependCachedNodeToList(cachedNode);
        } else {
            if (cachedValuesCount == maxCapacity) {
                keyValueStorage.remove(longAgoUsedNode.key);
                removeCachedNodeFromList(longAgoUsedNode);
                cachedValuesCount--;

                // Inv
                assert cachedValuesCount <= maxCapacity;
            }
            var newCachedNode = new CachedNode<>(key, value);
            keyValueStorage.put(key, newCachedNode);
            prependCachedNodeToList(newCachedNode);
            cachedValuesCount++;
        }
        // Inv
        assert cachedValuesCount <= maxCapacity;

        // Post
        assert recentlyUsedNode.key == key && recentlyUsedNode.value == value;
    }

    public Optional<V> get(final K key) {
        // Pre
        assert key != null;

        // Inv
        assert cachedValuesCount <= maxCapacity;

        var cachedNode = keyValueStorage.get(key);
        boolean keyExists = cachedNode != null;

        V valueInCache = null;
        if (keyExists) {
            removeCachedNodeFromList(cachedNode);
            prependCachedNodeToList(cachedNode);
            valueInCache = cachedNode.value;

            // Inv
            assert cachedValuesCount <= maxCapacity;
        }

        // Post
        assert !keyExists || recentlyUsedNode.key == key;
        return Optional.ofNullable(valueInCache);
    }

    private void prependCachedNodeToList(final CachedNode<K, V> cachedValue) {
        var cachedValueUsedAfter = recentlyUsedNode;

        recentlyUsedNode = cachedValue;
        if (cachedValueUsedAfter == null) {
            longAgoUsedNode = cachedValue;
        } else {
            cachedValueUsedAfter.prev = cachedValue;
            cachedValue.next = cachedValueUsedAfter;
        }
    }

    private void removeCachedNodeFromList(final CachedNode<K, V> cachedNode) {
        final var cachedNodeUsedBefore = cachedNode.prev;
        final var cachedNodeUsedAfter = cachedNode.next;

        if (cachedNodeUsedBefore == null) {
            recentlyUsedNode = cachedNodeUsedAfter;
        } else {
            cachedNodeUsedBefore.next = cachedNodeUsedAfter;
            cachedNode.prev = null;
        }

        if (cachedNodeUsedAfter == null) {
            longAgoUsedNode = cachedNodeUsedBefore;
        } else {
            cachedNodeUsedAfter.prev = cachedNodeUsedBefore;
            cachedNode.next = null;
        }
    }

    private static class CachedNode<K, V> {
        CachedNode<K, V> prev, next;
        K key;
        V value;

        public CachedNode(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
    }
}
