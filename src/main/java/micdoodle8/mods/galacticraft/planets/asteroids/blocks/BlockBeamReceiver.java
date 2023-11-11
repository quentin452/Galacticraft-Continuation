package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;

public class BlockBeamReceiver extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public BlockBeamReceiver(final String assetName) {
        super(Material.iron);
        this.setBlockName(assetName);
        this.setBlockTextureName("stone");
        this.setStepSound(Block.soundTypeMetal);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        final int oldMeta = world.getBlockMetadata(x, y, z);
        final int meta = this.getMetadataFromAngle(world, x, y, z, ForgeDirection.getOrientation(oldMeta).getOpposite().ordinal());
        if (meta == -1) {
            world.func_147480_a(x, y, z, true);
        }
        if (meta != oldMeta) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
            final TileEntity thisTile = world.getTileEntity(x, y, z);
            if (thisTile instanceof TileEntityBeamReceiver) {
                final TileEntityBeamReceiver thisReceiver = (TileEntityBeamReceiver)thisTile;
                thisReceiver.setFacing(ForgeDirection.getOrientation(meta));
                thisReceiver.invalidateReflector();
                thisReceiver.initiateReflector();
            }
        }
        super.onNeighborBlockChange(world, x, y, z, block);
    }
    
    public void onBlockAdded(final World world, final int x, final int y, final int z) {
        final TileEntity thisTile = world.getTileEntity(x, y, z);
        if (thisTile instanceof TileEntityBeamReceiver) {
            ((TileEntityBeamReceiver)thisTile).setFacing(ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)));
        }
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta != -1) {
            final ForgeDirection dir = ForgeDirection.getOrientation(meta);
            switch (dir) {
                case UP: {
                    this.setBlockBounds(0.3f, 0.3f, 0.3f, 0.7f, 1.0f, 0.7f);
                    break;
                }
                case DOWN: {
                    this.setBlockBounds(0.2f, 0.0f, 0.2f, 0.8f, 0.42f, 0.8f);
                    break;
                }
                case EAST: {
                    this.setBlockBounds(0.58f, 0.2f, 0.2f, 1.0f, 0.8f, 0.8f);
                    break;
                }
                case WEST: {
                    this.setBlockBounds(0.0f, 0.2f, 0.2f, 0.42f, 0.8f, 0.8f);
                    break;
                }
                case NORTH: {
                    this.setBlockBounds(0.2f, 0.2f, 0.0f, 0.8f, 0.8f, 0.42f);
                    break;
                }
                case SOUTH: {
                    this.setBlockBounds(0.2f, 0.2f, 0.58f, 0.8f, 0.8f, 1.0f);
                    break;
                }
            }
        }
    }
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
    }
    
    private int getMetadataFromAngle(final World world, final int x, final int y, final int z, final int side) {
        final ForgeDirection direction = ForgeDirection.getOrientation(side).getOpposite();
        TileEntity tileAt = world.getTileEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
        if (tileAt instanceof EnergyStorageTile) {
            if (((EnergyStorageTile)tileAt).getModeFromDirection(direction.getOpposite()) != null) {
                return direction.ordinal();
            }
            return -1;
        }
        else {
            if (EnergyUtil.otherModCanReceive(tileAt, direction.getOpposite())) {
                return direction.ordinal();
            }
            for (final ForgeDirection adjacentDir : ForgeDirection.VALID_DIRECTIONS) {
                if (adjacentDir != direction) {
                    tileAt = world.getTileEntity(x + adjacentDir.offsetX, y + adjacentDir.offsetY, z + adjacentDir.offsetZ);
                    if (!(tileAt instanceof IConductor)) {
                        if (tileAt instanceof EnergyStorageTile && ((EnergyStorageTile)tileAt).getModeFromDirection(adjacentDir.getOpposite()) != null) {
                            return adjacentDir.ordinal();
                        }
                        if (EnergyUtil.otherModCanReceive(tileAt, adjacentDir.getOpposite())) {
                            return adjacentDir.ordinal();
                        }
                    }
                }
            }
            return -1;
        }
    }
    
    public int onBlockPlaced(final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int meta) {
        return this.getMetadataFromAngle(world, x, y, z, side);
    }
    
    public boolean canPlaceBlockOnSide(final World world, final int x, final int y, final int z, final int side) {
        if (this.getMetadataFromAngle(world, x, y, z, side) != -1) {
            return true;
        }
        if (world.isRemote) {
            this.sendIncorrectSideMessage();
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    private void sendIncorrectSideMessage() {
        FMLClientHandler.instance().getClient().thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumColor.RED + GCCoreUtil.translate("gui.receiver.cannotAttach")));
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return -1;
    }
    
    public int damageDropped(final int metadata) {
        return 0;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return (TileEntity)new TileEntityBeamReceiver();
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityBeamReceiver && ((TileEntityBeamReceiver)tile).onMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
