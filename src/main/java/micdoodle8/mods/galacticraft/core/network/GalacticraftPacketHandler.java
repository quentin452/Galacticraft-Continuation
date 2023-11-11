package micdoodle8.mods.galacticraft.core.network;

import io.netty.channel.*;
import net.minecraft.network.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.common.*;
import net.minecraft.entity.player.*;

@ChannelHandler.Sharable
public class GalacticraftPacketHandler extends SimpleChannelInboundHandler<IPacket>
{
    protected void channelRead0(final ChannelHandlerContext ctx, final IPacket msg) throws Exception {
        final INetHandler netHandler = (INetHandler)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        final EntityPlayer player = GalacticraftCore.proxy.getPlayerFromNetHandler(netHandler);
        switch (FMLCommonHandler.instance().getEffectiveSide()) {
            case CLIENT: {
                msg.handleClientSide(player);
                break;
            }
            case SERVER: {
                msg.handleServerSide(player);
                break;
            }
        }
    }
}
