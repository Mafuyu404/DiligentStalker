package com.mafuyu404.diligentstalker.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class StalkerDataComponent implements Component, AutoSyncedComponent {
    private final CompoundTag data = new CompoundTag();

    public CompoundTag getStalkerData() {
        return data;
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        for (String key : data.getAllKeys()) {
            data.remove(key);
        }
        data.merge(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        tag.merge(data);
    }
}
