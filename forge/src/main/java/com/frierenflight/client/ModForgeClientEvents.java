package com.frierenflight.client;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.client.renderer.JudradjimPillarForgeRenderer;
import com.frierenflight.client.renderer.ReelseidenSlashForgeRenderer;
import com.frierenflight.registry.ModForgeEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FrierenFlightCommon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModForgeClientEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModForgeEntities.REELSEIDEN_SLASH.get(), ReelseidenSlashForgeRenderer::new);
        event.registerEntityRenderer(ModForgeEntities.JUDRADJIM_PILLAR.get(), JudradjimPillarForgeRenderer::new);
        event.registerEntityRenderer(ModForgeEntities.FLOWER_BED.get(), com.frierenflight.client.renderer.FlowerBedForgeRenderer::new);
        event.registerEntityRenderer(ModForgeEntities.EXPLOSION_CATACLYSM.get(), com.frierenflight.client.renderer.ExplosionCataclysmForgeRenderer::new);
        event.registerEntityRenderer(ModForgeEntities.EXPLOSION_CHANT.get(), com.frierenflight.client.renderer.ExplosionChantForgeRenderer::new);
    }
}
