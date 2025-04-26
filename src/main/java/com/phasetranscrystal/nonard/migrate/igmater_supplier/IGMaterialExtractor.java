package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.resources.ResourceLocation;

public interface IGMaterialExtractor<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_extractor");

    Codec<IGMaterialExtractor<T>> codec();

    Class<T> targetClass();

    double requestCount();

    /**从提供的资源供应器中提取所需要的内容
     * @param simulate 是否为模拟，如果为true则只计数，不更改供应器内数据
     */
    default double extractFrom(boolean simulate, IGMaterialSupplier<?>... suppliers){
        //TODO 调度 见IGMSup.Alternate
        return 0;
    }

    double extractedFromDispatched(boolean simulate, IGMaterialSupplier<T>... suppliers);
}
