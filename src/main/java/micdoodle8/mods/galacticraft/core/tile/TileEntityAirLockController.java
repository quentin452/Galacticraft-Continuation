package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.nbt.*;

public class TileEntityAirLockController extends TileEntityAirLock
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean redstoneActivation;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean playerDistanceActivation;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int playerDistanceSelection;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean playerNameMatches;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public String playerToOpenFor;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean invertSelection;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean horizontalModeEnabled;
    public boolean lastHorizontalModeEnabled;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public String ownerName;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean active;
    public boolean lastActive;
    public ArrayList<TileEntityAirLock> otherAirLocks;
    public ArrayList<TileEntityAirLock> lastOtherAirLocks;
    private AirLockProtocol protocol;
    private AirLockProtocol lastProtocol;
    
    public TileEntityAirLockController() {
        this.playerToOpenFor = "";
        this.ownerName = "";
        this.lastProtocol = this.protocol;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            this.active = false;
            if (this.redstoneActivation) {
                this.active = !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
            }
            if ((this.active || !this.redstoneActivation) && this.playerDistanceActivation) {
                double distance = 0.0;
                switch (this.playerDistanceSelection) {
                    case 0: {
                        distance = 1.0;
                        break;
                    }
                    case 1: {
                        distance = 2.0;
                        break;
                    }
                    case 2: {
                        distance = 5.0;
                        break;
                    }
                    case 3: {
                        distance = 10.0;
                        break;
                    }
                }
                final Vector3 thisPos = new Vector3((TileEntity)this).translate(0.5);
                final Vector3 minPos = new Vector3(thisPos).translate(-distance);
                final Vector3 maxPos = new Vector3(thisPos).translate(distance);
                final AxisAlignedBB matchingRegion = AxisAlignedBB.getBoundingBox(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z);
                final List playersWithin = this.worldObj.getEntitiesWithinAABB((Class)EntityPlayer.class, matchingRegion);
                boolean foundPlayer = false;
                for (int i = 0; i < playersWithin.size(); ++i) {
                    final Object o = playersWithin.get(i);
                    if (o instanceof EntityPlayer) {
                        if (!this.playerNameMatches) {
                            foundPlayer = true;
                            break;
                        }
                        if (((EntityPlayer)o).getGameProfile().getName().equalsIgnoreCase(this.playerToOpenFor)) {
                            foundPlayer = true;
                            break;
                        }
                    }
                }
                this.active = !foundPlayer;
            }
            if (this.invertSelection) {
                this.active = !this.active;
            }
            if (this.protocol == null) {
                final AirLockProtocol airLockProtocol = new AirLockProtocol((TileEntity)this);
                this.lastProtocol = airLockProtocol;
                this.protocol = airLockProtocol;
            }
            if (this.ticks % 10 == 0) {
                if (this.horizontalModeEnabled != this.lastHorizontalModeEnabled) {
                    this.unsealAirLock();
                }
                else {
                    this.otherAirLocks = (ArrayList<TileEntityAirLock>)this.protocol.calculate(this.horizontalModeEnabled);
                    if (this.active && (this.otherAirLocks != null || (this.lastOtherAirLocks != null && this.otherAirLocks != this.lastOtherAirLocks) || (this.lastOtherAirLocks != null && this.otherAirLocks.size() != this.lastOtherAirLocks.size()))) {
                        this.sealAirLock();
                    }
                    else if ((!this.active && this.lastActive) || (this.otherAirLocks == null && this.lastOtherAirLocks != null)) {
                        this.unsealAirLock();
                    }
                }
                if (this.active != this.lastActive) {
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                }
                this.lastActive = this.active;
                this.lastOtherAirLocks = this.otherAirLocks;
                this.lastProtocol = this.protocol;
                this.lastHorizontalModeEnabled = this.horizontalModeEnabled;
            }
        }
    }
    
    public void sealAirLock() {
        int x = this.lastProtocol.minX + (this.lastProtocol.maxX - this.lastProtocol.minX) / 2;
        int y = this.lastProtocol.minY + (this.lastProtocol.maxY - this.lastProtocol.minY) / 2;
        int z = this.lastProtocol.minZ + (this.lastProtocol.maxZ - this.lastProtocol.minZ) / 2;
        if (this.worldObj.getBlock(x, y, z) != GCBlocks.airLockSeal) {
            this.worldObj.playSoundEffect((double)x, (double)y, (double)z, GalacticraftCore.TEXTURE_PREFIX + "player.openairlock", 1.0f, 1.0f);
        }
        if (this.horizontalModeEnabled) {
            if (this.protocol.minY == this.protocol.maxY && this.protocol.minX != this.protocol.maxX && this.protocol.minZ != this.protocol.maxZ) {
                for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; ++x) {
                    for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; ++z) {
                        final Block blockAt = this.worldObj.getBlock(x, y, z);
                        if (blockAt != null && blockAt.isAir((IBlockAccess)this.worldObj, x, y, z)) {
                            this.worldObj.setBlock(x, this.protocol.minY, z, GCBlocks.airLockSeal, 0, 3);
                        }
                    }
                }
            }
        }
        else if (this.protocol.minX != this.protocol.maxX) {
            for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; ++x) {
                for (y = this.protocol.minY + 1; y <= this.protocol.maxY - 1; ++y) {
                    final Block blockAt = this.worldObj.getBlock(x, y, z);
                    if (blockAt != null && blockAt.isAir((IBlockAccess)this.worldObj, x, y, z)) {
                        this.worldObj.setBlock(x, y, this.protocol.minZ, GCBlocks.airLockSeal, 0, 3);
                    }
                }
            }
        }
        else if (this.protocol.minZ != this.protocol.maxZ) {
            for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; ++z) {
                for (y = this.protocol.minY + 1; y <= this.protocol.maxY - 1; ++y) {
                    final Block block = this.worldObj.getBlock(x, y, z);
                    if (block != null && block.isAir((IBlockAccess)this.worldObj, x, y, z)) {
                        this.worldObj.setBlock(this.protocol.minX, y, z, GCBlocks.airLockSeal, 0, 3);
                    }
                }
            }
        }
    }
    
    public void unsealAirLock() {
        if (this.lastProtocol == null) {
            return;
        }
        int x = this.lastProtocol.minX + (this.lastProtocol.maxX - this.lastProtocol.minX) / 2;
        int y = this.lastProtocol.minY + (this.lastProtocol.maxY - this.lastProtocol.minY) / 2;
        int z = this.lastProtocol.minZ + (this.lastProtocol.maxZ - this.lastProtocol.minZ) / 2;
        if (this.worldObj.getBlock(x, y, z).getMaterial() != Material.air) {
            this.worldObj.playSoundEffect((double)x, (double)y, (double)z, GalacticraftCore.TEXTURE_PREFIX + "player.closeairlock", 1.0f, 1.0f);
        }
        if (this.lastHorizontalModeEnabled) {
            if (this.protocol.minY == this.protocol.maxY && this.protocol.minX != this.protocol.maxX && this.protocol.minZ != this.protocol.maxZ) {
                for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; ++x) {
                    for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; ++z) {
                        final Block blockAt = this.worldObj.getBlock(x, y, z);
                        if (blockAt == GCBlocks.airLockSeal) {
                            this.worldObj.setBlockToAir(x, this.protocol.minY, z);
                        }
                    }
                }
            }
        }
        else if (this.lastProtocol.minX != this.lastProtocol.maxX) {
            for (x = this.lastProtocol.minX + 1; x <= this.lastProtocol.maxX - 1; ++x) {
                for (y = this.lastProtocol.minY + 1; y <= this.lastProtocol.maxY - 1; ++y) {
                    final Block blockAt = this.worldObj.getBlock(x, y, z);
                    if (blockAt == GCBlocks.airLockSeal) {
                        this.worldObj.setBlockToAir(x, y, this.lastProtocol.minZ);
                    }
                }
            }
        }
        else if (this.lastProtocol.minZ != this.lastProtocol.maxZ) {
            for (z = this.lastProtocol.minZ + 1; z <= this.lastProtocol.maxZ - 1; ++z) {
                for (y = this.lastProtocol.minY + 1; y <= this.lastProtocol.maxY - 1; ++y) {
                    final Block blockAt = this.worldObj.getBlock(x, y, z);
                    if (blockAt == GCBlocks.airLockSeal) {
                        this.worldObj.setBlockToAir(this.lastProtocol.minX, y, z);
                    }
                }
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.ownerName = nbt.getString("OwnerName");
        this.redstoneActivation = nbt.getBoolean("RedstoneActivation");
        this.playerDistanceActivation = nbt.getBoolean("PlayerDistanceActivation");
        this.playerDistanceSelection = nbt.getInteger("PlayerDistanceSelection");
        this.playerNameMatches = nbt.getBoolean("PlayerNameMatches");
        this.playerToOpenFor = nbt.getString("PlayerToOpenFor");
        this.invertSelection = nbt.getBoolean("InvertSelection");
        this.active = nbt.getBoolean("active");
        this.lastActive = nbt.getBoolean("lastActive");
        this.horizontalModeEnabled = nbt.getBoolean("HorizontalModeEnabled");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("OwnerName", this.ownerName);
        nbt.setBoolean("RedstoneActivation", this.redstoneActivation);
        nbt.setBoolean("PlayerDistanceActivation", this.playerDistanceActivation);
        nbt.setInteger("PlayerDistanceSelection", this.playerDistanceSelection);
        nbt.setBoolean("PlayerNameMatches", this.playerNameMatches);
        nbt.setString("PlayerToOpenFor", this.playerToOpenFor);
        nbt.setBoolean("InvertSelection", this.invertSelection);
        nbt.setBoolean("active", this.active);
        nbt.setBoolean("lastActive", this.lastActive);
        nbt.setBoolean("HorizontalModeEnabled", this.horizontalModeEnabled);
    }
    
    public double getPacketRange() {
        return 20.0;
    }
    
    public int getPacketCooldown() {
        return 3;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
}
