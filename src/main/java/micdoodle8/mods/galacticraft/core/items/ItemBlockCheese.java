package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemBlockCheese extends ItemBlockDesc
{
    public ItemBlockCheese(final Block par2Block) {
        super(par2Block);
        this.setMaxStackSize(1);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("galacticraftmoon:cheese_block");
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
}
