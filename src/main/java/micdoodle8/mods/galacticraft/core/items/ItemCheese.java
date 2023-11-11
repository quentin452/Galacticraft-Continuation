package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemCheese extends ItemFood
{
    public ItemCheese(final int par1, final float par2, final boolean par3) {
        super(par1, par2, par3);
        this.setUnlocalizedName("cheeseCurd");
    }
    
    public ItemCheese(final int par1, final boolean par2) {
        this(par1, 0.6f, par2);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("galacticraftmoon:cheese_curd");
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
}
