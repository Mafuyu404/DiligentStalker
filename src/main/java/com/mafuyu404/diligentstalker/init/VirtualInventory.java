package com.mafuyu404.diligentstalker.init;

import com.google.common.collect.ImmutableList;
import com.mafuyu404.diligentstalker.mixin.InventoryAccessor;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class VirtualInventory extends Inventory {
    public int size;

    public VirtualInventory(int size, Player player) {
        super(Objects.requireNonNull(player));
        ((InventoryAccessor) this).setItems(NonNullList.withSize(size, ItemStack.EMPTY));
        ((InventoryAccessor) this).setCompartments(ImmutableList.of(this.items, this.armor, this.offhand));
        this.size = size;
    }

    public Storage<ItemVariant> getStorage() {
        return InventoryStorage.of(this, null);
    }
}