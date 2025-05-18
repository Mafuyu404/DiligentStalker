package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ArrowStalkerRender extends ArrowRenderer<AbstractArrow> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(DiligentStalker.MODID, "textures/entity/arrow_stalker.png");

    public ArrowStalkerRender(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractArrow entity) {
        return TEXTURE;
    }

}
