package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.handler.StalkerManage;
import com.mafuyu404.diligentstalker.event.handler.StalkerManage.DroneLocation;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class StalkerMasterUsePacket implements Packet {

    public StalkerMasterUsePacket() {
    }

    public static StalkerMasterUsePacket decode(FriendlyByteBuf buffer) {
        return new StalkerMasterUsePacket();
    }

    public static void handle(MinecraftServer server, ServerPlayer player, StalkerMasterUsePacket msg) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            if (player.level().isClientSide) {
                return;
            }
            if (Stalker.hasInstanceOf(player)) {
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof StalkerMasterItem)) {
                return;
            }
            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains("StalkerId")) {
                return;
            }
            UUID entityUUID = tag.getUUID("StalkerId");
            DroneLocation location = StalkerManage.DronePosition.get(entityUUID);
            if (location == null) {
                return;
            }
            BlockPos center = location.position();
            if (center == null) {
                return;
            }
            NetworkHandler.sendToClient(player, NetworkHandler.CLIENT_REMOTE_CONNECT_PACKET,
                    new ClientRemoteConnectPacket(entityUUID, center));
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
    }
}