package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import java.util.*;
import net.minecraft.tileentity.*;

public class PathfinderChecker extends Pathfinder
{
    public PathfinderChecker(final World world, final INetworkConnection targetConnector, final NetworkType networkType, final INetworkConnection... ignoreConnector) {
        super((IPathCallBack)new IPathCallBack() {
            public Set<BlockVec3> getConnectedNodes(final Pathfinder finder, final BlockVec3 currentNode) {
                final Set<BlockVec3> neighbors = new HashSet<BlockVec3>();
                for (int i = 0; i < 6; ++i) {
                    final ForgeDirection direction = ForgeDirection.getOrientation(i);
                    final BlockVec3 position = currentNode.clone().modifyPositionFromSide(direction);
                    final TileEntity connectedBlock = position.getTileEntity((IBlockAccess)world);
                    if (connectedBlock instanceof ITransmitter && !Arrays.asList(ignoreConnector).contains(connectedBlock) && ((ITransmitter)connectedBlock).canConnect(direction.getOpposite(), networkType)) {
                        neighbors.add(position);
                    }
                }
                return neighbors;
            }
            
            public boolean onSearch(final Pathfinder finder, final BlockVec3 node) {
                if (node.getTileEntity((IBlockAccess)world) == targetConnector) {
                    finder.results.add(node);
                    return true;
                }
                return false;
            }
        });
    }
}
