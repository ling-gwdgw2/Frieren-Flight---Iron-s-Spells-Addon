package com.frierenflight.entity;

import com.frierenflight.registry.ModForgeEntities;
import com.frierenflight.registry.ModForgeSpells;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ReelseidenSlashForgeEntity extends Entity implements IEntityAdditionalSpawnData {
    private LivingEntity owner;
    private UUID ownerUUID;
    private float damage = 42.0f;
    private int lifetime = 14;
    private final Set<Integer> hitEntityIds = new HashSet<>();

    public ReelseidenSlashForgeEntity(EntityType<? extends ReelseidenSlashForgeEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public ReelseidenSlashForgeEntity(Level level, LivingEntity owner, float damage) {
        this(ModForgeEntities.REELSEIDEN_SLASH.get(), level);
        this.owner = owner;
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
        }
        this.damage = damage;
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
        Vec3 m = getDeltaMovement();
        buffer.writeDouble(m.x);
        buffer.writeDouble(m.y);
        buffer.writeDouble(m.z);
        buffer.writeFloat(damage);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        setDeltaMovement(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.damage = buffer.readFloat();
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 movement = getDeltaMovement();
        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);

        // Discard after lifetime on both client and server
        if (tickCount >= lifetime) {
            discard();
            return;
        }

        BlockPos currentPos = blockPosition();

        if (level().isClientSide) {
            // Immediate client-side dissipation upon hitting solid geometry
            if (level().getBlockState(currentPos).isSolid()) {
                discard();
            }
            return;
        }

        // Server-side block collision
        if (level().getBlockState(currentPos).isSolid()) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2f, 1.8f);
            discard();
            return;
        }

        // Wide cleaving hit detection
        AABB hitBox = getBoundingBox().inflate(1.2, 0.4, 1.2);
        List<LivingEntity> victims = level().getEntitiesOfClass(LivingEntity.class, hitBox,
                e -> e != owner && e.isAlive() && !hitEntityIds.contains(e.getId()) && (owner == null || !DamageSources.isFriendlyFireBetween(owner, e))
        );

        for (LivingEntity victim : victims) {
            hitEntityIds.add(victim.getId());

            // 1. Pierce & Disable Shield
            if (victim.isBlocking()) {
                if (victim instanceof Player targetPlayer) {
                    targetPlayer.disableShield(true);
                }
                level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.5f, 1.2f);
            }

            // 2. 100% Armor-Piercing True Spell Damage
            if (owner != null) {
                DamageSources.applyDamage(victim, damage, ModForgeSpells.REELSEIDEN.get().getDamageSource(owner));
            } else {
                victim.hurt(damageSources().magic(), damage);
            }
        }
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public LivingEntity getOwner() {
        if (owner == null && ownerUUID != null && level() instanceof ServerLevel sl) {
            Entity entity = sl.getEntity(ownerUUID);
            if (entity instanceof LivingEntity le) {
                owner = le;
            }
        }
        return owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
        this.damage = tag.getFloat("Damage");
        this.lifetime = tag.getInt("Lifetime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
        tag.putFloat("Damage", damage);
        tag.putInt("Lifetime", lifetime);
    }
}
