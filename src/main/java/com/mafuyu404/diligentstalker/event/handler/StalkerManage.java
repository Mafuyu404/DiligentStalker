package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.data.ModLookupApi;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.event.EntityDeathCallback;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.network.ClientStalkerPacket;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StalkerManage {
    public static final HashMap<UUID, Map.Entry<String, BlockPos>> DronePosition = new HashMap<>();

    public static void initServerEvents() {
        ServerTickEvents.START_SERVER_TICK.register(StalkerManage::onServerTick);
        ServerLifecycleEvents.SERVER_STARTING.register(StalkerManage::onServerLaunch);
        ServerWorldEvents.UNLOAD.register((server, level) -> onWorldUnload(level));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> onEntityRemove(entity));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, srv) -> onPlayerLogout(handler.player));
        UseBlockCallback.EVENT.register(StalkerManage::onUseBlock);
        EntityDeathCallback.EVENT.register((entity, source) -> {
            if (Stalker.hasInstanceOf(entity)) {
                Stalker stalkerInstance = Stalker.getInstanceOf(entity);
                if (stalkerInstance != null) {
                    stalkerInstance.disconnect();
                }
            }
        });
    }

    public static void onServerTick(MinecraftServer server) {
        if (server.getTickCount() % 10 == 0) {
            server.getAllLevels().forEach(StalkerManage::onLevelTick);
        }
        server.getPlayerList().getPlayers().forEach(StalkerManage::onPlayerTick);

        if (server.getTickCount() % 6000 == 0) {
            performPeriodicCleanup(server);
        }
        if (server.getTickCount() % 600 == 0) {
            performLightweightCleanup(server);
        }
    }

    private static void onPlayerTick(ServerPlayer player) {
        if (player.tickCount % 20 == 0) syncMasterTag(player);
        if (!Stalker.hasInstanceOf(player)) {
            return;
        }
        Entity stalker = Stalker.getInstanceOf(player).getStalker();
        int timer = 10;
        if (ControllableUtils.isControllable(stalker)) {
            var data = ModLookupApi.STALKER_DATA.find(player, null);
            CompoundTag input = data != null ? data.getData().getCompound(ControllableUtils.CONTROL_INPUT_KEY) : new CompoundTag();
            if (!input.isEmpty()) {
                if (input.contains("xRot")) stalker.setXRot(input.getFloat("xRot"));
                if (input.contains("yRot")) stalker.setYRot(input.getFloat("yRot"));

                Vec3 direction = stalker.position().subtract(player.position());
                int distance = (int) direction.length();
                boolean checkFuel = ControllableUtils.getFuel(stalker) > 0;
                boolean checkSignal = distance < ControllableUtils.getSignalRadius(stalker);

                CompoundTag _input = StalkerUtil.getEmptyInput();
                if ((checkFuel && checkSignal) || player.isCreative()) _input = input;

                stalker.setDeltaMovement(ControllableUtils.tickServerControl(stalker, _input, stalker.getDeltaMovement()));
            }
            if (player.tickCount % timer == 0) {
                ControllableUtils.syncFuel(stalker, ControllableUtils.getFuel(stalker));
//                NetworkHandler.sendToClient(player, new ClientFuelPacket(stalker.getId(), droneStalker.getFuel()));
            }
        }
        if (player.tickCount % timer == 0) {
//            player.serverLevel().getChunkSource().move(player);
            player.teleportRelative(0, 0, 0);
        }
    }

    private static void onLevelTick(ServerLevel level) {
        ChunkLoader chunkLoader = ChunkLoader.of(level);

        chunkLoader.removeAll();

        level.getEntities().getAll().forEach(entity -> {
            if (Stalker.hasInstanceOf(entity)) {
                boolean isControllable = ControllableUtils.isControllable(entity);
                boolean isArrowStalker = entity instanceof ArrowStalkerEntity;
                boolean isVoidStalker = entity instanceof VoidStalkerEntity;
                if (isControllable || isArrowStalker || isVoidStalker) {
                    StalkerUtil.getToLoadChunks(entity, 0).forEach(chunkLoader::addChunk);
                }
            }
        });
        level.players().forEach(player -> {
            if (ServerStalkerUtil.hasVisualCenter(player)) {
                ChunkPos center = new ChunkPos(ServerStalkerUtil.getVisualCenter(player));
                chunkLoader.addChunk(center);
            }
        });
    }

    public static void onServerLaunch(MinecraftServer server) {
        ChunkLoader.init();
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

    public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CameraStalkerBlockEntity be) {
            UUID entityUUID = be.getCameraStalkerUUID();
            if (entityUUID != null && hand == InteractionHand.MAIN_HAND) {
                Entity entity = ((ServerLevel) level).getEntity(entityUUID);
                ItemStack itemStack = player.getMainHandItem();
                if (player.isShiftKeyDown() && itemStack.is(StalkerItems.STALKER_MASTER)) {
                    CompoundTag tag = itemStack.getOrCreateTag();
                    if (!tag.contains("StalkerId") || !tag.getUUID("StalkerId").equals(entityUUID)) {
                        tag.putUUID("StalkerId", entityUUID);
                        tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                        if (player instanceof ServerPlayer sp) {
                            sp.displayClientMessage(Component.translatable("item.diligentstalker.stalker_master.record_success").withStyle(net.minecraft.ChatFormatting.GREEN), true);
                        }
                    }
                } else if (player instanceof ServerPlayer sp) {
                    NetworkHandler.sendToClient(sp, NetworkHandler.CLIENT_STALKER_PACKET, new ClientStalkerPacket(entity.getId()));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static void onPlayerLogout(ServerPlayer player) {
        if (Stalker.hasInstanceOf(player)) {
            Stalker stalkerInstance = Stalker.getInstanceOf(player);
            if (stalkerInstance != null) {
                stalkerInstance.disconnect();
            }
        }
        Stalker.cleanupPlayer(player.getUUID());
    }


    private static void onEntityRemove(Entity entity) {
        if (Stalker.hasInstanceOf(entity)) {
            Stalker stalkerInstance = Stalker.getInstanceOf(entity);
            if (stalkerInstance != null) {
                stalkerInstance.disconnect();
            }
        }
    }

    private static void onWorldUnload(Level level) {
        Stalker.cleanupLevel(level);
    }

    private static void performPeriodicCleanup(MinecraftServer server) {
        int beforeCount = Stalker.getMappingCount();

        server.getAllLevels().forEach(Stalker::cleanupInvalidMappings);

        int afterCount = Stalker.getMappingCount();
        if (beforeCount != afterCount) {
            DiligentStalker.LOGGER.info("Periodic cleanup removed {} invalid stalker mappings",
                    beforeCount - afterCount);
        }
    }

    private static void performLightweightCleanup(MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(player -> {
            if (Stalker.hasInstanceOf(player)) {
                var stalkerInstance = Stalker.getInstanceOf(player);
                if (stalkerInstance != null) {
                    Entity stalker = stalkerInstance.getStalker();
                    if (stalker == null || !stalker.isAlive()) {
                        stalkerInstance.disconnect();
                        var data = ModLookupApi.STALKER_DATA.find(player, null);
                        if (data != null) {
                            data.getData().putBoolean("LoadingCacheChunk", true);
                        }
                    }
                }
            }
        });
    }
}
