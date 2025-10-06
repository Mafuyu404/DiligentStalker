package com.mafuyu404.diligentstalker.data;

import com.mafuyu404.diligentstalker.DiligentStalker;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<ControllableStorageComponent> CONTROLLABLE_STORAGE =
            ComponentRegistry.getOrCreate(
                    new ResourceLocation(DiligentStalker.MODID, "controllable_storage"),
                    ControllableStorageComponent.class
            );

    public static final ComponentKey<StalkerDataComponent> STALKER_DATA =
            ComponentRegistry.getOrCreate(
                    new ResourceLocation(DiligentStalker.MODID, "stalker_data"),
                    StalkerDataComponent.class
            );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, CONTROLLABLE_STORAGE, e -> new ControllableStorageComponent());

        registry.registerFor(Entity.class, STALKER_DATA, e -> new StalkerDataComponent());
    }
}