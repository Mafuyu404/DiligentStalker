package com.mafuyu404.diligentstalker;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.ModBlocks;
import com.mafuyu404.diligentstalker.init.ModEntities;
import com.mafuyu404.diligentstalker.init.ModItems;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DiligentStalker.MODID)
public class DiligentStalker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "diligentstalker";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

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
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
//        }
//    }
}
