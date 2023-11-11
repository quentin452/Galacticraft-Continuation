package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.tileentity.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockSpinThruster extends BlockAdvanced implements ItemBlockDesc.IBlockShiftDesc
{
    public static IIcon thrusterIcon;
    
    protected BlockSpinThruster(final String assetName) {
        super(Material.circuits);
        this.setHardness(0.1f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName("stone");
        this.setBlockName(assetName);
    }
    
    private static boolean isBlockSolidOnSide(final World world, final int x, final int y, final int z, final ForgeDirection direction, final boolean nope) {
        return world.getBlock(x, y, z).isSideSolid((IBlockAccess)world, x, y, z, direction);
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int x, final int y, final int z) {
        return null;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int x, final int y, final int z) {
        return isBlockSolidOnSide(par1World, x - 1, y, z, ForgeDirection.EAST, true) || isBlockSolidOnSide(par1World, x + 1, y, z, ForgeDirection.WEST, true) || isBlockSolidOnSide(par1World, x, y, z - 1, ForgeDirection.SOUTH, true) || isBlockSolidOnSide(par1World, x, y, z + 1, ForgeDirection.NORTH, true);
    }
    
    public int onBlockPlaced(final World par1World, final int x, final int y, final int z, final int par5, final float par6, final float par7, final float par8, final int par9) {
        int var10 = par9;
        if (par5 == 2 && isBlockSolidOnSide(par1World, x, y, z + 1, ForgeDirection.NORTH, true)) {
            var10 = 4;
        }
        if (par5 == 3 && isBlockSolidOnSide(par1World, x, y, z - 1, ForgeDirection.SOUTH, true)) {
            var10 = 3;
        }
        if (par5 == 4 && isBlockSolidOnSide(par1World, x + 1, y, z, ForgeDirection.WEST, true)) {
            var10 = 2;
        }
        if (par5 == 5 && isBlockSolidOnSide(par1World, x - 1, y, z, ForgeDirection.EAST, true)) {
            var10 = 1;
        }
        return 0;
    }
    
    public void updateTick(final World par1World, final int x, final int y, final int z, final Random par5Random) {
        super.updateTick(par1World, x, y, z, par5Random);
        if (par1World.getBlockMetadata(x, y, z) == 0) {
            this.onBlockAdded(par1World, x, y, z);
        }
    }
    
    public void onBlockAdded(final World par1World, final int x, final int y, final int z) {
        int metadata = par1World.getBlockMetadata(x, y, z);
        final TileEntityThruster tile = (TileEntityThruster)par1World.getTileEntity(x, y, z);
        if (metadata == 0) {
            if (isBlockSolidOnSide(par1World, x - 1, y, z, ForgeDirection.EAST, true)) {
                metadata = 1;
                par1World.setBlockMetadataWithNotify(x, y, z, metadata, 3);
            }
            else if (isBlockSolidOnSide(par1World, x + 1, y, z, ForgeDirection.WEST, true)) {
                metadata = 2;
                par1World.setBlockMetadataWithNotify(x, y, z, metadata, 3);
            }
            else if (isBlockSolidOnSide(par1World, x, y, z - 1, ForgeDirection.SOUTH, true)) {
                metadata = 3;
                par1World.setBlockMetadataWithNotify(x, y, z, metadata, 3);
            }
            else if (isBlockSolidOnSide(par1World, x, y, z + 1, ForgeDirection.NORTH, true)) {
                metadata = 4;
                par1World.setBlockMetadataWithNotify(x, y, z, metadata, 3);
            }
        }
        BlockVec3 baseBlock = null;
        switch (metadata) {
            case 1: {
                baseBlock = new BlockVec3(x - 1, y, z);
                break;
            }
            case 2: {
                baseBlock = new BlockVec3(x + 1, y, z);
                break;
            }
            case 3: {
                baseBlock = new BlockVec3(x, y, z - 1);
                break;
            }
            case 4: {
                baseBlock = new BlockVec3(x, y, z + 1);
                break;
            }
            default: {
                this.dropTorchIfCantStay(par1World, x, y, z);
                return;
            }
        }
        if (!par1World.isRemote && par1World.provider instanceof WorldProviderSpaceStation) {
            ((WorldProviderSpaceStation)par1World.provider).getSpinManager().checkSS(baseBlock, true);
        }
    }
    
    public void onNeighborBlockChange(final World par1World, final int x, final int y, final int z, final Block par5) {
        if (this.dropTorchIfCantStay(par1World, x, y, z)) {
            final int var6 = par1World.getBlockMetadata(x, y, z) & 0x7;
            boolean var7 = false;
            if (!isBlockSolidOnSide(par1World, x - 1, y, z, ForgeDirection.EAST, true) && var6 == 1) {
                var7 = true;
            }
            if (!isBlockSolidOnSide(par1World, x + 1, y, z, ForgeDirection.WEST, true) && var6 == 2) {
                var7 = true;
            }
            if (!isBlockSolidOnSide(par1World, x, y, z - 1, ForgeDirection.SOUTH, true) && var6 == 3) {
                var7 = true;
            }
            if (!isBlockSolidOnSide(par1World, x, y, z + 1, ForgeDirection.NORTH, true) && var6 == 4) {
                var7 = true;
            }
            if (var7) {
                this.dropBlockAsItem(par1World, x, y, z, par1World.getBlockMetadata(x, y, z), 0);
                par1World.setBlock(x, y, z, Blocks.air);
            }
        }
        if (!par1World.isRemote && par1World.provider instanceof WorldProviderSpaceStation) {
            ((WorldProviderSpaceStation)par1World.provider).getSpinManager().checkSS(new BlockVec3(x, y, z), true);
        }
    }
    
    private boolean dropTorchIfCantStay(final World par1World, final int x, final int y, final int z) {
        if (!this.canPlaceBlockAt(par1World, x, y, z)) {
            if (par1World.getBlock(x, y, z) == this) {
                this.dropBlockAsItem(par1World, x, y, z, par1World.getBlockMetadata(x, y, z), 0);
                par1World.setBlock(x, y, z, Blocks.air);
            }
            return false;
        }
        return true;
    }
    
    public MovingObjectPosition collisionRayTrace(final World par1World, final int x, final int y, final int z, final Vec3 par5Vec3, final Vec3 par6Vec3) {
        final int var7 = par1World.getBlockMetadata(x, y, z) & 0x7;
        final float var8 = 0.3f;
        if (var7 == 1) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - var8, var8 * 2.0f, 0.8f, 0.5f + var8);
        }
        else if (var7 == 2) {
            this.setBlockBounds(1.0f - var8 * 2.0f, 0.2f, 0.5f - var8, 1.0f, 0.8f, 0.5f + var8);
        }
        else if (var7 == 3) {
            this.setBlockBounds(0.5f - var8, 0.2f, 0.0f, 0.5f + var8, 0.8f, var8 * 2.0f);
        }
        else if (var7 == 4) {
            this.setBlockBounds(0.5f - var8, 0.2f, 1.0f - var8 * 2.0f, 0.5f + var8, 0.8f, 1.0f);
        }
        return super.collisionRayTrace(par1World, x, y, z, par5Vec3, par6Vec3);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World par1World, final int x, final int y, final int z, final Random par5Random) {
        if (par1World.provider instanceof WorldProviderSpaceStation && (((WorldProviderSpaceStation)par1World.provider).getSpinManager().thrustersFiring || par5Random.nextInt(80) == 0)) {
            final int var6 = par1World.getBlockMetadata(x, y, z) & 0x7;
            final double var7 = x + 0.5f;
            final double var8 = y + 0.7f;
            final double var9 = z + 0.5f;
            final double var10 = 0.2199999988079071;
            final double var11 = 0.27000001072883606;
            if (var6 == 1) {
                par1World.spawnParticle("smoke", var7 - 0.27000001072883606, var8 + 0.2199999988079071, var9, 0.0, 0.0, 0.0);
            }
            else if (var6 == 2) {
                par1World.spawnParticle("smoke", var7 + 0.27000001072883606, var8 + 0.2199999988079071, var9, 0.0, 0.0, 0.0);
            }
            else if (var6 == 3) {
                par1World.spawnParticle("smoke", var7, var8 + 0.2199999988079071, var9 - 0.27000001072883606, 0.0, 0.0, 0.0);
            }
            else if (var6 == 4) {
                par1World.spawnParticle("smoke", var7, var8 + 0.2199999988079071, var9 + 0.27000001072883606, 0.0, 0.0, 0.0);
            }
        }
    }
    
    public boolean onUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final int facing = metadata & 0x8;
        final int change = 8 + metadata & 0xF;
        world.setBlockMetadataWithNotify(x, y, z, change, 2);
        if (world.provider instanceof WorldProviderSpaceStation && !world.isRemote) {
            final SpinManager worldOrbital = ((WorldProviderSpaceStation)world.provider).getSpinManager();
            worldOrbital.checkSS(new BlockVec3(x, y, z), true);
        }
        return true;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityThruster();
    }
    
    public void onBlockPreDestroy(final World world, final int x, final int y, final int z, final int metadata) {
        if (!world.isRemote) {
            final int facing = metadata & 0x8;
            if (world.provider instanceof WorldProviderSpaceStation) {
                final SpinManager worldOrbital = ((WorldProviderSpaceStation)world.provider).getSpinManager();
                final BlockVec3 baseBlock = new BlockVec3(x, y, z);
                worldOrbital.removeThruster(baseBlock, facing == 0);
                worldOrbital.updateSpinSpeed();
            }
        }
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
