package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.registry.StalkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class CameraStalkerBlockEntity extends BlockEntity {
    public UUID CameraStalkerUUID;

    public CameraStalkerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(StalkerBlockEntities.CAMERA_STALKER.get(), p_155229_, p_155230_);
    }


    public UUID getCameraStalkerUUID() {
        return CameraStalkerUUID;
    }

    public void setCameraStalkerUUID(UUID uuid) {
        CameraStalkerUUID = uuid;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putUUID("CameraStalkerUUID", CameraStalkerUUID);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        CameraStalkerUUID = tag.getUUID("CameraStalkerUUID");
    }
}
