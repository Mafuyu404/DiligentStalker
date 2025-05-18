package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class StalkerItems {
    public static Item CAMERA_STALKER;
    public static Item DRONE_STALKER;
    public static Item STALKER_MASTER;
    public static Item ARROW_STALKER;
    public static Item VOID_STALKER;
    public static Item STALKER_CORE;



    public static void register() {
        // 注册物品
        CAMERA_STALKER = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(DiligentStalker.MODID, "camera_stalker"),
                new BlockItem(StalkerBlocks.CAMERA_STALKER, new FabricItemSettings())
        );

        DRONE_STALKER = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(DiligentStalker.MODID, "drone_stalker"),
                new DroneStalkerItem()
        );

        STALKER_MASTER = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(DiligentStalker.MODID, "stalker_master"),
                new StalkerMasterItem()
        );

        ARROW_STALKER = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(DiligentStalker.MODID, "arrow_stalker"),
                new ArrowStalkerItem()
        );

        VOID_STALKER = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(DiligentStalker.MODID, "void_stalker"),
                new VoidStalkerItem()
        );

        STALKER_CORE = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(DiligentStalker.MODID, "stalker_core"),
                new StalkerCoreItem()
        );
    }
}