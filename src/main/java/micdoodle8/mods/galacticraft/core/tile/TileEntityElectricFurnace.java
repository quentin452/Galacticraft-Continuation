package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;

public class TileEntityElectricFurnace extends TileBaseElectricBlockWithInventory implements ISidedInventory
{
    public static int PROCESS_TIME_REQUIRED;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTimeRequired;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack[] containingItems;
    public final Set<EntityPlayer> playersUsing;
    private boolean initialised;
    
    public TileEntityElectricFurnace() {
        this(1);
    }
    
    public TileEntityElectricFurnace(final int tier) {
        this.processTimeRequired = TileEntityElectricFurnace.PROCESS_TIME_REQUIRED;
        this.processTicks = 0;
        this.containingItems = new ItemStack[3];
        this.playersUsing = new HashSet<EntityPlayer>();
        this.initialised = false;
        this.initialised = true;
        if (tier == 1) {
            this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 60.0f : 45.0f);
            return;
        }
        this.setTier2();
    }
    
    private void setTier2() {
        this.storage.setCapacity(25000.0f);
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90.0f : 60.0f);
        this.processTimeRequired = 100;
        this.setTierGC(2);
    }
    
    public void updateEntity() {
        if (!this.initialised) {
            final int metadata = this.getBlockMetadata();
            final Block b = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
            if (b == GCBlocks.machineBase) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, GCBlocks.machineTiered, 4, 2);
            }
            else if (metadata >= 8) {
                this.setTier2();
            }
            this.initialised = true;
        }
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.canProcess()) {
                if (this.hasEnoughEnergyToRun) {
                    if (this.tierGC == 2) {
                        this.processTimeRequired = 200 / (1 + this.poweredByTierGC);
                    }
                    if (this.processTicks == 0) {
                        this.processTicks = this.processTimeRequired;
                    }
                    else if (--this.processTicks <= 0) {
                        this.smeltItem();
                        this.processTicks = (this.canProcess() ? this.processTimeRequired : 0);
                    }
                }
                else if (this.processTicks > 0 && this.processTicks < this.processTimeRequired && this.worldObj.rand.nextInt(4) == 0) {
                    ++this.processTicks;
                }
            }
            else {
                this.processTicks = 0;
            }
        }
    }
    
    public boolean canProcess() {
        if (this.containingItems[1] == null || FurnaceRecipes.smelting().getSmeltingResult(this.containingItems[1]) == null) {
            return false;
        }
        if (this.containingItems[2] != null) {
            if (!this.containingItems[2].isItemEqual(FurnaceRecipes.smelting().getSmeltingResult(this.containingItems[1]))) {
                return false;
            }
            if (this.containingItems[2].stackSize + 1 > 64) {
                return false;
            }
        }
        return true;
    }
    
    public void smeltItem() {
        if (this.canProcess()) {
            final ItemStack resultItemStack = FurnaceRecipes.smelting().getSmeltingResult(this.containingItems[1]);
            if (this.containingItems[2] == null) {
                this.containingItems[2] = resultItemStack.copy();
                if (this.tierGC > 1) {
                    final String nameSmelted = this.containingItems[1].getUnlocalizedName().toLowerCase();
                    if (resultItemStack.getUnlocalizedName().toLowerCase().contains("ingot") && (nameSmelted.contains("ore") || nameSmelted.contains("raw") || nameSmelted.contains("moon") || nameSmelted.contains("mars") || nameSmelted.contains("shard"))) {
                        final ItemStack itemStack = this.containingItems[2];
                        itemStack.stackSize += resultItemStack.stackSize;
                    }
                }
            }
            else if (this.containingItems[2].isItemEqual(resultItemStack)) {
                final ItemStack itemStack2 = this.containingItems[2];
                itemStack2.stackSize += resultItemStack.stackSize;
                if (this.tierGC > 1) {
                    final String nameSmelted = this.containingItems[1].getUnlocalizedName().toLowerCase();
                    if (resultItemStack.getUnlocalizedName().toLowerCase().contains("ingot") && (nameSmelted.contains("ore") || nameSmelted.contains("raw") || nameSmelted.contains("moon") || nameSmelted.contains("mars") || nameSmelted.contains("shard"))) {
                        final ItemStack itemStack3 = this.containingItems[2];
                        itemStack3.stackSize += resultItemStack.stackSize;
                    }
                }
            }
            final ItemStack itemStack4 = this.containingItems[1];
            --itemStack4.stackSize;
            if (this.containingItems[1].stackSize <= 0) {
                this.containingItems[1] = null;
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (this.storage.getEnergyStoredGC() > 16000.0f) {
            this.setTier2();
            this.initialised = true;
        }
        else {
            this.initialised = false;
        }
        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks");
        this.containingItems = this.readStandardItemsFromNBT(par1NBTTagCompound);
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > 16000.0f) {
            this.storage.setEnergyStored(16000.0f);
        }
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks);
        this.writeStandardItemsToNBT(par1NBTTagCompound);
    }
    
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate((this.tierGC == 1) ? "tile.machine.2.name" : "tile.machine.7.name");
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {
        return itemStack != null && ((slotID == 1) ? (FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null) : (slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem())));
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1, 2 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return this.isItemValidForSlot(slotID, par2ItemStack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return slotID == 2;
    }
    
    public boolean shouldUseEnergy() {
        return this.canProcess();
    }
    
    static {
        TileEntityElectricFurnace.PROCESS_TIME_REQUIRED = 130;
    }
}
