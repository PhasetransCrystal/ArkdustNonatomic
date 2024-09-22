package com.phasetranscrystal.nonard.eventdistribute;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class MarkedTreeElement {
    public final HashSet<IdentEvent<?>> endpoint = new HashSet<>();
    public final HashMap<ResourceLocation, MarkedTreeElement> bifurcation = new HashMap<>();

    public boolean isEmpty() {
        return endpoint.isEmpty() && bifurcation.isEmpty();
    }

    public boolean tidyUp() {
        if (isEmpty()) return true;
        bifurcation.values().removeIf(MarkedTreeElement::tidyUp);
        return isEmpty();
    }

    public boolean remove(IdentEvent<?> ident, ResourceLocation... childLoc) {
        if (childLoc.length == 0) {
            return endpoint.remove(ident);
        } else {
            return Optional.ofNullable(bifurcation.get(childLoc[0]))
                    .map(element -> element.remove(ident, Arrays.copyOfRange(childLoc, 1, childLoc.length)))
                    .orElse(false);
        }
    }

    public boolean removeIncludeChild(IdentEvent<?> ident) {
        boolean flag = endpoint.remove(ident);
        for (MarkedTreeElement element : bifurcation.values()) {
            flag = element.removeIncludeChild(ident) | flag;
        }
        return flag;
    }

    public boolean remove(Class<?> clazz, ResourceLocation... childLoc) {
        if (childLoc.length == 0) {
            return endpoint.removeIf(i -> i.event().equals(clazz));
        } else {
            return Optional.ofNullable(bifurcation.get(childLoc[0]))
                    .map(element -> element.remove(clazz, Arrays.copyOfRange(childLoc, 1, childLoc.length)))
                    .orElse(false);
        }
    }

    public boolean removeIncludeChild(Class<?> clazz) {
        boolean flag = endpoint.removeIf(i -> i.event().equals(clazz));
        for (MarkedTreeElement element : bifurcation.values()) {
            flag = element.removeIncludeChild(clazz) | flag;
        }
        return flag;
    }

    public boolean add(IdentEvent<?> ident, ResourceLocation... childLoc) {
        if (childLoc.length == 0) {
            return endpoint.add(ident);
        } else {
            return bifurcation.computeIfAbsent(childLoc[0], loc -> new MarkedTreeElement()).add(ident, Arrays.copyOfRange(childLoc, 1, childLoc.length));
        }
    }

    public Collection<IdentEvent<?>> removeEndPoint(ResourceLocation... childLoc) {
        if (childLoc.length == 0) {
            var collection = List.copyOf(endpoint);
            endpoint.clear();
            return collection;
        } else {
            return Optional.ofNullable(bifurcation.get(childLoc[0]))
                    .map(element -> element.removeEndPoint(Arrays.copyOfRange(childLoc, 1, childLoc.length)))
                    .orElse(Collections.emptyList());
        }
    }

    public Collection<IdentEvent<?>> remove(ResourceLocation... childLoc) {
        if (childLoc.length == 0) {
            var collection = new ArrayList<>(endpoint);
            bifurcation.values().stream().map(MarkedTreeElement::remove).forEach(collection::addAll);
            endpoint.clear();
            bifurcation.clear();
            return collection;
        } else {
            return Optional.ofNullable(bifurcation.remove(childLoc[0]))
                    .map(element -> element.remove(Arrays.copyOfRange(childLoc, 1, childLoc.length)))
                    .orElse(Collections.emptyList());
        }
    }

    public boolean contains(ResourceLocation... child) {
        if (child.length == 0) return endpoint.isEmpty();
        if (child.length == 1) return bifurcation.containsKey(child[0]);
        else
            return Optional.ofNullable(bifurcation.get(child[0])).map(i -> i.contains(Arrays.copyOfRange(child, 1, child.length))).orElse(false);
    }
}
