package com.frierenflight.spell;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class FlightCastData implements ICastDataSerializable {
    private boolean activating;

    public FlightCastData() {
        this.activating = false;
    }

    public FlightCastData(boolean activating) {
        this.activating = activating;
    }

    public boolean isActivating() {
        return activating;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.activating);
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
        this.activating = buffer.readBoolean();
    }

    @Override
    public void reset() {}

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("activating", this.activating);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.activating = tag.getBoolean("activating");
    }
}
