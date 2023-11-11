package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.client.particle.*;

public class BlockMulti extends BlockContainer implements IPartialSealableBlock, ITileEntityProvider
{
    private IIcon[] fakeIcons;
    
    public BlockMulti(final String assetName) {
        super(GCBlocks.machine);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
        this.setResistance(9.9999999E14f);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.fakeIcons = new IIcon[5])[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "launch_pad");
        this.fakeIcons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "workbench_nasa_top");
        this.fakeIcons[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "solar_basic_0");
        this.fakeIcons[4] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "buggy_fueler_blank");
        if (GalacticraftCore.isPlanetsLoaded) {
            try {
                final Class<?> c = Class.forName("micdoodle8.mods.galacticraft.planets.mars.MarsModule");
                final String texturePrefix = (String)c.getField("TEXTURE_PREFIX").get(null);
                this.fakeIcons[3] = par1IconRegister.registerIcon(texturePrefix + "cryoDummy");
            }
            catch (Exception e) {
                this.fakeIcons[3] = this.fakeIcons[2];
                e.printStackTrace();
            }
        }
        else {
            this.fakeIcons[3] = this.fakeIcons[2];
        }
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, final int par2) {
        switch (par2) {
            case 0: {
                return this.fakeIcons[2];
            }
            case 2: {
                return this.fakeIcons[0];
            }
            case 3: {
                return this.fakeIcons[1];
            }
            case 4: {
                return this.fakeIcons[2];
            }
            case 5: {
                return this.fakeIcons[3];
            }
            case 6: {
                return this.fakeIcons[4];
            }
            default: {
                return this.fakeIcons[0];
            }
        }
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 2 || meta == 6) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.2f, 1.0f);
        }
        else {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 2 || meta == 6) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.2f, 1.0f);
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
        ((TileEntityMulti)worldObj.getTileEntity(position.x, position.y, position.z)).setMainBlock(mainBlock);
    }
    
    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        final TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);
        if (tileEntity instanceof TileEntityMulti) {
            final BlockVec3 mainBlockPosition = ((TileEntityMulti)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null && !mainBlockPosition.equals((Object)new BlockVec3(par2, par3, par4))) {
                return mainBlockPosition.getBlock((IBlockAccess)par1World).getBlockHardness(par1World, par2, par3, par4);
            }
        }
        return this.blockHardness;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 2 || metadata == 6) {
            return direction == ForgeDirection.DOWN;
        }
        return metadata == 4 && direction == ForgeDirection.UP;
    }
    
    public Block setBlockTextureName(final String name) {
        this.textureName = name;
        return (Block)this;
    }
    
    public void breakBlock(final World world, final int x, final int y, final int z, final Block par5, final int par6) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMulti) {
            ((TileEntityMulti)tileEntity).onBlockRemoval();
        }
        super.breakBlock(world, x, y, z, par5, par6);
    }
    
    public boolean onBlockActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        final TileEntityMulti tileEntity = (TileEntityMulti)par1World.getTileEntity(x, y, z);
        return tileEntity.onBlockActivated(par1World, x, y, z, par5EntityPlayer);
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
        return new TileEntityMulti();
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMulti) {
            final BlockVec3 mainBlockPosition = ((TileEntityMulti)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null && !mainBlockPosition.equals((Object)new BlockVec3(x, y, z))) {
                final Block mainBlockID = world.getBlock(mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
                if (Blocks.air != mainBlockID) {
                    return mainBlockID.getPickBlock(target, world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
                }
            }
        }
        return null;
    }
    
    public int getBedDirection(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMulti) {
            final BlockVec3 mainBlockPosition = ((TileEntityMulti)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null && !mainBlockPosition.equals((Object)new BlockVec3(x, y, z))) {
                return mainBlockPosition.getBlock(world).getBedDirection(world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
            }
        }
        return BlockDirectional.getDirection(world.getBlockMetadata(x, y, z));
    }
    
    public boolean isBed(final IBlockAccess world, final int x, final int y, final int z, final EntityLivingBase player) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMulti) {
            final BlockVec3 mainBlockPosition = ((TileEntityMulti)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null && !mainBlockPosition.equals((Object)new BlockVec3(x, y, z))) {
                return mainBlockPosition.getBlock(world).isBed(world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, player);
            }
        }
        return super.isBed(world, x, y, z, player);
    }
    
    public void setBedOccupied(final IBlockAccess world, final int x, final int y, final int z, final EntityPlayer player, final boolean occupied) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        final BlockVec3 mainBlockPosition = ((TileEntityMulti)tileEntity).mainBlockPosition;
        if (mainBlockPosition != null && !mainBlockPosition.equals((Object)new BlockVec3(x, y, z))) {
            mainBlockPosition.getBlock(world).setBedOccupied(world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, player, occupied);
        }
        else {
            super.setBedOccupied(world, x, y, z, player, occupied);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(final World worldObj, final MovingObjectPosition target, final EffectRenderer effectRenderer) {
        final TileEntity tileEntity = worldObj.getTileEntity(target.blockX, target.blockY, target.blockZ);
        if (tileEntity instanceof TileEntityMulti) {
            final BlockVec3 mainBlockPosition = ((TileEntityMulti)tileEntity).mainBlockPosition;
            if (mainBlockPosition != null && !mainBlockPosition.equals((Object)new BlockVec3(target.blockX, target.blockY, target.blockZ))) {
                effectRenderer.addBlockHitEffects(mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, target);
            }
        }
        return super.addHitEffects(worldObj, target, effectRenderer);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(final World world, final int x, final int y, final int z, final int meta, final EffectRenderer effectRenderer) {
        return super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
    }
}
