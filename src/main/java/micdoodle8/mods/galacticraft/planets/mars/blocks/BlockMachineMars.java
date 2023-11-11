package micdoodle8.mods.galacticraft.planets.mars.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.creativetab.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.world.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.planets.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.util.*;

public class BlockMachineMars extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc, IPartialSealableBlock
{
    public static final int TERRAFORMER_METADATA = 0;
    public static final int CRYOGENIC_CHAMBER_METADATA = 4;
    public static final int LAUNCH_CONTROLLER_METADATA = 8;
    private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconTerraformer;
    private IIcon iconLaunchController;
    private IIcon iconCryochamber;
    
    public BlockMachineMars() {
        super(GCBlocks.machine);
        this.setStepSound(BlockMachineMars.soundTypeMetal);
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        this.iconTerraformer = par1IconRegister.registerIcon("galacticraftmars:terraformer_0");
        this.iconLaunchController = par1IconRegister.registerIcon("galacticraftmars:launchController");
        this.iconCryochamber = par1IconRegister.registerIcon("galacticraftmars:cryoDummy");
    }
    
    public void breakBlock(final World var1, final int var2, final int var3, final int var4, final Block var5, final int var6) {
        final TileEntity var7 = var1.getTileEntity(var2, var3, var4);
        if (var7 instanceof IMultiBlock) {
            ((IMultiBlock)var7).onDestroy(var7);
        }
        super.breakBlock(var1, var2, var3, var4, var5, var6);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public IIcon getIcon(final int side, int metadata) {
        if (side == 0 || side == 1) {
            return this.blockIcon;
        }
        if (metadata >= 8) {
            metadata -= 8;
            if (side == metadata + 2) {
                return this.iconInput;
            }
            if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
                return this.iconMachineSide;
            }
            return this.iconLaunchController;
        }
        else {
            if (metadata >= 4) {
                return this.iconCryochamber;
            }
            if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
                return this.iconMachineSide;
            }
            if (side == ForgeDirection.getOrientation(metadata + 2).ordinal()) {
                return this.iconInput;
            }
            return this.iconTerraformer;
        }
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        int change = 0;
        switch (angle) {
            case 0: {
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
            case 3: {
                change = 3;
                break;
            }
        }
        if (metadata >= 8) {
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
            world.setBlockMetadataWithNotify(x, y, z, 8 + change, 3);
        }
        else if (metadata >= 4) {
            if (!this.canPlaceChamberAt(world, x, y, z, entityLiving)) {
                if (entityLiving instanceof EntityPlayer) {
                    if (!world.isRemote) {
                        ((EntityPlayer)entityLiving).addChatMessage((IChatComponent)new ChatComponentText(EnumColor.RED + GCCoreUtil.translate("gui.warning.noroom")));
                    }
                    world.setBlockToAir(x, y, z);
                    ((EntityPlayer)entityLiving).inventory.addItemStackToInventory(new ItemStack(Item.getItemFromBlock(MarsBlocks.machine), 1, 4));
                    return;
                }
            }
            else {
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
                world.setBlockMetadataWithNotify(x, y, z, 4 + change, 3);
            }
        }
        else {
            world.setBlockMetadataWithNotify(x, y, z, 0 + change, 3);
        }
        final TileEntity var8 = world.getTileEntity(x, y, z);
        if (var8 instanceof IMultiBlock) {
            ((IMultiBlock)var8).onCreate(new BlockVec3(x, y, z));
        }
        if (metadata >= 8) {
            for (int dX = -2; dX < 3; ++dX) {
                for (int dZ = -2; dZ < 3; ++dZ) {
                    final Block id = world.getBlock(x + dX, y, z + dZ);
                    if (id == GCBlocks.landingPadFull) {
                        world.markBlockForUpdate(x + dX, y, z + dZ);
                    }
                }
            }
        }
        if (var8 instanceof IChunkLoader && !var8.getWorldObj().isRemote && ConfigManagerMars.launchControllerChunkLoad && entityLiving instanceof EntityPlayer) {
            ((IChunkLoader)var8).setOwnerName(((EntityPlayer)entityLiving).getGameProfile().getName());
            ((IChunkLoader)var8).onTicketLoaded(ForgeChunkManager.requestTicket((Object)GalacticraftCore.instance, var8.getWorldObj(), ForgeChunkManager.Type.NORMAL), true);
        }
        else if (var8 instanceof TileEntityLaunchController && entityLiving instanceof EntityPlayer) {
            ((TileEntityLaunchController)var8).setOwnerName(((EntityPlayer)entityLiving).getGameProfile().getName());
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
            return false;
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
        if (metadata >= 8 || metadata < 4) {
            final TileEntity te = par1World.getTileEntity(x, y, z);
            if (te instanceof TileBaseUniversalElectrical) {
                ((TileBaseUniversalElectrical)te).updateFacing();
            }
        }
        par1World.setBlockMetadataWithNotify(x, y, z, change, 3);
        return true;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata >= 8) {
            par5EntityPlayer.openGui((Object)GalacticraftPlanets.instance, 2, world, x, y, z);
            return true;
        }
        if (metadata >= 4) {
            ((IMultiBlock)world.getTileEntity(x, y, z)).onActivated(par5EntityPlayer);
            return true;
        }
        par5EntityPlayer.openGui((Object)GalacticraftPlanets.instance, 2, world, x, y, z);
        return true;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata >= 8) {
            return (TileEntity)new TileEntityLaunchController();
        }
        if (metadata >= 4) {
            return (TileEntity)new TileEntityCryogenicChamber();
        }
        return (TileEntity)new TileEntityTerraformer();
    }
    
    public void onBlockDestroyedByPlayer(final World world, final int x, final int y, final int z, final int par5) {
        super.onBlockDestroyedByPlayer(world, x, y, z, par5);
        if (world.getBlockMetadata(x, y, z) >= 8) {
            for (int dX = -2; dX < 3; ++dX) {
                for (int dZ = -2; dZ < 3; ++dZ) {
                    final Block id = world.getBlock(x + dX, y, z + dZ);
                    if (id == GCBlocks.landingPadFull) {
                        world.markBlockForUpdate(x + dX, y, z + dZ);
                    }
                }
            }
        }
    }
    
    public ItemStack getTerraformer() {
        return new ItemStack((Block)this, 1, 0);
    }
    
    public ItemStack getChamber() {
        return new ItemStack((Block)this, 1, 4);
    }
    
    public ItemStack getLaunchController() {
        return new ItemStack((Block)this, 1, 8);
    }
    
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(this.getTerraformer());
        par3List.add(this.getChamber());
        par3List.add(this.getLaunchController());
    }
    
    private boolean canPlaceChamberAt(final World world, final int x0, final int y0, final int z0, final EntityLivingBase player) {
        for (int y = 0; y < 3; ++y) {
            final Block blockAt = world.getBlock(x0, y0 + y, z0);
            final int metaAt = world.getBlockMetadata(x0, y0 + y, z0);
            if (y != 0 || blockAt != MarsBlocks.machine || metaAt < 4 || metaAt >= 8) {
                if (!blockAt.getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int damageDropped(final int metadata) {
        if (metadata >= 8) {
            return 8;
        }
        if (metadata >= 4) {
            return 4;
        }
        return 0;
    }
    
    public int getRenderType() {
        return GalacticraftPlanets.getBlockRenderID((Block)this);
    }
    
    public boolean isBed(final IBlockAccess world, final int x, final int y, final int z, final EntityLivingBase player) {
        return world.getBlockMetadata(x, y, z) >= 4;
    }
    
    public ChunkCoordinates getBedSpawnPosition(final IBlockAccess world, final int x, final int y, final int z, final EntityPlayer player) {
        return new ChunkCoordinates(x, y + 1, z);
    }
    
    public void setBedOccupied(final IBlockAccess world, final int x, final int y, final int z, final EntityPlayer player, final boolean occupied) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCryogenicChamber) {
            ((TileEntityCryogenicChamber)tile).isOccupied = true;
        }
    }
    
    public static ChunkCoordinates getNearestEmptyChunkCoordinates(final World par0World, final int par1, final int par2, final int par3, int par4) {
        for (int k1 = 0; k1 <= 1; ++k1) {
            final int l1 = par1 - 1;
            final int i2 = par3 - 1;
            final int j2 = l1 + 2;
            final int k2 = i2 + 2;
            for (int l2 = l1; l2 <= j2; ++l2) {
                for (int i3 = i2; i3 <= k2; ++i3) {
                    if (World.doesBlockHaveSolidTopSurface((IBlockAccess)par0World, l2, par2 - 1, i3) && !par0World.getBlock(l2, par2, i3).getMaterial().isOpaque() && !par0World.getBlock(l2, par2 + 1, i3).getMaterial().isOpaque()) {
                        if (par4 <= 0) {
                            return new ChunkCoordinates(l2, par2, i3);
                        }
                        --par4;
                    }
                }
            }
        }
        return null;
    }
    
    public int getBedDirection(final IBlockAccess world, final int x, final int y, final int z) {
        return 0;
    }
    
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 4: {
                return GCCoreUtil.translate("tile.cryoChamber.description");
            }
            case 8: {
                return GCCoreUtil.translate("tile.launchController.description");
            }
            case 0: {
                return GCCoreUtil.translate("tile.terraformer.description");
            }
            default: {
                return "";
            }
        }
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        return true;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        final int meta = world.getBlockMetadata(x, y, z) & 0xC;
        return meta != 4;
    }
}
