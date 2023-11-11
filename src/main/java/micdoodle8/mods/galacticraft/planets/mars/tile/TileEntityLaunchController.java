package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.world.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.common.*;
import net.minecraft.world.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.api.entity.*;

public class TileEntityLaunchController extends TileBaseElectricBlockWithInventory implements IChunkLoader, ISidedInventory, ILandingPadAttachable
{
    public static final int WATTS_PER_TICK = 1;
    private ItemStack[] containingItems;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean launchPadRemovalDisabled;
    private ForgeChunkManager.Ticket chunkLoadTicket;
    private List<ChunkCoordinates> connectedPads;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int frequency;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int destFrequency;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public String ownerName;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean frequencyValid;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean destFrequencyValid;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int launchDropdownSelection;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean launchSchedulingEnabled;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean controlEnabled;
    public boolean hideTargetDestination;
    public boolean requiresClientUpdate;
    public Object attachedDock;
    private boolean frequencyCheckNeeded;
    
    public TileEntityLaunchController() {
        this.containingItems = new ItemStack[1];
        this.launchPadRemovalDisabled = true;
        this.connectedPads = new ArrayList<ChunkCoordinates>();
        this.frequency = -1;
        this.destFrequency = -1;
        this.ownerName = "";
        this.hideTargetDestination = true;
        this.attachedDock = null;
        this.frequencyCheckNeeded = false;
        this.storage.setMaxExtract(10.0f);
        this.noRedstoneControl = true;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            this.controlEnabled = (this.launchSchedulingEnabled && this.hasEnoughEnergyToRun && !this.getDisabled(0));
            if (this.frequencyCheckNeeded) {
                this.checkDestFrequencyValid();
                this.frequencyCheckNeeded = false;
            }
            if (this.requiresClientUpdate) {
                this.requiresClientUpdate = false;
            }
            if (this.ticks % 40 == 0) {
                this.setFrequency(this.frequency);
                this.setDestinationFrequency(this.destFrequency);
            }
            if (this.ticks % 20 == 0 && this.chunkLoadTicket != null) {
                for (int i = 0; i < this.connectedPads.size(); ++i) {
                    final ChunkCoordinates coords = this.connectedPads.get(i);
                    final Block block = this.worldObj.getBlock(coords.posX, coords.posY, coords.posZ);
                    if (block != GCBlocks.landingPadFull) {
                        this.connectedPads.remove(i);
                        ForgeChunkManager.unforceChunk(this.chunkLoadTicket, new ChunkCoordIntPair(coords.posX >> 4, coords.posZ >> 4));
                    }
                }
            }
        }
        else if (this.frequency == -1 && this.destFrequency == -1) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, new Object[] { 5, this.xCoord, this.yCoord, this.zCoord, 0 }));
        }
    }
    
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }
    
    public void invalidate() {
        super.invalidate();
        if (this.chunkLoadTicket != null) {
            ForgeChunkManager.releaseTicket(this.chunkLoadTicket);
        }
    }
    
    public void onTicketLoaded(final ForgeChunkManager.Ticket ticket, final boolean placed) {
        if (!this.worldObj.isRemote && ConfigManagerMars.launchControllerChunkLoad) {
            if (ticket == null) {
                return;
            }
            if (this.chunkLoadTicket == null) {
                this.chunkLoadTicket = ticket;
            }
            final NBTTagCompound nbt = this.chunkLoadTicket.getModData();
            nbt.setInteger("ChunkLoaderTileX", this.xCoord);
            nbt.setInteger("ChunkLoaderTileY", this.yCoord);
            nbt.setInteger("ChunkLoaderTileZ", this.zCoord);
            for (int x = -2; x <= 2; ++x) {
                for (int z = -2; z <= 2; ++z) {
                    final Block blockID = this.worldObj.getBlock(this.xCoord + x, this.yCoord, this.zCoord + z);
                    if (blockID instanceof BlockLandingPadFull && (this.xCoord + x >> 4 != this.xCoord >> 4 || this.zCoord + z >> 4 != this.zCoord >> 4)) {
                        this.connectedPads.add(new ChunkCoordinates(this.xCoord + x, this.yCoord, this.zCoord + z));
                        if (placed) {
                            ChunkLoadingCallback.forceChunk(this.chunkLoadTicket, this.worldObj, this.xCoord + x, this.yCoord, this.zCoord + z, this.getOwnerName());
                        }
                        else {
                            ChunkLoadingCallback.addToList(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getOwnerName());
                        }
                    }
                }
            }
            ChunkLoadingCallback.forceChunk(this.chunkLoadTicket, this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getOwnerName());
        }
    }
    
    public ForgeChunkManager.Ticket getTicket() {
        return this.chunkLoadTicket;
    }
    
    public ChunkCoordinates getCoords() {
        return new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        this.ownerName = nbt.getString("OwnerName");
        this.launchDropdownSelection = nbt.getInteger("LaunchSelection");
        this.frequency = nbt.getInteger("ControllerFrequency");
        this.destFrequency = nbt.getInteger("TargetFrequency");
        this.frequencyCheckNeeded = true;
        this.launchPadRemovalDisabled = nbt.getBoolean("LaunchPadRemovalDisabled");
        this.launchSchedulingEnabled = nbt.getBoolean("LaunchPadSchedulingEnabled");
        this.hideTargetDestination = nbt.getBoolean("HideTargetDestination");
        this.requiresClientUpdate = true;
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
        nbt.setString("OwnerName", this.ownerName);
        nbt.setInteger("LaunchSelection", this.launchDropdownSelection);
        nbt.setInteger("ControllerFrequency", this.frequency);
        nbt.setInteger("TargetFrequency", this.destFrequency);
        nbt.setBoolean("LaunchPadRemovalDisabled", this.launchPadRemovalDisabled);
        nbt.setBoolean("LaunchPadSchedulingEnabled", this.launchSchedulingEnabled);
        nbt.setBoolean("HideTargetDestination", this.hideTargetDestination);
    }
    
    public ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.launchcontroller.name");
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem());
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return this.isItemValidForSlot(slotID, par2ItemStack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return slotID == 0;
    }
    
    public boolean shouldUseEnergy() {
        return !this.getDisabled(0);
    }
    
    public void setDisabled(final int index, final boolean disabled) {
        if (this.disableCooldown == 0) {
            switch (index) {
                case 0: {
                    this.disabled = disabled;
                    this.disableCooldown = 10;
                    break;
                }
                case 1: {
                    this.launchSchedulingEnabled = disabled;
                    break;
                }
                case 2: {
                    this.hideTargetDestination = disabled;
                    this.disableCooldown = 10;
                    break;
                }
            }
        }
    }
    
    public boolean getDisabled(final int index) {
        switch (index) {
            case 0: {
                return this.disabled;
            }
            case 1: {
                return this.launchSchedulingEnabled;
            }
            case 2: {
                return this.hideTargetDestination;
            }
            default: {
                return true;
            }
        }
    }
    
    public boolean canAttachToLandingPad(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityLandingPad;
    }
    
    public void setFrequency(final int frequency) {
        this.frequency = frequency;
        if (this.frequency >= 0 && FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
            this.frequencyValid = true;
            final WorldServer[] servers = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
        Label_0168:
            for (int i = 0; i < servers.length; ++i) {
                final WorldServer world = servers[i];
                for (TileEntity tile2 : new ArrayList<TileEntity>(world.loadedTileEntityList)) {
                    if (this != tile2) {
                        tile2 = world.getTileEntity(tile2.xCoord, tile2.yCoord, tile2.zCoord);
                        if (tile2 == null) {
                            continue;
                        }
                        if (!(tile2 instanceof TileEntityLaunchController)) {
                            continue;
                        }
                        final TileEntityLaunchController launchController2 = (TileEntityLaunchController)tile2;
                        if (launchController2.frequency == this.frequency) {
                            this.frequencyValid = false;
                            break Label_0168;
                        }
                        continue;
                    }
                }
            }
        }
        else {
            this.frequencyValid = false;
        }
    }
    
    public void setDestinationFrequency(final int frequency) {
        if (frequency != this.destFrequency) {
            this.destFrequency = frequency;
            this.checkDestFrequencyValid();
            this.updateRocketOnDockSettings();
        }
    }
    
    public void checkDestFrequencyValid() {
        if (!this.worldObj.isRemote && FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
            this.destFrequencyValid = false;
            if (this.destFrequency >= 0) {
                final WorldServer[] servers = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
                for (int i = 0; i < servers.length; ++i) {
                    final WorldServer world = servers[i];
                    for (TileEntity tile2 : new ArrayList<TileEntity>(world.loadedTileEntityList)) {
                        if (this != tile2) {
                            tile2 = world.getTileEntity(tile2.xCoord, tile2.yCoord, tile2.zCoord);
                            if (tile2 == null) {
                                continue;
                            }
                            if (!(tile2 instanceof TileEntityLaunchController)) {
                                continue;
                            }
                            final TileEntityLaunchController launchController2 = (TileEntityLaunchController)tile2;
                            if (launchController2.frequency == this.destFrequency) {
                                this.destFrequencyValid = true;
                                return;
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public boolean validFrequency() {
        this.checkDestFrequencyValid();
        return !this.getDisabled(0) && this.hasEnoughEnergyToRun && this.frequencyValid && this.destFrequencyValid;
    }
    
    public void setLaunchDropdownSelection(final int newvalue) {
        if (newvalue != this.launchDropdownSelection) {
            this.launchDropdownSelection = newvalue;
            this.checkDestFrequencyValid();
            this.updateRocketOnDockSettings();
        }
    }
    
    public void setLaunchSchedulingEnabled(final boolean newvalue) {
        if (newvalue != this.launchSchedulingEnabled) {
            this.launchSchedulingEnabled = newvalue;
            this.checkDestFrequencyValid();
            this.updateRocketOnDockSettings();
        }
    }
    
    public void updateRocketOnDockSettings() {
        if (this.attachedDock instanceof TileEntityLandingPad) {
            final TileEntityLandingPad pad = (TileEntityLandingPad)this.attachedDock;
            final IDockable rocket = pad.getDockedEntity();
            if (rocket instanceof EntityAutoRocket) {
                ((EntityAutoRocket)rocket).updateControllerSettings((IFuelDock)pad);
            }
        }
    }
    
    public void setAttachedPad(final IFuelDock pad) {
        this.attachedDock = pad;
    }
}
