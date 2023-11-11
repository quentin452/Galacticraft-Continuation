package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;

public class Pathfinder
{
    public IPathCallBack callBackCheck;
    public Set<BlockVec3> closedSet;
    public Set<BlockVec3> results;
    
    public Pathfinder(final IPathCallBack callBack) {
        this.callBackCheck = callBack;
        this.reset();
    }
    
    public boolean findNodes(final BlockVec3 currentNode) {
        this.closedSet.add(currentNode);
        if (this.callBackCheck.onSearch(this, currentNode)) {
            return false;
        }
        for (final BlockVec3 node : this.callBackCheck.getConnectedNodes(this, currentNode)) {
            if (!this.closedSet.contains(node) && this.findNodes(node)) {
                return true;
            }
        }
        return false;
    }
    
    public Pathfinder init(final BlockVec3 startNode) {
        this.findNodes(startNode);
        return this;
    }
    
    public Pathfinder reset() {
        this.closedSet = new HashSet<BlockVec3>();
        this.results = new HashSet<BlockVec3>();
        return this;
    }
}
