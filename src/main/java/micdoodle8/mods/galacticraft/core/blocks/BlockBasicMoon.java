package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.common.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;

public class BlockBasicMoon extends BlockAdvancedTile implements IDetectableResource, IPlantableBlock, ITerraformableBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon[] moonBlockIcons;
    
    public BlockBasicMoon() {
        super(Material.rock);
        this.blockHardness = 1.5f;
        this.blockResistance = 2.5f;
        this.setBlockName("moonBlock");
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        if (world.getBlockMetadata(x, y, z) == 15) {
            return AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)x, (double)y, (double)z);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        if (world.getBlockMetadata(x, y, z) == 15) {
            return AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)x, (double)y, (double)z);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    
    public boolean isNormalCube(final IBlockAccess world, final int x, final int y, final int z) {
        return world.getBlockMetadata(x, y, z) != 15 && super.isNormalCube(world, x, y, z);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.moonBlockIcons = new IIcon[17])[0] = par1IconRegister.registerIcon("galacticraftmoon:top");
        this.moonBlockIcons[1] = par1IconRegister.registerIcon("galacticraftmoon:brick");
        this.moonBlockIcons[2] = par1IconRegister.registerIcon("galacticraftmoon:middle");
        this.moonBlockIcons[3] = par1IconRegister.registerIcon("galacticraftmoon:top_side");
        this.moonBlockIcons[4] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_1");
        this.moonBlockIcons[5] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_2");
        this.moonBlockIcons[6] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_3");
        this.moonBlockIcons[7] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_4");
        this.moonBlockIcons[8] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_5");
        this.moonBlockIcons[9] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_6");
        this.moonBlockIcons[10] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_7");
        this.moonBlockIcons[11] = par1IconRegister.registerIcon("galacticraftmoon:grass_step_8");
        this.moonBlockIcons[12] = par1IconRegister.registerIcon("galacticraftmoon:moonore_copper");
        this.moonBlockIcons[13] = par1IconRegister.registerIcon("galacticraftmoon:moonore_tin");
        this.moonBlockIcons[14] = par1IconRegister.registerIcon("galacticraftmoon:moonore_cheese");
        this.moonBlockIcons[15] = par1IconRegister.registerIcon("galacticraftmoon:bottom");
        this.moonBlockIcons[16] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "blank");
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public float getExplosionResistance(final Entity par1Entity, final World world, final int x, final int y, final int z, final double explosionX, final double explosionY, final double explosionZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 15) {
            return 10000.0f;
        }
        if (metadata == 14) {
            return 40.0f;
        }
        if (metadata == 4) {
            return 6.0f;
        }
        if (metadata < 3) {
            return 3.0f;
        }
        return this.blockResistance / 5.0f;
    }
    
    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        final int meta = par1World.getBlockMetadata(par2, par3, par4);
        if (meta == 3 || (meta >= 5 && meta <= 13)) {
            return 0.5f;
        }
        if (meta == 14) {
            return 4.0f;
        }
        if (meta > 13) {
            return -1.0f;
        }
        if (meta < 2) {
            return 5.0f;
        }
        if (meta == 2) {
            return 3.0f;
        }
        return this.blockHardness;
    }
    
    public boolean canHarvestBlock(final EntityPlayer player, final int meta) {
        return meta == 3 || (meta >= 5 && meta <= 13) || super.canHarvestBlock(player, meta);
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        if (meta >= 5 && meta <= 13) {
            if (side == 1) {
                switch (meta - 5) {
                    case 0: {
                        return this.moonBlockIcons[0];
                    }
                    case 1: {
                        return this.moonBlockIcons[4];
                    }
                    case 2: {
                        return this.moonBlockIcons[5];
                    }
                    case 3: {
                        return this.moonBlockIcons[6];
                    }
                    case 4: {
                        return this.moonBlockIcons[7];
                    }
                    case 5: {
                        return this.moonBlockIcons[8];
                    }
                    case 6: {
                        return this.moonBlockIcons[9];
                    }
                    case 7: {
                        return this.moonBlockIcons[10];
                    }
                    case 8: {
                        return this.moonBlockIcons[11];
                    }
                    default: {
                        return null;
                    }
                }
            }
            else {
                if (side == 0) {
                    return this.moonBlockIcons[2];
                }
                return this.moonBlockIcons[3];
            }
        }
        else {
            switch (meta) {
                case 0: {
                    return this.moonBlockIcons[12];
                }
                case 1: {
                    return this.moonBlockIcons[13];
                }
                case 2: {
                    return this.moonBlockIcons[14];
                }
                case 3: {
                    return this.moonBlockIcons[2];
                }
                case 4: {
                    return this.moonBlockIcons[15];
                }
                case 14: {
                    return this.moonBlockIcons[1];
                }
                case 15: {
                    return this.moonBlockIcons[16];
                }
                default: {
                    return this.moonBlockIcons[16];
                }
            }
        }
    }
    
    public Item getItemDropped(final int meta, final Random random, final int par3) {
        switch (meta) {
            case 2: {
                return GCItems.cheeseCurd;
            }
            case 15: {
                return Item.getItemFromBlock(Blocks.air);
            }
            default: {
                return Item.getItemFromBlock((Block)this);
            }
        }
    }
    
    public int damageDropped(final int meta) {
        if (meta >= 5 && meta <= 13) {
            return 5;
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
        switch (meta) {
            case 2: {
                if (fortune >= 1) {
                    return (random.nextFloat() < fortune * 0.29f - 0.25f) ? 2 : 1;
                }
                return 1;
            }
            case 15: {
                return 0;
            }
            default: {
                return 1;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int var4 = 0; var4 < 6; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
        for (int var4 = 14; var4 < 15; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        if (metadata == 15) {
            return new TileEntityDungeonSpawner();
        }
        return null;
    }
    
    public boolean hasTileEntity(final int metadata) {
        return metadata == 15;
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
            default: {
                return false;
            }
        }
    }
    
    public boolean canSustainPlant(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection direction, final IPlantable plantable) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata < 5 && metadata > 13) {
            return false;
        }
        plantable.getPlant(world, x, y + 1, z);
        return plantable instanceof BlockFlower;
    }
    
    public int requiredLiquidBlocksNearby() {
        return 4;
    }
    
    public boolean isPlantable(final int metadata) {
        return metadata >= 5 && metadata <= 13;
    }
    
    public boolean isTerraformable(final World world, final int x, final int y, final int z) {
        final int meta = world.getBlockMetadata(x, y, z);
        return meta >= 5 && meta <= 13 && !world.getBlock(x, y + 1, z).isOpaqueCube();
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 2) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, metadata);
        }
        if (metadata == 15) {
            return null;
        }
        return super.getPickBlock(target, world, x, y, z);
    }
    
    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int par6) {
        super.breakBlock(world, x, y, z, block, par6);
        if (!world.isRemote && block == this && par6 == 5) {
            final Map<Long, List<Footprint>> footprintChunkMap = TickHandlerServer.serverFootprintMap.get(world.provider.dimensionId);
            if (footprintChunkMap != null) {
                final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(x >> 4, z >> 4);
                final List<Footprint> footprintList = footprintChunkMap.get(chunkKey);
                if (footprintList != null && !footprintList.isEmpty()) {
                    final List<Footprint> toRemove = new ArrayList<Footprint>();
                    for (final Footprint footprint : footprintList) {
                        if (footprint.position.x > x && footprint.position.x < x + 1 && footprint.position.z > z && footprint.position.z < z + 1) {
                            toRemove.add(footprint);
                        }
                    }
                    if (!toRemove.isEmpty()) {
                        footprintList.removeAll(toRemove);
                        footprintChunkMap.put(chunkKey, footprintList);
                    }
                }
            }
            TickHandlerServer.footprintBlockChanges.add(new BlockVec3Dim(x, y, z, world.provider.dimensionId));
        }
    }
    
    public boolean isReplaceableOreGen(final World world, final int x, final int y, final int z, final Block target) {
        if (target != Blocks.stone) {
            return false;
        }
        final int meta = world.getBlockMetadata(x, y, z);
        return meta == 3 || meta == 4;
    }
}
