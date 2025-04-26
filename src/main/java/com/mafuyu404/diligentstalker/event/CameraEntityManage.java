package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class CameraEntityManage {
    public static Entity targetEntity;
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;
    public static boolean moribund = false;

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (Minecraft.getInstance().screen != null) return;
        if (event.phase == TickEvent.Phase.START) return;
        Player player = event.player;
        if (!player.isLocalPlayer()) return;
        Options options = Minecraft.getInstance().options;
        if (targetEntity == null) {
            Minecraft.getInstance().setCameraEntity(player);
            return;
        }
        targetEntity.setXRot(xRot);
        targetEntity.setYRot(yRot);
        CompoundTag input = StalkerAction.handleInput();
        player.getPersistentData().put("DroneStalkerInput", input);
        StalkerAction.syncData();

        ClientLevel level = (ClientLevel) player.level();

//        if (level.getChunkSource().hasChunk(player.chunkPosition().x, player.chunkPosition().z)) {
//            System.out.print("dddddddd\n");
//            player.getPersistentData().putBoolean("LoadingChunk", false);
//            StalkerAction.syncData();
//        }
    }

    public static void launch(Entity entity, Player player) {
        if (!player.isLocalPlayer()) return;
        if (targetEntity instanceof DroneStalkerEntity droneStalker) {
            droneStalker.setMasterPlayer(player);
        }
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
        targetEntity = entity;
        Minecraft.getInstance().setCameraEntity(entity);
        player.getPersistentData().putInt("StalkerEntityId", entity.getId());
        NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(player.getId(), player.getPersistentData()));
    }
//    @SubscribeEvent
//    public static void onEntityRender(RenderLivingEvent.Pre<? extends Entity, ? extends EntityModel<?>> event) {
//        if (targetEntity == null) return;
//        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) return;
//        if (targetEntity.getUUID() == event.getEntity().getUUID()) {
//            event.setCanceled(true);
//        }
//    }
    @SubscribeEvent
    public static void onEnter(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof DroneStalkerEntity entity) {
            if (entity.level().isClientSide) {
                entity.getPersistentData().putUUID("MasterPlayer", entity.getUUID());
                entity.disconnect();
//                disconnect();
            }
        }
        if (event.getEntity() instanceof Player player) {
            player.getPersistentData().putInt("StalkerEntityId", -1);
        }
    }
    @SubscribeEvent
    public static void onUnload(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof DroneStalkerEntity entity) {
            if (entity.level().isClientSide) entity.disconnect();
        }
        if (targetEntity == null) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID() || event.getEntity().getUUID() == Minecraft.getInstance().player.getUUID()) {
            if (event.getEntity().level().isClientSide) quit();
        }
        if (event.getEntity() instanceof Player player) {
            player.getPersistentData().putInt("StalkerEntityId", -1);
        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (targetEntity == null) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID() || event.getEntity().getUUID() == Minecraft.getInstance().player.getUUID()) {
            if (event.getEntity().level().isClientSide) quit();
        }
    }

    public static void quit() {
//        if (targetEntity instanceof DroneStalkerEntity droneStalker) {
//            if (!droneStalker.underControlling()) return;
//            Player player = Minecraft.getInstance().player;
//            if (player == null) return;
//            NetworkHandler.CHANNEL.sendToServer(new CameraEntityStatePacket(targetEntity.getId(), false));
//            moribund = true;
//            targetEntity = null;
//            Minecraft.getInstance().setCameraEntity(player);
//        }
        if (targetEntity instanceof DroneStalkerEntity droneStalker) {
            droneStalker.disconnect();
        }
        targetEntity = null;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Minecraft.getInstance().setCameraEntity(player);
        player.getPersistentData().putInt("StalkerEntityId", -1);
        player.getPersistentData().putBoolean("LoadingChunk", true);
        StalkerAction.syncData();
        player.getPersistentData().putBoolean("LoadingChunk", false);
    }
    public static void disconnect() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (targetEntity instanceof DroneStalkerEntity droneStalker) {
            droneStalker.disconnect();
        }
        targetEntity = null;
        Minecraft.getInstance().setCameraEntity(player);
        player.getPersistentData().putInt("StalkerEntityId", -1);
        StalkerAction.syncData();
    }

    public static Vec3 getViewVector() {
        return Tools.calculateViewVector(xRot, yRot);
    }

    public static boolean isEnable(Player player) {
        if (!player.getPersistentData().contains("StalkerEntityId")) {
            player.getPersistentData().putInt("StalkerEntityId", -1);
        }
        return player.getPersistentData().getInt("StalkerEntityId") > 0;
    }

    public static Vec3 getCameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }
}
