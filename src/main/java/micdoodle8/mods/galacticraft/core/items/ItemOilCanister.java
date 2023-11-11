package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;

public class ItemOilCanister extends ItemCanisterGeneric
{
    protected IIcon[] icons;
    
    public ItemOilCanister(final String assetName) {
        super(assetName);
        this.icons = new IIcon[7];
        this.setAllowedFluid(ConfigManagerCore.useOldOilFluidID ? "oilgc" : "oil");
        this.setContainerItem((Item)this);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() == 0) {
            return "item.emptyLiquidCanister";
        }
        if (itemStack.getItemDamage() == 1) {
            return "item.oilCanister";
        }
        return "item.oilCanisterPartial";
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
            par3List.add(GCCoreUtil.translate("gui.message.oil.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, this.getMaxDamage()));
    }
    
    public void onUpdate(final ItemStack par1ItemStack, final World par2World, final Entity par3Entity, final int par4, final boolean par5) {
        if (1001 == par1ItemStack.getItemDamage()) {
            par1ItemStack.stackTagCompound = null;
        }
        else if (par1ItemStack.getItemDamage() <= 0) {
            par1ItemStack.setItemDamage(1);
        }
    }
}
