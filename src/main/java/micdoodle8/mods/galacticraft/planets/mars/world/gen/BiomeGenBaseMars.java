package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BiomeGenBaseMars extends BiomeGenBase
{
    public static final BiomeGenBase marsFlat;
    
    BiomeGenBaseMars(final int var1) {
        super(var1);
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedZombie.class, 10, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSpider.class, 10, 4, 4));
        this.rainfall = 0.0f;
    }
    
    public BiomeGenBaseMars setColor(final int var1) {
        return (BiomeGenBaseMars)super.setColor(var1);
    }
    
    public float getSpawningChance() {
        return 0.01f;
    }
    
    static {
        marsFlat = new BiomeGenFlatMars(ConfigManagerCore.biomeIDbase + 1).setBiomeName("marsFlat");
    }
}
