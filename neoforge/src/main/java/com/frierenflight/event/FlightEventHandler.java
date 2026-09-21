package com.frierenflight.event;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.registry.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = FrierenFlightMod.MODID)
public class FlightEventHandler {
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(ModEffects.FLIGHT_EFFECT)) {
                event.setCanceled(true);
                event.setDistance(0);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isAlive() && !player.isDeadOrDying() && event.getAmount() > 0 && player.hasEffect(ModEffects.FLOWER_BLESSING)) {
                event.setAmount(event.getAmount() * 0.5f); // 50% Damage Reduction!
            }
        }
    }
}
