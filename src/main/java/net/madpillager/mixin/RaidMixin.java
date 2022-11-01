package net.madpillager.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.madpillager.init.ConfigInit;
import net.madpillager.init.EntityInit;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;

@Mixin(Raid.class)
public abstract class RaidMixin {

    @Shadow
    @Final
    @Mutable
    private ServerWorld world;

    @Inject(method = "spawnNextWave", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/village/raid/Raid;isSpawningExtraWave()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void spawnNextWaveMixin(BlockPos pos, CallbackInfo info, boolean bl, int i, LocalDifficulty localDifficulty, boolean bl2) {
        if (i >= ConfigInit.CONFIG.startingWave) {
            int spawnCount = ConfigInit.CONFIG.minSpawnCount;
            if (localDifficulty.getGlobalDifficulty() == Difficulty.HARD && ConfigInit.CONFIG.hardDifficultyIncrease)
                spawnCount++;
            if (bl2 && ConfigInit.CONFIG.bonusWaveIncrease)
                spawnCount++;

            for (int o = 0; o < spawnCount; o++) {
                RaiderEntity raiderEntity = EntityInit.MAD_PILLAGER_ENTITY.create(this.world);
                this.addRaider(i, raiderEntity, pos, false);
            }
        }
    }

    @Shadow
    public void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing) {
    }
}
