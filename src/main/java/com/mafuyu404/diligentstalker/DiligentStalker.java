package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.data.StalkerDataComponents;
import com.mafuyu404.diligentstalker.event.handler.ModSetup;
import com.mafuyu404.diligentstalker.event.handler.StalkerManage;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.registry.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class DiligentStalker implements ModInitializer {
    public static final String MODID = "diligentstalker";

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ModConfig.register();
        NetworkHandler.register();
        StalkerDataComponents.register();
        StalkerBlocks.register();
        StalkerItems.register();
        StalkerCreativeModeTab.register();
        StalkerEntities.register();
        StalkerBlockEntities.register();
        ModSetup.init();
        StalkerManage.initServerEvents();
    }
}
