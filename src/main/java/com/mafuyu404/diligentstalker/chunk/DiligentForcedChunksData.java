package com.mafuyu404.diligentstalker.chunk;

import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager.TicketOwner;
import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager.TicketTracker;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.util.datafix.DataFixTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

//TODO 区块加载嫌疑3号，有待重构
public final class DiligentForcedChunksData extends SavedData {
    public static final String SAVE_ID = "diligent_forced";

    private DiligentChunkManager.TicketTracker<BlockPos> blockForcedChunks = new DiligentChunkManager.TicketTracker<>();
    private DiligentChunkManager.TicketTracker<UUID> entityForcedChunks = new DiligentChunkManager.TicketTracker<>();

    public DiligentChunkManager.TicketTracker<BlockPos> getBlockForcedChunks() { return blockForcedChunks; }
    public DiligentChunkManager.TicketTracker<UUID> getEntityForcedChunks() { return entityForcedChunks; }

    public static SavedData.Factory<DiligentForcedChunksData> factory() {
        return new SavedData.Factory<>(DiligentForcedChunksData::new, DiligentForcedChunksData::load, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
    }

    public static DiligentForcedChunksData load(CompoundTag nbt, HolderLookup.Provider provider) {
        DiligentForcedChunksData data = new DiligentForcedChunksData();
        readForced(nbt, data.blockForcedChunks, true);
        readForced(nbt, data.entityForcedChunks, false);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        writeForced(nbt, blockForcedChunks, true);
        writeForced(nbt, entityForcedChunks, false);
        return nbt;
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
            for (CompoundTag tag : entry.getValue().values()) modForced.add(tag);
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

    private static void readForced(CompoundTag nbt,
                                   TicketTracker<?> tracker,
                                   boolean block) {
        ListTag forced = nbt.getList("DiligentForced", Tag.TAG_COMPOUND);
        for (int i = 0; i < forced.size(); i++) {
            CompoundTag forcedEntry = forced.getCompound(i);
            String modId = forcedEntry.getString("Mod");
            ListTag modForced = forcedEntry.getList("ModForced", Tag.TAG_COMPOUND);
            for (int j = 0; j < modForced.size(); j++) {
                CompoundTag modEntry = modForced.getCompound(j);
                long chunkPos = modEntry.getLong("Chunk");
                if (block) {
                    readBlocks(modId, chunkPos, modEntry, "Blocks", ((TicketTracker<BlockPos>) tracker).chunks);
                    readBlocks(modId, chunkPos, modEntry, "TickingBlocks", ((TicketTracker<BlockPos>) tracker).tickingChunks);
                } else {
                    readEntities(modId, chunkPos, modEntry, "Entities", ((TicketTracker<UUID>) tracker).chunks);
                    readEntities(modId, chunkPos, modEntry, "TickingEntities", ((TicketTracker<UUID>) tracker).tickingChunks);
                }
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
            BlockPos pos = readBlockPos(forcedBlocks.getCompound(k));
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

    private static BlockPos readBlockPos(CompoundTag tag) {
        return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }
}