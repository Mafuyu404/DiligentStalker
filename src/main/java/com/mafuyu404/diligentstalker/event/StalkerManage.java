package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.api.PersistentDataHolder;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.network.ClientFuelPacket;
import com.mafuyu404.diligentstalker.network.ClientStalkerPacket;
import com.mafuyu404.diligentstalker.registry.ModConfig;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import java.util.*;

public class StalkerManage {
    public static final HashMap<UUID, Map.Entry<String, BlockPos>> DronePosition = new HashMap<>();
    private static int SIGNAL_RADIUS = 0;
    private static HashMap<UUID, ArrayList<ArrayList<ChunkPos>>> LoadingChunks = new HashMap<>();

    public static void init() {
        // 注册服务器Tick事件
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 == 0) {
                server.getAllLevels().forEach(StalkerManage::onLevelTick);
            }
            server.getPlayerList().getPlayers().forEach(StalkerManage::onPlayerTick);
        });

        // 注册实体离开世界事件
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (Stalker.hasInstanceOf(entity)) {
                Stalker.getInstanceOf(entity).disconnect();
            }
        });

        // 注册方块交互事件
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            if (!world.isClientSide() && world.getBlockEntity(pos) instanceof CameraStalkerBlockEntity be) {
                UUID entityUUID = be.getCameraStalkerUUID();
                if (entityUUID != null && hand == InteractionHand.MAIN_HAND) {
                    Entity entity = ((ServerLevel) world).getEntity(entityUUID);
                    ItemStack itemStack = player.getMainHandItem();
                    if (player.isShiftKeyDown() && itemStack.is(StalkerItems.STALKER_MASTER)) {
                        CompoundTag tag = itemStack.getOrCreateTag();
                        if (!tag.contains("StalkerId") || tag.getUUID("StalkerId") != entityUUID) {
                            tag.putUUID("StalkerId", entityUUID);
                            tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                            player.displayClientMessage(Component.translatable("item.diligentstalker.stalker_master.record_success").withStyle(ChatFormatting.GREEN), true);
                        }
                    }
                    else {
                        NetworkHandler.sendToClient((ServerPlayer) player, new ClientStalkerPacket(entity.getId()));
                    }
                }
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
    }

    private static void onPlayerTick(ServerPlayer player) {
        if (player.tickCount % 20 == 0) syncMasterTag(player);
        if (!Stalker.hasInstanceOf(player)) return;
        Entity stalker = Stalker.getInstanceOf(player).getStalker();
        int timer = 10;
        if (stalker instanceof DroneStalkerEntity droneStalker) {
            PersistentDataHolder holder = (PersistentDataHolder) player;
            CompoundTag input = (CompoundTag) holder.getPersistentData().get("DroneStalkerInput");
            Vec3 direction = droneStalker.position().subtract(player.position());
            int distance = (int) direction.length();
            if (SIGNAL_RADIUS == 0) SIGNAL_RADIUS = ModConfig.get().droneSetting.signalRadius;
            if (input != null) {
                float xRot = input.getFloat("xRot");
                float yRot = input.getFloat("yRot");
                stalker.setXRot(xRot);
                stalker.setYRot(yRot);
                if ((droneStalker.getFuel() > 0 && distance < SIGNAL_RADIUS) || player.isCreative()) {
                    stalker.setDeltaMovement(Tools.move(input, stalker.getDeltaMovement()));
                } else {
                    stalker.setDeltaMovement(Tools.move(Tools.getEmptyInput(), stalker.getDeltaMovement()));
                }
            }
            if (player.tickCount % timer == 0) {
                NetworkHandler.sendToClient(player, new ClientFuelPacket(stalker.getId(), droneStalker.getFuel()));
            }
        }
        if (player.tickCount % timer == 0) {
//            player.serverLevel().getChunkSource().move(player);
            player.teleportRelative(0, 0, 0);
        }
    }

    private static void onLevelTick(ServerLevel level) {
        ChunkLoader.removeAll(level);
        level.getEntities().getAll().forEach(entity -> {
            if (Stalker.hasInstanceOf(entity)) {
                boolean isDroneStalker = entity instanceof DroneStalkerEntity;
                boolean isArrowStalker = entity instanceof ArrowStalkerEntity;
                boolean isVoidStalker = entity instanceof VoidStalkerEntity;
                if (isDroneStalker || isArrowStalker || isVoidStalker) {
                    Tools.getToLoadChunks(entity, 0).forEach(chunkPos -> {
                        ChunkLoader.add(level, chunkPos);
                    });
                }
            }
        });
        level.players().forEach(player -> {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                ChunkLoader.add(level, new ChunkPos(entry.getValue()));
            }
        });
    }

    private static void syncMasterTag(ServerPlayer player) {
        String levelKey = player.level().dimension().toString();
        player.getInventory().items.forEach(itemStack -> {
            if (itemStack.getItem() instanceof StalkerMasterItem) {
                CompoundTag tag = itemStack.getOrCreateTag();
                if (!tag.contains("StalkerId")) return;
                UUID entityUUID = tag.getUUID("StalkerId");
                if (DronePosition.containsKey(entityUUID)) {
                    BlockPos pos = DronePosition.get(entityUUID).getValue();
                    tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                } else if (tag.contains("StalkerPosition")) {
                    int[] pos = tag.getIntArray("StalkerPosition");
                    DronePosition.put(entityUUID, new Map.Entry<>() {
                        @Override
                        public String getKey() {
                            return levelKey;
                        }
                        @Override
                        public BlockPos getValue() {
                            return new BlockPos(pos[0], pos[1], pos[2]);
                        }
                        @Override
                        public BlockPos setValue(BlockPos value) {
                            return null;
                        }
                    });
                }
            }
        });
    }
}