package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.network.StalkerSyncPacket;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class Stalker {
    private final UUID playerUUID;
    private final int stalkerId;
    public final Level level;
    public static final HashMap<UUID, Integer> InstanceMap = new HashMap<>();
    private static final HashMap<Integer, UUID> StalkerToPlayerMap = new HashMap<>();

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
            NetworkHandler.CHANNEL.sendToServer(new StalkerSyncPacket(this.stalkerId, false));
            ClientStalkerUtil.cancelRemoteConnect();
        }

        DisconnectEvent event = new DisconnectEvent(getPlayer(), getStalker());
        MinecraftForge.EVENT_BUS.post(event);

        if (!level.isClientSide) {
            Player player = getPlayer();
            if (player != null) {
                ServerStalkerUtil.setVisualCenter(player, BlockPos.ZERO);
                player.getPersistentData().putBoolean("LoadingCacheChunk", true);
            }
        }

        InstanceMap.remove(playerUUID);
        StalkerToPlayerMap.remove(stalkerId);
    }

    public static Stalker connect(Player player, Entity stalker) {
        if (player == null || stalker == null) return null;
        if (hasInstanceOf(player) || hasInstanceOf(stalker)) return null;
        if (player.level().isClientSide) {
            StalkerControl.connect(player, stalker);
            NetworkHandler.CHANNEL.sendToServer(new StalkerSyncPacket(stalker.getId(), true));
            ClientStalkerUtil.cancelRemoteConnect();
        }

        ConnectEvent event = new ConnectEvent(player, stalker);
        MinecraftForge.EVENT_BUS.post(event);

        InstanceMap.put(player.getUUID(), stalker.getId());
        StalkerToPlayerMap.put(stalker.getId(), player.getUUID());
        return new Stalker(player.getUUID(), stalker.getId(), player.level());
    }

    public static boolean hasInstanceOf(Entity entity) {
        if (entity == null) return false;
        boolean isPlayer = InstanceMap.containsKey(entity.getUUID());
        boolean isStalker = StalkerToPlayerMap.containsKey(entity.getId());
        return (isPlayer || isStalker);
    }

    public static Stalker getInstanceOf(Entity entity) {
        if (entity == null) return null;

        if (InstanceMap.containsKey(entity.getUUID())) {
            Integer stalkerId = InstanceMap.get(entity.getUUID());
            if (stalkerId != null) {
                return new Stalker(entity.getUUID(), stalkerId, entity.level());
            }
        }

        if (StalkerToPlayerMap.containsKey(entity.getId())) {
            UUID playerUUID = StalkerToPlayerMap.get(entity.getId());
            if (playerUUID != null) {
                return new Stalker(playerUUID, entity.getId(), entity.level());
            }
        }

        return null;
    }

    public static void cleanupPlayer(UUID playerUUID) {
        Integer stalkerId = InstanceMap.remove(playerUUID);
        if (stalkerId != null) {
            StalkerToPlayerMap.remove(stalkerId);
        }
    }

    public static void cleanupStalker(int stalkerId) {
        UUID playerUUID = StalkerToPlayerMap.remove(stalkerId);
        if (playerUUID != null) {
            InstanceMap.remove(playerUUID);
        }
    }

    public static void cleanupLevel(Level level) {
        InstanceMap.entrySet().removeIf(entry -> {
            UUID playerUUID = entry.getKey();
            Integer stalkerId = entry.getValue();

            Player player = level.getPlayerByUUID(playerUUID);
            Entity stalker = level.getEntity(stalkerId);

            if (player != null || stalker != null) {
                StalkerToPlayerMap.remove(stalkerId);
                return true;
            }
            return false;
        });
    }

    public static void cleanupInvalidMappings(Level level) {
        InstanceMap.entrySet().removeIf(entry -> {
            UUID playerUUID = entry.getKey();
            Integer stalkerId = entry.getValue();

            Player player = level.getPlayerByUUID(playerUUID);
            Entity stalker = level.getEntity(stalkerId);

            if (player == null || stalker == null) {
                StalkerToPlayerMap.remove(stalkerId);
                return true;
            }
            return false;
        });
    }

    public static int getMappingCount() {
        return InstanceMap.size();
    }

    public static class ConnectEvent extends PlayerEvent {
        private final Entity stalker;

        public ConnectEvent(Player player, Entity stalker) {
            super(player);
            this.stalker = stalker;
        }

        public Entity getStalker() {
            return stalker;
        }
    }

    public static class DisconnectEvent extends PlayerEvent {
        private final Entity stalker;

        public DisconnectEvent(Player player, Entity stalker) {
            super(player);
            this.stalker = stalker;
        }

        public Entity getStalker() {
            return stalker;
        }
    }
}