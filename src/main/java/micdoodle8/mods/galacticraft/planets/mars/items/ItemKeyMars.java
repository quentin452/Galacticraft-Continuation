package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.util.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;

public class ItemKeyMars extends Item implements IKeyItem
{
    public static String[] keyTypes;
    public IIcon[] keyIcons;
    
    public ItemKeyMars() {
        this.keyIcons = new IIcon[1];
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        return "item.key." + ItemKeyMars.keyTypes[itemStack.getItemDamage()];
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        int i = 0;
        for (final String name : ItemKeyMars.keyTypes) {
            this.keyIcons[i++] = iconRegister.registerIcon("galacticraftmars:key_" + name);
        }
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.keyIcons.length > damage) {
            return this.keyIcons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemKeyMars.keyTypes.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    public int getTier(final ItemStack keyStack) {
        return 2;
    }
    
    static {
        ItemKeyMars.keyTypes = new String[] { "T2" };
    }
}
