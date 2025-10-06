package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.block.CameraStalkerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StalkerBlocks {
    public static Block CAMERA_STALKER;

    public static void register() {
        CAMERA_STALKER = Registry.register(
                BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "camera_stalker"),
                new CameraStalkerBlock(BlockBehaviour.Properties.of().noCollission())
        );
    }
}