package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.registry.StalkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class CameraStalkerBlockEntity extends BlockEntity {
    public UUID CameraStalkerUUID;

    public CameraStalkerBlockEntity(BlockPos pos, BlockState state) {
        super(StalkerBlockEntities.CAMERA_STALKER, pos, state);
    }


    public UUID getCameraStalkerUUID() {
        return CameraStalkerUUID;
    }

    public void setCameraStalkerUUID(UUID uuid) {
        CameraStalkerUUID = uuid;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID("CameraStalkerUUID", CameraStalkerUUID);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        CameraStalkerUUID = tag.getUUID("CameraStalkerUUID");
    }
}
