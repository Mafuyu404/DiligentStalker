package com.mafuyu404.diligentstalker.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public interface IControllable {
    void pushAdditionalControl(CompoundTag input);

    Vec3 tickServerControl(CompoundTag input, Vec3 motion);
}
