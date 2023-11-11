package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import micdoodle8.mods.galacticraft.core.world.gen.dungeon.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.planets.mars.world.gen.dungeon.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import com.google.common.collect.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.api.prefab.core.*;
import net.minecraft.block.*;
import net.minecraft.world.chunk.*;

public class ChunkProviderMars extends ChunkProviderSpace
{
    private final BiomeDecoratorMars marsBiomeDecorator;
    private final MapGenCavernMars caveGenerator;
    private final MapGenCaveMars cavernGenerator;
    private final MapGenDungeon dungeonGenerator;

    public ChunkProviderMars(final World par1World, final long seed, final boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
        this.marsBiomeDecorator = new BiomeDecoratorMars();
        this.caveGenerator = new MapGenCavernMars();
        this.cavernGenerator = new MapGenCaveMars();
        this.dungeonGenerator = new MapGenDungeon(MarsBlocks.marsBlock, 7, 8, 16, 6);
        this.dungeonGenerator.otherRooms.add(new RoomEmptyMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomChestsMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomChestsMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.bossRooms.add(new RoomBossMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.treasureRooms.add(new RoomTreasureMars(null, 0, 0, 0, ForgeDirection.UNKNOWN));
    }

    protected BiomeDecoratorSpace getBiomeGenerator() {
        return (BiomeDecoratorSpace)this.marsBiomeDecorator;
    }

    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[] { BiomeGenBaseMars.marsFlat };
    }

    protected int getSeaLevel() {
        return 93;
    }

    protected List<MapGenBaseMeta> getWorldGenerators() {
        final List<MapGenBaseMeta> generators = Lists.newArrayList();
        generators.add(this.caveGenerator);
        generators.add(this.cavernGenerator);
        return generators;
    }

    protected BiomeGenBase.SpawnListEntry[] getMonsters() {
        final List<BiomeGenBase.SpawnListEntry> monsters = new ArrayList<BiomeGenBase.SpawnListEntry>();
        monsters.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedZombie.class, 8, 2, 3));
        monsters.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSpider.class, 8, 2, 3));
        monsters.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedSkeleton.class, 8, 2, 3));
        monsters.add(new BiomeGenBase.SpawnListEntry((Class)EntityEvolvedCreeper.class, 8, 2, 3));
        return monsters.toArray(new BiomeGenBase.SpawnListEntry[monsters.size()]);
    }

    protected BiomeGenBase.SpawnListEntry[] getCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    protected BlockMetaPair getGrassBlock() {
        return new BlockMetaPair(MarsBlocks.marsBlock, (byte)5);
    }

    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(MarsBlocks.marsBlock, (byte)6);
    }

    protected BlockMetaPair getStoneBlock() {
        return new BlockMetaPair(MarsBlocks.marsBlock, (byte)9);
    }

    public double getHeightModifier() {
        return 12.0;
    }

    public double getSmallFeatureHeightModifier() {
        return 26.0;
    }

    public double getMountainHeightModifier() {
        return 95.0;
    }

    public double getValleyHeightModifier() {
        return 50.0;
    }

    public int getCraterProbability() {
        return 2000;
    }

    public void onChunkProvide(final int cX, final int cZ, final Block[] blocks, final byte[] metadata) {
        this.dungeonGenerator.generateUsingArrays(this.worldObj, this.worldObj.getSeed(), cX * 16, 30, cZ * 16, cX, cZ, blocks, metadata);
    }

    public void onPopulate(final IChunkProvider provider, final int cX, final int cZ) {
        this.dungeonGenerator.handleTileEntities(this.rand);
    }
}
