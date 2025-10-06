package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerRemoteConnectPacket implements Packet {
    private final BlockPos blockPos;

    public ServerRemoteConnectPacket(BlockPos blockPos) {
        this.blockPos = blockPos == null ? BlockPos.ZERO : blockPos;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
    }

    public static ServerRemoteConnectPacket decode(FriendlyByteBuf buffer) {
        return new ServerRemoteConnectPacket(buffer.readBlockPos());
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerRemoteConnectPacket msg) {
        server.execute(() -> {
            ServerStalkerUtil.setVisualCenter(player, msg.blockPos);
        });
    }
}
