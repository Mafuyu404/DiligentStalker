package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.block.CameraStalkerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class StalkerBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> CAMERA_STALKER = BLOCKS.register("camera_stalker", () -> new CameraStalkerBlock(BlockBehaviour.Properties.of().noCollission()));
}
