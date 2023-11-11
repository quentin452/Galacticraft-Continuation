package micdoodle8.mods.galacticraft.core.energy.grid;

import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import java.util.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

public class NetworkFinder
{
    public World worldObj;
    public BlockVec3 start;
    private int theDim;
    private BlockVec3 toIgnore;
    private Set<BlockVec3> iterated;
    public List<IConductor> found;
    
    public NetworkFinder(final World world, final BlockVec3 location, final BlockVec3 ignore) {
        this.iterated = new HashSet<BlockVec3>();
        this.found = new LinkedList<IConductor>();
        this.worldObj = world;
        this.start = location;
        this.toIgnore = ignore;
    }
    
    private void loopAll(final int x, final int y, final int z, final int dirIn) {
        BlockVec3 obj = null;
        for (int dir = 0; dir < 6; ++dir) {
            if (dir != dirIn) {
                switch (dir) {
                    case 0: {
                        obj = new BlockVec3(x, y - 1, z);
                        break;
                    }
                    case 1: {
                        obj = new BlockVec3(x, y + 1, z);
                        break;
                    }
                    case 2: {
                        obj = new BlockVec3(x, y, z - 1);
                        break;
                    }
                    case 3: {
                        obj = new BlockVec3(x, y, z + 1);
                        break;
                    }
                    case 4: {
                        obj = new BlockVec3(x - 1, y, z);
                        break;
                    }
                    case 5: {
                        obj = new BlockVec3(x + 1, y, z);
                        break;
                    }
                }
                if (!this.iterated.contains(obj)) {
                    this.iterated.add(obj);
                    final TileEntity tileEntity = this.worldObj.getTileEntity(obj.x, obj.y, obj.z);
                    if (tileEntity instanceof IConductor) {
                        this.found.add((IConductor)tileEntity);
                        this.loopAll(obj.x, obj.y, obj.z, dir ^ 0x1);
                    }
                }
            }
        }
    }
    
    public List<IConductor> exploreNetwork() {
        if (this.start.getTileEntity((IBlockAccess)this.worldObj) instanceof IConductor) {
            this.iterated.add(this.start);
            this.iterated.add(this.toIgnore);
            this.found.add((IConductor)this.start.getTileEntity((IBlockAccess)this.worldObj));
            this.loopAll(this.start.x, this.start.y, this.start.z, 6);
        }
        return this.found;
    }
}
