package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.util.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemOrionDrive extends Item
{
    public IIcon[] icons;
    
    public ItemOrionDrive(final String assetName) {
        this.setMaxDamage(0);
        this.setUnlocalizedName(assetName);
        this.setTextureName("galacticraftasteroids:" + assetName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
}
