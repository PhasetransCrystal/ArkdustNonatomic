package com.phasetranscrystal.nonard.opesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonatomic.EventHooks;
import com.phasetranscrystal.nonatomic.Helper;
import com.phasetranscrystal.nonatomic.Nonatomic;
import com.phasetranscrystal.nonatomic.Registries;
import com.phasetranscrystal.nonatomic.core.OpeHandler;
import com.phasetranscrystal.nonatomic.core.Operator;
import com.phasetranscrystal.nonatomic.core.OperatorType;
import com.phasetranscrystal.nonatomic.core.player_opehandler.OpeHandlerNoRepetition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;

import java.util.*;

public class ArkOpeHandler extends OpeHandlerNoRepetition {
    public static final ResourceLocation CONTAINER_ID = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "operators");
    public static final Logger LOGGER = LogManager.getLogger("ArkdustNona:OpeHandler");

    public static final Codec<ArkOpeHandler> CODEC = RecordCodecBuilder.create(a -> a.group(
            Registries.OPERATOR_TYPE.byNameCodec().listOf().fieldOf("deploying").forGetter(i -> i.deploying),
            Registries.OPERATOR_TYPE.byNameCodec().listOf().fieldOf("history").forGetter(i -> i.lastDeployingList),
            Helper.mapLikeWithKeyProvider(Operator.CODEC, Operator::getType).fieldOf("operators").forGetter(i -> i.operators),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(OpeHandlerNoRepetition::ownerUUId),
            Codec.INT.fieldOf("unlocked_place").forGetter(i -> i.unlockedPlace)
    ).apply(a, ArkOpeHandler::new));

    private int unlockedPlace = 2;

    public ArkOpeHandler(List<OperatorType> deploying, List<OperatorType> lastDeployingList, Map<OperatorType, Operator> operators, UUID playerUUID, int unlockedPlace) {
        super(deploying, lastDeployingList, operators, playerUUID, CONTAINER_ID);
        setUnlockedPlace(unlockedPlace);
    }

    public ArkOpeHandler() {
        super(5, CONTAINER_ID);
    }

    public ArkOpeHandler(ServerPlayer player) {
        super(5, player, CONTAINER_ID);
    }

    public int getUnlockedPlace() {
        return unlockedPlace;
    }

    public void setUnlockedPlace(int unlockedPlace) {
        this.unlockedPlace = Math.clamp(2, 5, unlockedPlace);
    }

    @Override
    public int addDeploying(Operator ope, int exceptIndex, boolean simulate, boolean allowDispatch) {
        if (exceptIndex >= deploying.size() || deploying.contains(ope.getType())) return -1;
        if (exceptIndex >= 0) {
            if (exceptIndex < unlockedPlace && deploying.get(exceptIndex) == Nonatomic.PLACE_HOLDER.get()) {
                if (!simulate) deploying.set(exceptIndex, ope.identifier.type());
                return exceptIndex;
            } else if (!allowDispatch) return -1;
        }
        for (int i = 0; i < unlockedPlace; i++) {
            OperatorType type = deploying.get(i);
            if (type == Nonatomic.PLACE_HOLDER.get()) {
                if (!simulate) deploying.set(i, ope.identifier.type());
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean unlock(OperatorType type) {
        if (type instanceof ArknightsOperatorType) {
            return super.unlock(type);
        }
        LOGGER.warn("Attempted to unlock a non-arknights operator type {}, skipped.", type);
        return false;
    }

    public static class WorldAttach implements OpeHandler.GroupProvider {
        public static final Codec<WorldAttach> CODEC = Helper.mapLikeWithKeyProvider(ArkOpeHandler.CODEC, OpeHandlerNoRepetition::ownerUUId).xmap(WorldAttach::new, i -> i.data);

        private final Map<UUID, ArkOpeHandler> data;

        public WorldAttach(){
            this.data = new HashMap<>();
        }

        protected WorldAttach(Map<UUID, ArkOpeHandler> data) {
            this.data = data;
        }

        public boolean deploy(ArknightsOperatorType type, ServerPlayer player, BlockPos expectPos) {
            return findOperator(type, player).map(o -> o.deploy(true, false, expectPos) >= 0).orElseGet(() -> {
                EventHooks.deployFailed(null, player, -7);
                return false;
            });
        }

        public Optional<Operator> findOperator(ArknightsOperatorType type, ServerPlayer player) {
            return withPlayer(player).flatMap(handler -> handler.findOperator(new Operator.Identifier(type)));
        }

        @Override
        public Optional<ArkOpeHandler> withUUID(UUID playerUUID) {
            return Optional.ofNullable(data.get(playerUUID));
        }

        @Override
        public Optional<ArkOpeHandler> withPlayer(ServerPlayer player) {
            return Optional.of(data.computeIfAbsent(player.getUUID(), uuid -> new ArkOpeHandler(player)));
        }
    }
}
