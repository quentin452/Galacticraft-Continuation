package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class SlotRocketBenchResult extends Slot
{
    private final IInventory craftMatrix;
    private final EntityPlayer thePlayer;
    
    public SlotRocketBenchResult(final EntityPlayer par1EntityPlayer, final IInventory par2IInventory, final IInventory par3IInventory, final int par4, final int par5, final int par6) {
        super(par3IInventory, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.craftMatrix = par2IInventory;
    }
    
    public boolean isItemValid(final ItemStack par1ItemStack) {
        return false;
    }
    
    public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par1ItemStack) {
        for (int var2 = 0; var2 < this.craftMatrix.getSizeInventory(); ++var2) {
            final ItemStack var3 = this.craftMatrix.getStackInSlot(var2);
            if (var3 != null) {
                this.craftMatrix.decrStackSize(var2, 1);
                if (var3.getItem().hasContainerItem(var3)) {
                    final ItemStack var4 = new ItemStack(var3.getItem().getContainerItem());
                    if (!var3.getItem().doesContainerItemLeaveCraftingGrid(var3) || !this.thePlayer.inventory.addItemStackToInventory(var4)) {
                        if (this.craftMatrix.getStackInSlot(var2) == null) {
                            this.craftMatrix.setInventorySlotContents(var2, var4);
                        }
                        else {
                            this.thePlayer.entityDropItem(var4, 0.0f);
                        }
                    }
                }
            }
        }
    }
}
