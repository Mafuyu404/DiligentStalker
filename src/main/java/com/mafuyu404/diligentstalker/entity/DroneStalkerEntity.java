package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.api.IControllable;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

import static com.mafuyu404.diligentstalker.utils.StalkerUtil.limitSpeed;

public class DroneStalkerEntity extends Boat implements HasCustomInventoryScreen, ContainerEntity, IControllable {
    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    private static final int SIGNAL_RADIUS = 1024;
    private static final int MAX_FUEL = 100;
    private static final int MAX_FUEL_TICK = 720;
    private int fuel_tick = MAX_FUEL_TICK;
    private static final int ITEM_PICKUP_RANGE = 2;
    private int interact_cooldown = 0;

    public DroneStalkerEntity(EntityType<? extends Boat> p_219869_, Level level) {
        super(p_219869_, level);
        ControllableUtils.setMaxFuel(this, MAX_FUEL);
        ControllableUtils.setSignalRadius(this, SIGNAL_RADIUS);
    }

    public DroneStalkerEntity(Level p_219872_, double p_219873_, double p_219874_, double p_219875_) {
        this(EntityType.CHEST_BOAT, p_219872_);
        this.setPos(p_219873_, p_219874_, p_219875_);
        this.xo = p_219873_;
        this.yo = p_219874_;
        this.zo = p_219875_;
    }

    @Override
    public void pushAdditionalControl(CompoundTag input) {

    }

    @Override
    public Vec3 tickServerControl(CompoundTag input, Vec3 motion) {
        float xRot = input.getFloat("xRot");
        float yRot = input.getFloat("yRot");
        float speed = 0.45f;
        Vec3 forward = Vec3.ZERO;
        Vec3 right = Vec3.ZERO;
        Vec3 top = Vec3.ZERO;
        Vec3 result = Vec3.ZERO;
        if (input.getBoolean("Up") || input.getBoolean("Down")) {
            float x = 0;
            float z = 0;
            Vec3 lookAngle = calculateViewVector(xRot, yRot);
            double xz = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
            float forwardX = (float) (lookAngle.x / xz);
            float forwardZ = (float) (lookAngle.z / xz);
            if (input.getBoolean("Up")) {
                x += forwardX * speed;
                z += forwardZ * speed;
            }
            if (input.getBoolean("Down")) {
                x -= forwardX * speed;
                z -= forwardZ * speed;
            }
            forward = limitSpeed(new Vec3(x, 0, z), speed);
        }
        if (input.getBoolean("Left") || input.getBoolean("Right")) {
            float x = 0;
            float z = 0;
            Vec3 subAngle = StalkerUtil.calculateViewVector(xRot, yRot - 90);
            double xz = Math.sqrt(subAngle.x * subAngle.x + subAngle.z * subAngle.z);
            float forwardX = (float) (subAngle.x / xz);
            float forwardZ = (float) (subAngle.z / xz);
            if (input.getBoolean("Left")) {
                x += forwardX * speed;
                z += forwardZ * speed;
            }
            if (input.getBoolean("Right")) {
                x -= forwardX * speed;
                z -= forwardZ * speed;
            }
            right = limitSpeed(new Vec3(x, 0, z), speed);
        }
        if (input.getBoolean("Jump") || input.getBoolean("Shift")) {
            float y = 0;
            if (input.getBoolean("Jump")) {
                y = speed;
            }
            if (input.getBoolean("Shift")) {
                y = -speed;
            }
            top = limitSpeed(new Vec3(0, y, 0), speed);
        }

        result = result.add(forward).add(right).add(top);

        result = new Vec3(
                Mth.lerp(0.3f, motion.x, motion.x + result.x),
                Mth.lerp(0.3f, motion.y, motion.y + result.y),
                Mth.lerp(0.3f, motion.z, motion.z + result.z)
        );

        result = limitSpeed(new Vec3(result.x, 0, result.z), speed).add(limitSpeed(new Vec3(0, result.y, 0), speed));

        result = result.scale(0.8);

        return result;
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (interact_cooldown > 0) return InteractionResult.FAIL;
        interact_cooldown = 20;
        if (player.isShiftKeyDown()) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.is(Items.SUGAR)) {
                if (!level().isClientSide) {
                    int needed = MAX_FUEL - ControllableUtils.getFuel(this);
                    if (needed > 0) {
                        int toAdd = Math.min(itemStack.getCount(), needed);
                        ControllableUtils.setFuel(this, ControllableUtils.getFuel(this) + toAdd);
                        itemStack.shrink(toAdd);
                        player.displayClientMessage(Component.translatable("message.diligentstalker.fuel_added", toAdd).withStyle(ChatFormatting.GREEN), true);
                    } else {
                        player.displayClientMessage(Component.translatable("message.diligentstalker.fuel_full").withStyle(ChatFormatting.RED), true);
                    }
                }
                return InteractionResult.FAIL;
            } else if (itemStack.is(StalkerItems.STALKER_MASTER.get())) {
                CompoundTag tag = itemStack.getOrCreateTag();
                if (!tag.contains("StalkerId") || tag.getUUID("StalkerId") != this.uuid) {
                    tag.putUUID("StalkerId", this.uuid);
                    BlockPos pos = this.blockPosition();
                    tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                    player.displayClientMessage(Component.translatable("message.diligentstalker.record_success").withStyle(ChatFormatting.GREEN), true);
                }
            } else {
                if (!level().isClientSide) {
                    InteractionResult interactionresult = this.interactWithContainerVehicle(player);
                    if (interactionresult.consumesAction()) {
                        this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                    }
                    return interactionresult;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void tick() {
        if (interact_cooldown > 0) interact_cooldown--;

        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!Stalker.hasInstanceOf(this)) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
        } else {
            if (!this.level().isClientSide && this.getDeltaMovement().length() > 0.1) {
                if (fuel_tick <= 0) {
                    ControllableUtils.consumeFuel(this, 1);
                    fuel_tick = MAX_FUEL_TICK;
                } else fuel_tick -= 1;
            }
        }

        if (!this.level().isClientSide && tickCount % 10 == 0) {
            AABB area = this.getBoundingBox().inflate(ITEM_PICKUP_RANGE);
            List<ItemEntity> items = this.level().getEntitiesOfClass(
                    ItemEntity.class,
                    area,
                    e -> e.isAlive() && !e.getItem().isEmpty()
            );
            if (!items.isEmpty()) {
                for (ItemEntity item : items) {
                    ItemStack remaining = ItemHandlerHelper.insertItem(
                            new InvWrapper(this),
                            item.getItem(),
                            false
                    );

                    if (remaining.isEmpty()) {
                        item.discard();
                        this.gameEvent(GameEvent.ENTITY_INTERACT, this);
                    } else if (remaining.getCount() < item.getItem().getCount()) {
                        item.setItem(remaining);
                        this.gameEvent(GameEvent.ENTITY_INTERACT, this);
                    }
                }
            }
        }
    }

    @Override
    protected void checkFallDamage(double p_38307_, boolean p_38308_, BlockState p_38309_, BlockPos p_38310_) {

    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && reason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
            int fuel = ControllableUtils.getFuel(this);
            if (fuel > 0) {
                ItemEntity itementity = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(Items.SUGAR, fuel));
                level().addFreshEntity(itementity);
            }
        }
        super.remove(reason);
    }


    protected float getSinglePassengerXOffset() {
        return 0.15F;
    }

    protected int getMaxPassengers() {
        return 0;
    }

    protected void addAdditionalSaveData(CompoundTag p_219908_) {
        super.addAdditionalSaveData(p_219908_);
        this.addChestVehicleSaveData(p_219908_);
    }

    protected void readAdditionalSaveData(CompoundTag p_219901_) {
        super.readAdditionalSaveData(p_219901_);
        this.readChestVehicleSaveData(p_219901_);
    }

    public void destroy(DamageSource p_219892_) {
        super.destroy(p_219892_);
        this.chestVehicleDestroyed(p_219892_, this.level(), this);
    }

    public void openCustomInventoryScreen(Player p_219906_) {
        p_219906_.openMenu(this);
        if (!p_219906_.level().isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, p_219906_);
            PiglinAi.angerNearbyPiglins(p_219906_, true);
        }

    }

    public Item getDropItem() {
        return StalkerItems.DRONE_STALKER.get();
    }

    public void clearContent() {
        this.clearChestVehicleContent();
    }

    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    public ItemStack getItem(int p_219880_) {
        return this.getChestVehicleItem(p_219880_);
    }

    public ItemStack removeItem(int p_219882_, int p_219883_) {
        return this.removeChestVehicleItem(p_219882_, p_219883_);
    }

    public ItemStack removeItemNoUpdate(int p_219904_) {
        return this.removeChestVehicleItemNoUpdate(p_219904_);
    }

    public void setItem(int p_219885_, ItemStack p_219886_) {
        this.setChestVehicleItem(p_219885_, p_219886_);
    }

    public SlotAccess getSlot(int p_219918_) {
        return this.getChestVehicleSlot(p_219918_);
    }

    public void setChanged() {
    }

    public boolean stillValid(Player p_219896_) {
        return this.isChestVehicleStillValid(p_219896_);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int p_219910_, Inventory inventory, Player player) {
        if (this.lootTable != null && player.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(inventory.player);
            return ChestMenu.threeRows(p_219910_, inventory, this);
        }
    }

    public void unpackLootTable(@Nullable Player p_219914_) {
        this.unpackChestVehicleLootTable(p_219914_);
    }

    @Nullable
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(@Nullable ResourceLocation p_219890_) {
        this.lootTable = p_219890_;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long p_219888_) {
        this.lootTableSeed = p_219888_;
    }

    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    // Forge Start
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && this.isAlive())
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
    }

    public void stopOpen(Player p_270286_) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(p_270286_));
    }
}
