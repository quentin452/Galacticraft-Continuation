package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.init.*;
import net.minecraftforge.oredict.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.registry.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;

public class GCBlocks
{
    public static Block breatheableAir;
    public static Block brightAir;
    public static Block brightBreatheableAir;
    public static Block brightLamp;
    public static Block treasureChestTier1;
    public static Block landingPad;
    public static Block unlitTorch;
    public static Block unlitTorchLit;
    public static Block oxygenDistributor;
    public static Block oxygenPipe;
    public static Block oxygenCollector;
    public static Block oxygenCompressor;
    public static Block oxygenSealer;
    public static Block oxygenDetector;
    public static Block nasaWorkbench;
    public static Block fallenMeteor;
    public static Block basicBlock;
    public static Block airLockFrame;
    public static Block airLockSeal;
    public static Block crudeOil;
    public static Block fuel;
    public static Block refinery;
    public static Block fuelLoader;
    public static Block landingPadFull;
    public static Block spaceStationBase;
    public static Block fakeBlock;
    public static Block sealableBlock;
    public static Block cargoLoader;
    public static Block parachest;
    public static Block solarPanel;
    public static Block radioTelescope;
    public static Block machineBase;
    public static Block machineBase2;
    public static Block machineTiered;
    public static Block aluminumWire;
    public static Block glowstoneTorch;
    public static Block blockMoon;
    public static Block cheeseBlock;
    public static Block spinThruster;
    public static Block screen;
    public static Block telemetry;
    public static Block slabGCHalf;
    public static Block slabGCDouble;
    public static Block tinStairs1;
    public static Block tinStairs2;
    public static Block moonStoneStairs;
    public static Block moonBricksStairs;
    public static Block wallGC;
    public static final Material machine;
    public static ArrayList<Block> hiddenBlocks;
    public static ArrayList<Block> otherModTorchesLit;
    
    public static void initBlocks() {
        GCBlocks.breatheableAir = (Block)new BlockBreathableAir("breatheableAir");
        GCBlocks.brightAir = (Block)new BlockBrightAir("brightAir");
        GCBlocks.brightBreatheableAir = (Block)new BlockBrightBreathableAir("brightBreathableAir");
        GCBlocks.brightLamp = (Block)new BlockBrightLamp("arclamp");
        GCBlocks.treasureChestTier1 = (Block)new BlockT1TreasureChest("treasureChest");
        GCBlocks.landingPad = (Block)new BlockLandingPad("landingPad");
        GCBlocks.landingPadFull = (Block)new BlockLandingPadFull("landingPadFull");
        GCBlocks.unlitTorch = (Block)new BlockUnlitTorch(false, "unlitTorch");
        GCBlocks.unlitTorchLit = (Block)new BlockUnlitTorch(true, "unlitTorchLit");
        GCBlocks.oxygenDistributor = (Block)new BlockOxygenDistributor("distributor");
        GCBlocks.oxygenPipe = (Block)new BlockOxygenPipe("oxygenPipe");
        GCBlocks.oxygenCollector = (Block)new BlockOxygenCollector("oxygenCollector");
        GCBlocks.nasaWorkbench = (Block)new BlockNasaWorkbench("rocketWorkbench");
        GCBlocks.fallenMeteor = (Block)new BlockFallenMeteor("fallenMeteor");
        GCBlocks.basicBlock = (Block)new BlockBasic("gcBlockCore");
        GCBlocks.airLockFrame = (Block)new BlockAirLockFrame("airLockFrame");
        GCBlocks.airLockSeal = (Block)new BlockAirLockWall("airLockSeal");
        GCBlocks.refinery = (Block)new BlockRefinery("refinery");
        GCBlocks.oxygenCompressor = (Block)new BlockOxygenCompressor(false, "oxygenCompressor");
        GCBlocks.fuelLoader = (Block)new BlockFuelLoader("fuelLoader");
        GCBlocks.spaceStationBase = (Block)new BlockSpaceStationBase("spaceStationBase");
        GCBlocks.fakeBlock = (Block)new BlockMulti("dummyblock");
        GCBlocks.oxygenSealer = (Block)new BlockOxygenSealer("sealer");
        GCBlocks.sealableBlock = (Block)new BlockEnclosed("enclosed");
        GCBlocks.oxygenDetector = (Block)new BlockOxygenDetector("oxygenDetector");
        GCBlocks.cargoLoader = (Block)new BlockCargoLoader("cargo");
        GCBlocks.parachest = (Block)new BlockParaChest("parachest");
        GCBlocks.solarPanel = (Block)new BlockSolar("solar");
        GCBlocks.radioTelescope = (Block)new BlockDish("dish");
        GCBlocks.machineBase = (Block)new BlockMachine("machine");
        GCBlocks.machineBase2 = (Block)new BlockMachine2("machine2");
        GCBlocks.machineTiered = (Block)new BlockMachineTiered("machineTiered");
        GCBlocks.aluminumWire = (Block)new BlockAluminumWire("aluminumWire");
        GCBlocks.glowstoneTorch = (Block)new BlockGlowstoneTorch("glowstoneTorch");
        GCBlocks.blockMoon = (Block)new BlockBasicMoon();
        GCBlocks.cheeseBlock = (Block)new BlockCheese();
        GCBlocks.spinThruster = (Block)new BlockSpinThruster("spinThruster");
        GCBlocks.screen = (Block)new BlockScreen("viewScreen");
        GCBlocks.telemetry = (Block)new BlockTelemetry("telemetry");
        GCBlocks.slabGCHalf = (Block)new BlockSlabGC("slabGCHalf", false, Material.rock);
        GCBlocks.slabGCDouble = (Block)new BlockSlabGC("slabGCDouble", true, Material.rock);
        GCBlocks.tinStairs1 = new BlockStairsGC("tinStairs1", GCBlocks.blockMoon, BlockStairsGC.StairsCategoryGC.TIN1).setHardness(2.0f);
        GCBlocks.tinStairs2 = new BlockStairsGC("tinStairs2", GCBlocks.blockMoon, BlockStairsGC.StairsCategoryGC.TIN2).setHardness(2.0f);
        GCBlocks.moonStoneStairs = new BlockStairsGC("moonStoneStairs", GCBlocks.blockMoon, BlockStairsGC.StairsCategoryGC.MOON_STONE).setHardness(1.5f);
        GCBlocks.moonBricksStairs = new BlockStairsGC("moonBricksStairs", GCBlocks.blockMoon, BlockStairsGC.StairsCategoryGC.MOON_BRICKS).setHardness(4.0f);
        GCBlocks.wallGC = (Block)new BlockWallGC("wallGC", GCBlocks.blockMoon);
        GCCoreUtil.registerGalacticraftBlock("rocketLaunchPad", GCBlocks.landingPad, 0);
        GCCoreUtil.registerGalacticraftBlock("buggyFuelingPad", GCBlocks.landingPad, 1);
        GCCoreUtil.registerGalacticraftBlock("oxygenCollector", GCBlocks.oxygenCollector);
        GCCoreUtil.registerGalacticraftBlock("oxygenCompressor", GCBlocks.oxygenCompressor);
        GCCoreUtil.registerGalacticraftBlock("oxygenDistributor", GCBlocks.oxygenDistributor);
        GCCoreUtil.registerGalacticraftBlock("oxygenSealer", GCBlocks.oxygenSealer);
        GCCoreUtil.registerGalacticraftBlock("oxygenDetector", GCBlocks.oxygenDetector);
        GCCoreUtil.registerGalacticraftBlock("oxygenPipe", GCBlocks.oxygenPipe);
        GCCoreUtil.registerGalacticraftBlock("refinery", GCBlocks.refinery);
        GCCoreUtil.registerGalacticraftBlock("fuelLoader", GCBlocks.fuelLoader);
        GCCoreUtil.registerGalacticraftBlock("cargoLoader", GCBlocks.cargoLoader, 0);
        GCCoreUtil.registerGalacticraftBlock("cargoUnloader", GCBlocks.cargoLoader, 4);
        GCCoreUtil.registerGalacticraftBlock("nasaWorkbench", GCBlocks.nasaWorkbench);
        GCCoreUtil.registerGalacticraftBlock("tinDecorationBlock1", GCBlocks.basicBlock, 3);
        GCCoreUtil.registerGalacticraftBlock("tinDecorationBlock2", GCBlocks.basicBlock, 4);
        GCCoreUtil.registerGalacticraftBlock("airLockFrame", GCBlocks.airLockFrame);
        GCCoreUtil.registerGalacticraftBlock("sealableOxygenPipe", GCBlocks.sealableBlock, 1);
        GCCoreUtil.registerGalacticraftBlock("sealableCopperCable", GCBlocks.sealableBlock, 2);
        GCCoreUtil.registerGalacticraftBlock("sealableGoldCable", GCBlocks.sealableBlock, 3);
        GCCoreUtil.registerGalacticraftBlock("sealableHighVoltageCable", GCBlocks.sealableBlock, 0);
        GCCoreUtil.registerGalacticraftBlock("sealableGlassFibreCable", GCBlocks.sealableBlock, 5);
        GCCoreUtil.registerGalacticraftBlock("sealableLowVoltageCable", GCBlocks.sealableBlock, 6);
        GCCoreUtil.registerGalacticraftBlock("sealableStonePipeItem", GCBlocks.sealableBlock, 7);
        GCCoreUtil.registerGalacticraftBlock("sealableCobblestonePipeItem", GCBlocks.sealableBlock, 8);
        GCCoreUtil.registerGalacticraftBlock("sealableStonePipeFluid", GCBlocks.sealableBlock, 9);
        GCCoreUtil.registerGalacticraftBlock("sealableCobblestonePipeFluid", GCBlocks.sealableBlock, 10);
        GCCoreUtil.registerGalacticraftBlock("sealableStonePipePower", GCBlocks.sealableBlock, 11);
        GCCoreUtil.registerGalacticraftBlock("sealableGoldPipePower", GCBlocks.sealableBlock, 12);
        GCCoreUtil.registerGalacticraftBlock("sealableMECable", GCBlocks.sealableBlock, 13);
        GCCoreUtil.registerGalacticraftBlock("copperWire", GCBlocks.aluminumWire);
        GCCoreUtil.registerGalacticraftBlock("parachest", GCBlocks.parachest);
        GCCoreUtil.registerGalacticraftBlock("coalGenerator", GCBlocks.machineBase, 0);
        GCCoreUtil.registerGalacticraftBlock("solarPanelBasic", GCBlocks.solarPanel, 0);
        GCCoreUtil.registerGalacticraftBlock("solarPanelAdvanced", GCBlocks.solarPanel, 4);
        GCCoreUtil.registerGalacticraftBlock("radioTelescope", GCBlocks.radioTelescope, 0);
        GCCoreUtil.registerGalacticraftBlock("energyStorageModule", GCBlocks.machineTiered, 0);
        GCCoreUtil.registerGalacticraftBlock("electricFurnace", GCBlocks.machineTiered, 4);
        GCCoreUtil.registerGalacticraftBlock("ingotCompressor", GCBlocks.machineBase, 12);
        GCCoreUtil.registerGalacticraftBlock("circuitFabricator", GCBlocks.machineBase2, 4);
        GCCoreUtil.registerGalacticraftBlock("ingotCompressorElectric", GCBlocks.machineBase2, 0);
        GCCoreUtil.registerGalacticraftBlock("electricArcFurnace", GCBlocks.machineTiered, 12);
        GCCoreUtil.registerGalacticraftBlock("energyStorageCluster", GCBlocks.machineTiered, 8);
        GCCoreUtil.registerGalacticraftBlock("oreCopper", GCBlocks.basicBlock, 5);
        GCCoreUtil.registerGalacticraftBlock("oreTin", GCBlocks.basicBlock, 6);
        GCCoreUtil.registerGalacticraftBlock("oreAluminum", GCBlocks.basicBlock, 7);
        GCCoreUtil.registerGalacticraftBlock("oreSilicon", GCBlocks.basicBlock, 8);
        GCCoreUtil.registerGalacticraftBlock("fallenMeteor", GCBlocks.fallenMeteor);
        GCCoreUtil.registerGalacticraftBlock("torchGlowstone", GCBlocks.glowstoneTorch);
        GCCoreUtil.registerGalacticraftBlock("wireAluminum", GCBlocks.aluminumWire);
        GCCoreUtil.registerGalacticraftBlock("wireAluminumHeavy", GCBlocks.aluminumWire, 1);
        GCCoreUtil.registerGalacticraftBlock("spinThruster", GCBlocks.spinThruster);
        GCCoreUtil.registerGalacticraftBlock("viewScreen", GCBlocks.screen);
        GCCoreUtil.registerGalacticraftBlock("telemetry", GCBlocks.telemetry);
        GCCoreUtil.registerGalacticraftBlock("arclamp", GCBlocks.brightLamp);
        GCCoreUtil.registerGalacticraftBlock("treasureChestTier1", GCBlocks.treasureChestTier1);
        GCBlocks.hiddenBlocks.add(GCBlocks.airLockSeal);
        GCBlocks.hiddenBlocks.add(GCBlocks.breatheableAir);
        GCBlocks.hiddenBlocks.add(GCBlocks.brightBreatheableAir);
        GCBlocks.hiddenBlocks.add(GCBlocks.brightAir);
        GCBlocks.hiddenBlocks.add(GCBlocks.unlitTorch);
        GCBlocks.hiddenBlocks.add(GCBlocks.unlitTorchLit);
        GCBlocks.hiddenBlocks.add(GCBlocks.landingPadFull);
        GCBlocks.hiddenBlocks.add(GCBlocks.fakeBlock);
        GCBlocks.hiddenBlocks.add(GCBlocks.spaceStationBase);
        GCBlocks.hiddenBlocks.add(GCBlocks.slabGCDouble);
        registerBlocks();
        setHarvestLevels();
        BlockUnlitTorch.register((BlockUnlitTorch)GCBlocks.unlitTorch, (BlockUnlitTorch)GCBlocks.unlitTorchLit, Blocks.torch);
        doOtherModsTorches();
        OreDictionary.registerOre("oreCopper", new ItemStack(GCBlocks.basicBlock, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(GCBlocks.blockMoon, 1, 0));
        OreDictionary.registerOre("oreTin", new ItemStack(GCBlocks.basicBlock, 1, 6));
        OreDictionary.registerOre("oreTin", new ItemStack(GCBlocks.blockMoon, 1, 1));
        OreDictionary.registerOre("oreAluminum", new ItemStack(GCBlocks.basicBlock, 1, 7));
        OreDictionary.registerOre("oreAluminium", new ItemStack(GCBlocks.basicBlock, 1, 7));
        OreDictionary.registerOre("oreNaturalAluminum", new ItemStack(GCBlocks.basicBlock, 1, 7));
        OreDictionary.registerOre("oreSilicon", new ItemStack(GCBlocks.basicBlock, 1, 8));
        OreDictionary.registerOre("oreCheese", new ItemStack(GCBlocks.blockMoon, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(GCBlocks.basicBlock, 1, 9));
        OreDictionary.registerOre("blockTin", new ItemStack(GCBlocks.basicBlock, 1, 10));
        OreDictionary.registerOre("blockAluminum", new ItemStack(GCBlocks.basicBlock, 1, 11));
        OreDictionary.registerOre("blockAluminium", new ItemStack(GCBlocks.basicBlock, 1, 11));
    }
    
    private static void doOtherModsTorches() {
        if (Loader.isModLoaded("TConstruct")) {
            Block modTorch = null;
            try {
                try {
                    final Class clazz = Class.forName("slimeknights.tconstruct.gadgets.TinkerGadgets");
                    modTorch = (Block)clazz.getField("stoneTorch").get(null);
                }
                catch (Exception e) {
                    final Class clazz2 = Class.forName("tconstruct.world.TinkerWorld");
                    modTorch = (Block)clazz2.getField("stoneTorch").get(null);
                }
            }
            catch (Exception ex) {}
            if (modTorch != null) {
                final BlockUnlitTorch torch = new BlockUnlitTorch(false, "unlitTorch_Stone");
                final BlockUnlitTorch torchLit = new BlockUnlitTorch(true, "unlitTorchLit_Stone");
                GCBlocks.hiddenBlocks.add((Block)torch);
                GCBlocks.hiddenBlocks.add((Block)torchLit);
                GCBlocks.otherModTorchesLit.add((Block)torchLit);
                GameRegistry.registerBlock((Block)torch, (Class)ItemBlockGC.class, torch.getUnlocalizedName());
                GameRegistry.registerBlock((Block)torchLit, (Class)ItemBlockGC.class, torchLit.getUnlocalizedName());
                BlockUnlitTorch.register(torch, torchLit, modTorch);
            }
        }
    }
    
    public static void setHarvestLevels() {
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 2, 5);
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 2, 6);
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 2, 7);
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 1, 8);
        GCBlocks.fallenMeteor.setHarvestLevel("pickaxe", 3);
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 2, 0);
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 2, 1);
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 1, 2);
        GCBlocks.blockMoon.setHarvestLevel("shovel", 0, 3);
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 1, 4);
        GCBlocks.slabGCHalf.setHarvestLevel("pickaxe", 1, 0);
        GCBlocks.slabGCHalf.setHarvestLevel("pickaxe", 1, 1);
        GCBlocks.slabGCHalf.setHarvestLevel("pickaxe", 1, 2);
        GCBlocks.slabGCHalf.setHarvestLevel("pickaxe", 3, 3);
        GCBlocks.slabGCHalf.setHarvestLevel("pickaxe", 1, 4);
        GCBlocks.slabGCHalf.setHarvestLevel("pickaxe", 3, 5);
        GCBlocks.slabGCDouble.setHarvestLevel("pickaxe", 1, 0);
        GCBlocks.slabGCDouble.setHarvestLevel("pickaxe", 1, 1);
        GCBlocks.slabGCDouble.setHarvestLevel("pickaxe", 1, 2);
        GCBlocks.slabGCDouble.setHarvestLevel("pickaxe", 3, 3);
        GCBlocks.slabGCDouble.setHarvestLevel("pickaxe", 1, 4);
        GCBlocks.slabGCDouble.setHarvestLevel("pickaxe", 3, 5);
        GCBlocks.tinStairs1.setHarvestLevel("pickaxe", 1);
        GCBlocks.tinStairs1.setHarvestLevel("pickaxe", 1);
        GCBlocks.moonStoneStairs.setHarvestLevel("pickaxe", 1);
        GCBlocks.moonBricksStairs.setHarvestLevel("pickaxe", 3);
        GCBlocks.wallGC.setHarvestLevel("pickaxe", 1, 0);
        GCBlocks.wallGC.setHarvestLevel("pickaxe", 1, 1);
        GCBlocks.wallGC.setHarvestLevel("pickaxe", 1, 2);
        GCBlocks.wallGC.setHarvestLevel("pickaxe", 3, 3);
        GCBlocks.wallGC.setHarvestLevel("pickaxe", 0, 4);
        GCBlocks.wallGC.setHarvestLevel("pickaxe", 3, 5);
        for (int num = 5; num < 14; ++num) {
            GCBlocks.blockMoon.setHarvestLevel("shovel", 0, num);
        }
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 3, 14);
    }
    
    public static void registerBlocks() {
        GameRegistry.registerBlock(GCBlocks.landingPad, (Class)ItemBlockLandingPad.class, GCBlocks.landingPad.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.landingPadFull, (Class)ItemBlockGC.class, GCBlocks.landingPadFull.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.unlitTorch, (Class)ItemBlock.class, GCBlocks.unlitTorch.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.unlitTorchLit, (Class)ItemBlock.class, GCBlocks.unlitTorchLit.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.breatheableAir, (Class)ItemBlockGC.class, GCBlocks.breatheableAir.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.brightAir, (Class)ItemBlockGC.class, GCBlocks.brightAir.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.brightBreatheableAir, (Class)ItemBlockGC.class, GCBlocks.brightBreatheableAir.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.oxygenCollector, (Class)ItemBlockDesc.class, GCBlocks.oxygenCollector.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.oxygenCompressor, (Class)ItemBlockOxygenCompressor.class, GCBlocks.oxygenCompressor.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.oxygenDistributor, (Class)ItemBlockDesc.class, GCBlocks.oxygenDistributor.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.oxygenSealer, (Class)ItemBlockDesc.class, GCBlocks.oxygenSealer.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.oxygenDetector, (Class)ItemBlockDesc.class, GCBlocks.oxygenDetector.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.oxygenPipe, (Class)ItemBlockDesc.class, GCBlocks.oxygenPipe.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.refinery, (Class)ItemBlockDesc.class, GCBlocks.refinery.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.fuelLoader, (Class)ItemBlockDesc.class, GCBlocks.fuelLoader.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.cargoLoader, (Class)ItemBlockCargoLoader.class, GCBlocks.cargoLoader.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.nasaWorkbench, (Class)ItemBlockDesc.class, GCBlocks.nasaWorkbench.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.basicBlock, (Class)ItemBlockBase.class, GCBlocks.basicBlock.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.airLockFrame, (Class)ItemBlockAirLock.class, GCBlocks.airLockFrame.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.airLockSeal, (Class)ItemBlockGC.class, GCBlocks.airLockSeal.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.sealableBlock, (Class)ItemBlockEnclosed.class, GCBlocks.sealableBlock.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.spaceStationBase, (Class)ItemBlockGC.class, GCBlocks.spaceStationBase.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.fakeBlock, (Class)ItemBlockDummy.class, GCBlocks.fakeBlock.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.parachest, (Class)ItemBlockDesc.class, GCBlocks.parachest.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.solarPanel, (Class)ItemBlockSolar.class, GCBlocks.solarPanel.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.radioTelescope, (Class)ItemBlockGC.class, GCBlocks.radioTelescope.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.machineBase, (Class)ItemBlockMachine.class, GCBlocks.machineBase.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.machineBase2, (Class)ItemBlockMachine.class, GCBlocks.machineBase2.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.machineTiered, (Class)ItemBlockMachine.class, GCBlocks.machineTiered.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.aluminumWire, (Class)ItemBlockAluminumWire.class, GCBlocks.aluminumWire.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.glowstoneTorch, (Class)ItemBlockDesc.class, GCBlocks.glowstoneTorch.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.fallenMeteor, (Class)ItemBlockDesc.class, GCBlocks.fallenMeteor.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.blockMoon, (Class)ItemBlockMoon.class, GCBlocks.blockMoon.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.cheeseBlock, (Class)ItemBlockCheese.class, GCBlocks.cheeseBlock.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.spinThruster, (Class)ItemBlockThruster.class, GCBlocks.spinThruster.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.screen, (Class)ItemBlockDesc.class, GCBlocks.screen.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.telemetry, (Class)ItemBlockDesc.class, GCBlocks.telemetry.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.brightLamp, (Class)ItemBlockArclamp.class, GCBlocks.brightLamp.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.treasureChestTier1, (Class)ItemBlockDesc.class, GCBlocks.treasureChestTier1.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.tinStairs1, (Class)ItemBlockGC.class, GCBlocks.tinStairs1.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.tinStairs2, (Class)ItemBlockGC.class, GCBlocks.tinStairs2.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.moonStoneStairs, (Class)ItemBlockGC.class, GCBlocks.moonStoneStairs.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.moonBricksStairs, (Class)ItemBlockGC.class, GCBlocks.moonBricksStairs.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.wallGC, (Class)ItemBlockWallGC.class, GCBlocks.wallGC.getUnlocalizedName());
        GameRegistry.registerBlock(GCBlocks.slabGCHalf, (Class)ItemBlockSlabGC.class, GCBlocks.slabGCHalf.getUnlocalizedName().replace("tile.", ""), new Object[] { GCBlocks.slabGCHalf, GCBlocks.slabGCDouble });
        GameRegistry.registerBlock(GCBlocks.slabGCDouble, (Class)ItemBlockSlabGC.class, GCBlocks.slabGCDouble.getUnlocalizedName().replace("tile.", ""), new Object[] { GCBlocks.slabGCHalf, GCBlocks.slabGCDouble });
    }
    
    static {
        machine = new Material(MapColor.ironColor);
        GCBlocks.hiddenBlocks = new ArrayList<Block>();
        GCBlocks.otherModTorchesLit = new ArrayList<Block>();
    }
}
