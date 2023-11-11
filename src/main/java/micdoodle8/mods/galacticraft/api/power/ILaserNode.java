package micdoodle8.mods.galacticraft.api.power;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.vector.*;

public interface ILaserNode extends IEnergyHandlerGC
{
    Vector3 getInputPoint();
    
    Vector3 getOutputPoint(final boolean p0);
    
    ILaserNode getTarget();
    
    TileEntity getTile();
    
    boolean canConnectTo(final ILaserNode p0);
    
    Vector3 getColor();
    
    void addNode(final ILaserNode p0);
    
    void removeNode(final ILaserNode p0);
    
    int compareTo(final ILaserNode p0, final BlockVec3 p1);
}
