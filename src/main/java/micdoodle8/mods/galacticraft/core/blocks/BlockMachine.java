package micdoodle8.mods.galacticraft.core.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator;
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace;
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule;
import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class BlockMachine extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc {

    public static final int COAL_GENERATOR_METADATA = 0;
    public static final int COMPRESSOR_METADATA = 12;

    private IIcon iconMachineSide;
    private IIcon iconOutput;

    private IIcon iconCoalGenerator;
    private IIcon iconCompressor;

    public BlockMachine(String assetName) {
        super(GCBlocks.machine);
        this.setBlockName("basicMachine");
        this.setHardness(1.0F);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @Override
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine");
        this.iconOutput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_output");
        this.iconMachineSide = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_side");

        this.iconCoalGenerator = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "coalGenerator");
        this.iconCompressor = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "compressor");
    }

    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random) {
        final TileEntity tile = par1World.getTileEntity(x, y, z);

        if (tile instanceof TileEntityCoalGenerator) {
            TileEntityCoalGenerator tileEntity = (TileEntityCoalGenerator) tile;

            if (tileEntity.heatGJperTick > 0) {
                final int metadata = par1World.getBlockMetadata(x, y, z);
                final float var7 = x + 0.5F;
                final float var8 = y + 0.0F + par5Random.nextFloat() * 6.0F / 16.0F;
                final float var9 = z + 0.5F;
                final float var10 = 0.52F;
                final float var11 = par5Random.nextFloat() * 0.6F - 0.3F;

                switch (metadata) {
                    case 0:
                        par1World.spawnParticle("smoke", var7 - var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                        par1World.spawnParticle("flame", var7 - var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                        break;
                    case 1:
                        par1World.spawnParticle("smoke", var7 + var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                        par1World.spawnParticle("flame", var7 + var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                        break;
                    case 2:
                        par1World.spawnParticle("smoke", var7 + var11, var8, var9 + var10, 0.0D, 0.0D, 0.0D);
                        par1World.spawnParticle("flame", var7 + var11, var8, var9 + var10, 0.0D, 0.0D, 0.0D);
                        break;
                    case 3:
                        par1World.spawnParticle("smoke", var7 + var11, var8, var9 - var10, 0.0D, 0.0D, 0.0D);
                        par1World.spawnParticle("flame", var7 + var11, var8, var9 - var10, 0.0D, 0.0D, 0.0D);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return this.getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @Override
    public IIcon getIcon(int side, int metadata) {
        if (side == 0 || side == 1) {
            return this.blockIcon;
        }

        if (metadata >= BlockMachine.COMPRESSOR_METADATA) {
            metadata -= BlockMachine.COMPRESSOR_METADATA;

            if (metadata == 0 && side == 4 || metadata == 1 && side == 5
                || metadata == 2 && side == 3
                || metadata == 3 && side == 2) {
                return this.iconCompressor;
            }
        } else {
            // If it is the front side
            if (side == metadata + 2) {
                return this.iconOutput;
            }
            // If it is the back side
            if (metadata == 0 && side == 4 || metadata == 1 && side == 5
                || metadata == 2 && side == 3
                || metadata == 3 && side == 2) {
                return this.iconCoalGenerator;
            }
        }

        return this.iconMachineSide;
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
        final int metadata = world.getBlockMetadata(x, y, z);

        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int change = 0;

        switch (angle) {
            case 0:
                change = 3;
                break;
            case 1:
                change = 1;
                break;
            case 2:
                change = 2;
                break;
            case 3:
                change = 0;
                break;
        }

        world.setBlockMetadataWithNotify(x, y, z, (metadata & 12) + change, 3);
    }

    @Override
    public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side,
        float hitX, float hitY, float hitZ) {
        final int metadata = par1World.getBlockMetadata(x, y, z);
        final int original = metadata & 3;
        int change = 0;

        // Re-orient the block
        switch (original) {
            case 0:
                change = 3;
                break;
            case 3:
                change = 1;
                break;
            case 1:
                change = 2;
                break;
            case 2:
                change = 0;
                break;
        }

        par1World.setBlockMetadataWithNotify(x, y, z, (metadata & 12) + change, 3);
        return true;
    }

    /**
     * Called when the block is right clicked by the player
     */
    @Override
    public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side,
        float hitX, float hitY, float hitZ) {
        final int metadata = par1World.getBlockMetadata(x, y, z);

        if (!par1World.isRemote) {
            if (metadata >= BlockMachine.COMPRESSOR_METADATA) {
                par5EntityPlayer.openGui(GalacticraftCore.instance, -1, par1World, x, y, z);
                return true;
            }
            par5EntityPlayer.openGui(GalacticraftCore.instance, -1, par1World, x, y, z);
        }

        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        metadata &= 12;
        switch (metadata) {
            case BlockMachine.COMPRESSOR_METADATA:
                return new TileEntityIngotCompressor();
            case 4:
                return new TileEntityEnergyStorageModule();
            case 8:
                return new TileEntityElectricFurnace();
            default:
                return new TileEntityCoalGenerator();
        }
    }

    public ItemStack getCompressor() {
        return new ItemStack(this, 1, BlockMachine.COMPRESSOR_METADATA);
    }

    public ItemStack getCoalGenerator() {
        return new ItemStack(this, 1, BlockMachine.COAL_GENERATOR_METADATA);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        par3List.add(this.getCoalGenerator());
        par3List.add(this.getCompressor());
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata & 12;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        final int metadata = this.getDamageValue(world, x, y, z);

        return new ItemStack(this, 1, metadata);
    }

    @Override
    public String getShiftDescription(int meta) {
        switch (meta) {
            case COAL_GENERATOR_METADATA:
                return GCCoreUtil.translate("tile.coalGenerator.description");
            case COMPRESSOR_METADATA:
                return GCCoreUtil.translate("tile.compressor.description");
        }
        return "";
    }

    @Override
    public boolean showDescription(int meta) {
        return true;
    }
}
