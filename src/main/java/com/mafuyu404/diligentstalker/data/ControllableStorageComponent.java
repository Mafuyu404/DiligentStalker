package com.mafuyu404.diligentstalker.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class ControllableStorageComponent implements Component, AutoSyncedComponent {

    private final ControllableStorage storage = new ControllableStorage();

    @Override
    public void readFromNbt(CompoundTag tag) {
        storage.deserializeNBT(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        CompoundTag inner = storage.serializeNBT();
        tag.merge(inner);
    }

    public ControllableStorage getStorage() {
        return storage;
    }
}
