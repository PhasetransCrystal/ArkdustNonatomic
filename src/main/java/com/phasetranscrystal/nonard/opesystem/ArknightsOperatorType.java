package com.phasetranscrystal.nonard.opesystem;

import com.phasetranscrystal.nonard.opesystem.info.OperatorBasicInfo;
import com.phasetranscrystal.nonard.opesystem.info.OperatorSkillInfo;
import com.phasetranscrystal.nonard.opesystem.info.OperatorUpgradeInfo;
import com.phasetranscrystal.nonatomic.Registries;
import com.phasetranscrystal.nonatomic.core.OperatorType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public abstract class ArknightsOperatorType<T extends OperatorEntity> extends OperatorType {

    //TODO 信息注册事件 信息与默认信息的合并
    public final OperatorBasicInfo info;
    public final OperatorUpgradeInfo upgrade;
    public final List<OperatorSkillInfo<T>> skills;//根据最低解锁等级排序
    //职业 分支 天赋 武器 模组


    @Override
    public abstract @Nullable EntityType<T> getEntityType();

    @Override
    public BlockPos findPlaceForGenerate(ServerPlayer player, @Nullable BlockPos pos) {
        BlockPos finded = super.findPlaceForGenerate(player, pos);
        if (finded != null) return finded;

        Random r = new Random();
        for (int count = 0; count < 5; count++) {
            pos = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, player.blockPosition().offset(r.nextInt(-4, 5), 0, r.nextInt(-4, 5)));
            if (!getEntityType().isBlockDangerous(player.level().getBlockState(pos))) {
                return pos.above();
            }
        }
        return null;
    }

    public ResourceKey<OperatorType> getKey(){
        return Registries.OPERATOR_TYPE.getResourceKey(this).get();
    }

    public String toOperatorKey(){
        ResourceLocation location = getKey().location();
        return "arkdust.operator.type." + location.getNamespace() + '.' + location.getPath();
    }

    public String getNameTransKey(){
        return toOperatorKey() + ".name";
    }
}
