package com.phasetranscrystal.nonard.network;

import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.event.ModEventHook;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SKeyInputPacket(long key) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<C2SKeyInputPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "c2s_key_input_packet"));

    public C2SKeyInputPacket(int key, int modifier, int action) {
        //默认key与modifier为正数，action只为0、1、2
        //将key放在低31位，modifier放在31-62位，action放在62-63位
        //为什么这么写，因为不会写int[]的codecs😭
        this(key + ((long) modifier << 31) + ((long) action << 62));
    }

    public static final StreamCodec<ByteBuf, C2SKeyInputPacket> STREAM_CODEC = ByteBufCodecs.VAR_LONG.map(C2SKeyInputPacket::new, C2SKeyInputPacket::key);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void serverHandler(C2SKeyInputPacket pack, IPayloadContext context) {
        ModEventHook.onKeyInput((ServerPlayer) context.player(), (int) (pack.key & 0x7FFFFFFFL), (int) (pack.key >>> 31 & 0x7FFFFFFFL), (int) (pack.key >>> 62 & 0x3L));
    }
}
