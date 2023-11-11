package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemMeteoricIron extends Item
{
    private final String iconName;
    
    public ItemMeteoricIron(final String assetName) {
        this.setUnlocalizedName(this.iconName = assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("galacticraftmoon:" + this.iconName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
}
