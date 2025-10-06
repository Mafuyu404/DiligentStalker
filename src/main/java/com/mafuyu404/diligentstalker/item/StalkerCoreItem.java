package com.mafuyu404.diligentstalker.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class StalkerCoreItem extends Item {
    public StalkerCoreItem() {
        super(new Properties());
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> result, TooltipFlag p_41214_) {
        result.add(Component.translatable("item.diligentstalker.stalker_core.intro").withStyle(ChatFormatting.GOLD));
    }
}
