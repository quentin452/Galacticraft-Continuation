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

public class BlockMachineTiered extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public static final int STORAGE_MODULE_METADATA = 0;
    public static final int ELECTRIC_FURNACE_METADATA = 4;
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconOutput;
    private IIcon iconTier2;
    private IIcon iconMachineSideT2;
    private IIcon iconInputT2;
    private IIcon iconOutputT2;
    private IIcon[] iconEnergyStorageModule;
    private IIcon[] iconEnergyStorageModuleT2;
    private IIcon iconElectricFurnace;
    private IIcon iconElectricFurnaceT2;
    
    public BlockMachineTiered(final String assetName) {
        super(GCBlocks.machine);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + "machine");
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
        this.iconInput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
        this.iconOutput = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_output");
        this.iconMachineSide = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_side");
        this.iconEnergyStorageModule = new IIcon[17];
        for (int i = 0; i < this.iconEnergyStorageModule.length; ++i) {
            this.iconEnergyStorageModule[i] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "energyStorageModule_" + i);
        }
        this.iconElectricFurnace = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "electricFurnace");
        if (GalacticraftCore.isPlanetsLoaded) {
            this.iconTier2 = iconRegister.registerIcon("galacticraftasteroids:machine");
            this.iconInputT2 = iconRegister.registerIcon("galacticraftasteroids:machine_input");
            this.iconOutputT2 = iconRegister.registerIcon("galacticraftasteroids:machine_output");
            this.iconMachineSideT2 = iconRegister.registerIcon("galacticraftasteroids:machine_side");
            this.iconEnergyStorageModuleT2 = new IIcon[17];
            for (int i = 0; i < this.iconEnergyStorageModule.length; ++i) {
                this.iconEnergyStorageModuleT2[i] = iconRegister.registerIcon("galacticraftasteroids:energyStorageModule_" + i);
            }
            this.iconElectricFurnaceT2 = iconRegister.registerIcon("galacticraftasteroids:electricFurnace");
        }
        else {
            this.iconTier2 = iconRegister.registerIcon("void");
            this.iconInputT2 = iconRegister.registerIcon("void");
            this.iconOutputT2 = iconRegister.registerIcon("void");
            this.iconMachineSideT2 = iconRegister.registerIcon("void");
            this.iconEnergyStorageModuleT2 = new IIcon[17];
            for (int i = 0; i < this.iconEnergyStorageModule.length; ++i) {
                this.iconEnergyStorageModuleT2[i] = iconRegister.registerIcon("void");
            }
            this.iconElectricFurnaceT2 = iconRegister.registerIcon("void");
        }
    }
    
    public IIcon getIcon(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final int type = metadata & 0x4;
        final int metaside = (metadata & 0x3) + 2;
        if (type != 0) {
            return this.getIcon(side, metadata);
        }
        if (side == 0 || side == 1) {
            if (metadata >= 8) {
                return this.iconTier2;
            }
            return this.blockIcon;
        }
        else if (side == metaside) {
            if (metadata >= 8) {
                return this.iconOutputT2;
            }
            return this.iconOutput;
        }
        else if (side == (metaside ^ 0x1)) {
            if (metadata >= 8) {
                return this.iconInputT2;
            }
            return this.iconInput;
        }
        else {
            final TileEntity tile = world.getTileEntity(x, y, z);
            int level = 0;
            if (tile instanceof TileEntityEnergyStorageModule) {
                level = ((TileEntityEnergyStorageModule)tile).scaledEnergyLevel;
            }
            if (metadata >= 8) {
                return this.iconEnergyStorageModuleT2[level];
            }
            return this.iconEnergyStorageModule[level];
        }
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        final int metaside = (metadata & 0x3) + 2;
        if (side == 0 || side == 1) {
            if (metadata >= 8) {
                return this.iconTier2;
            }
            return this.blockIcon;
        }
        else if ((metadata & 0x4) == 0x4) {
            if (side == metaside) {
                if (metadata >= 8) {
                    return this.iconInputT2;
                }
                return this.iconInput;
            }
            else if ((metaside == 2 && side == 4) || (metaside == 3 && side == 5) || (metaside == 4 && side == 3) || (metaside == 5 && side == 2)) {
                if (metadata >= 8) {
                    return this.iconElectricFurnaceT2;
                }
                return this.iconElectricFurnace;
            }
            else {
                if (metadata >= 8) {
                    return this.iconMachineSideT2;
                }
                return this.iconMachineSide;
            }
        }
        else if (side == metaside) {
            if (metadata >= 8) {
                return this.iconOutputT2;
            }
            return this.iconOutput;
        }
        else if (side == (metaside ^ 0x1)) {
            if (metadata >= 8) {
                return this.iconInputT2;
            }
            return this.iconInput;
        }
        else {
            if (metadata >= 8) {
                return this.iconEnergyStorageModuleT2[16];
            }
            return this.iconEnergyStorageModule[16];
        }
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
    
    public boolean onMachineActivated(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        if (!par1World.isRemote) {
            par5EntityPlayer.openGui((Object)GalacticraftCore.instance, -1, par1World, x, y, z);
        }
        return true;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        final int tier = metadata / 8 + 1;
        if ((metadata & 0x4) == 0x4) {
            return new TileEntityElectricFurnace(tier);
        }
        return new TileEntityEnergyStorageModule(tier);
    }
    
    public ItemStack getEnergyStorageModule() {
        return new ItemStack((Block)this, 1, 0);
    }
    
    public ItemStack getEnergyStorageCluster() {
        return new ItemStack((Block)this, 1, 8);
    }
    
    public ItemStack getElectricFurnace() {
        return new ItemStack((Block)this, 1, 4);
    }
    
    public ItemStack getElectricArcFurnace() {
        return new ItemStack((Block)this, 1, 12);
    }
    
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(this.getEnergyStorageModule());
        par3List.add(this.getElectricFurnace());
        if (GalacticraftCore.isPlanetsLoaded) {
            par3List.add(this.getEnergyStorageCluster());
            par3List.add(this.getElectricArcFurnace());
        }
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
        final int tier = (meta >= 8) ? 2 : 1;
        switch (meta & 0x4) {
            case 4: {
                return GCCoreUtil.translate("tile.electricFurnaceTier" + tier + ".description");
            }
            case 0: {
                return GCCoreUtil.translate("tile.energyStorageModuleTier" + tier + ".description");
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
