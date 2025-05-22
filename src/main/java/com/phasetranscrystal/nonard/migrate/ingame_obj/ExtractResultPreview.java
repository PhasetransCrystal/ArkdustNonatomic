package com.phasetranscrystal.nonard.migrate.ingame_obj;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.nonard.migrate.ingame_obj.supplier.IGOSupplier;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.function.Consumer;

public record ExtractResultPreview<T>(IGOSupplier<T> root, Int2IntMap index2Count) {


    public static class Element<T>{
        public final IGOExtractor<T> extractor;
        public final double extractedCount;
        public final ImmutableMap<Integer,T> extractedByIndex;
        protected final Consumer<IGOSupplier<T>> executor;

        public Element(IGOExtractor<T> extractor, double extractedCount, ImmutableMap<Integer,T> extractedByIndex, Consumer<IGOSupplier<T>> executor){
            this.extractor = extractor;
            this.extractedCount = extractedCount;
            this.extractedByIndex = extractedByIndex;
            this.executor = executor;
        }
    }
}
