package com.mafuyu404.diligentstalker.api;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fabric版本的物品处理接口，替代Forge的IItemHandler
 */
public interface ItemHandler {
    /**
     * 返回此物品处理器中的槽位数量
     */
    int getSlots();

    /**
     * 返回指定槽位中的物品堆
     */
    @NotNull
    ItemStack getStackInSlot(int slot);

    /**
     * 向指定槽位插入物品
     *
     * @param slot     目标槽位
     * @param stack    要插入的物品堆
     * @param simulate 如果为true，则只模拟操作而不实际执行
     * @return 剩余未能插入的物品
     */
    @NotNull
    ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

    /**
     * 从指定槽位提取物品
     *
     * @param slot     目标槽位
     * @param amount   要提取的数量
     * @param simulate 如果为true，则只模拟操作而不实际执行
     * @return 提取的物品
     */
    @NotNull
    ItemStack extractItem(int slot, int amount, boolean simulate);

    /**
     * 返回指定槽位的最大堆叠数量
     */
    int getSlotLimit(int slot);

    /**
     * 检查指定物品是否可以放入指定槽位
     */
    boolean isItemValid(int slot, @NotNull ItemStack stack);
}