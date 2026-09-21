package com.frierenflight.spell;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.ReelseidenSlashForgeEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ReelseidenForgeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(FrierenFlightCommon.MODID, "reelseiden");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(6.0)
            .build();

    public ReelseidenForgeSpell() {
        this.baseManaCost = 50;
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 38;
        this.spellPowerPerLevel = 8;
        this.castTime = 0; // Instant silent cast
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
        return CastType.INSTANT;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            Vec3 look = entity.getLookAngle();
            Vec3 eyePos = entity.getEyePosition();
            Vec3 spawnPos = eyePos.add(look.scale(0.8));
            float damage = getSpellPower(spellLevel, entity); // Base 38 + (8 * level), with Evocation power scales to 40+ - 75+!

            // 1. Silent cutting sound effect
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.5f, 1.8f);
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.2f, 2.0f);

            // 2. Spawn Custom 3D Crescent Blade Entity
            ReelseidenSlashForgeEntity slash = new ReelseidenSlashForgeEntity(serverLevel, entity, damage);
            slash.setPos(spawnPos.x, spawnPos.y - 0.1, spawnPos.z);
            slash.setDeltaMovement(look.scale(2.4)); // Lightning-fast forward cleave
            serverLevel.addFreshEntity(slash);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.distance", 30)
        );
    }
}
