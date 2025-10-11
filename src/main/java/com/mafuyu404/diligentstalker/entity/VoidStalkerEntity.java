package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.api.HasControllableStorage;
import com.mafuyu404.diligentstalker.api.HasStalkerData;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.api.IStalkerData;
import com.mafuyu404.diligentstalker.data.ControllableStorage;
import com.mafuyu404.diligentstalker.data.StalkerDataComponent;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class VoidStalkerEntity extends ThrowableItemProjectile implements HasControllableStorage, HasStalkerData {
    private final ControllableStorage diligentstalker$storage = new ControllableStorage();
    private final IStalkerData diligentstalker$stalkerData = new StalkerDataComponent();

    public VoidStalkerEntity(EntityType<? extends VoidStalkerEntity> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }
    public VoidStalkerEntity(LivingEntity owner, Level level) {
        super(StalkerEntities.VOID_STALKER, owner, level);
        this.setNoGravity(true);
        owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 1, false, false));
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
        if (this.tickCount >= 40) {
            this.discard();
        }
    }

    protected Item getDefaultItem() {
        return StalkerItems.VOID_STALKER;
    }

    public void handleEntityEvent(byte p_37402_) {

    }

    protected void onHitEntity(EntityHitResult p_37404_) {

    }

    protected void onHit(HitResult p_37406_) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.put("DiligentControllableStorage", diligentstalker$storage.serializeNBT());
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
