package com.frierenflight.registry;

import com.frierenflight.FrierenFlightCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, FrierenFlightCommon.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MEGUMIN_CHANT = SOUND_EVENTS.register(
            "megumin_explosion_chant",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "megumin_explosion_chant"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> MEGUMIN_CHARGING = SOUND_EVENTS.register(
            "megumin_explosion_charging",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "megumin_explosion_charging"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> MEGUMIN_RAY = SOUND_EVENTS.register(
            "megumin_explosion_ray",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "megumin_explosion_ray"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> MEGUMIN_DETONATION = SOUND_EVENTS.register(
            "megumin_explosion_detonation",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "megumin_explosion_detonation"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> MEGUMIN_BLAST = SOUND_EVENTS.register(
            "megumin_explosion_blast",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "megumin_explosion_blast"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> MEGUMIN_EXPLOSION = SOUND_EVENTS.register(
            "megumin_explosion",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FrierenFlightCommon.MODID, "megumin_explosion"))
    );

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
