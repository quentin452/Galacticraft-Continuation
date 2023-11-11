package micdoodle8.mods.galacticraft.api.vector;

import net.minecraft.block.*;
import net.minecraft.item.*;

public class BlockTuple
{
    public Block block;
    public int meta;
    
    public BlockTuple(final Block b, final int m) {
        this.block = b;
        this.meta = m;
    }
    
    @Override
    public String toString() {
        final Item item = Item.getItemFromBlock(this.block);
        if (item == null) {
            return "unknown";
        }
        return new ItemStack(item, 1, this.meta).getUnlocalizedName() + ".name";
    }
}
