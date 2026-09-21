package com.frierenflight.entity;

import com.frierenflight.registry.ModForgeEntities;
import com.frierenflight.registry.ModForgeSpells;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.UUID;

public class JudradjimPillarForgeEntity extends Entity implements IEntityAdditionalSpawnData {
    private LivingEntity owner;
    private UUID ownerUUID;
    private float damage = 65.0f;
    private float blastRadius = 7.5f;
    private int maxAge = 25;

    public JudradjimPillarForgeEntity(EntityType<? extends JudradjimPillarForgeEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public JudradjimPillarForgeEntity(Level level, LivingEntity owner, float damage, float blastRadius) {
        this(ModForgeEntities.JUDRADJIM_PILLAR.get(), level);
        this.owner = owner;
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
        }
        this.damage = damage;
        this.blastRadius = blastRadius;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeFloat(blastRadius);
        buffer.writeInt(maxAge);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.blastRadius = buffer.readFloat();
        this.maxAge = buffer.readInt();
    }

    @Override
    public void tick() {
        super.tick();

        // 1. Initial Blast & Shockwave on Spawn (Tick 1)
        if (tickCount == 1) {
            if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
                // Thunder & Explosion sounds broadcast once from server to all nearby players
                serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.5f, 0.85f);
                serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 3.0f, 0.9f);

                // Shockwave damage & outward knockback
                AABB aabb = new AABB(getX() - blastRadius, getY() - 3, getZ() - blastRadius,
                        getX() + blastRadius, getY() + 8, getZ() + blastRadius);
                List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb,
                        e -> e != owner && e.isAlive() && (owner == null || !DamageSources.isFriendlyFireBetween(owner, e))
                );

                for (LivingEntity victim : targets) {
                    double dist = victim.distanceToSqr(position());
                    if (dist <= blastRadius * blastRadius) {
                        double distRatio = 1.0 - (Math.sqrt(dist) / blastRadius);
                        float entityDamage = (float) (damage * (0.6 + 0.4 * distRatio));

                        if (owner != null) {
                            DamageSources.applyDamage(victim, entityDamage, ModForgeSpells.JUDRADJIM.get().getDamageSource(owner));
                        } else {
                            victim.hurt(damageSources().magic(), entityDamage);
                        }

                        // Massive shockwave outward & upward knockback
                        Vec3 knockbackDir = victim.position().subtract(position());
                        if (knockbackDir.lengthSqr() < 0.01) {
                            knockbackDir = new Vec3(0, 1, 0);
                        } else {
                            knockbackDir = knockbackDir.normalize().add(0, 0.45, 0);
                        }
                        victim.setDeltaMovement(victim.getDeltaMovement().add(knockbackDir.scale(1.6)));
                        victim.hasImpulse = true;
                    }
                }
            }
        }

        // Clean discard across both client and server once lifespan completes
        if (tickCount >= maxAge) {
            discard();
        }
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(float blastRadius) {
        this.blastRadius = blastRadius;
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
