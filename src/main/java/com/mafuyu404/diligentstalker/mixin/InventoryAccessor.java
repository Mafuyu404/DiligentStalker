package com.mafuyu404.diligentstalker.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Inventory.class)
public interface InventoryAccessor {
    @Accessor("items")
    @Mutable
    void setItems(NonNullList<ItemStack> items);

    @Accessor("items")
    NonNullList<ItemStack> getItems();

    @Accessor("compartments") // 访问 compartments 字段
    @Mutable // 移除 final 修饰符
    void setCompartments(List<NonNullList<ItemStack>> compartments);

    @Accessor("compartments")
    List<NonNullList<ItemStack>> getCompartments();
}