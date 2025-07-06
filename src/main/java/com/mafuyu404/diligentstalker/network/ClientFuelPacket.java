package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ClientFuelPacket {
    private final int entityId;
    private final int fuel;

    public ClientFuelPacket(int entityId, int fuel) {
        this.entityId = entityId;
        this.fuel = fuel;
    }

    public static void encode(ClientFuelPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeInt(msg.fuel);
    }

    public static ClientFuelPacket decode(FriendlyByteBuf buffer) {
        return new ClientFuelPacket(buffer.readInt(), buffer.readInt());
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ClientFuelPacket msg = decode(buf);
        client.execute(() -> {
            ClientLevel level = client.level;
            if (level == null) return;

            Entity entity = level.getEntity(msg.entityId);
            if (entity instanceof DroneStalkerEntity droneStalker) {
                droneStalker.setFuel(msg.fuel);
            }
        });
    }
}
