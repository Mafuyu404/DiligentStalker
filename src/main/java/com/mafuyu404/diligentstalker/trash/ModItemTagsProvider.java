package com.mafuyu404.diligentstalker.trash;

import com.mafuyu404.diligentstalker.registry.StalkerItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 将自定义箭矢添加到原版ARROWS标签
        tag(ItemTags.ARROWS).add(StalkerItems.ARROW_STALKER.get());

        // 可以在此添加其他自定义标签
        // tag(Tags.Items.ARROWS) // 如果需要Forge通用标签
    }
}
