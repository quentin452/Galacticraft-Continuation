package micdoodle8.mods.galacticraft.core.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.world.biome.*;

public class WorldChunkManagerMoon extends WorldChunkManagerSpace
{
    public BiomeGenBase getBiome() {
        return BiomeGenBaseMoon.moonFlat;
    }
}
