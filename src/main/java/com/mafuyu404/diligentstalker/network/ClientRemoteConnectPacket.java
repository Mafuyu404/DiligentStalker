package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ClientRemoteConnectPacket implements Packet {
    private final UUID stalkerId;
    private final BlockPos center;

    public ClientRemoteConnectPacket(UUID stalkerId, BlockPos center) {
        this.stalkerId = stalkerId;
        this.center = center;
    }

    public static ClientRemoteConnectPacket decode(FriendlyByteBuf buffer) {
        UUID stalkerId = buffer.readUUID();
        BlockPos center = buffer.readBlockPos();
        return new ClientRemoteConnectPacket(stalkerId, center);
    }

    public static void handle(ClientRemoteConnectPacket msg, Minecraft client) {
        client.execute(() -> {
            if (msg.center == null) {
                return;
            }
            ClientStalkerUtil.tryRemoteConnect(msg.center, entity -> entity.getUUID().equals(msg.stalkerId));
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(stalkerId);
        buffer.writeBlockPos(center);
    }
}