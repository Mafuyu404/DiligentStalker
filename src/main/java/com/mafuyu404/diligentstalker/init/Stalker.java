package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.network.StalkerSyncMsg;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Stalker {
    private final UUID playerUUID;
    private final int stalkerId;
    public final Level level;
    public static final HashMap<UUID, Integer> InstanceMap = new HashMap<>();

    public Stalker(UUID playerUUID, int stalkerId, Level level) {
        this.playerUUID = playerUUID;
        this.stalkerId = stalkerId;
        this.level = level;
    }

    public Player getPlayer() {
        return level.getPlayerByUUID(playerUUID);
    }
    public Entity getStalker() {
        return level.getEntity(stalkerId);
    }
    public void disconnect() {
        if (level.isClientSide) {
            System.out.print("disconnect\n");
            NetworkHandler.CHANNEL.sendToServer(new StalkerSyncMsg(this.stalkerId, false));
        }
        else {
            StalkerManage.onDisconnect(this);
        }
        InstanceMap.remove(playerUUID);
    }

    public static Stalker connect(Player player, Entity stalker) {
        if (hasInstanceOf(player) || hasInstanceOf(stalker)) return null;
        if (player.level().isClientSide) {
            System.out.print("connect\n");
            StalkerControl.connect(player);
            NetworkHandler.CHANNEL.sendToServer(new StalkerSyncMsg(stalker.getId(), true));
        }
        InstanceMap.put(player.getUUID(), stalker.getId());
        return new Stalker(player.getUUID(), stalker.getId(), player.level());
    }
    public static boolean hasInstanceOf(Entity entity) {
        if (entity == null) return false;
        boolean isPlayer = InstanceMap.containsKey(entity.getUUID());
        boolean isStalker = InstanceMap.containsValue(entity.getId());
        return (isPlayer || isStalker);
    }
    public static Stalker getInstanceOf(Entity entity) {
        boolean isPlayer = InstanceMap.containsKey(entity.getUUID());
        boolean isStalker = InstanceMap.containsValue(entity.getId());
        if (isPlayer) {
            int stalkerId = InstanceMap.get(entity.getUUID());
            return new Stalker(entity.getUUID(), stalkerId, entity.level());
        }
        if (isStalker) {
            AtomicReference<UUID> playerUUID = new AtomicReference<>();
            InstanceMap.forEach((uuid, stalkerId) -> {
                if (stalkerId == entity.getId()) playerUUID.set(uuid);
            });
            return new Stalker(playerUUID.get(), entity.getId(), entity.level());
        }
        return null;
    }
}
