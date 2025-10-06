package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public class ClientChunkCache$StorageMixin {
    @Inject(method = "inRange", at = @At("HEAD"), cancellable = true)
    private void modifyRange(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        Stalker instance = Stalker.getInstanceOf(player);
        if (instance != null) {
            ArrayList<ChunkPos> chunkPos = StalkerUtil.getToLoadChunks(instance.getStalker(), 1);
            if (chunkPos.contains(new ChunkPos(x, z))) {
                cir.setReturnValue(true);
            }
        } else {
            if (ClientStalkerUtil.getVisualCenter() != null) {
                if (new ChunkPos(ClientStalkerUtil.getVisualCenter()).equals(new ChunkPos(x, z))) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
