package com.frierenflight.entity;

import com.frierenflight.registry.ModEntities;
import com.frierenflight.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FlowerBedEntity extends Entity {
    private float maxRadius = 64.0f;
    private int maxAge = 1200; // 60.0 seconds sustained field
    private int spellLevel = 1;
    public static final int TOTAL_WAVE_TICKS = 35; // ~1.75 seconds wave expansion

    public FlowerBedEntity(EntityType<? extends FlowerBedEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public FlowerBedEntity(Level level, double x, double y, double z, float maxRadius) {
        this(level, x, y, z, maxRadius, 1200, 1);
    }

    public FlowerBedEntity(Level level, double x, double y, double z, float maxRadius, int maxAge) {
        this(level, x, y, z, maxRadius, maxAge, 1);
    }

    public FlowerBedEntity(Level level, double x, double y, double z, float maxRadius, int maxAge, int spellLevel) {
        this(ModEntities.FLOWER_BED.get(), level);
        setPos(x, y, z);
        this.maxRadius = maxRadius;
        this.maxAge = maxAge;
        this.spellLevel = spellLevel;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();

        // 1. Server-side sequential bloom wave & sanctuary buff refresh
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            if (tickCount <= TOTAL_WAVE_TICKS) {
                growBloomWaveRing(serverLevel, tickCount);
            }

            // Periodic sanctuary refresh for PLAYERS ONLY & -2% Max HP decay for MONSTERS (every 1 second)
            if (tickCount % 20 == 0) {
                int amplifier = Math.max(0, spellLevel - 1);
                AABB aabb = getBoundingBox().inflate(maxRadius);
                double maxRadiusSq = maxRadius * maxRadius;

                // 1. Refresh Flower Blessing on PLAYERS ONLY within 64 blocks
                List<Player> players = serverLevel.getEntitiesOfClass(Player.class, aabb,
                        p -> p.isAlive() && !p.isDeadOrDying() && !p.isSpectator() && p.distanceToSqr(this) <= maxRadiusSq
                );
                for (Player playerTarget : players) {
                    playerTarget.addEffect(new MobEffectInstance(ModEffects.FLOWER_BLESSING, 60, amplifier, false, true, true));
                }

                // 2. Decay MONSTERS inside 64 blocks: -2% Max HP every second
                List<LivingEntity> monsters = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb,
                        e -> e.isAlive() && !e.isDeadOrDying() && !e.isSpectator() && (e instanceof Enemy || e instanceof Monster) && e.distanceToSqr(this) <= maxRadiusSq
                );
                for (LivingEntity monster : monsters) {
                    float drainAmount = Math.max(1.0f, monster.getMaxHealth() * 0.02f);
                    monster.invulnerableTime = 0;
                    monster.hurt(damageSources().magic(), drainAmount);
                    serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, monster.getX(), monster.getY() + monster.getBbHeight() * 0.5, monster.getZ(), 3, 0.2, 0.2, 0.2, 0.02);
                }
            }
        }

        // 2. Client-side ambient ascending mana motes & petals
        if (level().isClientSide) {
            float waveProgress = Math.min(1.0f, (float) tickCount / (float) TOTAL_WAVE_TICKS);
            float currentR = Math.min(maxRadius, 1.0f + (maxRadius - 1.0f) * waveProgress);

            for (int i = 0; i < 5; i++) {
                double ang = random.nextDouble() * Math.PI * 2.0;
                double dist = Math.sqrt(random.nextDouble()) * currentR;
                double px = getX() + Math.cos(ang) * dist;
                double pz = getZ() + Math.sin(ang) * dist;

                // Ascending mana motes (gentle vertical float into the sky)
                level().addParticle(ParticleTypes.END_ROD, px, getY() + 0.1, pz, 0, 0.015 + random.nextDouble() * 0.02, 0);
                level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, px, getY() + 0.2 + random.nextDouble() * 1.5, pz, 0, 0.01, 0);
                if (random.nextFloat() < 0.35f) {
                    level().addParticle(ParticleTypes.GLOW, px, getY() + 0.25, pz, 0, 0.025, 0);
                }
                if (random.nextFloat() < 0.25f) {
                    level().addParticle(ParticleTypes.CHERRY_LEAVES, px, getY() + 0.4 + random.nextDouble() * 2.0, pz, 0.01, -0.005, 0.01);
                }
            }
        }

        if (tickCount >= maxAge) {
            discard();
        }
    }

    private void growBloomWaveRing(ServerLevel serverLevel, int currentTick) {
        float prevR = (currentTick - 1) * (maxRadius / (float) TOTAL_WAVE_TICKS);
        float currR = currentTick * (maxRadius / (float) TOTAL_WAVE_TICKS);
        double prevRSq = prevR * prevR;
        double currRSq = currR * currR;

        BlockPos center = blockPosition();
        RandomSource rng = serverLevel.getRandom();

        int minX = (int) Math.floor(-currR);
        int maxX = (int) Math.ceil(currR);

        for (int dx = minX; dx <= maxX; dx++) {
            double dxSq = dx * dx;
            if (dxSq > currRSq) continue;

            int maxDz = (int) Math.floor(Math.sqrt(currRSq - dxSq));
            int minDz = -maxDz;

            for (int dz = minDz; dz <= maxDz; dz++) {
                double distSq = dxSq + dz * dz;
                if (distSq < prevRSq) continue;

                // 24% flower density for a natural picturesque meadow
                if (rng.nextFloat() > 0.24f) continue;

                for (int dy = 4; dy >= -5; dy--) {
                    BlockPos targetPos = center.offset(dx, dy, dz);
                    BlockState currentState = serverLevel.getBlockState(targetPos);

                    if (currentState.isAir() || currentState.canBeReplaced()) {
                        Block flower = selectClusteredFlower(rng, dx, dz, distSq);
                        BlockState flowerState = flower.defaultBlockState();

                        if (flowerState.canSurvive(serverLevel, targetPos)) {
                            serverLevel.setBlock(targetPos, flowerState, 2);
                            break;
                        } else if (serverLevel.getBlockState(targetPos.below()).isSolid() && rng.nextFloat() < 0.22f) {
                            // On stone/rock surfaces, verdant moss carpets sprout
                            serverLevel.setBlock(targetPos, Blocks.MOSS_CARPET.defaultBlockState(), 2);
                            break;
                        }
                    }
                }
            }
        }

        // Leading edge wavefront particles
        int particleSteps = Math.max(16, (int) (currR * 2.2));
        double angleStep = (2.0 * Math.PI) / particleSteps;
        for (int p = 0; p < particleSteps; p++) {
            double angle = p * angleStep;
            double px = getX() + Math.cos(angle) * currR;
            double pz = getZ() + Math.sin(angle) * currR;
            serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, px, getY() + 0.3, pz, 1, 0.1, 0.1, 0.1, 0.01);
            if (rng.nextFloat() < 0.35f) {
                serverLevel.sendParticles(ParticleTypes.GLOW, px, getY() + 0.25, pz, 1, 0.05, 0.1, 0.05, 0.01);
            }
        }

        // Melodic blooming audio progression
        if (currentTick % 7 == 0 || currentTick == 1 || currentTick == TOTAL_WAVE_TICKS) {
            float pitch = 1.0f + (currentTick / (float) TOTAL_WAVE_TICKS) * 0.40f;
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.3f, pitch);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.PLAYERS, 0.9f, pitch * 0.9f);
        }
    }

    private Block selectClusteredFlower(RandomSource rng, int dx, int dz, double distSq) {
        // 1. Center of the field (within 16 blocks) is predominantly Blue Moonweed
        if (distSq < 16.0 * 16.0) {
            float roll = rng.nextFloat();
            if (roll < 0.60f) return Blocks.CORNFLOWER;
            if (roll < 0.88f) return Blocks.BLUE_ORCHID;
            return Blocks.LILY_OF_THE_VALLEY; // Pure white bells
        }

        // 2. Spatial noise clustering for natural bouquet patches
        double noise = Math.sin(dx * 0.18) * Math.cos(dz * 0.18) + Math.sin((dx + dz) * 0.11) * 0.5;
        if (noise > 0.40) {
            // White bouquet patch
            return rng.nextFloat() < 0.65f ? Blocks.LILY_OF_THE_VALLEY : Blocks.OXEYE_DAISY;
        } else if (noise < -0.40) {
            // Lavender & pink floral accents
            return rng.nextFloat() < 0.60f ? Blocks.ALLIUM : Blocks.PINK_TULIP;
        } else {
            // Main Blue Moonweed meadow
            return rng.nextFloat() < 0.60f ? Blocks.CORNFLOWER : Blocks.BLUE_ORCHID;
        }
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getSpellLevel() {
        return spellLevel;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.maxRadius = tag.getFloat("MaxRadius");
        this.maxAge = tag.getInt("MaxAge");
        this.spellLevel = tag.getInt("SpellLevel");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("MaxRadius", maxRadius);
        tag.putInt("MaxAge", maxAge);
        tag.putInt("SpellLevel", spellLevel);
    }
}
