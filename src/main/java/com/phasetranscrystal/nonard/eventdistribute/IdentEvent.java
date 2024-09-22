package com.phasetranscrystal.nonard.eventdistribute;

import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

public record IdentEvent<T extends Event>(Class<T> event, Consumer<T> listener) {
}
