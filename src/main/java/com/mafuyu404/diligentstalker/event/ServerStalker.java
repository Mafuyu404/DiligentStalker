package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ServerStalker {
    public static ArrayList<UUID> MoribundStalker = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isLocalPlayer()) return;
        ServerPlayer player = (ServerPlayer) event.player;
//        if (!MoribundStalker.isEmpty()) {
//            Iterator<Integer> iterator = MoribundStalker.iterator();
//            while (iterator.hasNext()) {
//                int id = iterator.next();
//                if (player.level().getEntity(id) == null) return;
//                if (player.level().getEntity(id) instanceof DroneStalkerEntity droneStalker) {
//                    iterator.remove();
//                }
//            }
//        }
        Entity entity = getCameraEntity(player);
        if (entity == null) {
            return;
        }
        CompoundTag input = (CompoundTag) player.getPersistentData().get("DroneStalkerInput");
        if (input == null) return;
        float xRot = input.getFloat("xRot");
        float yRot = input.getFloat("yRot");
        entity.setXRot(xRot);
        entity.setYRot(yRot);
        entity.setDeltaMovement(Tools.move(input));
        ChunkPos center = new ChunkPos(entity.blockPosition());
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                ChunkPos pos = new ChunkPos(center.x + x, center.z + z);
                entity.level().getChunkSource().updateChunkForced(pos, true);
            }
        }
        if (ServerStalker.getCameraEntity(player) != null) {
            if (ServerStalker.getCameraEntity(player) instanceof DroneStalkerEntity droneStalker) {
                if (ServerStalker.MoribundStalker.contains(droneStalker.getId())) {
                    player.serverLevel().getChunkSource().move(player);
                }
                else if (player.tickCount % 40 == 0) {
                    SectionPos sectionpos = SectionPos.of(droneStalker);
                    player.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionpos.x(), sectionpos.z()));
                    player.serverLevel().getChunkSource().move(player);
                    player.teleportRelative(0, 0, 0);
                }
            }
        }
        if (player.getPersistentData().getBoolean("LoadingChunk")) {
            player.serverLevel().getChunkSource().setViewDistance(2);
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }

    public static Entity getCameraEntity(Player player) {
        int entityId = player.getPersistentData().getInt("StalkerEntityId");
        if (entityId < 0) return null;
        Entity entity = player.level().getEntity(entityId);
        return entity;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getPersistentData().putInt("StalkerEntityId", -1);
    }
}
