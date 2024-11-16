package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta;
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;

public class BiomeDecoratorMars extends BiomeDecoratorSpace {

    private final WorldGenerator dirtGen;
    private final WorldGenerator deshGen;
    private final WorldGenerator tinGen;
    private final WorldGenerator copperGen;
    private final WorldGenerator ironGen;
    private final WorldGenerator iceGen;
    private World currentWorld;

    public BiomeDecoratorMars() {
        this.copperGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 4, 0, true, MarsBlocks.marsBlock, 9);
        this.tinGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 4, 1, true, MarsBlocks.marsBlock, 9);
        this.deshGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 6, 2, true, MarsBlocks.marsBlock, 9);
        this.ironGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 8, 3, true, MarsBlocks.marsBlock, 9);
        this.dirtGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 32, 6, true, MarsBlocks.marsBlock, 9);
        this.iceGen = new WorldGenMinableMeta(Blocks.ice, 18, 0, true, MarsBlocks.marsBlock, 6);
    }

    @Override
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

    @Override
    protected void setCurrentWorld(World world) {
        this.currentWorld = world;
    }

    @Override
    protected World getCurrentWorld() {
        return this.currentWorld;
    }
}
