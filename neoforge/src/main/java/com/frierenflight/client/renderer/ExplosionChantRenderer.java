package com.frierenflight.client.renderer;

import com.frierenflight.client.vfx.ExplosionVFXRenderer;
import com.frierenflight.entity.ExplosionChantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ExplosionChantRenderer extends EntityRenderer<ExplosionChantEntity> {

    public ExplosionChantRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosionChantEntity entity) {
        return ExplosionVFXRenderer.TEX_MAGIC_CIRCLE;
    }

    @Override
    public boolean shouldRender(ExplosionChantEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true; // Never cull chanting circle at player's feet
    }

    @Override
    public void render(ExplosionChantEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float time = entity.tickCount + partialTicks;
        float progress = Mth.clamp(time / 85.0f, 0.0f, 1.0f);

        // Alpha envelope: Quick fade-in over 4 ticks, intense glow, quick fade-out at end
        float alpha = time < 4.0f ? (time / 4.0f) : (time > 85.0f ? Mth.clamp((95.0f - time) / 10.0f, 0.0f, 1.0f) : 1.0f);
        int aByte = (int) (alpha * 255);

        // Smooth expanding radius from 1.8m to 4.2m
        float radius = 1.8f + (float) Math.sin(progress * Math.PI * 0.5) * 2.4f;

        // 1. Dark Purple Outer Aura Ring
        VertexConsumer darkConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(ExplosionVFXRenderer.TEX_DARK_PURPLE_RING));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-time * 1.8f));
        ExplosionVFXRenderer.renderHorizontalDisc(poseStack, darkConsumer, radius * 1.35f, 0.05f, 255, 60, 50, (int) (alpha * 230));
        poseStack.popPose();

        // 2. Neon Purple Runic Ring
        VertexConsumer purpleConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(ExplosionVFXRenderer.TEX_PURPLE_RING));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 2.5f));
        ExplosionVFXRenderer.renderHorizontalDisc(poseStack, purpleConsumer, radius * 1.2f, 0.07f, 255, 120, 255, (int) (alpha * 220));
        poseStack.popPose();

        // 3. Centerpiece Authentic Megumin Magic Circle (Red & Gold)
        VertexConsumer magicConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(ExplosionVFXRenderer.TEX_MAGIC_CIRCLE));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 3.5f));
        ExplosionVFXRenderer.renderHorizontalDisc(poseStack, magicConsumer, radius, 0.09f, 255, 255, 255, aByte);
        poseStack.popPose();

        // 5. Gyroscopic 3D Magic Circle tilted at torso height
        poseStack.pushPose();
        poseStack.translate(0, 1.1f + (float) Math.sin(time * 0.15f) * 0.15f, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(35.0f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 4.5f));
        ExplosionVFXRenderer.renderHorizontalDisc(poseStack, magicConsumer, 2.8f, 0.0f, 255, 200, 120, (int) (alpha * 210));
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }
}
