package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemFuel extends Item
{
    public ItemFuel(final String assetName) {
        this.setUnlocalizedName(assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "fuel_flow");
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
}
