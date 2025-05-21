package com.phasetranscrystal.nonard.migrate.ingame_obj;

import com.phasetranscrystal.nonard.migrate.ingame_obj.supplier.IGOSupplier;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public record ExtractResultPreview<T>(IGOSupplier<T> root, Int2IntMap index2Count) {
}
