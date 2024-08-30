package org.fabricmcpatcher.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.RenderPhase;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class Memoize3 {


    public static <T, U,X, R> TriFunction<T, U,X, R> memoize(TriFunction<T, U,X, R> biFunction) {
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
}
