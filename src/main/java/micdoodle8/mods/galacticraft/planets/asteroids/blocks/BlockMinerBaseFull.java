package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.material.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class BlockMinerBaseFull extends BlockTileGC
{
    private IIcon iconInput;
    
    public BlockMinerBaseFull(final String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0f;
        this.setBlockName(assetName);
        this.setBlockTextureName("galacticraftasteroids:machineframe");
        this.setStepSound(BlockMinerBaseFull.soundTypeMetal);
    }
    
    public int getRenderType() {
        return -1;
    }
    
    public int damageDropped(final int meta) {
        return 0;
    }
    
    public int quantityDropped(final int meta, final int fortune, final Random random) {
        return 1;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return (TileEntity)new TileEntityMinerBase();
    }
    
    public int onBlockPlaced(final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int meta) {
        return 0;
    }
    
    public boolean canPlaceBlockOnSide(final World world, final int x, final int y, final int z, final int side) {
        return true;
    }
    
    public void breakBlock(final World world, final int x, final int y, final int z, final Block par5, final int par6) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMinerBase) {
            ((TileEntityMinerBase)tileEntity).onBlockRemoval();
        }
        super.breakBlock(world, x, y, z, par5, par6);
    }
    
    public boolean onMachineActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        final TileEntity tileEntity = par1World.getTileEntity(x, y, z);
        return tileEntity instanceof TileEntityMinerBase && ((TileEntityMinerBase)tileEntity).onActivated(par5EntityPlayer);
    }
    
    public Item getItemDropped(final int par1, final Random par2Random, final int par3) {
        return Item.getItemFromBlock(AsteroidBlocks.blockMinerBase);
    }
    
    public ArrayList<ItemStack> getDrops(final World world, final int x, final int y, final int z, final int metadata, final int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Item.getItemFromBlock(AsteroidBlocks.blockMinerBase), 8, 0));
        return ret;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        return new ItemStack(Item.getItemFromBlock(AsteroidBlocks.blockMinerBase), 1, 0);
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final TileEntity te = par1World.getTileEntity(x, y, z);
        if (te instanceof TileEntityMinerBase) {
            ((TileEntityMinerBase)te).updateFacing();
        }
        return true;
    }
}
