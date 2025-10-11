package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.HasControllableStorage;
import com.mafuyu404.diligentstalker.api.HasStalkerData;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.api.IStalkerData;
import com.mafuyu404.diligentstalker.data.ControllableStorage;
import com.mafuyu404.diligentstalker.data.StalkerDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityStorageMixin implements HasControllableStorage, HasStalkerData {
    @Unique
    private final ControllableStorage diligentstalker$storage = new ControllableStorage();
    @Unique
    private final IStalkerData diligentstalker$stalkerData = new StalkerDataComponent();

    @Override
    public IControllableStorage diligentstalker$getControllableStorage() {
        return diligentstalker$storage;
    }

    @Override
    public IStalkerData diligentstalker$getStalkerData() {
        return diligentstalker$stalkerData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void diligentstalker$writeData(CompoundTag tag, CallbackInfo ci) {
        CompoundTag storageTag;
        storageTag = diligentstalker$storage.serializeNBT();
        tag.put("DiligentControllableStorage", storageTag);

        CompoundTag stalkerTag = new CompoundTag();
        diligentstalker$stalkerData.writeToNbt(stalkerTag);
        tag.put("DiligentStalkerData", stalkerTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void diligentstalker$readData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("DiligentControllableStorage")) {
            diligentstalker$storage.deserializeNBT(tag.getCompound("DiligentControllableStorage"));
        }
        if (tag.contains("DiligentStalkerData")) {
            diligentstalker$stalkerData.readFromNbt(tag.getCompound("DiligentStalkerData"));
        }
    }
}