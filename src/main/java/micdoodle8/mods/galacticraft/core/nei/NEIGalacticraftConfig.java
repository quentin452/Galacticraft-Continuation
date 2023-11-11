package micdoodle8.mods.galacticraft.core.nei;

import codechicken.nei.*;
import codechicken.nei.api.API;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import codechicken.nei.recipe.*;
import codechicken.nei.api.*;
import java.util.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;

public class NEIGalacticraftConfig implements IConfigureNEI
{
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> rocketBenchRecipes;
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> buggyBenchRecipes;
    private static HashMap<PositionedStack, PositionedStack> refineryRecipes;
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> circuitFabricatorRecipes;
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> ingotCompressorRecipes;

    public void loadConfig() {
        this.registerRecipes();
        for (final Item item : GCItems.hiddenItems) {
            API.hideItem(new ItemStack(item, 1, 0));
        }
        for (final Block block : GCBlocks.hiddenBlocks) {
            API.hideItem(new ItemStack(block, 1, 0));
            if (block == GCBlocks.slabGCDouble) {
                for (int j = 1; j < (GalacticraftCore.isPlanetsLoaded ? 6 : 4); ++j) {
                    API.hideItem(new ItemStack(block, 1, j));
                }
            }
        }
        API.registerRecipeHandler((ICraftingHandler)new RocketT1RecipeHandler());
        API.registerUsageHandler((IUsageHandler)new RocketT1RecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new BuggyRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new BuggyRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new RefineryRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new RefineryRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new CircuitFabricatorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new CircuitFabricatorRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new IngotCompressorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new IngotCompressorRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new ElectricIngotCompressorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new ElectricIngotCompressorRecipeHandler());
        API.registerHighlightIdentifier(GCBlocks.basicBlock, (IHighlightHandler)new GCNEIHighlightHandler());
        API.registerHighlightIdentifier(GCBlocks.blockMoon, (IHighlightHandler)new GCNEIHighlightHandler());
        API.registerHighlightIdentifier(GCBlocks.fakeBlock, (IHighlightHandler)new GCNEIHighlightHandler());
    }

    public String getName() {
        return "Galacticraft NEI Plugin";
    }

    public String getVersion() {
        return "3.0.12";
    }

    public void registerIngotCompressorRecipe(final HashMap<Integer, PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftConfig.ingotCompressorRecipes.put(input, output);
    }

    public void registerCircuitFabricatorRecipe(final HashMap<Integer, PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftConfig.circuitFabricatorRecipes.put(input, output);
    }

    public void registerRocketBenchRecipe(final HashMap<Integer, PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftConfig.rocketBenchRecipes.put(input, output);
    }

    public void registerBuggyBenchRecipe(final HashMap<Integer, PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftConfig.buggyBenchRecipes.put(input, output);
    }

    public void registerRefineryRecipe(final PositionedStack input, final PositionedStack output) {
        NEIGalacticraftConfig.refineryRecipes.put(input, output);
    }

    public static Set<Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getIngotCompressorRecipes() {
        return NEIGalacticraftConfig.ingotCompressorRecipes.entrySet();
    }

    public static Set<Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getCircuitFabricatorRecipes() {
        return NEIGalacticraftConfig.circuitFabricatorRecipes.entrySet();
    }

    public static Set<Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getRocketBenchRecipes() {
        return NEIGalacticraftConfig.rocketBenchRecipes.entrySet();
    }

    public static Set<Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getBuggyBenchRecipes() {
        return NEIGalacticraftConfig.buggyBenchRecipes.entrySet();
    }

    public static Set<Map.Entry<PositionedStack, PositionedStack>> getRefineryRecipes() {
        return NEIGalacticraftConfig.refineryRecipes.entrySet();
    }

    public void registerRecipes() {
        this.registerRefineryRecipe(new PositionedStack((Object)new ItemStack(GCItems.oilCanister, 1, 1), 2, 3), new PositionedStack((Object)new ItemStack(GCItems.fuelCanister, 1, 1), 148, 3));
        this.addRocketRecipes();
        this.addBuggyRecipes();
        this.addCircuitFabricatorRecipes();
        this.addIngotCompressorRecipes();
    }

    private void addBuggyRecipes() {
        HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
        input1 = new HashMap<Integer, PositionedStack>();
        input1.put(0, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 0), 18, 37));
        input1.put(1, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 0), 18, 91));
        input1.put(2, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 0), 90, 37));
        input1.put(3, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 0), 90, 91));
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 4; ++y) {
                if (x == 1 && y == 1) {
                    input1.put(y * 3 + x + 4, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 1), 36 + x * 18, 37 + y * 18));
                }
                else {
                    input1.put(y * 3 + x + 4, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 36 + x * 18, 37 + y * 18));
                }
            }
        }
        this.registerBuggyBenchRecipe(input1, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 0), 139, 101));
        HashMap<Integer, PositionedStack> input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(16, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 90, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 1), 139, 101));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(17, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 116, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 1), 139, 101));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(18, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 142, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 1), 139, 101));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(16, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 90, 8));
        input2.put(17, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 116, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 2), 139, 101));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(17, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 116, 8));
        input2.put(18, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 142, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 2), 139, 101));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(16, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 90, 8));
        input2.put(18, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 142, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 2), 139, 101));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(16, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 90, 8));
        input2.put(17, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 116, 8));
        input2.put(18, new PositionedStack((Object)new ItemStack(GCItems.partBuggy, 1, 2), 142, 8));
        this.registerBuggyBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.buggy, 1, 3), 139, 101));
    }

    private void addRocketRecipes() {
        final HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
        input1.put(0, new PositionedStack((Object)new ItemStack(GCItems.partNoseCone), 45, 15));
        input1.put(1, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 36, 33));
        input1.put(2, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 36, 51));
        input1.put(3, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 36, 69));
        input1.put(4, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 36, 87));
        input1.put(5, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 54, 33));
        input1.put(6, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 54, 51));
        input1.put(7, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 54, 69));
        input1.put(8, new PositionedStack((Object)new ItemStack(GCItems.heavyPlatingTier1), 54, 87));
        input1.put(9, new PositionedStack((Object)new ItemStack(GCItems.rocketEngine), 45, 105));
        input1.put(10, new PositionedStack((Object)new ItemStack(GCItems.partFins), 18, 87));
        input1.put(11, new PositionedStack((Object)new ItemStack(GCItems.partFins), 18, 105));
        input1.put(12, new PositionedStack((Object)new ItemStack(GCItems.partFins), 72, 87));
        input1.put(13, new PositionedStack((Object)new ItemStack(GCItems.partFins), 72, 105));
        this.registerRocketBenchRecipe(input1, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 0), 139, 92));
        HashMap<Integer, PositionedStack> input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 1), 139, 92));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(15, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 1), 139, 92));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(16, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 1), 139, 92));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        input2.put(15, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 2), 139, 92));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(15, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        input2.put(16, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 2), 139, 92));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        input2.put(16, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 2), 139, 92));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        input2.put(15, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        input2.put(16, new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.rocketTier1, 1, 3), 139, 92));
    }

    private void addCircuitFabricatorRecipes() {
        final HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
        input1.put(0, new PositionedStack((Object)new ItemStack(Items.diamond), 10, 22));
        final int siliconCount = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).size();
        final ItemStack[] silicons = new ItemStack[siliconCount];
        for (int j = 0; j < siliconCount; ++j) {
            silicons[j] = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).get(j);
        }
        input1.put(1, new PositionedStack((Object)silicons, 69, 51));
        input1.put(2, new PositionedStack((Object)silicons, 69, 69));
        input1.put(3, new PositionedStack((Object)new ItemStack(Items.redstone), 117, 51));
        input1.put(4, new PositionedStack((Object)new ItemStack(Blocks.redstone_torch), 140, 25));
        this.registerCircuitFabricatorRecipe(input1, new PositionedStack((Object)new ItemStack(GCItems.basicItem, ConfigManagerCore.quickMode ? 5 : 3, 13), 147, 91));
        HashMap<Integer, PositionedStack> input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(4, new PositionedStack((Object)new ItemStack(Items.dye, 1, 4), 140, 25));
        this.registerCircuitFabricatorRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.basicItem, 9, 12), 147, 91));
        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(4, new PositionedStack((Object)new ItemStack(Items.repeater), 140, 25));
        this.registerCircuitFabricatorRecipe(input2, new PositionedStack((Object)new ItemStack(GCItems.basicItem, ConfigManagerCore.quickMode ? 2 : 1, 14), 147, 91));
    }

    private void addIngotCompressorRecipes() {
        for (int i = 0; i < CompressorRecipes.getRecipeList().size(); ++i) {
            final HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
            final IRecipe rec = CompressorRecipes.getRecipeList().get(i);
            if (rec instanceof ShapedRecipes) {
                final ShapedRecipes recipe = (ShapedRecipes)rec;
                for (int j = 0; j < recipe.recipeItems.length; ++j) {
                    final ItemStack stack = recipe.recipeItems[j];
                    input1.put(j, new PositionedStack((Object)stack, 21 + j % 3 * 18, 26 + j / 3 * 18));
                }
            }
            else if (rec instanceof ShapelessOreRecipe) {
                final ShapelessOreRecipe recipe2 = (ShapelessOreRecipe)rec;
                for (int j = 0; j < recipe2.getInput().size(); ++j) {
                    final Object obj = recipe2.getInput().get(j);
                    input1.put(j, new PositionedStack(obj, 21 + j % 3 * 18, 26 + j / 3 * 18));
                }
            }
            final ItemStack resultItemStack = rec.getRecipeOutput();
            if (ConfigManagerCore.quickMode && resultItemStack.getItem().getUnlocalizedName(resultItemStack).contains("compressed")) {
                final ItemStack itemStack = resultItemStack;
                itemStack.stackSize *= 2;
            }
            this.registerIngotCompressorRecipe(input1, new PositionedStack((Object)resultItemStack, 140, 46));
        }
    }

    static {
        NEIGalacticraftConfig.rocketBenchRecipes = new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
        NEIGalacticraftConfig.buggyBenchRecipes = new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
        NEIGalacticraftConfig.refineryRecipes = new HashMap<PositionedStack, PositionedStack>();
        NEIGalacticraftConfig.circuitFabricatorRecipes = new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
        NEIGalacticraftConfig.ingotCompressorRecipes = new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
    }
}
