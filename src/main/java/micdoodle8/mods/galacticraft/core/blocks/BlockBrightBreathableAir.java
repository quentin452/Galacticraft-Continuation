package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;

public class BlockBrightBreathableAir extends BlockAir
{
    public BlockBrightBreathableAir(final String assetName) {
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
        final Block block = par1IBlockAccess.getBlock(par2, par3, par4);
        return block != this && block != GCBlocks.breatheableAir && (block == null || block.isAir(par1IBlockAccess, par2, par3, par4)) && par5 >= 0 && par5 <= 5;
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block idBroken) {
        if (Blocks.air != idBroken && idBroken != GCBlocks.brightAir) {
            OxygenPressureProtocol.onEdgeBlockUpdated(world, new BlockVec3(x, y, z));
        }
    }
}
