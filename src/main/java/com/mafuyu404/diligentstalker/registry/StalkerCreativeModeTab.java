package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class StalkerCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DiligentStalker.MODID);

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.diligentstalker"))
            .icon(() -> new ItemStack(StalkerItems.STALKER_MASTER.get()))
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
