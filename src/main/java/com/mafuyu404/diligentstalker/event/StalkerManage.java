package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class StalkerManage {
    public static int LOAD_RADIUS = 3;
    public static final HashMap<UUID, Map.Entry<String, BlockPos>> DronePosition = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (event.getServer().getTickCount() %10 == 0) {
            event.getServer().getAllLevels().forEach(StalkerManage::onLevelTick);
        }
        event.getServer().getPlayerList().getPlayers().forEach(StalkerManage::onPlayerTick);
    }
    private static void onPlayerTick(ServerPlayer player) {
        if (player.tickCount %20 == 0) syncMasterTag(player);
        if (!Stalker.hasInstanceOf(player)) return;
        Entity stalker = Stalker.getInstanceOf(player).getStalker();
        int timer = 10;
        if (stalker instanceof DroneStalkerEntity) {
            CompoundTag input = (CompoundTag) player.getPersistentData().get("DroneStalkerInput");
            if (input != null) {
                float xRot = input.getFloat("xRot");
                float yRot = input.getFloat("yRot");
                stalker.setXRot(xRot);
                stalker.setYRot(yRot);
                stalker.setDeltaMovement(Tools.move(input, stalker.getDeltaMovement()));
                timer = 30;
            }
        }
        if (player.tickCount % timer == 0) {
            player.serverLevel().getChunkSource().move(player);
            player.teleportRelative(0, 0, 0);
        }
    }
    private static void onLevelTick(ServerLevel level) {
        ChunkLoader.removeAll(level);
        level.getEntities().getAll().forEach(entity -> {
            if (Stalker.hasInstanceOf(entity)) {
                boolean isDroneStalker = entity instanceof DroneStalkerEntity;
                boolean isArrowStalker = entity instanceof ArrowStalkerEntity;
                boolean isVoidStalker = entity instanceof VoidStalkerEntity;
                if (isDroneStalker || isArrowStalker || isVoidStalker) {
                    Tools.getToLoadChunk(entity, 0).forEach(chunkPos -> {
                        ChunkLoader.add(level, chunkPos);
                    });
                }
            }
        });
        level.players().forEach(player -> {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                ChunkLoader.add(level, new ChunkPos(entry.getValue()));
            }
        });
    }

    @SubscribeEvent
    public static void onUnload(EntityLeaveLevelEvent event) {
        if (Stalker.hasInstanceOf(event.getEntity())) {
            Stalker.getInstanceOf(event.getEntity()).disconnect();
        }
    }

    private static void syncMasterTag(ServerPlayer player) {
        String levelKey = player.level().dimension().toString();
        player.getInventory().items.forEach(itemStack -> {
            if (itemStack.getItem() instanceof StalkerMasterItem) {
                CompoundTag tag = itemStack.getOrCreateTag();
                if (!tag.contains("StalkerId")) return;
                UUID entityUUID = tag.getUUID("StalkerId");
                if (DronePosition.containsKey(entityUUID)) {
                    BlockPos pos = DronePosition.get(entityUUID).getValue();
                    tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                } else if (tag.contains("StalkerPosition")) {
                    int[] pos = tag.getIntArray("StalkerPosition");
                    DronePosition.put(entityUUID, new Map.Entry<>() {
                        @Override
                        public String getKey() {
                            return levelKey;
                        }
                        @Override
                        public BlockPos getValue() {
                            return new BlockPos(pos[0], pos[1], pos[2]);
                        }
                        @Override
                        public BlockPos setValue(BlockPos value) {
                            return null;
                        }
                    });
                }
            }
        });
    }
}
