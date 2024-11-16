package micdoodle8.mods.galacticraft.core.dimension;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;

public class TeleportTypeMoon implements ITeleportType {

    @Override
    public boolean useParachute() {
        return ConfigManagerCore.disableLander;
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
        if (ConfigManagerCore.disableLander) {
            final double x = (rand.nextDouble() * 2 - 1.0D) * 4.0D;
            final double z = (rand.nextDouble() * 2 - 1.0D) * 4.0D;
            return new Vector3(player.posX + x, 220.0D, player.posZ + z);
        }

        return null;
    }

    @Override
    public void onSpaceDimensionChanged(World newWorld, EntityPlayerMP player, boolean ridingAutoRocket) {
        final GCPlayerStats stats = GCPlayerStats.get(player);
        if (!ridingAutoRocket && !ConfigManagerCore.disableLander && stats.teleportCooldown <= 0) {
            if (player.capabilities.isFlying) {
                player.capabilities.isFlying = false;
            }

            final EntityLander lander = new EntityLander(player);
            lander.setPosition(player.posX, player.posY, player.posZ);

            if (!newWorld.isRemote) {
                newWorld.spawnEntityInWorld(lander);
            }

            stats.teleportCooldown = 10;
        }
    }

    @Override
    public void setupAdventureSpawn(EntityPlayerMP player) {
        // TODO Auto-generated method stub

    }
}
