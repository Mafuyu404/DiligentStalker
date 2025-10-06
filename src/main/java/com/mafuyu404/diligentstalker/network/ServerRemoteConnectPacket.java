package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record ServerRemoteConnectPacket(BlockPos blockPos) implements Packet {
    public static final Type<ServerRemoteConnectPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "server_remote_connect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerRemoteConnectPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerRemoteConnectPacket::blockPos,
            ServerRemoteConnectPacket::new
    );

    public ServerRemoteConnectPacket(BlockPos blockPos) {
        this.blockPos = blockPos == null ? BlockPos.ZERO : blockPos;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerRemoteConnectPacket msg) {
        server.execute(() -> {
            ServerStalkerUtil.setVisualCenter(player, msg.blockPos);
        });
    }
}