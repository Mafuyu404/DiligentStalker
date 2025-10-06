package com.mafuyu404.diligentstalker.chunk;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

//TODO 区块加载嫌疑二号，有待重构
public final class DiligentChunkManager {
    private DiligentChunkManager() {}

    private static final TicketType<TicketOwner<BlockPos>> BLOCK_TICKING = TicketType.create("diligent:block_ticking", Comparator.comparing(TicketOwner::stableKey));
    private static final TicketType<TicketOwner<UUID>> ENTITY = TicketType.create("diligent:entity", Comparator.comparing(TicketOwner::stableKey));
    private static final TicketType<TicketOwner<UUID>> ENTITY_TICKING = TicketType.create("diligent:entity_ticking", Comparator.comparing(TicketOwner::stableKey));

    private static final Map<String, LoadingValidationCallback> callbacks = new HashMap<>();

    public static void setForcedChunkLoadingCallback(String modId, LoadingValidationCallback callback) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            callbacks.put(modId, callback);
        }
    }

    public static boolean hasForcedChunks(ServerLevel level) {
        DiligentForcedChunksData data = level.getDataStorage().get(DiligentForcedChunksData.factory(), DiligentForcedChunksData.SAVE_ID);
        if (data == null) return false;
        return !data.getBlockForcedChunks().isEmpty() || !data.getEntityForcedChunks().isEmpty();
    }

    public static boolean forceChunk(ServerLevel level, String modId, BlockPos owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return forceChunk(level, modId, owner, chunkX, chunkZ, add, ticking,
                ticking ? BLOCK_TICKING : null,
                DiligentForcedChunksData::getBlockForcedChunks);
    }

    public static boolean forceChunk(ServerLevel level, String modId, Entity owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return forceChunk(level, modId, owner.getUUID(), chunkX, chunkZ, add, ticking);
    }

    public static boolean forceChunk(ServerLevel level, String modId, UUID owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return forceChunk(level, modId, owner, chunkX, chunkZ, add, ticking,
                ticking ? ENTITY_TICKING : ENTITY,
                DiligentForcedChunksData::getEntityForcedChunks);
    }

    private static <T extends Comparable<? super T>> boolean forceChunk(ServerLevel level,
                                                                        String modId,
                                                                        T owner,
                                                                        int chunkX,
                                                                        int chunkZ,
                                                                        boolean add,
                                                                        boolean ticking,
                                                                        TicketType<TicketOwner<T>> ticketType,
                                                                        Function<DiligentForcedChunksData, TicketTracker<T>> trackerGetter) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            return false;
        }
        DiligentForcedChunksData saveData = level.getDataStorage().computeIfAbsent(DiligentForcedChunksData.factory(), DiligentForcedChunksData.SAVE_ID);

        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        long chunk = pos.toLong();
        TicketTracker<T> tickets = trackerGetter.apply(saveData);
        TicketOwner<T> ticketOwner = new TicketOwner<>(modId, owner);
        boolean success;
        if (add) {
            success = tickets.add(ticketOwner, chunk, ticking);
            if (success) {
                // 保证区块立刻存在
                level.getChunk(chunkX, chunkZ);
            }
        } else {
            success = tickets.remove(ticketOwner, chunk, ticking);
        }
        if (success) {
            saveData.setDirty(true);
            applyTicketToLevel(level, pos, ticketOwner, add, ticking, ticketType);
        }
        return success;
    }

    private static <T extends Comparable<? super T>> void applyTicketToLevel(ServerLevel level,
                                                                             ChunkPos pos,
                                                                             TicketOwner<T> owner,
                                                                             boolean add,
                                                                             boolean ticking,
                                                                             TicketType<TicketOwner<T>> ticketType) {
        ServerChunkCache chunkSource = level.getChunkSource();
        if (ticking) {
            if (add) {
                chunkSource.addRegionTicket(ticketType, pos, 2, owner);
            } else {
                chunkSource.removeRegionTicket(ticketType, pos, 2, owner);
            }
        } else {
            // 非全 Tick 的场景复用 vanilla 的 forced 标记以保持行为一致
            chunkSource.updateChunkForced(pos, add);
        }
    }

    public static void reinstatePersistentChunks(ServerLevel level, DiligentForcedChunksData saveData) {
        if (!callbacks.isEmpty()) {
            Map<String, Map<BlockPos, Pair<LongSet, LongSet>>> blockTickets = gatherTicketsByModId(saveData.getBlockForcedChunks());
            Map<String, Map<UUID, Pair<LongSet, LongSet>>> entityTickets = gatherTicketsByModId(saveData.getEntityForcedChunks());
            for (Map.Entry<String, LoadingValidationCallback> entry : callbacks.entrySet()) {
                String modId = entry.getKey();
                boolean hasBlockTicket = blockTickets.containsKey(modId);
                boolean hasEntityTicket = entityTickets.containsKey(modId);
                if (hasBlockTicket || hasEntityTicket) {
                    Map<BlockPos, Pair<LongSet, LongSet>> ownedBlockTickets = hasBlockTicket ? Collections.unmodifiableMap(blockTickets.get(modId)) : Collections.emptyMap();
                    Map<UUID, Pair<LongSet, LongSet>> ownedEntityTickets = hasEntityTicket ? Collections.unmodifiableMap(entityTickets.get(modId)) : Collections.emptyMap();
                    entry.getValue().validateTickets(level, new TicketHelper(saveData, modId, ownedBlockTickets, ownedEntityTickets));
                }
            }
        }
        // 重新应用到运行时
        reinstate(level, saveData.getBlockForcedChunks().chunks, false, BLOCK_TICKING);
        reinstate(level, saveData.getBlockForcedChunks().tickingChunks, true, BLOCK_TICKING);
        reinstate(level, saveData.getEntityForcedChunks().chunks, false, ENTITY);
        reinstate(level, saveData.getEntityForcedChunks().tickingChunks, true, ENTITY_TICKING);
    }

    private static <T extends Comparable<? super T>> Map<String, Map<T, Pair<LongSet, LongSet>>> gatherTicketsByModId(TicketTracker<T> tickets) {
        Map<String, Map<T, Pair<LongSet, LongSet>>> modSorted = new HashMap<>();
        accumulate(modSorted, tickets.chunks, Pair::getFirst);
        accumulate(modSorted, tickets.tickingChunks, Pair::getSecond);
        return modSorted;
    }

    private static <T extends Comparable<? super T>> void accumulate(Map<String, Map<T, Pair<LongSet, LongSet>>> modSorted,
                                                                     Map<TicketOwner<T>, LongSet> src,
                                                                     Function<Pair<LongSet, LongSet>, LongSet> pick) {
        for (Map.Entry<TicketOwner<T>, LongSet> e : src.entrySet()) {
            Pair<LongSet, LongSet> pair = modSorted
                    .computeIfAbsent(e.getKey().modId, k -> new HashMap<>())
                    .computeIfAbsent(e.getKey().owner, k -> new Pair<>(new LongOpenHashSet(), new LongOpenHashSet()));
            pick.apply(pair).addAll(e.getValue());
        }
    }

    private static <T extends Comparable<? super T>> void reinstate(ServerLevel level,
                                                                    Map<TicketOwner<T>, LongSet> tickets,
                                                                    boolean ticking,
                                                                    TicketType<TicketOwner<T>> type) {
        for (Map.Entry<TicketOwner<T>, LongSet> e : tickets.entrySet()) {
            for (long chunk : e.getValue()) {
                applyTicketToLevel(level, new ChunkPos(chunk), e.getKey(), true, ticking, type);
            }
        }
    }

    @FunctionalInterface
    public interface LoadingValidationCallback {
        void validateTickets(ServerLevel level, TicketHelper helper);
    }

    public static final class TicketHelper {
        private final DiligentForcedChunksData saveData;
        private final String modId;
        private final Map<BlockPos, Pair<LongSet, LongSet>> blockTickets;
        private final Map<UUID, Pair<LongSet, LongSet>> entityTickets;

        private TicketHelper(DiligentForcedChunksData saveData,
                             String modId,
                             Map<BlockPos, Pair<LongSet, LongSet>> blockTickets,
                             Map<UUID, Pair<LongSet, LongSet>> entityTickets) {
            this.saveData = saveData;
            this.modId = modId;
            this.blockTickets = blockTickets;
            this.entityTickets = entityTickets;
        }

        public Map<BlockPos, Pair<LongSet, LongSet>> getBlockTickets() { return blockTickets; }
        public Map<UUID, Pair<LongSet, LongSet>> getEntityTickets() { return entityTickets; }

        public void removeTicket(BlockPos owner, long chunk, boolean ticking) {
            remove(saveData.getBlockForcedChunks(), owner, chunk, ticking);
        }
        public void removeTicket(UUID owner, long chunk, boolean ticking) {
            remove(saveData.getEntityForcedChunks(), owner, chunk, ticking);
        }
        public void removeAllTickets(UUID owner) {
            TicketTracker<UUID> t = saveData.getEntityForcedChunks();
            TicketOwner<UUID> key = new TicketOwner<>(modId, owner);
            LongSet a = t.chunks.remove(key);
            LongSet b = t.tickingChunks.remove(key);
            if ((a != null && !a.isEmpty()) || (b != null && !b.isEmpty())) {
                saveData.setDirty(true);
            }
        }
        private <T extends Comparable<? super T>> void remove(TicketTracker<T> tickets, T owner, long chunk, boolean ticking) {
            if (tickets.remove(new TicketOwner<>(modId, owner), chunk, ticking)) {
                saveData.setDirty(true);
            }
        }
    }

    public static final class TicketOwner<T> implements Comparable<TicketOwner<T>> {
        final String modId;
        final T owner;
        public TicketOwner(String modId, T owner) { this.modId = modId; this.owner = owner; }
        String stableKey() { return modId + "|" + owner; }
        @Override public int compareTo(TicketOwner<T> o) { return this.stableKey().compareTo(o.stableKey()); }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TicketOwner<?> other)) return false;
            return Objects.equals(modId, other.modId) && Objects.equals(owner, other.owner);
        }
        @Override public int hashCode() { return Objects.hash(modId, owner); }
    }

    public static final class TicketTracker<T> {
        final Map<TicketOwner<T>, LongSet> chunks = new HashMap<>();
        final Map<TicketOwner<T>, LongSet> tickingChunks = new HashMap<>();
        boolean isEmpty() { return chunks.isEmpty() && tickingChunks.isEmpty(); }
        boolean add(TicketOwner<T> owner, long chunk, boolean ticking) {
            return (ticking ? tickingChunks : chunks).computeIfAbsent(owner, k -> new LongOpenHashSet()).add(chunk);
        }
        boolean remove(TicketOwner<T> owner, long chunk, boolean ticking) {
            LongSet set = (ticking ? tickingChunks : chunks).get(owner);
            return set != null && set.remove(chunk);
        }
    }
}