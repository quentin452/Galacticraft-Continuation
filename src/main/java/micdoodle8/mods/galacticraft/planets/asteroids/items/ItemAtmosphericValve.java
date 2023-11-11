package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.renderer.texture.*;

public class ItemAtmosphericValve extends Item
{
    public ItemAtmosphericValve(final String assetName) {
        this.setMaxDamage(0);
        this.setUnlocalizedName(assetName);
        this.setMaxStackSize(64);
        this.setTextureName("galacticraftasteroids:" + assetName);
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
        this.itemIcon = iconRegister.registerIcon(this.getIconString());
    }
}
