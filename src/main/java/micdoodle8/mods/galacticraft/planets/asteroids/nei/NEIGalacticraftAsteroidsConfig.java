package micdoodle8.mods.galacticraft.planets.asteroids.nei;

import codechicken.nei.*;
import codechicken.nei.recipe.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import micdoodle8.mods.galacticraft.planets.mars.nei.*;
import codechicken.nei.api.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.api.*;
import java.util.*;

public class NEIGalacticraftAsteroidsConfig implements IConfigureNEI
{
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes;
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> astroMinerRecipes;
    
    public void loadConfig() {
        this.registerRecipes();
        API.registerRecipeHandler((ICraftingHandler)new RocketT3RecipeHandler());
        API.registerUsageHandler((IUsageHandler)new RocketT3RecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new AstroMinerRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new AstroMinerRecipeHandler());
        API.registerHighlightIdentifier(AsteroidBlocks.blockBasic, (IHighlightHandler)NEIGalacticraftMarsConfig.planetsHighlightHandler);
    }
    
    public String getName() {
        return "Galacticraft Asteroids NEI Plugin";
    }
    
    public String getVersion() {
        return "3.0.12";
    }
    
    public void registerRocketBenchRecipe(final ArrayList<PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftAsteroidsConfig.rocketBenchRecipes.put(input, output);
    }
    
    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRocketBenchRecipes() {
        return NEIGalacticraftAsteroidsConfig.rocketBenchRecipes.entrySet();
    }
    
    public void registerAstroMinerRecipe(final ArrayList<PositionedStack> input, final PositionedStack output) {
        NEIGalacticraftAsteroidsConfig.astroMinerRecipes.put(input, output);
    }
    
    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getAstroMinerRecipes() {
        return NEIGalacticraftAsteroidsConfig.astroMinerRecipes.entrySet();
    }
    
    public void registerRecipes() {
        final int changeY = 15;
        ArrayList<PositionedStack> input1 = new ArrayList<PositionedStack>();
        input1.add(new PositionedStack((Object)new ItemStack((Item)AsteroidsItems.heavyNoseCone), 45, 7));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, 25));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, 43));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, 61));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, 79));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, 97));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, 25));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, 43));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, 61));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, 79));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, 97));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 1), 45, 115));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.rocketEngine, 1, 1), 18, 79));
        input1.add(new PositionedStack((Object)new ItemStack(GCItems.rocketEngine, 1, 1), 72, 79));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 2), 18, 97));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 2), 18, 115));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 2), 72, 97));
        input1.add(new PositionedStack((Object)new ItemStack(AsteroidsItems.basicItem, 1, 2), 72, 115));
        this.registerRocketBenchRecipe(input1, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 0), 139, 102));
        ArrayList<PositionedStack> input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), 139, 102));
        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 90, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 116, 0));
        input2.add(new PositionedStack((Object)new ItemStack((Block)Blocks.chest), 142, 0));
        this.registerRocketBenchRecipe(input2, new PositionedStack((Object)new ItemStack(AsteroidsItems.tier3Rocket, 1, 3), 139, 102));
        input1 = new ArrayList<PositionedStack>();
        final Collection<ItemStack> amRecipe = GalacticraftRegistry.getAstroMinerRecipes().get(0).getRecipeInput().values();
        final Iterator<ItemStack> iter = amRecipe.iterator();
        final int dx = -3;
        final int dy = -40;
        input1.add(new PositionedStack((Object)iter.next(), 27 + dx, 61 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 45 + dx, 61 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 63 + dx, 61 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 81 + dx, 61 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 16 + dx, 79 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 34 + dx, 79 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 52 + dx, 79 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 70 + dx, 79 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 88 + dx, 79 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 44 + dx, 97 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 62 + dx, 97 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 80 + dx, 97 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 8 + dx, 103 + dy));
        input1.add(new PositionedStack((Object)iter.next(), 26 + dx, 103 + dy));
        this.registerAstroMinerRecipe(input1, new PositionedStack((Object)new ItemStack(AsteroidsItems.astroMiner, 1, 0), 142 + dx, 98 + dy));
    }
    
    static {
        NEIGalacticraftAsteroidsConfig.rocketBenchRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
        NEIGalacticraftAsteroidsConfig.astroMinerRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
    }
}
