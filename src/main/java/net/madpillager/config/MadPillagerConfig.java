package net.madpillager.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "madpillager")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class MadPillagerConfig implements ConfigData {

    @Comment("Wave count when mad pillagers spawn")
    public int startingWave = 2;
    @Comment("Minimum mad pillager spawns")
    public int minSpawnCount = 1;
    @Comment("If bonus waves increase count")
    public boolean bonusWaveIncrease = true;
    @Comment("If hard difficulty increase count")
    public boolean hardDifficultyIncrease = true;
    @Comment("Minimum time in ticks to explode tnt")
    public int tntStartTimer = 80;
    @Comment("Random extra ticks to tnt timer")
    public int randomExtraTicks = 80;
    public float explosionPower = 4.0f;
    @Comment("time in ticks for fresh tnt")
    public int cooldown = 100;
    public boolean tntRotation = false;
    @ConfigEntry.Gui.RequiresRestart
    public float tntSize = 0.8f;

}