package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.entities.*;

public class TeleportTypeMoon implements ITeleportType
{
    public boolean useParachute() {
        return ConfigManagerCore.disableLander;
    }
    
    public Vector3 getPlayerSpawnLocation(final WorldServer world, final EntityPlayerMP player) {
        if (player != null) {
            final GCPlayerStats stats = GCPlayerStats.get(player);
            double x = stats.coordsTeleportedFromX;
            double z = stats.coordsTeleportedFromZ;
            final int limit = ConfigManagerCore.otherPlanetWorldBorders - 2;
            if (limit > 20) {
                if (x > limit) {
                    z *= limit / x;
                    x = limit;
                }
                else if (x < -limit) {
                    z *= -limit / x;
                    x = -limit;
                }
                if (z > limit) {
                    x *= limit / z;
                    z = limit;
                }
                else if (z < -limit) {
                    x *= -limit / z;
                    z = -limit;
                }
            }
            return new Vector3(x, ConfigManagerCore.disableLander ? 250.0 : 900.0, z);
        }
        return null;
    }
    
    public Vector3 getEntitySpawnLocation(final WorldServer world, final Entity entity) {
        return new Vector3(entity.posX, ConfigManagerCore.disableLander ? 250.0 : 900.0, entity.posZ);
    }
    
    public Vector3 getParaChestSpawnLocation(final WorldServer world, final EntityPlayerMP player, final Random rand) {
        if (ConfigManagerCore.disableLander) {
            final double x = (rand.nextDouble() * 2.0 - 1.0) * 4.0;
            final double z = (rand.nextDouble() * 2.0 - 1.0) * 4.0;
            return new Vector3(player.posX + x, 220.0, player.posZ + z);
        }
        return null;
    }
    
    public void onSpaceDimensionChanged(final World newWorld, final EntityPlayerMP player, final boolean ridingAutoRocket) {
        final GCPlayerStats stats = GCPlayerStats.get(player);
        if (!ridingAutoRocket && !ConfigManagerCore.disableLander && stats.teleportCooldown <= 0) {
            if (player.capabilities.isFlying) {
                player.capabilities.isFlying = false;
            }
            final EntityLander lander = new EntityLander(player);
            lander.setPosition(player.posX, player.posY, player.posZ);
            if (!newWorld.isRemote) {
                newWorld.spawnEntityInWorld((Entity)lander);
            }
            stats.teleportCooldown = 10;
        }
    }
    
    public void setupAdventureSpawn(final EntityPlayerMP player) {
    }
}
