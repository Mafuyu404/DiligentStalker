package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientRemoteConnectPacket{
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

    public static void handle(ClientRemoteConnectPacket msg,  Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (msg.center == null) {
                return;
            }
            ClientStalkerUtil.tryRemoteConnect(msg.center, entity -> entity.getUUID().equals(msg.stalkerId));
        });
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(stalkerId);
        buffer.writeBlockPos(center);
    }
}