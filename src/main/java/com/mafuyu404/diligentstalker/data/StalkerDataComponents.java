package com.mafuyu404.diligentstalker.data;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class StalkerDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DiligentStalker.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> STALKER_ID =
            DATA_COMPONENT_TYPES.register("stalker_id", () ->
                    DataComponentType.<UUID>builder()
                            .persistent(UUIDUtil.CODEC)
                            .networkSynchronized(UUIDUtil.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> STALKER_POSITION =
            DATA_COMPONENT_TYPES.register("stalker_position", () ->
                    DataComponentType.<BlockPos>builder()
                            .persistent(BlockPos.CODEC)
                            .networkSynchronized(BlockPos.STREAM_CODEC)
                            .build()
            );
}
