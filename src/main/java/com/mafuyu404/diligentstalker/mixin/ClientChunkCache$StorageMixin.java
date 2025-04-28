package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Map;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public class ClientChunkCache$StorageMixin {
    @Inject(method = "inRange", at = @At("HEAD"), cancellable = true)
    private void modifyRange(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        Stalker instance = Stalker.getInstanceOf(player);
        if (instance != null) {
            ArrayList<ChunkPos> chunkPos = Tools.getToLoadChunk(instance.getStalker(), 1);
            if (chunkPos.contains(new ChunkPos(x, z))) {
                cir.setReturnValue(true);
            }
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                if (new ChunkPos(entry.getValue()).equals(new ChunkPos(x, z))) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
