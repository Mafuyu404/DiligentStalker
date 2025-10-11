package com.mafuyu404.diligentstalker.api;

import net.minecraft.nbt.CompoundTag;

public interface IStalkerData {
    CompoundTag getData();

    void merge(CompoundTag tag);

    void readFromNbt(CompoundTag tag);

    void writeToNbt(CompoundTag tag);
}
