package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.api.Controllable;
import com.mafuyu404.diligentstalker.init.ControllableStorageProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ForgeChunkManager.setForcedChunkLoadingCallback(MODID,
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
