package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockMachine2 extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public static final int ELECTRIC_COMPRESSOR_METADATA = 0;
    public static final int CIRCUIT_FABRICATOR_METADATA = 4;
    public static final int OXYGEN_STORAGE_MODULE_METADATA = 8;
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconOxygenInput;
    private IIcon iconOxygenOutput;
    private IIcon iconElectricCompressor;
    private IIcon iconCircuitFabricator;
    private IIcon[] iconOxygenStorageModule;
    
    public BlockMachine2(final String assetName) {
        super(GCBlocks.machine);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public void registerBlockIcons(final IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine");
        this.iconInput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
        this.iconOxygenInput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oxygen_input");
        this.iconOxygenOutput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oxygen_output");
        this.iconMachineSide = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_side");
        this.iconElectricCompressor = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "electric_compressor");
        this.iconCircuitFabricator = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "circuit_fabricator");
        this.iconOxygenStorageModule = new IIcon[17];
        for (int i = 0; i < this.iconOxygenStorageModule.length; ++i) {
            this.iconOxygenStorageModule[i] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oxygenStorageModule_" + i);
        }
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
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
                if (metadata == 3) {
                    par1World.spawnParticle("smoke", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                }
                else if (metadata == 2) {
                    par1World.spawnParticle("smoke", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
                }
                else if (metadata == 1) {
                    par1World.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
                }
                else if (metadata == 0) {
                    par1World.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
                    par1World.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    public IIcon getIcon(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata < 8) {
            return super.getIcon(world, x, y, z, side);
        }
        metadata -= 8;
        if (side == 0 || side == 1) {
            return this.blockIcon;
        }
        if (side == metadata + 2) {
            return this.iconOxygenInput;
        }
        if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
            return this.iconOxygenOutput;
        }
        int oxygenLevel = 0;
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityOxygenStorageModule) {
            oxygenLevel = Math.min(((TileEntityOxygenStorageModule)tile).scaledOxygenLevel, 16);
        }
        return this.iconOxygenStorageModule[oxygenLevel];
    }
    
    public IIcon getIcon(final int side, int metadata) {
        if (side == 0 || side == 1) {
            return this.blockIcon;
        }
        if (metadata < 8) {
            if (metadata >= 4) {
                metadata -= 4;
                if ((metadata == 0 && side == 4) || (metadata == 1 && side == 5) || (metadata == 2 && side == 3) || (metadata == 3 && side == 2)) {
                    return this.iconCircuitFabricator;
                }
                if (side == ForgeDirection.getOrientation(metadata + 2).ordinal()) {
                    return this.iconInput;
                }
            }
            else if (metadata >= 0) {
                metadata += 0;
                if ((metadata == 0 && side == 4) || (metadata == 1 && side == 5) || (metadata == 2 && side == 3) || (metadata == 3 && side == 2)) {
                    return this.iconElectricCompressor;
                }
                if (side == ForgeDirection.getOrientation(metadata + 2).ordinal()) {
                    return this.iconInput;
                }
            }
            return this.iconMachineSide;
        }
        metadata -= 8;
        if (side == 0 || side == 1) {
            return this.blockIcon;
        }
        if (side == metadata + 2) {
            return this.iconOxygenInput;
        }
        if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
            return this.iconOxygenOutput;
        }
        return this.iconOxygenStorageModule[16];
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
        if (metadata >= 8) {
            world.setBlockMetadataWithNotify(x, y, z, 8 + change, 3);
        }
        else if (metadata >= 4) {
            world.setBlockMetadataWithNotify(x, y, z, 4 + change, 3);
        }
        else if (metadata >= 0) {
            world.setBlockMetadataWithNotify(x, y, z, 0 + change, 3);
        }
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        int original;
        final int metadata = original = par1World.getBlockMetadata(x, y, z);
        int change = 0;
        if (metadata >= 8) {
            original -= 8;
        }
        else if (metadata >= 4) {
            original -= 4;
        }
        else if (metadata >= 0) {
            original += 0;
            final TileEntity te = par1World.getTileEntity(x, y, z);
            if (te instanceof TileBaseUniversalElectrical) {
                ((TileBaseUniversalElectrical)te).updateFacing();
            }
        }
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
        if (metadata >= 8) {
            change += 8;
        }
        else if (metadata >= 4) {
            change += 4;
        }
        else if (metadata >= 0) {
            change += 0;
        }
        par1World.setBlockMetadataWithNotify(x, y, z, change, 3);
        return true;
    }
    
    public boolean onMachineActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        if (!par1World.isRemote) {
            par5EntityPlayer.openGui((Object)GalacticraftCore.instance, -1, par1World, x, y, z);
            return true;
        }
        return true;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata >= 8) {
            return new TileEntityOxygenStorageModule();
        }
        if (metadata >= 4) {
            return new TileEntityCircuitFabricator();
        }
        if (metadata >= 0) {
            return new TileEntityElectricIngotCompressor();
        }
        return null;
    }
    
    public ItemStack getElectricCompressor() {
        return new ItemStack((Block)this, 1, 0);
    }
    
    public ItemStack getCircuitFabricator() {
        return new ItemStack((Block)this, 1, 4);
    }
    
    public ItemStack getOxygenStorageModule() {
        return new ItemStack((Block)this, 1, 8);
    }
    
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(this.getElectricCompressor());
        par3List.add(this.getCircuitFabricator());
        par3List.add(this.getOxygenStorageModule());
    }
    
    public int damageDropped(final int metadata) {
        if (metadata >= 8) {
            return 8;
        }
        if (metadata >= 4) {
            return 4;
        }
        if (metadata >= 0) {
            return 0;
        }
        return 0;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = this.getDamageValue(world, x, y, z);
        return new ItemStack((Block)this, 1, metadata);
    }
    
    @Override
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 4: {
                return GCCoreUtil.translate("tile.circuitFabricator.description");
            }
            case 0: {
                return GCCoreUtil.translate("tile.compressorElectric.description");
            }
            case 8: {
                return GCCoreUtil.translate("tile.oxygenStorageModule.description");
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
