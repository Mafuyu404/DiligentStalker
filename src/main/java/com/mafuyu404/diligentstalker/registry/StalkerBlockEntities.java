package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StalkerBlockEntities {
    public static BlockEntityType<CameraStalkerBlockEntity> CAMERA_STALKER;

    public static void register() {
        CAMERA_STALKER = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(DiligentStalker.MODID, "camera_stalker"),
                FabricBlockEntityTypeBuilder.create(CameraStalkerBlockEntity::new, StalkerBlocks.CAMERA_STALKER).build()
        );
    }
}