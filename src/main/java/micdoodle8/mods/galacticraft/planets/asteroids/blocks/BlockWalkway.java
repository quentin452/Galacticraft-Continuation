package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockWalkway extends BlockTransmitter implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    protected BlockWalkway(final String assetName) {
        super(Material.iron);
        this.setHardness(1.0f);
        this.setBlockTextureName("galacticraftasteroids:walkway");
        this.setBlockName(assetName);
        this.setStepSound(Block.soundTypeMetal);
        this.isBlockContainer = true;
        this.minVector = new Vector3(0.0, 0.32, 0.0);
        this.maxVector = new Vector3(1.0, 1.0, 1.0);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockAccess p_149646_1_, final int p_149646_2_, final int p_149646_3_, final int p_149646_4_, final int p_149646_5_) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public int getRenderType() {
        return GalacticraftPlanets.getBlockRenderID((Block)this);
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int onBlockPlaced(final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int meta) {
        return this.getWalkwayOrientation(world, x, y, z);
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block blockChanged) {
        world.setBlock(x, y, z, (Block)this, this.getWalkwayOrientation(world, x, y, z), 3);
        if (this.getNetworkType() != null) {
            super.onNeighborBlockChange(world, x, y, z, blockChanged);
        }
    }
    
    public int getWalkwayOrientation(final World world, final int x, final int y, final int z) {
        final int connectedNorth = (this.isBlockNormalCube(world.getBlock(x, y, z - 1)) || world.getBlock(x, y, z - 1) instanceof BlockWalkway) ? 1 : 0;
        final int connectedEast = (this.isBlockNormalCube(world.getBlock(x + 1, y, z)) || world.getBlock(x + 1, y, z) instanceof BlockWalkway) ? 2 : 0;
        final int connectedSouth = (this.isBlockNormalCube(world.getBlock(x, y, z + 1)) || world.getBlock(x, y, z + 1) instanceof BlockWalkway) ? 4 : 0;
        final int connectedWest = (this.isBlockNormalCube(world.getBlock(x - 1, y, z)) || world.getBlock(x - 1, y, z) instanceof BlockWalkway) ? 8 : 0;
        return connectedNorth | connectedEast | connectedSouth | connectedWest;
    }
    
    public boolean isBlockNormalCube(final Block block) {
        return block.getMaterial().blocksMovement() && block.renderAsNormalBlock();
    }
    
    public TileEntity createNewTileEntity(final World world, final int metadata) {
        if (this == AsteroidBlocks.blockWalkwayOxygenPipe) {
            return (TileEntity)new TileEntityOxygenPipe();
        }
        if (this == AsteroidBlocks.blockWalkwayWire) {
            return (TileEntity)new TileEntityAluminumWire(2);
        }
        return null;
    }
    
    public NetworkType getNetworkType() {
        if (this == AsteroidBlocks.blockWalkwayOxygenPipe) {
            return NetworkType.OXYGEN;
        }
        if (this == AsteroidBlocks.blockWalkwayWire) {
            return NetworkType.POWER;
        }
        return null;
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        TileEntity[] connectable = new TileEntity[6];
        if (tileEntity != null) {
            if (this.getNetworkType() != null) {
                switch (this.getNetworkType()) {
                    case OXYGEN: {
                        connectable = OxygenUtil.getAdjacentOxygenConnections(tileEntity);
                        break;
                    }
                    case POWER: {
                        connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
                        break;
                    }
                }
            }
            final float minX = 0.0f;
            float minY = 0.32f;
            final float minZ = 0.0f;
            final float maxX = 1.0f;
            final float maxY = 1.0f;
            final float maxZ = 1.0f;
            if (connectable[0] != null) {
                minY = 0.0f;
            }
            this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
    
    private void addCollisionBox(final World world, final int x, final int y, final int z, final AxisAlignedBB aabb, final List list) {
        final AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisalignedbb1 != null && aabb.intersectsWith(axisalignedbb1)) {
            list.add(axisalignedbb1);
        }
    }
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        TileEntity[] connectable = new TileEntity[6];
        if (this.getNetworkType() != null) {
            switch (this.getNetworkType()) {
                case OXYGEN: {
                    connectable = OxygenUtil.getAdjacentOxygenConnections(tileEntity);
                    break;
                }
                case POWER: {
                    connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
                    break;
                }
            }
        }
        this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
        this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        this.setBlockBounds(0.0f, 0.9f, 0.0f, 1.0f, 1.0f, 1.0f);
        this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        if (connectable[4] != null) {
            this.setBlockBounds(0.0f, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
            this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        }
        if (connectable[5] != null) {
            this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, 1.0f, (float)this.maxVector.y, (float)this.maxVector.z);
            this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        }
        if (connectable[0] != null) {
            this.setBlockBounds((float)this.minVector.x, 0.0f, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
            this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        }
        if (connectable[1] != null) {
            this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, 1.0f, (float)this.maxVector.z);
            this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        }
        if (connectable[2] != null) {
            this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, 0.0f, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
            this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        }
        if (connectable[3] != null) {
            this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, 1.0f);
            this.addCollisionBox(world, x, y, z, axisalignedbb, list);
        }
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public String getShiftDescription(final int meta) {
        if (this == AsteroidBlocks.blockWalkway) {
            return GCCoreUtil.translate("tile.walkway.description");
        }
        if (this == AsteroidBlocks.blockWalkwayWire) {
            return GCCoreUtil.translate("tile.walkwayAluminumWire.description");
        }
        if (this == AsteroidBlocks.blockWalkwayOxygenPipe) {
            return GCCoreUtil.translate("tile.walkwayOxygenPipe.description");
        }
        return "";
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
