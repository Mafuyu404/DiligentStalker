package com.mafuyu404.diligentstalker.component;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<ControllableStorageComponent> CONTROLLABLE_STORAGE =
            ComponentRegistry.getOrCreate(
                    ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "controllable_storage"),
                    ControllableStorageComponent.class
            );

    public static final ComponentKey<StalkerDataComponent> STALKER_DATA =
            ComponentRegistry.getOrCreate(
                    ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "stalker_data"),
                    StalkerDataComponent.class
            );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, CONTROLLABLE_STORAGE, e -> new ControllableStorageComponent());

        registry.registerFor(Entity.class, STALKER_DATA, e -> new StalkerDataComponent());
    }
}