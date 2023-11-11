package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;

public class BlockScreen extends BlockAdvanced implements ItemBlockDesc.IBlockShiftDesc, IPartialSealableBlock
{
    private IIcon iconFront;
    private IIcon iconSide;
    
    protected BlockScreen(final String assetName) {
        super(Material.circuits);
        this.setHardness(0.1f);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockTextureName("glass");
        this.setBlockName(assetName);
    }
    
    public boolean isSideSolid(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection direction) {
        return direction.ordinal() != world.getBlockMetadata(x, y, z);
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
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconFront = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "screenFront");
        this.iconSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "screenSide");
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        if (side == (metadata & 0x7)) {
            return this.iconSide;
        }
        return this.iconFront;
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int metadata = 0;
        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        int change = 0;
        switch (angle) {
            case 0: {
                change = 3;
                break;
            }
            case 1: {
                change = 4;
                break;
            }
            case 2: {
                change = 2;
                break;
            }
            case 3: {
                change = 5;
                break;
            }
        }
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
    }
    
    public boolean onUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final int facing = metadata & 0x7;
        int change = 0;
        switch (facing) {
            case 0: {
                change = 1;
                break;
            }
            case 1: {
                change = 3;
                break;
            }
            case 2: {
                change = 5;
                break;
            }
            case 3: {
                change = 4;
                break;
            }
            case 4: {
                change = 2;
                break;
            }
            case 5: {
                change = 0;
                break;
            }
        }
        change += (0x8 & metadata);
        world.setBlockMetadataWithNotify(x, y, z, change, 2);
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityScreen) {
            ((TileEntityScreen)tile).breakScreen(facing);
        }
        return true;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityScreen();
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityScreen) {
            ((TileEntityScreen)tile).changeChannel();
            return true;
        }
        return false;
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block neighbour) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityScreen) {
            ((TileEntityScreen)tile).refreshConnections(true);
        }
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return true;
    }
    
    public MovingObjectPosition collisionRayTrace(final World par1World, final int x, final int y, final int z, final Vec3 par5Vec3, final Vec3 par6Vec3) {
        final int metadata = par1World.getBlockMetadata(x, y, z) & 0x7;
        final float boundsFront = 0.094f;
        final float boundsBack = 1.0f - boundsFront;
        switch (metadata) {
            case 0: {
                this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, boundsBack, 1.0f);
                break;
            }
            case 1: {
                this.setBlockBounds(0.0f, boundsFront, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 2: {
                this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, boundsBack);
                break;
            }
            case 3: {
                this.setBlockBounds(0.0f, 0.0f, boundsFront, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 4: {
                this.setBlockBounds(0.0f, 0.0f, 0.0f, boundsBack, 1.0f, 1.0f);
                break;
            }
            case 5: {
                this.setBlockBounds(boundsFront, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
        }
        return super.collisionRayTrace(par1World, x, y, z, par5Vec3, par6Vec3);
    }
}
