package com.frierenflight.event;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.registry.ModForgeEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FrierenFlightCommon.MODID)
public class FlightForgeEventHandler {
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(ModForgeEffects.FLIGHT_EFFECT.get())) {
                event.setCanceled(true);
                event.setDistance(0);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isAlive() && !player.isDeadOrDying() && event.getAmount() > 0 && player.hasEffect(ModForgeEffects.FLOWER_BLESSING.get())) {
                event.setAmount(event.getAmount() * 0.5f); // 50% Damage Reduction!
            }
        }
    }
}
