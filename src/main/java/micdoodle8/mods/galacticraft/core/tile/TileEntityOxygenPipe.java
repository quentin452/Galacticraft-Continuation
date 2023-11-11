package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.nbt.*;
import io.netty.buffer.*;
import net.minecraft.client.multiplayer.*;

public class TileEntityOxygenPipe extends TileEntityOxygenTransmitter implements IColorable
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public byte pipeColor;
    private byte lastPipeColor;
    
    public TileEntityOxygenPipe() {
        this.pipeColor = 15;
        this.lastPipeColor = -1;
    }
    
    @Override
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        final TileEntity adjacentTile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, direction);
        return type == NetworkType.OXYGEN && (!(adjacentTile instanceof IColorable) || this.getColor() == ((IColorable)adjacentTile).getColor());
    }
    
    @Override
    public boolean canUpdate() {
        return this.worldObj == null || !this.worldObj.isRemote;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (this.ticks % 60 == 0 && this.lastPipeColor != this.getColor() && !this.worldObj.isRemote) {
            GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketDynamic((TileEntity)this), this.worldObj.provider.dimensionId);
            this.lastPipeColor = this.getColor();
        }
    }
    
    public double getPacketRange() {
        return 12.0;
    }
    
    public int getPacketCooldown() {
        return 5;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
    
    @Override
    public void validate() {
        super.validate();
        if (this.worldObj != null && this.worldObj.isRemote) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
    }
    
    public void setColor(final byte col) {
        this.pipeColor = col;
        if (this.worldObj != null) {
            if (this.worldObj.isRemote) {
                this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
            }
            else {
                this.getNetwork().split((Object)this);
                this.resetNetwork();
            }
        }
    }
    
    public byte getColor() {
        return this.pipeColor;
    }
    
    public void onAdjacentColorChanged(final ForgeDirection direction) {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        if (!this.worldObj.isRemote) {
            this.refresh();
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.setColor(par1NBTTagCompound.getByte("pipeColor"));
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("pipeColor", this.getColor());
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        final byte colorBefore = this.pipeColor;
        super.decodePacketdata(buffer);
        if (this.pipeColor != colorBefore && this.worldObj instanceof WorldClient) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
    }
}
