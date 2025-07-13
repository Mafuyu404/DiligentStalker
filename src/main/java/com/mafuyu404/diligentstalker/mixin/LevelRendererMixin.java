package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyVariable(method = "setupRender", at = @At("STORE"), ordinal = 0)
    private double modifyX(double x) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getX();
        } else {
            if (StalkerControl.visualCenter != null) return StalkerControl.visualCenter.getX();
        }
        return x;
    }

    @ModifyVariable(method = "setupRender", at = @At("STORE"), ordinal = 1)
    private double modifyY(double y) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getY();
        } else {
            if (StalkerControl.visualCenter != null) return StalkerControl.visualCenter.getY();
        }
        return y;
    }

    @ModifyVariable(method = "setupRender", at = @At("STORE"), ordinal = 2)
    private double modifyZ(double z) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getZ();
        } else {
            if (StalkerControl.visualCenter != null) return StalkerControl.visualCenter.getZ();
        }
        return z;
    }
}
