package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.registry.*;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.registry.ModConfig;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class DiligentStalker implements ModInitializer {
    public static final String MODID = "diligentstalker";
    public static boolean HIDE_EXP_BAR = false;

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        NetworkHandler.register();
        // 加载配置
        ModConfig.register();
        
        // 注册实体、物品、方块等
        StalkerEntities.register();
        StalkerBlocks.register();
        StalkerItems.register();
        StalkerBlockEntities.register();
        StalkerCreativeModeTab.register();
        

    }
}
