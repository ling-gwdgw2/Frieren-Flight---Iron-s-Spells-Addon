package com.frierenflight.registry;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.JudradjimPillarForgeEntity;
import com.frierenflight.entity.ReelseidenSlashForgeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModForgeEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FrierenFlightCommon.MODID);

    public static final RegistryObject<EntityType<ReelseidenSlashForgeEntity>> REELSEIDEN_SLASH = ENTITIES.register("reelseiden_slash", () ->
            EntityType.Builder.<ReelseidenSlashForgeEntity>of(ReelseidenSlashForgeEntity::new, MobCategory.MISC)
                    .sized(3.0f, 0.6f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("reelseiden_slash")
    );

    public static final RegistryObject<EntityType<JudradjimPillarForgeEntity>> JUDRADJIM_PILLAR = ENTITIES.register("judradjim_pillar", () ->
            EntityType.Builder.<JudradjimPillarForgeEntity>of(JudradjimPillarForgeEntity::new, MobCategory.MISC)
                    .sized(3.0f, 32.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("judradjim_pillar")
    );

    public static final RegistryObject<EntityType<com.frierenflight.entity.FlowerBedForgeEntity>> FLOWER_BED = ENTITIES.register("flower_bed", () ->
            EntityType.Builder.<com.frierenflight.entity.FlowerBedForgeEntity>of(com.frierenflight.entity.FlowerBedForgeEntity::new, MobCategory.MISC)
                    .sized(40.0f, 4.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("flower_bed")
    );

    public static final RegistryObject<EntityType<com.frierenflight.entity.ExplosionCataclysmForgeEntity>> EXPLOSION_CATACLYSM = ENTITIES.register("explosion_cataclysm", () ->
            EntityType.Builder.<com.frierenflight.entity.ExplosionCataclysmForgeEntity>of(com.frierenflight.entity.ExplosionCataclysmForgeEntity::new, MobCategory.MISC)
                    .sized(64.0f, 60.0f)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .build("explosion_cataclysm")
    );

    public static final RegistryObject<EntityType<com.frierenflight.entity.ExplosionChantForgeEntity>> EXPLOSION_CHANT = ENTITIES.register("explosion_chant", () ->
            EntityType.Builder.<com.frierenflight.entity.ExplosionChantForgeEntity>of(com.frierenflight.entity.ExplosionChantForgeEntity::new, MobCategory.MISC)
                    .sized(8.0f, 3.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("explosion_chant")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
