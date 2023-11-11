package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.gen.feature.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.event.wgen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.dimension.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.world.*;
import java.util.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;

public class OreGenOtherMods
{
    private World worldObj;
    private Random randomGenerator;
    private int chunkX;
    private int chunkZ;
    private WorldGenerator oreGen;
    public static ArrayList<OreGenData> data;
    
    public static void addOre(final Block block, final int meta, final int rarity, final int depth, final int clumpSize, final boolean extraRandom, final int dim) {
        int clusters = 12;
        int size = 4;
        int min = 0;
        int max = 64;
        switch (depth) {
            case 0: {
                size = 6;
                clusters = 20;
                max = 80;
                if (rarity == 1) {
                    clusters = 9;
                    size = 4;
                    break;
                }
                if (rarity == 2) {
                    clusters = 6;
                    size = 3;
                    max = 96;
                    break;
                }
                break;
            }
            case 1: {
                size = 5;
                clusters = 12;
                max = 32;
                if (rarity == 1) {
                    clusters = 6;
                    size = 4;
                    max = 20;
                    break;
                }
                if (rarity == 2) {
                    clusters = 2;
                    size = 3;
                    max = 16;
                    break;
                }
                break;
            }
            case 2: {
                size = 6;
                clusters = 15;
                min = 32;
                max = 80;
                if (rarity == 1) {
                    clusters = 8;
                    size = 4;
                    min = 32;
                    max = 72;
                    break;
                }
                if (rarity == 2) {
                    clusters = 3;
                    size = 3;
                    min = 40;
                    max = 64;
                    break;
                }
                break;
            }
        }
        if (clumpSize == 0) {
            size = 1;
            clusters = 3 * clusters / 2;
        }
        else if (clumpSize == 2) {
            size *= 4;
            clusters /= 2;
        }
        if (extraRandom) {
            if (depth == 1) {
                min = -max * 3;
            }
            else {
                max *= 4;
            }
        }
        final OreGenData ore = new OreGenData(block, meta, clusters, size, min, max, dim);
        OreGenOtherMods.data.add(ore);
    }
    
    @SubscribeEvent
    public void onPlanetDecorated(final GCCoreEventPopulate.Post event) {
        this.worldObj = event.worldObj;
        this.randomGenerator = event.rand;
        this.chunkX = event.chunkX;
        this.chunkZ = event.chunkZ;
        int dimDetected = 0;
        final WorldProvider prov = this.worldObj.provider;
        if (!(prov instanceof IGalacticraftWorldProvider) || prov instanceof WorldProviderSpaceStation) {
            return;
        }
        Block stoneBlock = null;
        int stoneMeta = 0;
        if (prov instanceof WorldProviderMoon) {
            stoneBlock = GCBlocks.blockMoon;
            stoneMeta = 4;
            dimDetected = 1;
        }
        else if (GalacticraftCore.isPlanetsLoaded && prov instanceof WorldProviderMars) {
            stoneBlock = MarsBlocks.marsBlock;
            stoneMeta = 9;
            dimDetected = 2;
        }
        if (stoneBlock == null) {
            return;
        }
        for (final OreGenData ore : OreGenOtherMods.data) {
            if (ore.dimRestrict == 0 || ore.dimRestrict == dimDetected) {
                this.oreGen = (WorldGenerator)new WorldGenMinableMeta(ore.oreBlock, ore.sizeCluster, ore.oreMeta, true, stoneBlock, stoneMeta);
                this.genStandardOre1(ore.numClusters, this.oreGen, ore.minHeight, ore.maxHeight);
            }
        }
    }
    
    void genStandardOre1(final int amountPerChunk, final WorldGenerator worldGenerator, final int minY, final int maxY) {
        for (int var5 = 0; var5 < amountPerChunk; ++var5) {
            final int var6 = this.chunkX + this.randomGenerator.nextInt(16);
            final int var7 = this.randomGenerator.nextInt(maxY - minY) + minY;
            if (var7 >= 0) {
                final int var8 = this.chunkZ + this.randomGenerator.nextInt(16);
                worldGenerator.generate(this.worldObj, this.randomGenerator, var6, var7, var8);
            }
        }
    }
    
    static {
        OreGenOtherMods.data = new ArrayList<OreGenData>();
        for (final String str : ConfigManagerCore.oregenIDs) {
            try {
                final int slash = str.indexOf(47);
                int rarity = 0;
                int depth = 0;
                int size = 1;
                boolean extraRandom = false;
                int dim = 0;
                String s;
                if (slash >= 0) {
                    s = str.substring(0, slash).trim();
                    final String params = str.substring(slash).toUpperCase();
                    if (params.contains("UNCOMMON")) {
                        rarity = 1;
                    }
                    else if (params.contains("RARE")) {
                        rarity = 2;
                    }
                    if (params.contains("DEEP")) {
                        depth = 1;
                    }
                    else if (params.contains("SHALLOW")) {
                        depth = 2;
                    }
                    if (params.contains("SINGLE")) {
                        size = 0;
                    }
                    else if (params.contains("LARGE")) {
                        size = 2;
                    }
                    if (params.contains("XTRARANDOM")) {
                        extraRandom = true;
                    }
                    if (params.contains("ONLYMOON")) {
                        dim = 1;
                    }
                    else if (params.contains("ONLYMARS")) {
                        dim = 2;
                    }
                }
                else {
                    s = str;
                }
                final BlockTuple bt = ConfigManagerCore.stringToBlock(s, "Other mod ore generate IDs", true);
                if (bt != null) {
                    int meta = bt.meta;
                    if (meta == -1) {
                        meta = 0;
                    }
                    addOre(bt.block, meta, rarity, depth, size, extraRandom, dim);
                }
            }
            catch (Exception e) {
                GCLog.severe("[config] External Sealable IDs: error parsing '" + str + "'. Must be in the form Blockname or BlockName:metadata followed by / parameters ");
            }
        }
    }
    
    public static class OreGenData
    {
        public Block oreBlock;
        public int oreMeta;
        public int sizeCluster;
        public int numClusters;
        public int minHeight;
        public int maxHeight;
        public int dimRestrict;
        
        public OreGenData(final Block block, final int meta, final int num, final int cluster, final int min, final int max, final int dim) {
            this.oreBlock = GCBlocks.blockMoon;
            this.oreMeta = 0;
            this.sizeCluster = 4;
            this.numClusters = 8;
            this.minHeight = 0;
            this.maxHeight = 128;
            this.dimRestrict = 0;
            this.oreBlock = block;
            this.oreMeta = meta;
            this.sizeCluster = cluster;
            this.numClusters = num;
            this.minHeight = min;
            this.maxHeight = max;
            this.dimRestrict = dim;
        }
        
        public OreGenData(final Block block, final int meta, final int num, final int cluster) {
            this.oreBlock = GCBlocks.blockMoon;
            this.oreMeta = 0;
            this.sizeCluster = 4;
            this.numClusters = 8;
            this.minHeight = 0;
            this.maxHeight = 128;
            this.dimRestrict = 0;
            this.oreBlock = block;
            this.oreMeta = meta;
            this.sizeCluster = cluster;
            this.numClusters = num;
            this.minHeight = 0;
            this.maxHeight = 128;
        }
        
        public OreGenData(final Block block, final int meta, final int num) {
            this.oreBlock = GCBlocks.blockMoon;
            this.oreMeta = 0;
            this.sizeCluster = 4;
            this.numClusters = 8;
            this.minHeight = 0;
            this.maxHeight = 128;
            this.dimRestrict = 0;
            this.oreBlock = block;
            this.oreMeta = meta;
            this.sizeCluster = 4;
            this.numClusters = num;
            this.minHeight = 0;
            this.maxHeight = 128;
        }
    }
}
