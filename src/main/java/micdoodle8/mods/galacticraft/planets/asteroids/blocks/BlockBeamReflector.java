package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockBeamReflector extends BlockTileGC implements ItemBlockDesc.IBlockShiftDesc
{
    public BlockBeamReflector(final String assetName) {
        super(Material.iron);
        this.setBlockName(assetName);
        this.setBlockTextureName("stone");
        this.setStepSound(Block.soundTypeMetal);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
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
        return metadata;
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        this.setBlockBounds(0.25f, 0.0f, 0.25f, 0.75f, 0.8f, 0.75f);
    }
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return (TileEntity)new TileEntityBeamReflector();
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityBeamReflector && ((TileEntityBeamReflector)tile).onMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
