package net.madpillager;

import net.fabricmc.api.ModInitializer;
import net.madpillager.init.ConfigInit;
import net.madpillager.init.EntityInit;
import net.madpillager.network.MadPillagerServerPacket;

public class MadPillagerMain implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigInit.init();
        EntityInit.init();
        MadPillagerServerPacket.init();
    }

}
