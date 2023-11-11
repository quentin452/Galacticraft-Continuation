package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.init.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockOxygenPipe extends BlockTransmitter implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private IIcon[] pipeIcons;
    
    public BlockOxygenPipe(final String assetName) {
        super(Material.glass);
        this.pipeIcons = new IIcon[16];
        this.setHardness(0.3f);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final Block par5, final int par6) {
        final TileEntityOxygenPipe tile = (TileEntityOxygenPipe)par1World.getTileEntity(par2, par3, par4);
        if (tile != null && tile.getColor() != 15) {
            final float f = 0.7f;
            final double d0 = par1World.rand.nextFloat() * 0.7f + 0.15000000596046448;
            final double d2 = par1World.rand.nextFloat() * 0.7f + 0.06000000238418579 + 0.6;
            final double d3 = par1World.rand.nextFloat() * 0.7f + 0.15000000596046448;
            final EntityItem entityitem = new EntityItem(par1World, par2 + d0, par3 + d2, par4 + d3, new ItemStack(Items.dye, 1, (int)tile.getColor()));
            entityitem.delayBeforeCanPickup = 10;
            par1World.spawnEntityInWorld((Entity)entityitem);
        }
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }
    
    @Override
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
        final TileEntityOxygenPipe tileEntity = (TileEntityOxygenPipe)par1IBlockAccess.getTileEntity(x, y, z);
        if (blockAt == GCBlocks.oxygenPipe && ((TileEntityOxygenPipe)thisVec.getTileEntity(par1IBlockAccess)).getColor() == tileEntity.getColor()) {
            return this.pipeIcons[15];
        }
        return this.pipeIcons[tileEntity.getColor()];
    }
    
    public boolean onBlockActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        final TileEntityOxygenPipe tileEntity = (TileEntityOxygenPipe)par1World.getTileEntity(x, y, z);
        if (!par1World.isRemote) {
            final ItemStack stack = par5EntityPlayer.inventory.getCurrentItem();
            if (stack != null && stack.getItem() instanceof ItemDye) {
                final int dyeColor = par5EntityPlayer.inventory.getCurrentItem().getItemDamageForDisplay();
                final byte colorBefore = tileEntity.getColor();
                tileEntity.setColor((byte)dyeColor);
                if (colorBefore != (byte)dyeColor && !par5EntityPlayer.capabilities.isCreativeMode) {
                    final ItemStack getCurrentItem = par5EntityPlayer.inventory.getCurrentItem();
                    if (--getCurrentItem.stackSize == 0) {
                        par5EntityPlayer.inventory.mainInventory[par5EntityPlayer.inventory.currentItem] = null;
                    }
                }
                if (colorBefore != (byte)dyeColor && colorBefore != 15) {
                    final float f = 0.7f;
                    final double d0 = par1World.rand.nextFloat() * 0.7f + 0.15000000596046448;
                    final double d2 = par1World.rand.nextFloat() * 0.7f + 0.06000000238418579 + 0.6;
                    final double d3 = par1World.rand.nextFloat() * 0.7f + 0.15000000596046448;
                    final EntityItem entityitem = new EntityItem(par1World, x + d0, y + d2, z + d3, new ItemStack(Items.dye, 1, (int)colorBefore));
                    entityitem.delayBeforeCanPickup = 10;
                    par1World.spawnEntityInWorld((Entity)entityitem);
                }
                final BlockVec3 tileVec = new BlockVec3((TileEntity)tileEntity);
                for (final ForgeDirection dir : ForgeDirection.values()) {
                    final TileEntity tileAt = tileVec.getTileEntityOnSide(tileEntity.getWorldObj(), dir);
                    if (tileAt != null && tileAt instanceof IColorable) {
                        ((IColorable)tileAt).onAdjacentColorChanged(dir);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.pipeIcons = new IIcon[16];
        for (int count = 0; count < ItemDye.field_150923_a.length; ++count) {
            this.pipeIcons[count] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "pipe_oxygen_" + ItemDye.field_150923_a[count]);
        }
        this.blockIcon = this.pipeIcons[15];
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
        return new TileEntityOxygenPipe();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int i, final int j, final int k) {
        return this.getCollisionBoundingBoxFromPool(world, i, j, k);
    }
    
    @Override
    public NetworkType getNetworkType() {
        return NetworkType.OXYGEN;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
