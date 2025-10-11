package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager;
import net.minecraft.server.level.ServerLevel;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class ModSetup {
    public static void init() {
        DiligentChunkManager.setForcedChunkLoadingCallback(MODID, ModSetup::validateChunkTickets);
    }

    private static void validateChunkTickets(ServerLevel level, DiligentChunkManager.TicketHelper helper) {
        helper.getEntityTickets().forEach((uuid, pair) -> {
            if (level.getEntity(uuid) == null) {
                helper.removeAllTickets(uuid);
            }
        });
    }
}
