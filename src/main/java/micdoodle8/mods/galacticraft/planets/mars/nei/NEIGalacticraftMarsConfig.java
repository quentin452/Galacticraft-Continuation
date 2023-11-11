package micdoodle8.mods.galacticraft.planets.mars.nei;

import codechicken.nei.*;
import codechicken.nei.recipe.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import codechicken.nei.api.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import java.util.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;

public class NEIGalacticraftMarsConfig implements IConfigureNEI
{
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes;
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> cargoBenchRecipes;
    private static HashMap<PositionedStack, PositionedStack> liquefierRecipes;
    private static HashMap<PositionedStack, PositionedStack> synthesizerRecipes;
    public static GCMarsNEIHighlightHandler planetsHighlightHandler;
    
    public void loadConfig() {
        this.registerRecipes();
        API.registerRecipeHandler((ICraftingHandler)new RocketT2RecipeHandler());
        API.registerUsageHandler((IUsageHandler)new RocketT2RecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new CargoRocketRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new CargoRocketRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new GasLiquefierRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new GasLiquefierRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new MethaneSynthesizerRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new MethaneSynthesizerRecipeHandler());
        API.registerHighlightIdentifier(MarsBlocks.marsBlock, (IHighlightHandler)NEIGalacticraftMarsConfig.planetsHighlightHandler);
    }
    
    public String getName() {
        return "Galacticraft Mars NEI Plugin";
    }
    
    public String getVersion() {
        return "3.0.12";
    }
    
    public void registerRocketBenchRecipe(final ArrayList<PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftMarsConfig.rocketBenchRecipes.put(input, output);
    }
    
    public void registerCargoBenchRecipe(final ArrayList<PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftMarsConfig.cargoBenchRecipes.put(input, output);
    }
    
    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRocketBenchRecipes() {
        return NEIGalacticraftMarsConfig.rocketBenchRecipes.entrySet();
    }
    
    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getCargoBenchRecipes() {
        return NEIGalacticraftMarsConfig.cargoBenchRecipes.entrySet();
    }
    
    private void registerLiquefierRecipe(final PositionedStack inputStack, final PositionedStack outputStack) {
        NEIGalacticraftMarsConfig.liquefierRecipes.put(inputStack, outputStack);
    }
    
    public static Set<Map.Entry<PositionedStack, PositionedStack>> getLiquefierRecipes() {
        return NEIGalacticraftMarsConfig.liquefierRecipes.entrySet();
    }
    
    private void registerSynthesizerRecipe(final PositionedStack inputStack, final PositionedStack outputStack) {
        NEIGalacticraftMarsConfig.synthesizerRecipes.put(inputStack, outputStack);
    }
    
    public static Set<Map.Entry<PositionedStack, PositionedStack>> getSynthesizerRecipes() {
        return NEIGalacticraftMarsConfig.synthesizerRecipes.entrySet();
    }
    
    public void registerRecipes() {
        final int changeY = 15;
        ArrayList<PositionedStack> input1 = new ArrayList<PositionedStack>();
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partNoseCone), 45, 7));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 25));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 43));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 61));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 79));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 97));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 25));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 43));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 61));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 79));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 97));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.rocketEngine), 45, 115));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.rocketEngine, 1, 1), 18, 79));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.rocketEngine, 1, 1), 72, 79));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 18, 97));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 18, 115));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 72, 97));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 72, 115));
        this.registerRocketBenchRecipe(input1, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 0), 139, 102));
        ArrayList<PositionedStack> input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 1), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 1), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 1), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 2), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 2), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 2), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 3), 139, 102));
        input1 = new ArrayList<PositionedStack>();
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partNoseCone), 45, 14));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.basicItem, 1, 14), 45, 32));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 50));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 68));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, 86));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 50));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 68));
        input1.add(new PositionedStack((Object)new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, 86));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 18, 86));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 72, 86));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.rocketEngine), 45, 104));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 18, 104));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.partFins), 72, 104));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 11), 139, 92));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 11), 139, 92));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 11), 139, 92));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 12), 139, 92));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 12), 139, 92));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 12), 139, 92));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 8));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 8));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 8));
        this.registerCargoBenchRecipe(input2, new PositionedStack((Object)new ItemStack(MarsItems.spaceship, 1, 13), 139, 92));
        this.registerLiquefierRecipe(new PositionedStack((Object)new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 2, 3), new PositionedStack((Object)new ItemStack(GCItems.fuelCanister, 1, 1), 127, 3));
        this.registerLiquefierRecipe(new PositionedStack((Object)new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 2, 3), new PositionedStack((Object)new ItemStack(AsteroidsItems.canisterLN2, 1, 1), 127, 3));
        this.registerLiquefierRecipe(new PositionedStack((Object)new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 2, 3), new PositionedStack((Object)new ItemStack(AsteroidsItems.canisterLOX, 1, 1), 148, 3));
        this.registerLiquefierRecipe(new PositionedStack((Object)new ItemStack(AsteroidsItems.canisterLN2, 1, 501), 2, 3), new PositionedStack((Object)new ItemStack(AsteroidsItems.canisterLN2, 1, 1), 127, 3));
        this.registerLiquefierRecipe(new PositionedStack((Object)new ItemStack(AsteroidsItems.canisterLOX, 1, 501), 2, 3), new PositionedStack((Object)new ItemStack(AsteroidsItems.canisterLOX, 1, 1), 148, 3));
        this.registerSynthesizerRecipe(new PositionedStack((Object)new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 23, 3), new PositionedStack((Object)new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 148, 3));
        this.registerSynthesizerRecipe(new PositionedStack((Object)new ItemStack(MarsItems.carbonFragments, 25, 0), 23, 49), new PositionedStack((Object)new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 148, 3));
    }
    
    static {
        NEIGalacticraftMarsConfig.rocketBenchRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
        NEIGalacticraftMarsConfig.cargoBenchRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
        NEIGalacticraftMarsConfig.liquefierRecipes = new HashMap<PositionedStack, PositionedStack>();
        NEIGalacticraftMarsConfig.synthesizerRecipes = new HashMap<PositionedStack, PositionedStack>();
        NEIGalacticraftMarsConfig.planetsHighlightHandler = new GCMarsNEIHighlightHandler();
    }
}
