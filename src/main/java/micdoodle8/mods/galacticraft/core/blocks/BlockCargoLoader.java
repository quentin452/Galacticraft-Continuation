package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockCargoLoader extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconFrontLoader;
    private IIcon iconFrontUnloader;
    private IIcon iconItemInput;
    private IIcon iconItemOutput;
    public static final int METADATA_CARGO_LOADER = 0;
    public static final int METADATA_CARGO_UNLOADER = 4;
    
    public BlockCargoLoader(final String assetName) {
        super(Material.rock);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 4));
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public void onBlockAdded(final World world, final int x, final int y, final int z) {
        super.onBlockAdded(world, x, y, z);
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityCargoLoader) {
                ((TileEntityCargoLoader)tileEntity).checkForCargoEntity();
            }
            else if (tileEntity instanceof TileEntityCargoUnloader) {
                ((TileEntityCargoUnloader)tileEntity).checkForCargoEntity();
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconFrontLoader = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_cargoloader");
        this.iconFrontUnloader = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_cargounloader");
        this.iconItemInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_item_input");
        this.iconItemOutput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_item_output");
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        entityPlayer.openGui((Object)GalacticraftCore.instance, -1, world, x, y, z);
        return true;
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        int shiftedMeta = metadata;
        if (side == 0 || side == 1) {
            return this.iconMachineSide;
        }
        if (metadata >= 4) {
            shiftedMeta -= 4;
            if (side == shiftedMeta + 2) {
                return this.iconInput;
            }
            if (side == ForgeDirection.getOrientation(shiftedMeta + 2).getOpposite().ordinal()) {
                return (metadata < 4) ? this.iconItemInput : this.iconItemOutput;
            }
            return (metadata < 4) ? this.iconFrontLoader : this.iconFrontUnloader;
        }
        else {
            if (metadata < 0) {
                return this.iconMachineSide;
            }
            shiftedMeta += 0;
            if (side == shiftedMeta + 2) {
                return this.iconInput;
            }
            if (side == ForgeDirection.getOrientation(shiftedMeta + 2).getOpposite().ordinal()) {
                return (metadata < 4) ? this.iconItemInput : this.iconItemOutput;
            }
            return (metadata < 4) ? this.iconFrontLoader : this.iconFrontUnloader;
        }
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata < 4) {
            return new TileEntityCargoLoader();
        }
        return new TileEntityCargoUnloader();
    }
    
    public boolean onUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        int shiftedMeta;
        final int metadata = shiftedMeta = world.getBlockMetadata(x, y, z);
        int baseMeta = 0;
        if (metadata >= 4) {
            baseMeta = 4;
        }
        else if (metadata >= 0) {
            baseMeta = 0;
        }
        shiftedMeta -= baseMeta;
        int change = 0;
        switch (shiftedMeta) {
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
        final TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBaseUniversalElectrical) {
            ((TileBaseUniversalElectrical)te).updateFacing();
        }
        return world.setBlockMetadataWithNotify(x, y, z, baseMeta + change, 3);
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        final int metadata = world.getBlockMetadata(x, y, z);
        int change = 0;
        int baseMeta = 0;
        if (metadata >= 4) {
            baseMeta = 4;
        }
        else if (metadata >= 0) {
            baseMeta = 0;
        }
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
        world.setBlockMetadataWithNotify(x, y, z, baseMeta + change, 3);
        for (int dX = -2; dX < 3; ++dX) {
            for (int dZ = -2; dZ < 3; ++dZ) {
                final Block block = world.getBlock(x + dX, y, z + dZ);
                if (block == GCBlocks.landingPadFull) {
                    world.markBlockForUpdate(x + dX, y, z + dZ);
                }
            }
        }
    }
    
    public void onBlockDestroyedByPlayer(final World world, final int x, final int y, final int z, final int par5) {
        super.onBlockDestroyedByPlayer(world, x, y, z, par5);
        for (int dX = -2; dX < 3; ++dX) {
            for (int dZ = -2; dZ < 3; ++dZ) {
                final Block block = world.getBlock(x + dX, y, z + dZ);
                if (block == GCBlocks.landingPadFull) {
                    world.markBlockForUpdate(x + dX, y, z + dZ);
                }
            }
        }
    }
    
    public int damageDropped(final int metadata) {
        if (metadata >= 4) {
            return 4;
        }
        if (metadata >= 0) {
            return 0;
        }
        return 0;
    }
    
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 0: {
                return GCCoreUtil.translate("tile.cargoLoader.description");
            }
            case 4: {
                return GCCoreUtil.translate("tile.cargoUnloader.description");
            }
            default: {
                return "";
            }
        }
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
