package com.frierenflight.client.renderer;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.ReelseidenSlashForgeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ReelseidenSlashForgeRenderer extends EntityRenderer<ReelseidenSlashForgeEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FrierenFlightCommon.MODID, "textures/entity/reelseiden_slash.png");

    public ReelseidenSlashForgeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ReelseidenSlashForgeEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(ReelseidenSlashForgeEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(ReelseidenSlashForgeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        Vec3 delta = entity.getDeltaMovement();
        float yRot = (float) (Mth.atan2(delta.x, delta.z) * (180.0 / Math.PI));
        float xRot = (float) (Mth.atan2(delta.y, Math.sqrt(delta.x * delta.x + delta.z * delta.z)) * (180.0 / Math.PI));

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
        poseStack.translate(0, 0.25, 0);

        // Scale the crescent blade: wide 3.6m width, 2.6m depth
        poseStack.scale(3.6f, 1.0f, 2.6f);

        float age = entity.tickCount + partialTicks;
        float alpha = Mth.clamp(1.0f - (age / 14.0f) * 0.6f, 0.1f, 1.0f);
        int alphaByte = (int) (alpha * 255);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();
        Matrix3f norm = pose.normal();
        int light = 0xF000F0; // Full brightness illumination

        // Top Quad
        addVertex(builder, mat, norm, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 255, 255, 255, alphaByte, light, 0, 1, 0);
        addVertex(builder, mat, norm, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 255, 255, 255, alphaByte, light, 0, 1, 0);
        addVertex(builder, mat, norm, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 255, 255, 255, alphaByte, light, 0, 1, 0);
        addVertex(builder, mat, norm, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255, 255, 255, alphaByte, light, 0, 1, 0);

        // Bottom Quad
        addVertex(builder, mat, norm, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255, 255, 255, alphaByte, light, 0, -1, 0);
        addVertex(builder, mat, norm, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 255, 255, 255, alphaByte, light, 0, -1, 0);
        addVertex(builder, mat, norm, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 255, 255, 255, alphaByte, light, 0, -1, 0);
        addVertex(builder, mat, norm, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 255, 255, 255, alphaByte, light, 0, -1, 0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void addVertex(VertexConsumer builder, Matrix4f mat, Matrix3f norm,
                           float x, float y, float z,
                           float u, float v,
                           int r, int g, int b, int a,
                           int light,
                           float nx, float ny, float nz) {
        builder.vertex(mat, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(norm, nx, ny, nz)
                .endVertex();
    }
}
