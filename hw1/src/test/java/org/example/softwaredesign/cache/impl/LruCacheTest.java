package org.example.softwaredesign.cache.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class LruCacheTest {

    @Test
    void shouldAllowNonNegativeCapacitiesOnly() {
        int negativeCapacity = -24;
        int zeroCapacity = 0;
        int positiveCapacity = 24;

        Assertions.assertThrows(AssertionError.class, () -> new LruCache<>(negativeCapacity));
        Assertions.assertThrows(AssertionError.class, () -> new LruCache<>(zeroCapacity));
        Assertions.assertDoesNotThrow(() -> new LruCache<>(positiveCapacity));
    }

    @Test
    void shouldNotAllowToCacheNullKeys() {
        int maxCapacity = 1;

        var cache = new LruCache<>(maxCapacity);

        Assertions.assertThrows(AssertionError.class, () -> cache.put(null, 42));
    }

    @Test
    void shouldNotAllowToCacheNullValues() {
        int maxCapacity = 1;

        var cache = new LruCache<>(maxCapacity);

        Assertions.assertThrows(AssertionError.class, () -> cache.put(42, null));
    }

    @Test
    void shouldReturnNothingWhenTheGivenKeyIsNotInCache() {
        int maxCapacity = 1;

        var cache = new LruCache<>(maxCapacity);

        var cachedValue = cache.get(42);
        Assertions.assertEquals(cachedValue, Optional.empty());
    }

    @Test
    void shouldCacheTheGivenKeyAndValue() {
        int maxCapacity = 1;
        String key = "hello", value = "world";

        var cache = new LruCache<>(maxCapacity);
        cache.put(key, value);

        var cachedValue = cache.get(key);
        Assertions.assertEquals(cachedValue, Optional.of(value));
    }

    @Test
    void shouldRemoveTheFirstPutKeyWhenCacheIsFull() {
        int maxCapacity = 1;
        String key1 = "first_key", value1 = "first_value";
        String key2 = "second_key", value2 = "second_value";

        var cache = new LruCache<>(maxCapacity);
        cache.put(key1, value1);
        cache.put(key2, value2);

        var cachedValue1 = cache.get(key1);
        Assertions.assertEquals(cachedValue1, Optional.empty());

        var cachedValue2 = cache.get(key2);
        Assertions.assertEquals(cachedValue2, Optional.of(value2));
    }

    @Test
    void shouldRemoveKeysWhichAreRequestedLongAgo() {
        int maxCapacity = 3;
        String key1 = "first_key", value1 = "first_value";
        String key2 = "second_key", value2 = "second_value";
        String key3 = "third_key", value3 = "third_value";
        String key4 = "fourth_key", value4 = "fourth_value";
        String key5 = "fifth_key", value5 = "fifth_value";

        var cache = new LruCache<>(maxCapacity);
        cache.put(key1, value1);
        cache.put(key2, value2);
        cache.put(key3, value3);
        cache.get(key1);
        cache.get(key2);

        cache.put(key4, value4);
        var cachedValue3 = cache.get(key3);
        Assertions.assertEquals(cachedValue3, Optional.empty());

        cache.put(key5, value5);
        var cachedValue1 = cache.get(key1);
        Assertions.assertEquals(cachedValue1, Optional.empty());

        var cachedValue2 = cache.get(key2);
        Assertions.assertEquals(cachedValue2, Optional.of(value2));

        var cachedValue4 = cache.get(key4);
        Assertions.assertEquals(cachedValue4, Optional.of(value4));

        var cachedValue5 = cache.get(key5);
        Assertions.assertEquals(cachedValue5, Optional.of(value5));
    }

    @Test
    void shouldRewriteValueWhenKeyIsAlreadyInCache() {
        int maxCapacity = 2;
        String key1 = "first_key", value1 = "first_value";
        String key2 = "second_key", value2 = "second_value";
        String modifiedValue2 = "modified_second_value";

        var cache = new LruCache<>(maxCapacity);
        cache.put(key1, value1);
        cache.put(key2, value2);
        cache.put(key2, modifiedValue2);

        cache.get(key1);
        var cachedValue1 = cache.get(key1);
        Assertions.assertEquals(cachedValue1, Optional.of(value1));

        cache.get(key2);
        var cachedValue2 = cache.get(key2);
        Assertions.assertEquals(cachedValue2, Optional.of(modifiedValue2));
    }

}
