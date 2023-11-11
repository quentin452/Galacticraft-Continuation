package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.tile.*;

public interface IBoss
{
    void setRoom(final Vector3 p0, final Vector3 p1);
    
    void onBossSpawned(final TileEntityDungeonSpawner p0);
}
