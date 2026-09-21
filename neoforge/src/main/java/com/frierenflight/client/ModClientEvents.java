package com.frierenflight.client;

import com.frierenflight.FrierenFlightMod;
import com.frierenflight.client.renderer.JudradjimPillarRenderer;
import com.frierenflight.client.renderer.ReelseidenSlashRenderer;
import com.frierenflight.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = FrierenFlightMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.REELSEIDEN_SLASH.get(), ReelseidenSlashRenderer::new);
        event.registerEntityRenderer(ModEntities.JUDRADJIM_PILLAR.get(), JudradjimPillarRenderer::new);
        event.registerEntityRenderer(ModEntities.FLOWER_BED.get(), com.frierenflight.client.renderer.FlowerBedRenderer::new);
        event.registerEntityRenderer(ModEntities.EXPLOSION_CATACLYSM.get(), com.frierenflight.client.renderer.ExplosionCataclysmRenderer::new);
        event.registerEntityRenderer(ModEntities.EXPLOSION_CHANT.get(), com.frierenflight.client.renderer.ExplosionChantRenderer::new);
    }
}
