package com.frierenflight.spell;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.registry.ModEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class FlowerBedSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FrierenFlightMod.MODID, "flower_bed");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20.0)
            .build();

    public FlowerBedSpell() {
        this.baseManaCost = 65;
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 5; // Base 5.0 HP/sec recovery
        this.spellPowerPerLevel = 1;
        this.castTime = 20; // 1.0 second serene channel
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Optional<net.minecraft.sounds.SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.AMETHYST_BLOCK_CHIME);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            float radius = 64.0f;
            int duration = 1000 + (spellLevel - 1) * 50; // 50.0 to 60.0 seconds
            int amplifier = spellLevel - 1; // Level 1: 5.0 HP/s, Level 5: 10.0 HP/s

            // 1. Serene cast chimes
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0f, 1.4f);
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FLOWERING_AZALEA_PLACE, SoundSource.PLAYERS, 1.6f, 1.0f);

            // 2. Spawn 3D Visual & Botanical Entity (Handles sequential ripple bloom wave, sunbeams, petals, sanctuary buff & monster decay)
            com.frierenflight.entity.FlowerBedEntity flowerBed = new com.frierenflight.entity.FlowerBedEntity(serverLevel, entity.getX(), entity.getY(), entity.getZ(), radius, duration, spellLevel);
            serverLevel.addFreshEntity(flowerBed);

            // 3. Grant initial "Flower Blessing" Buff to Caster and Nearby PLAYERS in 64-block radius
            AABB aabb = entity.getBoundingBox().inflate(radius);
            double radiusSq = radius * radius;
            List<Player> players = serverLevel.getEntitiesOfClass(Player.class, aabb,
                    p -> p.isAlive() && !p.isDeadOrDying() && !p.isSpectator() && p.distanceToSqr(entity) <= radiusSq
            );

            for (Player playerTarget : players) {
                playerTarget.addEffect(new MobEffectInstance(ModEffects.FLOWER_BLESSING, duration, amplifier, false, true, true));
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        float healPerSec = 5.0f + (spellLevel - 1) * 1.25f; // 5.0 to 10.0 HP/sec
        float manaPerSec = 15.0f + (spellLevel - 1) * 3.5f; // 15 to 29 Mana/sec
        return List.of(
                Component.translatable("ui.frieren_flight.healing_per_sec", Utils.stringTruncation(healPerSec, 1)),
                Component.translatable("ui.frieren_flight.mana_per_sec", Utils.stringTruncation(manaPerSec, 1)),
                Component.translatable("ui.frieren_flight.damage_reduction"),
                Component.translatable("ui.frieren_flight.monster_hp_drain"),
                Component.translatable("ui.irons_spellbooks.radius", 64.0),
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(1000 + (spellLevel - 1) * 50, 1))
        );
    }
}
