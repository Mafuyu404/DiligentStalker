package com.mafuyu404.diligentstalker.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface EntityDeathCallback {
    Event<EntityDeathCallback> EVENT = EventFactory.createArrayBacked(EntityDeathCallback.class, listeners -> (entity, source) -> {
        for (EntityDeathCallback l : listeners) l.onDeath(entity, source);
    });

    void onDeath(LivingEntity entity, DamageSource source);
}