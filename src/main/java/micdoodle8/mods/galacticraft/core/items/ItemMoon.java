package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemMoon extends Item
{
    public static String[] names;
    protected IIcon[] icons;
    
    public ItemMoon(final String str) {
        this.icons = new IIcon[ItemMoon.names.length];
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(str);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        int i = 0;
        for (final String name : ItemMoon.names) {
            this.icons[i++] = iconRegister.registerIcon("galacticraftmoon:" + name);
        }
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemMoon.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        if (this.icons.length > par1ItemStack.getItemDamage()) {
            return "item." + ItemMoon.names[par1ItemStack.getItemDamage()];
        }
        return "unnamed";
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    static {
        ItemMoon.names = new String[] { "meteoricIronIngot", "compressedMeteoricIron" };
    }
}
