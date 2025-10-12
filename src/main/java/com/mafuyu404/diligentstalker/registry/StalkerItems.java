package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class StalkerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> CAMERA_STALKER = ITEMS.register("camera_stalker", () -> new BlockItem(StalkerBlocks.CAMERA_STALKER.get(), new Item.Properties()));

    public static final DeferredItem<Item> DRONE_STALKER = ITEMS.register("drone_stalker",
            DroneStalkerItem::new);
    public static final DeferredItem<Item> STALKER_MASTER = ITEMS.register("stalker_master",
            StalkerMasterItem::new);
    public static final DeferredItem<Item> ARROW_STALKER = ITEMS.register("arrow_stalker",
            ArrowStalkerItem::new);
    public static final DeferredItem<Item> VOID_STALKER = ITEMS.register("void_stalker",
            VoidStalkerItem::new);
    public static final DeferredItem<Item> STALKER_CORE = ITEMS.register("stalker_core",
            StalkerCoreItem::new);
}