package net.madpillager.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.madpillager.entity.MadPillagerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MadPillagerRenderer extends IllagerEntityRenderer<MadPillagerEntity> {
    private static final Identifier TEXTURE = new Identifier("madpillager", "textures/entity/mad_pillager.png");

    public MadPillagerRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        // this.addFeature(new HeldTntFeatureRenderer<MadPillagerEntity, IllagerEntityModel<MadPillagerEntity>>(this, context.getHeldItemRenderer()));

    }

    @Override
    public Identifier getTexture(MadPillagerEntity madPillagerEntity) {
        return TEXTURE;
    }
}
