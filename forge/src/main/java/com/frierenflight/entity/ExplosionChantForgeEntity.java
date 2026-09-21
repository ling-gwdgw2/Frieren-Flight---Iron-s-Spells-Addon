package com.frierenflight.entity;

import com.frierenflight.registry.ModForgeEntities;
import com.frierenflight.registry.ModForgeSpells;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class ExplosionChantForgeEntity extends Entity implements IEntityAdditionalSpawnData {
    private LivingEntity caster;
    private UUID casterUUID;
    private int maxAge = 95; // 4.75 seconds (synced with 4.5s castTime)

    public ExplosionChantForgeEntity(EntityType<? extends ExplosionChantForgeEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public ExplosionChantForgeEntity(Level level, LivingEntity caster) {
        this(ModForgeEntities.EXPLOSION_CHANT.get(), level);
        this.caster = caster;
        if (caster != null) {
            this.casterUUID = caster.getUUID();
        }
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
        buffer.writeInt(maxAge);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.maxAge = buffer.readInt();
    }

    @Override
    public void tick() {
        super.tick();

        if (caster != null && caster.isAlive()) {
            setPos(caster.getX(), caster.getY(), caster.getZ());
        } else if (!level().isClientSide && casterUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity found = serverLevel.getEntity(casterUUID);
            if (found instanceof LivingEntity living) {
                this.caster = living;
                setPos(caster.getX(), caster.getY(), caster.getZ());
            }
        }

        if (!level().isClientSide) {
            if (caster == null || !caster.isAlive()) {
                discard();
                return;
            }
            if (caster instanceof ServerPlayer player) {
                MagicData magicData = MagicData.getPlayerMagicData(player);
                if (!magicData.isCasting() || !ModForgeSpells.EXPLOSION.get().getSpellResource().equals(magicData.getCastingSpellId())) {
                    discard();
                    return;
                }
            }
            if (tickCount >= maxAge) {
                discard();
            }
        }
    }

    public LivingEntity getCaster() {
        return caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Caster")) {
            this.casterUUID = tag.getUUID("Caster");
        }
        this.maxAge = tag.getInt("MaxAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (casterUUID != null) {
            tag.putUUID("Caster", casterUUID);
        }
        tag.putInt("MaxAge", maxAge);
    }
}
