package net.madpillager.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class MadPillagerServerPacket {

    public static final Identifier MAD_TNT_PACKET = new Identifier("madpillager", "mad_tnt");

    public static void init() {

    }

    public static void writeS2CMadTntIgnitePacket(ServerWorld world, int entityId, int tntId) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeInt(tntId);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(MAD_TNT_PACKET, buf);
        for (int i = 0; i < world.getPlayers().size(); i++)
            world.getPlayers().get(i).networkHandler.sendPacket(packet);
    }

}
