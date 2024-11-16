package micdoodle8.mods.galacticraft.core.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.relauncher.Side;
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.inventory.PersistantInventoryCrafting;
import micdoodle8.mods.galacticraft.core.util.Annotations.NetworkedField;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class TileEntityElectricIngotCompressor extends TileBaseElectricBlock implements IInventory, ISidedInventory {

    public static final int PROCESS_TIME_REQUIRED_BASE = 200;

    @NetworkedField(targetSide = Side.CLIENT)
    public int processTimeRequired = PROCESS_TIME_REQUIRED_BASE;

    @NetworkedField(targetSide = Side.CLIENT)
    public int processTicks = 0;

    private ItemStack producingStack = null;
    private long ticks;

    private ItemStack[] containingItems = new ItemStack[3];
    public PersistantInventoryCrafting compressingCraftMatrix = new PersistantInventoryCrafting();
    private static final Random randnum = new Random();

    public TileEntityElectricIngotCompressor() {
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90 : 75);
        this.setTierGC(2);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.worldObj.isRemote) {
            boolean updateInv = false;

            if (this.hasEnoughEnergyToRun && this.canCompress()) {
                ++this.processTicks;

                this.processTimeRequired = TileEntityElectricIngotCompressor.PROCESS_TIME_REQUIRED_BASE * 2
                    / (1 + this.poweredByTierGC);

                if (this.processTicks >= this.processTimeRequired) {
                    this.worldObj
                        .playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "random.anvil_land", 0.2F, 0.5F);
                    this.processTicks = 0;
                    this.compressItems();
                    updateInv = true;
                }
            } else {
                this.processTicks = 0;
            }

            if (updateInv) {
                this.markDirty();
            }
        }

        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 0;
        }

        this.ticks++;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    private boolean canCompress() {
        final ItemStack itemstack = this.producingStack;
        if (itemstack == null) {
            return false;
        }
        if (this.containingItems[1] == null && this.containingItems[2] == null) {
            return true;
        }
        if (this.containingItems[1] != null && !this.containingItems[1].isItemEqual(itemstack)
            || this.containingItems[2] != null && !this.containingItems[2].isItemEqual(itemstack)) {
            return false;
        }
        final int result = this.containingItems[1] == null ? 0
            : this.containingItems[1].stackSize + itemstack.stackSize;
        final int result2 = this.containingItems[2] == null ? 0
            : this.containingItems[2].stackSize + itemstack.stackSize;
        return result <= this.getInventoryStackLimit() && result <= itemstack.getMaxStackSize()
            && result2 <= this.getInventoryStackLimit()
            && result2 <= itemstack.getMaxStackSize();
    }

    public void updateInput() {
        this.producingStack = CompressorRecipes.findMatchingRecipe(this.compressingCraftMatrix, this.worldObj);
    }

    public void compressItems() {
        final int stackSize1 = this.containingItems[1] == null ? 0 : this.containingItems[1].stackSize;
        final int stackSize2 = this.containingItems[2] == null ? 0 : this.containingItems[2].stackSize;

        if (stackSize1 <= stackSize2) {
            this.compressIntoSlot(1);
            this.compressIntoSlot(2);
        } else {
            this.compressIntoSlot(2);
            this.compressIntoSlot(1);
        }
    }

    private void compressIntoSlot(int slot) {
        if (this.canCompress()) {
            final ItemStack resultItemStack = this.producingStack.copy();
            if (ConfigManagerCore.quickMode && resultItemStack.getItem()
                .getUnlocalizedName(resultItemStack)
                .contains("compressed")) {
                resultItemStack.stackSize *= 2;
            }

            if (this.containingItems[slot] == null) {
                this.containingItems[slot] = resultItemStack;
            } else if (this.containingItems[slot].isItemEqual(resultItemStack)) {
                if (this.containingItems[slot].stackSize + resultItemStack.stackSize > 64) {
                    for (int i = 0; i < this.containingItems[slot].stackSize + resultItemStack.stackSize - 64; i++) {
                        final float var = 0.7F;
                        final double dx = this.worldObj.rand.nextFloat() * var + (1.0F - var) * 0.5D;
                        final double dy = this.worldObj.rand.nextFloat() * var + (1.0F - var) * 0.5D;
                        final double dz = this.worldObj.rand.nextFloat() * var + (1.0F - var) * 0.5D;
                        final EntityItem entityitem = new EntityItem(
                            this.worldObj,
                            this.xCoord + dx,
                            this.yCoord + dy,
                            this.zCoord + dz,
                            new ItemStack(resultItemStack.getItem(), 1, resultItemStack.getItemDamage()));

                        entityitem.delayBeforeCanPickup = 10;

                        this.worldObj.spawnEntityInWorld(entityitem);
                    }
                    this.containingItems[slot].stackSize = 64;
                } else {
                    this.containingItems[slot].stackSize += resultItemStack.stackSize;
                }
            }

            for (int i = 0; i < this.compressingCraftMatrix.getSizeInventory(); i++) {
                if (this.compressingCraftMatrix.getStackInSlot(i) != null
                    && this.compressingCraftMatrix.getStackInSlot(i)
                        .getItem() == Items.water_bucket) {
                    this.compressingCraftMatrix.setInventorySlotContentsNoUpdate(i, new ItemStack(Items.bucket));
                } else {
                    this.compressingCraftMatrix.decrStackSize(i, 1);
                }
            }

            this.updateInput();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks");
        final NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory() - this.compressingCraftMatrix.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 255;

            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            } else if (var5 < this.containingItems.length + this.compressingCraftMatrix.getSizeInventory()) {
                this.compressingCraftMatrix
                    .setInventorySlotContents(var5 - this.containingItems.length, ItemStack.loadItemStackFromNBT(var4));
            }
        }

        this.updateInput();
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks);
        final NBTTagList var2 = new NBTTagList();
        int var3;

        for (var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        for (var3 = 0; var3 < this.compressingCraftMatrix.getSizeInventory(); ++var3) {
            if (this.compressingCraftMatrix.getStackInSlot(var3) != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) (var3 + this.containingItems.length));
                this.compressingCraftMatrix.getStackInSlot(var3)
                    .writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    @Override
    public int getSizeInventory() {
        return this.containingItems.length + this.compressingCraftMatrix.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int par1) {
        if (par1 >= this.containingItems.length) {
            return this.compressingCraftMatrix.getStackInSlot(par1 - this.containingItems.length);
        }

        return this.containingItems[par1];
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (par1 >= this.containingItems.length) {
            final ItemStack result = this.compressingCraftMatrix
                .decrStackSize(par1 - this.containingItems.length, par2);
            if (result != null) {
                this.updateInput();
            }
            return result;
        }

        if (this.containingItems[par1] == null) {
            return null;
        }
        ItemStack var3;

        if (this.containingItems[par1].stackSize <= par2) {
            var3 = this.containingItems[par1];
            this.containingItems[par1] = null;
        } else {
            var3 = this.containingItems[par1].splitStack(par2);

            if (this.containingItems[par1].stackSize == 0) {
                this.containingItems[par1] = null;
            }
        }
        return var3;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
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

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        if (par1 >= this.containingItems.length) {
            this.compressingCraftMatrix.setInventorySlotContents(par1 - this.containingItems.length, par2ItemStack);
            this.updateInput();
        } else {
            this.containingItems[par1] = par2ItemStack;

            if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
                par2ItemStack.stackSize = this.getInventoryStackLimit();
            }
        }
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.machine2.4.name");
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && entityplayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
        if (slotID == 0) {
            return itemStack != null && ItemElectricBase.isElectricItem(itemStack.getItem());
        }
        if (slotID >= 3) {
            if (this.producingStack != null) {
                final ItemStack stackInSlot = this.getStackInSlot(slotID);
                return stackInSlot != null && stackInSlot.isItemEqual(itemStack);
            }
            return this.isItemCompressorInput(itemStack, slotID - 3);
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isItemCompressorInput(ItemStack stack, int id) {
        for (final IRecipe recipe : CompressorRecipes.getRecipeList()) {
            if (recipe instanceof ShapedRecipes) {
                if (id >= ((ShapedRecipes) recipe).recipeItems.length) {
                    continue;
                }
                final ItemStack itemstack1 = ((ShapedRecipes) recipe).recipeItems[id];
                if (stack.getItem() == itemstack1.getItem()
                    && (itemstack1.getItemDamage() == 32767 || stack.getItemDamage() == itemstack1.getItemDamage())) {
                    for (int i = 0; i < ((ShapedRecipes) recipe).recipeItems.length; i++) {
                        if (i == id) {
                            continue;
                        }
                        final ItemStack itemstack2 = ((ShapedRecipes) recipe).recipeItems[i];
                        if (stack.getItem() == itemstack2.getItem() && (itemstack2.getItemDamage() == 32767
                            || stack.getItemDamage() == itemstack2.getItemDamage())) {
                            final ItemStack is3 = this.getStackInSlot(id + 3);
                            final ItemStack is4 = this.getStackInSlot(i + 3);
                            return is3 == null || is4 != null && is3.stackSize < is4.stackSize;
                        }
                    }
                    return true;
                }
            } else if (recipe instanceof ShapelessOreRecipe) {
                final ArrayList<Object> required = new ArrayList<>(((ShapelessOreRecipe) recipe).getInput());

                int match = 0;

                for (Object next : required) {
                    if (next instanceof ItemStack) {
                        if (OreDictionary.itemMatches((ItemStack) next, stack, false)) {
                            match++;
                        }
                    } else if (next instanceof ArrayList) {
                        for (ItemStack element : (ArrayList<ItemStack>) next) {
                            if (OreDictionary.itemMatches(element, stack, false)) {
                                match++;
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

                // Shapeless recipe can go into (match) number of slots
                int slotsFilled = 0;
                for (int i = 3; i < 12; i++) {
                    final ItemStack inMatrix = this.getStackInSlot(i);
                    if (inMatrix != null && inMatrix.isItemEqual(stack)) {
                        slotsFilled++;
                    }
                }
                if (slotsFilled < match) {
                    return this.getStackInSlot(id + 3) == null;
                }

                return randnum.nextInt(match) == 0;
            }
        }

        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == 0) {
            return new int[] { 1, 2 };
        }
        final int[] slots = { 0, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        final ArrayList<Integer> removeSlots = new ArrayList<>();

        for (int i = 3; i < 12; i++) {
            if (removeSlots.contains(i)) {
                continue;
            }
            final ItemStack stack1 = this.getStackInSlot(i);
            if (stack1 == null || stack1.stackSize <= 0) {
                continue;
            }

            for (int j = i + 1; j < 12; j++) {
                if (removeSlots.contains(j)) {
                    continue;
                }
                final ItemStack stack2 = this.getStackInSlot(j);
                if (stack2 == null) {
                    continue;
                }

                if (stack1.isItemEqual(stack2)) {
                    if (stack2.stackSize >= stack1.stackSize) {
                        removeSlots.add(j);
                    } else {
                        removeSlots.add(i);
                    }
                    break;
                }
            }
        }

        if (removeSlots.size() > 0) {
            final int[] returnSlots = new int[slots.length - removeSlots.size()];
            int j = 0;
            for (int i = 0; i < slots.length; i++) {
                if (i > 0 && removeSlots.contains(slots[i])) {
                    continue;
                }
                returnSlots[j] = slots[i];
                j++;
            }

            return returnSlots;
        }

        return slots;
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack par2ItemStack, int par3) {
        return this.isItemValidForSlot(slotID, par2ItemStack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack par2ItemStack, int par3) {
        return slotID == 1 || slotID == 2;
    }

    @Override
    public boolean shouldUseEnergy() {
        return this.processTicks > 0;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation((this.getBlockMetadata() & 3) + 2);
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }
}
