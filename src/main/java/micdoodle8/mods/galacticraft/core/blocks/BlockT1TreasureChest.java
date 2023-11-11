package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.util.*;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockT1TreasureChest extends BlockContainer implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private final Random random;

    protected BlockT1TreasureChest(final String assetName) {
        super(Material.rock);
        this.random = new Random();
        this.setHardness(2.5f);
        this.setResistance(100.0f);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName(assetName);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "treasureChest");
    }

    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        return -1.0f;
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

    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
    }

    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {
        super.onBlockAdded(par1World, par2, par3, par4);
        this.unifyAdjacentChests(par1World, par2, par3, par4);
        final Block var5 = par1World.getBlock(par2, par3, par4 - 1);
        final Block var6 = par1World.getBlock(par2, par3, par4 + 1);
        final Block var7 = par1World.getBlock(par2 - 1, par3, par4);
        final Block var8 = par1World.getBlock(par2 + 1, par3, par4);
        if (var5 == this) {
            this.unifyAdjacentChests(par1World, par2, par3, par4 - 1);
        }
        if (var6 == this) {
            this.unifyAdjacentChests(par1World, par2, par3, par4 + 1);
        }
        if (var7 == this) {
            this.unifyAdjacentChests(par1World, par2 - 1, par3, par4);
        }
        if (var8 == this) {
            this.unifyAdjacentChests(par1World, par2 + 1, par3, par4);
        }
    }

    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLiving, final ItemStack stack) {
        final Block var6 = par1World.getBlock(par2, par3, par4 - 1);
        final Block var7 = par1World.getBlock(par2, par3, par4 + 1);
        final Block var8 = par1World.getBlock(par2 - 1, par3, par4);
        final Block var9 = par1World.getBlock(par2 + 1, par3, par4);
        byte var10 = 0;
        final int var11 = MathHelper.floor_double(par5EntityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        if (var11 == 0) {
            var10 = 2;
        }
        if (var11 == 1) {
            var10 = 5;
        }
        if (var11 == 2) {
            var10 = 3;
        }
        if (var11 == 3) {
            var10 = 4;
        }
        if (var6 != this && var7 != this && var8 != this && var9 != this) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, (int)var10, 3);
        }
        else {
            if ((var6 == this || var7 == this) && (var10 == 4 || var10 == 5)) {
                if (var6 == this) {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 - 1, (int)var10, 3);
                }
                else {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 + 1, (int)var10, 3);
                }
                par1World.setBlockMetadataWithNotify(par2, par3, par4, (int)var10, 3);
            }
            if ((var8 == this || var9 == this) && (var10 == 2 || var10 == 3)) {
                if (var8 == this) {
                    par1World.setBlockMetadataWithNotify(par2 - 1, par3, par4, (int)var10, 3);
                }
                else {
                    par1World.setBlockMetadataWithNotify(par2 + 1, par3, par4, (int)var10, 3);
                }
                par1World.setBlockMetadataWithNotify(par2, par3, par4, (int)var10, 3);
            }
        }
    }

    public void unifyAdjacentChests(final World par1World, final int par2, final int par3, final int par4) {
        if (!par1World.isRemote) {
            final Block var5 = par1World.getBlock(par2, par3, par4 - 1);
            final Block var6 = par1World.getBlock(par2, par3, par4 + 1);
            final Block var7 = par1World.getBlock(par2 - 1, par3, par4);
            final Block var8 = par1World.getBlock(par2 + 1, par3, par4);
            byte var9;
            if (var5 != this && var6 != this) {
                if (var7 != this && var8 != this) {
                    var9 = 3;
                    if (var5.func_149730_j() && !var6.func_149730_j()) {
                        var9 = 3;
                    }
                    if (var6.func_149730_j() && !var5.func_149730_j()) {
                        var9 = 2;
                    }
                    if (var7.func_149730_j() && !var8.func_149730_j()) {
                        var9 = 5;
                    }
                    if (var8.func_149730_j() && !var7.func_149730_j()) {
                        var9 = 4;
                    }
                }
                else {
                    final Block var10 = par1World.getBlock((var7 == this) ? (par2 - 1) : (par2 + 1), par3, par4 - 1);
                    final Block var11 = par1World.getBlock((var7 == this) ? (par2 - 1) : (par2 + 1), par3, par4 + 1);
                    var9 = 3;
                    int var12;
                    if (var7 == this) {
                        var12 = par1World.getBlockMetadata(par2 - 1, par3, par4);
                    }
                    else {
                        var12 = par1World.getBlockMetadata(par2 + 1, par3, par4);
                    }
                    if (var12 == 2) {
                        var9 = 2;
                    }
                    if ((var5.func_149730_j() || var10.func_149730_j()) && !var6.func_149730_j() && !var11.func_149730_j()) {
                        var9 = 3;
                    }
                    if ((var6.func_149730_j() || var11.func_149730_j()) && !var5.func_149730_j() && !var10.func_149730_j()) {
                        var9 = 2;
                    }
                }
            }
            else {
                final Block var10 = par1World.getBlock(par2 - 1, par3, (var5 == this) ? (par4 - 1) : (par4 + 1));
                final Block var11 = par1World.getBlock(par2 + 1, par3, (var5 == this) ? (par4 - 1) : (par4 + 1));
                var9 = 5;
                int var12;
                if (var5 == this) {
                    var12 = par1World.getBlockMetadata(par2, par3, par4 - 1);
                }
                else {
                    var12 = par1World.getBlockMetadata(par2, par3, par4 + 1);
                }
                if (var12 == 4) {
                    var9 = 4;
                }
                if ((var7.func_149730_j() || var10.func_149730_j()) && !var8.func_149730_j() && !var11.func_149730_j()) {
                    var9 = 5;
                }
                if ((var8.func_149730_j() || var11.func_149730_j()) && !var7.func_149730_j() && !var10.func_149730_j()) {
                    var9 = 4;
                }
            }
            par1World.setBlockMetadataWithNotify(par2, par3, par4, (int)var9, 3);
        }
    }

    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4) {
        int var5 = 0;
        if (par1World.getBlock(par2 - 1, par3, par4) == this) {
            ++var5;
        }
        if (par1World.getBlock(par2 + 1, par3, par4) == this) {
            ++var5;
        }
        if (par1World.getBlock(par2, par3, par4 - 1) == this) {
            ++var5;
        }
        if (par1World.getBlock(par2, par3, par4 + 1) == this) {
            ++var5;
        }
        return var5 <= 1 && !this.isThereANeighborChest(par1World, par2 - 1, par3, par4) && !this.isThereANeighborChest(par1World, par2 + 1, par3, par4) && !this.isThereANeighborChest(par1World, par2, par3, par4 - 1) && !this.isThereANeighborChest(par1World, par2, par3, par4 + 1);
    }

    private boolean isThereANeighborChest(final World par1World, final int par2, final int par3, final int par4) {
        return par1World.getBlock(par2, par3, par4) == this && (par1World.getBlock(par2 - 1, par3, par4) == this || par1World.getBlock(par2 + 1, par3, par4) == this || par1World.getBlock(par2, par3, par4 - 1) == this || par1World.getBlock(par2, par3, par4 + 1) == this);
    }

    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        final TileEntityTreasureChest var6 = (TileEntityTreasureChest)par1World.getTileEntity(par2, par3, par4);
        if (var6 != null) {
            var6.updateContainingBlockInfo();
        }
    }

    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final Block par5, final int par6) {
        final TileEntityTreasureChest var7 = (TileEntityTreasureChest)par1World.getTileEntity(par2, par3, par4);
        if (var7 != null) {
            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
                final ItemStack var9 = var7.getStackInSlot(var8);
                if (var9 != null) {
                    final float var10 = this.random.nextFloat() * 0.8f + 0.1f;
                    final float var11 = this.random.nextFloat() * 0.8f + 0.1f;
                    final float var12 = this.random.nextFloat() * 0.8f + 0.1f;
                    while (var9.stackSize > 0) {
                        int var13 = this.random.nextInt(21) + 10;
                        if (var13 > var9.stackSize) {
                            var13 = var9.stackSize;
                        }
                        final ItemStack itemStack = var9;
                        itemStack.stackSize -= var13;
                        final EntityItem var14 = new EntityItem(par1World, (double)(par2 + var10), (double)(par3 + var11), (double)(par4 + var12), new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
                        final float var15 = 0.05f;
                        var14.motionX = (float)this.random.nextGaussian() * 0.05f;
                        var14.motionY = (float)this.random.nextGaussian() * 0.05f + 0.2f;
                        var14.motionZ = (float)this.random.nextGaussian() * 0.05f;
                        if (var9.hasTagCompound()) {
                            var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                        }
                        par1World.spawnEntityInWorld((Entity)var14);
                    }
                }
            }
        }
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        Object var10 = par1World.getTileEntity(par2, par3, par4);
        if (var10 == null) {
            return true;
        }
        if (par1World.isSideSolid(par2, par3 + 1, par4, ForgeDirection.DOWN)) {
            return true;
        }
        if (isOcelotBlockingChest(par1World, par2, par3, par4)) {
            return true;
        }
        if (par1World.getBlock(par2 - 1, par3, par4) == this && (par1World.isSideSolid(par2 - 1, par3 + 1, par4, ForgeDirection.DOWN) || isOcelotBlockingChest(par1World, par2 - 1, par3, par4))) {
            return true;
        }
        if (par1World.getBlock(par2 + 1, par3, par4) == this && (par1World.isSideSolid(par2 + 1, par3 + 1, par4, ForgeDirection.DOWN) || isOcelotBlockingChest(par1World, par2 + 1, par3, par4))) {
            return true;
        }
        if (par1World.getBlock(par2, par3, par4 - 1) == this && (par1World.isSideSolid(par2, par3 + 1, par4 - 1, ForgeDirection.DOWN) || isOcelotBlockingChest(par1World, par2, par3, par4 - 1))) {
            return true;
        }
        if (par1World.getBlock(par2, par3, par4 + 1) == this && (par1World.isSideSolid(par2, par3 + 1, par4 + 1, ForgeDirection.DOWN) || isOcelotBlockingChest(par1World, par2, par3, par4 + 1))) {
            return true;
        }
        if (par1World.getBlock(par2 - 1, par3, par4) == this) {
            var10 = new InventoryLargeChest("container.chestDouble", (IInventory)par1World.getTileEntity(par2 - 1, par3, par4), (IInventory)var10);
        }
        if (par1World.getBlock(par2 + 1, par3, par4) == this) {
            var10 = new InventoryLargeChest("container.chestDouble", (IInventory)var10, (IInventory)par1World.getTileEntity(par2 + 1, par3, par4));
        }
        if (par1World.getBlock(par2, par3, par4 - 1) == this) {
            var10 = new InventoryLargeChest("container.chestDouble", (IInventory)par1World.getTileEntity(par2, par3, par4 - 1), (IInventory)var10);
        }
        if (par1World.getBlock(par2, par3, par4 + 1) == this) {
            var10 = new InventoryLargeChest("container.chestDouble", (IInventory)var10, (IInventory)par1World.getTileEntity(par2, par3, par4 + 1));
        }
        if (par1World.isRemote) {
            return true;
        }
        par5EntityPlayer.displayGUIChest((IInventory)var10);
        return true;
    }

    public TileEntity createNewTileEntity(final World par1World, final int metadata) {
        return new TileEntityTreasureChest(1);
    }

    public static boolean isOcelotBlockingChest(final World par0World, final int par1, final int par2, final int par3) {
        List<EntityOcelot> ocelots = par0World.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox(par1, par2 + 1, par3, par1 + 1, par2 + 2, par3 + 1));

        for (final EntityOcelot var5 : ocelots) {
            if (var5.isSitting()) {
                return true;
            }
        }
        return false;
    }


    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    public boolean showDescription(final int meta) {
        return true;
    }
}
