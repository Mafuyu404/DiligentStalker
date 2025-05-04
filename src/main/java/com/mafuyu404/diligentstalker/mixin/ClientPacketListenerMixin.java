package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Redirect(method = "handleContainerContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V"))
    private void avoidInventoryUpdate(InventoryMenu instance, int i, List<ItemStack> list, ItemStack itemStack) {
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) {
            instance.initializeContents(i, list, itemStack);
        }
    }
//    @Inject(method = "handleLevelChunkWithLight", at = @At("HEAD"), cancellable = true)
//    private void aaq(ClientboundLevelChunkWithLightPacket packet, CallbackInfo ci) {
//        Player player = Minecraft.getInstance().player;
//        if (Stalker.hasInstanceOf(player)) {
//            ChunkLoadTask.TaskList.add(packet);
//            ci.cancel();
//        }
//    }
}
