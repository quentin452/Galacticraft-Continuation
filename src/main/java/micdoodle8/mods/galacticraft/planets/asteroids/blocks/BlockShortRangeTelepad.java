package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.planets.*;

public class BlockShortRangeTelepad extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    protected BlockShortRangeTelepad(final String assetName) {
        super(Material.iron);
        this.blockHardness = 3.0f;
        this.setBlockName(assetName);
        this.setBlockTextureName("stone");
        this.setStepSound(Block.soundTypeMetal);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public int getRenderType() {
        return -1;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return (TileEntity)new TileEntityShortRangeTelepad();
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.45f, 1.0f);
    }
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.45f, 1.0f);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
    }
    
    public void onBlockPlacedBy(final World world, final int x0, final int y0, final int z0, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        super.onBlockPlacedBy(world, x0, y0, z0, entityLiving, itemStack);
        final TileEntity tile = world.getTileEntity(x0, y0, z0);
        boolean validSpot = true;
        for (int x = -1; x <= 1; ++x) {
            for (int y = 0; y < 3; y += 2) {
                for (int z = -1; z <= 1; ++z) {
                    if (x != 0 || y != 0 || z != 0) {
                        final Block blockAt = world.getBlock(x0 + x, y0 + y, z0 + z);
                        if (!blockAt.getMaterial().isReplaceable()) {
                            validSpot = false;
                        }
                    }
                }
            }
        }
        if (!validSpot) {
            world.setBlockToAir(x0, y0, z0);
            if (entityLiving instanceof EntityPlayer) {
                if (!world.isRemote) {
                    ((EntityPlayer)entityLiving).addChatMessage((IChatComponent)new ChatComponentText(EnumColor.RED + GCCoreUtil.translate("gui.warning.noroom")));
                }
                ((EntityPlayer)entityLiving).inventory.addItemStackToInventory(new ItemStack(Item.getItemFromBlock((Block)this), 1, 0));
            }
            return;
        }
        if (tile instanceof TileEntityShortRangeTelepad) {
            ((TileEntityShortRangeTelepad)tile).onCreate(new BlockVec3(x0, y0, z0));
            ((TileEntityShortRangeTelepad)tile).setOwner(((EntityPlayer)entityLiving).getGameProfile().getName());
        }
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer par5EntityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        return ((IMultiBlock)world.getTileEntity(x, y, z)).onActivated(par5EntityPlayer);
    }
    
    public void breakBlock(final World world, final int x0, final int y0, final int z0, final Block var5, final int var6) {
        final TileEntity tileAt = world.getTileEntity(x0, y0, z0);
        int fakeBlockCount = 0;
        for (int x = -1; x <= 1; ++x) {
            for (int y = 0; y < 3; y += 2) {
                for (int z = -1; z <= 1; ++z) {
                    if ((x != 0 || y != 0 || z != 0) && world.getBlock(x0 + x, y0 + y, z0 + z) == AsteroidBlocks.fakeTelepad) {
                        ++fakeBlockCount;
                    }
                }
            }
        }
        if (tileAt instanceof TileEntityShortRangeTelepad) {
            if (fakeBlockCount > 0) {
                ((TileEntityShortRangeTelepad)tileAt).onDestroy(tileAt);
            }
            ShortRangeTelepadHandler.removeShortRangeTeleporter((TileEntityShortRangeTelepad)tileAt);
        }
        super.breakBlock(world, x0, y0, z0, var5, var6);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
        final TileEntity tileAt = world.getTileEntity(x, y, z);
        if (tileAt instanceof TileEntityShortRangeTelepad) {
            final TileEntityShortRangeTelepad telepad = (TileEntityShortRangeTelepad)tileAt;
            final float teleportTimeScaled = Math.min(1.0f, telepad.teleportTime / 150.0f);
            for (int i = 0; i < 6; ++i) {
                for (int j = 0; j < 4; ++j) {
                    final float f = rand.nextFloat() * 0.6f + 0.4f;
                    final float r = f * 0.3f;
                    final float g = f * (0.3f + teleportTimeScaled * 0.7f);
                    final float b = f * (1.0f - teleportTimeScaled * 0.7f);
                    GalacticraftPlanets.spawnParticle("portalBlue", new Vector3(x + 0.2 + rand.nextDouble() * 0.6, y + 0.1, z + 0.2 + rand.nextDouble() * 0.6), new Vector3(0.0, 1.4, 0.0), telepad, false);
                }
                final float f = rand.nextFloat() * 0.6f + 0.4f;
                final float r = f * 0.3f;
                final float g = f * (0.3f + teleportTimeScaled * 0.7f);
                final float b = f * (1.0f - teleportTimeScaled * 0.7f);
                GalacticraftPlanets.spawnParticle("portalBlue", new Vector3(x + 0.0 + rand.nextDouble() * 0.2, y + 2.9, z + rand.nextDouble()), new Vector3(0.0, -2.95, 0.0), telepad, true);
                GalacticraftPlanets.spawnParticle("portalBlue", new Vector3(x + 0.8 + rand.nextDouble() * 0.2, y + 2.9, z + rand.nextDouble()), new Vector3(0.0, -2.95, 0.0), telepad, true);
                GalacticraftPlanets.spawnParticle("portalBlue", new Vector3(x + rand.nextDouble(), y + 2.9, z + 0.2 + rand.nextDouble() * 0.2), new Vector3(0.0, -2.95, 0.0), telepad, true);
                GalacticraftPlanets.spawnParticle("portalBlue", new Vector3(x + rand.nextDouble(), y + 2.9, z + 0.8 + rand.nextDouble() * 0.2), new Vector3(0.0, -2.95, 0.0), telepad, true);
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
