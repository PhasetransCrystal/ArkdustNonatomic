package com.phasetranscrystal.nonard.migrate.ingame_obj;

public record IGObjectsExtractorSet<T>(IGOExtractor<T>... extractors) {
    @SafeVarargs
    public IGObjectsExtractorSet {
    }


}
