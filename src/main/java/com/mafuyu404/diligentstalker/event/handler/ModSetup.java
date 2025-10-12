package com.mafuyu404.diligentstalker.event.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

@EventBusSubscriber(modid = MODID)
public class ModSetup {

    @SubscribeEvent
    public static void onRegisterTicketControllers(RegisterTicketControllersEvent event) {
        event.register(new TicketController(
                ResourceLocation.fromNamespaceAndPath(MODID, "default"),
                ModSetup::validateChunkTickets
        ));
    }

    private static void validateChunkTickets(ServerLevel level, TicketHelper helper) {
        helper.getEntityTickets().forEach((uuid, ticketSet) -> {
            if (level.getEntity(uuid) == null) {
                helper.removeAllTickets(uuid);
            }
        });
    }
}