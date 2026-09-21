package com.frierenflight.registry;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.spell.FlightForgeSpell;
import com.frierenflight.spell.FlowerBedForgeSpell;
import com.frierenflight.spell.JudradjimForgeSpell;
import com.frierenflight.spell.ReelseidenForgeSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModForgeSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, FrierenFlightCommon.MODID);

    public static final RegistryObject<FlightForgeSpell> FLIGHT = SPELLS.register("flight", FlightForgeSpell::new);
    public static final RegistryObject<JudradjimForgeSpell> JUDRADJIM = SPELLS.register("judradjim", JudradjimForgeSpell::new);
    public static final RegistryObject<ReelseidenForgeSpell> REELSEIDEN = SPELLS.register("reelseiden", ReelseidenForgeSpell::new);
    public static final RegistryObject<FlowerBedForgeSpell> FLOWER_BED = SPELLS.register("flower_bed", FlowerBedForgeSpell::new);
    public static final RegistryObject<com.frierenflight.spell.ExplosionForgeSpell> EXPLOSION = SPELLS.register("explosion", com.frierenflight.spell.ExplosionForgeSpell::new);

    public static void register(IEventBus bus) {
        SPELLS.register(bus);
    }
}
