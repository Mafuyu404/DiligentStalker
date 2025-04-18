package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.network.MovePacket;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class TestEvent {
    public static Entity targetEntity;
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        Player player = event.player;
        if (!player.isLocalPlayer()) return;
        if (targetEntity != null) {
            targetEntity.setXRot(xRot);
            targetEntity.setYRot(yRot);
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
        targetEntity = event.getTarget();
        Minecraft.getInstance().setCameraEntity(event.getTarget());
//        if (event.getTarget() instanceof Mob mob) {
//            mob.setNoAi(true);
//        }
    }
    @SubscribeEvent
    public static void onAction(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Options options = Minecraft.getInstance().options;
        if (targetEntity == null) return;
        if (event.getKey() == options.keySprint.getKey().getValue()) {
            options.keySprint.setDown(false);
            targetEntity = null;
            Minecraft.getInstance().setCameraEntity(player);
            return;
        }
        Vec3 lookAngle = calculateViewVector(xRot, yRot);
        double speed = 0.1;
        double x = Math.sqrt(lookAngle.x / (lookAngle.x + lookAngle.z));
        double y = 0;
        double z = Math.sqrt(lookAngle.z / (lookAngle.x + lookAngle.z));
        if (event.getKey() == options.keyUp.getKey().getValue()) {
            x *= speed;
            z *= speed;
        }
        if (event.getKey() == options.keyDown.getKey().getValue()) {
            x *= -speed;
            z *= -speed;
        }
        if (event.getKey() == options.keyJump.getKey().getValue()) {
            x = 0;
            y = speed;
            z = 0;
        }
        NetworkHandler.CHANNEL.sendToServer(new MovePacket(targetEntity.getId(), new Vec3(x, y, z)));
    }
    @SubscribeEvent
    public static void onEntityRender(RenderLivingEvent.Pre<? extends Entity, ? extends EntityModel<?>> event) {
        if (targetEntity == null) return;
        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID()) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLoad(EntityLeaveLevelEvent event) {
        if (targetEntity == null) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID() || event.getEntity().getUUID() == Minecraft.getInstance().player.getUUID()) {
            targetEntity = null;
        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (targetEntity == null) return;
        if (targetEntity.getUUID() == event.getEntity().getUUID() || event.getEntity().getUUID() == Minecraft.getInstance().player.getUUID()) {
            targetEntity = null;
        }
    }

    public static Vec3 calculateViewVector(float xRot, float yRot) {
        float f = xRot * ((float)Math.PI / 180F);
        float f1 = -yRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
}
