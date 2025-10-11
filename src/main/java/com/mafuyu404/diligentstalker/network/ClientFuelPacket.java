package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ClientFuelPacket implements Packet {
    private final int entityId;
    private final int fuel;

    public ClientFuelPacket(int entityId, int fuel) {
        this.entityId = entityId;
        this.fuel = fuel;
    }

    public static ClientFuelPacket decode(FriendlyByteBuf buffer) {
        return new ClientFuelPacket(buffer.readInt(), buffer.readInt());
    }

    public static void handle(ClientFuelPacket msg, Minecraft client) {
        client.execute(() -> {
            ClientStalkerUtil.updateFuel(msg.entityId, msg.fuel);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(fuel);
    }
}
