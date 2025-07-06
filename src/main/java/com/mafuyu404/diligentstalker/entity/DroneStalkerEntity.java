package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DroneStalkerEntity extends Boat implements HasCustomInventoryScreen, Container {
    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    private int fuel = 0;
    private static final int MAX_FUEL = 100;
    private static final String FUEL_TAG = "DroneFuel";
    private static final int MAX_FUEL_TICK = 720;
    private int fuel_tick = MAX_FUEL_TICK;
    private static final int ITEM_PICKUP_RANGE = 2;
    private int interact_cooldown = 0;

    public DroneStalkerEntity(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public DroneStalkerEntity(Level level, double x, double y, double z) {
        this(EntityType.CHEST_BOAT, level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (interact_cooldown > 0) return InteractionResult.FAIL;
        interact_cooldown = 20;
        if (player.isShiftKeyDown()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(Items.SUGAR)) {
                if (!level().isClientSide) {
                    int needed = MAX_FUEL - this.fuel;
                    if (needed > 0) {
                        int toAdd = Math.min(itemStack.getCount(), needed);
                        setFuel(this.fuel + toAdd);
                        itemStack.shrink(toAdd);
                        player.displayClientMessage(Component.translatable("entity.diligentstalker.drone_stalker.fuel_added", toAdd).withStyle(net.minecraft.ChatFormatting.GREEN), true);
                    } else {
                        player.displayClientMessage(Component.translatable("entity.diligentstalker.drone_stalker.fuel_full").withStyle(net.minecraft.ChatFormatting.RED), true);
                    }
                }
                return InteractionResult.FAIL;
            } else if (itemStack.is(StalkerItems.STALKER_MASTER)) {
                CompoundTag tag = itemStack.getOrCreateTag();
                if (!tag.contains("StalkerId") || tag.getUUID("StalkerId") != this.getUUID()) {
                    tag.putUUID("StalkerId", this.getUUID());
                    BlockPos pos = this.blockPosition();
                    tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                    player.displayClientMessage(Component.translatable("item.diligentstalker.stalker_master.record_success").withStyle(net.minecraft.ChatFormatting.GREEN), true);
                }
            } else {
                if (!level().isClientSide) {
                    // 在Fabric中实现容器交互
                    player.openMenu(new SimpleMenuProvider(
                            (id, inventory, p) -> ChestMenu.threeRows(id, inventory, this),
                            Component.translatable("container.chest")
                    ));
                    this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                    return InteractionResult.SUCCESS;
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
                    consumeFuel(1);
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
                    ItemStack itemStack = item.getItem();
                    // 尝试将物品放入容器
                    boolean added = false;
                    for (int i = 0; i < this.getContainerSize(); i++) {
                        ItemStack slotStack = this.getItem(i);
                        if (slotStack.isEmpty()) {
                            this.setItem(i, itemStack.copy());
                            item.discard();
                            added = true;
                            break;
                        } else if (ItemStack.isSameItemSameTags(slotStack, itemStack) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                            int toAdd = Math.min(itemStack.getCount(), slotStack.getMaxStackSize() - slotStack.getCount());
                            slotStack.grow(toAdd);
                            itemStack.shrink(toAdd);
                            if (itemStack.isEmpty()) {
                                item.discard();
                                added = true;
                                break;
                            }
                        }
                    }
                    if (added) {
                        this.gameEvent(GameEvent.ENTITY_INTERACT, this);
                    }
                }
            }
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // 不处理掉落伤害
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && reason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
            if (fuel > 0) {
                ItemEntity itementity = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(Items.SUGAR, fuel));
                level().addFreshEntity(itementity);
            }
        }
        super.remove(reason);
    }

    public int getFuel() {
        return this.fuel;
    }

    public void setFuel(int amount) {
        this.fuel = Mth.clamp(amount, 0, MAX_FUEL);
    }

    public boolean consumeFuel(int amount) {
        if (this.fuel >= amount) {
            if (Stalker.hasInstanceOf(this)) {
                setFuel(this.fuel - amount);
                return true;
            }
        }
        return false;
    }

    protected float getSinglePassengerXOffset() {
        return 0.15F;
    }

    protected int getMaxPassengers() {
        return 0;
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(FUEL_TAG, this.fuel);
        tag.putInt("FuelTick", this.fuel_tick);

        // 保存容器内容
        if (this.lootTable != null) {
            tag.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                tag.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ContainerHelper.saveAllItems(tag, this.itemStacks);
        }
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.fuel = tag.getInt(FUEL_TAG);
        this.fuel_tick = tag.getInt("FuelTick");

        // 读取容器内容
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (tag.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(tag.getString("LootTable"));
            this.lootTableSeed = tag.getLong("LootTableSeed");
        } else {
            ContainerHelper.loadAllItems(tag, this.itemStacks);
        }
    }

    public void destroy(DamageSource source) {
        super.kill();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(this.level(), this, this);
            if (fuel > 0) {
                this.spawnAtLocation(new ItemStack(Items.SUGAR, fuel));
            }
            this.spawnAtLocation(this.getDropItem());
        }
    }

    public void openCustomInventoryScreen(Player player) {
        player.openMenu(new SimpleMenuProvider(
                (id, inventory, p) -> ChestMenu.threeRows(id, inventory, this),
                Component.translatable("container.chest")
        ));
        if (!player.level().isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            // Fabric中没有直接的PiglinAi.angerNearbyPiglins方法，需要自行实现或使用其他方式
        }
    }

    public Item getDropItem() {
        return StalkerItems.DRONE_STALKER;
    }

    // Container接口实现
    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        this.unpackLootTable(null);
        return this.itemStacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);
        ItemStack itemstack = ContainerHelper.removeItem(this.itemStacks, slot, amount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.unpackLootTable(null);
        ItemStack itemstack = this.itemStacks.get(slot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.unpackLootTable(null);
        this.itemStacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public SlotAccess getSlot(int slot) {
        return slot >= 0 && slot < this.getContainerSize() ?
                new SlotAccess() {
                    public ItemStack get() {
                        return DroneStalkerEntity.this.getItem(slot);
                    }

                    public boolean set(ItemStack stack) {
                        DroneStalkerEntity.this.setItem(slot, stack);
                        return true;
                    }
                } : SlotAccess.NULL;
    }

    @Override
    public void setChanged() {
        // 在Fabric中实现容器变更通知
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    public void unpackLootTable(@Nullable Player player) {
        MinecraftServer minecraftserver = this.level().getServer();
        if (this.getLootTable() != null && minecraftserver != null) {
            LootTable loottable = minecraftserver.getLootData().getLootTable(this.getLootTable());
            if (player != null) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, this.getLootTable());
            }
            this.setLootTable(null);
            LootParams.Builder builder = (new LootParams.Builder((ServerLevel) this.level()))
                    .withParameter(LootContextParams.ORIGIN, this.position());
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            loottable.fill(this, builder.create(LootContextParamSets.CHEST), this.getLootTableSeed());
        }
    }

    @Nullable
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long seed) {
        this.lootTableSeed = seed;
    }

    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    public void stopOpen(Player player) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(player));
    }
}