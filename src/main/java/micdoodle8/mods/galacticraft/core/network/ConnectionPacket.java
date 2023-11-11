package micdoodle8.mods.galacticraft.core.network;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.common.network.internal.*;
import io.netty.buffer.*;
import java.util.*;
import java.io.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.client.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.network.*;

public class ConnectionPacket
{
    public static final String CHANNEL = "galacticraft";
    public static FMLEventChannel bus;
    
    public void handle(final ByteBuf payload, final EntityPlayer player) {
        final int packetId = payload.readByte();
        List<Object> data = new ArrayList<Object>();
        switch (packetId) {
            case 101: {
                for (int length = payload.readInt(), i = 0; i < length; ++i) {
                    data.add(payload.readInt());
                }
                WorldUtil.decodePlanetsListClient(data);
                break;
            }
            case 102: {
                for (int llength = payload.readInt(), j = 0; j < llength; ++j) {
                    data.add(payload.readInt());
                }
                WorldUtil.decodeSpaceStationListClient(data);
                break;
            }
            case 103: {
                try {
                    data = NetworkUtil.decodeData(PacketSimple.EnumSimplePacket.C_UPDATE_CONFIGS.getDecodeClasses(), payload);
                    ConfigManagerCore.saveClientConfigOverrideable();
                    ConfigManagerCore.setConfigOverride(data);
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Server-set configs received OK on client.");
                    }
                }
                catch (Exception e) {
                    System.err.println("[Galacticraft] Error handling connection packet - maybe the player's Galacticraft version does not match the server version?");
                    e.printStackTrace();
                }
                break;
            }
        }
        if (payload.readInt() != 3519) {
            GCLog.severe("Packet completion problem for connection packet " + packetId + " - maybe the player's Galacticraft version does not match the server version?");
        }
    }
    
    public static FMLProxyPacket createDimPacket(final Integer[] dims) {
        final ArrayList<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < dims.length; ++i) {
            data.add(dims[i]);
        }
        return createPacket((byte)101, data);
    }
    
    public static FMLProxyPacket createSSPacket(final Integer[] dims) {
        final ArrayList<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < dims.length; ++i) {
            data.add(dims[i]);
        }
        return createPacket((byte)102, data);
    }
    
    public static FMLProxyPacket createPacket(final byte packetId, final Collection<Integer> data) {
        final ByteBuf payload = Unpooled.buffer();
        payload.writeByte((int)packetId);
        payload.writeInt(data.size());
        for (final Integer i : data) {
            payload.writeInt((int)i);
        }
        payload.writeInt(3519);
        return new FMLProxyPacket(payload, "galacticraft");
    }
    
    public static FMLProxyPacket createConfigPacket(final List<Object> data) {
        final ByteBuf payload = Unpooled.buffer();
        payload.writeByte(103);
        try {
            NetworkUtil.encodeData(payload, data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        payload.writeInt(3519);
        return new FMLProxyPacket(payload, "galacticraft");
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPacketData(final FMLNetworkEvent.ClientCustomPacketEvent event) {
        final FMLProxyPacket pkt = event.packet;
        this.onFMLProxyPacketData(event.manager, pkt, (EntityPlayer)Minecraft.getMinecraft().thePlayer);
    }
    
    @SubscribeEvent
    public void onPacketData(final FMLNetworkEvent.ServerCustomPacketEvent event) {
        final FMLProxyPacket pkt = event.packet;
        this.onFMLProxyPacketData(event.manager, pkt, (EntityPlayer)((NetHandlerPlayServer)event.handler).playerEntity);
    }
    
    public void onFMLProxyPacketData(final NetworkManager manager, final FMLProxyPacket packet, final EntityPlayer player) {
        try {
            if (packet == null || packet.payload() == null) {
                throw new RuntimeException("Empty packet sent to Galacticraft channel");
            }
            final ByteBuf data = packet.payload();
            this.handle(data, player);
        }
        catch (Exception e) {
            GCLog.severe("GC login packet handler: Failed to read packet");
            GCLog.severe(e.toString());
            e.printStackTrace();
        }
    }
}
