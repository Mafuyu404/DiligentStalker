package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.component.ModComponents;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record StalkerSyncPacket(int entityId, boolean state) implements Packet {
    public static final Type<StalkerSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "stalker_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StalkerSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StalkerSyncPacket::entityId,
            ByteBufCodecs.BOOL, StalkerSyncPacket::state,
            StalkerSyncPacket::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, StalkerSyncPacket msg) {
        server.execute(() -> {
            if (player == null) return;

            Level level = player.level();
            Entity stalker = level.getEntity(msg.entityId);
            if (stalker == null) return;

            if (msg.state) {
                if (!Stalker.hasInstanceOf(player) && !Stalker.hasInstanceOf(stalker)) {
                    Stalker.connect(player, stalker);
                }
            } else {
                if (Stalker.hasInstanceOf(player)) {
                    Stalker.getInstanceOf(player).disconnect();
                }
                player.inventoryMenu.sendAllDataToRemote();
                ModComponents.STALKER_DATA.get(player).getStalkerData().putBoolean("LoadingCacheChunk", true);
                if (stalker instanceof ArrowStalkerEntity arrowStalker) {
                    arrowStalker.spawnAtLocation(new ItemStack(StalkerItems.ARROW_STALKER));
                    arrowStalker.discard();
                }
            }
        });
    }
}