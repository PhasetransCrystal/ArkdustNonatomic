package com.phasetranscrystal.nonard.migrate.nona_quench.damage;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ExtendedDamageSource extends DamageSource {
    public final DamageSourceContext context;
    public ExtendedDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition, DamageSourceContext context) {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.context = context;
    }

    public ExtendedDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, DamageSourceContext context) {
        super(type, directEntity, causingEntity);
        this.context = context;
    }

    public ExtendedDamageSource(Holder<DamageType> type, Vec3 damageSourcePosition, DamageSourceContext context) {
        super(type, damageSourcePosition);
        this.context = context;
    }

    public ExtendedDamageSource(Holder<DamageType> type, @Nullable Entity entity, DamageSourceContext context) {
        super(type, entity);
        this.context = context;
    }

    public ExtendedDamageSource(Holder<DamageType> type, DamageSourceContext context) {
        super(type);
        this.context = context;
    }

    public DamageSourceContext getContext() {
        return context;
    }
}
