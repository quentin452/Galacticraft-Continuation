package micdoodle8.mods.galacticraft.planets.mars.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockHydrogenPipe extends BlockTransmitter implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private IIcon pipeIcon;
    
    public BlockHydrogenPipe(final String assetName) {
        super(Material.glass);
        this.setHardness(0.3f);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockName(assetName);
    }
    
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final Block par5, final int par6) {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        world.func_147479_m(x, y, z);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final IBlockAccess par1IBlockAccess, final int x, final int y, final int z, final int par5) {
        final BlockVec3 thisVec = new BlockVec3(x, y, z).modifyPositionFromSide(ForgeDirection.getOrientation(par5));
        final Block blockAt = thisVec.getBlock(par1IBlockAccess);
        if (blockAt == MarsBlocks.hydrogenPipe) {
            return this.pipeIcon;
        }
        return this.pipeIcon;
    }
    
    public int getRenderType() {
        return GalacticraftPlanets.getBlockRenderID((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.pipeIcon = par1IconRegister.registerIcon("galacticraftmars:pipe_hydrogen");
        this.blockIcon = this.pipeIcon;
    }
    
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        return true;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityHydrogenPipe();
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int i, final int j, final int k) {
        return this.getCollisionBoundingBoxFromPool(world, i, j, k);
    }
    
    public NetworkType getNetworkType() {
        return NetworkType.HYDROGEN;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
