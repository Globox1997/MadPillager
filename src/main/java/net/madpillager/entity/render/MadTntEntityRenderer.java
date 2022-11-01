package net.madpillager.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.madpillager.entity.MadTntEntity;
import net.madpillager.init.ConfigInit;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class MadTntEntityRenderer extends EntityRenderer<MadTntEntity> {
    private final BlockRenderManager blockRenderManager;

    public MadTntEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(MadTntEntity tntEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0, 0.5, 0.0);
        int j = tntEntity.getFuse();
        if ((float) j - g + 1.0f < 10.0f) {
            float h = 1.0f - ((float) j - g + 1.0f) / 10.0f;
            h = MathHelper.clamp(h, 0.0f, 1.0f);
            h *= h;
            h *= h;
            float k = 1.0f + h * 0.3f;
            matrixStack.scale(k, k, k);
        }

        if (ConfigInit.CONFIG.tntRotation && tntEntity.getVehicle() != null)
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-tntEntity.getVehicle().getYaw()));

        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        matrixStack.translate(-0.5, -0.5, 0.5);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));

        if (tntEntity.isIgnited())
            TntMinecartEntityRenderer.renderFlashingBlock(this.blockRenderManager, Blocks.TNT.getDefaultState(), matrixStack, vertexConsumerProvider, i, j / 5 % 2 == 0);
        else
            blockRenderManager.renderBlockAsEntity(Blocks.TNT.getDefaultState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);

        matrixStack.pop();
        super.render(tntEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(MadTntEntity tntEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
