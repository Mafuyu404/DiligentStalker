# Diligent Stalker Rendering and Loading Roadmap

## Goals

- Support a remote visual center that can be far outside the player's vanilla view distance.
- Keep the server-side chunks around the visual center loaded while the remote view is active.
- Keep entity tracking, chunk watch/unwatch, light data, and client render chunks consistent with vanilla/Forge lifecycle.
- Expose the system through small APIs instead of spreading state across packet handlers, events, and Mixins.
- Reduce global patches. Prefer targeted hooks around chunk tracking, camera state, and client chunk cache behavior.

## Current Problems

- `ChunkMap.move` is cancelled and replaced with partial chunk sending. Vanilla also updates entity tracking, player map state, distance manager state, chunk cache center packets, and chunk unwatch events.
- `playerLoadedChunk` is used without an owned watch set, so there is no symmetric remote unwatch path.
- Server forced chunks are removed and re-added every 10 ticks instead of being diffed per owner.
- Client chunk packets are cancelled at packet handle time and replayed later, which can reorder chunk data, light data, biome updates, and forget-chunk packets.
- `ClientChunkCache.Storage.inRange` builds a chunk list in a hot path and does not address storage slot collisions between player-centered and remote-centered chunks.
- Camera state is split across `Minecraft#getCameraEntity`, `Camera#setPosition`, and `LevelRenderer#setupRender`.
- `Entity#distanceToSqr` is globally modified for controlled players, which can affect unrelated gameplay and other mods.

## Target Architecture

### Remote View Session

Create one authoritative session object per observing player.

Suggested API:

```java
public interface RemoteViewSession {
    ServerPlayer player();
    ResourceKey<Level> dimension();
    BlockPos centerBlock();
    ChunkPos centerChunk();
    int loadRadius();
    int renderRadius();
    RemoteViewMode mode();
    @Nullable Entity stalkerEntity();
}
```

Responsibilities:

- Own connection/disconnection lifecycle.
- Store player UUID, dimension, optional stalker UUID/entity id, visual center, and radii.
- Provide stable accessors for server events, network packets, and Mixins.
- Clear state on logout, dimension change, entity removal, death, and world unload.

### Server Remote Chunk Watcher

Create a server manager that tracks remote chunks watched by each player.

Suggested API:

```java
public interface RemoteChunkWatcher {
    void update(ServerPlayer player, RemoteViewSession session);
    void clear(ServerPlayer player);
    boolean isRemoteWatched(ServerPlayer player, ChunkPos pos);
}
```

Responsibilities:

- Maintain `Map<UUID, Set<ChunkPos>> watchedChunks`.
- Diff old and new target chunk sets.
- For added chunks, use the same payload path as vanilla `playerLoadedChunk`.
- For removed chunks, call `ServerPlayer#untrackChunk`, fire Forge chunk unwatch, and clean local bookkeeping.
- Avoid cancelling all of `ChunkMap.move`; keep vanilla movement and player chunk state intact.

### Server Chunk Ticket Manager

Replace full remove/add cycles with owner-aware tickets.

Suggested API:

```java
public interface RemoteChunkTicketManager {
    void setOwnedChunks(ServerLevel level, UUID owner, Set<ChunkPos> chunks);
    void clearOwner(ServerLevel level, UUID owner);
    void clearLevel(ServerLevel level);
}
```

Responsibilities:

- Maintain per-dimension `Map<ChunkPos, Set<UUID>>`.
- Add a region ticket only when the first owner starts using a chunk.
- Remove the ticket only when the last owner releases a chunk.
- Clamp radius through config.

### Client Remote Chunk Queue

Keep packet throttling explicit and ordered.

Suggested API:

```java
public interface RemoteChunkQueue {
    void enqueue(ClientboundLevelChunkWithLightPacket packet);
    void drain(ClientPacketListener connection, ClientLevel level, int budget);
    void clear();
}
```

Responsibilities:

- Use a queue or ordered map keyed by `ChunkPos`.
- Do not discard packets silently.
- Keep latest packet for a duplicated chunk position.
- Drain on client tick with a bounded budget.
- Clear on disconnect, level unload, or remote session end.

### Client View State

Create a single client-side source of truth.

Suggested API:

```java
public interface ClientRemoteViewState {
    boolean active();
    Vec3 cameraPosition(float partialTick);
    float cameraYaw();
    float cameraPitch();
    ChunkPos chunkCacheCenter();
    @Nullable Entity cameraEntity();
}
```

Responsibilities:

- For entity stalkers, prefer using Minecraft's camera entity lifecycle.
- For fixed cameras, provide a virtual center without pretending the player moved.
- Let renderer and chunk-cache Mixins read the same state.

## Mixin Policy

- Keep `ChunkMap$TrackedEntity.updatePlayer` style hooks for entity tracking, but scope them to active remote sessions and the correct dimension.
- Replace `ChunkMap.move` cancellation with a post-vanilla remote watcher update, or a targeted redirect that preserves vanilla player state.
- Keep a client chunk-cache range hook, but make it O(1) and backed by `ClientRemoteViewState`.
- Replace `LevelRenderer.setupRender` ordinal variable edits with a more stable hook around player position reads or `ViewArea.repositionCamera`.
- Remove `Entity#distanceToSqr` once tracking/render-specific patches cover the use cases.
- Avoid suppressing logger warnings globally. If a warning is expected, make the state transition correct instead.

## Migration Plan

### Phase 1: Stabilize Existing System

- Add radius config bounds.
- Fix client chunk queue so it does not skip entries or discard 40 percent of queued packets.
- Make `ClientChunkCache.Storage.inRange` use O(1) range checks.
- Clear client chunk queue on remote disconnect and level unload.
- Keep current behavior otherwise.

### Phase 2: Introduce Session and Managers

- Add `RemoteViewSession` and migrate `Stalker.InstanceMap` usage behind a manager API.
- Add dimension-aware stalker lookup.
- Add `RemoteChunkTicketManager` with owner-aware diffing.
- Replace `ChunkLoader.removeAll()` per tick with `setOwnedChunks`.

### Phase 3: Replace ChunkMap.move Cancellation

- Add a remote watched chunk set per player.
- Update that set when the remote center changes chunk.
- Add chunks through vanilla-compatible `playerLoadedChunk`.
- Remove chunks through untrack/unwatch.
- Stop using `teleportRelative(0, 0, 0)` as a trigger.
- Remove `ChunkMapMixin` HEAD cancellation.

### Phase 4: Client Render and Cache Cleanup

- Add `ClientRemoteViewState`.
- Make camera entity switching explicit with restore on disconnect.
- Rework fixed-camera mode as a virtual center.
- Replace ordinal-based `LevelRenderer.setupRender` modification with a stable hook.
- Decide whether remote view owns the normal client chunk cache center while active or needs a separate cache.

### Phase 5: Remove Broad Patches

- Remove `Entity#distanceToSqr` modification.
- Replace `Entity#setPosRaw` cancellation with correct reconnect/cache restoration.
- Replace packet-handle interception with listener-level or server-side throttling.
- Revisit Embeddium support with a renderer adapter instead of disabling behavior.

## Verification Checklist

- Connect to a stalker outside vanilla view distance and see terrain, block entities, and entities.
- Move the stalker across chunk boundaries without missing chunks or flickering old chunks.
- Disconnect and verify the player area reloads without void fall or stale remote chunks.
- Test two players watching different stalkers in the same dimension.
- Test two stalkers sharing some forced chunks and verify tickets are not removed too early.
- Test dimension changes and world unload.
- Check logs for chunk range warnings after remote view transitions.
- Run `compileJava` after each phase.
