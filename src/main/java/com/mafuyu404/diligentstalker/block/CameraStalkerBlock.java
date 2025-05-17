package com.mafuyu404.diligentstalker.block;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerEntity;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CameraStalkerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    public static final EnumProperty<AttachFace> FACE = EnumProperty.create("face", AttachFace.class);

    public CameraStalkerBlock(Properties p_49795_) {
        super(p_49795_.strength(1f));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_49727_) {
        return InteractionResult.FAIL;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            CameraStalkerEntity entity = new CameraStalkerEntity(StalkerEntities.CAMERA_STALKER.get(), level);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 0.425, pos.getZ() + 0.5);
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
        builder.add(FACING, FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction playerFacing = context.getHorizontalDirection();

        // 处理潜行时固定方向
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return this.defaultBlockState()
                    .setValue(FACING, playerFacing)
                    .setValue(FACE, getAttachFace(clickedFace));
        }

        // 根据点击面和玩家朝向确定属性
        Direction facing = determineFacing(clickedFace, playerFacing, context.getNearestLookingDirection());
        AttachFace face = getAttachFace(clickedFace);

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(FACE, face);
    }
    private AttachFace getAttachFace(Direction clickedFace) {
        return switch (clickedFace) {
            case UP -> AttachFace.FLOOR;
            case DOWN -> AttachFace.CEILING;
            default -> AttachFace.WALL;
        };
    }

    private Direction determineFacing(Direction clickedFace, Direction playerFacing, Direction nearestLooking) {
        if (clickedFace.getAxis().isVertical()) {
            return playerFacing.getOpposite();
        } else {
            return clickedFace.getOpposite();
        }
    }
}
