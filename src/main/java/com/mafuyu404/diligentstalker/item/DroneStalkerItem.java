package com.mafuyu404.diligentstalker.item;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class DroneStalkerItem extends Item {
    public DroneStalkerItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null) {
            Level level = context.getLevel();
            DroneStalkerEntity drone = new DroneStalkerEntity(StalkerEntities.DRONE_STALKER.get(), level);
            drone.setPos(context.getClickLocation());
            level.addFreshEntity(drone);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> result, TooltipFlag p_41214_) {
        result.add(Component.translatable("item.diligentstalker.drone_stalker.intro1").withStyle(ChatFormatting.GOLD));
        result.add(Component.translatable("item.diligentstalker.drone_stalker.intro2").withStyle(ChatFormatting.GOLD));
    }
}
