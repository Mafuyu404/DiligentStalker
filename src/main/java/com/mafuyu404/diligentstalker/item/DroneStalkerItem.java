package com.mafuyu404.diligentstalker.item;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DroneStalkerItem extends Item {
    public DroneStalkerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null) {
            Level level = context.getLevel();
            DroneStalkerEntity drone = new DroneStalkerEntity(StalkerEntities.DRONE_STALKER.get(), level);
            drone.setPos(context.getClickLocation());
            level.addFreshEntity(drone);
            context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.diligentstalker.drone_stalker.intro1").withStyle(ChatFormatting.GOLD));
        list.add(Component.translatable("item.diligentstalker.drone_stalker.intro2").withStyle(ChatFormatting.GOLD));
    }
}
