package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.util.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemBasicAsteroids extends Item
{
    public static String[] names;
    protected IIcon[] icons;
    
    public ItemBasicAsteroids() {
        this.icons = new IIcon[ItemBasicAsteroids.names.length];
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("itemBasicAsteroids");
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        int i = 0;
        for (final String name : ItemBasicAsteroids.names) {
            this.icons[i++] = iconRegister.registerIcon("galacticraftasteroids:" + name);
        }
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemBasicAsteroids.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        if (this.icons.length > par1ItemStack.getItemDamage()) {
            return "item." + ItemBasicAsteroids.names[par1ItemStack.getItemDamage()];
        }
        return "unnamed";
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (par1ItemStack != null && par1ItemStack.getItemDamage() == 0) {
            par3List.add(GCCoreUtil.translate("item.tier3.desc"));
        }
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    static {
        ItemBasicAsteroids.names = new String[] { "reinforcedPlateT3", "engineT2", "rocketFinsT2", "shardIron", "shardTitanium", "ingotTitanium", "compressedTitanium", "thermalCloth", "beamCore" };
    }
}
