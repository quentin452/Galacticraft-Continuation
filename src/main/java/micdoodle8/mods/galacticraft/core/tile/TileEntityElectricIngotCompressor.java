package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;
import java.util.*;
import net.minecraftforge.common.util.*;

public class TileEntityElectricIngotCompressor extends TileBaseElectricBlock implements IInventory, ISidedInventory
{
    public static final int PROCESS_TIME_REQUIRED_BASE = 200;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTimeRequired;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack producingStack;
    private long ticks;
    private ItemStack[] containingItems;
    public PersistantInventoryCrafting compressingCraftMatrix;
    private static Random randnum;
    
    public TileEntityElectricIngotCompressor() {
        this.processTimeRequired = 200;
        this.processTicks = 0;
        this.producingStack = null;
        this.containingItems = new ItemStack[3];
        this.compressingCraftMatrix = new PersistantInventoryCrafting();
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90.0f : 75.0f);
        this.setTierGC(2);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            boolean updateInv = false;
            if (this.hasEnoughEnergyToRun) {
                if (this.canCompress()) {
                    ++this.processTicks;
                    this.processTimeRequired = 400 / (1 + this.poweredByTierGC);
                    if (this.processTicks >= this.processTimeRequired) {
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
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    private boolean canCompress() {
        final ItemStack itemstack = this.producingStack;
        if (itemstack == null) {
            return false;
        }
        if (this.containingItems[1] == null && this.containingItems[2] == null) {
            return true;
        }
        if ((this.containingItems[1] != null && !this.containingItems[1].isItemEqual(itemstack)) || (this.containingItems[2] != null && !this.containingItems[2].isItemEqual(itemstack))) {
            return false;
        }
        final int result = (this.containingItems[1] == null) ? 0 : (this.containingItems[1].stackSize + itemstack.stackSize);
        final int result2 = (this.containingItems[2] == null) ? 0 : (this.containingItems[2].stackSize + itemstack.stackSize);
        return result <= this.getInventoryStackLimit() && result <= itemstack.getMaxStackSize() && result2 <= this.getInventoryStackLimit() && result2 <= itemstack.getMaxStackSize();
    }
    
    public void updateInput() {
        this.producingStack = CompressorRecipes.findMatchingRecipe((IInventory)this.compressingCraftMatrix, this.worldObj);
    }
    
    public void compressItems() {
        final int stackSize1 = (this.containingItems[1] == null) ? 0 : this.containingItems[1].stackSize;
        final int stackSize2 = (this.containingItems[2] == null) ? 0 : this.containingItems[2].stackSize;
        if (stackSize1 <= stackSize2) {
            this.compressIntoSlot(1);
            this.compressIntoSlot(2);
        }
        else {
            this.compressIntoSlot(2);
            this.compressIntoSlot(1);
        }
    }
    
    private void compressIntoSlot(final int slot) {
        if (this.canCompress()) {
            final ItemStack resultItemStack = this.producingStack.copy();
            if (ConfigManagerCore.quickMode && resultItemStack.getItem().getUnlocalizedName(resultItemStack).contains("compressed")) {
                final ItemStack itemStack = resultItemStack;
                itemStack.stackSize *= 2;
            }
            if (this.containingItems[slot] == null) {
                this.containingItems[slot] = resultItemStack;
            }
            else if (this.containingItems[slot].isItemEqual(resultItemStack)) {
                if (this.containingItems[slot].stackSize + resultItemStack.stackSize > 64) {
                    for (int i = 0; i < this.containingItems[slot].stackSize + resultItemStack.stackSize - 64; ++i) {
                        final float var = 0.7f;
                        final double dx = this.worldObj.rand.nextFloat() * var + (1.0f - var) * 0.5;
                        final double dy = this.worldObj.rand.nextFloat() * var + (1.0f - var) * 0.5;
                        final double dz = this.worldObj.rand.nextFloat() * var + (1.0f - var) * 0.5;
                        final EntityItem entityitem = new EntityItem(this.worldObj, this.xCoord + dx, this.yCoord + dy, this.zCoord + dz, new ItemStack(resultItemStack.getItem(), 1, resultItemStack.getItemDamage()));
                        entityitem.delayBeforeCanPickup = 10;
                        this.worldObj.spawnEntityInWorld((Entity)entityitem);
                    }
                    this.containingItems[slot].stackSize = 64;
                }
                else {
                    final ItemStack itemStack2 = this.containingItems[slot];
                    itemStack2.stackSize += resultItemStack.stackSize;
                }
            }
            for (int i = 0; i < this.compressingCraftMatrix.getSizeInventory(); ++i) {
                if (this.compressingCraftMatrix.getStackInSlot(i) != null && this.compressingCraftMatrix.getStackInSlot(i).getItem() == Items.water_bucket) {
                    this.compressingCraftMatrix.setInventorySlotContentsNoUpdate(i, new ItemStack(Items.bucket));
                }
                else {
                    this.compressingCraftMatrix.decrStackSize(i, 1);
                }
            }
            this.updateInput();
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks");
        final NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory() - this.compressingCraftMatrix.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
            else if (var5 < this.containingItems.length + this.compressingCraftMatrix.getSizeInventory()) {
                this.compressingCraftMatrix.setInventorySlotContents(var5 - this.containingItems.length, ItemStack.loadItemStackFromNBT(var4));
            }
        }
        this.updateInput();
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks);
        final NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        for (int var3 = 0; var3 < this.compressingCraftMatrix.getSizeInventory(); ++var3) {
            if (this.compressingCraftMatrix.getStackInSlot(var3) != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)(var3 + this.containingItems.length));
                this.compressingCraftMatrix.getStackInSlot(var3).writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        par1NBTTagCompound.setTag("Items", (NBTBase)var2);
    }
    
    public int getSizeInventory() {
        return this.containingItems.length + this.compressingCraftMatrix.getSizeInventory();
    }
    
    public ItemStack getStackInSlot(final int par1) {
        if (par1 >= this.containingItems.length) {
            return this.compressingCraftMatrix.getStackInSlot(par1 - this.containingItems.length);
        }
        return this.containingItems[par1];
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (par1 >= this.containingItems.length) {
            final ItemStack result = this.compressingCraftMatrix.decrStackSize(par1 - this.containingItems.length, par2);
            if (result != null) {
                this.updateInput();
            }
            return result;
        }
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
        if (par1 >= this.containingItems.length) {
            return this.compressingCraftMatrix.getStackInSlotOnClosing(par1 - this.containingItems.length);
        }
        if (this.containingItems[par1] != null) {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        if (par1 >= this.containingItems.length) {
            this.compressingCraftMatrix.setInventorySlotContents(par1 - this.containingItems.length, par2ItemStack);
            this.updateInput();
        }
        else {
            this.containingItems[par1] = par2ItemStack;
            if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
                par2ItemStack.stackSize = this.getInventoryStackLimit();
            }
        }
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.machine2.4.name");
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && entityplayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {
        if (slotID == 0) {
            return itemStack != null && ItemElectricBase.isElectricItem(itemStack.getItem());
        }
        if (slotID < 3) {
            return false;
        }
        if (this.producingStack != null) {
            final ItemStack stackInSlot = this.getStackInSlot(slotID);
            return stackInSlot != null && stackInSlot.isItemEqual(itemStack);
        }
        return this.isItemCompressorInput(itemStack, slotID - 3);
    }
    
    public boolean isItemCompressorInput(final ItemStack stack, final int id) {
        for (final IRecipe recipe : CompressorRecipes.getRecipeList()) {
            if (recipe instanceof ShapedRecipes) {
                if (id >= ((ShapedRecipes)recipe).recipeItems.length) {
                    continue;
                }
                final ItemStack itemstack1 = ((ShapedRecipes)recipe).recipeItems[id];
                if (stack.getItem() == itemstack1.getItem() && (itemstack1.getItemDamage() == 32767 || stack.getItemDamage() == itemstack1.getItemDamage())) {
                    for (int i = 0; i < ((ShapedRecipes)recipe).recipeItems.length; ++i) {
                        if (i != id) {
                            final ItemStack itemstack2 = ((ShapedRecipes)recipe).recipeItems[i];
                            if (stack.getItem() == itemstack2.getItem() && (itemstack2.getItemDamage() == 32767 || stack.getItemDamage() == itemstack2.getItemDamage())) {
                                final ItemStack is3 = this.getStackInSlot(id + 3);
                                final ItemStack is4 = this.getStackInSlot(i + 3);
                                return is3 == null || (is4 != null && is3.stackSize < is4.stackSize);
                            }
                        }
                    }
                    return true;
                }
                continue;
            }
            else {
                if (!(recipe instanceof ShapelessOreRecipe)) {
                    continue;
                }
                final ArrayList<Object> required = new ArrayList<Object>(((ShapelessOreRecipe)recipe).getInput());
                final Iterator<Object> req = required.iterator();
                int match = 0;
                while (req.hasNext()) {
                    final Object next = req.next();
                    if (next instanceof ItemStack) {
                        if (!OreDictionary.itemMatches((ItemStack)next, stack, false)) {
                            continue;
                        }
                        ++match;
                    }
                    else {
                        if (!(next instanceof ArrayList)) {
                            continue;
                        }
                        final Iterator<ItemStack> itr = ((ArrayList)next).iterator();
                        while (itr.hasNext()) {
                            if (OreDictionary.itemMatches((ItemStack)itr.next(), stack, false)) {
                                ++match;
                                break;
                            }
                        }
                    }
                }
                if (match == 0) {
                    continue;
                }
                if (match == 1) {
                    return true;
                }
                int slotsFilled = 0;
                for (int j = 3; j < 12; ++j) {
                    final ItemStack inMatrix = this.getStackInSlot(j);
                    if (inMatrix != null && inMatrix.isItemEqual(stack)) {
                        ++slotsFilled;
                    }
                }
                if (slotsFilled < match) {
                    return this.getStackInSlot(id + 3) == null;
                }
                return TileEntityElectricIngotCompressor.randnum.nextInt(match) == 0;
            }
        }
        return false;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        if (side == 0) {
            return new int[] { 1, 2 };
        }
        final int[] slots = { 0, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        final ArrayList<Integer> removeSlots = new ArrayList<Integer>();
        for (int i = 3; i < 12; ++i) {
            if (!removeSlots.contains(i)) {
                final ItemStack stack1 = this.getStackInSlot(i);
                if (stack1 != null) {
                    if (stack1.stackSize > 0) {
                        for (int j = i + 1; j < 12; ++j) {
                            if (!removeSlots.contains(j)) {
                                final ItemStack stack2 = this.getStackInSlot(j);
                                if (stack2 != null) {
                                    if (stack1.isItemEqual(stack2)) {
                                        if (stack2.stackSize >= stack1.stackSize) {
                                            removeSlots.add(j);
                                            break;
                                        }
                                        removeSlots.add(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (removeSlots.size() > 0) {
            final int[] returnSlots = new int[slots.length - removeSlots.size()];
            int k = 0;
            for (int l = 0; l < slots.length; ++l) {
                if (l <= 0 || !removeSlots.contains(slots[l])) {
                    returnSlots[k] = slots[l];
                    ++k;
                }
            }
            return returnSlots;
        }
        return slots;
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return this.isItemValidForSlot(slotID, par2ItemStack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return slotID == 1 || slotID == 2;
    }
    
    public boolean shouldUseEnergy() {
        return this.processTicks > 0;
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation((this.getBlockMetadata() & 0x3) + 2);
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }
    
    static {
        TileEntityElectricIngotCompressor.randnum = new Random();
    }
}
