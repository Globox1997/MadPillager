package net.madpillager.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.madpillager.entity.render.MadPillagerRenderer;
import net.madpillager.entity.render.MadTntEntityRenderer;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static void init() {
        EntityRendererRegistry.register(EntityInit.MAD_PILLAGER_ENTITY, MadPillagerRenderer::new);
        EntityRendererRegistry.register(EntityInit.MAD_TNT_ENTITY, MadTntEntityRenderer::new);
    }

}
