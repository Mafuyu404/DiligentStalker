package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ChunkLoadTask {
    private static final int PREPARE_INTERVAL = 10;
    private static final boolean CacheLock = false;
    private static boolean draining;

    public static final LinkedHashMap<Long, ClientboundLevelChunkWithLightPacket> TaskList = new LinkedHashMap<>();
    public static ArrayList<ClientboundLevelChunkWithLightPacket> WorkList = new ArrayList<>();
    public static int ChannelLimit = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        ClientStalkerUtil.applyRemoteChunkCenter();
        Stalker instance = Stalker.getInstanceOf(player);
        Entity stalker = instance == null ? null : instance.getStalker();
        ChunkPos remoteCenter = ClientStalkerUtil.getRemoteChunkCenter();
        if (stalker == null && remoteCenter == null) {
            clear();
            return;
        }

        if (player.tickCount % PREPARE_INTERVAL == 0 && !TaskList.isEmpty()) {
            ChannelLimit = Math.max(1, (int) Math.ceil(TaskList.size() * 1d / PREPARE_INTERVAL));
            WorkList = stalker == null
                    ? createChunksLoadTask(remoteCenter, TaskList)
                    : createChunksLoadTask(stalker, TaskList);
            TaskList.clear();
        }

        if (WorkList.isEmpty()) return;
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null) return;

        int processed = 0;
        while (!WorkList.isEmpty() && processed < ChannelLimit) {
            ClientboundLevelChunkWithLightPacket packet = WorkList.remove(0);
            if (level != null && level.getChunkSource().hasChunk(packet.getX(), packet.getZ()) && CacheLock) {
                continue;
            }
            try {
                draining = true;
                connection.handleLevelChunkWithLight(packet);
            } finally {
                draining = false;
            }
            processed++;
        }
    }

    public static boolean isDraining() {
        return draining;
    }

    public static void add(ClientboundLevelChunkWithLightPacket packet) {
        TaskList.put(ChunkPos.asLong(packet.getX(), packet.getZ()), packet);
    }

    public static void clear() {
        TaskList.clear();
        WorkList.clear();
        ChannelLimit = 0;
    }

    public static ArrayList<ClientboundLevelChunkWithLightPacket> createChunksLoadTask(
            Entity stalker,
            Map<Long, ClientboundLevelChunkWithLightPacket> toLoadChunks
    ) {
        ArrayList<ClientboundLevelChunkWithLightPacket> result = new ArrayList<>(toLoadChunks.values());
        result.sort(chunkComparator(stalker));
        return result;
    }

    public static ArrayList<ClientboundLevelChunkWithLightPacket> createChunksLoadTask(
            ChunkPos center,
            Map<Long, ClientboundLevelChunkWithLightPacket> toLoadChunks
    ) {
        ArrayList<ClientboundLevelChunkWithLightPacket> result = new ArrayList<>(toLoadChunks.values());
        Vec3 start = center.getWorldPosition().getCenter();
        result.sort(Comparator.comparingDouble(packet -> {
            Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
            return end.subtract(start).length();
        }));
        return result;
    }

    public static ArrayList<ClientboundLevelChunkWithLightPacket> createChunksLoadTask(
            Entity stalker,
            ArrayList<ClientboundLevelChunkWithLightPacket> toLoadChunks
    ) {
        ArrayList<ClientboundLevelChunkWithLightPacket> result = new ArrayList<>(toLoadChunks);
        result.sort(chunkComparator(stalker));
        return result;
    }

    public static boolean isInRemoteRange(Entity stalker, int x, int z, int offset) {
        if (stalker == null) return false;
        ChunkPos center = stalker.chunkPosition();
        int radius = StalkerUtil.getLoadRadius(stalker, offset);
        return StalkerUtil.isChunkInRadius(center, x, z, radius);
    }

    private static Comparator<ClientboundLevelChunkWithLightPacket> chunkComparator(Entity stalker) {
        Vec3 direction = StalkerUtil.calculateViewVector(StalkerControl.xRot, StalkerControl.yRot);
        Vec3 start = stalker.chunkPosition().getWorldPosition().getCenter();
        return Comparator
                .comparingDouble((ClientboundLevelChunkWithLightPacket packet) -> {
                    Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
                    return -StalkerUtil.calculateViewAlignment(direction, start, end);
                })
                .thenComparingDouble(packet -> {
                    Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
                    return end.subtract(start).length();
                });
    }
}
