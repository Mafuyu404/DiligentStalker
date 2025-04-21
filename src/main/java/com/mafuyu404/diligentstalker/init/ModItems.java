package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.item.DroneStalkerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Creates a new BlockItem with the id "diligentstalker:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(ModBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "diligentstalker:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> DRONE_ITEM = ITEMS.register("drone_spawner",
            DroneStalkerItem::new);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> STALKER_GROUP = CREATIVE_MODE_TABS.register("diligentstalker", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
    }).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == STALKER_GROUP.getKey()) {
            event.accept(ModItems.EXAMPLE_ITEM);
            event.accept(ModItems.DRONE_ITEM);
        }
    }
}
