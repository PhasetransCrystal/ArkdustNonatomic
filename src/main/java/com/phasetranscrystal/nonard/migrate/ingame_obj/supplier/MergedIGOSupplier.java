package com.phasetranscrystal.nonard.migrate.ingame_obj.supplier;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.phasetranscrystal.nonard.migrate.ingame_obj.ExtractResultPreview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MergedIGOSupplier<T> implements IGOSupplier<T>, IMultiIGOS {
    public static final Logger LOGGER = LogManager.getLogger("BreaIgose:IGOSupplier:Merged");
    public final List<IGOSupplier<T>> suppliers;
    public final Class<T> clazzCache;
    public final boolean isSnapshot;

    public MergedIGOSupplier(List<IGOSupplier<T>> suppliers) {
        if(suppliers.isEmpty()){
            LOGGER.error("Can't create without target class support. Use MergedIGOSupplier.<init>(Class<T>) to create an empty instance.");
            throw new IllegalArgumentException();
        }
        this.suppliers = buildContentList(suppliers);
        this.clazzCache = suppliers.getFirst().targetClass();
        this.isSnapshot = false;
    }

    @SafeVarargs
    public MergedIGOSupplier(IGOSupplier<T>... suppliers) {
        this(List.of(suppliers));
    }

    public MergedIGOSupplier(Class<T> clazz){
        this.suppliers = buildContentList(List.of());
        this.clazzCache = clazz;
        this.isSnapshot = false;
    }

    //only use for create snapshot
    protected MergedIGOSupplier(List<IGOSupplier<T>> listMutable, @Nullable Class<T> clazz) {
        this.suppliers = listMutable.stream().map(IGOSupplier::createSnapshot).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        this.clazzCache = clazz;
        this.isSnapshot = true;
    }

    //--[IGOS]--

    @Override
    public Class<T> targetClass() {
        return clazzCache;
    }

    @Override
    public int size() {
        return suppliers.stream().map(IGOSupplier::size).reduce(0, Integer::sum);
    }

    @Override
    public IGOSupplier<T> createSnapshot() {
        return new MergedIGOSupplier<>(this.suppliers, targetClass());
    }

    @Override
    public T get(int index) {
        Pair<IGOSupplier<T>, Integer> pair = indexTarget(index);
        return pair.getFirst().get(pair.getSecond());
    }

    @Override
    public boolean set(int index, T value) {
        if (!isVariable(index)) return false;
        Pair<IGOSupplier<T>, Integer> pair = indexTarget(index);
        return pair.getFirst().set(pair.getSecond(), value);
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isVariable(int index) {
        Pair<IGOSupplier<T>, Integer> pair = indexTarget(index);
        return pair.getFirst().isVariable(pair.getSecond());
    }

    @Override
    public boolean isSnapshot() {
        return isSnapshot;
    }

    @Override
    public void bindExtractResultPreview(ExtractResultPreview<T> resultPreview) {
        //TODO
    }

    //--[MultiIGOS]--

    @Override
    public @NotNull List<IGOSupplier<?>> getSuppliers() {
        return (List) suppliers;
    }

    @Override
    public <N> @NotNull List<IGOSupplier<N>> getSuppliers(@NotNull Class<N> clazz) {
        return clazz == this.targetClass() ? (List) suppliers : List.of();
    }

    @Override
    public boolean canAddSupplier(IGOSupplier<?> supplier) {
        return !isStable() && supplier.targetClass() == this.targetClass() && containsSupplier(supplier);
    }

    @Override
    public boolean canRemoveSupplier(IGOSupplier<?> supplier) {
        return !isStable() && supplier.targetClass() == this.targetClass() && containsSupplier(supplier);
    }

    @Override
    public boolean addSupplier(IGOSupplier<?> supplier) {
        if(!canAddSupplier(supplier)) return false;
        return suppliers.add((IGOSupplier<T>) supplier);
    }

    @Override
    public boolean removeSupplier(IGOSupplier<?> supplier) {
        if(!canRemoveSupplier(supplier)) return false;
        return suppliers.remove(supplier);
    }

    @Override
    public boolean containsSupplier(IGOSupplier<?> supplier) {
        return suppliers.contains(supplier);
    }

    //--[信息处理]--

    public Pair<IGOSupplier<T>, Integer> indexTarget(int index) {
        for (IGOSupplier<T> sup : suppliers) {
            if (index >= sup.size()) {
                index -= sup.size();
            } else return Pair.of(sup, index);
        }
        LOGGER.error("try to get MergedIGOSupplier[{}] while its size is {}. Is there any IGOS changed mistakenly?", index + size(), size());
        LOGGER.error("Instance details: {}", this);
        throw new IndexOutOfBoundsException();
    }

    protected List<IGOSupplier<T>> buildContentList(List<IGOSupplier<T>> suppliers) {
        return new ArrayList<>(suppliers);
    }

    @Override
    public String toString() {
        return "BreaIgose-IGOS-Merged{" +
                "suppliers=" + suppliers +
                ", isSnapshot=" + isSnapshot +
                '}';
    }
}
