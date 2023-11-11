package micdoodle8.mods.galacticraft.core.network;

import io.netty.channel.*;
import io.netty.buffer.*;
import net.minecraft.entity.player.*;

public interface IPacket
{
    void encodeInto(final ChannelHandlerContext p0, final ByteBuf p1);
    
    void decodeInto(final ChannelHandlerContext p0, final ByteBuf p1);
    
    void handleClientSide(final EntityPlayer p0);
    
    void handleServerSide(final EntityPlayer p0);
}
