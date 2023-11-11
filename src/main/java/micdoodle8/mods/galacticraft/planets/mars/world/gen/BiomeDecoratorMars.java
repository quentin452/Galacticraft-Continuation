package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import micdoodle8.mods.galacticraft.core.world.gen.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.planets.mars.*;

public class BiomeDecoratorMars extends BiomeDecoratorSpace
{
    private WorldGenerator dirtGen;
    private WorldGenerator deshGen;
    private WorldGenerator tinGen;
    private WorldGenerator copperGen;
    private WorldGenerator ironGen;
    private WorldGenerator iceGen;
    private World currentWorld;
    
    public BiomeDecoratorMars() {
        this.copperGen = (WorldGenerator)new WorldGenMinableMeta(MarsBlocks.marsBlock, 4, 0, true, MarsBlocks.marsBlock, 9);
        this.tinGen = (WorldGenerator)new WorldGenMinableMeta(MarsBlocks.marsBlock, 4, 1, true, MarsBlocks.marsBlock, 9);
        this.deshGen = (WorldGenerator)new WorldGenMinableMeta(MarsBlocks.marsBlock, 6, 2, true, MarsBlocks.marsBlock, 9);
        this.ironGen = (WorldGenerator)new WorldGenMinableMeta(MarsBlocks.marsBlock, 8, 3, true, MarsBlocks.marsBlock, 9);
        this.dirtGen = (WorldGenerator)new WorldGenMinableMeta(MarsBlocks.marsBlock, 32, 6, true, MarsBlocks.marsBlock, 9);
        this.iceGen = (WorldGenerator)new WorldGenMinableMeta(Blocks.ice, 18, 0, true, MarsBlocks.marsBlock, 6);
    }
    
    protected void decorate() {
        this.generateOre(4, this.iceGen, 60, 120);
        this.generateOre(20, this.dirtGen, 0, 200);
        if (!ConfigManagerMars.disableDeshGen) {
            this.generateOre(15, this.deshGen, 20, 64);
        }
        if (!ConfigManagerMars.disableCopperGen) {
            this.generateOre(26, this.copperGen, 0, 60);
        }
        if (!ConfigManagerMars.disableTinGen) {
            this.generateOre(23, this.tinGen, 0, 60);
        }
        if (!ConfigManagerMars.disableIronGen) {
            this.generateOre(20, this.ironGen, 0, 64);
        }
    }
    
    protected void setCurrentWorld(final World world) {
        this.currentWorld = world;
    }
    
    protected World getCurrentWorld() {
        return this.currentWorld;
    }
}
