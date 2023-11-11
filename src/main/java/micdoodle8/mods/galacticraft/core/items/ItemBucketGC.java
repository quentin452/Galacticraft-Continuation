package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;

public class ItemBucketGC extends ItemBucket
{
    private String texture_prefix;
    
    public ItemBucketGC(final Block block, final String texture_prefix) {
        super(block);
        this.texture_prefix = texture_prefix;
        this.setContainerItem(Items.bucket);
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", this.texture_prefix));
    }
}
