package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.util.*;

public class BlockOxygenDetector extends BlockContainer implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private IIcon iconSide;
    private IIcon iconTop;
    
    protected BlockOxygenDetector(final String assetName) {
        super(Material.iron);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconTop = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "detector_side");
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        if (side == 0 || side == 1) {
            return this.iconTop;
        }
        return this.iconSide;
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityOxygenDetector();
    }
    
    public void updateOxygenState(final World par1World, final int x, final int y, final int z, final boolean valid) {
        if (valid) {
            par1World.setBlockMetadataWithNotify(x, y, z, 1, 3);
        }
        else {
            par1World.setBlockMetadataWithNotify(x, y, z, 0, 3);
        }
    }
    
    public boolean canProvidePower() {
        return true;
    }
    
    public int isProvidingWeakPower(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1) ? 15 : 0;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    public boolean isSideSolid(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection side) {
        return true;
    }
}
