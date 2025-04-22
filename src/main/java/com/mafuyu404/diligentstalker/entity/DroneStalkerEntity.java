package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import com.mafuyu404.diligentstalker.init.FakePlayerManager;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.network.CameraEntityStatePacket;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DroneStalkerEntity extends Boat implements HasCustomInventoryScreen, ContainerEntity {
    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public DroneStalkerEntity(EntityType<? extends Boat> p_219869_, Level level) {
        super(p_219869_, level);
        this.getPersistentData().putUUID("MasterPlayer", this.uuid);
    }

    public Player getMasterPlayer() {
        return this.level().getPlayerByUUID(this.getPersistentData().getUUID("MasterPlayer"));
    }

    public void setMasterPlayer(Player player) {
        this.getPersistentData().putUUID("MasterPlayer", player.getUUID());
        if (player.isLocalPlayer()) {
            NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(this.getId(), this.getPersistentData()));
        }
    }

    public boolean underControlling() {
        return getMasterPlayer() != null;
    }

    public void disconnect() {
        this.getPersistentData().putUUID("MasterPlayer", this.uuid);
        if (this.level().isClientSide) {
            NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(this.getId(), this.getPersistentData()));
        }
    }

    public DroneStalkerEntity(Level p_219872_, double p_219873_, double p_219874_, double p_219875_) {
        this(EntityType.CHEST_BOAT, p_219872_);
        this.setPos(p_219873_, p_219874_, p_219875_);
        this.xo = p_219873_;
        this.yo = p_219874_;
        this.zo = p_219875_;
    }

    public InteractionResult interact(Player player, InteractionHand p_219899_) {
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

    @Override
    protected void checkFallDamage(double p_38307_, boolean p_38308_, BlockState p_38309_, BlockPos p_38310_) {

    }

    private static final int LOAD_RADIUS = 5;
    private final Set<Long> loadedChunks = new HashSet<>();
    private ChunkPos lastCenter;

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            ChunkPos currentCenter = new ChunkPos(blockPosition());

            // 每2秒（40 tick）检查位置变化
            if (this.tickCount % 40 == 0 || !currentCenter.equals(lastCenter)) {
                updateChunkLoading(serverLevel, currentCenter);
                lastCenter = currentCenter;
            }
        }
        else NetworkHandler.CHANNEL.sendToServer(new CameraEntityStatePacket(this.getId(), this.underControlling()));
        boolean shouldLoad = this.underControlling();
        CompoundTag data = this.getPersistentData();

//        if (shouldLoad) {
//            // 每5 ticks更新一次位置
//            if (this.level() instanceof ServerLevel serverLevel) {
//                ServerPlayer fakePlayer = FakePlayerManager.getOrCreate(serverLevel, this);
//
//                // 同步假人位置（重要！）
//                fakePlayer.setPos(this.getX(), this.getY(), this.getZ());
////                fakePlayer.connection.resetPosition();
//
//                // 强制加载区块
//                int viewDistance = 10;
//                ChunkPos center = new ChunkPos(this.blockPosition());
//                for (int x = -viewDistance; x <= viewDistance; x++) {
//                    for (int z = -viewDistance; z <= viewDistance; z++) {
//                        serverLevel.getChunkSource().addRegionTicket(
//                                TicketType.PLAYER,
//                                new ChunkPos(center.x + x, center.z + z),
//                                10,
//                                center
//                        );
//                    }
//                }
//            }
//        } else {
//            FakePlayerManager.remove(this.uuid);
//            data.remove("chunkLoaderActive");
//        }
    }

    private void updateChunkLoading(ServerLevel level, ChunkPos center) {
        Set<Long> newChunks = new HashSet<>();

        // 生成5x5区块区域
        for (int x = -LOAD_RADIUS; x <= LOAD_RADIUS; x++) {
            for (int z = -LOAD_RADIUS; z <= LOAD_RADIUS; z++) {
                newChunks.add(new ChunkPos(center.x + x, center.z + z).toLong());
            }
        }

        // 释放旧的区块
        Set<Long> toRemove = new HashSet<>(loadedChunks);
        toRemove.removeAll(newChunks);
        toRemove.forEach(chunk ->
                ForgeChunkManager.forceChunk(level, DiligentStalker.MODID, this.getUUID(),
                        ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false, false)
        );

        // 加载新的区块
        newChunks.forEach(chunk ->
                ForgeChunkManager.forceChunk(level, DiligentStalker.MODID, this.getUUID(),
                        ChunkPos.getX(chunk), ChunkPos.getZ(chunk), true, true)
        );

        loadedChunks.clear();
        loadedChunks.addAll(newChunks);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && reason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
        }
        if (!this.level().isClientSide) {
            loadedChunks.forEach(chunk ->
                    ForgeChunkManager.forceChunk((ServerLevel) this.level(), DiligentStalker.MODID,
                            this.getUUID(), ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false, false)
            );
        }
        super.remove(reason);
    }

//    private void releaseAllChunks(ServerLevel level) {
//        for (ChunkPos pos : forcedChunks) {
//            level.setChunkForced(pos.x, pos.z, false);
//        }
//        forcedChunks.clear();
//    }



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
        return ModItems.DRONE_STALKER_ITEM.get();
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
    public AbstractContainerMenu createMenu(int p_219910_, Inventory p_219911_, Player p_219912_) {
        if (this.lootTable != null && p_219912_.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(p_219911_.player);
            return ChestMenu.threeRows(p_219910_, p_219911_, this);
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
