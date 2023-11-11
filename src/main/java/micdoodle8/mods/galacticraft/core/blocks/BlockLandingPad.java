package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockLandingPad extends BlockAdvancedTile implements IPartialSealableBlock, ItemBlockDesc.IBlockShiftDesc
{
    private IIcon[] icons;
    
    public BlockLandingPad(final String assetName) {
        super(Material.iron);
        this.icons = new IIcon[3];
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.2f, 1.0f);
        this.setHardness(1.0f);
        this.setResistance(10.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < 2; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.icons[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "launch_pad");
        this.icons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "buggy_fueler_blank");
        this.icons[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "cargo_pad");
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "launch_pad");
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, final int par2) {
        if (par2 < 0 || par2 > this.icons.length) {
            return this.blockIcon;
        }
        return this.icons[par2];
    }
    
    public boolean canPlaceBlockOnSide(final World par1World, final int par2, final int par3, final int par4, final int par5) {
        final Block id = GCBlocks.landingPad;
        return (par1World.getBlock(par2 + 1, par3, par4) != id || par1World.getBlock(par2 + 2, par3, par4) != id || par1World.getBlock(par2 + 3, par3, par4) != id) && (par1World.getBlock(par2 - 1, par3, par4) != id || par1World.getBlock(par2 - 2, par3, par4) != id || par1World.getBlock(par2 - 3, par3, par4) != id) && (par1World.getBlock(par2, par3, par4 + 1) != id || par1World.getBlock(par2, par3, par4 + 2) != id || par1World.getBlock(par2, par3, par4 + 3) != id) && (par1World.getBlock(par2, par3, par4 - 1) != id || par1World.getBlock(par2, par3, par4 - 2) != id || par1World.getBlock(par2, par3, par4 - 3) != id) && (par1World.getBlock(par2, par3 - 1, par4) != GCBlocks.landingPad || par5 != 1) && this.canPlaceBlockAt(par1World, par2, par3, par4);
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (world.isRemote) {
            return null;
        }
        switch (metadata) {
            case 0: {
                return new TileEntityLandingPadSingle();
            }
            case 1: {
                return new TileEntityBuggyFuelerSingle();
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return null;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return direction == ForgeDirection.UP;
    }
    
    public int damageDropped(final int meta) {
        return meta;
    }
    
    public String getShiftDescription(final int meta) {
        if (meta == 0) {
            return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
        }
        return GCCoreUtil.translate("tile.buggyPad.description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
