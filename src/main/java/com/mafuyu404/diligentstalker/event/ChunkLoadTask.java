package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
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
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ChunkLoadTask {
    public static ArrayList<ClientboundLevelChunkWithLightPacket> TaskList = new ArrayList<>();
    public static ArrayList<ClientboundLevelChunkWithLightPacket> WorkList = new ArrayList<>();
    public static int ChannelLimit = 0;
    private static boolean CacheLock = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (event.phase == TickEvent.Phase.START) return;
        if (Stalker.hasInstanceOf(player)) {
            Entity stalker = Stalker.getInstanceOf(player).getStalker();
            int timer = 10;
            if (player.tickCount % timer == 0) {
                ChannelLimit = (int) Math.ceil(TaskList.size() * 1d / timer);
                WorkList = createChunksLoadTask(stalker, TaskList);
                TaskList.clear();
            }
            if (WorkList.isEmpty()) return;
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null) return;
            for (int i = 0; i < WorkList.size(); i++) {
                if (i < ChannelLimit) {
                    ClientboundLevelChunkWithLightPacket packet = WorkList.get(i);
                    if (level.getChunkSource().hasChunk(packet.getX(), packet.getZ()) && CacheLock) continue;
                    connection.handleLevelChunkWithLight(packet);
                    WorkList.remove(i);
                    i++;
                }
            }
        }
    }

    public static ArrayList<ClientboundLevelChunkWithLightPacket> createChunksLoadTask(Entity stalker, ArrayList<ClientboundLevelChunkWithLightPacket> toLoadChunks) {
        LocalPlayer player = Minecraft.getInstance().player;
        Vec3 direction = player.getLookAngle();
        ArrayList<ClientboundLevelChunkWithLightPacket> result = new ArrayList<>();

        // 先对拟合度排序
        sortChunks(toLoadChunks, packet -> -1 * Tools.calculateViewAlignment(direction, stalker.chunkPosition().getWorldPosition().getCenter(), new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter()));
        for (int i = 0; i < toLoadChunks.size() * 0.6; i++) {
            result.add(toLoadChunks.get(i));
        }

        // 后对距离排序
        sortChunks(result, packet -> new ChunkPos(packet.getX(), packet.getZ()).getWorldPosition().getCenter().subtract(stalker.chunkPosition().getWorldPosition().getCenter()).length());

//        // 剔除部分区块
//        for (int i = 0; i < toLoadChunks.size() * 0.5; i++) {
//            result.add(toLoadChunks.get(i));
//        }

        return new ArrayList<>(result);
    }
    public static void sortChunks(ArrayList<ClientboundLevelChunkWithLightPacket> chunks, Function<ClientboundLevelChunkWithLightPacket, Double> handler) {
        if (chunks == null || chunks.size() <= 1) return;
        for (int i = 1; i < chunks.size(); i++) {
            ClientboundLevelChunkWithLightPacket current = chunks.get(i);
            double currentView = handler.apply(current);
            int j = i - 1;
            while (j >= 0 && handler.apply(chunks.get(j)) > currentView) {
                chunks.set(j + 1, chunks.get(j));
                j--;
            }
            chunks.set(j + 1, current);
        }
    }
}
