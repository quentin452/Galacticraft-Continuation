package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;

public class ItemRocketEngineGC extends Item
{
    public static final String[] names;
    protected IIcon[] icons;
    
    public ItemRocketEngineGC(final String assetName) {
        this.icons = new IIcon[ItemRocketEngineGC.names.length];
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
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
        for (final String name : ItemRocketEngineGC.names) {
            this.icons[i++] = iconRegister.registerIcon(this.getIconString() + "." + name);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        return this.getUnlocalizedName() + "." + ItemRocketEngineGC.names[itemStack.getItemDamage()];
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemRocketEngineGC.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    static {
        names = new String[] { "tier1engine", "tier1booster" };
    }
}
