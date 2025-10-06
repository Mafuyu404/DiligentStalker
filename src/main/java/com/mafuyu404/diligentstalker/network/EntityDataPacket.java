package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.data.ModComponents;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityDataPacket implements Packet {
    private final int entityId;
    private final CompoundTag nbt;

    public EntityDataPacket(int entityId, CompoundTag nbt) {
        this.entityId = entityId;
        this.nbt = nbt;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeNbt(nbt);
    }

    public static EntityDataPacket decode(FriendlyByteBuf buffer) {
        return new EntityDataPacket(buffer.readInt(), buffer.readNbt());
    }

    public static void handle(MinecraftServer server, ServerPlayer player, EntityDataPacket msg) {
        server.execute(() -> {
            Level level = player.level();
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            ModComponents.STALKER_DATA.get(entity).getStalkerData().merge(msg.nbt);
            ModComponents.STALKER_DATA.get(entity).getStalkerData().put(ControllableUtils.CONTROL_INPUT_KEY, msg.nbt.get(ControllableUtils.CONTROL_INPUT_KEY));
        });
    }
}
