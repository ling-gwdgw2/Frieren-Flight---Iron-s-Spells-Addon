package com.frierenflight.spell;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.client.FlightForgeClientHandler;
import com.frierenflight.registry.ModForgeEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;

public class FlightForgeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(FrierenFlightCommon.MODID, FrierenFlightCommon.SPELL_ID);
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(3.0)
            .build();

    public FlightForgeSpell() {
        this.baseManaCost = 25;
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
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
    public ICastDataSerializable getEmptyCastData() {
        return new FlightForgeCastData();
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            if (player.hasEffect(ModForgeEffects.FLIGHT_EFFECT.get())) {
                player.removeEffect(ModForgeEffects.FLIGHT_EFFECT.get());
                player.displayClientMessage(Component.translatable("message.frieren_flight.flight_disabled").withStyle(ChatFormatting.AQUA), true);
                playerMagicData.setAdditionalCastData(new FlightForgeCastData(false));
            } else {
                player.addEffect(new MobEffectInstance(ModForgeEffects.FLIGHT_EFFECT.get(), 72000, spellLevel - 1, false, false, true));
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.displayClientMessage(Component.translatable("message.frieren_flight.flight_enabled").withStyle(ChatFormatting.LIGHT_PURPLE), true);
                playerMagicData.setAdditionalCastData(new FlightForgeCastData(true));
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
        super.onClientCast(level, spellLevel, entity, castData);
        if (level.isClientSide && entity instanceof Player) {
            if (castData instanceof FlightForgeCastData flightCastData && flightCastData.isActivating()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    FlightForgeClientHandler.triggerAscension();
                });
            }
        }
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        int drain = FrierenFlightCommon.getManaDrain(spellLevel - 1);
        return List.of(
                Component.translatable("ui.frieren_flight.mana_drain", drain).withStyle(ChatFormatting.BLUE)
        );
    }
}
