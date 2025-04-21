package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ServerEvent {
    public static int entityId = -1;
    public static float xRot, yRot;
    public static CompoundTag input = new CompoundTag();
    public static ArrayList<String> boostKey = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTIck(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.isLocalPlayer()) return;
        Level level = player.level();
        Entity entity = level.getEntity(entityId);
        if (entity == null) {
            entityId = -1;
            return;
        }
        float x = 0;
        float y = 0;
        float z = 0;
        float defaultSpeed = 0.25f;
        float boostSpeed = 10f;
        if (input.getBoolean("Up") || input.getBoolean("Down")) {
            Vec3 lookAngle = Tools.calculateViewVector(xRot, yRot);
            double xz = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
            float forwardX = (float) (lookAngle.x / xz);
            float forwardZ = (float) (lookAngle.z / xz);
            if (input.getBoolean("Up")) {
                float speed = boostKey.contains("Up") ? boostSpeed : defaultSpeed;
                x += forwardX * speed;
                z += forwardZ * speed;
            }
            if (input.getBoolean("Down")) {
                float speed = boostKey.contains("Down") ? boostSpeed : defaultSpeed;
                x -= forwardX * speed;
                z -= forwardZ * speed;
            }
        }
        if (input.getBoolean("Left") || input.getBoolean("Right")) {
            Vec3 subAngle = Tools.calculateViewVector(xRot, yRot - 90);
            double xz = Math.sqrt(subAngle.x * subAngle.x + subAngle.z * subAngle.z);
            float forwardX = (float) (subAngle.x / xz);
            float forwardZ = (float) (subAngle.z / xz);
            if (input.getBoolean("Left")) {
                float speed = boostKey.contains("Left") ? boostSpeed : defaultSpeed;
                x += forwardX * speed;
                z += forwardZ * speed;
            }
            if (input.getBoolean("Right")) {
                float speed = boostKey.contains("Right") ? boostSpeed : defaultSpeed;
                x -= forwardX * speed;
                z -= forwardZ * speed;
            }
        }
        if (input.getBoolean("Jump")) {
            float speed = boostKey.contains("Jump") ? boostSpeed : defaultSpeed;
            y = speed;
        }
        if (input.getBoolean("Shift")) {
            float speed = boostKey.contains("Shift") ? boostSpeed : defaultSpeed;
            y = -speed;
        }
        entity.setDeltaMovement(x, y, z);
        boostKey.clear();
//        entity.setDeltaMovement(Tools.move(input));
    }

    public static Entity getCameraEntity(Player player) {
        if (entityId == -1) return null;
        Entity entity = player.level().getEntity(entityId);
        return entity;
    }

//    @SubscribeEvent
//    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
//        if (!event.getEntity().isLocalPlayer() && entityId != -1) {
//            System.out.print("server\n");
//            event.setCanceled(true);
//        }
//    }
}
