package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.init.Stalker;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ClientStalkerPacket {
    private final int entityId;

    public ClientStalkerPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ClientStalkerPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
    }

    public static ClientStalkerPacket decode(FriendlyByteBuf buffer) {
        return new ClientStalkerPacket(buffer.readInt());
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ClientStalkerPacket msg = decode(buf);
        client.execute(() -> {
            ClientLevel level = client.level;
            LocalPlayer player = client.player;
            if (level == null || player == null) return;
            
            Entity entity = level.getEntity(msg.entityId);
            if (entity != null) {
                Stalker.connect(player, entity);
            }
        });
    }
}
