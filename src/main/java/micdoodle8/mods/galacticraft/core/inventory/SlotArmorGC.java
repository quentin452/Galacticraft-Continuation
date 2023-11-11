package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;

public class SlotArmorGC extends Slot
{
    final int armorType;
    final EntityPlayer thePlayer;
    
    public SlotArmorGC(final EntityPlayer thePlayer, final IInventory par2IInventory, final int par3, final int par4, final int par5, final int par6) {
        super(par2IInventory, par3, par4, par5);
        this.thePlayer = thePlayer;
        this.armorType = par6;
    }
    
    public int getSlotStackLimit() {
        return 1;
    }
    
    public boolean isItemValid(final ItemStack par1ItemStack) {
        final Item item = (par1ItemStack == null) ? null : par1ItemStack.getItem();
        return item != null && item.isValidArmor(par1ItemStack, this.armorType, (Entity)this.thePlayer);
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getBackgroundIconIndex() {
        return ItemArmor.func_94602_b(this.armorType);
    }
}
