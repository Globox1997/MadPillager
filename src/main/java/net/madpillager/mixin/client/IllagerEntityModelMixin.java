package net.madpillager.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.madpillager.entity.MadPillagerEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
@Mixin(IllagerEntityModel.class)
public abstract class IllagerEntityModelMixin<T extends IllagerEntity> extends SinglePartEntityModel<T> {

    @Shadow
    @Mutable
    @Final
    private ModelPart rightArm;
    @Shadow
    @Mutable
    @Final
    private ModelPart leftArm;

    @Inject(method = "setAngles", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/IllagerEntity;getState()Lnet/minecraft/entity/mob/IllagerEntity$State;"), cancellable = true)
    private void setAnglesMixin(T illagerEntity, float f, float g, float h, float i, float j, CallbackInfo info) {
        if (illagerEntity instanceof MadPillagerEntity madPillagerEntity && madPillagerEntity.getMainHandStack().isOf(Items.TNT)) {
            this.rightArm.pitch = 3.14f;
            this.leftArm.pitch = 3.14f;
            info.cancel();
        }
    }

}
