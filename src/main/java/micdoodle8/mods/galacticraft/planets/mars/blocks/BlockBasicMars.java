package micdoodle8.mods.galacticraft.planets.mars.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.block.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.util.*;

public class BlockBasicMars extends Block implements IDetectableResource, IPlantableBlock, ITileEntityProvider, ITerraformableBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon[] marsBlockIcons;
    
    public MapColor getMapColor(final int meta) {
        switch (meta) {
            case 7: {
                return MapColor.greenColor;
            }
            case 5: {
                return MapColor.dirtColor;
            }
            default: {
                return MapColor.redColor;
            }
        }
    }
    
    public BlockBasicMars() {
        super(Material.rock);
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        if (world.getBlockMetadata(x, y, z) == 10) {
            return null;
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        if (world.getBlockMetadata(x, y, z) == 10) {
            return AxisAlignedBB.getBoundingBox(x + 0.0, y + 0.0, z + 0.0, x + 0.0, y + 0.0, z + 0.0);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    
    public float getExplosionResistance(final Entity par1Entity, final World world, final int x, final int y, final int z, final double explosionX, final double explosionY, final double explosionZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 10) {
            return 10000.0f;
        }
        if (metadata == 7) {
            return 40.0f;
        }
        return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.marsBlockIcons = new IIcon[11])[0] = par1IconRegister.registerIcon("galacticraftmars:cobblestone");
        this.marsBlockIcons[1] = par1IconRegister.registerIcon("galacticraftmars:decoration_desh");
        this.marsBlockIcons[2] = par1IconRegister.registerIcon("galacticraftmars:middle");
        this.marsBlockIcons[3] = par1IconRegister.registerIcon("galacticraftmars:brick");
        this.marsBlockIcons[4] = par1IconRegister.registerIcon("galacticraftmars:top");
        this.marsBlockIcons[5] = par1IconRegister.registerIcon("galacticraftmars:copper");
        this.marsBlockIcons[6] = par1IconRegister.registerIcon("galacticraftmars:desh");
        this.marsBlockIcons[7] = par1IconRegister.registerIcon("galacticraftmars:tin");
        this.marsBlockIcons[8] = par1IconRegister.registerIcon("galacticraftmars:bottom");
        this.marsBlockIcons[9] = par1IconRegister.registerIcon("galacticraftmars:iron");
        this.marsBlockIcons[10] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "blank");
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        final int meta = par1World.getBlockMetadata(par2, par3, par4);
        if (meta == 7) {
            return 4.0f;
        }
        if (meta == 10) {
            return -1.0f;
        }
        return this.blockHardness;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata == 10) {
            return (TileEntity)new TileEntityDungeonSpawnerMars();
        }
        return null;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return null;
    }
    
    public boolean canHarvestBlock(final EntityPlayer player, final int meta) {
        return meta != 10 && super.canHarvestBlock(player, meta);
    }
    
    public IIcon getIcon(final int side, final int meta) {
        switch (meta) {
            case 0: {
                return this.marsBlockIcons[5];
            }
            case 1: {
                return this.marsBlockIcons[7];
            }
            case 2: {
                return this.marsBlockIcons[6];
            }
            case 3: {
                return this.marsBlockIcons[9];
            }
            case 4: {
                return this.marsBlockIcons[0];
            }
            case 5: {
                return this.marsBlockIcons[4];
            }
            case 6: {
                return this.marsBlockIcons[2];
            }
            case 7: {
                return this.marsBlockIcons[3];
            }
            case 8: {
                return this.marsBlockIcons[1];
            }
            case 9: {
                return this.marsBlockIcons[8];
            }
            case 10: {
                return this.marsBlockIcons[10];
            }
            default: {
                return this.marsBlockIcons[1];
            }
        }
    }
    
    public Item getItemDropped(final int meta, final Random random, final int par3) {
        if (meta == 2) {
            return MarsItems.marsItemBasic;
        }
        if (meta == 10) {
            return Item.getItemFromBlock(Blocks.air);
        }
        return Item.getItemFromBlock((Block)this);
    }
    
    public int damageDropped(final int meta) {
        if (meta == 9) {
            return 4;
        }
        if (meta == 2) {
            return 0;
        }
        return meta;
    }
    
    public int getDamageValue(final World p_149643_1_, final int p_149643_2_, final int p_149643_3_, final int p_149643_4_) {
        return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
    }
    
    public int quantityDropped(final int meta, final int fortune, final Random random) {
        if (meta == 10) {
            return 0;
        }
        if (meta == 2 && fortune >= 1) {
            return (random.nextFloat() < fortune * 0.29f - 0.25f) ? 2 : 1;
        }
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int var4 = 0; var4 < 11; ++var4) {
            if (var4 != 10) {
                par3List.add(new ItemStack(par1, 1, var4));
            }
        }
    }
    
    public boolean isValueable(final int metadata) {
        switch (metadata) {
            case 0: {
                return true;
            }
            case 1: {
                return true;
            }
            case 2: {
                return true;
            }
            case 3: {
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
    
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
        if (rand.nextInt(10) == 0) {
            final int metadata = world.getBlockMetadata(x, y, z);
            if (metadata == 7) {
                GalacticraftPlanets.spawnParticle("sludgeDrip", new Vector3(x + rand.nextDouble(), (double)y, z + rand.nextDouble()), new Vector3(0.0, 0.0, 0.0), new Object[0]);
                if (rand.nextInt(100) == 0) {
                    world.playSound((double)x, (double)y, (double)z, GalacticraftCore.TEXTURE_PREFIX + "ambience.singledrip", 1.0f, 0.8f + rand.nextFloat() / 5.0f, false);
                }
            }
        }
    }
    
    public boolean isTerraformable(final World world, final int x, final int y, final int z) {
        return world.getBlockMetadata(x, y, z) == 5 && !world.getBlock(x, y + 1, z).isOpaqueCube();
    }
    
    public boolean canSilkHarvest(final World world, final EntityPlayer player, final int x, final int y, final int z, final int metadata) {
        return metadata < 10;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 2) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, metadata);
        }
        if (metadata == 9) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, metadata);
        }
        if (metadata == 10) {
            return null;
        }
        return super.getPickBlock(target, world, x, y, z);
    }
    
    public boolean isReplaceableOreGen(final World world, final int x, final int y, final int z, final Block target) {
        if (target != Blocks.stone) {
            return false;
        }
        final int meta = world.getBlockMetadata(x, y, z);
        return meta == 6 || meta == 9;
    }
    
    public boolean hasTileEntity(final int metadata) {
        return metadata == 10;
    }
}
