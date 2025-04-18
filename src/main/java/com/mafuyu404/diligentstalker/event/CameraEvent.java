package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.api.ICamera;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class CameraEvent {
    public static boolean isCameraMode = false;
    public static double cameraX, cameraY, cameraZ;
    private static float yaw, pitch;

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (isCameraMode) {
            ICamera camera = (ICamera) Minecraft.getInstance().gameRenderer.getMainCamera();
            camera.setCameraPosition(cameraX, cameraY, cameraZ);
//            System.out.print("1\n");
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Player player = event.player;
        if (!player.isLocalPlayer()) return;
        if (player.level().isClientSide && Minecraft.getInstance().gameMode.getPlayerMode() == GameType.ADVENTURE) {

            // 初始化摄像机位置
            if (!isCameraMode) {
                cameraX = player.getX();
                cameraY = player.getY();
                cameraZ = player.getZ();
                yaw = player.getYRot();
                pitch = player.getXRot();
                isCameraMode = true;
            }

            ICamera camera = (ICamera) Minecraft.getInstance().gameRenderer.getMainCamera();
//            camera.setCameraPosition(cameraX, cameraY, cameraZ);

            // 应用视角位置
//            player.setYRot(yaw);
//            player.setXRot(pitch);
//            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
//            player.xo = player.getX(); // 阻止位置同步
//            player.yo = player.getY();
//            player.zo = player.getZ();
        } else {
            isCameraMode = false;
        }
    }
    @SubscribeEvent
    public static void onKeydown(InputEvent.Key event) {
        Options options = Minecraft.getInstance().options;
        if (!isCameraMode) return;
//        if (event.getKey() == options.keyUp.getKey().getValue()) options.keyUp.setDown(false);
        Input input = new Input();
        input.up = options.keyUp.isDown();
        input.down = options.keyDown.isDown();
        input.left = options.keyLeft.isDown();
        input.right = options.keyRight.isDown();
        input.jumping = options.keyJump.isDown();
        input.shiftKeyDown = options.keyShift.isDown();
        handleCameraMovement(input);
        options.keyUp.setDown(false);
        options.keyDown.setDown(false);
        options.keyLeft.setDown(false);
        options.keyRight.setDown(false);
        options.keyJump.setDown(false);
        options.keyShift.setDown(false);
    }
//    @SubscribeEvent
//    public static void onMovementInput(MovementInputUpdateEvent event) {
//        Options options = Minecraft.getInstance().options;
//        Player player = event.getEntity();
//        if (!player.level().isClientSide) return;
//
//        if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.ADVENTURE) {
//            Input input = event.getInput();
//            handleCameraMovement(input);
//
//            // 阻止本体移动
//            input.right = false;
//            input.left = false;
//            input.up = false;
//            input.down = false;
//            input.jumping = false;
//            input.shiftKeyDown = false;
//            options.keyUp.setDown(false);
//            options.keyDown.setDown(false);
//            options.keyLeft.setDown(false);
//            options.keyRight.setDown(false);
//            options.keyJump.setDown(false);
//            options.keyShift.setDown(false);
//        }
//    }
    private static void handleCameraMovement(Input input) {
        float speed = 0.5f;
        boolean up = input.jumping;
        boolean down = input.shiftKeyDown;

        int forward = input.up ? 1 : 0;
        int back = input.down ? 1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? 1 : 0;
        int forwardOffset = forward - back;
        int rightOffset = right - left;

        // 计算运动方向
        float yawRad = (float) Math.toRadians(yaw);
        double dx = -Math.sin(yawRad) * forwardOffset + Math.cos(yawRad) * rightOffset;
        double dz = Math.cos(yawRad) * forwardOffset + Math.sin(yawRad) * rightOffset;

        // 更新摄像机位置
        cameraX += dx * speed;
        cameraY += (up ? speed : 0) + (down ? -speed : 0);
        cameraZ += dz * speed;

        // 应用位置到玩家视角
//        Minecraft.getInstance().player.setPos(cameraX, cameraY, cameraZ);
    }
}
