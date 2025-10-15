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
import java.util.function.Function;

@EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ChunkLoadTask {
    public static final List<ClientboundLevelChunkWithLightPacket> TASK_LIST = Collections.synchronizedList(new ArrayList<>());
    public static final List<ClientboundLevelChunkWithLightPacket> WORK_LIST = new ArrayList<>();
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
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if (player == null || level == null) return;
        if (!Stalker.hasInstanceOf(player)) return;

        Entity stalker = Stalker.getInstanceOf(player).getStalker();

        synchronized (TASK_LIST) {
            TASK_LIST.removeIf(Objects::isNull);
            if (!TASK_LIST.isEmpty() && WORK_LIST.isEmpty()) {
                WORK_LIST.addAll(createChunksLoadTask(stalker, TASK_LIST));
                if (!FMLLoader.isProduction()) {
                    DS_LOGGER.debug("[DS][client] schedule chunk tasks total={} selected={} desiredPerTick={}",
                            TASK_LIST.size(), WORK_LIST.size(), DESIRED_CHUNKS_PER_TICK);
                }
                TASK_LIST.clear();
            }
        }

        if (WORK_LIST.isEmpty()) return;

        ClientPacketListener connection = mc.getConnection();
        if (connection == null) return;

        channelLimit = Math.max(1, (int) Math.ceil(DESIRED_CHUNKS_PER_TICK));

        Iterator<ClientboundLevelChunkWithLightPacket> it = WORK_LIST.iterator();
        int count = 0;

        while (it.hasNext() && count < channelLimit) {
            ClientboundLevelChunkWithLightPacket packet = it.next();
            if (packet == null) {
                it.remove();
                continue;
            }

            boolean hasBefore = level.getChunkSource().hasChunk(packet.getX(), packet.getZ());
            if (!FMLLoader.isProduction()) {
                DS_LOGGER.debug("[DS][client] send chunk x={} z={} hasBefore={}", packet.getX(), packet.getZ(), hasBefore);
            }

            if (!hasBefore) {
                connection.handleLevelChunkWithLight(packet);
            }

            boolean hasAfter = level.getChunkSource().hasChunk(packet.getX(), packet.getZ());
            if (!FMLLoader.isProduction()) {
                DS_LOGGER.debug("[DS][client] handled chunk x={} z={} hasAfter={}", packet.getX(), packet.getZ(), hasAfter);
            }

            it.remove();
            count++;
        }
    }

    public static List<ClientboundLevelChunkWithLightPacket> createChunksLoadTask(Entity stalker, List<ClientboundLevelChunkWithLightPacket> toLoadChunks) {
        List<ClientboundLevelChunkWithLightPacket> safeList = new ArrayList<>(toLoadChunks);
        safeList.removeIf(Objects::isNull);

        Vec3 direction = StalkerUtil.calculateViewVector(StalkerControl.xRot, StalkerControl.yRot);
        Vec3 startCenter = stalker.chunkPosition().getWorldPosition().getCenter();

        // 按视线方向排序
        sortChunks(safeList, packet -> {
            Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
            return -StalkerUtil.calculateViewAlignment(direction, startCenter, end);
        });

        // 截取前 60%
        int limit = (int) (safeList.size() * 0.6);
        List<ClientboundLevelChunkWithLightPacket> result = new ArrayList<>(safeList.subList(0, Math.max(1, limit)));

        // 后按距离排序
        sortChunks(result, packet -> {
            Vec3 end = new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter();
            return end.subtract(startCenter).length();
        });

        return result;
    }

    public static void sortChunks(List<ClientboundLevelChunkWithLightPacket> chunks, Function<ClientboundLevelChunkWithLightPacket, Double> handler) {
        if (chunks == null || chunks.size() <= 1) return;
        chunks.removeIf(Objects::isNull);

        chunks.sort(Comparator.comparingDouble(handler::apply));
    }
}
