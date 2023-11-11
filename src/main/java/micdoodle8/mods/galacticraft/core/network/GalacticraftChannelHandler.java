package micdoodle8.mods.galacticraft.core.network;

import java.util.*;
import cpw.mods.fml.relauncher.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.common.*;

public class GalacticraftChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket>
{
    private EnumMap<Side, FMLEmbeddedChannel> channels;

    private GalacticraftChannelHandler() {
        this.addDiscriminator(0, (Class)PacketSimple.class);
        this.addDiscriminator(1, (Class)PacketRotateRocket.class);
        this.addDiscriminator(2, (Class)PacketDynamic.class);
        this.addDiscriminator(3, (Class)PacketControllableEntity.class);
        this.addDiscriminator(4, (Class)PacketEntityUpdate.class);
        this.addDiscriminator(5, (Class)PacketDynamicInventory.class);
    }

    public static GalacticraftChannelHandler init() {
        final GalacticraftChannelHandler channelHandler = new GalacticraftChannelHandler();
        channelHandler.channels = (EnumMap<Side, FMLEmbeddedChannel>)NetworkRegistry.INSTANCE.newChannel("GalacticraftCore", new ChannelHandler[] { (ChannelHandler)channelHandler, (ChannelHandler)new GalacticraftPacketHandler() });
        return channelHandler;
    }

    public void encodeInto(final ChannelHandlerContext ctx, final IPacket msg, final ByteBuf target) throws Exception {
        msg.encodeInto(ctx, target);
    }

    public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf source, final IPacket msg) {
        msg.decodeInto(ctx, source);
    }

    public void sendToAll(final IPacket message) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set((FMLOutboundHandler.OutboundTarget) FMLOutboundHandler.OutboundTarget.ALL);
        this.channels.get(Side.SERVER).writeOutbound(new Object[] { message });
    }

    public void sendTo(final IPacket message, final EntityPlayerMP player) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set((FMLOutboundHandler.OutboundTarget) FMLOutboundHandler.OutboundTarget.PLAYER);
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set((Object)player);
        this.channels.get(Side.SERVER).writeOutbound(new Object[] { message });
    }

    public void sendToAllAround(final IPacket message, final NetworkRegistry.TargetPoint point) {
        try {
            this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set((FMLOutboundHandler.OutboundTarget) FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
            this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set((Object)point);
            this.channels.get(Side.SERVER).writeOutbound(new Object[] { message });
        }
        catch (Exception e) {
            GCLog.severe("Forge error when sending network packet to nearby players - this is not a Galacticraft bug, does another mod make fake players?");
            e.printStackTrace();
        }
    }

    public void sendToDimension(final IPacket message, final int dimensionId) {
        try {
            this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set((FMLOutboundHandler.OutboundTarget) FMLOutboundHandler.OutboundTarget.DIMENSION);
            this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set((Object)dimensionId);
            this.channels.get(Side.SERVER).writeOutbound(new Object[] { message });
        }
        catch (Exception e) {
            GCLog.severe("Forge error when sending network packet to all players in dimension - this is not a Galacticraft bug, does another mod make fake players?");
            e.printStackTrace();
        }
    }

    public void sendToServer(final IPacket message) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return;
        }
        this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set((FMLOutboundHandler.OutboundTarget) FMLOutboundHandler.OutboundTarget.TOSERVER);
        this.channels.get(Side.CLIENT).writeOutbound(new Object[] { message });
    }
}
