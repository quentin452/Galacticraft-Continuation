package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BiomeGenBaseMoon extends BiomeGenBase
{
    public static final BiomeGenBase moonFlat;
    
    BiomeGenBaseMoon(final int var1) {
        super(var1);
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedZombie.class, 8, 2, 3));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSpider.class, 8, 2, 3));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSkeleton.class, 8, 2, 3));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedCreeper.class, 8, 2, 3));
        this.rainfall = 0.0f;
    }
    
    public BiomeGenBaseMoon setColor(final int var1) {
        return (BiomeGenBaseMoon)super.setColor(var1);
    }
    
    public float getSpawningChance() {
        return 0.1f;
    }
    
    static {
        moonFlat = new BiomeGenFlatMoon(ConfigManagerCore.biomeIDbase).setBiomeName("moon");
    }
}
