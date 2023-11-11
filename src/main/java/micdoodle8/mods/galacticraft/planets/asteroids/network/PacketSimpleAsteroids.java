package micdoodle8.mods.galacticraft.planets.asteroids.network;

import io.netty.channel.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.io.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.entity.player.*;

public class PacketSimpleAsteroids implements IPacket
{
    private EnumSimplePacketAsteroids type;
    private List<Object> data;

    public PacketSimpleAsteroids() {
    }

    public PacketSimpleAsteroids(final EnumSimplePacketAsteroids packetType, final Object[] data) {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimpleAsteroids(final EnumSimplePacketAsteroids packetType, final List<Object> data) {
        if (packetType.getDecodeClasses().length != data.size()) {
            GCLog.info("Simple Packet found data length different than packet type");
        }
        this.type = packetType;
        this.data = data;
    }

    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.type.ordinal());
        try {
            NetworkUtil.encodeData(buffer, (Collection)this.data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        this.type = EnumSimplePacketAsteroids.values()[buffer.readInt()];
        if (this.type.getDecodeClasses().length > 0) {
            this.data = (List<Object>)NetworkUtil.decodeData((Class[])this.type.getDecodeClasses(), buffer);
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClientSide(final EntityPlayer player) {
        EntityClientPlayerMP playerBaseClient = null;
        if (player instanceof EntityClientPlayerMP) {
            playerBaseClient = (EntityClientPlayerMP)player;
        }
        switch (this.type) {
            case C_TELEPAD_SEND: {
                final Entity entity = playerBaseClient.worldObj.getEntityByID((int)this.data.get(1));
                if (entity != null && entity instanceof EntityLivingBase) {
                    final BlockVec3 pos = (BlockVec3) this.data.get(0);
                    entity.setPosition(pos.x + 0.5, pos.y + 2.2, pos.z + 0.5);
                    break;
                }
                break;
            }
            case C_UPDATE_GRAPPLE_POS: {
                final Entity entity = playerBaseClient.worldObj.getEntityByID((int)this.data.get(0));
                if (entity != null && entity instanceof EntityGrapple) {
                    final Vector3 vec = (Vector3) this.data.get(1);
                    entity.setPosition(vec.x, vec.y, vec.z);
                    break;
                }
                break;
            }
            case C_UPDATE_MINERBASE_FACING: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                final int facingNew = (int) this.data.get(3);
                if (tile instanceof TileEntityMinerBase) {
                    ((TileEntityMinerBase)tile).facing = facingNew;
                    ((TileEntityMinerBase)tile).setMainBlockPos((Integer) this.data.get(4), (Integer) this.data.get(5), (Integer) this.data.get(6));
                    final int link = (int) this.data.get(7);
                    if (link > 0) {
                        ((TileEntityMinerBase)tile).linkedMinerID = UUID.randomUUID();
                    }
                    else {
                        ((TileEntityMinerBase)tile).linkedMinerID = null;
                    }
                    break;
                }
                break;
            }
        }
    }

    public void handleServerSide(final EntityPlayer player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        Label_0295: {
            switch (this.type) {
                case S_UPDATE_ADVANCED_GUI: {
                    final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(1), (int)this.data.get(2), (int)this.data.get(3));
                    if (((Integer) Objects.requireNonNull(this.data.get(0))) == 0) {
                        if (tile instanceof TileEntityShortRangeTelepad) {
                            final TileEntityShortRangeTelepad launchController = (TileEntityShortRangeTelepad) tile;
                            launchController.setAddress((Integer) this.data.get(4));
                            break Label_0295;
                        }
                        break Label_0295;
                    } else if (((Integer) this.data.get(0)) == 1) {
                        if (tile instanceof TileEntityShortRangeTelepad) {
                            final TileEntityShortRangeTelepad launchController = (TileEntityShortRangeTelepad) tile;
                            launchController.setTargetAddress((Integer) this.data.get(4));
                            break Label_0295;
                        }
                        break Label_0295;
                    }
                    break Label_0295;
                }
                case S_REQUEST_MINERBASE_FACING: {
                    final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                    if (tile instanceof TileEntityMinerBase) {
                        ((TileEntityMinerBase)tile).updateClientFlag = true;
                        break;
                    }
                    break;
                }
            }
        }
    }

    public enum EnumSimplePacketAsteroids
    {
        S_UPDATE_ADVANCED_GUI(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class }),
        S_REQUEST_MINERBASE_FACING(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        C_TELEPAD_SEND(Side.CLIENT, (Class<?>[])new Class[] { BlockVec3.class, Integer.class }),
        C_UPDATE_GRAPPLE_POS(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Vector3.class }),
        C_UPDATE_MINERBASE_FACING(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class });

        private Side targetSide;
        private Class<?>[] decodeAs;

        private EnumSimplePacketAsteroids(final Side targetSide, final Class<?>[] decodeAs) {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide() {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses() {
            return this.decodeAs;
        }
    }
}
