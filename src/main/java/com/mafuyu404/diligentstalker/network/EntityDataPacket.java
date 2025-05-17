package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.api.PersistentDataHolder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityDataPacket {
    private final int entityId;
    private final CompoundTag nbt;

    public EntityDataPacket(int entityId, CompoundTag nbt) {
        this.entityId = entityId;
        this.nbt = nbt;
    }

    public static void encode(EntityDataPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeNbt(msg.nbt);
    }

    public static EntityDataPacket decode(FriendlyByteBuf buffer) {
        return new EntityDataPacket(buffer.readInt(), buffer.readNbt());
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        EntityDataPacket msg = decode(buf);
        server.execute(() -> {
            Level level = player.level();
            if (level == null) return;
            
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            PersistentDataHolder holder = (PersistentDataHolder) entity;
            holder.getPersistentData().merge(msg.nbt);
        });
    }
}
