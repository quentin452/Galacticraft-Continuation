package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemCanisterLiquidArgon extends ItemCanisterGeneric
{
    protected IIcon[] icons;
    
    public ItemCanisterLiquidArgon(final String assetName) {
        super(assetName);
        this.icons = new IIcon[7];
        this.setAllowedFluid("liquidargon");
        this.setTextureName("galacticraftasteroids:" + assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() == 0) {
            return "item.emptyGasCanister";
        }
        if (itemStack.getItemDamage() == 1) {
            return "item.canister.liquidArgon.full";
        }
        return "item.canister.liquidArgon.partial";
    }
    
    public IIcon getIconFromDamage(final int par1) {
        final int damage = 6 * par1 / this.getMaxDamage();
        if (this.icons.length > damage) {
            return this.icons[this.icons.length - damage - 1];
        }
        return super.getIconFromDamage(damage);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() > 0) {
            par3List.add(GCCoreUtil.translate("item.canister.liquidArgon.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()));
        }
    }
}
