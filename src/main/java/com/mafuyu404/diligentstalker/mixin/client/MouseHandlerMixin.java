package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.event.client.MouseCallbacks;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void diligentstalker$onPress(long window, int button, int action, int mods, CallbackInfo ci) {
        if (MouseCallbacks.MOUSE_BUTTON_EVENT.invoker().onMouseButton(button, action)) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void diligentstalker$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (MouseCallbacks.MOUSE_SCROLL_EVENT.invoker().onMouseScroll(xOffset, yOffset)) {
            ci.cancel();
        }
    }
}