package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.world.biome.*;

public class WorldChunkManagerMars extends WorldChunkManagerSpace
{
    public BiomeGenBase getBiome() {
        return BiomeGenBaseMars.marsFlat;
    }
}
