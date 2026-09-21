package com.frierenflight;

import com.frierenflight.registry.ModEffects;
import com.frierenflight.registry.ModEntities;
import com.frierenflight.registry.ModSpells;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(FrierenFlightMod.MODID)
public class FrierenFlightMod {
    public static final String MODID = "frieren_flight";

    public FrierenFlightMod(IEventBus bus) {
        ModEffects.register(bus);
        ModEntities.register(bus);
        ModSpells.register(bus);
        com.frierenflight.registry.ModSounds.register(bus);
    }
}
