package com.frierenflight.registry;

import com.frierenflight.FrierenFlightCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModForgeSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FrierenFlightCommon.MODID);

    public static final RegistryObject<SoundEvent> MEGUMIN_CHANT = SOUND_EVENTS.register(
            "megumin_explosion_chant",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FrierenFlightCommon.MODID, "megumin_explosion_chant"))
    );

    public static final RegistryObject<SoundEvent> MEGUMIN_CHARGING = SOUND_EVENTS.register(
            "megumin_explosion_charging",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FrierenFlightCommon.MODID, "megumin_explosion_charging"))
    );

    public static final RegistryObject<SoundEvent> MEGUMIN_RAY = SOUND_EVENTS.register(
            "megumin_explosion_ray",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FrierenFlightCommon.MODID, "megumin_explosion_ray"))
    );

    public static final RegistryObject<SoundEvent> MEGUMIN_DETONATION = SOUND_EVENTS.register(
            "megumin_explosion_detonation",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FrierenFlightCommon.MODID, "megumin_explosion_detonation"))
    );

    public static final RegistryObject<SoundEvent> MEGUMIN_BLAST = SOUND_EVENTS.register(
            "megumin_explosion_blast",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FrierenFlightCommon.MODID, "megumin_explosion_blast"))
    );

    public static final RegistryObject<SoundEvent> MEGUMIN_EXPLOSION = SOUND_EVENTS.register(
            "megumin_explosion",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FrierenFlightCommon.MODID, "megumin_explosion"))
    );

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
