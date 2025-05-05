package com.mafuyu404.diligentstalker.block;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerEntity;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CameraStalkerBlock extends BaseEntityBlock {

    public CameraStalkerBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            // 生成实体并绑定
            CameraStalkerEntity entity = new CameraStalkerEntity(StalkerEntities.CAMERA_STALKER.get(), level);
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(entity);

            if (level.getBlockEntity(pos) instanceof CameraStalkerBlockEntity be) {
                be.setCameraStalkerUUID(entity.getUUID());
                be.setChanged();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CameraStalkerBlockEntity be) {
            UUID entityId = be.getCameraStalkerUUID();
            if (entityId != null) {
                Entity entity = ((ServerLevel) level).getEntity(entityId);
                if (entity != null) entity.discard();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new CameraStalkerBlockEntity(p_153215_, p_153216_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.ATTACH_FACE);
    }
}
