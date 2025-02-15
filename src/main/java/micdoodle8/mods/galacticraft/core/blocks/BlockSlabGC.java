package micdoodle8.mods.galacticraft.core.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;

public class BlockSlabGC extends BlockSlab {

    private static final String[] woodTypes = { "tin", "tin", "moon", "moonBricks", "mars", "marsBricks" };

    private IIcon[] textures;
    private IIcon[] tinSideIcon;
    private final boolean isDoubleSlab;

    public BlockSlabGC(String name, boolean par2, Material material) {
        super(par2, material);
        this.isDoubleSlab = par2;
        this.setBlockName(name);
        this.useNeighborBrightness = true;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.textures = new IIcon[6];
        this.textures[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4");
        this.textures[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_2");
        this.textures[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "bottom");
        this.textures[3] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "brick");

        if (GalacticraftCore.isPlanetsLoaded) {
            try {
                final String prefix = (String) Class.forName("micdoodle8.mods.galacticraft.planets.mars.MarsModule")
                    .getField("TEXTURE_PREFIX")
                    .get(null);
                this.textures[4] = par1IconRegister.registerIcon(prefix + "cobblestone");
                this.textures[5] = par1IconRegister.registerIcon(prefix + "brick");
            } catch (final Exception e) {
                e.printStackTrace();
                this.textures[4] = this.textures[3];
                this.textures[5] = this.textures[3];
            }
        } else {
            this.textures[4] = this.textures[3];
            this.textures[5] = this.textures[3];
        }

        this.tinSideIcon = new IIcon[1];
        this.tinSideIcon[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_1");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 1 || meta == 9) {
            switch (side) {
                case 0:
                    return this.textures[0]; // BOTTOM
                case 1:
                    return this.textures[1]; // TOP
                case 2:
                    return this.tinSideIcon[0]; // Z-
                case 3:
                    return this.tinSideIcon[0]; // Z+
                case 4:
                    return this.tinSideIcon[0]; // X-
                case 5:
                    return this.tinSideIcon[0]; // X+
            }
        }
        return this.textures[getTypeFromMeta(meta)];
    }

    @Override
    public void getSubBlocks(Item block, CreativeTabs creativeTabs, List<ItemStack> list) {
        int max = 0;

        if (GalacticraftCore.isPlanetsLoaded) {
            max = 6; // Number of slab types with Planets loaded
        } else {
            max = 4; // Number of slab types with Planets not loaded
        }
        for (int i = 0; i < max; ++i) {
            list.add(new ItemStack(block, 1, i));
        }
    }

    @Override
    public String func_150002_b(int meta) {
        return new StringBuilder().append(woodTypes[this.getWoodType(meta)])
            .append("Slab")
            .toString();
    }

    @Override
    public int damageDropped(int meta) {
        return meta & 7;
    }

    @Override
    public Item getItemDropped(int meta, Random par2Random, int par3) {
        if (this.isDoubleSlab && this == GCBlocks.slabGCDouble) {
            return Item.getItemFromBlock(GCBlocks.slabGCHalf);
        }
        return Item.getItemFromBlock(this);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        if (!this.isDoubleSlab) {
            return GalacticraftCore.galacticraftBlocksTab;
        }
        return null;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        final int meta = world.getBlockMetadata(x, y, z);
        int type = getTypeFromMeta(meta);

        float hardness;
        if (type == 2 || type == 3) {
            hardness = 1.5F;
        } else {
            hardness = 2.0F;
        }

        return hardness;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        if (this == GCBlocks.slabGCDouble) {
            return new ItemStack(GCBlocks.slabGCHalf, 1, world.getBlockMetadata(x, y, z));
        }
        return new ItemStack(GCBlocks.slabGCHalf, 1, world.getBlockMetadata(x, y, z) & 7);
    }

    @Override
    protected ItemStack createStackedBlock(int par1) {
        return new ItemStack(this, 2, par1);
    }

    private int getWoodType(int meta) {
        meta = getTypeFromMeta(meta);

        if (meta < woodTypes.length) {
            return meta;
        }
        return 0;
    }

    private static int getTypeFromMeta(int meta) {
        return Math.min(5, meta & 7);
    }
}
