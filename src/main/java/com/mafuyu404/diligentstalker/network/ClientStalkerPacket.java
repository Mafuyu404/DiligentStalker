package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientStalkerPacket(int entityId) implements Packet {
    public static final Type<ClientStalkerPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "client_stalker"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientStalkerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientStalkerPacket::entityId,
            ClientStalkerPacket::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientStalkerPacket msg, Minecraft client) {
        client.execute(() -> ClientStalkerUtil.clientConnect(msg.entityId));
    }
}