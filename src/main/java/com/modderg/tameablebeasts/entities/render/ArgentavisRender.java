package com.modderg.tameablebeasts.entities.render;

import com.modderg.tameablebeasts.entities.custom.ArgentavisEntity;
import com.modderg.tameablebeasts.entities.custom.CrestedGeckoEntity;
import com.modderg.tameablebeasts.entities.model.ArgentavisModel;
import com.modderg.tameablebeasts.entities.model.CrestedGeckoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArgentavisRender extends GeoEntityRenderer<ArgentavisEntity> {
    public ArgentavisRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ArgentavisModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ArgentavisEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(!animatable.hasSaddle() && bone.getName().contains("saddle")){
            return;
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
