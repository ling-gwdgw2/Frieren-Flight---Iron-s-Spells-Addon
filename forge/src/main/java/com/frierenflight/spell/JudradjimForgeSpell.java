package com.frierenflight.spell;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.JudradjimPillarForgeEntity;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class JudradjimForgeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(FrierenFlightCommon.MODID, "judradjim");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(16.0)
            .build();

    public JudradjimForgeSpell() {
        this.baseManaCost = 120;
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 55;
        this.spellPowerPerLevel = 12;
        this.castTime = 30; // 1.5 seconds charge
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
        return Optional.of(SoundEvents.LIGHTNING_BOLT_THUNDER);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            float range = 36.0f;
            HitResult hitResult = Utils.raycastForEntity(serverLevel, entity, range, true, 0.5f);
            Vec3 targetPos;
            if (hitResult.getType() != HitResult.Type.MISS) {
                targetPos = hitResult.getLocation();
            } else {
                Vec3 look = entity.getLookAngle();
                HitResult blockHit = Utils.raycastForBlock(serverLevel, entity.getEyePosition(), entity.getEyePosition().add(look.scale(range)), ClipContext.Fluid.NONE);
                if (blockHit.getType() != HitResult.Type.MISS) {
                    targetPos = blockHit.getLocation();
                } else {
                    // Snap downward to ground terrain if aiming towards open air/horizon
                    Vec3 airPoint = entity.getEyePosition().add(look.scale(range));
                    HitResult groundHit = Utils.raycastForBlock(serverLevel, airPoint, airPoint.subtract(0, 64, 0), ClipContext.Fluid.NONE);
                    if (groundHit.getType() != HitResult.Type.MISS) {
                        targetPos = groundHit.getLocation();
                    } else {
                        targetPos = airPoint;
                    }
                }
            }

            float damage = getSpellPower(spellLevel, entity); // Base 55 + (12 * level), scales to 60+ - 110+

            // Spawn the Colossal Black-Gold 3D Lightning Pillar Entity
            JudradjimPillarForgeEntity pillar = new JudradjimPillarForgeEntity(serverLevel, entity, damage, 7.5f);
            pillar.setPos(targetPos.x, targetPos.y, targetPos.z);
            serverLevel.addFreshEntity(pillar);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.radius", 7.5)
        );
    }
}
