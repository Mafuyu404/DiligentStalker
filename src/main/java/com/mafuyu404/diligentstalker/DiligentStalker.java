package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.registry.*;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
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
    }
}
