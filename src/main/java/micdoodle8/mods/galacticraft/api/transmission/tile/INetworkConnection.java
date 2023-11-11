package micdoodle8.mods.galacticraft.api.transmission.tile;

import net.minecraft.tileentity.*;

public interface INetworkConnection extends IConnector
{
    TileEntity[] getAdjacentConnections();
    
    void refresh();
    
    void onNetworkChanged();
}
