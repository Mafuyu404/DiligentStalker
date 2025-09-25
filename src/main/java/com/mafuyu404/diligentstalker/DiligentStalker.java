package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.api.Controllable;
import com.mafuyu404.diligentstalker.init.ControllableStorageProvider;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DiligentStalker.MODID)
public class DiligentStalker {
    public static final String MODID = "diligentstalker";

    public static final Logger LOGGER = LogUtils.getLogger();

    public DiligentStalker() {
        NetworkHandler.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StalkerEntities.ENTITIES.register(modEventBus);
        StalkerItems.ITEMS.register(modEventBus);
        StalkerBlocks.BLOCKS.register(modEventBus);
        StalkerBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        StalkerItems.CREATIVE_MODE_TABS.register(modEventBus);

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(Controllable.class);
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Entity) {
            ControllableStorageProvider provider = new ControllableStorageProvider();
            event.addCapability(
                    new ResourceLocation(MODID, "controllable_storage"),
                    provider
            );
        }
    }
}
