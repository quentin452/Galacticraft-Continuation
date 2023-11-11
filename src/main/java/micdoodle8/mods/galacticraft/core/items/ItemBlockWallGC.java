package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockWallGC extends ItemBlock
{
    private static final String[] types;
    
    public ItemBlockWallGC(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int meta) {
        return meta;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        if (meta < 0 || meta >= ItemBlockWallGC.types.length) {
            meta = 0;
        }
        return super.getUnlocalizedName() + "." + ItemBlockWallGC.types[meta];
    }
    
    static {
        types = new String[] { "tin", "tin", "moon", "moonBricks", "mars", "marsBricks" };
    }
}
