package com.mafuyu404.diligentstalker.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class StalkerDataComponent implements Component, AutoSyncedComponent {
    private final CompoundTag data = new CompoundTag();

    public CompoundTag getStalkerData() {
        return data;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        for (String key : data.getAllKeys()) {
            data.remove(key);
        }
        data.merge(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.merge(data);
    }
}
