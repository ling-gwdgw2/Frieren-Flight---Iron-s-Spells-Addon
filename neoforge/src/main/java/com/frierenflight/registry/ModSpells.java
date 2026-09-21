package com.frierenflight.registry;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.spell.FlightSpell;
import com.frierenflight.spell.FlowerBedSpell;
import com.frierenflight.spell.JudradjimSpell;
import com.frierenflight.spell.ReelseidenSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, FrierenFlightMod.MODID);

    public static final DeferredHolder<AbstractSpell, FlightSpell> FLIGHT = SPELLS.register("flight", FlightSpell::new);
    public static final DeferredHolder<AbstractSpell, JudradjimSpell> JUDRADJIM = SPELLS.register("judradjim", JudradjimSpell::new);
    public static final DeferredHolder<AbstractSpell, ReelseidenSpell> REELSEIDEN = SPELLS.register("reelseiden", ReelseidenSpell::new);
    public static final DeferredHolder<AbstractSpell, FlowerBedSpell> FLOWER_BED = SPELLS.register("flower_bed", FlowerBedSpell::new);
    public static final DeferredHolder<AbstractSpell, com.frierenflight.spell.ExplosionSpell> EXPLOSION = SPELLS.register("explosion", com.frierenflight.spell.ExplosionSpell::new);

    public static void register(IEventBus bus) {
        SPELLS.register(bus);
    }
}
