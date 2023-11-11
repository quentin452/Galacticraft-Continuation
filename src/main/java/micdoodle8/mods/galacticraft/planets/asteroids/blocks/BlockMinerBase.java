package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockMinerBase extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public BlockMinerBase(final String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0f;
        this.setBlockName(assetName);
        this.setBlockTextureName("galacticraftasteroids:machineframe");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(BlockMinerBase.soundTypeMetal);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("galacticraftasteroids:machineframe");
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        return this.blockIcon;
    }
    
    public Item getItemDropped(final int meta, final Random random, final int par3) {
        return super.getItemDropped(0, random, par3);
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
        return new TileEntityMinerBaseSingle();
    }
    
    public int onBlockPlaced(final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int meta) {
        return 0;
    }
    
    public boolean canPlaceBlockOnSide(final World world, final int x, final int y, final int z, final int side) {
        return true;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
