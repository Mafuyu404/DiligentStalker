package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Map;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyVariable(method = "setupRender",at = @At("STORE"), ordinal = 0)
    private double modifyX(double x) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getX();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getX();
            }
        }
        return x;
    }
    @ModifyVariable(method = "setupRender",at = @At("STORE"), ordinal = 1)
    private double modifyY(double y) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getY();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getY();
            }
        }
        return y;
    }
    @ModifyVariable(method = "setupRender",at = @At("STORE"), ordinal = 2)
    private double modifyZ(double z) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().getZ();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getZ();
            }
        }
        return z;
    }
}
