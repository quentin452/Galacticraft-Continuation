package micdoodle8.mods.galacticraft.api.transmission.tile;

import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public interface IConnector
{
    boolean canConnect(final ForgeDirection p0, final NetworkType p1);
}
