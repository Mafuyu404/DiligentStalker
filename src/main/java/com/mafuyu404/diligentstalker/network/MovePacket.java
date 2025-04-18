package com.mafuyu404.diligentstalker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MovePacket {
    private final int entityId;
    private final Vec3 direction;

    public MovePacket(int entityId, Vec3 direction) {
        this.entityId = entityId;
        this.direction = direction;
    }

    public static void encode(MovePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeVector3f(msg.direction.toVector3f());
    }

    public static MovePacket decode(FriendlyByteBuf buffer) {
        return new MovePacket(buffer.readInt(), new Vec3(buffer.readVector3f()));
    }

    public static void handle(MovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.add(msg.direction));
            entity.hurtMarked = true;
        });
        ctx.get().setPacketHandled(true);
    }
}
