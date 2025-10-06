package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "getCameraEntity", at = @At("HEAD"), cancellable = true)
    private void modifyCameraEntity(CallbackInfoReturnable<Entity> cir) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            Entity stalker = Stalker.getInstanceOf(player).getStalker();
            // 只有当 stalker 不为 null 时才设置返回值
            if (stalker != null) {
                cir.setReturnValue(stalker);
            }
            // 如果 stalker 为 null，让原方法继续执行，返回默认的相机实体（通常是玩家）
        }
    }
}
