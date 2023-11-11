package micdoodle8.mods.galacticraft.planets.mars.network;

import io.netty.channel.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;
import java.io.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import micdoodle8.mods.galacticraft.planets.mars.client.gui.*;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.pathfinding.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.util.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public class PacketSimpleMars implements IPacket
{
    private EnumSimplePacketMars type;
    private List<Object> data;

    public PacketSimpleMars() {
    }

    public PacketSimpleMars(final EnumSimplePacketMars packetType, final Object[] data) {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimpleMars(final EnumSimplePacketMars packetType, final List<Object> data) {
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
        this.type = EnumSimplePacketMars.values()[buffer.readInt()];
        if (this.type.getDecodeClasses().length > 0) {
            this.data = (List<Object>)NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClientSide(final EntityPlayer player) {
        EntityClientPlayerMP playerBaseClient = null;
        if (player instanceof EntityClientPlayerMP) {
            playerBaseClient = (EntityClientPlayerMP)player;
        }
        Label_0276: {
            switch (this.type) {
                case C_OPEN_CUSTOM_GUI: {
                    int entityID = 0;
                    Entity entity = null;
                    if ((int)this.data.get(1) == 0) {
                        entityID = (int) this.data.get(2);
                        entity = player.worldObj.getEntityByID(entityID);
                        if (entity != null && entity instanceof EntitySlimeling) {
                            FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen) new GuiSlimelingInventory(player, (EntitySlimeling) entity));
                        }
                        player.openContainer.windowId = (int) this.data.get(0);
                        break Label_0276;
                    } else if ((int)this.data.get(1) == 1) {
                        entityID = (int) this.data.get(2);
                        entity = player.worldObj.getEntityByID(entityID);
                        if (entity != null && entity instanceof EntityCargoRocket) {
                            FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen) new GuiCargoRocket((IInventory) player.inventory, (EntityCargoRocket) entity));
                        }
                        player.openContainer.windowId = (int) this.data.get(0);
                        break Label_0276;
                    }
                    break;
                }
                case C_BEGIN_CRYOGENIC_SLEEP: {
                    final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                    if (tile instanceof TileEntityCryogenicChamber) {
                        ((TileEntityCryogenicChamber)tile).sleepInBedAt(player, (Integer) this.data.get(0), (Integer) this.data.get(1), (Integer) this.data.get(2));
                        break;
                    }
                    break;
                }
            }
        }
    }

    public void handleServerSide(final EntityPlayer player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        final GCPlayerStats stats = GCPlayerStats.get(playerBase);
        Label_0949: {
            switch (this.type) {
                case S_UPDATE_SLIMELING_DATA: {
                    final Entity entity = player.worldObj.getEntityByID((int)this.data.get(0));
                    if (entity instanceof EntitySlimeling) {
                        final EntitySlimeling slimeling = (EntitySlimeling)entity;
                        final int subType = (int) this.data.get(1);
                        switch (subType) {
                            case 0: {
                                if (player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    slimeling.setSittingAI(!slimeling.isSitting());
                                    slimeling.setJumping(false);
                                    slimeling.setPathToEntity((PathEntity)null);
                                    slimeling.setTarget((Entity)null);
                                    slimeling.setAttackTarget((EntityLivingBase)null);
                                    break;
                                }
                                break;
                            }
                            case 1: {
                                if (player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    slimeling.setName(slimeling.slimelingName = (String) this.data.get(2));
                                    break;
                                }
                                break;
                            }
                            case 2: {
                                if (player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    final EntitySlimeling entitySlimeling = slimeling;
                                    entitySlimeling.age += 5000;
                                    break;
                                }
                                break;
                            }
                            case 3: {
                                if (!slimeling.isInLove() && player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    slimeling.func_146082_f((EntityPlayer)playerBase);
                                    break;
                                }
                                break;
                            }
                            case 4: {
                                if (player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    slimeling.attackDamage = Math.min(slimeling.attackDamage + 0.1f, 1.0f);
                                    break;
                                }
                                break;
                            }
                            case 5: {
                                if (player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    slimeling.setHealth(slimeling.getHealth() + 5.0f);
                                    break;
                                }
                                break;
                            }
                            case 6: {
                                if (player == slimeling.getOwner() && !slimeling.worldObj.isRemote) {
                                    MarsUtil.openSlimelingInventory(playerBase, slimeling);
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                case S_WAKE_PLAYER: {
                    final ChunkCoordinates c = playerBase.playerLocation;
                    if (c != null) {
                        final EventWakePlayer event = new EventWakePlayer((EntityPlayer)playerBase, c.posX, c.posY, c.posZ, true, true, false, true);
                        MinecraftForge.EVENT_BUS.post((Event)event);
                        playerBase.wakeUpPlayer(true, true, false);
                        break;
                    }
                    break;
                }
                case S_UPDATE_ADVANCED_GUI: {
                    final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(1), (int)this.data.get(2), (int)this.data.get(3));
                    if (Objects.requireNonNull(this.data.get(0)) instanceof Integer && (Integer) this.data.get(0) == 0) {
                        if (tile instanceof TileEntityLaunchController) {
                            final TileEntityLaunchController launchController = (TileEntityLaunchController) tile;
                            launchController.setFrequency((Integer) this.data.get(4));
                            break Label_0949;
                        }
                        break Label_0949;
                    } else if (this.data.get(0) instanceof Integer && (Integer) this.data.get(0) == 1) {
                        if (tile instanceof TileEntityLaunchController) {
                            final TileEntityLaunchController launchController = (TileEntityLaunchController) tile;
                            launchController.setLaunchDropdownSelection((Integer) this.data.get(4));
                            break Label_0949;
                        }
                        break Label_0949;
                    } else if (this.data.get(0) instanceof Integer && (Integer) this.data.get(0) == 2) {
                        if (tile instanceof TileEntityLaunchController) {
                            final TileEntityLaunchController launchController = (TileEntityLaunchController) tile;
                            launchController.setDestinationFrequency((Integer) this.data.get(4));
                            break Label_0949;
                        }
                        break Label_0949;
                    } else if (this.data.get(0) instanceof Integer && (Integer) this.data.get(0) == 3) {
                        if (tile instanceof TileEntityLaunchController) {
                            final TileEntityLaunchController launchController = (TileEntityLaunchController) tile;
                            launchController.launchPadRemovalDisabled = ((Integer) this.data.get(4)) == 1;
                            break Label_0949;
                        }
                        break Label_0949;
                    } else if (this.data.get(0) instanceof Integer && (Integer) this.data.get(0) == 4) {
                        if (tile instanceof TileEntityLaunchController) {
                            final TileEntityLaunchController launchController = (TileEntityLaunchController) tile;
                            launchController.setLaunchSchedulingEnabled(((Integer) this.data.get(4)) == 1);
                            break Label_0949;
                        }
                        break Label_0949;
                    } else if (this.data.get(0) instanceof Integer && (Integer) this.data.get(0) == 5) {
                        if (tile instanceof TileEntityLaunchController) {
                            final TileEntityLaunchController launchController = (TileEntityLaunchController) tile;
                            launchController.requiresClientUpdate = true;
                            break Label_0949;
                        }
                        break Label_0949;
                    }
                    break Label_0949;
                }
                case S_UPDATE_CARGO_ROCKET_STATUS: {
                    final Entity entity2 = player.worldObj.getEntityByID((int)this.data.get(0));
                    if (entity2 instanceof EntityCargoRocket) {
                        final EntityCargoRocket rocket = (EntityCargoRocket)entity2;
                        final int subType2 = (int) this.data.get(1);
                        rocket.statusValid = rocket.checkLaunchValidity();
                        break;
                    }
                    break;
                }
            }
        }
    }

    public enum EnumSimplePacketMars
    {
        S_UPDATE_SLIMELING_DATA(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, String.class }),
        S_WAKE_PLAYER(Side.SERVER, (Class<?>[])new Class[0]),
        S_UPDATE_ADVANCED_GUI(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class }),
        S_UPDATE_CARGO_ROCKET_STATUS(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class }),
        C_OPEN_CUSTOM_GUI(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        C_BEGIN_CRYOGENIC_SLEEP(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class });

        private Side targetSide;
        private Class<?>[] decodeAs;

        private EnumSimplePacketMars(final Side targetSide, final Class<?>[] decodeAs) {
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
