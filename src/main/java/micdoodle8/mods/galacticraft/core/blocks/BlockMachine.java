package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockMachine extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public static final int COAL_GENERATOR_METADATA = 0;
    public static final int COMPRESSOR_METADATA = 12;
    private IIcon iconMachineSide;
    private IIcon iconOutput;
    private IIcon iconCoalGenerator;
    private IIcon iconCompressor;
    
    public BlockMachine(final String assetName) {
        super(GCBlocks.machine);
        this.setBlockName("basicMachine");
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
    
    public void registerBlockIcons(final IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine");
        this.iconOutput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_output");
        this.iconMachineSide = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_side");
        this.iconCoalGenerator = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "coalGenerator");
        this.iconCompressor = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "compressor");
    }
    
    public void randomDisplayTick(final World par1World, final int x, final int y, final int z, final Random par5Random) {
        final TileEntity tile = par1World.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCoalGenerator) {
            final TileEntityCoalGenerator tileEntity = (TileEntityCoalGenerator)tile;
            if (tileEntity.heatGJperTick > 0.0f) {
                final int metadata = par1World.getBlockMetadata(x, y, z);
                final float var7 = x + 0.5f;
                final float var8 = y + 0.0f + par5Random.nextFloat() * 6.0f / 16.0f;
                final float var9 = z + 0.5f;
                final float var10 = 0.52f;
                final float var11 = par5Random.nextFloat() * 0.6f - 0.3f;
                if (metadata == 0) {
                    par1World.spawnParticle("smoke", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                }
                else if (metadata == 1) {
                    par1World.spawnParticle("smoke", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                }
                else if (metadata == 2) {
                    par1World.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
                }
                else if (metadata == 3) {
                    par1World.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    public IIcon getIcon(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        final int metadata = world.getBlockMetadata(x, y, z);
        return this.getIcon(side, world.getBlockMetadata(x, y, z));
    }
    
    public IIcon getIcon(final int side, int metadata) {
        if (side == 0 || side == 1) {
            return this.blockIcon;
        }
        if (metadata >= 12) {
            metadata -= 12;
            if ((metadata == 0 && side == 4) || (metadata == 1 && side == 5) || (metadata == 2 && side == 3) || (metadata == 3 && side == 2)) {
                return this.iconCompressor;
            }
        }
        else {
            if (side == metadata + 2) {
                return this.iconOutput;
            }
            if ((metadata == 0 && side == 4) || (metadata == 1 && side == 5) || (metadata == 2 && side == 3) || (metadata == 3 && side == 2)) {
                return this.iconCoalGenerator;
            }
        }
        return this.iconMachineSide;
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int metadata = world.getBlockMetadata(x, y, z);
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
        world.setBlockMetadataWithNotify(x, y, z, (metadata & 0xC) + change, 3);
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = par1World.getBlockMetadata(x, y, z);
        final int original = metadata & 0x3;
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
        if (metadata < 12) {
            final TileEntity te = par1World.getTileEntity(x, y, z);
            if (te instanceof TileBaseUniversalElectrical) {
                ((TileBaseUniversalElectrical)te).updateFacing();
            }
        }
        par1World.setBlockMetadataWithNotify(x, y, z, (metadata & 0xC) + change, 3);
        return true;
    }
    
    public boolean onMachineActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = par1World.getBlockMetadata(x, y, z);
        if (par1World.isRemote) {
            return true;
        }
        if (metadata >= 12) {
            par5EntityPlayer.openGui((Object)GalacticraftCore.instance, -1, par1World, x, y, z);
            return true;
        }
        par5EntityPlayer.openGui((Object)GalacticraftCore.instance, -1, par1World, x, y, z);
        return true;
    }
    
    public TileEntity createTileEntity(final World world, int metadata) {
        metadata &= 0xC;
        if (metadata == 12) {
            return new TileEntityIngotCompressor();
        }
        if (metadata == 4) {
            return new TileEntityEnergyStorageModule();
        }
        if (metadata == 8) {
            return new TileEntityElectricFurnace();
        }
        return new TileEntityCoalGenerator();
    }
    
    public ItemStack getCompressor() {
        return new ItemStack((Block)this, 1, 12);
    }
    
    public ItemStack getCoalGenerator() {
        return new ItemStack((Block)this, 1, 0);
    }
    
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(this.getCoalGenerator());
        par3List.add(this.getCompressor());
    }
    
    public int damageDropped(final int metadata) {
        return metadata & 0xC;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = this.getDamageValue(world, x, y, z);
        return new ItemStack((Block)this, 1, metadata);
    }
    
    @Override
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 0: {
                return GCCoreUtil.translate("tile.coalGenerator.description");
            }
            case 12: {
                return GCCoreUtil.translate("tile.compressor.description");
            }
            default: {
                return "";
            }
        }
    }
    
    @Override
    public boolean showDescription(final int meta) {
        return true;
    }
}
