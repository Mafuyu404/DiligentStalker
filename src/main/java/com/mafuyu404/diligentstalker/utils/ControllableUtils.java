package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.api.IControllable;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.init.ControllableStorageProvider;
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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ControllableUtils {
    public static String CONTROL_INPUT_KEY = "StalkerControlInput";

    public static boolean isControllable(Entity entity) {
        return entity instanceof IControllable;
    }

    public static void setMaxFuel(Entity entity, int maxFuel) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.setMaxFuel(maxFuel);
        });
    }
    public static void setFuel(Entity entity, int amount) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.setFuel(amount);
            syneFuel(entity, controllable.getFuel());
        });
    }
    public static void consumeFuel(Entity entity, int amount) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.consumeFuel(amount);
            syneFuel(entity, controllable.getFuel());
        });
    }
    public static int getFuel(Entity entity) {
        AtomicInteger fuel = new AtomicInteger();
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            fuel.set(controllable.getFuel());
        });
        return fuel.get();
    }
    public static float getFuelPercent(Entity entity) {
        final float[] fuel = {0};
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            fuel[0] = (1f * controllable.getFuel() / controllable.getMaxFuel());
        });
        return fuel[0];
    }
    public static void syneFuel(Entity controllable, int fuel) {
        if (controllable.level().isClientSide) return;
        if (!isControllable(controllable)) return;
        Stalker instance = Stalker.getInstanceOf(controllable);
        if (instance != null) {
            Player player = instance.getPlayer();
            NetworkHandler.sendToClient((ServerPlayer) player, new ClientFuelPacket(controllable.getId(), fuel));
        }
    }

    public static boolean isCameraFollowing(Entity entity) {
        AtomicBoolean result = new AtomicBoolean(false);
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            result.set(controllable.getCameraState().equals("follow"));
        });
        return result.get();
    }
    public static boolean isCameraControlling(Entity entity) {
        AtomicBoolean result = new AtomicBoolean(false);
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            result.set(controllable.getCameraState().equals("control"));
        });
        return result.get();
    }
    public static String getCameraState(Entity entity) {
        AtomicReference<String> result = new AtomicReference<>("free");
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            result.set(controllable.getCameraState());
        });
        return result.get();
    }
    public static void switchCameraState(Entity entity) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.switchCameraState();
            if (!isControllable(entity) && controllable.getCameraState().equals("control")) {
                controllable.switchCameraState();
            }
            Stalker instance = Stalker.getInstanceOf(entity);
            if (instance != null) {
                instance.getPlayer().displayClientMessage(Component.translatable("message.diligentstalker." + controllable.getCameraState() + "_camera").withStyle(ChatFormatting.BOLD), true);
            }
        });
    }
    public static void setCameraControlling(Entity entity) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.setCameraState("control");
            Stalker instance = Stalker.getInstanceOf(entity);
            if (instance != null) {
                instance.getPlayer().displayClientMessage(Component.translatable("message.diligentstalker." + controllable.getCameraState() + "_camera").withStyle(ChatFormatting.BOLD), true);
            }
        });
    }

    public static int getSignalRadius(Entity entity) {
        AtomicInteger result = new AtomicInteger();
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            result.set(controllable.getSignalRadius());
        });
        return result.get();
    }
    public static void setSignalRadius(Entity entity, int value) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.setSignalRadius(value);
        });
    }

    public static Vec3 tickServerControl(Entity entity, CompoundTag input, Vec3 motion) {
        if (isControllable(entity)) {
            return ((IControllable) entity).tickServerControl(input, motion);
        }
        return motion;
    }

    public static boolean isActionControlling(Entity entity) {
        AtomicBoolean result = new AtomicBoolean(false);
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            result.set(controllable.isActionControlling());
        });
        return result.get();
    }
    public static void turnActionControlling(Entity entity) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.turnActionControlling();
            if (!isControllable(entity) && controllable.isActionControlling()) controllable.turnActionControlling();
            Stalker instance = Stalker.getInstanceOf(entity);
            if (instance != null) {
                instance.getPlayer().displayClientMessage(Component.translatable("message.diligentstalker.action_control_" + (controllable.isActionControlling() ? "on" : "off")).withStyle(ChatFormatting.BOLD), true);
            }
        });
    }
}
