package micdoodle8.mods.galacticraft.core.event;

import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.world.*;

public class EventLandingPadRemoval extends Event
{
    public boolean allow;
    public final int x;
    public final int y;
    public final int z;
    public final World world;
    
    public EventLandingPadRemoval(final World world, final int x, final int y, final int z) {
        this.allow = true;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
