package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockFuelLoader extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconFront;
    private IIcon iconFuelInput;
    
    public BlockFuelLoader(final String assetName) {
        super(Material.rock);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconFront = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_fuelloader");
        this.iconFuelInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_fuel_input");
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return new TileEntityFuelLoader();
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        entityPlayer.openGui((Object)GalacticraftCore.instance, -1, world, x, y, z);
        return true;
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        if (side == 0 || side == 1) {
            return this.iconMachineSide;
        }
        if (side == metadata + 2) {
            return this.iconInput;
        }
        if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
            return this.iconFuelInput;
        }
        return this.iconFront;
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        int change = 0;
        switch (par1World.getBlockMetadata(x, y, z)) {
            case 0: {
                change = 3;
                break;
            }
            case 3: {
                change = 1;
                break;
            }
            case 1: {
                change = 2;
                break;
            }
            case 2: {
                change = 0;
                break;
            }
        }
        par1World.setBlockMetadataWithNotify(x, y, z, change, 3);
        final TileEntity te = par1World.getTileEntity(x, y, z);
        if (te instanceof TileBaseUniversalElectrical) {
            ((TileBaseUniversalElectrical)te).updateFacing();
        }
        return true;
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        int change = 0;
        switch (angle) {
            case 0: {
                change = 3;
                break;
            }
            case 1: {
                change = 1;
                break;
            }
            case 2: {
                change = 2;
                break;
            }
            case 3: {
                change = 0;
                break;
            }
        }
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
        for (int dX = -2; dX < 3; ++dX) {
            for (int dZ = -2; dZ < 3; ++dZ) {
                final Block block = world.getBlock(x + dX, y, z + dZ);
                if (block == GCBlocks.landingPadFull) {
                    world.markBlockForUpdate(x + dX, y, z + dZ);
                }
            }
        }
    }
    
    public void onBlockDestroyedByPlayer(final World world, final int x, final int y, final int z, final int par5) {
        super.onBlockDestroyedByPlayer(world, x, y, z, par5);
        for (int dX = -2; dX < 3; ++dX) {
            for (int dZ = -2; dZ < 3; ++dZ) {
                final Block block = world.getBlock(x + dX, y, z + dZ);
                if (block == GCBlocks.landingPadFull) {
                    world.markBlockForUpdate(x + dX, y, z + dZ);
                }
            }
        }
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
