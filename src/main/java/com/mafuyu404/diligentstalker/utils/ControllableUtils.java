package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.api.IControllable;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.data.ModLookupApi;
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

    private static IControllableStorage getStorage(Entity entity) {
        return ModLookupApi.CONTROLLABLE_STORAGE.find(entity, null);
    }

    public static boolean isControllable(Entity entity) {
        return entity instanceof IControllable;
    }

    public static void setMaxFuel(Entity entity, int maxFuel) {
        getStorage(entity).setMaxFuel(maxFuel);
    }

    public static void setFuel(Entity entity, int amount) {
        IControllableStorage controllable = getStorage(entity);
        controllable.setFuel(amount);
        syncFuel(entity, controllable.getFuel());
    }

    public static void consumeFuel(Entity entity, int amount) {
        IControllableStorage controllable = getStorage(entity);
        controllable.consumeFuel(amount);
        syncFuel(entity, controllable.getFuel());
    }

    public static int getFuel(Entity entity) {
        return getStorage(entity).getFuel();
    }

    public static float getFuelPercent(Entity entity) {
        IControllableStorage controllable = getStorage(entity);
        return (float) controllable.getFuel() / controllable.getMaxFuel();
    }

    public static void syncFuel(Entity entity, int fuel) {
        if (entity.level().isClientSide) return;
        if (!isControllable(entity)) return;

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            Player player = instance.getPlayer();
            NetworkHandler.sendToClient((ServerPlayer) player, NetworkHandler.CLIENT_FUEL_PACKET,
                    new ClientFuelPacket(entity.getId(), fuel));
        }
    }

    public static boolean isCameraFollowing(Entity entity) {
        IControllableStorage storage = ModLookupApi.CONTROLLABLE_STORAGE.find(entity, null);
        return storage != null && "follow".equals(storage.getCameraState());
    }

    public static boolean isCameraControlling(Entity entity) {
        IControllableStorage storage = ModLookupApi.CONTROLLABLE_STORAGE.find(entity, null);
        return storage != null && "control".equals(storage.getCameraState());
    }

    public static String getCameraState(Entity entity) {
        IControllableStorage storage = ModLookupApi.CONTROLLABLE_STORAGE.find(entity, null);
        return storage != null ? storage.getCameraState() : "free";
    }

    public static void switchCameraState(Entity entity) {
        IControllableStorage controllable = getStorage(entity);
        controllable.switchCameraState();

        if (!isControllable(entity) && controllable.getCameraState().equals("control")) {
            controllable.switchCameraState();
        }

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            instance.getPlayer().displayClientMessage(
                    Component.translatable("message.diligentstalker." + controllable.getCameraState() + "_camera")
                            .withStyle(ChatFormatting.BOLD), true);
        }
    }

    public static void setCameraControlling(Entity entity) {
        IControllableStorage controllable = getStorage(entity);
        controllable.setCameraState("control");

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            instance.getPlayer().displayClientMessage(
                    Component.translatable("message.diligentstalker." + controllable.getCameraState() + "_camera")
                            .withStyle(ChatFormatting.BOLD), true);
        }
    }

    public static int getSignalRadius(Entity entity) {
        return getStorage(entity).getSignalRadius();
    }

    public static void setSignalRadius(Entity entity, int value) {
        getStorage(entity).setSignalRadius(value);
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
            if (instance == null || instance.getStalker() == null) return false;
            var data = ModLookupApi.STALKER_DATA.find(instance.getStalker(), null);
            if (data == null) return false;
            CompoundTag input = data.getData().getCompound(CONTROL_INPUT_KEY);
            return !input.isEmpty();
        } else {
            return getStorage(entity).isActionControlling();
        }
    }

    public static void turnActionControlling(Entity entity) {
        IControllableStorage controllable = getStorage(entity);
        controllable.turnActionControlling();

        if (!isControllable(entity) && controllable.isActionControlling()) {
            controllable.turnActionControlling();
        }

        Stalker instance = Stalker.getInstanceOf(entity);
        if (instance != null) {
            instance.getPlayer().displayClientMessage(
                    Component.translatable("message.diligentstalker.action_control_" +
                                    (controllable.isActionControlling() ? "on" : "off"))
                            .withStyle(ChatFormatting.BOLD), true);
        }
    }
}
