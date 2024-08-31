package org.fabricmcpatcher.utils;

import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Triple;
import org.javatuples.Quartet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memoize {


    public static <T, U,X, R> TriFunction<T, U,X, R> memoize3(TriFunction<T, U,X, R> biFunction) {
        return new TriFunction<T, U,X, R>() {
            private final Map<Triple<T, U, X>, R> cache = new ConcurrentHashMap<>();

            public R apply(T a, U b, X c) {
                return this.cache.computeIfAbsent(Triple.of(a, b,c), pair -> biFunction.apply(pair.getLeft(), pair.getMiddle(), pair.getRight()));
            }

            public String toString() {
                return "memoize/3[function=" + biFunction + ", size=" + this.cache.size() + "]";
            }
        };
    }
    public static <T, U,V,Q, R> QuadFunction<T, U,V,Q, R> memoize4(QuadFunction<T, U,V,Q, R> biFunction) {
        return new QuadFunction<T, U, V, Q, R>() {
            private final Map<Quartet<T, U,V,Q>, R> cache = new ConcurrentHashMap<>();

            public R apply(T a, U b, V c, Q d) {
                return this.cache.computeIfAbsent(new Quartet<>(a, b,c,d), pair -> biFunction.apply(pair.getValue0(), pair.getValue1(), pair.getValue2(),pair.getValue3()));
            }

            public String toString() {
                return "memoize/4[function=" + biFunction + ", size=" + this.cache.size() + "]";
            }
        };
    }
}
