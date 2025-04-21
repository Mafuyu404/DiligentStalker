package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import com.mafuyu404.diligentstalker.init.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class DroneStalkerEntity extends AbstractMinecartContainer {
    private final int containerSize = 27;

    public DroneStalkerEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    protected Item getDropItem() {
        return ModItems.DRONE_ITEM.get();
    }

    public int getContainerSize() {
        return containerSize;
    }

    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
    }

    public int getDefaultDisplayOffset() {
        return 8;
    }

    public AbstractContainerMenu createMenu(int p_38496_, Inventory p_38497_) {
        return ChestMenu.threeRows(p_38496_, p_38497_, this);
    }

    public void stopOpen(Player p_270111_) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(p_270111_));
    }

    public InteractionResult interact(Player player, InteractionHand p_270576_) {
        if (player.isShiftKeyDown()) {
            InteractionResult interactionresult = this.interactWithContainerVehicle(player);
            if (interactionresult.consumesAction()) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinAi.angerNearbyPiglins(player, true);
            }
            return interactionresult;
        }
        else if (player.isLocalPlayer()) {
            CameraEntityManage.launch(this, player);
        }
        return InteractionResult.FAIL;
    }
}
