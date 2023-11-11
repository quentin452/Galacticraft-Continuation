package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.*;

public class TeleportTypeOverworld implements ITeleportType
{
    public boolean useParachute() {
        return true;
    }
    
    public Vector3 getPlayerSpawnLocation(final WorldServer world, final EntityPlayerMP player) {
        if (player != null) {
            final GCPlayerStats stats = GCPlayerStats.get(player);
            return new Vector3(stats.coordsTeleportedFromX, 250.0, stats.coordsTeleportedFromZ);
        }
        return null;
    }
    
    public Vector3 getEntitySpawnLocation(final WorldServer world, final Entity entity) {
        return new Vector3(entity.posX, 250.0, entity.posZ);
    }
    
    public Vector3 getParaChestSpawnLocation(final WorldServer world, final EntityPlayerMP player, final Random rand) {
        final double x = (rand.nextDouble() * 2.0 - 1.0) * 5.0;
        final double z = (rand.nextDouble() * 2.0 - 1.0) * 5.0;
        return new Vector3(player.posX + x, 230.0, player.posZ + z);
    }
    
    public void onSpaceDimensionChanged(final World newWorld, final EntityPlayerMP player, final boolean ridingAutoRocket) {
    }
    
    public void setupAdventureSpawn(final EntityPlayerMP player) {
    }
}
