package com.mafuyu404.diligentstalker.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ContainerStorageAdapter implements Storage<ItemVariant> {
    private final Container container;

    private final SnapshotParticipant<ItemStack[]> snapshots = new SnapshotParticipant<>() {
        @Override
        protected ItemStack[] createSnapshot() {
            ItemStack[] copy = new ItemStack[container.getContainerSize()];
            for (int i = 0; i < copy.length; i++) {
                copy[i] = container.getItem(i).copy();
            }
            return copy;
        }

        @Override
        protected void readSnapshot(ItemStack[] snapshot) {
            for (int i = 0; i < snapshot.length; i++) {
                container.setItem(i, snapshot[i].copy());
            }
        }

        @Override
        protected void onFinalCommit() {}
    };

    public ContainerStorageAdapter(Container container) {
        this.container = container;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0) return 0;
        snapshots.updateSnapshots(transaction);
        long inserted = 0;
        int size = container.getContainerSize();

        for (int i = 0; i < size && inserted < maxAmount; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && ItemVariant.of(stack).equals(resource)) {
                int canAdd = Math.min(stack.getMaxStackSize() - stack.getCount(), (int) (maxAmount - inserted));
                if (canAdd > 0) {
                    stack.grow(canAdd);
                    container.setItem(i, stack);
                    inserted += canAdd;
                }
            }
        }

        for (int i = 0; i < size && inserted < maxAmount; i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                int toAdd = (int) Math.min(maxAmount - inserted, resource.getItem().getDefaultMaxStackSize());
                ItemStack newStack = resource.toStack(toAdd);
                container.setItem(i, newStack);
                inserted += toAdd;
            }
        }
        if (inserted > 0) container.setChanged();
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0) return 0;
        snapshots.updateSnapshots(transaction);
        long extracted = 0;
        int size = container.getContainerSize();
        for (int i = 0; i < size && extracted < maxAmount; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && ItemVariant.of(stack).equals(resource)) {
                int canRemove = (int) Math.min(stack.getCount(), maxAmount - extracted);
                if (canRemove > 0) {
                    stack.shrink(canRemove);
                    if (stack.getCount() <= 0) stack = ItemStack.EMPTY;
                    container.setItem(i, stack);
                    extracted += canRemove;
                }
            }
        }
        if (extracted > 0) container.setChanged();
        return extracted;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        int size = container.getContainerSize();
        for (int i = 0; i < size; i++) {
            views.add(new SlotView(i));
        }
        return Collections.unmodifiableList(views).iterator();
    }

    private final class SlotView implements StorageView<ItemVariant> {
        private final int slot;
        SlotView(int slot) { this.slot = slot; }
        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty() || !ItemVariant.of(stack).equals(resource) || maxAmount <= 0) return 0;
            snapshots.updateSnapshots(transaction);
            int canRemove = (int) Math.min(stack.getCount(), maxAmount);
            stack.shrink(canRemove);
            if (stack.getCount() <= 0) stack = ItemStack.EMPTY;
            container.setItem(slot, stack);
            container.setChanged();
            return canRemove;
        }
        @Override
        public boolean isResourceBlank() {
            return container.getItem(slot).isEmpty();
        }
        @Override
        public ItemVariant getResource() {
            ItemStack stack = container.getItem(slot);
            return stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
        }
        @Override
        public long getAmount() {
            return container.getItem(slot).getCount();
        }
        @Override
        public long getCapacity() {
            ItemStack stack = container.getItem(slot);
            return stack.isEmpty() ? resourceMaxStack() : stack.getMaxStackSize();
        }
        private int resourceMaxStack() {
            return 64;
        }
    }
}