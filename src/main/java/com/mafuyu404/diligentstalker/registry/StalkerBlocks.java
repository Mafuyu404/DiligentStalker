package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.block.CameraStalkerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class StalkerBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<Block> CAMERA_STALKER = BLOCKS.register("camera_stalker", () -> new CameraStalkerBlock(BlockBehaviour.Properties.of().noCollission()));
}
