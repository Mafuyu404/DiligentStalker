package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.api.HasControllableStorage;
import com.mafuyu404.diligentstalker.api.HasStalkerData;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.api.IStalkerData;
import com.mafuyu404.diligentstalker.data.ControllableStorage;
import com.mafuyu404.diligentstalker.data.StalkerDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CameraStalkerEntity extends Entity implements HasControllableStorage, HasStalkerData {
    private final ControllableStorage diligentstalker$storage = new ControllableStorage();
    private final IStalkerData diligentstalker$stalkerData = new StalkerDataComponent();

    public CameraStalkerEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.noPhysics = true;
        this.setInvisible(true);
    }

    @Override
    public IControllableStorage diligentstalker$getControllableStorage() {
        return diligentstalker$storage;
    }

    @Override
    public IStalkerData diligentstalker$getStalkerData() {
        return diligentstalker$stalkerData;
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
    public void addAdditionalSaveData(CompoundTag tag) {
        CompoundTag storageTag;
        storageTag = diligentstalker$storage.serializeNBT();
        tag.put("DiligentControllableStorage", storageTag);

        CompoundTag stalkerTag = new CompoundTag();
        diligentstalker$stalkerData.writeToNbt(stalkerTag);
        tag.put("DiligentStalkerData", stalkerTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("DiligentControllableStorage")) {
            diligentstalker$storage.deserializeNBT(tag.getCompound("DiligentControllableStorage"));
        }
        if (tag.contains("DiligentStalkerData")) {
            diligentstalker$stalkerData.readFromNbt(tag.getCompound("DiligentStalkerData"));
        }
    }
}
