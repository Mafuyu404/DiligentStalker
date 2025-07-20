package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RClickBlockPacket {
    private final Vec3 position;
    private final Vec3 viewVec;

    public RClickBlockPacket(Vec3 position, Vec3 viewVec) {
        this.position = position;
        this.viewVec = viewVec;
    }

    public static void encode(RClickBlockPacket msg, FriendlyByteBuf buffer) {
        buffer.writeVector3f(msg.position.toVector3f());
        buffer.writeVector3f(msg.viewVec.toVector3f());
    }

    public static RClickBlockPacket decode(FriendlyByteBuf buffer) {
        return new RClickBlockPacket(new Vec3(buffer.readVector3f()), new Vec3(buffer.readVector3f()));
    }

    public static void handle(RClickBlockPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            ServerLevel level = player.serverLevel();
            if (!Stalker.hasInstanceOf(player)) return;

            StalkerControl.RightClickBlock(player, msg.position, msg.viewVec);
        });
        ctx.get().setPacketHandled(true);
    }
}
