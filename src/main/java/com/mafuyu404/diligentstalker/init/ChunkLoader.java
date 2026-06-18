package com.mafuyu404.diligentstalker.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChunkLoader {
    private static HashMap<ResourceLocation, ChunkLoader> dimension = new HashMap<>();
    private static final UUID LEGACY_OWNER = new UUID(0L, 0L);

    public static ChunkLoader of(ServerLevel serverLevel) {
        ResourceLocation id = serverLevel.dimension().location();
        if (!dimension.containsKey(id)) dimension.put(id, new ChunkLoader(serverLevel));
        return dimension.get(id);
    }

    public static void init() {
        dimension = new HashMap<>();
    }

    private final ServerLevel level;
    private final Map<UUID, Set<ChunkPos>> chunksByOwner = new HashMap<>();
    private final Map<ChunkPos, Set<UUID>> ownersByChunk = new HashMap<>();

    public ChunkLoader(ServerLevel level) {
        this.level = level;
    }

    public void addChunk(ChunkPos chunkPos) {
        addOwner(chunkPos, LEGACY_OWNER);
        chunksByOwner.computeIfAbsent(LEGACY_OWNER, ignored -> new HashSet<>()).add(chunkPos);
    }

    public void setOwnedChunks(UUID owner, Collection<ChunkPos> chunks) {
        Set<ChunkPos> previous = chunksByOwner.getOrDefault(owner, Set.of());
        Set<ChunkPos> next = new HashSet<>(chunks);

        for (ChunkPos chunkPos : previous) {
            if (!next.contains(chunkPos)) {
                removeOwner(chunkPos, owner);
            }
        }

        for (ChunkPos chunkPos : next) {
            if (!previous.contains(chunkPos)) {
                addOwner(chunkPos, owner);
            }
        }

        if (next.isEmpty()) {
            chunksByOwner.remove(owner);
        } else {
            chunksByOwner.put(owner, next);
        }
    }

    public void clearOwner(UUID owner) {
        setOwnedChunks(owner, Set.of());
    }

    public void keepOnlyOwners(Set<UUID> activeOwners) {
        Set<UUID> knownOwners = new HashSet<>(chunksByOwner.keySet());
        for (UUID owner : knownOwners) {
            if (!activeOwners.contains(owner)) {
                clearOwner(owner);
            }
        }
    }

    public void removeAll() {
        for (ChunkPos chunkPos : new HashSet<>(ownersByChunk.keySet())) {
            removeTicket(chunkPos);
        }
        ownersByChunk.clear();
        chunksByOwner.clear();
    }

    private void addOwner(ChunkPos chunkPos, UUID owner) {
        Set<UUID> owners = ownersByChunk.computeIfAbsent(chunkPos, ignored -> new HashSet<>());
        if (owners.add(owner) && owners.size() == 1) {
            addTicket(chunkPos);
        }
    }

    private void removeOwner(ChunkPos chunkPos, UUID owner) {
        Set<UUID> owners = ownersByChunk.get(chunkPos);
        if (owners == null || !owners.remove(owner)) return;
        if (owners.isEmpty()) {
            ownersByChunk.remove(chunkPos);
            removeTicket(chunkPos);
        }
    }

    private void addTicket(ChunkPos chunkPos) {
        level.getChunkSource().addRegionTicket(
                TicketType.FORCED,
                chunkPos,
                2,
                chunkPos,
                true
        );
    }

    private void removeTicket(ChunkPos chunkPos) {
        level.getChunkSource().removeRegionTicket(
                TicketType.FORCED,
                chunkPos,
                2,
                chunkPos,
                true
        );
    }
}
