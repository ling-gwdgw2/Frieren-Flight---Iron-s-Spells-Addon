package com.frierenflight.effect;

import com.frierenflight.registry.ModEffects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FlightMobEffect extends MagicMobEffect {
    public FlightMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x64D2FF);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.resetFallDistance();

            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }

            if (player.getAbilities().flying) {
                if (player.level().isClientSide) {
                    double x = player.getX();
                    double y = player.getY() + 0.05;
                    double z = player.getZ();
                    player.level().addParticle(ParticleTypes.END_ROD, x + (player.getRandom().nextDouble() - 0.5) * 0.4, y, z + (player.getRandom().nextDouble() - 0.5) * 0.4, 0, -0.01, 0);
                    player.level().addParticle(ParticleTypes.ENCHANT, x + (player.getRandom().nextDouble() - 0.5) * 0.6, y + 0.1, z + (player.getRandom().nextDouble() - 0.5) * 0.6, 0, 0.02, 0);
                } else {
                    if (!player.isCreative() && player.tickCount % 20 == 0) {
                        int drain = Math.max(5, 18 - (amplifier * 3));
                        MagicData magicData = MagicData.getPlayerMagicData(player);
                        float currentMana = magicData.getMana();
                        if (currentMana >= drain) {
                            magicData.setMana(currentMana - drain);
                        } else {
                            player.removeEffect(ModEffects.FLIGHT_EFFECT);
                            player.displayClientMessage(Component.translatable("message.frieren_flight.out_of_mana").withStyle(ChatFormatting.RED), true);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        super.onEffectRemoved(entity, amplifier);
        if (entity instanceof Player player) {
            player.resetFallDistance();
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().flying = false;
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
                if (!player.level().isClientSide && player.isAlive() && !player.isDeadOrDying()) {
                    if (player.getServer() != null) {
                        player.getServer().execute(() -> {
                            if (player.isAlive() && !player.isDeadOrDying()) {
                                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0, false, false, true));
                            }
                        });
                    } else {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0, false, false, true));
                    }
                }
            }
        }
    }
}
