package com.frierenflight.registry;

import com.frierenflight.FrierenFlightCommon;
import com.frierenflight.entity.JudradjimPillarEntity;
import com.frierenflight.entity.ReelseidenSlashEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, FrierenFlightCommon.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ReelseidenSlashEntity>> REELSEIDEN_SLASH = ENTITIES.register("reelseiden_slash", () ->
            EntityType.Builder.<ReelseidenSlashEntity>of(ReelseidenSlashEntity::new, MobCategory.MISC)
                    .sized(3.0f, 0.6f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("reelseiden_slash")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<JudradjimPillarEntity>> JUDRADJIM_PILLAR = ENTITIES.register("judradjim_pillar", () ->
            EntityType.Builder.<JudradjimPillarEntity>of(JudradjimPillarEntity::new, MobCategory.MISC)
                    .sized(3.0f, 32.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("judradjim_pillar")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<com.frierenflight.entity.FlowerBedEntity>> FLOWER_BED = ENTITIES.register("flower_bed", () ->
            EntityType.Builder.<com.frierenflight.entity.FlowerBedEntity>of(com.frierenflight.entity.FlowerBedEntity::new, MobCategory.MISC)
                    .sized(40.0f, 4.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("flower_bed")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<com.frierenflight.entity.ExplosionCataclysmEntity>> EXPLOSION_CATACLYSM = ENTITIES.register("explosion_cataclysm", () ->
            EntityType.Builder.<com.frierenflight.entity.ExplosionCataclysmEntity>of(com.frierenflight.entity.ExplosionCataclysmEntity::new, MobCategory.MISC)
                    .sized(64.0f, 60.0f)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .build("explosion_cataclysm")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<com.frierenflight.entity.ExplosionChantEntity>> EXPLOSION_CHANT = ENTITIES.register("explosion_chant", () ->
            EntityType.Builder.<com.frierenflight.entity.ExplosionChantEntity>of(com.frierenflight.entity.ExplosionChantEntity::new, MobCategory.MISC)
                    .sized(8.0f, 3.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("explosion_chant")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
