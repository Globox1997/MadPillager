package net.madpillager.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.madpillager.config.MadPillagerConfig;

public class ConfigInit {

    public static MadPillagerConfig CONFIG = new MadPillagerConfig();

    public static void init() {
        AutoConfig.register(MadPillagerConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(MadPillagerConfig.class).getConfig();
    }

}
