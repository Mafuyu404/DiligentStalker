package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.*;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class ChunkLoader {
    private static final TicketType<ChunkPos> StalkerTicket = TicketType.create(new ResourceLocation(MODID, "stalker").toString(), Comparator.comparingLong(ChunkPos::toLong));
    private static final HashMap<String, ArrayList<ChunkPos>> loaders = new HashMap<>();

    public static void add(ServerLevel level, ChunkPos center) {
        String key = level.dimension().toString();
        if (!loaders.containsKey(key)) loaders.put(key, new ArrayList<>());
        if (!loaders.get(key).contains(center)) {
            loaders.get(key).add(center);
//            level.getChunkSource().addRegionTicket(StalkerTicket, center, 33, center);
            ForgeChunkManager.forceChunk(level, DiligentStalker.MODID, center.getMiddleBlockPosition(0),
                    center.x, center.z, true, true);
        }
    }
    public static void removeAll(ServerLevel level) {
        String key = level.dimension().toString();
        if (!loaders.containsKey(key)) return;
        if (loaders.get(key).isEmpty()) return;
        Iterator<ChunkPos> iterator = loaders.get(key).iterator();
        while (iterator.hasNext()) {
            ChunkPos center = iterator.next();
            iterator.remove();
//            level.getChunkSource().addRegionTicket(StalkerTicket, chunkPos, 33, chunkPos);
            ForgeChunkManager.forceChunk(level, DiligentStalker.MODID, center.getMiddleBlockPosition(0),
                    center.x, center.z, false, false);
        }
    }
}