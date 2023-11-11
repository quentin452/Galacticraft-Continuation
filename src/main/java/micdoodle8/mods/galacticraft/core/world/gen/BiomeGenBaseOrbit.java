package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BiomeGenBaseOrbit extends BiomeGenBase
{
    public static final BiomeGenBase space;
    
    private BiomeGenBaseOrbit(final int var1) {
        super(var1);
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedZombie.class, 10, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSpider.class, 10, 4, 4));
        this.rainfall = 0.0f;
    }
    
    public BiomeGenBaseOrbit setColor(final int var1) {
        return (BiomeGenBaseOrbit)super.setColor(var1);
    }
    
    public float getSpawningChance() {
        return 0.01f;
    }
    
    static {
        space = new BiomeGenBaseOrbit(ConfigManagerCore.biomeIDbase + 3).setBiomeName("space");
    }
}
