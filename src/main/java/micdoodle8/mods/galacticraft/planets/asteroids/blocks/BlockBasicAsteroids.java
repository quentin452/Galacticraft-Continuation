package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.block.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.common.*;
import net.minecraft.util.*;

public class BlockBasicAsteroids extends Block implements IDetectableResource, IPlantableBlock, ITerraformableBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon[] blockIcons;
    
    public BlockBasicAsteroids(final String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0f;
        this.setBlockName(assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.blockIcons = new IIcon[6])[0] = par1IconRegister.registerIcon("galacticraftasteroids:asteroid0");
        this.blockIcons[1] = par1IconRegister.registerIcon("galacticraftasteroids:asteroid1");
        this.blockIcons[2] = par1IconRegister.registerIcon("galacticraftasteroids:asteroid2");
        this.blockIcons[3] = par1IconRegister.registerIcon("galacticraftasteroids:oreAluminum");
        this.blockIcons[4] = par1IconRegister.registerIcon("galacticraftasteroids:oreIlmenite");
        this.blockIcons[5] = par1IconRegister.registerIcon("galacticraftasteroids:oreIron");
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
    
    public ArrayList<ItemStack> getDrops(final World world, final int x, final int y, final int z, final int metadata, final int fortune) {
        switch (metadata) {
            case 4: {
                final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
                for (int count = this.quantityDropped(metadata, fortune, world.rand), i = 0; i < count; ++i) {
                    ret.add(new ItemStack(AsteroidsItems.basicItem, 1, 3));
                }
                for (int count = this.quantityDropped(metadata, fortune, world.rand), i = 0; i < count; ++i) {
                    ret.add(new ItemStack(AsteroidsItems.basicItem, 1, 4));
                }
                return ret;
            }
            default: {
                return (ArrayList<ItemStack>)super.getDrops(world, x, y, z, metadata, fortune);
            }
        }
    }
    
    public int damageDropped(final int meta) {
        switch (meta) {
            case 4: {
                return 0;
            }
            default: {
                return meta;
            }
        }
    }
    
    public int getDamageValue(final World p_149643_1_, final int p_149643_2_, final int p_149643_3_, final int p_149643_4_) {
        return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
    }
    
    public int quantityDropped(final int meta, final int fortune, final Random random) {
        switch (meta) {
            case 4: {
                if (fortune >= 1) {
                    return (random.nextFloat() < fortune * 0.29f - 0.25f) ? 2 : 1;
                }
                break;
            }
        }
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int var4 = 0; var4 < this.blockIcons.length; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }
    
    public boolean isValueable(final int metadata) {
        switch (metadata) {
            case 3:
            case 4:
            case 5: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean canSustainPlant(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection direction, final IPlantable plantable) {
        return false;
    }
    
    public int requiredLiquidBlocksNearby() {
        return 4;
    }
    
    public boolean isPlantable(final int metadata) {
        return false;
    }
    
    public boolean isTerraformable(final World world, final int x, final int y, final int z) {
        return false;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 4) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, metadata);
        }
        return super.getPickBlock(target, world, x, y, z);
    }
}
