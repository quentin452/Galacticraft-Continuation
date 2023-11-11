package micdoodle8.mods.galacticraft.core.network;

import net.minecraft.entity.player.EntityPlayer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IPacket {

    void encodeInto(ChannelHandlerContext context, ByteBuf buffer);

    void decodeInto(ChannelHandlerContext context, ByteBuf buffer);

    void handleClientSide(EntityPlayer player);

    void handleServerSide(EntityPlayer player);
}
