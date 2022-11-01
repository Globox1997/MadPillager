package net.madpillager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.madpillager.init.RenderInit;
import net.madpillager.network.MadPillagerClientPacket;

@Environment(EnvType.CLIENT)
public class MadPillagerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RenderInit.init();
        MadPillagerClientPacket.init();
    }

}
