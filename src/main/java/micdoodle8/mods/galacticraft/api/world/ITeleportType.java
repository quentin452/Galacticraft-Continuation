package micdoodle8.mods.galacticraft.api.world;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.*;

public interface ITeleportType
{
    boolean useParachute();
    
    Vector3 getPlayerSpawnLocation(final WorldServer p0, final EntityPlayerMP p1);
    
    Vector3 getEntitySpawnLocation(final WorldServer p0, final Entity p1);
    
    Vector3 getParaChestSpawnLocation(final WorldServer p0, final EntityPlayerMP p1, final Random p2);
    
    void onSpaceDimensionChanged(final World p0, final EntityPlayerMP p1, final boolean p2);
    
    void setupAdventureSpawn(final EntityPlayerMP p0);
}
