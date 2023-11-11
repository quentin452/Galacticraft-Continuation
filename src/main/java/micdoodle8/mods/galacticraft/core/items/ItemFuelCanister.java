package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemFuelCanister extends ItemCanisterGeneric
{
    protected IIcon[] icons;
    
    public ItemFuelCanister(final String assetName) {
        super(assetName);
        this.icons = new IIcon[7];
        this.setAllowedFluid(ConfigManagerCore.useOldFuelFluidID ? "fuelgc" : "fuel");
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (itemStack.getItemDamage() == 1) {
            return "item.fuelCanister";
        }
        return "item.fuelCanisterPartial";
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
            par3List.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()));
        }
    }
}
