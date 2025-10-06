package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class StalkerCreativeModeTab {
    public static CreativeModeTab STALKER_GROUP;

    public static void register() {
        STALKER_GROUP = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "diligentstalker"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.tab.diligentstalker"))
                        .icon(() -> new ItemStack(StalkerItems.STALKER_MASTER))
                        .displayItems((context, output) -> {
                            output.accept(StalkerItems.DRONE_STALKER);
                            output.accept(StalkerItems.ARROW_STALKER);
                            output.accept(StalkerItems.VOID_STALKER);
                            output.accept(StalkerItems.STALKER_MASTER);
                            output.accept(StalkerItems.STALKER_CORE);
                            output.accept(StalkerItems.CAMERA_STALKER);
                        })
                        .build()
        );
    }
}
