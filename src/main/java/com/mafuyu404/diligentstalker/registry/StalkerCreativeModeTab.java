package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;

public class StalkerCreativeModeTab {
    public static CreativeModeTab STALKER_GROUP;

    public static void register() {
        STALKER_GROUP = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(DiligentStalker.MODID, "diligentstalker"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.tab.diligentstalker"))
                        .icon(() -> new ItemStack(StalkerItems.STALKER_MASTER))
                        .displayItems((context, output) -> {
                            // 添加物品前进行安全检查
                            safeAddItem(output, StalkerItems.DRONE_STALKER);
                            safeAddItem(output, StalkerItems.ARROW_STALKER);
                            safeAddItem(output, StalkerItems.VOID_STALKER);
                            safeAddItem(output, StalkerItems.STALKER_MASTER);
                            safeAddItem(output, StalkerItems.STALKER_CORE);
                            safeAddItem(output, StalkerItems.CAMERA_STALKER);
                        })
                        .build()
        );
    }

    private static void safeAddItem(CreativeModeTab.Output output, net.minecraft.world.item.Item item) {
        if (item == null) {
            DiligentStalker.LOGGER.warn("Attempted to add null item to creative tab");
            return;
        }
        
        // 特别检查BlockItem
        if (item instanceof BlockItem blockItem && blockItem.getBlock() == null) {
            DiligentStalker.LOGGER.warn("Attempted to add BlockItem with null block: {}", item);
            return;
        }
        
        output.accept(item);
    }
}
