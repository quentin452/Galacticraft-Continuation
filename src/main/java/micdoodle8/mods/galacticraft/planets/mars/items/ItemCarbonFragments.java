package micdoodle8.mods.galacticraft.planets.mars.items;

import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.renderer.texture.*;

public class ItemCarbonFragments extends Item
{
    public ItemCarbonFragments() {
        this.setMaxStackSize(64);
        this.setMaxDamage(0);
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
        this.itemIcon = iconRegister.registerIcon("galacticraftmars:carbonFragments");
    }
}
