package micdoodle8.mods.galacticraft.planets.mars.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import cpw.mods.fml.common.registry.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.*;
import micdoodle8.mods.galacticraft.planets.mars.items.ItemBlockMachine;

public class MarsBlocks
{
    public static Block marsBlock;
    public static Block blockSludge;
    public static Block vine;
    public static Block rock;
    public static Block tier2TreasureChest;
    public static Block machine;
    public static Block machineT2;
    public static Block creeperEgg;
    public static Block marsCobblestoneStairs;
    public static Block marsBricksStairs;
    public static Block hydrogenPipe;

    public static void initBlocks() {
        MarsBlocks.marsBlock = new BlockBasicMars().setHardness(2.2f).setBlockName("mars");
        MarsBlocks.vine = new BlockCavernousVine().setHardness(0.1f).setBlockName("cavernVines");
        MarsBlocks.rock = new BlockSlimelingEgg().setHardness(0.75f).setBlockName("slimelingEgg");
        MarsBlocks.tier2TreasureChest = AsteroidBlocks.treasureChestTier2;
        MarsBlocks.machine = new BlockMachineMars().setHardness(1.8f).setBlockName("marsMachine");
        MarsBlocks.machineT2 = new BlockMachineMarsT2().setHardness(1.8f).setBlockName("marsMachineT2");
        MarsBlocks.creeperEgg = new BlockCreeperEgg().setHardness(-1.0f).setBlockName("creeperEgg");
        MarsBlocks.marsCobblestoneStairs = new BlockStairsGC("marsCobblestoneStairs", MarsBlocks.marsBlock, BlockStairsGC.StairsCategoryGC.MARS_COBBLESTONE).setHardness(1.5f);
        MarsBlocks.marsBricksStairs = new BlockStairsGC("marsDungeonBricksStairs", MarsBlocks.marsBlock, BlockStairsGC.StairsCategoryGC.MARS_BRICKS).setHardness(4.0f);
        MarsBlocks.hydrogenPipe = (Block)new BlockHydrogenPipe("hydrogenPipe");
    }

    public static void setHarvestLevels() {
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 2, 0);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 2, 1);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 3, 2);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 1, 3);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 0, 4);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 3, 7);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 0, 8);
        MarsBlocks.marsBlock.setHarvestLevel("pickaxe", 1, 9);
        MarsBlocks.marsBlock.setHarvestLevel("shovel", 0, 5);
        MarsBlocks.marsBlock.setHarvestLevel("shovel", 0, 6);
        MarsBlocks.rock.setHarvestLevel("pickaxe", 3);
        MarsBlocks.marsCobblestoneStairs.setHarvestLevel("pickaxe", 0);
        MarsBlocks.marsBricksStairs.setHarvestLevel("pickaxe", 3);
    }

    public static void registerBlocks() {
        GameRegistry.registerBlock(MarsBlocks.marsBlock, (Class)ItemBlockMars.class, MarsBlocks.marsBlock.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.vine, (Class)ItemBlockDesc.class, MarsBlocks.vine.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.rock, (Class)ItemBlockEgg.class, MarsBlocks.rock.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.creeperEgg, (Class)ItemBlockDesc.class, MarsBlocks.creeperEgg.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.machine, (Class)ItemBlockMachine.class, MarsBlocks.machine.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.machineT2, (Class)ItemBlockMachine.class, MarsBlocks.machineT2.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.marsCobblestoneStairs, (Class)ItemBlockGC.class, MarsBlocks.marsCobblestoneStairs.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.marsBricksStairs, (Class)ItemBlockGC.class, MarsBlocks.marsBricksStairs.getUnlocalizedName());
        GameRegistry.registerBlock(MarsBlocks.hydrogenPipe, (Class)ItemBlockDesc.class, MarsBlocks.hydrogenPipe.getUnlocalizedName());
    }

    public static void oreDictRegistration() {
        OreDictionary.registerOre("oreCopper", new ItemStack(MarsBlocks.marsBlock, 1, 0));
        OreDictionary.registerOre("oreTin", new ItemStack(MarsBlocks.marsBlock, 1, 1));
        OreDictionary.registerOre("oreIron", new ItemStack(MarsBlocks.marsBlock, 1, 3));
        OreDictionary.registerOre("oreDesh", new ItemStack(MarsBlocks.marsBlock, 1, 2));
    }
}
