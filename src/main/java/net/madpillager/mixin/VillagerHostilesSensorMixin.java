package net.madpillager.mixin;

import com.google.common.collect.ImmutableMap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.madpillager.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {

    @Shadow
    @Final
    @Mutable
    private static ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER;

    static {
        SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<?>, Float>builder().putAll(SQUARED_DISTANCES_FOR_DANGER).put(EntityInit.MAD_PILLAGER_ENTITY, Float.valueOf(10.0f)).build();
    }
}
