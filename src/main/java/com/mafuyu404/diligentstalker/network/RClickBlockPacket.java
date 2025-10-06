package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.handler.StalkerControl;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class RClickBlockPacket implements Packet {
    private final Vec3 position;
    private final Vec3 viewVec;

    public RClickBlockPacket(Vec3 position, Vec3 viewVec) {
        this.position = position;
        this.viewVec = viewVec;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVector3f(position.toVector3f());
        buffer.writeVector3f(viewVec.toVector3f());
    }

    public static RClickBlockPacket decode(FriendlyByteBuf buffer) {
        return new RClickBlockPacket(new Vec3(buffer.readVector3f()), new Vec3(buffer.readVector3f()));
    }

    public static void handle(MinecraftServer server, ServerPlayer player, RClickBlockPacket msg) {
        server.execute(() -> {
            if (!Stalker.hasInstanceOf(player)) return;

            StalkerControl.RightClickBlock(player, msg.position, msg.viewVec);
        });
    }
}
