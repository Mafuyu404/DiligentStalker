package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class StalkerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final Supplier<BlockEntityType<CameraStalkerBlockEntity>> CAMERA_STALKER =
            BLOCK_ENTITIES.register("camera_stalker",
                    () -> BlockEntityType.Builder.of(CameraStalkerBlockEntity::new, StalkerBlocks.CAMERA_STALKER.get()).build(null));
}
