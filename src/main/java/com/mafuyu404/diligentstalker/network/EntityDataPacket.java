package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(EntityDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().level();
            if (level == null) return;
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            entity.getPersistentData().merge(msg.nbt);
            entity.getPersistentData().put(ControllableUtils.CONTROL_INPUT_KEY, msg.nbt.get(ControllableUtils.CONTROL_INPUT_KEY));
        });
        ctx.get().setPacketHandled(true);
    }
}
