package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockOxygenCompressor extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    public static final int OXYGEN_COMPRESSOR_METADATA = 0;
    public static final int OXYGEN_DECOMPRESSOR_METADATA = 4;
    private IIcon iconMachineSide;
    private IIcon iconCompressor1;
    private IIcon iconCompressor2;
    private IIcon iconDecompressor;
    private IIcon iconOxygenInput;
    private IIcon iconOxygenOutput;
    private IIcon iconInput;
    
    public BlockOxygenCompressor(final boolean isActive, final String assetName) {
        super(Material.rock);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconCompressor1 = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_compressor_1");
        this.iconCompressor2 = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_compressor_2");
        this.iconDecompressor = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_decompressor_1");
        this.iconOxygenInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oxygen_input");
        this.iconOxygenOutput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oxygen_output");
        this.iconInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        int original;
        final int metadata = original = par1World.getBlockMetadata(x, y, z);
        if (metadata >= 4) {
            original -= 4;
        }
        else if (metadata >= 0) {
            original += 0;
        }
        int meta = 0;
        switch (original) {
            case 0: {
                meta = 3;
                break;
            }
            case 3: {
                meta = 1;
                break;
            }
            case 1: {
                meta = 2;
                break;
            }
            case 2: {
                meta = 0;
                break;
            }
        }
        if (metadata >= 4) {
            meta += 4;
        }
        else if (metadata >= 0) {
            meta += 0;
        }
        final TileEntity te = par1World.getTileEntity(x, y, z);
        if (te instanceof TileBaseUniversalElectrical) {
            ((TileBaseUniversalElectrical)te).updateFacing();
        }
        par1World.setBlockMetadataWithNotify(x, y, z, meta, 3);
        return true;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        entityPlayer.openGui((Object)GalacticraftCore.instance, -1, world, x, y, z);
        return true;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata >= 4) {
            return new TileEntityOxygenDecompressor();
        }
        if (metadata >= 0) {
            return new TileEntityOxygenCompressor();
        }
        return null;
    }
    
    public IIcon getIcon(final int side, int metadata) {
        if (side == 0 || side == 1) {
            return this.iconMachineSide;
        }
        if (metadata >= 4) {
            metadata -= 4;
            if (side == metadata + 2) {
                return this.iconInput;
            }
            if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
                return this.iconOxygenOutput;
            }
            if ((metadata == 0 && side == 5) || (metadata == 3 && side == 3) || (metadata == 1 && side == 4) || (metadata == 2 && side == 2)) {
                return this.iconCompressor2;
            }
            return this.iconDecompressor;
        }
        else {
            if (metadata < 0) {
                return this.iconMachineSide;
            }
            metadata += 0;
            if (side == metadata + 2) {
                return this.iconInput;
            }
            if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
                return this.iconOxygenInput;
            }
            if ((metadata == 0 && side == 5) || (metadata == 3 && side == 3) || (metadata == 1 && side == 4) || (metadata == 2 && side == 2)) {
                return this.iconCompressor2;
            }
            return this.iconCompressor1;
        }
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
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
        if (itemStack.getItemDamage() >= 4) {
            change += 4;
        }
        else if (itemStack.getItemDamage() >= 0) {
            change += 0;
        }
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
    }
    
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack((Block)this, 1, 0));
        par3List.add(new ItemStack((Block)this, 1, 4));
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
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = this.getDamageValue(world, x, y, z);
        return new ItemStack((Block)this, 1, metadata);
    }
    
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 0: {
                return GCCoreUtil.translate("tile.oxygenCompressor.description");
            }
            case 4: {
                return GCCoreUtil.translate("tile.oxygenDecompressor.description");
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
