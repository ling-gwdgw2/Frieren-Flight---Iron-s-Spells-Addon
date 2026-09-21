package com.frierenflight.spell;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class FlightForgeCastData implements ICastDataSerializable {
    private boolean activating;

    public FlightForgeCastData() {
        this.activating = false;
    }

    public FlightForgeCastData(boolean activating) {
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("activating", this.activating);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.activating = tag.getBoolean("activating");
    }
}
