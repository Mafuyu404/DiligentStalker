package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.event.handler.ChunkLoadTask;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Final
    @Shadow
    private ChunkBatchSizeCalculator chunkBatchSizeCalculator;

    @Redirect(method = "handleContainerContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V"))
    private void avoidInventoryUpdate(InventoryMenu instance, int i, List<ItemStack> list, ItemStack itemStack) {
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) {
            instance.initializeContents(i, list, itemStack);
        }
    }

    @Inject(method = "handleChunkBatchFinished", at = @At("TAIL"))
    private void ds$onChunkBatchFinished(ClientboundChunkBatchFinishedPacket packet, CallbackInfo ci) {
        float desired = this.chunkBatchSizeCalculator.getDesiredChunksPerTick();
        ChunkLoadTask.setDesiredChunksPerTick(desired);
    }
}
