package micdoodle8.mods.galacticraft.core.network;

import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import java.util.*;
import java.io.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.server.*;
import cpw.mods.fml.common.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;

public class PacketDynamic implements IPacket
{
    private int type;
    private int dimID;
    private Object[] data;
    private ArrayList<Object> sendData;
    
    public PacketDynamic() {
    }
    
    public PacketDynamic(final Entity entity) {
        assert entity instanceof IPacketReceiver : "Entity does not implement " + IPacketReceiver.class.getSimpleName();
        this.type = 0;
        this.dimID = entity.worldObj.provider.dimensionId;
        this.data = new Object[] { entity.getEntityId() };
        this.sendData = new ArrayList<Object>();
        ((IPacketReceiver)entity).getNetworkedData((ArrayList)this.sendData);
    }
    
    public PacketDynamic(final TileEntity tile) {
        assert tile instanceof IPacketReceiver : "TileEntity does not implement " + IPacketReceiver.class.getSimpleName();
        this.type = 1;
        this.dimID = tile.getWorldObj().provider.dimensionId;
        this.data = new Object[] { tile.xCoord, tile.yCoord, tile.zCoord };
        this.sendData = new ArrayList<Object>();
        ((IPacketReceiver)tile).getNetworkedData((ArrayList)this.sendData);
    }
    
    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.type);
        buffer.writeInt(this.dimID);
        switch (this.type) {
            case 0: {
                buffer.writeInt((int)this.data[0]);
                break;
            }
            case 1: {
                buffer.writeInt((int)this.data[0]);
                buffer.writeInt((int)this.data[1]);
                buffer.writeInt((int)this.data[2]);
                break;
            }
        }
        try {
            NetworkUtil.encodeData(buffer, (Collection)this.sendData);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        this.type = buffer.readInt();
        this.dimID = buffer.readInt();
        World world = GalacticraftCore.proxy.getWorldForID(this.dimID);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            world = (World)MinecraftServer.getServer().worldServerForDimension(this.dimID);
        }
        if (world == null) {
            FMLLog.severe("Failed to get world for dimension ID: " + this.dimID, new Object[0]);
        }
        switch (this.type) {
            case 0: {
                (this.data = new Object[1])[0] = buffer.readInt();
                if (world != null) {
                    final Entity entity = world.getEntityByID((int)this.data[0]);
                    if (entity instanceof IPacketReceiver && buffer.readableBytes() > 0) {
                        ((IPacketReceiver)entity).decodePacketdata(buffer);
                    }
                    break;
                }
                break;
            }
            case 1: {
                (this.data = new Object[3])[0] = buffer.readInt();
                this.data[1] = buffer.readInt();
                this.data[2] = buffer.readInt();
                if (world == null) {
                    break;
                }
                final TileEntity tile = world.getTileEntity((int)this.data[0], (int)this.data[1], (int)this.data[2]);
                if (tile instanceof IPacketReceiver) {
                    ((IPacketReceiver)tile).decodePacketdata(buffer);
                    break;
                }
                break;
            }
        }
    }
    
    public void handleClientSide(final EntityPlayer player) {
        this.handleData(Side.CLIENT, player);
    }
    
    public void handleServerSide(final EntityPlayer player) {
        this.handleData(Side.SERVER, player);
    }
    
    private void handleData(final Side side, final EntityPlayer player) {
        switch (this.type) {
            case 0: {
                final Entity entity = player.worldObj.getEntityByID((int)this.data[0]);
                if (!(entity instanceof IPacketReceiver)) {
                    break;
                }
                ((IPacketReceiver)entity).handlePacketData(side, player);
                if (side == Side.SERVER && player instanceof EntityPlayerMP) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketDynamic(entity), (EntityPlayerMP)player);
                    break;
                }
                break;
            }
            case 1: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data[0], (int)this.data[1], (int)this.data[2]);
                if (!(tile instanceof IPacketReceiver)) {
                    break;
                }
                ((IPacketReceiver)tile).handlePacketData(side, player);
                if (side == Side.SERVER && player instanceof EntityPlayerMP) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketDynamic(tile), (EntityPlayerMP)player);
                    break;
                }
                break;
            }
        }
    }
}
