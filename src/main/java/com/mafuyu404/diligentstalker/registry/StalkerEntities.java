package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class StalkerEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final Supplier<EntityType<DroneStalkerEntity>> DRONE_STALKER = ENTITIES.register(
            "drone_stalker",
            () -> EntityType.Builder.<DroneStalkerEntity>of(DroneStalkerEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.4f)
                    .build(ResourceLocation.fromNamespaceAndPath(MODID, "drone_stalker").toString())
    );
    public static final Supplier<EntityType<ArrowStalkerEntity>> ARROW_STALKER = ENTITIES.register(
            "arrow_stalker",
            () -> EntityType.Builder.<ArrowStalkerEntity>of(ArrowStalkerEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(ResourceLocation.fromNamespaceAndPath(MODID, "arrow_stalker").toString())
    );
    public static final Supplier<EntityType<VoidStalkerEntity>> VOID_STALKER = ENTITIES.register(
            "void_stalker",
            () -> EntityType.Builder.<VoidStalkerEntity>of(VoidStalkerEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(ResourceLocation.fromNamespaceAndPath(MODID, "void_stalker").toString())
    );
    public static final Supplier<EntityType<CameraStalkerEntity>> CAMERA_STALKER = ENTITIES.register(
            "camera_stalker",
            () -> EntityType.Builder.of(CameraStalkerEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(ResourceLocation.fromNamespaceAndPath(MODID, "camera_stalker").toString())
    );
}
