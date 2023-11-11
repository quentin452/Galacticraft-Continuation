package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemSpadeAsteroids extends ItemSpade
{
    public ItemSpadeAsteroids(final String assetName) {
        super(AsteroidsItems.TOOL_TITANIUM);
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
