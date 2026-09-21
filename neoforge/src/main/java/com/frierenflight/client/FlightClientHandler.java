package com.frierenflight.client;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.registry.ModEffects;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = FrierenFlightMod.MODID, value = Dist.CLIENT)
public class FlightClientHandler {
    public static final int ASCENSION_TICKS = 60; // 3.0 seconds
    public static final int SETTLE_TICKS = 15;    // 0.75 seconds smooth settle
    private static int currentAscensionTick = 0;
    private static boolean isAscending = false;
    private static CameraType originalCameraType = null;

    public static void triggerAscension() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!isAscending) {
            originalCameraType = mc.options.getCameraType();
        }
        isAscending = true;
        currentAscensionTick = 0;

        // Switch to third-person back for dramatic anime cinematic
        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);

        // Play gentle magical ascension sound
        mc.player.playSound(SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, 0.9f, 1.3f);
    }

    public static boolean isAscending() {
        return isAscending;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!isAscending) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || (currentAscensionTick > 5 && !player.hasEffect(ModEffects.FLIGHT_EFFECT))) {
            if (isAscending && originalCameraType != null) {
                mc.options.setCameraType(originalCameraType);
            }
            isAscending = false;
            originalCameraType = null;
            return;
        }

        currentAscensionTick++;

        if (currentAscensionTick <= ASCENSION_TICKS) {
            // 1. Ensure player stays flying
            player.getAbilities().flying = true;

            // 2. Smooth vertical velocity according to sine curve (lifting ~10 blocks over 60 ticks)
            float progress = (float) currentAscensionTick / (float) ASCENSION_TICKS;
            double targetVy = 0.2618 * Math.sin(Math.PI * progress);

            // Ceiling collision check to prevent clipping into roofs/caves (check solid block shape)
            BlockPos headPos = player.blockPosition().above(2);
            boolean ceilingHit = (player.verticalCollision && player.getDeltaMovement().y > 0)
                    || !mc.level.getBlockState(headPos).getCollisionShape(mc.level, headPos).isEmpty();
            if (ceilingHit) {
                targetVy = 0.0;
            }

            Vec3 vel = player.getDeltaMovement();
            player.setDeltaMovement(vel.x * 0.3, targetVy, vel.z * 0.3);
            player.hasImpulse = true;

            // 3. Magical Mana sparkles spiraling around player
            double angle = currentAscensionTick * 0.4;
            double radius = 0.8;
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();

            mc.level.addParticle(ParticleTypes.END_ROD, px + Math.cos(angle) * radius, py + 0.1, pz + Math.sin(angle) * radius, 0, 0.05, 0);
            mc.level.addParticle(ParticleTypes.ENCHANT, px + Math.cos(angle + Math.PI) * radius, py + 0.3, pz + Math.sin(angle + Math.PI) * radius, 0, 0.1, 0);
            mc.level.addParticle(ParticleTypes.GLOW, px, py + 0.05, pz, (Math.random() - 0.5) * 0.05, 0.02, (Math.random() - 0.5) * 0.05);

            // Subtle chime at mid-ascension
            if (currentAscensionTick == 30) {
                player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.7f, 1.4f);
            }
        } else if (currentAscensionTick <= ASCENSION_TICKS + SETTLE_TICKS) {
            // Settle phase: player now hovers in place gracefully while camera smoothly glides back
            player.getAbilities().flying = true;
            Vec3 vel = player.getDeltaMovement();
            player.setDeltaMovement(vel.x * 0.5, 0.0, vel.z * 0.5);
        } else {
            // Animation finished!
            isAscending = false;
            // Smoothly return to the player's original camera mode
            if (originalCameraType != null) {
                mc.options.setCameraType(originalCameraType);
            }
            originalCameraType = null;
        }
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        // Lock directional movement inputs during ascension so the player rises straight and majestically
        if (isAscending && currentAscensionTick <= ASCENSION_TICKS) {
            Input input = event.getInput();
            input.forwardImpulse = 0;
            input.leftImpulse = 0;
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
        }
    }

    @SubscribeEvent
    public static void onCameraDistance(CalculateDetachedCameraDistanceEvent event) {
        if (!isAscending) return;

        float partialTick = event.getCamera().getPartialTickTime();

        if (currentAscensionTick <= ASCENSION_TICKS) {
            // Smooth zoom-out from 4.0 blocks to 9.0 blocks
            float t = Mth.clamp((currentAscensionTick + partialTick) / (float) ASCENSION_TICKS, 0.0f, 1.0f);
            float smooth = t * t * (3.0f - 2.0f * t); // EaseInOut (smoothstep)
            event.setDistance(4.0f + 5.0f * smooth);
        } else if (currentAscensionTick <= ASCENSION_TICKS + SETTLE_TICKS) {
            // Smooth settle from 9.0 blocks back to 4.0 blocks
            float t = Mth.clamp((currentAscensionTick - ASCENSION_TICKS + partialTick) / (float) SETTLE_TICKS, 0.0f, 1.0f);
            float smooth = t * t * (3.0f - 2.0f * t);
            event.setDistance(9.0f - 5.0f * smooth);
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        if (isAscending && currentAscensionTick <= ASCENSION_TICKS) {
            float partialTick = (float) event.getPartialTick();
            float t = Mth.clamp((currentAscensionTick + partialTick) / (float) ASCENSION_TICKS, 0.0f, 1.0f);
            // Subtle cinematic FOV pulse (+7 degrees max)
            double fovBonus = 7.0 * Math.sin(Math.PI * t);
            event.setFOV(event.getFOV() + fovBonus);
        }
    }
}
