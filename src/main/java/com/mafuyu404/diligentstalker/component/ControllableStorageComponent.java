package com.mafuyu404.diligentstalker.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ControllableStorageComponent implements Component, AutoSyncedComponent {

    private final ControllableStorage storage = new ControllableStorage();

    public ControllableStorage getStorage() {
        return storage;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
        storage.deserializeNBT(compoundTag);
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag inner = storage.serializeNBT();
        compoundTag.merge(inner);
    }
}
