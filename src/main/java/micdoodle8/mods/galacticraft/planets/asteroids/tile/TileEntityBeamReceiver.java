package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.power.*;
import com.google.common.collect.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.nbt.*;

public class TileEntityBeamReceiver extends TileEntityBeamOutput implements IEnergyHandlerGC, ILaserNode
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int facing;
    private int preLoadFacing;
    private float maxRate;
    private EnergyStorage storage;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int modeReceive;
    public Vector3 color;
    
    public TileEntityBeamReceiver() {
        this.facing = ForgeDirection.UNKNOWN.ordinal();
        this.preLoadFacing = -1;
        this.maxRate = 1500.0f;
        this.storage = new EnergyStorage(10.0f * this.maxRate, this.maxRate);
        this.modeReceive = ReceiverMode.UNDEFINED.ordinal();
        this.color = new Vector3(0.0, 1.0, 0.0);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (this.preLoadFacing != -1) {
            this.setFacing(ForgeDirection.getOrientation(this.preLoadFacing));
            this.preLoadFacing = -1;
        }
        if (!this.worldObj.isRemote) {
            if (this.getTarget() != null && this.modeReceive == ReceiverMode.EXTRACT.ordinal() && this.facing != ForgeDirection.UNKNOWN.ordinal()) {
                final TileEntity tile = this.getAttachedTile();
                if (tile instanceof TileBaseUniversalElectricalSource) {
                    final TileBaseUniversalElectricalSource electricalTile = (TileBaseUniversalElectricalSource)tile;
                    if (electricalTile.storage.getEnergyStoredGC() > 0.0f) {
                        final EnergySource.EnergySourceAdjacent source = new EnergySource.EnergySourceAdjacent(ForgeDirection.getOrientation(this.facing ^ 0x1));
                        final float toSend = Math.min(electricalTile.storage.getMaxExtract(), electricalTile.storage.getEnergyStoredGC());
                        final float transmitted = this.getTarget().receiveEnergyGC((EnergySource)new EnergySource.EnergySourceWireless((List)Lists.newArrayList((Object[])new ILaserNode[] { (ILaserNode)this })), toSend, false);
                        electricalTile.extractEnergyGC((EnergySource)source, transmitted, false);
                    }
                }
                else if (!(tile instanceof EnergyStorageTile) && !(tile instanceof TileBaseConductor)) {
                    final ForgeDirection inputAdj = ForgeDirection.getOrientation(this.facing);
                    final float availableToSend = EnergyUtil.otherModsEnergyExtract(tile, inputAdj, this.maxRate, true);
                    if (availableToSend > 0.0f) {
                        final float transmitted2 = this.getTarget().receiveEnergyGC((EnergySource)new EnergySource.EnergySourceWireless((List)Lists.newArrayList((Object[])new ILaserNode[] { (ILaserNode)this })), availableToSend, false);
                        EnergyUtil.otherModsEnergyExtract(tile, inputAdj, transmitted2, false);
                    }
                }
            }
            else if (this.modeReceive == ReceiverMode.RECEIVE.ordinal() && this.storage.getEnergyStoredGC() > 0.0f) {
                final float maxTransfer = Math.min(this.storage.getEnergyStoredGC(), this.maxRate * 5.0f);
                if (maxTransfer < 0.01f) {
                    this.storage.extractEnergyGCnoMax(maxTransfer, false);
                }
                else {
                    final TileEntity tileAdj = this.getAttachedTile();
                    if (tileAdj instanceof TileBaseUniversalElectrical) {
                        final TileBaseUniversalElectrical electricalTile2 = (TileBaseUniversalElectrical)tileAdj;
                        final EnergySource.EnergySourceAdjacent source2 = new EnergySource.EnergySourceAdjacent(ForgeDirection.getOrientation(this.facing ^ 0x1));
                        this.storage.extractEnergyGCnoMax(electricalTile2.receiveEnergyGC((EnergySource)source2, maxTransfer, false), false);
                    }
                    else if (!(tileAdj instanceof EnergyStorageTile) && !(tileAdj instanceof TileBaseConductor)) {
                        final ForgeDirection inputAdj2 = ForgeDirection.getOrientation(this.facing);
                        final float otherModTransferred = EnergyUtil.otherModsEnergyTransfer(tileAdj, inputAdj2, maxTransfer, false);
                        if (otherModTransferred > 0.0f) {
                            this.storage.extractEnergyGCnoMax(otherModTransferred, false);
                        }
                    }
                }
            }
        }
    }
    
    public double getPacketRange() {
        return 24.0;
    }
    
    public int getPacketCooldown() {
        return 3;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
    
    public Vector3 getInputPoint() {
        final Vector3 headVec = new Vector3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
        final ForgeDirection facingDir = ForgeDirection.getOrientation(this.facing);
        final Vector3 vector3 = headVec;
        vector3.x += facingDir.offsetX * 0.1f;
        final Vector3 vector4 = headVec;
        vector4.y += facingDir.offsetY * 0.1f;
        final Vector3 vector5 = headVec;
        vector5.z += facingDir.offsetZ * 0.1f;
        return headVec;
    }
    
    public Vector3 getOutputPoint(final boolean offset) {
        final Vector3 headVec = new Vector3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
        final ForgeDirection facingDir = ForgeDirection.getOrientation(this.facing);
        final Vector3 vector3 = headVec;
        vector3.x += facingDir.offsetX * 0.1f;
        final Vector3 vector4 = headVec;
        vector4.y += facingDir.offsetY * 0.1f;
        final Vector3 vector5 = headVec;
        vector5.z += facingDir.offsetZ * 0.1f;
        return headVec;
    }
    
    public TileEntity getTile() {
        return (TileEntity)this;
    }
    
    public TileEntity getAttachedTile() {
        if (this.facing == ForgeDirection.UNKNOWN.ordinal()) {
            return null;
        }
        final TileEntity tile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, this.facing);
        if (tile == null || tile.isInvalid()) {
            this.setFacing(ForgeDirection.UNKNOWN);
        }
        if (tile instanceof IConductor) {
            this.setFacing(ForgeDirection.UNKNOWN);
            return null;
        }
        if (tile instanceof EnergyStorageTile) {
            final EnergyStorage attachedStorage = ((EnergyStorageTile)tile).storage;
            this.storage.setCapacity(attachedStorage.getCapacityGC() - attachedStorage.getEnergyStoredGC());
            this.storage.setMaxExtract(attachedStorage.getMaxExtract());
            this.storage.setMaxReceive(attachedStorage.getMaxReceive());
        }
        return tile;
    }
    
    public float receiveEnergyGC(final EnergySource from, final float amount, final boolean simulate) {
        if (this.modeReceive != ReceiverMode.RECEIVE.ordinal()) {
            return 0.0f;
        }
        this.getAttachedTile();
        if (this.facing == ForgeDirection.UNKNOWN.ordinal()) {
            return 0.0f;
        }
        return this.storage.receiveEnergyGC(amount, simulate);
    }
    
    public float extractEnergyGC(final EnergySource from, final float amount, final boolean simulate) {
        if (this.modeReceive != ReceiverMode.EXTRACT.ordinal()) {
            return 0.0f;
        }
        final TileEntity tile = this.getAttachedTile();
        if (this.facing == ForgeDirection.UNKNOWN.ordinal()) {
            return 0.0f;
        }
        float extracted = this.storage.extractEnergyGC(amount, simulate);
        if (extracted < amount && tile instanceof EnergyStorageTile) {
            extracted += ((EnergyStorageTile)tile).storage.extractEnergyGC(amount - extracted, simulate);
        }
        return extracted;
    }
    
    public float getEnergyStoredGC(final EnergySource from) {
        final TileEntity tile = this.getAttachedTile();
        if (this.facing == ForgeDirection.UNKNOWN.ordinal()) {
            return 0.0f;
        }
        return this.storage.getEnergyStoredGC();
    }
    
    public float getMaxEnergyStoredGC(final EnergySource from) {
        final TileEntity tile = this.getAttachedTile();
        if (this.facing == ForgeDirection.UNKNOWN.ordinal()) {
            return 0.0f;
        }
        return this.storage.getCapacityGC();
    }
    
    public boolean nodeAvailable(final EnergySource from) {
        final TileEntity tile = this.getAttachedTile();
        return this.facing != ForgeDirection.UNKNOWN.ordinal();
    }
    
    public void setFacing(final ForgeDirection newDirection) {
        if (newDirection.ordinal() != this.facing) {
            if (newDirection == ForgeDirection.UNKNOWN) {
                this.modeReceive = ReceiverMode.UNDEFINED.ordinal();
            }
            else {
                final TileEntity tile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, newDirection);
                if (tile == null) {
                    this.modeReceive = ReceiverMode.UNDEFINED.ordinal();
                }
                else if (tile instanceof EnergyStorageTile) {
                    final ReceiverMode mode = ((EnergyStorageTile)tile).getModeFromDirection(newDirection.getOpposite());
                    if (mode != null) {
                        this.modeReceive = mode.ordinal();
                    }
                    else {
                        this.modeReceive = ReceiverMode.UNDEFINED.ordinal();
                    }
                }
                else if (EnergyUtil.otherModCanReceive(tile, newDirection.getOpposite())) {
                    this.modeReceive = ReceiverMode.RECEIVE.ordinal();
                }
                else if (EnergyUtil.otherModCanProduce(tile, newDirection.getOpposite())) {
                    this.modeReceive = ReceiverMode.EXTRACT.ordinal();
                }
            }
        }
        this.facing = newDirection.ordinal();
    }
    
    public boolean canConnectTo(final ILaserNode laserNode) {
        return this.modeReceive != ReceiverMode.UNDEFINED.ordinal() && this.color.equals((Object)laserNode.getColor()) && (!(laserNode instanceof TileEntityBeamReceiver) || ((TileEntityBeamReceiver)laserNode).modeReceive != this.modeReceive);
    }
    
    public Vector3 getColor() {
        return new Vector3(0.0, 1.0, 0.0);
    }
    
    public ILaserNode getTarget() {
        if (this.modeReceive == ReceiverMode.EXTRACT.ordinal()) {
            return super.getTarget();
        }
        return null;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.preLoadFacing = nbt.getInteger("FacingSide");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("FacingSide", this.facing);
    }
}
