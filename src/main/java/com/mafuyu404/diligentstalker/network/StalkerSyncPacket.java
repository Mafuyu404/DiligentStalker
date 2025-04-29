package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(StalkerSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            Entity stalker = level.getEntity(msg.entityId);
            if (stalker == null) return;
            if (msg.state) {
                // 创建跟踪狂实例
                if (!Stalker.hasInstanceOf(player) && !Stalker.hasInstanceOf(stalker)) {
                    Stalker.connect(player, stalker);
                }
            } else {
                // 删除跟踪狂实例
                if (Stalker.hasInstanceOf(player)) {
                    Stalker.getInstanceOf(player).disconnect();
                }
                player.inventoryMenu.sendAllDataToRemote();
                player.getPersistentData().putBoolean("LoadingCacheChunk", true);
                if (stalker instanceof ArrowStalkerEntity arrowStalker) {
                    arrowStalker.spawnAtLocation(new ItemStack(StalkerItems.ARROW_STALKER.get()));
                    arrowStalker.discard();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
