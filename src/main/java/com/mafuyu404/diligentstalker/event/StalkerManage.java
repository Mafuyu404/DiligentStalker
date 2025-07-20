package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.network.ClientFuelPacket;
import com.mafuyu404.diligentstalker.network.ClientStalkerPacket;
import com.mafuyu404.diligentstalker.registry.Config;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID)
public class StalkerManage {
    public static final HashMap<UUID, Map.Entry<String, BlockPos>> DronePosition = new HashMap<>();
    private static int SIGNAL_RADIUS = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;

        if (event.getServer().getTickCount() % 10 == 0) {
            event.getServer().getAllLevels().forEach(StalkerManage::onLevelTick);
        }
        event.getServer().getPlayerList().getPlayers().forEach(StalkerManage::onPlayerTick);

        if (event.getServer().getTickCount() % 6000 == 0) {
            performPeriodicCleanup(event.getServer());
        }

        if (event.getServer().getTickCount() % 600 == 0) {
            performLightweightCleanup(event.getServer());
        }
    }

    private static void onPlayerTick(ServerPlayer player) {
        if (player.tickCount % 20 == 0) syncMasterTag(player);
        if (!Stalker.hasInstanceOf(player)) {
            return;
        }
        Entity stalker = Stalker.getInstanceOf(player).getStalker();
        int timer = 10;
        if (stalker instanceof DroneStalkerEntity droneStalker) {
            CompoundTag input = (CompoundTag) player.getPersistentData().get("DroneStalkerInput");
            Vec3 direction = droneStalker.position().subtract(player.position());
            int distance = (int) direction.length();
            if (SIGNAL_RADIUS == 0) SIGNAL_RADIUS = Config.SIGNAL_RADIUS.get();
            if (input != null) {
                float xRot = input.getFloat("xRot");
                float yRot = input.getFloat("yRot");
                stalker.setXRot(xRot);
                stalker.setYRot(yRot);
                if ((droneStalker.getFuel() > 0 && distance < SIGNAL_RADIUS) || player.isCreative()) {
                    stalker.setDeltaMovement(StalkerUtil.move(input, stalker.getDeltaMovement()));
                } else {
                    stalker.setDeltaMovement(StalkerUtil.move(StalkerUtil.getEmptyInput(), stalker.getDeltaMovement()));
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
        ChunkLoader chunkLoader = ChunkLoader.of(level);

        chunkLoader.removeAll();

        level.getEntities().getAll().forEach(entity -> {
            if (Stalker.hasInstanceOf(entity)) {
                boolean isDroneStalker = entity instanceof DroneStalkerEntity;
                boolean isArrowStalker = entity instanceof ArrowStalkerEntity;
                boolean isVoidStalker = entity instanceof VoidStalkerEntity;
                if (isDroneStalker || isArrowStalker || isVoidStalker) {
                    StalkerUtil.getToLoadChunks(entity, 0).forEach(chunkLoader::addChunk);
                }
            }
        });
        level.players().forEach(player -> {
            if (ServerStalkerUtil.hasVisualCenter(player)) {
                chunkLoader.addChunk(new ChunkPos(ServerStalkerUtil.getVisualCenter(player)));
            }
        });
    }

    @SubscribeEvent
    public static void onUnload(EntityLeaveLevelEvent event) {
        if (Stalker.hasInstanceOf(event.getEntity())) {
            Stalker.getInstanceOf(event.getEntity()).disconnect();
        }
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

    @SubscribeEvent
    public static void useBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getHitVec().getBlockPos();
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CameraStalkerBlockEntity be) {
            UUID entityUUID = be.getCameraStalkerUUID();
            if (entityUUID != null && event.getHand() == InteractionHand.MAIN_HAND) {
                Entity entity = ((ServerLevel) level).getEntity(entityUUID);
                ItemStack itemStack = player.getMainHandItem();
                if (player.isShiftKeyDown() && itemStack.is(StalkerItems.STALKER_MASTER.get())) {
                    CompoundTag tag = itemStack.getOrCreateTag();
                    if (!tag.contains("StalkerId") || tag.getUUID("StalkerId") != entityUUID) {
                        tag.putUUID("StalkerId", entityUUID);
                        tag.putIntArray("StalkerPosition", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                        player.displayClientMessage(Component.translatable("item.diligentstalker.stalker_master.record_success").withStyle(ChatFormatting.GREEN), true);
                    }
                } else {
                    NetworkHandler.sendToClient((ServerPlayer) player, new ClientStalkerPacket(entity.getId()));
                }
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (Stalker.hasInstanceOf(player)) {
            Stalker stalkerInstance = Stalker.getInstanceOf(player);
            if (stalkerInstance != null) {
                stalkerInstance.disconnect();
            }
        }
        Stalker.cleanupPlayer(player.getUUID());
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (Stalker.hasInstanceOf(entity)) {
            Stalker stalkerInstance = Stalker.getInstanceOf(entity);
            if (stalkerInstance != null) {
                stalkerInstance.disconnect();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityRemove(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (Stalker.hasInstanceOf(entity)) {
            if (entity instanceof Player) {
                Stalker.cleanupPlayer(entity.getUUID());
            } else {
                Stalker.cleanupStalker(entity.getId());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            Stalker.cleanupLevel(level);
        }
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
                Stalker stalkerInstance = Stalker.getInstanceOf(player);
                if (stalkerInstance != null) {
                    Entity stalker = stalkerInstance.getStalker();
                    if (stalker == null || !stalker.isAlive()) {
                        stalkerInstance.disconnect();
                    }
                }
            }
        });
    }
}
