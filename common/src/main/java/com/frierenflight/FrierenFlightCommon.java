package com.frierenflight;

/**
 * Shared constants and utility calculations across all mod loaders and versions.
 */
public class FrierenFlightCommon {
    public static final String MODID = "frieren_flight";
    public static final String MOD_NAME = "Frieren Flight - Iron's Spells Addon";
    public static final String VERSION = "1.0.0";
    public static final String SPELL_ID = "flight";

    // Animation & Flight timing constants
    public static final int ASCENSION_TICKS = 60; // 3.0 seconds lift
    public static final int SETTLE_TICKS = 15;    // 0.75 seconds hover settle
    public static final int SLOW_FALLING_TICKS = 400; // 20 seconds safe descent

    /**
     * Calculate continuous mana drain per second based on spell level or amplifier.
     * Level 1 = 18 mana/sec
     * Level 2 = 15 mana/sec
     * Level 3 = 12 mana/sec
     * Level 4 = 9 mana/sec
     * Level 5 = 6 mana/sec (Min 5)
     */
    public static int getManaDrain(int levelIndex) {
        return Math.max(5, 18 - (levelIndex * 3));
    }
}
