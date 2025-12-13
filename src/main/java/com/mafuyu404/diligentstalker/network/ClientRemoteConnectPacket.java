package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientRemoteConnectPacket(UUID stalkerId, BlockPos center) implements CustomPacketPayload, Packet {
    public static final Type<ClientRemoteConnectPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "client_remote_connect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientRemoteConnectPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientRemoteConnectPacket::stalkerId,
            BlockPos.STREAM_CODEC, ClientRemoteConnectPacket::center,
            ClientRemoteConnectPacket::new
    );

    public static void handle(ClientRemoteConnectPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (msg.center == null) {
                return;
            }
            ClientStalkerUtil.tryRemoteConnect(
                    msg.center(),
                    entity -> entity.getUUID().equals(msg.stalkerId())
            );
        });
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
