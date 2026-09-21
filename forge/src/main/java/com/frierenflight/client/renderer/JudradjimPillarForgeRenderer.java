package com.frierenflight.client.renderer;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.JudradjimPillarForgeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class JudradjimPillarForgeRenderer extends EntityRenderer<JudradjimPillarForgeEntity> {
    private static final int FRAME_COUNT = 6;
    private static final ResourceLocation[] FRAMES = new ResourceLocation[FRAME_COUNT];
    private static final ResourceLocation SHOCKWAVE_TEXTURE = new ResourceLocation(FrierenFlightCommon.MODID, "textures/entity/judradjim_shockwave.png");

    static {
        for (int i = 0; i < FRAME_COUNT; i++) {
            FRAMES[i] = new ResourceLocation(FrierenFlightCommon.MODID, "textures/entity/judradjim_pillar_" + i + ".png");
        }
    }

    private static final float HEIGHT = 38.0f;

    public JudradjimPillarForgeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(JudradjimPillarForgeEntity entity) {
        int frame = Math.abs(entity.tickCount) % FRAME_COUNT;
        return FRAMES[frame];
    }

    @Override
    public boolean shouldRender(JudradjimPillarForgeEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true; // Never culled by camera frustum with shaders
    }

    @Override
    public void render(JudradjimPillarForgeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;

        // Dynamic fade-in and fade-out envelope
        float alpha = 1.0f;
        if (age < 3.0f) {
            alpha = age / 3.0f;
        } else if (age > 16.0f) {
            alpha = Mth.clamp(1.0f - (age - 16.0f) / 9.0f, 0.0f, 1.0f);
        }
        int alphaByte = (int) (alpha * 255);

        // Rapid 20 FPS multi-frame cycling across asynchronous layers
        int tick = entity.tickCount;
        int frameCore = Math.abs(tick) % FRAME_COUNT;
        int frameInner = Math.abs(tick + 2) % FRAME_COUNT;
        int frameOuter = Math.abs(tick + 4) % FRAME_COUNT;

        // 1. Central Jagged Lightning Core (3 Intersecting Cross Planes for intense vertical lightning strike)
        VertexConsumer coreBuilder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(FRAMES[frameCore]));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 7.0f));
        renderCrossPlanes(poseStack, coreBuilder, 2.2f, HEIGHT, alphaByte, -(age * 0.15f));
        poseStack.popPose();

        // 2. Inner Rotating Cataclysm Cylinder (High-speed counter-clockwise swirl with dark-gold lightning)
        VertexConsumer innerBuilder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(FRAMES[frameInner]));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-age * 22.0f));
        renderCylinder(poseStack, innerBuilder, 1.2f, HEIGHT, alphaByte, -(age * 0.10f), 8);
        poseStack.popPose();

        // 3. Outer Electric Plasma Cylinder (Clockwise expanding aura)
        VertexConsumer outerBuilder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(FRAMES[frameOuter]));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 14.0f));
        renderCylinder(poseStack, outerBuilder, 2.6f, HEIGHT, (int) (alphaByte * 0.80f), -(age * 0.05f), 12);
        poseStack.popPose();

        // 4. Ground Impact Shockwave Burst (Expanding circular electric ring)
        VertexConsumer shockBuilder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(SHOCKWAVE_TEXTURE));
        float shockRadius = Math.min(5.2f, 1.2f + age * 0.45f);
        renderShockwave(poseStack, shockBuilder, shockRadius, (int) (alphaByte * 0.90f), age * 12.0f);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderCrossPlanes(PoseStack poseStack, VertexConsumer builder, float width, float height, int alpha, float vOffset) {
        float halfW = width * 0.5f;

        for (int plane = 0; plane < 3; plane++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(plane * 60.0f));
            PoseStack.Pose pPose = poseStack.last();
            Matrix4f pMat = pPose.pose();
            Matrix3f pNorm = pPose.normal();

            float v1 = vOffset;
            float v2 = vOffset + 3.0f;

            // Front Face
            addVertex(builder, pMat, pNorm, -halfW, 0, 0, 0.0f, v2, 255, 255, 255, alpha, 0, 0, 1);
            addVertex(builder, pMat, pNorm, halfW, 0, 0, 1.0f, v2, 255, 255, 255, alpha, 0, 0, 1);
            addVertex(builder, pMat, pNorm, halfW, height, 0, 1.0f, v1, 255, 255, 255, alpha, 0, 0, 1);
            addVertex(builder, pMat, pNorm, -halfW, height, 0, 0.0f, v1, 255, 255, 255, alpha, 0, 0, 1);

            // Back Face
            addVertex(builder, pMat, pNorm, halfW, 0, 0, 1.0f, v2, 255, 255, 255, alpha, 0, 0, -1);
            addVertex(builder, pMat, pNorm, -halfW, 0, 0, 0.0f, v2, 255, 255, 255, alpha, 0, 0, -1);
            addVertex(builder, pMat, pNorm, -halfW, height, 0, 0.0f, v1, 255, 255, 255, alpha, 0, 0, -1);
            addVertex(builder, pMat, pNorm, halfW, height, 0, 1.0f, v1, 255, 255, 255, alpha, 0, 0, -1);

            poseStack.popPose();
        }
    }

    private void renderCylinder(PoseStack poseStack, VertexConsumer builder, float radius, float height, int alpha, float vOffset, int sides) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();
        Matrix3f norm = pose.normal();

        for (int i = 0; i < sides; i++) {
            float angle1 = (float) (i * 2 * Math.PI / sides);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / sides);

            float x1 = radius * Mth.cos(angle1);
            float z1 = radius * Mth.sin(angle1);
            float x2 = radius * Mth.cos(angle2);
            float z2 = radius * Mth.sin(angle2);

            float u1 = (float) i / sides;
            float u2 = (float) (i + 1) / sides;
            float v1 = vOffset;
            float v2 = vOffset + 3.0f;

            // Outside Face
            addVertex(builder, mat, norm, x1, 0, z1, u1, v2, 255, 255, 255, alpha, 0, 1, 0);
            addVertex(builder, mat, norm, x2, 0, z2, u2, v2, 255, 255, 255, alpha, 0, 1, 0);
            addVertex(builder, mat, norm, x2, height, z2, u2, v1, 255, 255, 255, alpha, 0, 1, 0);
            addVertex(builder, mat, norm, x1, height, z1, u1, v1, 255, 255, 255, alpha, 0, 1, 0);

            // Inside Face
            addVertex(builder, mat, norm, x1, height, z1, u1, v1, 255, 255, 255, alpha, 0, -1, 0);
            addVertex(builder, mat, norm, x2, height, z2, u2, v1, 255, 255, 255, alpha, 0, -1, 0);
            addVertex(builder, mat, norm, x2, 0, z2, u2, v2, 255, 255, 255, alpha, 0, -1, 0);
            addVertex(builder, mat, norm, x1, 0, z1, u1, v2, 255, 255, 255, alpha, 0, -1, 0);
        }
    }

    private void renderShockwave(PoseStack poseStack, VertexConsumer builder, float radius, int alpha, float rotation) {
        poseStack.pushPose();
        poseStack.translate(0, 0.08f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();
        Matrix3f norm = pose.normal();

        // Top Face
        addVertex(builder, mat, norm, -radius, 0, -radius, 0.0f, 0.0f, 255, 255, 255, alpha, 0, 1, 0);
        addVertex(builder, mat, norm, -radius, 0, radius, 0.0f, 1.0f, 255, 255, 255, alpha, 0, 1, 0);
        addVertex(builder, mat, norm, radius, 0, radius, 1.0f, 1.0f, 255, 255, 255, alpha, 0, 1, 0);
        addVertex(builder, mat, norm, radius, 0, -radius, 1.0f, 0.0f, 255, 255, 255, alpha, 0, 1, 0);

        // Bottom Face
        addVertex(builder, mat, norm, radius, 0, -radius, 1.0f, 0.0f, 255, 255, 255, alpha, 0, -1, 0);
        addVertex(builder, mat, norm, radius, 0, radius, 1.0f, 1.0f, 255, 255, 255, alpha, 0, -1, 0);
        addVertex(builder, mat, norm, -radius, 0, radius, 0.0f, 1.0f, 255, 255, 255, alpha, 0, -1, 0);
        addVertex(builder, mat, norm, -radius, 0, -radius, 0.0f, 0.0f, 255, 255, 255, alpha, 0, -1, 0);

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer builder, Matrix4f mat, Matrix3f norm,
                           float x, float y, float z,
                           float u, float v,
                           int r, int g, int b, int a,
                           float nx, float ny, float nz) {
        builder.vertex(mat, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0xF000F0)
                .normal(norm, nx, ny, nz)
                .endVertex();
    }
}
