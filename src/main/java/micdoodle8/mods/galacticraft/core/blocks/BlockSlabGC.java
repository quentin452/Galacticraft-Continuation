package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class BlockSlabGC extends BlockSlab
{
    private static final String[] woodTypes;
    private IIcon[] textures;
    private IIcon[] tinSideIcon;
    private final boolean isDoubleSlab;
    
    public BlockSlabGC(final String name, final boolean par2, final Material material) {
        super(par2, material);
        this.isDoubleSlab = par2;
        this.setBlockName(name);
        this.useNeighborBrightness = true;
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.textures = new IIcon[6])[0] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_4");
        this.textures[1] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_2");
        this.textures[2] = par1IconRegister.registerIcon("galacticraftmoon:bottom");
        this.textures[3] = par1IconRegister.registerIcon("galacticraftmoon:brick");
        if (GalacticraftCore.isPlanetsLoaded) {
            this.textures[4] = par1IconRegister.registerIcon("galacticraftmars:cobblestone");
            this.textures[5] = par1IconRegister.registerIcon("galacticraftmars:brick");
        }
        else {
            this.textures[4] = this.textures[3];
            this.textures[5] = this.textures[3];
        }
        (this.tinSideIcon = new IIcon[1])[0] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_1");
    }
    
    public IIcon getIcon(final int side, final int meta) {
        if (meta == 1 || meta == 9) {
            switch (side) {
                case 0: {
                    return this.textures[0];
                }
                case 1: {
                    return this.textures[1];
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
        return this.textures[getTypeFromMeta(meta)];
    }
    
    public void getSubBlocks(final Item block, final CreativeTabs creativeTabs, final List list) {
        int max = 0;
        if (GalacticraftCore.isPlanetsLoaded) {
            max = 6;
        }
        else {
            max = 4;
        }
        for (int i = 0; i < max; ++i) {
            list.add(new ItemStack(block, 1, i));
        }
    }
    
    public String func_150002_b(final int meta) {
        return BlockSlabGC.woodTypes[this.getWoodType(meta)] + "Slab";
    }
    
    public int damageDropped(final int meta) {
        return meta & 0x7;
    }
    
    public Item getItemDropped(final int meta, final Random par2Random, final int par3) {
        if (this.isDoubleSlab && this == GCBlocks.slabGCDouble) {
            return Item.getItemFromBlock(GCBlocks.slabGCHalf);
        }
        return Item.getItemFromBlock((Block)this);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        if (!this.isDoubleSlab) {
            return GalacticraftCore.galacticraftBlocksTab;
        }
        return null;
    }
    
    public float getBlockHardness(final World world, final int x, final int y, final int z) {
        final int meta = world.getBlockMetadata(x, y, z);
        float hardness = this.blockHardness;
        switch (getTypeFromMeta(meta)) {
            case 2:
            case 3: {
                hardness = 1.5f;
                break;
            }
            default: {
                hardness = 2.0f;
                break;
            }
        }
        return hardness;
    }
    
    public float getExplosionResistance(final Entity par1Entity, final World world, final int x, final int y, final int z, final double explosionX, final double explosionY, final double explosionZ) {
        return super.getBlockHardness(world, x, y, z);
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        if (this == GCBlocks.slabGCDouble) {
            return new ItemStack(GCBlocks.slabGCHalf, 1, world.getBlockMetadata(x, y, z));
        }
        return new ItemStack(GCBlocks.slabGCHalf, 1, world.getBlockMetadata(x, y, z) & 0x7);
    }
    
    protected ItemStack createStackedBlock(final int par1) {
        return new ItemStack((Block)this, 2, par1);
    }
    
    private int getWoodType(int meta) {
        meta = getTypeFromMeta(meta);
        if (meta < BlockSlabGC.woodTypes.length) {
            return meta;
        }
        return 0;
    }
    
    private static int getTypeFromMeta(final int meta) {
        return Math.min(5, meta & 0x7);
    }
    
    static {
        woodTypes = new String[] { "tin", "tin", "moon", "moonBricks", "mars", "marsBricks" };
    }
}
