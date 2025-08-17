package com.danny.treasurechests;

import com.danny.treasurechests.Animation.AnimationInfo;

import java.util.List;

public class LootTier {

    private final String name;
    private final String displayName;
    private final String headTexture;
    private final double chance;
    private final SoundInfo soundInfo;
    private final List<LootItem> items;
    private final AnimationInfo spawnAnimation;
    private final AnimationInfo despawnAnimation;

    public LootTier(String name, String displayName, String headTexture, double chance, SoundInfo soundInfo, List<LootItem> items, AnimationInfo spawnAnimation, AnimationInfo despawnAnimation) {
        this.name = name;
        this.displayName = displayName;
        this.headTexture = headTexture;
        this.chance = chance;
        this.soundInfo = soundInfo;
        this.items = items;
        this.spawnAnimation = spawnAnimation;
        this.despawnAnimation = despawnAnimation;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHeadTexture() {
        return headTexture;
    }

    public double getChance() {
        return chance;
    }

    public SoundInfo getSoundInfo() {
        return soundInfo;
    }

    public List<LootItem> getItems() {
        return items;
    }

    public AnimationInfo getSpawnAnimation() {
        return spawnAnimation;
    }

    public AnimationInfo getDespawnAnimation() {
        return despawnAnimation;
    }
}
