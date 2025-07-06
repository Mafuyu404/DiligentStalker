package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class StalkerItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> CAMERA_STALKER = ITEMS.register("camera_stalker", () -> new BlockItem(StalkerBlocks.CAMERA_STALKER.get(), new Item.Properties()));

    public static final RegistryObject<Item> DRONE_STALKER = ITEMS.register("drone_stalker",
            DroneStalkerItem::new);
    public static final RegistryObject<Item> STALKER_MASTER = ITEMS.register("stalker_master",
            StalkerMasterItem::new);
    public static final RegistryObject<Item> ARROW_STALKER = ITEMS.register("arrow_stalker",
            ArrowStalkerItem::new);
    public static final RegistryObject<Item> VOID_STALKER = ITEMS.register("void_stalker",
            VoidStalkerItem::new);
    public static final RegistryObject<Item> STALKER_CORE = ITEMS.register("stalker_core",
            StalkerCoreItem::new);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> STALKER_GROUP = CREATIVE_MODE_TABS.register("diligentstalker", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.tab.diligentstalker")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> STALKER_MASTER.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(DRONE_STALKER.get());
        output.accept(ARROW_STALKER.get());
        output.accept(VOID_STALKER.get());
        output.accept(STALKER_MASTER.get());
        output.accept(STALKER_CORE.get());
        output.accept(CAMERA_STALKER.get());
    }).build());
}
