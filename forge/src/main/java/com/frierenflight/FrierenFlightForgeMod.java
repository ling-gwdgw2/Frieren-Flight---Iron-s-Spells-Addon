package com.frierenflight;

import com.frierenflight.registry.ModForgeEffects;
import com.frierenflight.registry.ModForgeEntities;
import com.frierenflight.registry.ModForgeSpells;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FrierenFlightCommon.MODID)
public class FrierenFlightForgeMod {
    public FrierenFlightForgeMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModForgeEffects.register(bus);
        ModForgeEntities.register(bus);
        ModForgeSpells.register(bus);
        com.frierenflight.registry.ModForgeSounds.register(bus);
    }
}
