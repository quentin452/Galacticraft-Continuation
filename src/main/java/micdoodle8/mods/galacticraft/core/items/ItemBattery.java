package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBattery extends ItemElectricBase
{
    public ItemBattery(final String assetName) {
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
    
    public float getMaxElectricityStored(final ItemStack itemStack) {
        return 15000.0f;
    }
}
