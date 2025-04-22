package com.mafuyu404.diligentstalker.init;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class TicketController {
//    private static final int TICKET_DURATION = 600; // 30秒（600 ticks）
//    private final ServerLevel level;
//    private final Set<ChunkPos> loadedChunks = new HashSet<>();
//    private final TicketHelper tickets;
//
//    public TicketController(ServerLevel level) {
//        this.level = level;
//        this.tickets = new TicketHelper(level);
//    }
//
//    public void updateChunks(ChunkPos center, int radius) {
//        Set<ChunkPos> newChunks = calculateChunks(center, radius);
//        Set<ChunkPos> obsoleteChunks = new HashSet<>(loadedChunks);
//        obsoleteChunks.removeAll(newChunks);
//
//        // 释放旧区块
//        obsoleteChunks.forEach(pos -> {
//            tickets.releaseTicket(pos);
//            loadedChunks.remove(pos);
//        });
//
//        // 加载新区块
//        newChunks.forEach(pos -> {
//            if (!loadedChunks.contains(pos)) {
//                tickets.addTicket(pos, TICKET_DURATION);
//                loadedChunks.add(pos);
//            }
//        });
//    }
//
//    private Set<ChunkPos> calculateChunks(ChunkPos center, int radius) {
//        Set<ChunkPos> chunks = new HashSet<>();
//        for (int x = -radius; x <= radius; x++) {
//            for (int z = -radius; z <= radius; z++) {
//                chunks.add(new ChunkPos(center.x + x, center.z + z));
//            }
//        }
//        return chunks;
//    }
//
//    public void releaseAll() {
//        loadedChunks.forEach(tickets::releaseTicket);
//        loadedChunks.clear();
//    }
//
//    private static class TicketHelper {
//        private final ServerLevel level;
//        private final Map<ChunkPos, ChunkTicket<?>> activeTickets = new HashMap<>();
//
//        public TicketHelper(ServerLevel level) {
//            this.level = level;
//        }
//
//        public void addTicket(ChunkPos pos, int duration) {
//            ChunkTicket<?> ticket = new ChunkTicket<>(
//                    TicketType.FORCED,
//                    duration,
//                    new ChunkPos(pos.x, pos.z)
//            );
//            level.getChunkSource().addRegionTicket(
//                    TicketType.FORCED,
//                    pos,
//                    2, // 加载等级
//                    Unit.INSTANCE
//            );
//            activeTickets.put(pos, ticket);
//        }
//
//        public void releaseTicket(ChunkPos pos) {
//            if (activeTickets.containsKey(pos)) {
//                level.getChunkSource().removeRegionTicket(
//                        TicketType.FORCED,
//                        pos,
//                        2,
//                        Unit.INSTANCE
//                );
//                activeTickets.remove(pos);
//            }
//        }
//    }
}
