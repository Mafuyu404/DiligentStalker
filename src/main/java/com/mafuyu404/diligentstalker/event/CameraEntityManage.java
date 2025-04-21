package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.ModEntities;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.network.MovePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class CameraEntityManage {
    public static Entity targetEntity;
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;

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
        CameraEntityAction.postAction();
    }

    public static void launch(Entity entity, Player player) {
        if (!player.isLocalPlayer()) return;
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
        targetEntity = entity;
        Minecraft.getInstance().setCameraEntity(entity);
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
    public static void onUnload(EntityLeaveLevelEvent event) {
        if (targetEntity == null) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID() || event.getEntity().getUUID() == Minecraft.getInstance().player.getUUID()) {
            quit();
        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (targetEntity == null) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID() || event.getEntity().getUUID() == Minecraft.getInstance().player.getUUID()) {
            quit();
        }
    }

    public static void quit() {
        targetEntity = null;
        NetworkHandler.CHANNEL.sendToServer(new MovePacket(-1, CameraEntityAction.handleInput()));
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Minecraft.getInstance().setCameraEntity(player);
        System.out.print("quit\n");
    }

    public static Vec3 getViewVector() {
        return Tools.calculateViewVector(xRot, yRot);
    }

    public static boolean isEnable() {
        return CameraEntityManage.targetEntity != null || ServerEvent.entityId != -1;
    }

    public static Vec3 getCameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }
}
