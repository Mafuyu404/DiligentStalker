package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(ClientStalkerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            LocalPlayer player = Minecraft.getInstance().player;
            Entity entity = level.getEntity(msg.entityId);
            Stalker.connect(player, entity);
        });
        ctx.get().setPacketHandled(true);
    }
}
