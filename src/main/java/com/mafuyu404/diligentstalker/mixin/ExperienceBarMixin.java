package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class ExperienceBarMixin {
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void onRenderExperienceBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (DiligentStalker.HIDE_EXP_BAR) {
            ci.cancel();
        }
    }
}