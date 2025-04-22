package com.mafuyu404.diligentstalker.init;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakePlayerManager {
    private static final Map<UUID, GameProfile> FAKE_PROFILES = new ConcurrentHashMap<>();
    private static final Map<UUID, ServerPlayer> FAKE_PLAYERS = new ConcurrentHashMap<>();

    public static ServerPlayer getOrCreate(ServerLevel level, Entity anchor) {
        UUID uuid = UUID.nameUUIDFromBytes(("FakePlayerFor_" + anchor.getUUID()).getBytes());

        return FAKE_PLAYERS.computeIfAbsent(uuid, id -> {
            GameProfile profile = new GameProfile(uuid, "[ChunkLoader]");
            ServerPlayer fakePlayer = new ServerPlayer(
                    level.getServer(), level,
                    profile
            );
            fakePlayer.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            // 重要：设置为旁观模式避免影响游戏机制
            fakePlayer.gameMode.changeGameModeForPlayer(GameType.SPECTATOR);
            return fakePlayer;
        });
    }

    public static void remove(UUID anchorId) {
        UUID fakeId = UUID.nameUUIDFromBytes(("FakePlayerFor_" + anchorId).getBytes());
        ServerPlayer fake = FAKE_PLAYERS.remove(fakeId);
        if (fake != null) {
//            fake.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
        }
    }

}
