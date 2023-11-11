package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.*;

public class TeleportTypeOrbit implements ITeleportType
{
    public boolean useParachute() {
        return false;
    }
    
    public Vector3 getPlayerSpawnLocation(final WorldServer world, final EntityPlayerMP player) {
        return new Vector3(0.5, 65.0, 0.5);
    }
    
    public Vector3 getEntitySpawnLocation(final WorldServer world, final Entity player) {
        return new Vector3(0.5, 65.0, 0.5);
    }
    
    public Vector3 getParaChestSpawnLocation(final WorldServer world, final EntityPlayerMP player, final Random rand) {
        return new Vector3(-8.5, 90.0, -1.5);
    }
    
    public void onSpaceDimensionChanged(final World newWorld, final EntityPlayerMP player, final boolean ridingAutoRocket) {
    }
    
    public void setupAdventureSpawn(final EntityPlayerMP player) {
    }
}
