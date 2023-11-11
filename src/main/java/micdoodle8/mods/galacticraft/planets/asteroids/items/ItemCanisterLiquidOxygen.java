package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemCanisterLiquidOxygen extends ItemCanisterGeneric implements IItemOxygenSupply
{
    protected IIcon[] icons;
    private static HashMap<ItemStack, Integer> craftingvalues;
    
    public ItemCanisterLiquidOxygen(final String assetName) {
        super(assetName);
        this.icons = new IIcon[7];
        this.setAllowedFluid("liquidoxygen");
        this.setTextureName("galacticraftasteroids:" + assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (1001 - itemStack.getItemDamage() == 0) {
            return "item.emptyGasCanister";
        }
        if (itemStack.getItemDamage() == 1) {
            return "item.canister.LOX.full";
        }
        return "item.canister.LOX.partial";
    }
    
    public IIcon getIconFromDamage(final int par1) {
        final int damage = 6 * par1 / 1001;
        if (this.icons.length > damage) {
            return this.icons[this.icons.length - damage - 1];
        }
        return super.getIconFromDamage(damage);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (1001 - par1ItemStack.getItemDamage() > 0) {
            par3List.add(GCCoreUtil.translate("item.canister.LOX.name") + ": " + (1001 - par1ItemStack.getItemDamage()));
        }
    }
    
    public static void saveDamage(final ItemStack itemstack, final int damage) {
        ItemCanisterLiquidOxygen.craftingvalues.put(itemstack, damage);
    }
    
    public ItemStack getContainerItem(final ItemStack itemstack) {
        final Integer saved = ItemCanisterLiquidOxygen.craftingvalues.get(itemstack);
        if (saved == null) {
            return super.getContainerItem(itemstack);
        }
        if (saved < 1001) {
            ItemCanisterLiquidOxygen.craftingvalues.remove(itemstack);
            itemstack.setItemDamage((int)saved);
            return itemstack;
        }
        return new ItemStack(this.getContainerItem(), 1, 1001);
    }
    
    public float discharge(final ItemStack itemStack, final float amount) {
        final int damage = itemStack.getItemDamage();
        final int used = Math.min((int)(amount * 0.09259259f), 1001 - damage);
        this.setNewDamage(itemStack, damage + used);
        return used / 0.09259259f;
    }
    
    public int getOxygenStored(final ItemStack par1ItemStack) {
        return 1001 - par1ItemStack.getItemDamage();
    }
    
    static {
        ItemCanisterLiquidOxygen.craftingvalues = new HashMap<ItemStack, Integer>();
    }
}
