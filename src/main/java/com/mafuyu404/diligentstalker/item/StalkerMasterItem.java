package com.mafuyu404.diligentstalker.item;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.ChatFormatting;
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
        }
        return InteractionResultHolder.consume(itemStack);
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
    public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {

        }
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
}
