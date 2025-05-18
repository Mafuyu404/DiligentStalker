package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.api.PersistentDataHolder;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StalkerSyncPacket {
    private final int entityId;
    private final boolean state;

    public StalkerSyncPacket(int entityId, boolean state) {
        this.entityId = entityId;
        this.state = state;
    }

    public static void encode(StalkerSyncPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeBoolean(msg.state);
    }

    public static StalkerSyncPacket decode(FriendlyByteBuf buffer) {
        return new StalkerSyncPacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        StalkerSyncPacket msg = decode(buf);
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
                PersistentDataHolder holder = (PersistentDataHolder) player;
                holder.getPersistentData().putBoolean("LoadingCacheChunk", true);
                if (stalker instanceof ArrowStalkerEntity arrowStalker) {
                    arrowStalker.spawnAtLocation(new ItemStack(StalkerItems.ARROW_STALKER));
                    arrowStalker.discard();
                }
            }
        });
    }
}
