package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import java.util.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.item.*;
import net.minecraft.entity.item.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockRefinery extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    private final Random refineryRand;
    private IIcon iconMachineSide;
    private IIcon iconFuelOutput;
    private IIcon iconOilInput;
    private IIcon iconFront;
    private IIcon iconBack;
    private IIcon iconTop;
    
    protected BlockRefinery(final String assetName) {
        super(Material.rock);
        this.refineryRand = new Random();
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
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconFuelOutput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_fuel_input");
        this.iconOilInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oil_input");
        this.iconFront = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "refinery_front");
        this.iconBack = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "refinery_side");
        this.iconTop = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random) {
        final TileEntity te = par1World.getTileEntity(par2, par3, par4);
        if (te instanceof TileEntityRefinery) {
            final TileEntityRefinery refinery = (TileEntityRefinery)te;
            if (refinery.processTicks > 0) {
                par1World.getBlockMetadata(par2, par3, par4);
                final float var7 = par2 + 0.5f;
                final float var8 = par3 + 1.1f;
                final float var9 = par4 + 0.5f;
                final float var10 = 0.0f;
                final float var11 = 0.0f;
                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        par1World.spawnParticle("smoke", var7 + 0.0f + i * 0.2, (double)var8, var9 + 0.0f + j * 0.2, 0.0, 0.01, 0.0);
                        par1World.spawnParticle("flame", var7 + 0.0f + i * 0.1, var8 - 0.2, var9 + 0.0f + j * 0.1, 0.0, 1.0E-4, 0.0);
                    }
                }
            }
        }
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        entityPlayer.openGui((Object)GalacticraftCore.instance, -1, world, x, y, z);
        return true;
    }
    
    public boolean onUseWrench(final World par1World, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        int change = 0;
        switch (par1World.getBlockMetadata(x, y, z)) {
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
        par1World.setBlockMetadataWithNotify(x, y, z, change, 3);
        return true;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return new TileEntityRefinery();
    }
    
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final Block par5, final int par6) {
        final TileEntityRefinery var7 = (TileEntityRefinery)par1World.getTileEntity(par2, par3, par4);
        if (var7 != null) {
            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
                final ItemStack var9 = var7.getStackInSlot(var8);
                if (var9 != null) {
                    final float var10 = this.refineryRand.nextFloat() * 0.8f + 0.1f;
                    final float var11 = this.refineryRand.nextFloat() * 0.8f + 0.1f;
                    final float var12 = this.refineryRand.nextFloat() * 0.8f + 0.1f;
                    while (var9.stackSize > 0) {
                        int var13 = this.refineryRand.nextInt(21) + 10;
                        if (var13 > var9.stackSize) {
                            var13 = var9.stackSize;
                        }
                        final ItemStack itemStack = var9;
                        itemStack.stackSize -= var13;
                        final EntityItem var14 = new EntityItem(par1World, (double)(par2 + var10), (double)(par3 + var11), (double)(par4 + var12), new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
                        if (var9.hasTagCompound()) {
                            var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                        }
                        final float var15 = 0.05f;
                        var14.motionX = (float)this.refineryRand.nextGaussian() * 0.05f;
                        var14.motionY = (float)this.refineryRand.nextGaussian() * 0.05f + 0.2f;
                        var14.motionZ = (float)this.refineryRand.nextGaussian() * 0.05f;
                        par1World.spawnEntityInWorld((Entity)var14);
                    }
                }
            }
        }
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        if (side == metadata + 2) {
            return this.iconOilInput;
        }
        if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
            return this.iconFuelOutput;
        }
        if (side == 1) {
            return this.iconTop;
        }
        if (side == 0) {
            return this.iconMachineSide;
        }
        if ((metadata == 0 && side == 4) || (metadata == 1 && side == 5) || (metadata == 2 && side == 3) || (metadata == 3 && side == 2)) {
            return this.iconFront;
        }
        return this.iconBack;
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
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
