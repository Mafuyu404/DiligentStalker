package com.mafuyu404.diligentstalker.event;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = "diligentstalker", value = Dist.CLIENT)
public class ChunkLoadTask {
    public static final List<ClientboundLevelChunkWithLightPacket> TASK_LIST = Collections.synchronizedList(new ArrayList<>());
    public static final List<ClientboundLevelChunkWithLightPacket> WORK_LIST = new ArrayList<>();
    public static int channelLimit = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if (event.phase != TickEvent.Phase.START || player == null || level == null) return;

        if (!Stalker.hasInstanceOf(player)) return;
        Entity stalker = Stalker.getInstanceOf(player).getStalker();

        int timer = 10;
        if (player.tickCount % timer == 0) {
            synchronized (TASK_LIST) {
                TASK_LIST.removeIf(Objects::isNull);
                if (TASK_LIST.isEmpty()) return;

                channelLimit = Math.max(1, (int) Math.ceil(TASK_LIST.size() / (double) timer));
                WORK_LIST.clear();
                WORK_LIST.addAll(createChunksLoadTask(stalker, TASK_LIST));
                TASK_LIST.clear();
            }
        }

        if (WORK_LIST.isEmpty()) return;

        ClientPacketListener connection = mc.getConnection();
        if (connection == null) return;

        Iterator<ClientboundLevelChunkWithLightPacket> it = WORK_LIST.iterator();
        int count = 0;

        while (it.hasNext() && count < channelLimit) {
            ClientboundLevelChunkWithLightPacket packet = it.next();
            if (packet == null) {
                it.remove();
                continue;
            }

            if (!level.getChunkSource().hasChunk(packet.getX(), packet.getZ())) {
                connection.handleLevelChunkWithLight(packet);
            }

            it.remove();
            count++;
        }
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

        return dedup;
    }

    public static void sortChunks(List<ClientboundLevelChunkWithLightPacket> chunks, Function<ClientboundLevelChunkWithLightPacket, Double> handler) {
        if (chunks == null || chunks.size() <= 1) return;
        chunks.removeIf(Objects::isNull);

        chunks.sort(Comparator.comparingDouble(handler::apply));
    }
}
