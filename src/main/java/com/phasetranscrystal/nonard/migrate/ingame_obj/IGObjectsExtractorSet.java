package com.phasetranscrystal.nonard.migrate.ingame_obj;

public record IGObjectsExtractorSet<T>(IGObjectsExtractor<T>... extractors) {
    @SafeVarargs
    public IGObjectsExtractorSet {
    }


}
