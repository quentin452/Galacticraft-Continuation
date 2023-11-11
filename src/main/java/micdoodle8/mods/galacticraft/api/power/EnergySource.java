package micdoodle8.mods.galacticraft.api.power;

import java.util.*;
import net.minecraftforge.common.util.*;

public abstract class EnergySource
{
    public static class EnergySourceWireless extends EnergySource
    {
        public final List<ILaserNode> nodes;
        
        public EnergySourceWireless(final List<ILaserNode> nodes) {
            this.nodes = nodes;
        }
    }
    
    public static class EnergySourceAdjacent extends EnergySource
    {
        public final ForgeDirection direction;
        
        public EnergySourceAdjacent(final ForgeDirection direction) {
            this.direction = direction;
        }
    }
}
