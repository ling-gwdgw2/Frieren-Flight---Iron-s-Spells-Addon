package com.frierenflight.entity;

import com.frierenflight.registry.ModEntities;
import com.frierenflight.registry.ModSpells;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class ExplosionCataclysmEntity extends Entity {
    private LivingEntity owner;
    private UUID ownerUUID;
    private float damage = 950.0f;
    private float blastRadius = 55.0f;
    private int maxAge = 260; // 13.0 seconds cinematic 4-phase detonation

    public ExplosionCataclysmEntity(EntityType<? extends ExplosionCataclysmEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public ExplosionCataclysmEntity(Level level, LivingEntity owner, float damage, float blastRadius) {
        this(ModEntities.EXPLOSION_CATACLYSM.get(), level);
        this.owner = owner;
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
        }
        this.damage = damage;
        this.blastRadius = blastRadius;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            // Phase 1: Convergence Hum & Sky Pillar Activation (t = 1)
            if (tickCount == 1) {
                serverLevel.playSound(null, getX(), getY(), getZ(), com.frierenflight.registry.ModSounds.MEGUMIN_CHARGING.get(), SoundSource.PLAYERS, 10.0f, 1.0f);
            }

            // Phase 2: Stratosphere Plasma Beam Descent (t = 65)
            if (tickCount == 65) {
                serverLevel.playSound(null, getX(), getY(), getZ(), com.frierenflight.registry.ModSounds.MEGUMIN_RAY.get(), SoundSource.PLAYERS, 18.0f, 1.0f);
            }

            // Phase 4: MEGATON DETONATION (t = 100)
            if (tickCount == 100) {
                // Earth-shattering multi-layered audio broadcast across dimension
                serverLevel.playSound(null, getX(), getY(), getZ(), com.frierenflight.registry.ModSounds.MEGUMIN_BLAST.get(), SoundSource.PLAYERS, 25.0f, 1.0f);
                serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 18.0f, 0.75f);
                serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 16.0f, 0.5f);

                // Colossal Block Destruction (Crater Generation ~28 blocks wide, 14 blocks deep)
                int cr = 14;
                BlockPos centerPos = blockPosition();
                for (int dx = -cr; dx <= cr; dx++) {
                    for (int dy = -cr; dy <= cr / 2; dy++) {
                        for (int dz = -cr; dz <= cr; dz++) {
                            if (dx * dx + (dy * 1.4) * (dy * 1.4) + dz * dz <= cr * cr) {
                                BlockPos bpos = centerPos.offset(dx, dy, dz);
                                BlockState bstate = serverLevel.getBlockState(bpos);
                                if (!bstate.isAir() && bstate.getDestroySpeed(serverLevel, bpos) >= 0) {
                                    serverLevel.removeBlock(bpos, false);
                                }
                            }
                        }
                    }
                }

                // Scatter infernal fires around crater rim
                for (int fx = -cr - 3; fx <= cr + 3; fx += 2) {
                    for (int fz = -cr - 3; fz <= cr + 3; fz += 2) {
                        if (fx * fx + fz * fz <= (cr + 3) * (cr + 3) && random.nextFloat() < 0.28f) {
                            BlockPos fpos = centerPos.offset(fx, 0, fz);
                            for (int fy = 4; fy >= -cr; fy--) {
                                BlockPos testPos = fpos.above(fy);
                                if (serverLevel.getBlockState(testPos).isAir() && serverLevel.getBlockState(testPos.below()).isSolid()) {
                                    serverLevel.setBlock(testPos, Blocks.FIRE.defaultBlockState(), 3);
                                    break;
                                }
                            }
                        }
                    }
                }

                // Cataclysmic Entity Damage (850 - 1150+ Damage) & Shockwave Outward Launch
                AABB aabb = new AABB(getX() - blastRadius, getY() - 16, getZ() - blastRadius,
                        getX() + blastRadius, getY() + 48, getZ() + blastRadius);
                List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb,
                        e -> e != owner && e.isAlive() && (owner == null || !DamageSources.isFriendlyFireBetween(owner, e))
                );

                for (LivingEntity victim : targets) {
                    double dist = victim.distanceToSqr(position());
                    if (dist <= blastRadius * blastRadius) {
                        double distRatio = 1.0 - (Math.sqrt(dist) / blastRadius);
                        float entityDmg = (float) (damage * (0.35 + 0.65 * distRatio));

                        if (owner != null && ModSpells.EXPLOSION != null && ModSpells.EXPLOSION.get() != null) {
                            DamageSources.applyDamage(victim, entityDmg, ModSpells.EXPLOSION.get().getDamageSource(owner));
                        } else {
                            victim.hurt(damageSources().magic(), entityDmg);
                        }

                        // Massive explosive launch impulse
                        Vec3 knockback = victim.position().subtract(position());
                        if (knockback.lengthSqr() < 0.01) {
                            knockback = new Vec3(0, 1, 0);
                        } else {
                            knockback = knockback.normalize().add(0, 0.65, 0);
                        }
                        victim.setDeltaMovement(victim.getDeltaMovement().add(knockback.scale(3.2 * distRatio + 0.8)));
                        victim.hasImpulse = true;
                    }
                }
            }
        }

        // Client-side Ambient Mushroom Smoke & Rising Embers after detonation
        if (level().isClientSide && tickCount >= 100) {
            for (int i = 0; i < 4; i++) {
                double rad = random.nextDouble() * Math.PI * 2.0;
                double dist = random.nextDouble() * 14.0;
                double px = getX() + Math.cos(rad) * dist;
                double pz = getZ() + Math.sin(rad) * dist;
                double py = getY() + random.nextDouble() * 45.0;
                level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 0, 0.14, 0);
                if (random.nextFloat() < 0.4f) {
                    level().addParticle(ParticleTypes.FLAME, px, getY() + 1.0, pz, 0, 0.08, 0);
                }
            }
        }

        if (tickCount >= maxAge) {
            discard();
        }
    }

    public float getDamage() {
        return damage;
    }

    public float getBlastRadius() {
        return blastRadius;
    }

    public int getMaxAge() {
        return maxAge;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
        this.damage = tag.getFloat("Damage");
        this.blastRadius = tag.getFloat("BlastRadius");
        this.maxAge = tag.getInt("MaxAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
        tag.putFloat("Damage", damage);
        tag.putFloat("BlastRadius", blastRadius);
        tag.putInt("MaxAge", maxAge);
    }
}
