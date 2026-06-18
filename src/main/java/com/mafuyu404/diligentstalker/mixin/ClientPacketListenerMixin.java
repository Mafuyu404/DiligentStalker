package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleLevelChunkWithLight", at = @At("HEAD"), cancellable = true)
    private void queueRemoteChunk(ClientboundLevelChunkWithLightPacket packet, CallbackInfo ci) {
        if (!ChunkLoadTask.isDraining() && ClientStalkerUtil.queueRemoteChunkPacket(packet)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleSetChunkCacheCenter", at = @At("HEAD"), cancellable = true)
    private void keepRemoteChunkCenter(ClientboundSetChunkCacheCenterPacket packet, CallbackInfo ci) {
        if (ClientStalkerUtil.hasRemoteChunkCenter()) {
            ClientStalkerUtil.applyRemoteChunkCenter();
            ci.cancel();
        }
    }

    @Redirect(method = "handleContainerContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V"))
    private void avoidInventoryUpdate(InventoryMenu instance, int i, List<ItemStack> list, ItemStack itemStack) {
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) {
            instance.initializeContents(i, list, itemStack);
        }
    }
}
