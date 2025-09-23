package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.init.ControllableStorageProvider;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.network.ClientFuelPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllableUtils {
    public static void setFuel(Entity entity, int amount) {
        if (isControllable(entity)) {
            entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
                if (controllable.getMaxFuel() != 0) {
                    controllable.setFuel(amount);
                    syneFuel(entity, controllable.getFuel());
                }
            });
        }
    }

    public static void consumeFuel(Entity entity, int amount) {
        if (isControllable(entity)) {
            entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
                if (controllable.getMaxFuel() != 0) {
                    controllable.consumeFuel(amount);
                    syneFuel(entity, controllable.getFuel());
                }
            });
        }
    }

    public static int getFuel(Entity entity) {
        AtomicInteger fuel = new AtomicInteger();
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            if (controllable.getMaxFuel() != 0) {
                fuel.set(controllable.getFuel());
            } else fuel.set(0);
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

    public static boolean isControllable(Entity entity) {
        AtomicBoolean result = new AtomicBoolean(false);
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            result.set(controllable.getMaxFuel() != 0);
        });
        return result.get();
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

    public static void register(Entity entity, int maxFuel) {
        entity.getCapability(ControllableStorageProvider.CONTROLLABLE_STORAGE).ifPresent(controllable -> {
            controllable.setMaxFuel(maxFuel);
        });
    }
}
