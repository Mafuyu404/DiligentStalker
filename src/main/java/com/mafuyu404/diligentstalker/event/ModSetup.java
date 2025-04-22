package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ForgeChunkManager.setForcedChunkLoadingCallback(DiligentStalker.MODID,
                ModSetup::validateChunkTickets
        );
    }

    private static void validateChunkTickets(ServerLevel level, ForgeChunkManager.TicketHelper helper) {
        helper.getEntityTickets().forEach((uuid, chunks) -> {
            if (level.getEntity(uuid) == null) {
                helper.removeAllTickets(uuid);
            }
        });
    }
}
