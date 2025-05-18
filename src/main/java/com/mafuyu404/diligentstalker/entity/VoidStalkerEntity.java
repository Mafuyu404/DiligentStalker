package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class VoidStalkerEntity extends ThrowableItemProjectile {
    public VoidStalkerEntity(EntityType<? extends VoidStalkerEntity> type, Level level) {
        super(type, level);
    }

    public VoidStalkerEntity(LivingEntity owner, Level level) {
        super(StalkerEntities.VOID_STALKER, owner, level);
        this.setNoGravity(true);
        owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 1, false, false));
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

    public void handleEntityEvent(byte b) {

    }

    protected void onHitEntity(EntityHitResult entityHitResult) {

    }

    protected void onHit(HitResult hitResult) {

    }
}
