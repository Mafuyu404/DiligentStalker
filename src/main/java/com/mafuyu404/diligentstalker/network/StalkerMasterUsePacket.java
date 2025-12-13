package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StalkerMasterUsePacket {

    public StalkerMasterUsePacket() {
    }

    public static StalkerMasterUsePacket decode(FriendlyByteBuf buffer) {
        return new StalkerMasterUsePacket();
    }

    public static void handle(StalkerMasterUsePacket msg,  Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) {
                return;
            }
            if (ctx.get().getSender().level().isClientSide) {
                return;
            }
            if (Stalker.hasInstanceOf(ctx.get().getSender())) {
                return;
            }
            ItemStack stack = ctx.get().getSender().getMainHandItem();
            if (!(stack.getItem() instanceof StalkerMasterItem)) {
                return;
            }
            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains("StalkerId")) {
                return;
            }
            UUID entityUUID = tag.getUUID("StalkerId");
            StalkerManage.DroneLocation location = StalkerManage.DronePosition.get(entityUUID);
            if (location == null) {
                return;
            }
            BlockPos center = location.position();
            if (center == null) {
                return;
            }
            NetworkHandler.sendToClient(ctx.get().getSender(),
                    new ClientRemoteConnectPacket(entityUUID, center));
        });
    }

    public void encode(FriendlyByteBuf buffer) {
    }
}