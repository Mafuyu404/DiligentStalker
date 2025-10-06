package com.mafuyu404.diligentstalker.item;

import com.mafuyu404.diligentstalker.data.StalkerDataComponents;
import com.mafuyu404.diligentstalker.event.handler.StalkerManage;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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

import java.util.*;

public class StalkerMasterItem extends Item {

    public StalkerMasterItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            UUID stalkerId = stack.get(StalkerDataComponents.STALKER_ID);
            if (stalkerId == null || Stalker.hasInstanceOf(player)) {
                return InteractionResultHolder.fail(stack);
            }

            player.startUsingItem(hand);

            if (player.isLocalPlayer() && !Stalker.hasInstanceOf(player)) {
                BlockPos center = entryOfUsingStalkerMaster(player);
                UUID entityUUID = uuidOfUsingStalkerMaster(player);
                if (center != null && entityUUID != null) {
                    ClientStalkerUtil.tryRemoteConnect(center, entity -> entity.getUUID().equals(entityUUID));
                }
            }
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int duration = this.getUseDuration(stack, entity) - timeLeft;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        BlockPos pos = stack.get(StalkerDataComponents.STALKER_POSITION);
        if (pos != null) {
            tooltip.add(Component.literal("> " + pos.toShortString() + " <").withStyle(ChatFormatting.GREEN));
        }
        tooltip.add(Component.translatable("item.diligentstalker.stalker_master.intro1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.diligentstalker.stalker_master.intro2").withStyle(ChatFormatting.GOLD));
    }

    public static BlockPos entryOfUsingStalkerMaster(Player player) {
        if (player != null && player.isUsingItem() && player.getMainHandItem().getItem() instanceof StalkerMasterItem) {
            ItemStack stack = player.getMainHandItem();
            UUID stalkerId = stack.get(StalkerDataComponents.STALKER_ID);
            if (stalkerId != null && StalkerManage.DronePosition.containsKey(stalkerId)) {
                return StalkerManage.DronePosition.get(stalkerId).getValue();
            }
        }
        return null;
    }

    public static UUID uuidOfUsingStalkerMaster(Player player) {
        if (player != null && player.isUsingItem() && player.getMainHandItem().getItem() instanceof StalkerMasterItem) {
            ItemStack stack = player.getMainHandItem();
            return stack.get(StalkerDataComponents.STALKER_ID);
        }
        return null;
    }
}
