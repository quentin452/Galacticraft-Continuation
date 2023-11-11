package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockSlabGC extends ItemSlab
{
    public ItemBlockSlabGC(final Block block, final BlockSlabGC singleSlab, final BlockSlabGC doubleSlab) {
        super(block, (BlockSlab)singleSlab, (BlockSlab)doubleSlab, block == doubleSlab);
    }
    
    public int getMetadata(final int meta) {
        return meta & 0x7;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        final BlockSlabGC slab = (BlockSlabGC)Block.getBlockFromItem(itemStack.getItem());
        return super.getUnlocalizedName() + "." + new StringBuilder().append(slab.func_150002_b(itemStack.getItemDamage())).toString();
    }
}
