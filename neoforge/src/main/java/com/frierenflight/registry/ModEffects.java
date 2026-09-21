package com.frierenflight.registry;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.effect.FlightMobEffect;
import com.frierenflight.effect.FlowerBlessingMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, FrierenFlightMod.MODID);

    public static final DeferredHolder<MobEffect, FlightMobEffect> FLIGHT_EFFECT = EFFECTS.register("flight", FlightMobEffect::new);
    public static final DeferredHolder<MobEffect, FlowerBlessingMobEffect> FLOWER_BLESSING = EFFECTS.register("flower_blessing", FlowerBlessingMobEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
