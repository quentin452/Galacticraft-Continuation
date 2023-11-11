package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import cpw.mods.fml.common.registry.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.*;

public class AsteroidBlocks
{
    public static Block blockWalkway;
    public static Block blockWalkwayWire;
    public static Block blockWalkwayOxygenPipe;
    public static Block blockBasic;
    public static Block beamReflector;
    public static Block beamReceiver;
    public static Block shortRangeTelepad;
    public static Block fakeTelepad;
    public static Block treasureChestTier2;
    public static Block treasureChestTier3;
    public static Block blockDenseIce;
    public static Block blockMinerBase;
    public static Block minerBaseFull;
    
    public static void initBlocks() {
        AsteroidBlocks.treasureChestTier2 = (Block)new BlockTier2TreasureChest("treasureT2");
        AsteroidBlocks.treasureChestTier3 = (Block)new BlockTier3TreasureChest("treasureT3");
        AsteroidBlocks.blockWalkway = (Block)new BlockWalkway("walkway");
        AsteroidBlocks.blockWalkwayWire = (Block)new BlockWalkway("walkwayWire");
        AsteroidBlocks.blockWalkwayOxygenPipe = (Block)new BlockWalkway("walkwayOxygenPipe");
        AsteroidBlocks.blockBasic = new BlockBasicAsteroids("asteroidsBlock");
        AsteroidBlocks.beamReflector = (Block)new BlockBeamReflector("beamReflector");
        AsteroidBlocks.beamReceiver = (Block)new BlockBeamReceiver("beamReceiver");
        AsteroidBlocks.shortRangeTelepad = (Block)new BlockShortRangeTelepad("telepadShort");
        AsteroidBlocks.fakeTelepad = (Block)new BlockTelepadFake("telepadFake");
        AsteroidBlocks.blockDenseIce = (Block)new BlockIceAsteroids("denseIce");
        AsteroidBlocks.blockMinerBase = (Block)new BlockMinerBase("minerBase");
        AsteroidBlocks.minerBaseFull = (Block)new BlockMinerBaseFull("minerBaseFull");
        GCBlocks.hiddenBlocks.add(AsteroidBlocks.fakeTelepad);
        GCBlocks.hiddenBlocks.add(AsteroidBlocks.minerBaseFull);
    }
    
    public static void registerBlocks() {
        GameRegistry.registerBlock(AsteroidBlocks.treasureChestTier2, (Class)ItemBlockDesc.class, AsteroidBlocks.treasureChestTier2.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.treasureChestTier3, (Class)ItemBlockDesc.class, AsteroidBlocks.treasureChestTier3.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.blockBasic, (Class)ItemBlockAsteroids.class, AsteroidBlocks.blockBasic.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.blockWalkway, (Class)ItemBlockWalkway.class, AsteroidBlocks.blockWalkway.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.blockWalkwayWire, (Class)ItemBlockWalkway.class, AsteroidBlocks.blockWalkwayWire.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.blockWalkwayOxygenPipe, (Class)ItemBlockWalkway.class, AsteroidBlocks.blockWalkwayOxygenPipe.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.beamReflector, (Class)ItemBlockDesc.class, AsteroidBlocks.beamReflector.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.beamReceiver, (Class)ItemBlockDesc.class, AsteroidBlocks.beamReceiver.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.shortRangeTelepad, (Class)ItemBlockShortRangeTelepad.class, AsteroidBlocks.shortRangeTelepad.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.fakeTelepad, (Class)ItemBlockGC.class, AsteroidBlocks.fakeTelepad.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.blockDenseIce, (Class)ItemBlockGC.class, AsteroidBlocks.blockDenseIce.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.blockMinerBase, (Class)ItemBlockDesc.class, AsteroidBlocks.blockMinerBase.getUnlocalizedName());
        GameRegistry.registerBlock(AsteroidBlocks.minerBaseFull, (Class)ItemBlockDesc.class, AsteroidBlocks.minerBaseFull.getUnlocalizedName());
    }
    
    public static void setHarvestLevels() {
        AsteroidBlocks.blockBasic.setHarvestLevel("pickaxe", 0, 0);
        AsteroidBlocks.blockBasic.setHarvestLevel("pickaxe", 0, 1);
        AsteroidBlocks.blockBasic.setHarvestLevel("pickaxe", 0, 2);
        AsteroidBlocks.blockBasic.setHarvestLevel("pickaxe", 2, 3);
        AsteroidBlocks.blockBasic.setHarvestLevel("pickaxe", 2, 4);
        AsteroidBlocks.blockBasic.setHarvestLevel("pickaxe", 1, 5);
    }
    
    public static void oreDictRegistration() {
        OreDictionary.registerOre("oreAluminum", new ItemStack(AsteroidBlocks.blockBasic, 1, 3));
        OreDictionary.registerOre("oreAluminium", new ItemStack(AsteroidBlocks.blockBasic, 1, 3));
        OreDictionary.registerOre("oreNaturalAluminum", new ItemStack(AsteroidBlocks.blockBasic, 1, 3));
        OreDictionary.registerOre("oreIlmenite", new ItemStack(AsteroidBlocks.blockBasic, 1, 4));
        OreDictionary.registerOre("oreIron", new ItemStack(AsteroidBlocks.blockBasic, 1, 5));
    }
}
