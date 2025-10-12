package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.api.IControllable;
import com.mafuyu404.diligentstalker.data.ControllableStorage;
import com.mafuyu404.diligentstalker.data.StalkerDataAttachments;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.network.ClientFuelPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ControllableUtils {
    public static final String CONTROL_INPUT_KEY = "StalkerControlInput";

    public static boolean isControllable(Entity entity) {
        return entity instanceof IControllable;
    }

    public static ControllableStorage getData(Entity entity) {
        return entity.getData(StalkerDataAttachments.CONTROLLABLE_STORAGE);
    }

    public static void setMaxFuel(Entity entity, int maxFuel) {
        getData(entity).setMaxFuel(maxFuel);
    }

    public static void setFuel(Entity entity, int amount) {
        var data = getData(entity);
        data.setFuel(amount);
        syncFuel(entity, data.getFuel());
    }

    public static void consumeFuel(Entity entity, int amount) {
        var data = getData(entity);
        if (data.consumeFuel(amount)) {
            syncFuel(entity, data.getFuel());
        }
    }

    public static int getFuel(Entity entity) {
        return getData(entity).getFuel();
    }

    public static float getFuelPercent(Entity entity) {
        var data = getData(entity);
        return data.getMaxFuel() == 0 ? 0f : (float) data.getFuel() / data.getMaxFuel();
    }

    public static void syncFuel(Entity controllable, int fuel) {
        if (controllable.level().isClientSide) return;
        if (!isControllable(controllable)) return;

        Stalker instance = Stalker.getInstanceOf(controllable);
        if (instance != null) {
            Player player = instance.getPlayer();
            NetworkHandler.sendToClient((ServerPlayer) player, new ClientFuelPacket(controllable.getId(), fuel));
        }
    }


    public static boolean isCameraFollowing(Entity entity) {
        return getData(entity).getCameraState().equals("follow");
    }

    public static boolean isCameraControlling(Entity entity) {
        return getData(entity).getCameraState().equals("control");
    }

    public static String getCameraState(Entity entity) {
        return getData(entity).getCameraState();
    }

    public static void switchCameraState(Entity entity) {
        var data = getData(entity);
        data.switchCameraState();

        if (!isControllable(entity) && data.getCameraState().equals("control")) {
            data.switchCameraState();
        }

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            instance.getPlayer().displayClientMessage(
                    Component.translatable("message.diligentstalker." + data.getCameraState() + "_camera")
                            .withStyle(ChatFormatting.BOLD),
                    true
            );
        }
    }

    public static void setCameraControlling(Entity entity) {
        var data = getData(entity);
        data.setCameraState("control");

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            instance.getPlayer().displayClientMessage(
                    Component.translatable("message.diligentstalker." + data.getCameraState() + "_camera")
                            .withStyle(ChatFormatting.BOLD),
                    true
            );
        }
    }


    public static int getSignalRadius(Entity entity) {
        return getData(entity).getSignalRadius();
    }

    public static void setSignalRadius(Entity entity, int value) {
        getData(entity).setSignalRadius(value);
    }

    public static Vec3 tickServerControl(Entity entity, CompoundTag input, Vec3 motion) {
        if (isControllable(entity)) {
            return ((IControllable) entity).tickServerControl(input, motion);
        }
        return motion;
    }

    public static boolean isActionControlling(Entity entity) {
        if (!entity.level().isClientSide) {
            Stalker instance = Stalker.getInstanceOf(entity);
            CompoundTag input = (CompoundTag) instance.getPlayer().getPersistentData().get(CONTROL_INPUT_KEY);
            return input != null && !input.isEmpty();
        } else {
            return getData(entity).isActionControlling();
        }
    }

    public static void turnActionControlling(Entity entity) {
        var data = getData(entity);
        data.turnActionControlling();

        if (!isControllable(entity) && data.isActionControlling()) {
            data.turnActionControlling();
        }

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            instance.getPlayer().displayClientMessage(
                    Component.translatable("message.diligentstalker.action_control_" + (data.isActionControlling() ? "on" : "off"))
                            .withStyle(ChatFormatting.BOLD),
                    true
            );
        }
    }
}