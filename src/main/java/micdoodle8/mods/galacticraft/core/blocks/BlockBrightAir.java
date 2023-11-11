package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.world.*;

public class BlockBrightAir extends BlockAir
{
    public BlockBrightAir(final String assetName) {
        this.setResistance(1000.0f);
        this.setHardness(0.0f);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
        this.setStepSound(new Block.SoundType("sand", 0.0f, 1.0f));
        this.setLightLevel(1.0f);
    }
    
    public boolean canReplace(final World world, final int x, final int y, final int z, final int side, final ItemStack stack) {
        return true;
    }
    
    public boolean canPlaceBlockAt(final World var1, final int var2, final int var3, final int var4) {
        return true;
    }
    
    public int getRenderBlockPass() {
        return 1;
    }
    
    public int getMobilityFlag() {
        return 1;
    }
    
    public Item getItemDropped(final int var1, final Random var2, final int var3) {
        return Item.getItemFromBlock(Blocks.air);
    }
    
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        return false;
    }
    
    public int getLightValue(final IBlockAccess world, final int x, final int y, final int z) {
        return 15 - world.getBlockMetadata(x, y, z);
    }
}
