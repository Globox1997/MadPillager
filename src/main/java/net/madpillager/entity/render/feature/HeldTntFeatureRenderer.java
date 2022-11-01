package net.madpillager.entity.render.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.madpillager.entity.MadPillagerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3f;

//Unused
@Environment(EnvType.CLIENT)
public class HeldTntFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final HeldItemRenderer heldItemRenderer;

    public HeldTntFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {

        if (!livingEntity.getMainHandStack().isOf(Items.TNT))
            return;
        if ((LivingEntity) livingEntity instanceof MadPillagerEntity madPillager && madPillager.isRunning())
            return;
        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(105f));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(10.5f));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45f));
        matrixStack.translate(0.0D, -0.1D, 0.8D);
        matrixStack.scale(2.0f, 2.0f, 2.0f);

        this.heldItemRenderer.renderItem((LivingEntity) livingEntity, livingEntity.getMainHandStack(), ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, true, matrixStack, vertexConsumerProvider, i);

        matrixStack.pop();
    }

}
