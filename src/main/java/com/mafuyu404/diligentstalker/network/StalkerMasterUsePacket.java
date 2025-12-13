package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.component.StalkerDataComponents;
import com.mafuyu404.diligentstalker.event.handler.StalkerManage;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record StalkerMasterUsePacket() implements CustomPacketPayload, Packet {
    public static final Type<StalkerMasterUsePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "stalker_master_use"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StalkerMasterUsePacket> STREAM_CODEC = StreamCodec.unit(new StalkerMasterUsePacket());

    public static void handle(StalkerMasterUsePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                if (Stalker.hasInstanceOf(player)) return;

                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof StalkerMasterItem)) return;

                UUID stalkerId = stack.get(StalkerDataComponents.STALKER_ID.get());
                if (stalkerId == null) return;

                StalkerManage.DroneLocation location = StalkerManage.DronePosition.get(stalkerId);
                if (location == null) return;

                NetworkHandler.sendToClient(
                        player,
                        new ClientRemoteConnectPacket(stalkerId, location.position())
                );
            }
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
