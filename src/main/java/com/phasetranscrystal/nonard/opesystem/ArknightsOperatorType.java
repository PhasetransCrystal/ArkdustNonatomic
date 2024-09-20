package com.phasetranscrystal.nonard.opesystem;

import com.phasetranscrystal.nonard.preinfo.OperatorBaseAttributes;
import com.phasetranscrystal.nonatomic.Registries;
import com.phasetranscrystal.nonatomic.core.OperatorType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class ArknightsOperatorType extends OperatorType {
//    public final OperatorBaseAttributes baseAttributes;


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
        return "arkdust.operator." + location.getNamespace() + '.' + location.getPath();
    }
}
