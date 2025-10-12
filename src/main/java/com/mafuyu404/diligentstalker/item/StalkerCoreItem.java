package com.mafuyu404.diligentstalker.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class StalkerCoreItem extends Item {
    public StalkerCoreItem() {
        super(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.diligentstalker.stalker_core.intro").withStyle(ChatFormatting.GOLD));
    }
}
