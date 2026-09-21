package com.frierenflight.client.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * ==============================================================================
 * MASTER 3D VFX PIPELINE: MEGUMIN EXPLOSION MAGIC (NEOFORGE 1.21.1)
 * ==============================================================================
 * Faithful recreation & enhancement of Megumin's iconic anime Explosion Magic.
 * - 110-Meter Sky Pillar Concentric Circles ascending into the stratosphere
 * - Rotating Dark Purple & Neon Purple Runic Circles & Gyroscope Seals
 * - Stratospheric Rainbow Star Eclipse Flare & 110m Plasma Ray Strike
 * - Megaton 4-Point Cross Star Blast, Anime Shockwaves & HD Mushroom Torus
 * ==============================================================================
 */
public class ExplosionVFXRenderer {

    // --------------------------------------------------------------------------
    // High-Definition Anime Assets (Imported from Megumin Staff Addon)
    // --------------------------------------------------------------------------
    public static final ResourceLocation TEX_DARK_PURPLE_RING   = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_dark_purple_ring.png");
    public static final ResourceLocation TEX_PURPLE_RING        = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_purple_ring.png");
    public static final ResourceLocation TEX_MAGIC_CIRCLE       = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_magic_circle.png");
    public static final ResourceLocation TEX_RAINBOW_ECLIPSE    = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_star_eclipse_rainbow.png");
    public static final ResourceLocation TEX_EXPLOSION_STAR     = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_explosion_star.png");
    public static final ResourceLocation TEX_WHITE_STAR_EXPLODE = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_white_star_explode.png");
    public static final ResourceLocation TEX_SHOCKWAVE          = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_shockwave.png");
    public static final ResourceLocation TEX_SHOCKWAVE_AFTER    = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/megumin_shockwave_after.png");
    public static final ResourceLocation TEX_BEAM_PLASMA        = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/explosion_beam_plasma.png");
    public static final ResourceLocation TEX_CRATER             = ResourceLocation.fromNamespaceAndPath("frieren_flight", "textures/vfx/explosion_crater_fracture.png");

    public static final int EMISSIVE_LIGHT = 0xF000F0;

    // 11-Tier Sky Pillar concentric heights and radii matching anime addon
    private static final float[] PILLAR_HEIGHTS = {10.0f, 20.0f, 30.0f, 40.0f, 50.0f, 60.0f, 70.0f, 80.0f, 90.0f, 100.0f, 110.0f};
    private static final float[] PILLAR_RADII   = { 4.0f, 12.0f,  8.0f, 20.0f, 28.0f, 12.0f, 20.0f, 16.0f, 12.0f,   8.0f,  40.0f};

    /**
     * MASTER ORCHESTRATION PIPELINE
     * Total Animation Duration: ~260 ticks (~13 seconds)
     */
    public static void renderExplosion(PoseStack poseStack, MultiBufferSource bufferSource, float ageInTicks, float partialTick) {
        float time = ageInTicks + partialTick;

        // ======================================================================
        // PHASE 1: 110-METER SKY PILLAR RINGS & GROUND RUNIC MATRIX (t = 0 .. 78)
        // ======================================================================
        if (time < 78.0f) {
            float chantProgress = Mth.clamp(time / 68.0f, 0.0f, 1.0f);
            float alphaFade = (time > 70.0f ? (78.0f - time) / 8.0f : 1.0f);

            // 1. Ground Runic Seals: Dark Purple Ring & Concentric Neon Rings
            VertexConsumer darkCircleConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_DARK_PURPLE_RING));
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 1.8f));
            float groundRadius = 5.0f + chantProgress * 7.0f;
            renderHorizontalDisc(poseStack, darkCircleConsumer, groundRadius * 1.25f, 0.06f, 255, 50, 40, (int) (alphaFade * 240));
            poseStack.popPose();

            VertexConsumer purpleRingConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_PURPLE_RING));
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-time * 2.6f));
            renderHorizontalDisc(poseStack, purpleRingConsumer, groundRadius * 1.35f, 0.08f, 255, 120, 255, (int) (alphaFade * 210));
            poseStack.popPose();

            // 2. Centerpiece Authentic Megumin Red-and-Gold Magic Circle
            VertexConsumer magicCircleConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_MAGIC_CIRCLE));
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 3.2f));
            renderHorizontalDisc(poseStack, magicCircleConsumer, groundRadius, 0.10f, 255, 255, 255, (int) (alphaFade * 255));
            poseStack.popPose();

            // 3. Gyroscopic Hovering 3D Ring around center
            poseStack.pushPose();
            poseStack.translate(0, 1.5f + (float) Math.sin(time * 0.12f) * 0.3f, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(35.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(time * 4.0f));
            renderHorizontalDisc(poseStack, magicCircleConsumer, 4.2f, 0.0f, 255, 180, 80, (int) (alphaFade * 220));
            poseStack.popPose();

            // 4. 110-Meter Sky Pillar: Staggered Ascending Authentic Megumin Magic Circles
            for (int i = 0; i < PILLAR_HEIGHTS.length; i++) {
                float startThreshold = i * 3.2f;
                if (time >= startThreshold) {
                    float ringLife = Mth.clamp((time - startThreshold) / 8.0f, 0.0f, 1.0f);
                    float ringAlpha = (time > 70.0f ? (78.0f - time) / 8.0f : ringLife) * alphaFade;
                    float rotDir = (i % 2 == 0) ? 1.0f : -1.0f;
                    float rotSpeed = rotDir * (time * (2.2f + i * 0.25f));

                    poseStack.pushPose();
                    poseStack.translate(0, PILLAR_HEIGHTS[i], 0);
                    poseStack.mulPose(Axis.YP.rotationDegrees(rotSpeed));
                    // Authentic red-and-gold runic circle
                    renderHorizontalDisc(poseStack, magicCircleConsumer, PILLAR_RADII[i], 0.0f, 255, 230, 200, (int) (ringAlpha * 240));
                    poseStack.popPose();
                }
            }

            // 6. Stratospheric Pre-Charge Rainbow Flare (Y = 110)
            if (time > 45.0f) {
                float apexFlareT = (time - 45.0f) / 30.0f;
                float apexScale = 4.0f + apexFlareT * 18.0f;
                float apexAlpha = (time > 72.0f ? (78.0f - time) / 6.0f : apexFlareT) * 255;

                VertexConsumer rainbowConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_RAINBOW_ECLIPSE));
                poseStack.pushPose();
                poseStack.translate(0, 110.0f, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(time * 8.0f));
                renderCrossStarBillboard(poseStack, rainbowConsumer, apexScale, (int) apexAlpha);
                poseStack.popPose();
            }
        }

        // ======================================================================
        // PHASE 2: STRATOSPHERIC RAINBOW FLARE & 110M PLASMA RAY (t = 65 .. 100)
        // ======================================================================
        if (time >= 65.0f && time <= 102.0f) {
            float beamLife = (time - 65.0f) / 35.0f;
            float beamAlpha = beamLife < 0.2f ? beamLife / 0.2f : (1.0f - (beamLife - 0.2f) / 0.8f);
            float beamHeight = 110.0f; // Pierces all the way from Y=110 down to ground

            // Apex Lens Flare
            VertexConsumer rainbowConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_RAINBOW_ECLIPSE));
            poseStack.pushPose();
            poseStack.translate(0, beamHeight, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 15.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(time * 10.0f));
            renderCrossStarBillboard(poseStack, rainbowConsumer, 26.0f, (int) (beamAlpha * 255));
            poseStack.popPose();

            VertexConsumer beamConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_BEAM_PLASMA));

            // Inner White-Hot Core Cylinder
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 16.0f));
            float coreVOffset = -(time * 0.55f);
            renderCylinderTube(poseStack, beamConsumer, 1.4f, beamHeight, 12, coreVOffset, 255, 250, 220, (int) (beamAlpha * 255));
            poseStack.popPose();

            // Outer Fiery Orange/Crimson Plasma Cylinder
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-time * 10.0f));
            float midVOffset = -(time * 0.35f);
            renderCylinderTube(poseStack, beamConsumer, 3.2f, beamHeight, 12, midVOffset, 255, 80, 25, (int) (beamAlpha * 220));
            poseStack.popPose();

            // 3 Intersecting Cross-Planes for full 360-degree volumetric thickness
            renderIntersectingCrossPlanes(poseStack, beamConsumer, 5.0f, beamHeight, -(time * 0.45f), (int) (beamAlpha * 230));

            // Cascading Shockwave Discs dropping down the beam
            VertexConsumer shockConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_SHOCKWAVE));
            for (int k = 0; k < 4; k++) {
                float dropProgress = ((time * 2.5f + k * 28.0f) % 110.0f);
                float dropY = 110.0f - dropProgress;
                poseStack.pushPose();
                poseStack.translate(0, dropY, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(time * 6.0f));
                renderHorizontalDisc(poseStack, shockConsumer, 5.5f, 0.0f, 255, 200, 150, (int) (beamAlpha * 180));
                poseStack.popPose();
            }
        }

        // ======================================================================
        // PHASE 3: SINGULARITY & IMPLOSION (t = 88 .. 105 ticks)
        // ======================================================================
        if (time >= 88.0f && time <= 105.0f) {
            float implosionLife = (time - 88.0f) / 17.0f;
            float sphereRadius;
            if (implosionLife < 0.65f) {
                sphereRadius = 0.5f + (implosionLife / 0.65f) * 5.0f;
            } else {
                float collapseT = (implosionLife - 0.65f) / 0.35f;
                sphereRadius = 5.0f * (1.0f - collapseT * collapseT);
            }

            VertexConsumer shockConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_SHOCKWAVE));
            renderSingularitySphere(poseStack, shockConsumer, sphereRadius, time);
        }

        // ======================================================================
        // PHASE 4: MEGATON DETONATION & ANIME MUSHROOM CLOUD (t = 100 .. 260)
        // ======================================================================
        if (time >= 100.0f) {
            float blastAge = time - 100.0f;
            float blastNorm = Mth.clamp(blastAge / 150.0f, 0.0f, 1.0f);

            // 1. ANIME CROSS STAR EXPLOSION FLASH (Iconic Megumin 4-point cross star blast)
            if (blastAge < 28.0f) {
                float flashNorm = blastAge / 28.0f;
                float flashAlpha = (1.0f - flashNorm * flashNorm);
                float flashScale = 8.0f + (float) Math.sin(flashNorm * Math.PI * 0.5) * 45.0f;

                VertexConsumer starConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_EXPLOSION_STAR));
                poseStack.pushPose();
                poseStack.translate(0, 4.0f, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(blastAge * 4.0f));
                renderCrossStarBillboard(poseStack, starConsumer, flashScale, (int) (flashAlpha * 255));
                poseStack.popPose();

                VertexConsumer whiteStarConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_WHITE_STAR_EXPLODE));
                poseStack.pushPose();
                poseStack.translate(0, 4.0f, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(-blastAge * 6.0f));
                renderCrossStarBillboard(poseStack, whiteStarConsumer, flashScale * 0.85f, (int) (flashAlpha * 255));
                poseStack.popPose();
            }

            // 2. DUAL GROUND SHOCKWAVE EXPANSION RINGS
            VertexConsumer shockConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_SHOCKWAVE));
            float waveRadius1 = blastAge * 1.8f;
            float waveAlpha1 = Mth.clamp(1.0f - (waveRadius1 / 62.0f), 0.0f, 1.0f);
            if (waveAlpha1 > 0.01f) {
                renderGroundShockwave(poseStack, shockConsumer, waveRadius1, waveAlpha1);
            }

            if (blastAge > 4.0f) {
                VertexConsumer shockAfterConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_SHOCKWAVE_AFTER));
                float waveRadius2 = (blastAge - 4.0f) * 1.4f;
                float waveAlpha2 = Mth.clamp(1.0f - (waveRadius2 / 52.0f), 0.0f, 1.0f);
                if (waveAlpha2 > 0.01f) {
                    renderGroundShockwave(poseStack, shockAfterConsumer, waveRadius2, waveAlpha2 * 0.8f);
                }
            }


            // 5. BURNING SCORCHED CRATER FRACTURE DECAL
            VertexConsumer craterConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEX_CRATER));
            float craterAlpha = Mth.clamp(1.0f - blastAge / 160.0f, 0.0f, 1.0f);
            poseStack.pushPose();
            renderHorizontalDisc(poseStack, craterConsumer, 18.0f, 0.015f, 255, 140, 60, (int) (craterAlpha * 255));
            poseStack.popPose();
        }
    }

    // ==========================================================================
    // PROCEDURAL GEOMETRY PRIMITIVES
    // ==========================================================================

    /**
     * 3D CROSS STAR BILLBOARD (3 intersecting perpendicular quads: XY, ZY, and XZ)
     */
    public static void renderCrossStarBillboard(PoseStack poseStack, VertexConsumer builder, float size, int alpha) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        float half = size * 0.5f;

        // Quad 1: XY Plane
        builder.addVertex(matrix, -half, -half, 0).setColor(255, 255, 255, alpha).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);
        builder.addVertex(matrix,  half, -half, 0).setColor(255, 255, 255, alpha).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);
        builder.addVertex(matrix,  half,  half, 0).setColor(255, 255, 255, alpha).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);
        builder.addVertex(matrix, -half,  half, 0).setColor(255, 255, 255, alpha).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);

        // Quad 2: ZY Plane
        builder.addVertex(matrix, 0, -half, -half).setColor(255, 255, 255, alpha).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 1, 0, 0);
        builder.addVertex(matrix, 0, -half,  half).setColor(255, 255, 255, alpha).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 1, 0, 0);
        builder.addVertex(matrix, 0,  half,  half).setColor(255, 255, 255, alpha).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 1, 0, 0);
        builder.addVertex(matrix, 0,  half, -half).setColor(255, 255, 255, alpha).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 1, 0, 0);

        // Quad 3: XZ Plane (Horizontal)
        builder.addVertex(matrix, -half, 0, -half).setColor(255, 255, 255, alpha).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
        builder.addVertex(matrix, -half, 0,  half).setColor(255, 255, 255, alpha).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
        builder.addVertex(matrix,  half, 0,  half).setColor(255, 255, 255, alpha).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
        builder.addVertex(matrix,  half, 0, -half).setColor(255, 255, 255, alpha).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
    }

    public static void renderCylinderTube(PoseStack poseStack, VertexConsumer builder, float radius, float height, int segments, float vOffset, int r, int g, int b, int a) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        float angleStep = (float) (2 * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float a1 = i * angleStep;
            float a2 = (i + 1) * angleStep;

            float x1 = (float) Math.cos(a1) * radius;
            float z1 = (float) Math.sin(a1) * radius;
            float x2 = (float) Math.cos(a2) * radius;
            float z2 = (float) Math.sin(a2) * radius;

            float u1 = (float) i / segments;
            float u2 = (float) (i + 1) / segments;
            float v1 = 0.0f + vOffset;
            float v2 = 2.0f + vOffset;

            builder.addVertex(matrix, x1, 0, z1).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, x1 / radius, 0, z1 / radius);
            builder.addVertex(matrix, x2, 0, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, x2 / radius, 0, z2 / radius);
            builder.addVertex(matrix, x2, height, z2).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, x2 / radius, 0, z2 / radius);
            builder.addVertex(matrix, x1, height, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, x1 / radius, 0, z1 / radius);
        }
    }

    public static void renderHorizontalDisc(PoseStack poseStack, VertexConsumer builder, float radius, float yOffset, int r, int g, int b, int a) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        // Top Face (Normal UP - Counter-Clockwise looking from above)
        builder.addVertex(matrix, -radius, yOffset,  radius).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
        builder.addVertex(matrix,  radius, yOffset,  radius).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
        builder.addVertex(matrix,  radius, yOffset, -radius).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);
        builder.addVertex(matrix, -radius, yOffset, -radius).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 1, 0);

        // Bottom Face (Normal DOWN - Counter-Clockwise looking from below)
        builder.addVertex(matrix, -radius, yOffset, -radius).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, -1, 0);
        builder.addVertex(matrix,  radius, yOffset, -radius).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, -1, 0);
        builder.addVertex(matrix,  radius, yOffset,  radius).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, -1, 0);
        builder.addVertex(matrix, -radius, yOffset,  radius).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, -1, 0);
    }

    public static void renderGroundShockwave(PoseStack poseStack, VertexConsumer builder, float radius, float alpha) {
        poseStack.pushPose();
        int a = (int) (alpha * 245);
        renderHorizontalDisc(poseStack, builder, radius, 0.05f, 255, 230, 190, a);
        poseStack.popPose();
    }

    public static void renderIntersectingCrossPlanes(PoseStack poseStack, VertexConsumer builder, float width, float height, float vOffset, int alpha) {
        for (int i = 0; i < 3; i++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(i * 60.0f));
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix = pose.pose();
            float halfW = width * 0.5f;

            builder.addVertex(matrix, -halfW, 0, 0).setColor(255, 140, 50, alpha).setUv(0.0f, 2.0f + vOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);
            builder.addVertex(matrix,  halfW, 0, 0).setColor(255, 140, 50, alpha).setUv(1.0f, 2.0f + vOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);
            builder.addVertex(matrix,  halfW, height, 0).setColor(255, 220, 120, alpha).setUv(1.0f, 0.0f + vOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);
            builder.addVertex(matrix, -halfW, height, 0).setColor(255, 220, 120, alpha).setUv(0.0f, 0.0f + vOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(EMISSIVE_LIGHT).setNormal(pose, 0, 0, 1);

            poseStack.popPose();
        }
    }

    public static void renderSingularitySphere(PoseStack poseStack, VertexConsumer builder, float radius, float time) {
        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 25.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(time * 18.0f));
        renderCrossStarBillboard(poseStack, builder, radius * 2.0f, 255);
        poseStack.popPose();
    }

}
