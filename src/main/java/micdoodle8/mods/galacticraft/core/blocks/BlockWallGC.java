package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;

public class BlockWallGC extends BlockWall
{
    private IIcon[] wallBlockIcon;
    private IIcon[] tinSideIcon;
    
    public BlockWallGC(final String name, final Block par2Block) {
        super(par2Block);
        this.setBlockName(name);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.wallBlockIcon = new IIcon[6])[0] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_4");
        this.wallBlockIcon[1] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_2");
        this.wallBlockIcon[2] = par1IconRegister.registerIcon("galacticraftmoon:bottom");
        this.wallBlockIcon[3] = par1IconRegister.registerIcon("galacticraftmoon:brick");
        if (GalacticraftCore.isPlanetsLoaded) {
            try {
                final Class<?> c = Class.forName("micdoodle8.mods.galacticraft.planets.mars.MarsModule");
                final String texturePrefix = (String)c.getField("TEXTURE_PREFIX").get(null);
                this.wallBlockIcon[4] = par1IconRegister.registerIcon(texturePrefix + "cobblestone");
                this.wallBlockIcon[5] = par1IconRegister.registerIcon(texturePrefix + "brick");
            }
            catch (Exception e) {
                this.wallBlockIcon[4] = this.wallBlockIcon[3];
                this.wallBlockIcon[5] = this.wallBlockIcon[3];
                e.printStackTrace();
            }
        }
        else {
            this.wallBlockIcon[4] = this.wallBlockIcon[3];
            this.wallBlockIcon[5] = this.wallBlockIcon[3];
        }
        (this.tinSideIcon = new IIcon[1])[0] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_1");
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        if (meta == 1) {
            switch (side) {
                case 0: {
                    return this.wallBlockIcon[0];
                }
                case 1: {
                    return this.wallBlockIcon[1];
                }
                case 2: {
                    return this.tinSideIcon[0];
                }
                case 3: {
                    return this.tinSideIcon[0];
                }
                case 4: {
                    return this.tinSideIcon[0];
                }
                case 5: {
                    return this.tinSideIcon[0];
                }
            }
        }
        return this.wallBlockIcon[meta];
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public boolean getBlocksMovement(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        return false;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean canPlaceTorchOnTop(final World world, final int x, final int y, final int z) {
        return true;
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        final boolean flag = this.canConnectWallTo(par1IBlockAccess, par2, par3, par4 - 1);
        final boolean flag2 = this.canConnectWallTo(par1IBlockAccess, par2, par3, par4 + 1);
        final boolean flag3 = this.canConnectWallTo(par1IBlockAccess, par2 - 1, par3, par4);
        final boolean flag4 = this.canConnectWallTo(par1IBlockAccess, par2 + 1, par3, par4);
        float f = 0.25f;
        float f2 = 0.75f;
        float f3 = 0.25f;
        float f4 = 0.75f;
        float f5 = 1.0f;
        if (flag) {
            f3 = 0.0f;
        }
        if (flag2) {
            f4 = 1.0f;
        }
        if (flag3) {
            f = 0.0f;
        }
        if (flag4) {
            f2 = 1.0f;
        }
        if (flag && flag2 && !flag3 && !flag4) {
            f5 = 0.8125f;
            f = 0.3125f;
            f2 = 0.6875f;
        }
        else if (!flag && !flag2 && flag3 && flag4) {
            f5 = 0.8125f;
            f3 = 0.3125f;
            f4 = 0.6875f;
        }
        this.setBlockBounds(f, 0.0f, f3, f2, f5, f4);
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4) {
        this.setBlockBoundsBasedOnState((IBlockAccess)par1World, par2, par3, par4);
        this.maxY = 1.5;
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }
    
    public boolean canConnectWallTo(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        final Block block = par1IBlockAccess.getBlock(par2, par3, par4);
        return block == this || block == Blocks.fence_gate || (block != null && block.getMaterial().isOpaque() && block.renderAsNormalBlock() && block.getMaterial() != Material.gourd);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        if (GalacticraftCore.isPlanetsLoaded) {
            for (int var4 = 0; var4 < 6; ++var4) {
                par3List.add(new ItemStack(par1, 1, var4));
            }
        }
        else {
            for (int var4 = 0; var4 < 4; ++var4) {
                par3List.add(new ItemStack(par1, 1, var4));
            }
        }
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public int damageDropped(final int par1) {
        return par1;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        return par5 != 0 || super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
