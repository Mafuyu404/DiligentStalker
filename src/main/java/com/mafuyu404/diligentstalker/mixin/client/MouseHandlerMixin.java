package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.event.client.MouseCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow
    private double xpos;
    @Shadow
    private double ypos;

    @Shadow
    public abstract boolean isLeftPressed();

    @Shadow
    public abstract boolean isMiddlePressed();

    @Shadow
    public abstract boolean isRightPressed();

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void diligentstalker$onPress(long window, int button, int action, int mods, CallbackInfo ci) {
        if (MouseCallback.MOUSE_BUTTON_EVENT.invoker().onMouseButton(button, action, xpos, ypos, mods)) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void diligentstalker$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        double offset = (Minecraft.ON_OSX && yOffset == 0) ? xOffset : yOffset;
        if (MouseCallback.MOUSE_SCROLL_EVENT.invoker().onMouseScroll(
                offset, xpos, ypos,
                isLeftPressed(), isMiddlePressed(), isRightPressed()
        )) {
            ci.cancel();
        }
    }
}