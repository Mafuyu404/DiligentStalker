package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.data.StalkerDataAttachments;
import com.mafuyu404.diligentstalker.component.StalkerDataComponents;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.registry.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(DiligentStalker.MODID)
public class DiligentStalker {
    public static final String MODID = "diligentstalker";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean IS_DEVELOPMENT_ENV = !FMLLoader.isProduction();

    public static void debug(Object source, String msg, Object... params) {
        if (IS_DEVELOPMENT_ENV) {
            String className = source instanceof Class<?> c ? c.getSimpleName() :
                    source.getClass().getSimpleName();
            LOGGER.debug("[DEBUG][{}] {}", className, String.format(msg, params));
        }
    }

    public static void debug(Object source, String msg) {
        if (IS_DEVELOPMENT_ENV) {
            String className = source instanceof Class<?> c ? c.getSimpleName() :
                    source.getClass().getSimpleName();
            LOGGER.debug("[DEBUG][{}] {}", className, msg);
        }
    }


    public DiligentStalker(IEventBus modEventBus, ModContainer modContainer) {
        registerConfig(modContainer);
        init(modEventBus);
    }

    private static void init(IEventBus eventBus) {
        StalkerEntities.ENTITIES.register(eventBus);
        StalkerBlocks.BLOCKS.register(eventBus);
        StalkerBlockEntities.BLOCK_ENTITIES.register(eventBus);
        StalkerItems.ITEMS.register(eventBus);
        StalkerCreativeModeTab.TABS.register(eventBus);
        StalkerDataAttachments.ATTACHMENT_TYPES.register(eventBus);
        StalkerDataComponents.DATA_COMPONENT_TYPES.register(eventBus);
        eventBus.addListener(NetworkHandler::register);
    }

    private static void registerConfig(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.init());
    }
}
