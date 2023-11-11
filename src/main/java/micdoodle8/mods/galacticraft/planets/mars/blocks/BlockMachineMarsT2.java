package micdoodle8.mods.galacticraft.planets.mars.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockMachineMarsT2 extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public static final int GAS_LIQUEFIER = 0;
    public static final int METHANE_SYNTHESIZER = 4;
    public static final int ELECTROLYZER = 8;
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconGasInput;
    private IIcon iconGasOutput;
    private IIcon iconWaterInput;
    private IIcon iconGasLiquefier;
    private IIcon iconMethaneSynthesizer;
    private IIcon iconElectrolyzer;
    
    public BlockMachineMarsT2() {
        super(GCBlocks.machine);
        this.setStepSound(Block.soundTypeMetal);
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("galacticraftasteroids:machine");
        this.iconInput = par1IconRegister.registerIcon("galacticraftasteroids:machine_input");
        this.iconMachineSide = par1IconRegister.registerIcon("galacticraftasteroids:machine_side_warning");
        this.iconGasInput = par1IconRegister.registerIcon("galacticraftasteroids:machine_oxygen_input_warning");
        this.iconGasOutput = par1IconRegister.registerIcon("galacticraftasteroids:machine_oxygen_output_warning");
        this.iconWaterInput = par1IconRegister.registerIcon("galacticraftasteroids:machine_water_input_warning");
        this.iconGasLiquefier = par1IconRegister.registerIcon("galacticraftasteroids:gasLiquefier");
        this.iconMethaneSynthesizer = par1IconRegister.registerIcon("galacticraftasteroids:methaneSynthesizer");
        this.iconElectrolyzer = par1IconRegister.registerIcon("galacticraftasteroids:electrolyzer");
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public int getRenderType() {
        return GalacticraftPlanets.getBlockRenderID((Block)this);
    }
    
    public IIcon getIcon(final int side, int metadata) {
        if (side == 0) {
            return this.iconInput;
        }
        if (side == 1) {
            return this.blockIcon;
        }
        final int metaside = (metadata & 0x3) + 2;
        metadata &= 0xC;
        if (metadata == 0) {
            if (side == metaside) {
                return this.iconGasInput;
            }
            if (7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == side) {
                return this.iconGasLiquefier;
            }
        }
        else if (metadata == 4) {
            if (side == metaside) {
                return this.iconGasInput;
            }
            if (side == (metaside ^ 0x1)) {
                return this.iconGasOutput;
            }
            if (7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == side) {
                return this.iconMethaneSynthesizer;
            }
        }
        else if (metadata == 8) {
            if (side == (metaside ^ 0x1)) {
                return this.iconGasOutput;
            }
            if (7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == side) {
                return this.iconElectrolyzer;
            }
            if (side == metaside) {
                return this.iconWaterInput;
            }
            return this.iconGasOutput;
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
        final TileEntity te = par1World.getTileEntity(x, y, z);
        if (te instanceof TileBaseUniversalElectrical) {
            ((TileBaseUniversalElectrical)te).updateFacing();
        }
        par1World.setBlockMetadataWithNotify(x, y, z, (metadata & 0xC) + change, 3);
        return true;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        par5EntityPlayer.openGui((Object)GalacticraftPlanets.instance, 2, world, x, y, z);
        return true;
    }
    
    public TileEntity createTileEntity(final World world, int metadata) {
        metadata &= 0xC;
        if (metadata == 0) {
            return (TileEntity)new TileEntityGasLiquefier();
        }
        if (metadata == 4) {
            return (TileEntity)new TileEntityMethaneSynthesizer();
        }
        if (metadata == 8) {
            return (TileEntity)new TileEntityElectrolyzer();
        }
        return null;
    }
    
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack((Block)this, 1, 0));
        par3List.add(new ItemStack((Block)this, 1, 4));
        par3List.add(new ItemStack((Block)this, 1, 8));
    }
    
    public int damageDropped(final int metadata) {
        return metadata & 0xC;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = this.getDamageValue(world, x, y, z);
        return new ItemStack((Block)this, 1, metadata);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World par1World, final int par2, final int par3, final int par4, final Random rand) {
        final TileEntity te = par1World.getTileEntity(par2, par3, par4);
        if (te instanceof TileEntityGasLiquefier) {
            final TileEntityGasLiquefier tileEntity = (TileEntityGasLiquefier)te;
            if (tileEntity.processTicks > 0) {
                final float x = par2 + 0.5f;
                final float y = par3 + 0.8f + 0.05f * rand.nextInt(3);
                final float z = par4 + 0.5f;
                for (float i = -0.41f + 0.16f * rand.nextFloat(); i < 0.5f; i += 0.167f) {
                    if (rand.nextInt(3) == 0) {
                        GalacticraftCore.proxy.spawnParticle("whiteSmokeTiny", new Vector3((double)(x + i), (double)y, (double)(z - 0.41f)), new Vector3(0.0, -0.015, -0.0015), new Object[0]);
                    }
                    if (rand.nextInt(3) == 0) {
                        GalacticraftCore.proxy.spawnParticle("whiteSmokeTiny", new Vector3((double)(x + i), (double)y, (double)(z + 0.537f)), new Vector3(0.0, -0.015, 0.0015), new Object[0]);
                    }
                    if (rand.nextInt(3) == 0) {
                        GalacticraftCore.proxy.spawnParticle("whiteSmokeTiny", new Vector3((double)(x - 0.41f), (double)y, (double)(z + i)), new Vector3(-0.0015, -0.015, 0.0), new Object[0]);
                    }
                    if (rand.nextInt(3) == 0) {
                        GalacticraftCore.proxy.spawnParticle("whiteSmokeTiny", new Vector3((double)(x + 0.537f), (double)y, (double)(z + i)), new Vector3(0.0015, -0.015, 0.0), new Object[0]);
                    }
                }
            }
        }
    }
    
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 8: {
                return GCCoreUtil.translate("tile.electrolyzer.description");
            }
            case 0: {
                return GCCoreUtil.translate("tile.gasLiquefier.description");
            }
            case 4: {
                return GCCoreUtil.translate("tile.methaneSynthesizer.description");
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
