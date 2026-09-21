package com.frierenflight.client.renderer;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.FlowerBedEntity;
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
import org.joml.Matrix4f;

public class FlowerBedRenderer extends EntityRenderer<FlowerBedEntity> {
    private static final ResourceLocation PETAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "textures/entity/blue_moonweed_petal.png");
    private static final ResourceLocation SUNBEAM_TEXTURE = ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "textures/entity/sunbeam_ray.png");
    private static final int PETAL_COUNT = 84;

    public FlowerBedRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FlowerBedEntity entity) {
        return PETAL_TEXTURE;
    }

    @Override
    public boolean shouldRender(FlowerBedEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true; // Never culled by camera frustum for full 64-block field visibility
    }

    @Override
    public void render(FlowerBedEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;
        int maxLifespan = entity.getMaxAge();

        // 1. Dynamic Fade-in & Fade-out Envelope
        float fade = 1.0f;
        if (age < 20.0f) {
            fade = age / 20.0f;
        } else if (age > maxLifespan - 50.0f) {
            fade = Mth.clamp((maxLifespan - age) / 50.0f, 0.0f, 1.0f);
        }

        // 2. Smooth Expanding Wave Radius
        float maxR = entity.getMaxRadius();
        float waveProgress = Mth.clamp(age / (float) FlowerBedEntity.TOTAL_WAVE_TICKS, 0.0f, 1.0f);
        float currentRadius = 1.0f + (maxR - 1.0f) * (float) Math.sin(waveProgress * Math.PI * 0.5);

        int light = 0xF000F0; // Emissive glow

        // 3. Ethereal Translucent Sunbeams / God Rays (Center Light Columns)
        float beamAlpha = fade * (0.35f + 0.10f * (float) Math.sin(age * 0.04f));
        int beamAlphaByte = (int) (beamAlpha * 255);
        if (beamAlphaByte > 0) {
            VertexConsumer beamBuilder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(SUNBEAM_TEXTURE));
            float beamHeight = 28.0f;
            float beamHalfW = 2.4f;

            for (int b = 0; b < 6; b++) {
                float beamAngle = b * 30.0f;
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(beamAngle));
                renderBeamQuad(poseStack, beamBuilder, beamHalfW, beamHeight, 235, 245, 255, beamAlphaByte, light);
                poseStack.popPose();
            }
        }

        // 4. Natural Breeze Drift & 3D Fluttering Petals (No dizzy spinning)
        VertexConsumer petalBuilder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(PETAL_TEXTURE));
        float windAngle = 0.65f; // ~37 degrees gentle breeze
        float windDirX = (float) Math.cos(windAngle);
        float windDirZ = (float) Math.sin(windAngle);
        float perpX = -windDirZ;
        float perpZ = windDirX;

        for (int i = 0; i < PETAL_COUNT; i++) {
            // Anchor location spread across meadow using golden ratio
            float baseDist = 1.5f + (float) ((i * 7.919) % Math.max(2.5f, currentRadius - 3.0f));
            float baseAngle = (float) (i * 2.39996323);
            float anchorX = (float) Math.cos(baseAngle) * baseDist;
            float anchorZ = (float) Math.sin(baseAngle) * baseDist;

            // Natural wind drift progression with seamless cyclic fade
            float phase = (age * 0.35f + i * 11.37f) % 120.0f;
            float t = phase / 120.0f;
            float drift = (t - 0.5f) * 10.0f;
            float sway = (float) Math.sin(age * 0.06f + i * 1.7f) * 1.2f;

            float px = anchorX + windDirX * drift + perpX * sway;
            float pz = anchorZ + windDirZ * drift + perpZ * sway;
            float py = 0.35f + t * 2.4f + (float) Math.sin(age * 0.05f + i * 1.3f) * 0.35f + (i % 6) * 0.40f;

            // Cyclic alpha fade (seamless entry and exit)
            float petalFade = (float) Math.sin(t * Math.PI);
            int individualAlpha = (int) (fade * petalFade * 255);
            if (individualAlpha <= 0) continue;

            // 3D Flutter / Tumbling (gentle pitch/roll sine rock, NO 360 spin)
            float pitch = (float) Math.sin(age * 0.08f + i * 1.5f) * 35.0f;
            float roll = (float) Math.cos(age * 0.06f + i * 2.1f) * 30.0f;
            float yaw = (float) Math.toDegrees(windAngle) + (float) Math.sin(age * 0.035f + i) * 25.0f;

            // Multi-tone Color Palette
            int r, g, b;
            int type = i % 6;
            if (type < 3) {
                // Blue Moonweed (50%)
                r = 65; g = 165; b = 255;
            } else if (type < 5) {
                // Ethereal White (33%)
                r = 245; g = 250; b = 255;
            } else {
                // Golden Pollen Mote (17%)
                r = 255; g = 225; b = 110;
            }

            // Varied scales
            float petalSize = (i % 3 == 0) ? 0.50f : ((i % 3 == 1) ? 0.75f : 0.95f);

            poseStack.pushPose();
            poseStack.translate(px, py, pz);
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
            poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

            renderPetalQuad(poseStack, petalBuilder, petalSize, r, g, b, individualAlpha, light);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderBeamQuad(PoseStack poseStack, VertexConsumer builder, float halfW, float height, int r, int g, int b, int a, int light) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();

        // Front Face
        addVertex(builder, mat, pose, -halfW, 0.0f, 0.0f, 0.0f, 1.0f, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, mat, pose, halfW, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, mat, pose, halfW, height, 0.0f, 1.0f, 0.0f, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, mat, pose, -halfW, height, 0.0f, 0.0f, 0.0f, r, g, b, a, light, 0, 0, 1);

        // Back Face
        addVertex(builder, mat, pose, -halfW, height, 0.0f, 0.0f, 0.0f, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, mat, pose, halfW, height, 0.0f, 1.0f, 0.0f, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, mat, pose, halfW, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, mat, pose, -halfW, 0.0f, 0.0f, 0.0f, 1.0f, r, g, b, a, light, 0, 0, -1);
    }

    private void renderPetalQuad(PoseStack poseStack, VertexConsumer builder, float size, int r, int g, int b, int alpha, int light) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();
        float half = size * 0.5f;

        // Front Face
        addVertex(builder, mat, pose, -half, -half, 0.0f, 0.0f, 1.0f, r, g, b, alpha, light, 0, 0, 1);
        addVertex(builder, mat, pose, half, -half, 0.0f, 1.0f, 1.0f, r, g, b, alpha, light, 0, 0, 1);
        addVertex(builder, mat, pose, half, half, 0.0f, 1.0f, 0.0f, r, g, b, alpha, light, 0, 0, 1);
        addVertex(builder, mat, pose, -half, half, 0.0f, 0.0f, 0.0f, r, g, b, alpha, light, 0, 0, 1);

        // Back Face
        addVertex(builder, mat, pose, -half, half, 0.0f, 0.0f, 0.0f, r, g, b, alpha, light, 0, 0, -1);
        addVertex(builder, mat, pose, half, half, 0.0f, 1.0f, 0.0f, r, g, b, alpha, light, 0, 0, -1);
        addVertex(builder, mat, pose, half, -half, 0.0f, 1.0f, 1.0f, r, g, b, alpha, light, 0, 0, -1);
        addVertex(builder, mat, pose, -half, -half, 0.0f, 0.0f, 1.0f, r, g, b, alpha, light, 0, 0, -1);
    }

    private void addVertex(VertexConsumer builder, Matrix4f mat, PoseStack.Pose pose,
                           float x, float y, float z,
                           float u, float v,
                           int r, int g, int b, int a,
                           int light,
                           float nx, float ny, float nz) {
        builder.addVertex(mat, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
