package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockAirLockFrame extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    @SideOnly(Side.CLIENT)
    private IIcon[] airLockIcons;
    public static int METADATA_AIR_LOCK_FRAME;
    public static int METADATA_AIR_LOCK_CONTROLLER;
    
    public BlockAirLockFrame(final String assetName) {
        super(Material.rock);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, BlockAirLockFrame.METADATA_AIR_LOCK_FRAME));
        par3List.add(new ItemStack(par1, 1, BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER));
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4) {
        return true;
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityAirLockController && entityLiving instanceof EntityPlayer) {
            ((TileEntityAirLockController)tile).ownerName = ((EntityPlayer)entityLiving).getGameProfile().getName();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.airLockIcons = new IIcon[8])[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_off");
        this.airLockIcons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_on_1");
        this.airLockIcons[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_on_2");
        this.airLockIcons[3] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_on_3");
        this.airLockIcons[4] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_on_4");
        this.airLockIcons[5] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_on_5");
        this.airLockIcons[6] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_control_on");
        this.airLockIcons[7] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "airlock_control_off");
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, final int par2) {
        if (par2 < BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER) {
            return this.airLockIcons[0];
        }
        if (par1 == ForgeDirection.UP.ordinal() || par1 == ForgeDirection.DOWN.ordinal()) {
            return this.airLockIcons[0];
        }
        return this.airLockIcons[7];
    }
    
    public IIcon getIcon(final IBlockAccess world, final int par2, final int par3, final int par4, final int side) {
        if (world.getBlockMetadata(par2, par3, par4) < BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER) {
            for (final ForgeDirection orientation : ForgeDirection.values()) {
                if (orientation != ForgeDirection.UNKNOWN) {
                    final Vector3 vector = new Vector3((double)par2, (double)par3, (double)par4);
                    Vector3 blockVec = this.modifyPositionFromSide(vector.clone(), orientation, 1.0);
                    Block connection = blockVec.getBlock(world);
                    if (connection != null && connection.equals(GCBlocks.airLockSeal)) {
                        if (orientation.offsetY == -1) {
                            if (side == 0) {
                                return this.airLockIcons[1];
                            }
                            if (side == 1) {
                                return this.airLockIcons[0];
                            }
                            return this.airLockIcons[2];
                        }
                        else if (orientation.offsetY == 1) {
                            if (side == 0) {
                                return this.airLockIcons[0];
                            }
                            if (side == 1) {
                                return this.airLockIcons[1];
                            }
                            return this.airLockIcons[3];
                        }
                        else if (orientation.ordinal() == side) {
                            if (side == 0) {
                                return this.airLockIcons[0];
                            }
                            if (side == 1) {
                                return this.airLockIcons[1];
                            }
                            return this.airLockIcons[3];
                        }
                        else {
                            if (orientation.getOpposite().ordinal() == side) {
                                return this.airLockIcons[0];
                            }
                            blockVec = vector.clone().translate(new Vector3((double)orientation.offsetX, (double)orientation.offsetY, (double)orientation.offsetZ));
                            connection = blockVec.getBlock(world);
                            if (connection != null && connection.equals(GCBlocks.airLockSeal)) {
                                if (orientation.offsetX == 1) {
                                    if (side == 0) {
                                        return this.airLockIcons[4];
                                    }
                                    if (side == 1) {
                                        return this.airLockIcons[4];
                                    }
                                    if (side == 3) {
                                        return this.airLockIcons[4];
                                    }
                                    if (side == 2) {
                                        return this.airLockIcons[5];
                                    }
                                }
                                else if (orientation.offsetX == -1) {
                                    if (side == 0) {
                                        return this.airLockIcons[5];
                                    }
                                    if (side == 1) {
                                        return this.airLockIcons[5];
                                    }
                                    if (side == 3) {
                                        return this.airLockIcons[5];
                                    }
                                    if (side == 2) {
                                        return this.airLockIcons[4];
                                    }
                                }
                                else if (orientation.offsetZ == 1) {
                                    if (side == 0) {
                                        return this.airLockIcons[2];
                                    }
                                    if (side == 1) {
                                        return this.airLockIcons[2];
                                    }
                                    if (side == 4) {
                                        return this.airLockIcons[4];
                                    }
                                    if (side == 5) {
                                        return this.airLockIcons[5];
                                    }
                                }
                                else if (orientation.offsetZ == -1) {
                                    if (side == 0) {
                                        return this.airLockIcons[3];
                                    }
                                    if (side == 1) {
                                        return this.airLockIcons[3];
                                    }
                                    if (side == 4) {
                                        return this.airLockIcons[5];
                                    }
                                    if (side == 5) {
                                        return this.airLockIcons[4];
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return this.airLockIcons[0];
        }
        if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
            return this.airLockIcons[0];
        }
        final TileEntity tile = world.getTileEntity(par2, par3, par4);
        if (!(tile instanceof TileEntityAirLockController)) {
            return this.airLockIcons[6];
        }
        final TileEntityAirLockController controller = (TileEntityAirLockController)tile;
        if (controller.active) {
            return this.airLockIcons[6];
        }
        return this.airLockIcons[7];
    }
    
    public Vector3 modifyPositionFromSide(final Vector3 vec, final ForgeDirection side, final double amount) {
        switch (side.ordinal()) {
            case 0: {
                vec.y -= amount;
                break;
            }
            case 1: {
                vec.y += amount;
                break;
            }
            case 2: {
                vec.z -= amount;
                break;
            }
            case 3: {
                vec.z += amount;
                break;
            }
            case 4: {
                vec.x -= amount;
                break;
            }
            case 5: {
                vec.x += amount;
                break;
            }
        }
        return vec;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata < BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER) {
            return new TileEntityAirLock();
        }
        return new TileEntityAirLockController();
    }
    
    public boolean canConnectRedstone(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        return true;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (metadata >= BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER && tile instanceof TileEntityAirLockController) {
            entityPlayer.openGui((Object)GalacticraftCore.instance, -1, world, x, y, z);
            return true;
        }
        return false;
    }
    
    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int par6) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityAirLockController) {
            ((TileEntityAirLockController)tile).unsealAirLock();
        }
        super.breakBlock(world, x, y, z, block, par6);
    }
    
    public int damageDropped(final int metadata) {
        if (metadata >= BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER) {
            return BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER;
        }
        if (metadata >= BlockAirLockFrame.METADATA_AIR_LOCK_FRAME) {
            return BlockAirLockFrame.METADATA_AIR_LOCK_FRAME;
        }
        return 0;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    static {
        BlockAirLockFrame.METADATA_AIR_LOCK_FRAME = 0;
        BlockAirLockFrame.METADATA_AIR_LOCK_CONTROLLER = 1;
    }
}
