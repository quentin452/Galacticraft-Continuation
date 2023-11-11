package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.item.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraft.world.*;

public class TileEntityCargoUnloader extends TileBaseElectricBlockWithInventory implements ISidedInventory, ILandingPadAttachable
{
    private ItemStack[] containingItems;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean targetEmpty;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean targetNoInventory;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean noTarget;
    public ICargoEntity attachedFuelable;
    
    public TileEntityCargoUnloader() {
        this.containingItems = new ItemStack[15];
        this.storage.setMaxExtract(45.0f);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.ticks % 100 == 0) {
                this.checkForCargoEntity();
            }
            if (this.attachedFuelable != null) {
                this.noTarget = false;
                final ICargoEntity.RemovalResult result = this.attachedFuelable.removeCargo(false);
                if (result.resultStack != null) {
                    this.targetEmpty = false;
                    final ICargoEntity.EnumCargoLoadingState state = this.addCargo(result.resultStack, false);
                    this.targetEmpty = (state == ICargoEntity.EnumCargoLoadingState.EMPTY);
                    if (this.ticks % 15 == 0 && state == ICargoEntity.EnumCargoLoadingState.SUCCESS && !this.disabled && this.hasEnoughEnergyToRun) {
                        this.addCargo(this.attachedFuelable.removeCargo(true).resultStack, true);
                    }
                }
                else {
                    this.targetNoInventory = (result.resultState == ICargoEntity.EnumCargoLoadingState.NOINVENTORY);
                    this.noTarget = (result.resultState == ICargoEntity.EnumCargoLoadingState.NOTARGET);
                    this.targetEmpty = true;
                }
            }
            else {
                this.noTarget = true;
            }
        }
    }
    
    public void checkForCargoEntity() {
        boolean foundFuelable = false;
        for (final ForgeDirection dir : ForgeDirection.values()) {
            if (dir != ForgeDirection.UNKNOWN) {
                final TileEntity pad = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, dir);
                if (pad != null && pad instanceof TileEntityMulti) {
                    final TileEntity mainTile = ((TileEntityMulti)pad).getMainBlockTile();
                    if (mainTile instanceof ICargoEntity) {
                        this.attachedFuelable = (ICargoEntity)mainTile;
                        foundFuelable = true;
                        break;
                    }
                }
                else if (pad != null && pad instanceof ICargoEntity) {
                    this.attachedFuelable = (ICargoEntity)pad;
                    foundFuelable = true;
                    break;
                }
            }
        }
        if (!foundFuelable) {
            this.attachedFuelable = null;
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.containingItems = this.readStandardItemsFromNBT(par1NBTTagCompound);
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        this.writeStandardItemsToNBT(par1NBTTagCompound);
    }
    
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.cargounloader.name");
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return (side != this.getBlockMetadata() - 2) ? new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 } : new int[0];
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return false;
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        return side != this.getBlockMetadata() - 2 && (slotID != 0 || ItemElectricBase.isElectricItemEmpty(itemstack));
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return slotID != 0 || ItemElectricBase.isElectricItem(itemstack.getItem());
    }
    
    public boolean shouldUseEnergy() {
        return !this.getDisabled(0);
    }
    
    public ICargoEntity.EnumCargoLoadingState addCargo(final ItemStack stack, final boolean doAdd) {
        int count = 1;
        count = 1;
        while (count < this.containingItems.length) {
            final ItemStack stackAt = this.containingItems[count];
            if (stackAt != null && stackAt.getItem() == stack.getItem() && stackAt.getItemDamage() == stack.getItemDamage() && stackAt.stackSize < stackAt.getMaxStackSize()) {
                if (stackAt.stackSize + stack.stackSize <= stackAt.getMaxStackSize()) {
                    if (doAdd) {
                        final ItemStack itemStack = this.containingItems[count];
                        itemStack.stackSize += stack.stackSize;
                        this.markDirty();
                    }
                    return ICargoEntity.EnumCargoLoadingState.SUCCESS;
                }
                final int origSize = stackAt.stackSize;
                final int surplus = origSize + stack.stackSize - stackAt.getMaxStackSize();
                if (doAdd) {
                    this.containingItems[count].stackSize = stackAt.getMaxStackSize();
                    this.markDirty();
                }
                stack.stackSize = surplus;
                if (this.addCargo(stack, doAdd) == ICargoEntity.EnumCargoLoadingState.SUCCESS) {
                    return ICargoEntity.EnumCargoLoadingState.SUCCESS;
                }
                this.containingItems[count].stackSize = origSize;
                return ICargoEntity.EnumCargoLoadingState.FULL;
            }
            else {
                ++count;
            }
        }
        for (count = 1; count < this.containingItems.length; ++count) {
            final ItemStack stackAt = this.containingItems[count];
            if (stackAt == null) {
                if (doAdd) {
                    this.containingItems[count] = stack;
                    this.markDirty();
                }
                return ICargoEntity.EnumCargoLoadingState.SUCCESS;
            }
        }
        return ICargoEntity.EnumCargoLoadingState.FULL;
    }
    
    public ICargoEntity.RemovalResult removeCargo(final boolean doRemove) {
        for (int i = 1; i < this.containingItems.length; ++i) {
            final ItemStack stackAt = this.containingItems[i];
            if (stackAt != null) {
                final ItemStack resultStack = stackAt.copy();
                resultStack.stackSize = 1;
                if (doRemove) {
                    final ItemStack itemStack = stackAt;
                    if (--itemStack.stackSize <= 0) {
                        this.containingItems[i] = null;
                    }
                }
                if (doRemove) {
                    this.markDirty();
                }
                return new ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.SUCCESS, resultStack);
            }
        }
        return new ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.EMPTY, (ItemStack)null);
    }
    
    public boolean canAttachToLandingPad(final IBlockAccess world, final int x, final int y, final int z) {
        return true;
    }
}
