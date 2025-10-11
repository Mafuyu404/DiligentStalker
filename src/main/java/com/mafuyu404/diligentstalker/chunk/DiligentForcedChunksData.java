package com.mafuyu404.diligentstalker.chunk;

import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager.TicketOwner;
import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager.TicketTracker;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class DiligentForcedChunksData extends SavedData {
    public static final String SAVE_ID = "diligent_forced";

    private final DiligentChunkManager.TicketTracker<BlockPos> blockForcedChunks = new DiligentChunkManager.TicketTracker<>();
    private final DiligentChunkManager.TicketTracker<UUID> entityForcedChunks = new DiligentChunkManager.TicketTracker<>();

    public static DiligentForcedChunksData load(CompoundTag nbt) {
        DiligentForcedChunksData data = new DiligentForcedChunksData();
        readForcedBlocks(nbt, data.blockForcedChunks);
        readForcedEntities(nbt, data.entityForcedChunks);
        return data;
    }

    private static <T extends Comparable<? super T>> void writeForced(CompoundTag nbt,
                                                                      TicketTracker<T> tracker,
                                                                      boolean block) {
        if (tracker.isEmpty()) return;
        Map<String, Map<Long, CompoundTag>> forcedEntries = new HashMap<>();
        String listKey = block ? "Blocks" : "Entities";
        int listType = block ? Tag.TAG_COMPOUND : Tag.TAG_INT_ARRAY;
        BiConsumer<T, ListTag> ownerWriter = block
                ? (pos, forcedBlocks) -> forcedBlocks.add(NbtUtils.writeBlockPos((BlockPos) pos))
                : (uuid, forcedEntities) -> forcedEntities.add(NbtUtils.createUUID((UUID) uuid));
        writeOwners(forcedEntries, tracker.chunks, listKey, listType, ownerWriter);
        writeOwners(forcedEntries, tracker.tickingChunks, "Ticking" + listKey, listType, ownerWriter);

        ListTag forced = new ListTag();
        for (Map.Entry<String, Map<Long, CompoundTag>> entry : forcedEntries.entrySet()) {
            CompoundTag forcedEntry = new CompoundTag();
            forcedEntry.putString("Mod", entry.getKey());
            ListTag modForced = new ListTag();
            modForced.addAll(entry.getValue().values());
            forcedEntry.put("ModForced", modForced);
            forced.add(forcedEntry);
        }
        nbt.put("DiligentForced", forced);
    }

    private static <T extends Comparable<? super T>> void writeOwners(Map<String, Map<Long, CompoundTag>> forcedEntries,
                                                                      Map<TicketOwner<T>, LongSet> forcedChunks,
                                                                      String listKey,
                                                                      int listType,
                                                                      BiConsumer<T, ListTag> ownerWriter) {
        for (Map.Entry<TicketOwner<T>, LongSet> e : forcedChunks.entrySet()) {
            String modId = e.getKey().modId;
            Map<Long, CompoundTag> modForced = forcedEntries.computeIfAbsent(modId, k -> new HashMap<>());
            for (long chunk : e.getValue()) {
                CompoundTag modEntry = modForced.computeIfAbsent(chunk, c -> {
                    CompoundTag t = new CompoundTag();
                    t.putLong("Chunk", c);
                    return t;
                });
                ListTag ownerList = modEntry.getList(listKey, listType);
                ownerWriter.accept(e.getKey().owner, ownerList);
                modEntry.put(listKey, ownerList);
            }
        }
    }

    private static void readForcedBlocks(CompoundTag nbt, TicketTracker<BlockPos> tracker) {
        ListTag forced = nbt.getList("DiligentForced", Tag.TAG_COMPOUND);
        for (int i = 0; i < forced.size(); i++) {
            CompoundTag forcedEntry = forced.getCompound(i);
            String modId = forcedEntry.getString("Mod");
            ListTag modForced = forcedEntry.getList("ModForced", Tag.TAG_COMPOUND);
            for (int j = 0; j < modForced.size(); j++) {
                CompoundTag modEntry = modForced.getCompound(j);
                long chunkPos = modEntry.getLong("Chunk");
                readBlocks(modId, chunkPos, modEntry, "Blocks", tracker.chunks);
                readBlocks(modId, chunkPos, modEntry, "TickingBlocks", tracker.tickingChunks);
            }
        }
    }

    private static void readForcedEntities(CompoundTag nbt, TicketTracker<UUID> tracker) {
        ListTag forced = nbt.getList("DiligentForced", Tag.TAG_COMPOUND);
        for (int i = 0; i < forced.size(); i++) {
            CompoundTag forcedEntry = forced.getCompound(i);
            String modId = forcedEntry.getString("Mod");
            ListTag modForced = forcedEntry.getList("ModForced", Tag.TAG_COMPOUND);
            for (int j = 0; j < modForced.size(); j++) {
                CompoundTag modEntry = modForced.getCompound(j);
                long chunkPos = modEntry.getLong("Chunk");
                readEntities(modId, chunkPos, modEntry, "Entities", tracker.chunks);
                readEntities(modId, chunkPos, modEntry, "TickingEntities", tracker.tickingChunks);
            }
        }
    }

    private static void readBlocks(String modId,
                                   long chunkPos,
                                   CompoundTag modEntry,
                                   String key,
                                   Map<TicketOwner<BlockPos>, LongSet> out) {
        ListTag forcedBlocks = modEntry.getList(key, Tag.TAG_COMPOUND);
        for (int k = 0; k < forcedBlocks.size(); k++) {
            BlockPos pos = NbtUtils.readBlockPos(forcedBlocks.getCompound(k));
            out.computeIfAbsent(new TicketOwner<>(modId, pos), o -> new LongOpenHashSet()).add(chunkPos);
        }
    }

    private static void readEntities(String modId,
                                     long chunkPos,
                                     CompoundTag modEntry,
                                     String key,
                                     Map<TicketOwner<UUID>, LongSet> out) {
        ListTag forcedEntities = modEntry.getList(key, Tag.TAG_INT_ARRAY);
        for (Tag uuidTag : forcedEntities) {
            UUID uuid = NbtUtils.loadUUID(uuidTag);
            out.computeIfAbsent(new TicketOwner<>(modId, uuid), o -> new LongOpenHashSet()).add(chunkPos);
        }
    }

    public DiligentChunkManager.TicketTracker<BlockPos> getBlockForcedChunks() {
        return blockForcedChunks;
    }

    public DiligentChunkManager.TicketTracker<UUID> getEntityForcedChunks() {
        return entityForcedChunks;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        writeForced(nbt, blockForcedChunks, true);
        writeForced(nbt, entityForcedChunks, false);
        return nbt;
    }
}