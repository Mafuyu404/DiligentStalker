package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.api.remote.RemoteViewManager;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerRemoteConnectPacket {
    private final BlockPos blockPos;

    public ServerRemoteConnectPacket(BlockPos blockPos) {
        this.blockPos = blockPos == null ? BlockPos.ZERO : blockPos;
    }

    public static void encode(ServerRemoteConnectPacket msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.blockPos);
    }

    public static ServerRemoteConnectPacket decode(FriendlyByteBuf buffer) {
        return new ServerRemoteConnectPacket(buffer.readBlockPos());
    }

    public static void handle(ServerRemoteConnectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ServerStalkerUtil.setVisualCenter(player, msg.blockPos);
            if (msg.blockPos.equals(BlockPos.ZERO)) {
                RemoteViewManager.restorePlayerView(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
