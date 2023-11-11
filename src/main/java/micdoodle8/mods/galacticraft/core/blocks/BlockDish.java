package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.util.*;

public class BlockDish extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc, IPartialSealableBlock
{
    private IIcon[] icons;
    
    public BlockDish(final String assetName) {
        super(Material.iron);
        this.icons = new IIcon[6];
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.icons[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.icons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.icons[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.icons[3] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.icons[4] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.icons[5] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_output");
        this.blockIcon = this.icons[0];
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        return this.blockIcon;
    }
    
    public boolean canPlaceBlockOnSide(final World world, final int x1, final int y1, final int z1, final int side) {
        for (int y2 = 1; y2 <= 2; ++y2) {
            for (int x2 = -1; x2 <= 1; ++x2) {
                for (int z2 = -1; z2 <= 1; ++z2) {
                    final Block block = world.getBlock(x1 + ((y2 == 2) ? x2 : 0), y1 + y2, z1 + ((y2 == 2) ? z2 : 0));
                    if (block.getMaterial() != Material.air && !block.isReplaceable((IBlockAccess)world, x1 + x2, y1 + y2, z1 + z2)) {
                        return false;
                    }
                }
            }
        }
        return new BlockVec3(x1, y1, z1).newVecSide(side ^ 0x1).getBlock((IBlockAccess)world) != GCBlocks.fakeBlock;
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        int change = 0;
        switch (angle) {
            case 0: {
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
            case 3: {
                change = 3;
                break;
            }
        }
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDish) {
            ((TileEntityDish)tile).onCreate(new BlockVec3(x, y, z));
        }
    }
    
    @Override
    public void breakBlock(final World var1, final int var2, final int var3, final int var4, final Block var5, final int var6) {
        final TileEntity var7 = var1.getTileEntity(var2, var3, var4);
        if (var7 instanceof TileEntityDish) {
            ((TileEntityDish)var7).onDestroy(var7);
        }
        super.breakBlock(var1, var2, var3, var4, var5, var6);
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int original;
        final int metadata = original = par1World.getBlockMetadata(x, y, z);
        int change = 0;
        switch (original) {
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
        final TileEntity te = par1World.getTileEntity(x, y, z);
        if (te instanceof TileBaseUniversalElectrical) {
            ((TileBaseUniversalElectrical)te).updateFacing();
        }
        par1World.setBlockMetadataWithNotify(x, y, z, change, 3);
        return true;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        return true;
    }
    
    public int damageDropped(final int metadata) {
        return 0;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = this.getDamageValue(world, x, y, z);
        return new ItemStack((Block)this, 1, metadata);
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return new TileEntityDish();
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate("tile.radioTelescope.description");
    }
    
    @Override
    public boolean showDescription(final int meta) {
        return true;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return true;
    }
}
