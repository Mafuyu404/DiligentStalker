package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.api.IControllable;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerCoreItem;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

import static com.mafuyu404.diligentstalker.utils.ClientStalkerUtil.getCameraPosition;

@EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class StalkerControl {
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;
    public static boolean screen = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        screen = Minecraft.getInstance().screen != null;
        if (screen) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientLevel level = player.clientLevel;
        for (Entity entity : level.entitiesForRendering()) {
            if (ClientStalkerUtil.matchConnectingTarget(entity)) {
                Stalker.connect(player, entity);
                ClientStalkerUtil.setConnectingTarget(null);
                break;
            }
        }

        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker != null) {
            ClientStalkerUtil.setVisualCenter(null);

            if (ControllableUtils.isControllable(stalker)) {
                if (ControllableUtils.isCameraControlling(stalker)) {
                    stalker.setXRot(xRot);
                    stalker.setYRot(yRot);
                }
                StalkerControl.syncControl();
            }
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide().isClient()) {
            Player player = event.getEntity();
            if (player.isShiftKeyDown()) return;
            if (event.getItemStack().getItem() instanceof StalkerCoreItem) {
                Stalker.connect(player, event.getTarget());
                event.setCanceled(true);
            } else if (event.getTarget() instanceof DroneStalkerEntity stalker) {
                Stalker.connect(player, stalker);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onControl(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker == null) return;
        updateControlMap();
        if (event.getAction() == InputConstants.PRESS) {
            if (event.getKey() == KeyBindings.DISCONNECT.getKey().getValue()) {
                if (Stalker.hasInstanceOf(player)) Stalker.getInstanceOf(player).disconnect();
            }
            if (event.getKey() == KeyBindings.VIEW.getKey().getValue()) {
                ControllableUtils.switchCameraState(stalker);
            }
            if (event.getKey() == KeyBindings.CONTROL.getKey().getValue()) {
                ControllableUtils.turnActionControlling(stalker);
            }
        }
        if (StalkerUtil.ControlMap.containsValue(event.getKey())) {
            syncControl();
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide().isClient() && Stalker.hasInstanceOf(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getSide().isClient() && Stalker.hasInstanceOf(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAction(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            DroneStalkerHUD.RPress = event.getAction() == InputConstants.PRESS;
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            DroneStalkerHUD.LPress = event.getAction() == InputConstants.PRESS;
        }
        if (event.getAction() != InputConstants.PRESS) return;
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            event.setCanceled(true);
            return;
        }
        Stalker instance = Stalker.getInstanceOf(player);
        if (ControllableUtils.isControllable(instance.getStalker())) {
            Vec3 viewVector = StalkerUtil.calculateViewVector(xRot, yRot);
            BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(player.level(), player, getCameraPosition(), viewVector, 4);
            if (traceResult.getType() == HitResult.Type.BLOCK) {
                NetworkHandler.sendToServer(new RClickBlockPacket(getCameraPosition(), viewVector));
                RightClickBlock(player, getCameraPosition(), viewVector);
            }
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;
        event.setCanceled(true);
    }

    public static CompoundTag handleInput() {
        Options options = Minecraft.getInstance().options;
        CompoundTag input = new CompoundTag();
        StalkerUtil.ControlMap.forEach((s, key) -> {
            input.putBoolean(s, ClientStalkerUtil.isKeyPressed(key));
        });
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker != null && ControllableUtils.isCameraControlling(stalker)) {
            input.putFloat("xRot", xRot);
            input.putFloat("yRot", yRot);
        }
        return input;
    }

    public static void syncControl() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        updateControlMap();
        CompoundTag input = StalkerControl.handleInput();
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (ControllableUtils.isControllable(stalker)) {
            ((IControllable) stalker).pushAdditionalControl(input);
            if (!ControllableUtils.isActionControlling(stalker)) input = new CompoundTag();
        }
        player.getPersistentData().put(ControllableUtils.CONTROL_INPUT_KEY, input);
        NetworkHandler.sendToServer(new EntityDataPacket(player.getId(), player.getPersistentData()));
    }

    public static void RightClickBlock(Player player, Vec3 position, Vec3 viewVec) {
        player.displayClientMessage(Component.translatable("message.diligentstalker.under_maintaining").withStyle(ChatFormatting.RED), true);
//        Level level = player.level();
//        BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(level, position, viewVec, 4);
//        BlockState state = level.getBlockState(traceResult.getBlockPos());
//        InteractionResult result = state.use(level, player, InteractionHand.MAIN_HAND, traceResult);
//        if (result.consumesAction()) {
//            level.sendBlockUpdated(traceResult.getBlockPos(), state, state, 3);
//        }
    }

    public static void connect(Player player, Entity stalker) {
        if (!player.isLocalPlayer()) return;
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
        if (ControllableUtils.isControllable(stalker)) {
            xRot = stalker.getXRot();
            yRot = stalker.getYRot();
            ControllableUtils.setCameraControlling(stalker);
        }
    }

    @SubscribeEvent
    public static void onClientEnter(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ClientStalkerUtil.matchConnectingTarget(event.getEntity())) {
            Stalker.connect(player, event.getEntity());
        }

        if (event.getEntity() instanceof ArrowStalkerEntity stalker) {
            if (stalker.getOwner() != null && stalker.getOwner().getUUID().equals(player.getUUID())) {
                if (Stalker.hasInstanceOf(player)) return;
                Stalker.connect(player, stalker);
            }
        }
        if (event.getEntity() instanceof VoidStalkerEntity stalker) {
            if (stalker.getOwner() != null && stalker.getOwner().getUUID().equals(player.getUUID())) {
                if (Stalker.hasInstanceOf(player)) return;
                Stalker.connect(player, stalker);
            }
        }
    }

    private static void updateControlMap() {
        Options options = Minecraft.getInstance().options;
        StalkerUtil.ControlMap.put("Up", options.keyUp.getKey().getValue());
        StalkerUtil.ControlMap.put("Down", options.keyDown.getKey().getValue());
        StalkerUtil.ControlMap.put("Left", options.keyLeft.getKey().getValue());
        StalkerUtil.ControlMap.put("Right", options.keyRight.getKey().getValue());
        StalkerUtil.ControlMap.put("Jump", options.keyJump.getKey().getValue());
        StalkerUtil.ControlMap.put("Shift", options.keyShift.getKey().getValue());
    }
}
