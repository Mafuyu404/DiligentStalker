package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.api.HasControllableStorage;
import com.mafuyu404.diligentstalker.api.HasStalkerData;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.api.IStalkerData;
import com.mafuyu404.diligentstalker.data.ControllableStorage;
import com.mafuyu404.diligentstalker.data.StalkerDataComponent;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ArrowStalkerEntity extends AbstractArrow implements HasControllableStorage, HasStalkerData {
    private final ControllableStorage diligentstalker$storage = new ControllableStorage();
    private final IStalkerData diligentstalker$stalkerData = new StalkerDataComponent();

    public ArrowStalkerEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }
    public ArrowStalkerEntity(LivingEntity shooter, Level level) {
        super(StalkerEntities.ARROW_STALKER, shooter, level);
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
    protected ItemStack getPickupItem() {
        return new ItemStack(StalkerItems.ARROW_STALKER);
    }

    @Override
    protected void onHit(HitResult raytraceResultIn) {
        super.onHit(raytraceResultIn);
    }

    @Override
    protected void onHitEntity(EntityHitResult raytraceResultIn) {
        this.setDeltaMovement(this.getDeltaMovement().scale(0.3));
        Entity entity = raytraceResultIn.getEntity();
        if (entity.level().isClientSide) {
            if (Stalker.hasInstanceOf(this) && !Stalker.hasInstanceOf(entity) && this.getOwner() instanceof Player player) {
                Stalker.getInstanceOf(this).disconnect();
                Stalker.connect(player, entity);
            }
        }
    }

    @Override
    public void setSecondsOnFire(int seconds) {

    }

    @Override
    public void setCritArrow(boolean critical) {

    }

    @Override
    public void setKnockback(int knockbackStrengthIn) {

    }

    @Override
    public void setPierceLevel(byte level) {

    }

    @Override
    public void tick() {
        super.tick();
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

    @Override
    public boolean isOnFire() {
        return false;
    }
}
