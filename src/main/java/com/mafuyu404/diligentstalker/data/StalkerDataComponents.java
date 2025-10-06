package com.mafuyu404.diligentstalker.data;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class StalkerDataComponents {
    public static DataComponentType<UUID> STALKER_ID;
    public static DataComponentType<BlockPos> STALKER_POSITION;

    public static void register() {
        STALKER_ID = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "stalker_id"),
                DataComponentType.<UUID>builder()
                        .persistent(UUIDUtil.CODEC)
                        .networkSynchronized(UUIDUtil.STREAM_CODEC)
                        .build()
        );
        STALKER_POSITION = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "stalker_position"),
                DataComponentType.<BlockPos>builder()
                        .persistent(BlockPos.CODEC)
                        .networkSynchronized(BlockPos.STREAM_CODEC)
                        .build()
        );
    }
}
