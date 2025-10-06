package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.event.handler.StalkerControl;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public record RClickBlockPacket(Vec3 position, Vec3 viewVec) implements Packet {
    public static final Type<RClickBlockPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "rclick_block"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RClickBlockPacket> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of((
                    (buf, vec) -> {
                        buf.writeDouble(vec.x);
                        buf.writeDouble(vec.y);
                        buf.writeDouble(vec.z);
                    }),
                    buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            ), RClickBlockPacket::position,
            StreamCodec.of(
                    (buf, vec) -> {
                        buf.writeDouble(vec.x);
                        buf.writeDouble(vec.y);
                        buf.writeDouble(vec.z);
                    },
                    buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            ), RClickBlockPacket::viewVec,
            RClickBlockPacket::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, RClickBlockPacket msg) {
        server.execute(() -> {
            if (!Stalker.hasInstanceOf(player)) return;

            StalkerControl.RightClickBlock(player, msg.position, msg.viewVec);
        });
    }
}