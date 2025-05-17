package com.mafuyu404.diligentstalker.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CameraStalkerEntity extends Entity {
    public CameraStalkerEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.noPhysics = true;
        this.setInvisible(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (!(level().getBlockEntity(blockPosition()) instanceof CameraStalkerBlockEntity)) {
                discard();
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
