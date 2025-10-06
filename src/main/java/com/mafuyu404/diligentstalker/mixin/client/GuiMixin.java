package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.event.client.HideExperienceBarEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void diligentstalker$cancelExpBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (HideExperienceBarEvents.RENDER_EXP_BAR.invoker().shouldCancel(client, (Gui) (Object) this)) {
            ci.cancel();
        }
    }
}
