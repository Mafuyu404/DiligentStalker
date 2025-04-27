package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.ModItems;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StalkerSyncMsg {
    private final int entityId;
    private final boolean state;

    public StalkerSyncMsg(int entityId, boolean state) {
        this.entityId = entityId;
        this.state = state;
    }

    public static void encode(StalkerSyncMsg msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeBoolean(msg.state);
    }

    public static StalkerSyncMsg decode(FriendlyByteBuf buffer) {
        return new StalkerSyncMsg(buffer.readInt(), buffer.readBoolean());
    }

    public static void handle(StalkerSyncMsg msg, Supplier<NetworkEvent.Context> ctx) {
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
                StalkerManage.clearLoadedChunkOf(stalker);
                player.getPersistentData().putBoolean("LoadingCacheChunk", true);
                SectionPos sectionpos = SectionPos.of(player);
                player.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionpos.x(), sectionpos.z()));
                if (stalker instanceof ArrowStalkerEntity arrowStalker) {
                    arrowStalker.spawnAtLocation(new ItemStack(ModItems.ARROW_STALKER_ITEM.get()));
                    arrowStalker.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
