package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.client.*;
import net.minecraft.block.*;
import net.minecraft.nbt.*;
import java.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;

public class TileEntityDish extends TileBaseUniversalElectrical implements IMultiBlock, IDisableableMachine, IInventory, ISidedInventory, IConnector
{
    public float targetAngle;
    public float currentAngle;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean disabled;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int disableCooldown;
    private ItemStack[] containingItems;
    private boolean initialised;
    
    public TileEntityDish() {
        this.disabled = false;
        this.disableCooldown = 0;
        this.containingItems = new ItemStack[1];
        this.initialised = false;
        this.storage.setMaxExtract(100.0f);
        this.setTierGC(1);
        this.initialised = true;
    }
    
    public void updateEntity() {
        if (!this.initialised) {
            this.initialised = true;
        }
        super.updateEntity();
        if (!this.worldObj.isRemote && this.disableCooldown > 0) {
            --this.disableCooldown;
        }
        final float angle = (this.worldObj.getCelestialAngle(1.0f) - 0.7845194f < 0.0f) ? 0.21548063f : -0.7845194f;
        float celestialAngle = (this.worldObj.getCelestialAngle(1.0f) + angle) * 360.0f;
        celestialAngle %= 360.0f;
        if (celestialAngle > 30.0f && celestialAngle < 150.0f) {
            final float difference = this.targetAngle - celestialAngle;
            this.targetAngle -= difference / 20.0f;
        }
        else if (!this.worldObj.isDaytime() || this.worldObj.isRaining() || this.worldObj.isThundering()) {
            this.targetAngle = 257.5f;
        }
        else if (celestialAngle < 50.0f) {
            this.targetAngle = 50.0f;
        }
        else if (celestialAngle > 150.0f) {
            this.targetAngle = 150.0f;
        }
        final float difference = this.targetAngle - this.currentAngle;
        this.currentAngle += difference / 20.0f;
    }
    
    public boolean onActivated(final EntityPlayer entityPlayer) {
        return this.getBlockType().onBlockActivated(this.worldObj, this.xCoord, this.yCoord, this.zCoord, entityPlayer, 0, (float)this.xCoord, (float)this.yCoord, (float)this.zCoord);
    }
    
    public boolean canUpdate() {
        return true;
    }
    
    public void onCreate(final BlockVec3 placedPosition) {
        final int buildHeight = this.worldObj.getHeight() - 1;
        if (placedPosition.y + 1 > buildHeight) {
            return;
        }
        final BlockVec3 vecStrut = new BlockVec3(placedPosition.x, placedPosition.y + 1, placedPosition.z);
        ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecStrut, placedPosition, 0);
        if (placedPosition.y + 2 > buildHeight) {
            return;
        }
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                final BlockVec3 vecToAdd = new BlockVec3(placedPosition.x + x, placedPosition.y + 2, placedPosition.z + z);
                ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, (this.getTierGC() == 1) ? 4 : 0);
            }
        }
    }
    
    public void onDestroy(final TileEntity callingBlock) {
        final BlockVec3 thisBlock = new BlockVec3((TileEntity)this);
        for (int y = 1; y <= 2; ++y) {
            for (int x = -1; x < 2; ++x) {
                for (int z = -1; z < 2; ++z) {
                    if (this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.1) {
                        FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(thisBlock.x + ((y == 2) ? x : 0), thisBlock.y + y, thisBlock.z + ((y == 2) ? z : 0), GCBlocks.radioTelescope, Block.getIdFromBlock(GCBlocks.radioTelescope) >> 12 & 0xFF);
                    }
                    this.worldObj.setBlockToAir(thisBlock.x + ((y == 2) ? x : 0), thisBlock.y + y, thisBlock.z + ((y == 2) ? z : 0));
                }
            }
        }
        this.worldObj.func_147480_a(thisBlock.x, thisBlock.y, thisBlock.z, true);
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.storage.setCapacity(nbt.getFloat("maxEnergy"));
        this.currentAngle = nbt.getFloat("currentAngle");
        this.targetAngle = nbt.getFloat("targetAngle");
        this.setDisabled(0, nbt.getBoolean("disabled"));
        this.disableCooldown = nbt.getInteger("disabledCooldown");
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        this.initialised = false;
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setFloat("maxEnergy", this.getMaxEnergyStoredGC());
        nbt.setFloat("currentAngle", this.currentAngle);
        nbt.setFloat("targetAngle", this.targetAngle);
        nbt.setInteger("disabledCooldown", this.disableCooldown);
        nbt.setBoolean("disabled", this.getDisabled(0));
        final NBTTagList list = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                list.appendTag((NBTBase)var4);
            }
        }
        nbt.setTag("Items", (NBTBase)list);
    }
    
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)this.yCoord, (double)(this.zCoord - 1), (double)(this.xCoord + 2), (double)(this.yCoord + 4), (double)(this.zCoord + 2));
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public String getInventoryName() {
        return "";
    }
    
    public void setDisabled(final int index, final boolean disabled) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled;
            this.disableCooldown = 20;
        }
    }
    
    public boolean getDisabled(final int index) {
        return this.disabled;
    }
    
    public int getScaledElecticalLevel(final int i) {
        return (int)Math.floor(this.getEnergyStoredGC() * i / this.getMaxEnergyStoredGC());
    }
    
    public int getSizeInventory() {
        return this.containingItems.length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return this.containingItems[par1];
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.containingItems[par1] == null) {
            return null;
        }
        if (this.containingItems[par1].stackSize <= par2) {
            final ItemStack var3 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var3;
        }
        final ItemStack var3 = this.containingItems[par1].splitStack(par2);
        if (this.containingItems[par1].stackSize == 0) {
            this.containingItems[par1] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.containingItems[par1] != null) {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.containingItems[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        return slotID == 0;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem());
    }
}
