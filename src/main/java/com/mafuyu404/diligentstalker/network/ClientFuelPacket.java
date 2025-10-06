package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientFuelPacket(int entityId, int fuel) implements Packet {
    public static final Type<ClientFuelPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "client_fuel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientFuelPacket> STREAM_CODEC  = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientFuelPacket::entityId,
            ByteBufCodecs.VAR_INT, ClientFuelPacket::fuel,
            ClientFuelPacket::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC ;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientFuelPacket msg, Minecraft client) {
        client.execute(() -> {
            ClientStalkerUtil.updateFuel(msg.entityId, msg.fuel);
        });
    }
}