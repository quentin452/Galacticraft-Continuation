package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import net.minecraft.block.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;
import net.minecraft.item.*;

public class BlockMachineFrame extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon[] blockIcons;
    
    public BlockMachineFrame(final String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0f;
        this.setBlockName(assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.blockIcons = new IIcon[1])[0] = par1IconRegister.registerIcon("galacticraftasteroids:machineframe");
        this.blockIcon = this.blockIcons[0];
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        if (meta < 0 || meta >= this.blockIcons.length) {
            return this.blockIcon;
        }
        return this.blockIcons[meta];
    }
    
    public Item getItemDropped(final int meta, final Random random, final int par3) {
        return super.getItemDropped(meta, random, par3);
    }
    
    public int damageDropped(final int meta) {
        return meta;
    }
    
    public int quantityDropped(final int meta, final int fortune, final Random random) {
        return 1;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int var4 = 0; var4 < this.blockIcons.length; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }
}
