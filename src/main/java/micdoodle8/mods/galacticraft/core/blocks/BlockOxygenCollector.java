package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockOxygenCollector extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    @SideOnly(Side.CLIENT)
    private IIcon[] collectorIcons;
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconOutput;
    
    public BlockOxygenCollector(final String assetName) {
        super(Material.rock);
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
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_collector_fan");
        this.iconInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oxygen_output");
        this.iconOutput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
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
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        entityPlayer.openGui((Object)GalacticraftCore.instance, -1, world, x, y, z);
        return true;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return new TileEntityOxygenCollector();
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        if (side == metadata + 2) {
            return this.iconOutput;
        }
        if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
            return this.iconInput;
        }
        return this.iconMachineSide;
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
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World par1World, final int x, final int y, final int z, final Random rand) {
        if (par1World.getTileEntity(x, y, z) instanceof TileEntityOxygenCollector && ((TileEntityOxygenCollector)par1World.getTileEntity(x, y, z)).lastOxygenCollected > 1.0f) {
            for (int particleCount = 0; particleCount < 10; ++particleCount) {
                double x2 = x + rand.nextFloat();
                final double y2 = y + rand.nextFloat();
                double z2 = z + rand.nextFloat();
                double mX = 0.0;
                double mY = 0.0;
                double mZ = 0.0;
                final int dir = rand.nextInt(2) * 2 - 1;
                mX = (rand.nextFloat() - 0.5) * 0.5;
                mY = (rand.nextFloat() - 0.5) * 0.5;
                mZ = (rand.nextFloat() - 0.5) * 0.5;
                final int var2 = par1World.getBlockMetadata(x, y, z);
                if (var2 == 3 || var2 == 2) {
                    x2 = x + 0.5 + 0.25 * dir;
                    mX = rand.nextFloat() * 2.0f * dir;
                }
                else {
                    z2 = z + 0.5 + 0.25 * dir;
                    mZ = rand.nextFloat() * 2.0f * dir;
                }
                GalacticraftCore.proxy.spawnParticle("oxygen", new Vector3(x2, y2, z2), new Vector3(mX, mY, mZ), new Object[] { new Vector3(0.7, 0.7, 1.0) });
            }
        }
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
