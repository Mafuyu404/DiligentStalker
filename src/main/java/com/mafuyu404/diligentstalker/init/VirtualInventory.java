package com.mafuyu404.diligentstalker.init;

import com.google.common.collect.ImmutableList;
import com.mafuyu404.diligentstalker.mixin.InventoryAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VirtualInventory extends Inventory {
    public int size;
    public int playerInventorySize;

    public VirtualInventory(int size, Player player) {
        super(Objects.requireNonNull(player));
        ((InventoryAccessor) this).setItems(NonNullList.withSize(size, ItemStack.EMPTY));
        ((InventoryAccessor) this).setCompartments(ImmutableList.of(this.items, this.armor, this.offhand));
        this.size = size;
    }

    public ItemHandler getHandler() {
        return new ItemHandler(this);
    }

    public static class ItemHandler implements IItemHandler {

        private final VirtualInventory virtualInventory;

        public ItemHandler(VirtualInventory virtualInventory) {
            this.virtualInventory = virtualInventory;
        }

        @Override
        public int getSlots() {
            return virtualInventory.size;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return virtualInventory.getItem(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return null;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack itemStack = virtualInventory.getItem(slot);
            ItemStack result = itemStack.copy();
            itemStack.setCount(itemStack.getCount() - amount);
            result.setCount(amount);
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return virtualInventory.size;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    }
}
