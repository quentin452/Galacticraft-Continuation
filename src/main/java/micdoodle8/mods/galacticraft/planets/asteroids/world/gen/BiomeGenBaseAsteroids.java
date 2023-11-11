package micdoodle8.mods.galacticraft.planets.asteroids.world.gen;

import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.monster.*;

public class BiomeGenBaseAsteroids extends BiomeGenBase
{
    public static final BiomeGenBase asteroid;
    
    private BiomeGenBaseAsteroids(final int var1) {
        super(var1);
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.resetMonsterListByMode(ConfigManagerCore.challengeMobDropsAndSpawning);
        this.rainfall = 0.0f;
        if (!ConfigManagerCore.disableBiomeTypeRegistrations) {
            BiomeDictionary.registerBiomeType((BiomeGenBase)this, new BiomeDictionary.Type[] { BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.SPOOKY });
        }
    }
    
    public void resetMonsterListByMode(final boolean challengeMode) {
        this.spawnableMonsterList.clear();
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedZombie.class, 3000, 1, 3));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSpider.class, 2000, 1, 2));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSkeleton.class, 1500, 1, 1));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedCreeper.class, 2000, 1, 1));
        if (challengeMode) {
            this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry((Class)EntityEnderman.class, 250, 1, 1));
        }
    }
    
    public BiomeGenBaseAsteroids setColor(final int var1) {
        return (BiomeGenBaseAsteroids)super.setColor(var1);
    }
    
    public float getSpawningChance() {
        return 0.01f;
    }
    
    static {
        asteroid = new BiomeGenBaseAsteroids(ConfigManagerCore.biomeIDbase + 2).setBiomeName("asteroids");
    }
}
