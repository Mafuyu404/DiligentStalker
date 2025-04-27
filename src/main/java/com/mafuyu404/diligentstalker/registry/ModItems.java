package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.item.ArrowStalkerItem;
import com.mafuyu404.diligentstalker.item.DroneStalkerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(ModBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> DRONE_STALKER_ITEM = ITEMS.register("drone_stalker",
            DroneStalkerItem::new);
    public static final RegistryObject<Item> ARROW_STALKER_ITEM = ITEMS.register("arrow_stalker",
            () -> new ArrowStalkerItem(new Item.Properties()));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> STALKER_GROUP = CREATIVE_MODE_TABS.register("diligentstalker", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.tab.diligentstalker")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> DRONE_STALKER_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(DRONE_STALKER_ITEM.get());
        output.accept(ARROW_STALKER_ITEM.get());
    }).build());
}
