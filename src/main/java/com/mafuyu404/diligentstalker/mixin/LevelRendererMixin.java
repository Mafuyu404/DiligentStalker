package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Map;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Redirect(method = "setupRender",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"))
    private double modifyX(LocalPlayer player) {
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getX();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getX();
            }
        }
        return player.getX();
    }
    @Redirect(method = "setupRender",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"))
    private double modifyY(LocalPlayer player) {
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getY();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getY();
            }
        }
        return player.getY();
    }
    @Redirect(method = "setupRender",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"))
    private double modifyZ(LocalPlayer player) {
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getZ();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getZ();
            }
        }
        return player.getZ();
    }
}
