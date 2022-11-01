package net.madpillager.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.madpillager.entity.MadTntEntity;
import net.minecraft.entity.LivingEntity;

public class MadPillagerClientPacket {

    public static void init() {

        ClientPlayNetworking.registerGlobalReceiver(MadPillagerServerPacket.MAD_TNT_PACKET, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            int tntId = buf.readInt();
            client.execute(() -> {
                if (client.player != null && client.world.getEntityById(tntId) instanceof MadTntEntity madTntEntity) {
                    madTntEntity.setIgnited(true);
                    if (client.world.getEntityById(entityId) instanceof LivingEntity livingEntity)
                        madTntEntity.setIgniterEntity(livingEntity);
                }
            });
        });
    }
}
