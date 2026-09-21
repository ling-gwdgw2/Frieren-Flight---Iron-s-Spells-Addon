package com.frierenflight.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FlowerBlessingForgeMobEffect extends MagicMobEffect {
    public FlowerBlessingForgeMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x4AA3DF); // Celestial Blue Moonweed Color
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            // Ethereal floating flower petals, spore blossoms, and emerald glimmers
            if (entity.getRandom().nextFloat() < 0.45f) {
                double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 1.4;
                double y = entity.getY() + entity.getRandom().nextDouble() * (entity.getBbHeight() + 0.4);
                double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 1.4;
                entity.level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0, 0.02, 0);
                if (entity.getRandom().nextFloat() < 0.25f) {
                    entity.level().addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0.04, 0);
                }
            }
        } else {
            if (!entity.isAlive() || entity.isDeadOrDying()) {
                return;
            }
            // 1. Rapid HP Recovery (5.0 - 10.0 HP per second: applied as 2.5 - 5.0 HP every 10 ticks)
            if (entity.tickCount % 10 == 0) {
                float healAmount = 2.5f + (amplifier * 0.625f);
                entity.heal(healAmount);
            }

            // 2. Rapid Mana Recovery (+15 to +29 Mana per second)
            if (entity instanceof Player player && entity.tickCount % 20 == 0) {
                MagicData magicData = MagicData.getPlayerMagicData(player);
                float manaAmount = 15.0f + (amplifier * 3.5f);
                magicData.addMana(manaAmount);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
