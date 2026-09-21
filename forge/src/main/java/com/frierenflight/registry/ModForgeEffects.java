package com.frierenflight.registry;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.effect.FlightForgeMobEffect;
import com.frierenflight.effect.FlowerBlessingForgeMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModForgeEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, FrierenFlightCommon.MODID);

    public static final RegistryObject<MobEffect> FLIGHT_EFFECT = EFFECTS.register("flight", FlightForgeMobEffect::new);
    public static final RegistryObject<MobEffect> FLOWER_BLESSING = EFFECTS.register("flower_blessing", FlowerBlessingForgeMobEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
