package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.registry.ModBlocks;
import com.mafuyu404.diligentstalker.registry.ModEntities;
import com.mafuyu404.diligentstalker.registry.ModItems;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DiligentStalker.MODID)
public class DiligentStalker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "diligentstalker";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Creates a new Block with the id "diligentstalker:example_block", combining the namespace and path


    // Creates a creative tab with the id "diligentstalker:example_tab" for the example item, that is placed after the combat tab

//    public static final RegistryObject<EntityType<DroneStalkerEntity>> DRONE_STALKER = ENTITIES.register("drone",
//            () -> EntityType.Builder.of(DroneStalkerEntity::new, MobCategory.MISC)
//                    .sized(0.9f, 0.8f) // 尺寸调整
//                    .build("drone"));

    public DiligentStalker() {
        NetworkHandler.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
//        MinecraftForge.EVENT_BUS.register(this);
    }

    // Add the example block item to the building blocks tab

    // You can use SubscribeEvent and let the Event Bus discover methods to call
//    @SubscribeEvent
//    public void onServerStarting(ServerStartingEvent event) {
//        // Do something when the server starts
//        LOGGER.info("HELLO from server starting");
//    }
//
//    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
//    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//    public static class ClientModEvents {
//
//        @SubscribeEvent
//        public static void onClientSetup(FMLClientSetupEvent event) {
//            // Some client setup code
//            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstanceOf().getUser().getName());
//        }
//    }
}
