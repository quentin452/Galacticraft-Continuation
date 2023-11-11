package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import java.util.*;
import net.minecraft.entity.*;

public abstract class BlockTransmitter extends BlockContainer
{
    public Vector3 minVector;
    public Vector3 maxVector;
    
    public BlockTransmitter(final Material material) {
        super(material);
        this.minVector = new Vector3(0.3, 0.3, 0.3);
        this.maxVector = new Vector3(0.7, 0.7, 0.7);
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        final TileEntity tile = world.getTileEntity(x, y, z);
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_WIRE_BOUNDS, new Object[] { x, y, z }), new NetworkRegistry.TargetPoint(world.provider.dimensionId, (double)x, (double)y, (double)z, 10.0));
        if (tile instanceof INetworkConnection) {
            ((INetworkConnection)tile).refresh();
        }
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof ITransmitter) {
            TileEntity[] connectable = new TileEntity[6];
            switch (this.getNetworkType()) {
                case OXYGEN: {
                    connectable = OxygenUtil.getAdjacentOxygenConnections(tileEntity);
                    break;
                }
                case HYDROGEN: {
                    connectable = TileEntityHydrogenPipe.getAdjacentHydrogenConnections(tileEntity);
                    break;
                }
                case POWER: {
                    connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
                    break;
                }
            }
            float minX = (float)this.minVector.x;
            float minY = (float)this.minVector.y;
            float minZ = (float)this.minVector.z;
            float maxX = (float)this.maxVector.x;
            float maxY = (float)this.maxVector.y;
            float maxZ = (float)this.maxVector.z;
            if (connectable[0] != null) {
                minY = 0.0f;
            }
            if (connectable[1] != null) {
                maxY = 1.0f;
            }
            if (connectable[2] != null) {
                minZ = 0.0f;
            }
            if (connectable[3] != null) {
                maxZ = 1.0f;
            }
            if (connectable[4] != null) {
                minX = 0.0f;
            }
            if (connectable[5] != null) {
                maxX = 1.0f;
            }
            this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
    
    public abstract NetworkType getNetworkType();
    
    public void addCollisionBoxesToList(final World world, final int x, final int y, final int z, final AxisAlignedBB axisalignedbb, final List list, final Entity entity) {
        this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof ITransmitter) {
            TileEntity[] connectable = null;
            switch (this.getNetworkType()) {
                case OXYGEN: {
                    connectable = OxygenUtil.getAdjacentOxygenConnections(tileEntity);
                    break;
                }
                case HYDROGEN: {
                    connectable = TileEntityHydrogenPipe.getAdjacentHydrogenConnections(tileEntity);
                    break;
                }
                case POWER: {
                    connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
                    break;
                }
                default: {
                    connectable = new TileEntity[6];
                    break;
                }
            }
            if (connectable[4] != null) {
                this.setBlockBounds(0.0f, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
                super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
            }
            if (connectable[5] != null) {
                this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, 1.0f, (float)this.maxVector.y, (float)this.maxVector.z);
                super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
            }
            if (connectable[0] != null) {
                this.setBlockBounds((float)this.minVector.x, 0.0f, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
                super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
            }
            if (connectable[1] != null) {
                this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, 1.0f, (float)this.maxVector.z);
                super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
            }
            if (connectable[2] != null) {
                this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, 0.0f, (float)this.maxVector.x, (float)this.maxVector.y, (float)this.maxVector.z);
                super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
            }
            if (connectable[3] != null) {
                this.setBlockBounds((float)this.minVector.x, (float)this.minVector.y, (float)this.minVector.z, (float)this.maxVector.x, (float)this.maxVector.y, 1.0f);
                super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
            }
        }
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
}
