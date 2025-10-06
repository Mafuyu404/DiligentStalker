package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ClientStalkerPacket implements Packet {
    private final int entityId;

    public ClientStalkerPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static ClientStalkerPacket decode(FriendlyByteBuf buf) {
        return new ClientStalkerPacket(buf.readInt());
    }


    public static void handle(ClientStalkerPacket msg, Minecraft client) {
        client.execute(() -> ClientStalkerUtil.clientConnect(msg.entityId));
    }
}
