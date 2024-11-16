package micdoodle8.mods.galacticraft.planets.mars.dimension;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;

public class TeleportTypeMars implements ITeleportType {

    @Override
    public boolean useParachute() {
        return false;
    }

    @Override
    public Vector3 getPlayerSpawnLocation(WorldServer world, EntityPlayerMP player) {
        if (player != null) {
            final GCPlayerStats stats = GCPlayerStats.get(player);
            double x = stats.coordsTeleportedFromX;
            double z = stats.coordsTeleportedFromZ;
            final int limit = ConfigManagerCore.otherPlanetWorldBorders - 2;
            if (limit > 20) {
                if (x > limit) {
                    z *= limit / x;
                    x = limit;
                } else if (x < -limit) {
                    z *= -limit / x;
                    x = -limit;
                }
                if (z > limit) {
                    x *= limit / z;
                    z = limit;
                } else if (z < -limit) {
                    x *= -limit / z;
                    z = -limit;
                }
            }
            return new Vector3(x, ConfigManagerCore.disableLander ? 250.0 : 900.0, z);
        }

        return null;
    }

    @Override
    public Vector3 getEntitySpawnLocation(WorldServer world, Entity entity) {
        return new Vector3(entity.posX, ConfigManagerCore.disableLander ? 250.0 : 900.0, entity.posZ);
    }

    @Override
    public Vector3 getParaChestSpawnLocation(WorldServer world, EntityPlayerMP player, Random rand) {
        return null;
    }

    @Override
    public void onSpaceDimensionChanged(World newWorld, EntityPlayerMP player, boolean ridingAutoRocket) {
        if (!ridingAutoRocket && player != null && GCPlayerStats.get(player).teleportCooldown <= 0) {
            if (player.capabilities.isFlying) {
                player.capabilities.isFlying = false;
            }

            final EntityLandingBalloons lander = new EntityLandingBalloons(player);

            if (!newWorld.isRemote) {
                newWorld.spawnEntityInWorld(lander);
            }

            GCPlayerStats.get(player).teleportCooldown = 10;
        }
    }

    @Override
    public void setupAdventureSpawn(EntityPlayerMP player) {
        // TODO Auto-generated method stub

    }
}
