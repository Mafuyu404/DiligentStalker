package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

@EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ChunkLoadTask {
    public static final Queue<ClientboundLevelChunkWithLightPacket> TASK_QUEUE = new ConcurrentLinkedQueue<>();
    public static final Queue<ClientboundLevelChunkWithLightPacket> WORK_QUEUE = new ConcurrentLinkedQueue<>();

    public static int channelLimit = 0;
    private static final Logger DS_LOGGER = LogUtils.getLogger();
    public static volatile float DESIRED_CHUNKS_PER_TICK = 20f;

    public static void setDesiredChunksPerTick(float value) {
        DESIRED_CHUNKS_PER_TICK = Math.max(1f, value);
        if (!FMLLoader.isProduction()) {
            DS_LOGGER.debug("[DS][client] update desiredChunksPerTick={}", DESIRED_CHUNKS_PER_TICK);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        long startTime = System.nanoTime();

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        if (!Stalker.hasInstanceOf(player)) return;
        Entity stalker = Stalker.getInstanceOf(player).getStalker();

        if (!TASK_QUEUE.isEmpty() && WORK_QUEUE.isEmpty()) {
            List<ClientboundLevelChunkWithLightPacket> buffer = new ArrayList<>();
            ClientboundLevelChunkWithLightPacket packet;
            while ((packet = TASK_QUEUE.poll()) != null) {
                buffer.add(packet);
            }

            if (!buffer.isEmpty()) {
                DS_LOGGER.debug("[DS][client] preparing WORK_QUEUE: taskSize={}", buffer.size());
                List<ClientboundLevelChunkWithLightPacket> sorted = createChunksLoadTask(stalker, buffer);
                WORK_QUEUE.addAll(sorted);
                DS_LOGGER.debug("[DS][client] WORK_QUEUE prepared: size={}", WORK_QUEUE.size());
            }
        }

        if (WORK_QUEUE.isEmpty()) return;
        ClientPacketListener connection = mc.getConnection();
        if (connection == null) return;

        channelLimit = Math.max(1, (int) Math.ceil(DESIRED_CHUNKS_PER_TICK));
        int count = 0;

        while (count < channelLimit) {
            ClientboundLevelChunkWithLightPacket packet = WORK_QUEUE.poll();
            if (packet == null) break;

            ChunkPos pos = new ChunkPos(packet.getX(), packet.getZ());
            boolean hasBefore = level.getChunkSource().hasChunk(pos.x, pos.z);
            DS_LOGGER.debug("[DS][client] begin send x={} z={} hasBefore={}", pos.x, pos.z, hasBefore);

            if (!hasBefore) {
                connection.handleLevelChunkWithLight(packet);
            }

            boolean hasAfter = level.getChunkSource().hasChunk(pos.x, pos.z);
            DS_LOGGER.debug("[DS][client] end send x={} z={} hasAfter={} (sent={})",
                    pos.x, pos.z, hasAfter, !hasBefore);

            count++;
        }

        long elapsed = (System.nanoTime() - startTime) / 1_000_000;
        DS_LOGGER.debug("[DS][client] tick done processed={} left={} took={}ms",
                count, WORK_QUEUE.size(), elapsed);
    }

    public static List<ClientboundLevelChunkWithLightPacket> createChunksLoadTask(Entity stalker, List<ClientboundLevelChunkWithLightPacket> toLoadChunks) {
        List<ClientboundLevelChunkWithLightPacket> safeList = new ArrayList<>(toLoadChunks);
        safeList.removeIf(Objects::isNull);
        if (safeList.isEmpty()) return safeList;

        Vec3 direction = StalkerUtil.calculateViewVector(StalkerControl.xRot, StalkerControl.yRot);
        Vec3 startCenter = stalker.chunkPosition().getWorldPosition().getCenter();

        // 按chunk坐标去重，保留首次出现的顺序
        Map<Long, ClientboundLevelChunkWithLightPacket> uniq = new LinkedHashMap<>();
        for (ClientboundLevelChunkWithLightPacket packet : safeList) {
            long key = ChunkPos.asLong(packet.getX(), packet.getZ());
            uniq.putIfAbsent(key, packet);
        }
        List<ClientboundLevelChunkWithLightPacket> dedup = new ArrayList<>(uniq.values());

        // 按视线方向优先
        sortChunks(dedup, packet -> {
            Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
            return -StalkerUtil.calculateViewAlignment(direction, startCenter, end);
        });

        // 再按距离优先
        sortChunks(dedup, packet -> {
            Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
            return end.subtract(startCenter).length();
        });

        DS_LOGGER.debug("[DS][client] createChunksLoadTask: total={} result={}", safeList.size(), dedup.size());
        return dedup;
    }

    public static void sortChunks(List<ClientboundLevelChunkWithLightPacket> chunks, Function<ClientboundLevelChunkWithLightPacket, Double> handler) {
        if (chunks == null || chunks.size() <= 1) return;
        chunks.removeIf(Objects::isNull);
        chunks.sort(Comparator.comparingDouble(handler::apply));
    }

    public static void enqueue(ClientboundLevelChunkWithLightPacket packet) {
        if (packet == null) return;
        TASK_QUEUE.offer(packet);
        DS_LOGGER.debug("[DS][client] enqueue chunk packet x={} z={} queueSize={}",
                packet.getX(), packet.getZ(), TASK_QUEUE.size());
    }
}
