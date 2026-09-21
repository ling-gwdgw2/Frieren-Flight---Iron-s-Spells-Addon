package com.frierenflight.spell;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.ExplosionCataclysmForgeEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class ExplosionForgeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(FrierenFlightCommon.MODID, "explosion");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(30.0)
            .build();

    public ExplosionForgeSpell() {
        this.baseManaCost = 200;
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 850; // 850 - 1150 devastating damage
        this.spellPowerPerLevel = 75;
        this.castTime = 90; // 4.5 seconds Megumin incantation (perfect sync with audio)
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
        return Optional.of(com.frierenflight.registry.ModForgeSounds.MEGUMIN_CHARGING.get());
    }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        super.onServerCastTick(level, spellLevel, entity, playerMagicData);

        // 1. Root caster firmly in place during chanting
        entity.setDeltaMovement(entity.getDeltaMovement().x * 0.1, Math.min(entity.getDeltaMovement().y, 0), entity.getDeltaMovement().z * 0.1);
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 6, false, false, false));

        int elapsed = playerMagicData.getCastDuration() - playerMagicData.getCastDurationRemaining();

        // 2. Spawn 3D Caster Magic Circle and Swirling Energy Rings
        if (level instanceof ServerLevel serverLevel) {
            if (elapsed == 1) {
                com.frierenflight.entity.ExplosionChantForgeEntity chantEntity = new com.frierenflight.entity.ExplosionChantForgeEntity(serverLevel, entity);
                chantEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
                serverLevel.addFreshEntity(chantEntity);
            }

            double angle = Math.toRadians(elapsed * 24.0);
            double r = 2.2;
            double px1 = entity.getX() + Math.cos(angle) * r;
            double pz1 = entity.getZ() + Math.sin(angle) * r;
            double px2 = entity.getX() - Math.cos(angle) * r;
            double pz2 = entity.getZ() - Math.sin(angle) * r;

            serverLevel.sendParticles(ParticleTypes.FLAME, px1, entity.getY() + 0.1, pz1, 1, 0, 0.05, 0, 0.01);
            serverLevel.sendParticles(ParticleTypes.WITCH, px2, entity.getY() + 0.1, pz2, 1, 0, 0.05, 0, 0.01);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX() + (Math.random() - 0.5) * 3, entity.getY() + 0.2, entity.getZ() + (Math.random() - 0.5) * 3, 1, 0, 0.1, 0, 0.02);
        }
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            float range = 96.0f;
            HitResult hitResult = Utils.raycastForEntity(serverLevel, entity, range, true, 0.6f);
            Vec3 targetPos;

            if (hitResult.getType() != HitResult.Type.MISS) {
                targetPos = hitResult.getLocation();
            } else {
                Vec3 look = entity.getLookAngle();
                HitResult blockHit = Utils.raycastForBlock(serverLevel, entity.getEyePosition(), entity.getEyePosition().add(look.scale(range)), ClipContext.Fluid.NONE);
                if (blockHit.getType() != HitResult.Type.MISS) {
                    targetPos = blockHit.getLocation();
                } else {
                    Vec3 airPoint = entity.getEyePosition().add(look.scale(range));
                    HitResult groundHit = Utils.raycastForBlock(serverLevel, airPoint, airPoint.subtract(0, 96, 0), ClipContext.Fluid.NONE);
                    if (groundHit.getType() != HitResult.Type.MISS) {
                        targetPos = groundHit.getLocation();
                    } else {
                        targetPos = airPoint;
                    }
                }
            }

            float damage = getSpellPower(spellLevel, entity); // 850 - 1150 Damage
            float blastRadius = 48.0f;

            // 1. Spawn True 3D Cataclysmic Detonation Entity (Mushroom Cloud + Shockwave)
            ExplosionCataclysmForgeEntity explosion = new ExplosionCataclysmForgeEntity(serverLevel, entity, damage, blastRadius);
            explosion.setPos(targetPos.x, targetPos.y, targetPos.z);
            serverLevel.addFreshEntity(explosion);

            // 2. DRAIN 100% MANA TO 0 (Megumin Lore Requirement)
            playerMagicData.setMana(0.0f);

            // 3. Megumin Fatigue & Collapse (Slowness & Weakness for 12 seconds)
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 240, 5, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 240, 3, false, true, true));

            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("§4§l[EX-PLO-SION!] §cมานาทั้งหมดถูกเผาผลาญจนกลายเป็น 0! §7(คุณหมดแรงล้มพับกับการระเบิดขั้นสูงสุด)"), false);
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.radius", 48.0),
                Component.translatable("ui.frieren_flight.mana_drain_all")
        );
    }
}
