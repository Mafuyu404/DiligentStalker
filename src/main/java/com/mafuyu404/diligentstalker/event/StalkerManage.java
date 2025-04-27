package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class StalkerManage {
    private static final int LOAD_RADIUS = 3;
    private static final HashMap<UUID, Set<Long>> LoadedChunks = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        event.getServer().getPlayerList().getPlayers().forEach(StalkerManage::onPlayerTick);
    }

    public static void onPlayerTick(ServerPlayer player) {
        if (!Stalker.hasInstanceOf(player)) return;
        Entity stalker = Stalker.getInstanceOf(player).getStalker();
        CompoundTag input = (CompoundTag) player.getPersistentData().get("DroneStalkerInput");
        if (input == null) return;
        float xRot = input.getFloat("xRot");
        float yRot = input.getFloat("yRot");
        stalker.setXRot(xRot);
        stalker.setYRot(yRot);
        stalker.setDeltaMovement(Tools.move(input));
        if (player.tickCount % 40 == 0) {
            updateChunkLoading(player.serverLevel(), stalker);
            SectionPos sectionpos = SectionPos.of(stalker);
            player.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionpos.x(), sectionpos.z()));
            player.serverLevel().getChunkSource().move(player);
            player.teleportRelative(0, 0, 0);
        }
    }

    @SubscribeEvent
    public static void onEnter(EntityJoinLevelEvent event) {
        Stalker instance = Stalker.getInstanceOf(event.getEntity());
        if (instance != null) {
            if (instance.getPlayer() == null || instance.getStalker() == null) {
                instance.disconnect();
            }
        }
    }
    @SubscribeEvent
    public static void onUnload(EntityLeaveLevelEvent event) {
        if (Stalker.hasInstanceOf(event.getEntity())) {
            Stalker.getInstanceOf(event.getEntity()).disconnect();
        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (Stalker.hasInstanceOf(event.getEntity())) {
            Stalker.getInstanceOf(event.getEntity()).disconnect();
        }
    }

    private static void updateChunkLoading(ServerLevel level, Entity stalker) {
        ChunkPos center = stalker.chunkPosition();

        Set<Long> loadedChunks = LoadedChunks.getOrDefault(stalker.getUUID(), new HashSet<>());
        Set<Long> newChunks = new HashSet<>();

        for (int x = -LOAD_RADIUS; x <= LOAD_RADIUS; x++) {
            for (int z = -LOAD_RADIUS; z <= LOAD_RADIUS; z++) {
                newChunks.add(new ChunkPos(center.x + x, center.z + z).toLong());
                level.getChunkSource().updateChunkForced(new ChunkPos(center.x + x, center.z + z), true);
            }
        }

        Set<Long> toRemove = new HashSet<>(loadedChunks);
        toRemove.removeAll(newChunks);
        toRemove.forEach(chunk ->
                ForgeChunkManager.forceChunk(level, DiligentStalker.MODID, stalker.getUUID(),
                        ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false, false)
        );

        newChunks.forEach(chunk ->
                ForgeChunkManager.forceChunk(level, DiligentStalker.MODID, stalker.getUUID(),
                        ChunkPos.getX(chunk), ChunkPos.getZ(chunk), true, true)
        );

        loadedChunks.clear();
        loadedChunks.addAll(newChunks);
        for (int x = -LOAD_RADIUS; x <= LOAD_RADIUS; x++) {
            for (int z = -LOAD_RADIUS; z <= LOAD_RADIUS; z++) {
                level.getChunkSource().updateChunkForced(new ChunkPos(center.x + x, center.z + z), true);
            }
        }

        LoadedChunks.put(stalker.getUUID(), loadedChunks);
    }

    public static void clearLoadedChunkOf(Entity stalker) {
        Set<Long> loadedChunks = LoadedChunks.get(stalker.getUUID());
        loadedChunks.forEach(chunk ->
                ForgeChunkManager.forceChunk((ServerLevel) stalker.level(), DiligentStalker.MODID,
                        stalker.getUUID(), ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false, false)
        );
        LoadedChunks.put(stalker.getUUID(), loadedChunks);
    }
}
