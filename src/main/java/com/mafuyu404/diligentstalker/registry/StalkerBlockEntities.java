package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class StalkerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<CameraStalkerBlockEntity>> CAMERA_STALKER =
            BLOCK_ENTITIES.register("camera_stalker",
                    () -> BlockEntityType.Builder.of(CameraStalkerBlockEntity::new, StalkerBlocks.CAMERA_STALKER.get()).build(null));
}
