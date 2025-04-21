package com.mafuyu404.diligentstalker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnlockPacket {
    private final Vec3 position;

    public UnlockPacket(Vec3 position) {
        this.position = position;
    }

    public static void encode(UnlockPacket msg, FriendlyByteBuf buffer) {
        buffer.writeVector3f(msg.position.toVector3f());
    }

    public static UnlockPacket decode(FriendlyByteBuf buffer) {
        return new UnlockPacket(new Vec3(buffer.readVector3f()));
    }

    public static void handle(UnlockPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

        });
        ctx.get().setPacketHandled(true);
    }
}
