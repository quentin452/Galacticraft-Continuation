package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraftforge.common.util.*;

public class BlockNasaWorkbench extends BlockContainer implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc, IPartialSealableBlock
{
    IIcon[] iconBuffer;
    
    public BlockNasaWorkbench(final String assetName) {
        super(Material.iron);
        this.setBlockBounds(-0.3f, 0.0f, -0.3f, 1.3f, 0.5f, 1.3f);
        this.setHardness(2.5f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.iconBuffer = new IIcon[2])[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "workbench_nasa_side");
        this.iconBuffer[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "workbench_nasa_top");
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int i, final int j, final int k) {
        return AxisAlignedBB.getBoundingBox(i - 0.0, j + 0.0, k - 0.0, i + 1.0, j + 1.399999976158142, k + 1.0);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int i, final int j, final int k) {
        return this.getCollisionBoundingBoxFromPool(world, i, j, k);
    }
    
    public MovingObjectPosition collisionRayTrace(final World world, final int i, final int j, final int k, final Vec3 vec3d, final Vec3 vec3d1) {
        this.setBlockBounds(-0.0f, 0.0f, -0.0f, 1.0f, 1.4f, 1.0f);
        final MovingObjectPosition r = super.collisionRayTrace(world, i, j, k, vec3d, vec3d1);
        this.setBlockBounds(-0.0f, 0.0f, -0.0f, 1.0f, 1.4f, 1.0f);
        return r;
    }
    
    public void addCollisionBoxesToList(final World world, final int i, final int j, final int k, final AxisAlignedBB axisalignedbb, final List arraylist, final Entity par7Entity) {
        this.setBlockBounds(-0.0f, 0.0f, -0.0f, 1.0f, 1.4f, 1.0f);
        super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, arraylist, par7Entity);
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int x0, final int y0, final int z0) {
        return true;
    }
    
    public void onBlockPlacedBy(final World world, final int x0, final int y0, final int z0, final EntityLivingBase entity, final ItemStack stack) {
        final TileEntity tile = world.getTileEntity(x0, y0, z0);
        boolean validSpot = true;
        for (int x = -1; x < 2; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = -1; z < 2; ++z) {
                    if ((x != 0 || y != 0 || z != 0) && (Math.abs(x) != 1 || Math.abs(z) != 1)) {
                        final Block blockAt = world.getBlock(x0 + x, y0 + y, z0 + z);
                        if ((y == 0 || y == 3) && x == 0 && z == 0) {
                            if (!blockAt.getMaterial().isReplaceable()) {
                                validSpot = false;
                            }
                        }
                        else if (y != 0 && y != 3 && !blockAt.getMaterial().isReplaceable()) {
                            validSpot = false;
                        }
                    }
                }
            }
        }
        if (!validSpot) {
            world.setBlockToAir(x0, y0, z0);
            if (!world.isRemote && entity instanceof EntityPlayerMP) {
                final EntityPlayerMP player = (EntityPlayerMP)entity;
                player.addChatMessage((IChatComponent)new ChatComponentText(EnumColor.RED + GCCoreUtil.translate("gui.warning.noroom")));
                if (!player.capabilities.isCreativeMode) {
                    final ItemStack nasaWorkbench = new ItemStack((Block)this, 1, 0);
                    final EntityItem entityitem = player.dropPlayerItemWithRandomChoice(nasaWorkbench, false);
                    entityitem.delayBeforeCanPickup = 0;
                    entityitem.func_145797_a(player.getCommandSenderName());
                }
            }
            return;
        }
        if (tile instanceof IMultiBlock) {
            ((IMultiBlock)tile).onCreate(new BlockVec3(x0, y0, z0));
        }
        super.onBlockPlacedBy(world, x0, y0, z0, entity, stack);
    }
    
    public void breakBlock(final World world, final int x0, final int y0, final int z0, final Block var5, final int var6) {
        final TileEntity var7 = world.getTileEntity(x0, y0, z0);
        int fakeBlockCount = 0;
        for (int x = -1; x < 2; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = -1; z < 2; ++z) {
                    if ((x != 0 || y != 0 || z != 0) && (Math.abs(x) != 1 || Math.abs(z) != 1)) {
                        if ((y == 0 || y == 3) && x == 0 && z == 0) {
                            if (world.getBlock(x0 + x, y0 + y, z0 + z) == GCBlocks.fakeBlock) {
                                ++fakeBlockCount;
                            }
                        }
                        else if (y != 0 && y != 3 && world.getBlock(x0 + x, y0 + y, z0 + z) == GCBlocks.fakeBlock) {
                            ++fakeBlockCount;
                        }
                    }
                }
            }
        }
        if (fakeBlockCount >= 11 && var7 instanceof IMultiBlock) {
            ((IMultiBlock)var7).onDestroy(var7);
        }
        super.breakBlock(world, x0, y0, z0, var5, var6);
    }
    
    public IIcon getIcon(final int par1, final int par2) {
        return (par1 == 1) ? this.iconBuffer[1] : this.iconBuffer[0];
    }
    
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        par5EntityPlayer.openGui((Object)GalacticraftCore.instance, SchematicRegistry.getMatchingRecipeForID(0).getGuiID(), par1World, par2, par3, par4);
        return true;
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        this.setBlockBounds(-0.0f, 0.0f, -0.0f, 1.0f, 1.4f, 1.0f);
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityNasaWorkbench();
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return true;
    }
}
