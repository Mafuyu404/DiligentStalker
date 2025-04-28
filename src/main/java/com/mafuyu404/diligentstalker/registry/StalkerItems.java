package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.item.ArrowStalkerItem;
import com.mafuyu404.diligentstalker.item.DroneStalkerItem;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.item.VoidStalkerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class StalkerItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
//    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(StalkerBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> DRONE_STALKER_ITEM = ITEMS.register("drone_stalker",
            DroneStalkerItem::new);
    public static final RegistryObject<Item> STALKER_MASTER_ITEM = ITEMS.register("stalker_master",
            StalkerMasterItem::new);
    public static final RegistryObject<Item> ARROW_STALKER_ITEM = ITEMS.register("arrow_stalker",
            ArrowStalkerItem::new);
    public static final RegistryObject<Item> VOID_STALKER_ITEM = ITEMS.register("void_stalker",
            VoidStalkerItem::new);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> STALKER_GROUP = CREATIVE_MODE_TABS.register("diligentstalker", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.tab.diligentstalker")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> STALKER_MASTER_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(DRONE_STALKER_ITEM.get());
        output.accept(ARROW_STALKER_ITEM.get());
        output.accept(VOID_STALKER_ITEM.get());
        output.accept(STALKER_MASTER_ITEM.get());
    }).build());
}
