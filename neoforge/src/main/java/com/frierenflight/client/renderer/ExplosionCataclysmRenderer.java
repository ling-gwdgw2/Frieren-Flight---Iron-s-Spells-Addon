package com.frierenflight.client.renderer;

import com.frierenflight.client.vfx.ExplosionVFXRenderer;
import com.frierenflight.entity.ExplosionCataclysmEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ExplosionCataclysmRenderer extends EntityRenderer<ExplosionCataclysmEntity> {

    public ExplosionCataclysmRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosionCataclysmEntity entity) {
        return ExplosionVFXRenderer.TEX_MAGIC_CIRCLE;
    }

    @Override
    public boolean shouldRender(ExplosionCataclysmEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true; // Never cull the apocalyptic 70m beam & mushroom cloud
    }

    @Override
    public void render(ExplosionCataclysmEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Delegate directly to the master Konosuba 4-phase procedural rendering pipeline
        ExplosionVFXRenderer.renderExplosion(poseStack, bufferSource, entity.tickCount, partialTicks);
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }
}
