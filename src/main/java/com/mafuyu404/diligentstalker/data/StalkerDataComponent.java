package com.mafuyu404.diligentstalker.data;

import com.mafuyu404.diligentstalker.api.IStalkerData;
import net.minecraft.nbt.CompoundTag;

public class StalkerDataComponent implements IStalkerData {
    private final CompoundTag data = new CompoundTag();

    @Override
    public CompoundTag getData() {
        return data;
    }

    @Override
    public void merge(CompoundTag tag) {
        data.merge(tag);
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
