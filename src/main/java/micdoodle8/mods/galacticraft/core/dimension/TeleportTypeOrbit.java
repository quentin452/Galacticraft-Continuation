package micdoodle8.mods.galacticraft.core.dimension;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;

public class TeleportTypeOrbit implements ITeleportType {

    @Override
    public boolean useParachute() {
        return false;
    }

    @Override
    public Vector3 getPlayerSpawnLocation(WorldServer world, EntityPlayerMP player) {
        return new Vector3(0.5, 65.1, 0.5);
    }

    @Override
    public Vector3 getEntitySpawnLocation(WorldServer world, Entity player) {
        return new Vector3(0.5, 65.1, 0.5);
    }

    @Override
    public Vector3 getParaChestSpawnLocation(WorldServer world, EntityPlayerMP player, Random rand) {
        return new Vector3(-8.5D, 90.0, -1.5D);
    }

    @Override
    public void onSpaceDimensionChanged(World newWorld, EntityPlayerMP player, boolean ridingAutoRocket) {}

    @Override
    public void setupAdventureSpawn(EntityPlayerMP player) {
        // TODO Auto-generated method stub

    }
}
