package micdoodle8.mods.galacticraft.core.world;

import net.minecraftforge.common.*;
import net.minecraft.world.*;
import net.minecraft.util.*;

public interface IChunkLoader
{
    void onTicketLoaded(final ForgeChunkManager.Ticket p0, final boolean p1);
    
    ForgeChunkManager.Ticket getTicket();
    
    World getWorldObj();
    
    ChunkCoordinates getCoords();
    
    String getOwnerName();
    
    void setOwnerName(final String p0);
}
