package com.frierenflight.spell;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.client.FlightClientHandler;
import com.frierenflight.registry.ModEffects;
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

import java.util.List;

public class FlightSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FrierenFlightMod.MODID, "flight");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(3.0)
            .build();

    public FlightSpell() {
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
        return new FlightCastData();
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            if (player.hasEffect(ModEffects.FLIGHT_EFFECT)) {
                player.removeEffect(ModEffects.FLIGHT_EFFECT);
                player.displayClientMessage(Component.translatable("message.frieren_flight.flight_disabled").withStyle(ChatFormatting.AQUA), true);
                playerMagicData.setAdditionalCastData(new FlightCastData(false));
            } else {
                player.addEffect(new MobEffectInstance(ModEffects.FLIGHT_EFFECT, 72000, spellLevel - 1, false, false, true));
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.displayClientMessage(Component.translatable("message.frieren_flight.flight_enabled").withStyle(ChatFormatting.LIGHT_PURPLE), true);
                playerMagicData.setAdditionalCastData(new FlightCastData(true));
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
        super.onClientCast(level, spellLevel, entity, castData);
        if (level.isClientSide && entity instanceof Player) {
            // Reliably trigger ascension animation only when activating flight
            if (castData instanceof FlightCastData flightCastData && flightCastData.isActivating()) {
                ClientTrigger.trigger();
            }
        }
    }

    // Isolated inner class preventing eager class verification crash on dedicated server
    private static class ClientTrigger {
        private static void trigger() {
            FlightClientHandler.triggerAscension();
        }
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        int drain = Math.max(5, 18 - ((spellLevel - 1) * 3));
        return List.of(
                Component.translatable("ui.frieren_flight.mana_drain", drain).withStyle(ChatFormatting.BLUE)
        );
    }
}
