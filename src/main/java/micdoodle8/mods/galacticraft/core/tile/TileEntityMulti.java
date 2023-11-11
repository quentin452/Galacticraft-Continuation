package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import java.util.*;

public class TileEntityMulti extends TileEntityAdvanced
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public BlockVec3 mainBlockPosition;
    
    public void setMainBlock(final BlockVec3 mainBlock) {
        this.mainBlockPosition = mainBlock;
        if (!this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }
    
    public void onBlockRemoval() {
        if (this.mainBlockPosition != null) {
            final TileEntity tileEntity = this.worldObj.getTileEntity(this.mainBlockPosition.x, this.mainBlockPosition.y, this.mainBlockPosition.z);
            if (tileEntity instanceof IMultiBlock) {
                final IMultiBlock mainBlock = (IMultiBlock)tileEntity;
                mainBlock.onDestroy((TileEntity)this);
            }
        }
    }
    
    public boolean onBlockActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer) {
        if (this.mainBlockPosition != null) {
            final TileEntity tileEntity = this.worldObj.getTileEntity(this.mainBlockPosition.x, this.mainBlockPosition.y, this.mainBlockPosition.z);
            if (tileEntity instanceof IMultiBlock) {
                return ((IMultiBlock)tileEntity).onActivated(par5EntityPlayer);
            }
        }
        return false;
    }
    
    public TileEntity getMainBlockTile() {
        if (this.mainBlockPosition != null) {
            return this.worldObj.getTileEntity(this.mainBlockPosition.x, this.mainBlockPosition.y, this.mainBlockPosition.z);
        }
        return null;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mainBlockPosition = new BlockVec3(nbt.getCompoundTag("mainBlockPosition"));
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.mainBlockPosition != null) {
            nbt.setTag("mainBlockPosition", (NBTBase)this.mainBlockPosition.writeToNBT(new NBTTagCompound()));
        }
    }
    
    public double getPacketRange() {
        return 30.0;
    }
    
    public int getPacketCooldown() {
        return 50;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
    
    public void getNetworkedData(final ArrayList<Object> sendData) {
        if (this.mainBlockPosition == null) {
            return;
        }
        super.getNetworkedData((ArrayList)sendData);
    }
}
