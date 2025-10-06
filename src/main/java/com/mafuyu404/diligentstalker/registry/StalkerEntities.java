package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class StalkerEntities {
    public static EntityType<DroneStalkerEntity> DRONE_STALKER;
    public static EntityType<ArrowStalkerEntity> ARROW_STALKER;
    public static EntityType<VoidStalkerEntity> VOID_STALKER;
    public static EntityType<CameraStalkerEntity> CAMERA_STALKER;

    public static void register() {
        DRONE_STALKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "drone_stalker"),
                EntityType.Builder.<DroneStalkerEntity>of(DroneStalkerEntity::new, MobCategory.MISC)
                        .sized(0.8f, 0.4f)
                        .build(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "drone_stalker").toString())
        );

        ARROW_STALKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "arrow_stalker"),
                EntityType.Builder.<ArrowStalkerEntity>of(ArrowStalkerEntity::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F)
                        .build(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "arrow_stalker").toString())
        );

        VOID_STALKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "void_stalker"),
                EntityType.Builder.<VoidStalkerEntity>of(VoidStalkerEntity::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F)
                        .build(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "void_stalker").toString())
        );

        CAMERA_STALKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "camera_stalker"),
                EntityType.Builder.of(CameraStalkerEntity::new, MobCategory.MISC)
                        .sized(0.1F, 0.1F)
                        .build(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "camera_stalker").toString())
        );
    }
}