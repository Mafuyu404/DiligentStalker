package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.component.StalkerDataComponents;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.network.ClientStalkerPacket;
import com.mafuyu404.diligentstalker.registry.StalkerItems;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = DiligentStalker.MODID)
public class StalkerManage {
    public static final HashMap<UUID, Map.Entry<String, BlockPos>> DronePosition = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {

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
        if (ControllableUtils.isControllable(stalker)) {
            CompoundTag input = (CompoundTag) player.getPersistentData().get(ControllableUtils.CONTROL_INPUT_KEY);
            if (input != null && !input.isEmpty()) {
                if (input.contains("xRot")) stalker.setXRot(input.getFloat("xRot"));
                if (input.contains("yRot")) stalker.setXRot(input.getFloat("yRot"));

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

    @SubscribeEvent
    public static void onServerLaunch(ServerAboutToStartEvent event) {
        ChunkLoader.init();
    }

    @SubscribeEvent
    public static void onUnload(EntityLeaveLevelEvent event) {
        if (Stalker.hasInstanceOf(event.getEntity())) {
            try {
                Stalker.getInstanceOf(event.getEntity()).disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void syncMasterTag(ServerPlayer player) {
        String levelKey = player.serverLevel().dimension().location().toString();

        player.getInventory().items.forEach(itemStack -> {
            if (itemStack.getItem() instanceof StalkerMasterItem) {
                UUID entityUUID = itemStack.get(StalkerDataComponents.STALKER_ID.get());
                if (entityUUID == null) return;

                if (DronePosition.containsKey(entityUUID)) {
                    BlockPos pos = DronePosition.get(entityUUID).getValue();
                    itemStack.set(StalkerDataComponents.STALKER_POSITION.get(), pos);
                } else {
                    BlockPos pos = itemStack.get(StalkerDataComponents.STALKER_POSITION.get());
                    if (pos != null) {
                        DronePosition.put(entityUUID, new AbstractMap.SimpleEntry<>(levelKey, pos));
                    }
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
                if (player.isShiftKeyDown() && itemStack.is(StalkerItems.STALKER_MASTER)) {
                    UUID oldId = itemStack.get(StalkerDataComponents.STALKER_ID.get());

                    if (oldId == null || !oldId.equals(entityUUID)) {
                        itemStack.set(StalkerDataComponents.STALKER_ID.get(), entityUUID);
                        itemStack.set(StalkerDataComponents.STALKER_POSITION.get(), pos);

                        if (player instanceof ServerPlayer sp) {
                            sp.displayClientMessage(
                                    Component.translatable("item.diligentstalker.stalker_master.record_success")
                                            .withStyle(ChatFormatting.GREEN),
                                    true
                            );
                        }
                    }
                } else if (player instanceof ServerPlayer sp && entity != null) {
                    NetworkHandler.sendToClient(sp, new ClientStalkerPacket(entity.getId()));
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
