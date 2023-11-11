package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.tileentity.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockParaChest extends BlockContainer implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private final Random random;

    protected BlockParaChest(final String assetName) {
        super(Material.wood);
        this.random = new Random();
        this.setHardness(3.0f);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
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

    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {
        super.onBlockAdded(par1World, par2, par3, par4);
    }

    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        if (par1World.isRemote) {
            return true;
        }
        final IInventory iinventory = this.getInventory(par1World, par2, par3, par4);
        if (iinventory != null && par5EntityPlayer instanceof EntityPlayerMP) {
            par5EntityPlayer.openGui((Object)GalacticraftCore.instance, -1, par1World, par2, par3, par4);
        }
        return true;
    }

    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLivingBase, final ItemStack par6ItemStack) {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
    }

    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        final TileEntityParaChest tileentitychest = (TileEntityParaChest)par1World.getTileEntity(par2, par3, par4);
        if (tileentitychest != null) {
            tileentitychest.updateContainingBlockInfo();
        }
    }

    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4) {
        return super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final Block par5, final int par6) {
        final TileEntityParaChest tileentitychest = (TileEntityParaChest)par1World.getTileEntity(par2, par3, par4);
        if (tileentitychest != null) {
            for (int j1 = 0; j1 < tileentitychest.getSizeInventory(); ++j1) {
                final ItemStack itemstack = tileentitychest.getStackInSlot(j1);
                if (itemstack != null) {
                    final float f = this.random.nextFloat() * 0.8f + 0.1f;
                    final float f2 = this.random.nextFloat() * 0.8f + 0.1f;
                    final float f3 = this.random.nextFloat() * 0.8f + 0.1f;
                    while (itemstack.stackSize > 0) {
                        int k1 = this.random.nextInt(21) + 10;
                        if (k1 > itemstack.stackSize) {
                            k1 = itemstack.stackSize;
                        }
                        final ItemStack itemStack = itemstack;
                        itemStack.stackSize -= k1;
                        final EntityItem entityitem = new EntityItem(par1World, (double)(par2 + f), (double)(par3 + f2), (double)(par4 + f3), new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
                        final float f4 = 0.05f;
                        entityitem.motionX = (float)this.random.nextGaussian() * f4;
                        entityitem.motionY = (float)this.random.nextGaussian() * f4 + 0.2f;
                        entityitem.motionZ = (float)this.random.nextGaussian() * f4;
                        if (itemstack.hasTagCompound()) {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }
                        par1World.spawnEntityInWorld((Entity)entityitem);
                    }
                }
            }
            par1World.func_147453_f(par2, par3, par4, par5);
        }
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    public IInventory getInventory(final World par1World, final int par2, final int par3, final int par4) {
        final Object object = par1World.getTileEntity(par2, par3, par4);
        if (object == null) {
            return null;
        }
        if (par1World.isSideSolid(par2, par3 + 1, par4, ForgeDirection.DOWN)) {
            return null;
        }
        if (isOcelotBlockingChest(par1World, par2, par3, par4)) {
            return null;
        }
        return (IInventory)object;
    }

    public static boolean isOcelotBlockingChest(final World par0World, final int par1, final int par2, final int par3) {
        for (final Object entity : par0World.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox(par1, par2 + 1, par3, par1 + 1, par2 + 2, par3 + 1))) {
            if (entity instanceof EntityOcelot && ((EntityOcelot)entity).isSitting()) {
                return true;
            }
        }
        return false;
    }

    public TileEntity createNewTileEntity(final World par1World, final int meta) {
        return new TileEntityParaChest();
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("planks_oak");
    }

    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    public boolean showDescription(final int meta) {
        return true;
    }
}
