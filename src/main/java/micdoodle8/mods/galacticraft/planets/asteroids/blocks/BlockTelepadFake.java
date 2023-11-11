package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;

public class BlockTelepadFake extends BlockAdvancedTile implements ITileEntityProvider
{
    public BlockTelepadFake(final String assetName) {
        super(GCBlocks.machine);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + "launch_pad");
        this.setResistance(9.9999999E14f);
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            this.setBlockBounds(0.0f, 0.55f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        else if (meta == 1) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.38f, 1.0f);
        }
        else {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            this.setBlockBounds(0.0f, 0.55f, 0.0f, 1.0f, 1.0f, 1.0f);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
        }
        else if (meta == 1) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.38f, 1.0f);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
        }
        else {
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
        }
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    
    public boolean canDropFromExplosion(final Explosion par1Explosion) {
        return false;
    }
    
    public void makeFakeBlock(final World worldObj, final BlockVec3 position, final BlockVec3 mainBlock, final int meta) {
        worldObj.setBlock(position.x, position.y, position.z, (Block)this, meta, 3);
        ((TileEntityTelepadFake)worldObj.getTileEntity(position.x, position.y, position.z)).setMainBlock(mainBlock);
    }
    
    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        final TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);
        if (tileEntity instanceof TileEntityTelepadFake) {
            final BlockVec3 mainBlockPosition = ((TileEntityTelepadFake)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null) {
                return mainBlockPosition.getBlock((IBlockAccess)par1World).getBlockHardness(par1World, par2, par3, par4);
            }
        }
        return this.blockHardness;
    }
    
    public Block setBlockTextureName(final String name) {
        this.textureName = name;
        return (Block)this;
    }
    
    public void breakBlock(final World world, final int x, final int y, final int z, final Block par5, final int par6) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityTelepadFake) {
            ((TileEntityTelepadFake)tileEntity).onBlockRemoval();
        }
        super.breakBlock(world, x, y, z, par5, par6);
    }
    
    public boolean onBlockActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        final TileEntityTelepadFake tileEntity = (TileEntityTelepadFake)par1World.getTileEntity(x, y, z);
        return tileEntity.onActivated(par5EntityPlayer);
    }
    
    public int quantityDropped(final Random par1Random) {
        return 0;
    }
    
    public int getRenderType() {
        return -1;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public TileEntity createNewTileEntity(final World var1, final int meta) {
        return (TileEntity)new TileEntityTelepadFake();
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        final BlockVec3 mainBlockPosition = ((TileEntityTelepadFake)tileEntity).mainBlockPosition;
        if (mainBlockPosition != null) {
            final Block mainBlockID = world.getBlock(mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
            if (Blocks.air != mainBlockID) {
                return mainBlockID.getPickBlock(target, world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
            }
        }
        return null;
    }
    
    public int getBedDirection(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        final BlockVec3 mainBlockPosition = ((TileEntityTelepadFake)tileEntity).mainBlockPosition;
        if (mainBlockPosition != null) {
            return mainBlockPosition.getBlock(world).getBedDirection(world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
        }
        return BlockDirectional.getDirection(world.getBlockMetadata(x, y, z));
    }
    
    public boolean isBed(final IBlockAccess world, final int x, final int y, final int z, final EntityLivingBase player) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        final BlockVec3 mainBlockPosition = ((TileEntityTelepadFake)tileEntity).mainBlockPosition;
        if (mainBlockPosition != null) {
            return mainBlockPosition.getBlock(world).isBed(world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, player);
        }
        return super.isBed(world, x, y, z, player);
    }
    
    public void setBedOccupied(final IBlockAccess world, final int x, final int y, final int z, final EntityPlayer player, final boolean occupied) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        final BlockVec3 mainBlockPosition = ((TileEntityTelepadFake)tileEntity).mainBlockPosition;
        if (mainBlockPosition != null) {
            mainBlockPosition.getBlock(world).setBedOccupied(world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, player, occupied);
        }
        else {
            super.setBedOccupied(world, x, y, z, player, occupied);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(final World worldObj, final MovingObjectPosition target, final EffectRenderer effectRenderer) {
        if (worldObj.getBlockMetadata(target.blockX, target.blockY, target.blockZ) == 6) {
            return true;
        }
        final TileEntity tileEntity = worldObj.getTileEntity(target.blockX, target.blockY, target.blockZ);
        if (tileEntity instanceof TileEntityTelepadFake) {
            final BlockVec3 mainBlockPosition = ((TileEntityTelepadFake)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null) {
                effectRenderer.addBlockHitEffects(mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, target);
            }
        }
        return super.addHitEffects(worldObj, target, effectRenderer);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(final World world, final int x, final int y, final int z, final int meta, final EffectRenderer effectRenderer) {
        return world.getBlockMetadata(x, y, z) == 6 || super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
    }
}
