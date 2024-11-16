package micdoodle8.mods.galacticraft.core.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;
import micdoodle8.mods.galacticraft.core.wrappers.Footprint;

public class BlockBasicMoon extends BlockAdvancedTile
    implements IDetectableResource, IPlantableBlock, ITerraformableBlock {

    // CopperMoon: 0, TinMoon: 1, CheeseStone: 2
    // Moon dirt: 3; Moon rock: 4; Moon topsoil: 5-13 (6-13 have GC2 footprints);
    // Moon dungeon brick: 14; Moon boss
    // spawner: 15;
    @SideOnly(Side.CLIENT)
    private IIcon[] moonBlockIcons;

    public BlockBasicMoon() {
        super(Material.rock);
        this.blockHardness = 1.5F;
        this.blockResistance = 2.5F;
        this.setBlockName("moonBlock");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 15) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
        }

        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 15) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
        }

        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 15) {
            return false;
        }
        return super.isNormalCube(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.moonBlockIcons = new IIcon[17];
        this.moonBlockIcons[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "top");
        this.moonBlockIcons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "brick");
        this.moonBlockIcons[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "middle");
        this.moonBlockIcons[3] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "top_side");
        this.moonBlockIcons[4] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_1");
        this.moonBlockIcons[5] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_2");
        this.moonBlockIcons[6] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_3");
        this.moonBlockIcons[7] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_4");
        this.moonBlockIcons[8] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_5");
        this.moonBlockIcons[9] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_6");
        this.moonBlockIcons[10] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_7");
        this.moonBlockIcons[11] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "grass_step_8");
        this.moonBlockIcons[12] = par1IconRegister
            .registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "moonore_copper");
        this.moonBlockIcons[13] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "moonore_tin");
        this.moonBlockIcons[14] = par1IconRegister
            .registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "moonore_cheese");
        this.moonBlockIcons[15] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "bottom");
        this.moonBlockIcons[16] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "blank");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        final int metadata = world.getBlockMetadata(x, y, z);

        switch (metadata) {
            case 15:
                return 10000.0F;
            case 14:
                return 40.0F;
            case 4:
                return 6.0F;
            default:
                if (metadata < 3) {
                    return 3.0F;
                }
                break;
        }

        return this.blockResistance / 5.0F;
    }

    @Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4) {
        final int meta = par1World.getBlockMetadata(par2, par3, par4);

        if (meta == 3 || meta >= 5 && meta <= 13) {
            return 0.5F;
        }

        if (meta == 14) {
            return 4.0F;
        }

        if (meta > 13) {
            return -1F;
        }

        if (meta < 2) {
            return 5.0F;
        }

        if (meta == 2) {
            return 3.0F;
        }

        return this.blockHardness;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (meta == 3 || meta >= 5 && meta <= 13) {
            return true;
        }

        return super.canHarvestBlock(player, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if ((meta < 5) || (meta > 13)) {
            IIcon icon;
            switch (meta) {
                case 0:
                    icon = this.moonBlockIcons[12];
                    break;
                case 1:
                    icon = this.moonBlockIcons[13];
                    break;
                case 2:
                    icon = this.moonBlockIcons[14];
                    break;
                case 3:
                    icon = this.moonBlockIcons[2];
                    break;
                case 4:
                    icon = this.moonBlockIcons[15];
                    break;
                case 14:
                    icon = this.moonBlockIcons[1];
                    break;
                case 15:
                    icon = this.moonBlockIcons[16];
                    break;
                default:
                    icon = this.moonBlockIcons[16];
                    break;
            }
            return icon;
        }

        if (side == 1) {
            switch (meta - 5) {
                case 0:
                    return this.moonBlockIcons[0];
                case 1:
                    return this.moonBlockIcons[4];
                case 2:
                    return this.moonBlockIcons[5];
                case 3:
                    return this.moonBlockIcons[6];
                case 4:
                    return this.moonBlockIcons[7];
                case 5:
                    return this.moonBlockIcons[8];
                case 6:
                    return this.moonBlockIcons[9];
                case 7:
                    return this.moonBlockIcons[10];
                case 8:
                    return this.moonBlockIcons[11];
            }
        } else if (side == 0) {
            return this.moonBlockIcons[2];
        } else {
            return this.moonBlockIcons[3];
        }

        return null;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int par3) {
        Item droppedItem;
        switch (meta) {
            case 2:
                droppedItem = GCItems.cheeseCurd;
                break;
            case 15:
                droppedItem = Item.getItemFromBlock(Blocks.air);
                break;
            default:
                droppedItem = Item.getItemFromBlock(this);
                break;
        }
        return droppedItem;
    }

    @Override
    public int damageDropped(int meta) {
        if (meta >= 5 && meta <= 13) {
            return 5;
        }
        if (meta == 2) {
            return 0;
        }
        return meta;
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return worldIn.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        switch (meta) {
            case 2:
                if (fortune >= 1) {
                    return random.nextFloat() < fortune * 0.29F - 0.25F ? 2 : 1;
                }
                return 1;
            case 15:
                return 0;
            default:
                return 1;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        int var4;

        for (var4 = 0; var4 < 6; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }

        for (var4 = 14; var4 < 15; var4++) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 15) {
            return new TileEntityDungeonSpawner();
        }

        return null;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 15;
    }

    @Override
    public boolean isValueable(int metadata) {
        switch (metadata) {
            case 0:
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction,
        IPlantable plantable) {
        final int metadata = world.getBlockMetadata(x, y, z);

        if (metadata < 5 && metadata > 13) {
            return false;
        }

        plantable.getPlant(world, x, y + 1, z);

        return plantable instanceof BlockFlower;
    }

    @Override
    public int requiredLiquidBlocksNearby() {
        return 4;
    }

    @Override
    public boolean isPlantable(int metadata) {
        return metadata >= 5 && metadata <= 13;
    }

    @Override
    public boolean isTerraformable(World world, int x, int y, int z) {
        final int meta = world.getBlockMetadata(x, y, z);

        if (meta >= 5 && meta <= 13) {
            return !world.getBlock(x, y + 1, z)
                .isOpaqueCube();
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 2) {
            return new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        }
        if (metadata == 15) {
            return null;
        }

        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
        super.breakBlock(world, x, y, z, block, par6);

        if (!world.isRemote && block == this && par6 == 5) {
            final Map<Long, List<Footprint>> footprintChunkMap = TickHandlerServer.serverFootprintMap
                .get(world.provider.dimensionId);

            if (footprintChunkMap != null) {
                final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(x >> 4, z >> 4);
                final List<Footprint> footprintList = footprintChunkMap.get(chunkKey);

                if (footprintList != null && !footprintList.isEmpty()) {
                    final List<Footprint> toRemove = new ArrayList<>();

                    for (final Footprint footprint : footprintList) {
                        if (footprint.position.x > x && footprint.position.x < x + 1
                            && footprint.position.z > z
                            && footprint.position.z < z + 1) {
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

    @Override
    public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
        if (target != Blocks.stone) {
            return false;
        }
        final int meta = world.getBlockMetadata(x, y, z);
        return meta == 3 || meta == 4;
    }
}
