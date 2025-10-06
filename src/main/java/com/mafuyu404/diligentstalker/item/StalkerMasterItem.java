package com.mafuyu404.diligentstalker.item;

import com.mafuyu404.diligentstalker.event.handler.StalkerManage;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StalkerMasterItem extends Item {
    public StalkerMasterItem() {
        super(new Properties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            CompoundTag tag = itemStack.getOrCreateTag();
            if (Stalker.hasInstanceOf(player)) return InteractionResultHolder.fail(itemStack);
            if (!tag.contains("StalkerId")) return InteractionResultHolder.fail(itemStack);
            player.startUsingItem(hand);
            if (player.isLocalPlayer() && !Stalker.hasInstanceOf(player)) {
                BlockPos center = entryOfUsingStalkerMaster(player).getValue();
                UUID entityUUID = uuidOfUsingStalkerMaster(player);
                if (center != null && entityUUID != null) {
                    ClientStalkerUtil.tryRemoteConnect(center, entity -> entity.getUUID().equals(entityUUID));
                }
            }
        }
        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int duration = this.getUseDuration(stack) - timeLeft;
        }
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> result, TooltipFlag p_41214_) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("StalkerPosition")) {
            result.add(Component.literal("> " + Arrays.toString(tag.getIntArray("StalkerPosition")) + " <").withStyle(ChatFormatting.GREEN));
        }
        result.add(Component.translatable("item.diligentstalker.stalker_master.intro1").withStyle(ChatFormatting.GOLD));
        result.add(Component.translatable("item.diligentstalker.stalker_master.intro2").withStyle(ChatFormatting.GOLD));
    }

    public static Map.Entry<String, BlockPos> entryOfUsingStalkerMaster(Player player) {
        if (player != null && player.isUsingItem()) {
            if (player.getMainHandItem().getItem() instanceof StalkerMasterItem) {
                CompoundTag tag = player.getMainHandItem().getOrCreateTag();
                if (tag.contains("StalkerId") && StalkerManage.DronePosition.containsKey(tag.getUUID("StalkerId"))) {
                    return StalkerManage.DronePosition.get(tag.getUUID("StalkerId"));
                }
            }
        }
        return null;
    }

    public static UUID uuidOfUsingStalkerMaster(Player player) {
        if (player != null && player.isUsingItem()) {
            if (player.getMainHandItem().getItem() instanceof StalkerMasterItem) {
                CompoundTag tag = player.getMainHandItem().getOrCreateTag();
                if (tag.contains("StalkerId")) {
                    return tag.getUUID("StalkerId");
                }
            }
        }
        return null;
    }
}
