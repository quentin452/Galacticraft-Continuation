package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import java.util.*;

public class TileEntityCircuitFabricator extends TileBaseElectricBlockWithInventory implements ISidedInventory
{
    public static final int PROCESS_TIME_REQUIRED = 300;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack producingStack;
    private long ticks;
    private ItemStack[] containingItems;
    
    public TileEntityCircuitFabricator() {
        this.processTicks = 0;
        this.producingStack = null;
        this.containingItems = new ItemStack[7];
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 40.0f : 20.0f);
    }
    
    public void updateEntity() {
        super.updateEntity();
        this.updateInput();
        if (!this.worldObj.isRemote) {
            boolean updateInv = false;
            if (this.hasEnoughEnergyToRun) {
                if (this.canCompress()) {
                    ++this.processTicks;
                    if (this.processTicks == 300) {
                        this.worldObj.playSoundEffect((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, "random.anvil_land", 0.2f, 0.5f);
                        this.processTicks = 0;
                        this.compressItems();
                        updateInv = true;
                    }
                }
                else {
                    this.processTicks = 0;
                }
            }
            else {
                this.processTicks = 0;
            }
            if (updateInv) {
                this.markDirty();
            }
        }
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 0L;
        }
        ++this.ticks;
    }
    
    public void updateInput() {
        this.producingStack = CircuitFabricatorRecipes.getOutputForInput((ItemStack[])Arrays.copyOfRange(this.containingItems, 1, 6));
    }
    
    private boolean canCompress() {
        final ItemStack itemstack = this.producingStack;
        if (itemstack == null) {
            return false;
        }
        if (this.containingItems[6] == null) {
            return true;
        }
        if (this.containingItems[6] != null && !this.containingItems[6].isItemEqual(itemstack)) {
            return false;
        }
        final int result = (this.containingItems[6] == null) ? 0 : (this.containingItems[6].stackSize + itemstack.stackSize);
        return result <= this.getInventoryStackLimit() && result <= itemstack.getMaxStackSize();
    }
    
    public void compressItems() {
        if (this.canCompress()) {
            final ItemStack resultItemStack = this.producingStack.copy();
            if (ConfigManagerCore.quickMode && resultItemStack.getItem() == GCItems.basicItem) {
                if (resultItemStack.getItemDamage() == 13) {
                    resultItemStack.stackSize = 5;
                }
                else if (resultItemStack.getItemDamage() == 14) {
                    resultItemStack.stackSize = 2;
                }
            }
            if (this.containingItems[6] == null) {
                this.containingItems[6] = resultItemStack;
            }
            else if (this.containingItems[6].isItemEqual(resultItemStack)) {
                if (this.containingItems[6].stackSize + resultItemStack.stackSize > 64) {
                    for (int i = 0; i < this.containingItems[6].stackSize + resultItemStack.stackSize - 64; ++i) {
                        final float var = 0.7f;
                        final double dx = this.worldObj.rand.nextFloat() * var + (1.0f - var) * 0.5;
                        final double dy = this.worldObj.rand.nextFloat() * var + (1.0f - var) * 0.5;
                        final double dz = this.worldObj.rand.nextFloat() * var + (1.0f - var) * 0.5;
                        final EntityItem entityitem = new EntityItem(this.worldObj, this.xCoord + dx, this.yCoord + dy, this.zCoord + dz, new ItemStack(resultItemStack.getItem(), 1, resultItemStack.getItemDamage()));
                        entityitem.delayBeforeCanPickup = 10;
                        this.worldObj.spawnEntityInWorld((Entity)entityitem);
                    }
                    this.containingItems[6].stackSize = 64;
                }
                else {
                    final ItemStack itemStack = this.containingItems[6];
                    itemStack.stackSize += resultItemStack.stackSize;
                }
            }
        }
        for (int j = 1; j < 6; ++j) {
            this.decrStackSize(j, 1);
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks");
        this.containingItems = this.readStandardItemsFromNBT(par1NBTTagCompound);
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks);
        this.writeStandardItemsToNBT(par1NBTTagCompound);
    }
    
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.machine2.5.name");
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {
        if (slotID == 0) {
            return itemStack != null && ItemElectricBase.isElectricItem(itemStack.getItem());
        }
        if (slotID > 5) {
            return false;
        }
        final ArrayList<ItemStack> list = CircuitFabricatorRecipes.slotValidItems.get(slotID - 1);
        for (final ItemStack test : list) {
            if (test.isItemEqual(itemStack)) {
                return true;
            }
        }
        return false;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        if (side == 0) {
            return new int[] { 6 };
        }
        final boolean siliconFlag = this.containingItems[2] != null && (this.containingItems[3] == null || this.containingItems[3].stackSize < this.containingItems[2].stackSize);
        return siliconFlag ? new int[] { 0, 1, 3, 4, 5 } : new int[] { 0, 1, 2, 4, 5 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return slotID < 6 && this.isItemValidForSlot(slotID, par2ItemStack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return slotID == 6;
    }
    
    public boolean shouldUseEnergy() {
        return this.processTicks > 0;
    }
}
