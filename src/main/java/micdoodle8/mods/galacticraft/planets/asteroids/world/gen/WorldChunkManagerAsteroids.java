package micdoodle8.mods.galacticraft.planets.asteroids.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.world.biome.*;

public class WorldChunkManagerAsteroids extends WorldChunkManagerSpace
{
    public BiomeGenBase getBiome() {
        return BiomeGenBaseAsteroids.asteroid;
    }
}
