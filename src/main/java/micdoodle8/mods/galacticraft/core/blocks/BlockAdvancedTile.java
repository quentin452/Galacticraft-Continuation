package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.material.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import net.minecraft.inventory.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;

public abstract class BlockAdvancedTile extends BlockAdvanced implements ITileEntityProvider
{
    public BlockAdvancedTile(final Material par3Material) {
        super(par3Material);
        this.isBlockContainer = true;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return null;
    }
    
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {
        super.onBlockAdded(par1World, par2, par3, par4);
    }
    
    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metadata) {
        if (this.hasTileEntity(metadata)) {
            final TileEntity tileNew = world.getTileEntity(x, y, z);
            if (tileNew != null) {
                this.dropEntireInventory(world, x, y, z, block, metadata);
                tileNew.invalidate();
            }
        }
    }
    
    public boolean onBlockEventReceived(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6) {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        final TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null && tileentity.receiveClientEvent(par5, par6);
    }
    
    public void dropEntireInventory(final World world, final int x, final int y, final int z, final Block par5, final int par6) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof IInventory) {
            final IInventory inventory = (IInventory)tileEntity;
            for (int var6 = 0; var6 < inventory.getSizeInventory(); ++var6) {
                final ItemStack var7 = inventory.getStackInSlot(var6);
                if (var7 != null) {
                    final Random random = new Random();
                    final float var8 = random.nextFloat() * 0.8f + 0.1f;
                    final float var9 = random.nextFloat() * 0.8f + 0.1f;
                    final float var10 = random.nextFloat() * 0.8f + 0.1f;
                    while (var7.stackSize > 0) {
                        int var11 = random.nextInt(21) + 10;
                        if (var11 > var7.stackSize) {
                            var11 = var7.stackSize;
                        }
                        final ItemStack itemStack = var7;
                        itemStack.stackSize -= var11;
                        final EntityItem var12 = new EntityItem(world, (double)(x + var8), (double)(y + var9), (double)(z + var10), new ItemStack(var7.getItem(), var11, var7.getItemDamage()));
                        if (var7.hasTagCompound()) {
                            var12.getEntityItem().setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
                        }
                        final float var13 = 0.05f;
                        var12.motionX = (float)random.nextGaussian() * var13;
                        var12.motionY = (float)random.nextGaussian() * var13 + 0.2f;
                        var12.motionZ = (float)random.nextGaussian() * var13;
                        world.spawnEntityInWorld((Entity)var12);
                    }
                }
            }
        }
    }
}
