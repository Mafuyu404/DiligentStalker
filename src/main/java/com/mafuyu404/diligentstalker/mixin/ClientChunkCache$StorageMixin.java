package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public class ClientChunkCache$StorageMixin {
    @Inject(method = "inRange", at = @At("HEAD"), cancellable = true)
    private void modifyRange(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        Stalker instance = Stalker.getInstanceOf(player);
        if (instance != null) {
            Entity stalker = instance.getStalker();
            if (ChunkLoadTask.isInRemoteRange(stalker, x, z, 1)) {
                cir.setReturnValue(true);
            }
        } else {
            BlockPos visualCenter = ClientStalkerUtil.getVisualCenter();
            if (visualCenter != null) {
                ChunkPos center = new ChunkPos(visualCenter);
                int radius = StalkerUtil.getFixedCenterRadius(1);
                if (StalkerUtil.isChunkInRadius(center, x, z, radius)) cir.setReturnValue(true);
            }
        }
    }
}
