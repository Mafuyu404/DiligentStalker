package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ServerStalker {
    public static HashMap<Integer, ChunkLoader> chunkLoader = new HashMap<>();

    @SubscribeEvent
    public static void onServerTIck(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.isLocalPlayer()) return;
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

        boolean shouldLoad = true;
        boolean wasLoading = entity.getPersistentData().getBoolean("forceLoadChunks");

//        if (shouldLoad != wasLoading) {
//            {
//                // 创建新的ChunkLoader并激活
//                ChunkLoader loader = new ChunkLoader((ServerLevel) entity.level(), new ChunkPos(entity.blockPosition()));
//                entity.getPersistentData().put("chunkLoader", loader);
//                loader.activate(3); // 3区块半径
//            }
//            entity.getPersistentData().putBoolean("forceLoadChunks", shouldLoad);
//        }
//
//        // 更新加载器位置
//        if (shouldLoad) {
//            ChunkLoader loader = (ChunkLoader) entity.getPersistentData().get("chunkLoader");
//            ChunkPos newPos = new ChunkPos(entity.blockPosition());
//            if (!loader.center.equals(newPos)) {
//                loader.deactivate(3);
//                loader.center = newPos;
//                loader.activate(3);
//            }
//        }
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

//    @SubscribeEvent
//    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
//        if (!event.getEntity().isLocalPlayer() && entityId != -1) {
//            System.out.print("server\n");
//            event.setCanceled(true);
//        }
//    }
}
