package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public record ExtractResultPreview<T>(IGObjectsSupplier<T> root, Int2IntMap index2Count) {
}
