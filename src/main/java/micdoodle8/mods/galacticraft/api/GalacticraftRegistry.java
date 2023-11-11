package micdoodle8.mods.galacticraft.api;

import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.client.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.world.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.*;
import java.util.*;

public class GalacticraftRegistry
{
    private static Map<Class<? extends WorldProvider>, ITeleportType> teleportTypeMap;
    private static List<SpaceStationType> spaceStations;
    private static List<INasaWorkbenchRecipe> rocketBenchT1Recipes;
    private static List<INasaWorkbenchRecipe> buggyBenchRecipes;
    private static List<INasaWorkbenchRecipe> rocketBenchT2Recipes;
    private static List<INasaWorkbenchRecipe> cargoRocketRecipes;
    private static List<INasaWorkbenchRecipe> rocketBenchT3Recipes;
    private static List<INasaWorkbenchRecipe> astroMinerRecipes;
    private static Map<Class<? extends WorldProvider>, ResourceLocation> rocketGuiMap;
    private static Map<Integer, List<ItemStack>> dungeonLootMap;
    private static List<Integer> worldProviderIDs;
    private static List<IGameScreen> gameScreens;
    private static int maxScreenTypes;
    
    public static void registerTeleportType(final Class<? extends WorldProvider> clazz, final ITeleportType type) {
        if (!GalacticraftRegistry.teleportTypeMap.containsKey(clazz)) {
            GalacticraftRegistry.teleportTypeMap.put(clazz, type);
        }
    }
    
    public static void registerRocketGui(final Class<? extends WorldProvider> clazz, final ResourceLocation rocketGui) {
        if (!GalacticraftRegistry.rocketGuiMap.containsKey(clazz)) {
            GalacticraftRegistry.rocketGuiMap.put(clazz, rocketGui);
        }
    }
    
    public static void addDungeonLoot(final int tier, final ItemStack loot) {
        List<ItemStack> dungeonStacks = null;
        if (GalacticraftRegistry.dungeonLootMap.containsKey(tier)) {
            dungeonStacks = GalacticraftRegistry.dungeonLootMap.get(tier);
            dungeonStacks.add(loot);
        }
        else {
            dungeonStacks = new ArrayList<ItemStack>();
            dungeonStacks.add(loot);
        }
        GalacticraftRegistry.dungeonLootMap.put(tier, dungeonStacks);
    }
    
    public static void addT1RocketRecipe(final INasaWorkbenchRecipe recipe) {
        GalacticraftRegistry.rocketBenchT1Recipes.add(recipe);
    }
    
    public static void addT2RocketRecipe(final INasaWorkbenchRecipe recipe) {
        GalacticraftRegistry.rocketBenchT2Recipes.add(recipe);
    }
    
    public static void addT3RocketRecipe(final INasaWorkbenchRecipe recipe) {
        GalacticraftRegistry.rocketBenchT3Recipes.add(recipe);
    }
    
    public static void addCargoRocketRecipe(final INasaWorkbenchRecipe recipe) {
        GalacticraftRegistry.cargoRocketRecipes.add(recipe);
    }
    
    public static void addMoonBuggyRecipe(final INasaWorkbenchRecipe recipe) {
        GalacticraftRegistry.buggyBenchRecipes.add(recipe);
    }
    
    public static void addAstroMinerRecipe(final INasaWorkbenchRecipe recipe) {
        GalacticraftRegistry.astroMinerRecipes.add(recipe);
    }
    
    public static ITeleportType getTeleportTypeForDimension(Class<? extends WorldProvider> clazz) {
        if (!IGalacticraftWorldProvider.class.isAssignableFrom(clazz)) {
            clazz = (Class<? extends WorldProvider>)WorldProviderSurface.class;
        }
        return GalacticraftRegistry.teleportTypeMap.get(clazz);
    }
    
    public static void registerSpaceStation(final SpaceStationType type) {
        for (final SpaceStationType type2 : GalacticraftRegistry.spaceStations) {
            if (type2.getWorldToOrbitID() == type.getWorldToOrbitID()) {
                throw new RuntimeException("Two space station types registered with the same home planet ID: " + type.getWorldToOrbitID());
            }
        }
        GalacticraftRegistry.spaceStations.add(type);
    }
    
    public SpaceStationType getTypeFromPlanetID(final int planetID) {
        return GalacticraftRegistry.spaceStations.get(planetID);
    }
    
    public static List<SpaceStationType> getSpaceStationData() {
        return GalacticraftRegistry.spaceStations;
    }
    
    public static List<INasaWorkbenchRecipe> getRocketT1Recipes() {
        return GalacticraftRegistry.rocketBenchT1Recipes;
    }
    
    public static List<INasaWorkbenchRecipe> getRocketT2Recipes() {
        return GalacticraftRegistry.rocketBenchT2Recipes;
    }
    
    public static List<INasaWorkbenchRecipe> getRocketT3Recipes() {
        return GalacticraftRegistry.rocketBenchT3Recipes;
    }
    
    public static List<INasaWorkbenchRecipe> getCargoRocketRecipes() {
        return GalacticraftRegistry.cargoRocketRecipes;
    }
    
    public static List<INasaWorkbenchRecipe> getBuggyBenchRecipes() {
        return GalacticraftRegistry.buggyBenchRecipes;
    }
    
    public static List<INasaWorkbenchRecipe> getAstroMinerRecipes() {
        return GalacticraftRegistry.astroMinerRecipes;
    }
    
    @SideOnly(Side.CLIENT)
    public static ResourceLocation getResouceLocationForDimension(Class<? extends WorldProvider> clazz) {
        if (!IGalacticraftWorldProvider.class.isAssignableFrom(clazz)) {
            clazz = (Class<? extends WorldProvider>)WorldProviderSurface.class;
        }
        return GalacticraftRegistry.rocketGuiMap.get(clazz);
    }
    
    public static List<ItemStack> getDungeonLoot(final int tier) {
        return GalacticraftRegistry.dungeonLootMap.get(tier);
    }
    
    public static boolean registerProvider(final int id, final Class<? extends WorldProvider> provider, final boolean keepLoaded, final int defaultID) {
        final boolean flag = DimensionManager.registerProviderType(id, (Class)provider, keepLoaded);
        if (flag) {
            GalacticraftRegistry.worldProviderIDs.add(id);
            return true;
        }
        GalacticraftRegistry.worldProviderIDs.add(defaultID);
        FMLLog.severe("Could not register dimension " + id + " - does it clash with another mod?  Change the ID in config.", new Object[0]);
        return false;
    }
    
    @Deprecated
    public static void registerProvider(final int id, final Class<? extends WorldProvider> provider, final boolean keepLoaded) {
        registerProvider(id, provider, keepLoaded, 0);
    }
    
    public static int getProviderID(final int index) {
        return GalacticraftRegistry.worldProviderIDs.get(index);
    }
    
    public static int registerScreen(final IGameScreen screen) {
        GalacticraftRegistry.gameScreens.add(screen);
        ++GalacticraftRegistry.maxScreenTypes;
        screen.setFrameSize(0.098f);
        return GalacticraftRegistry.maxScreenTypes - 1;
    }
    
    public static int getMaxScreenTypes() {
        return GalacticraftRegistry.maxScreenTypes;
    }
    
    public static IGameScreen getGameScreen(final int type) {
        return GalacticraftRegistry.gameScreens.get(type);
    }
    
    static {
        GalacticraftRegistry.teleportTypeMap = new HashMap<Class<? extends WorldProvider>, ITeleportType>();
        GalacticraftRegistry.spaceStations = new ArrayList<SpaceStationType>();
        GalacticraftRegistry.rocketBenchT1Recipes = new ArrayList<INasaWorkbenchRecipe>();
        GalacticraftRegistry.buggyBenchRecipes = new ArrayList<INasaWorkbenchRecipe>();
        GalacticraftRegistry.rocketBenchT2Recipes = new ArrayList<INasaWorkbenchRecipe>();
        GalacticraftRegistry.cargoRocketRecipes = new ArrayList<INasaWorkbenchRecipe>();
        GalacticraftRegistry.rocketBenchT3Recipes = new ArrayList<INasaWorkbenchRecipe>();
        GalacticraftRegistry.astroMinerRecipes = new ArrayList<INasaWorkbenchRecipe>();
        GalacticraftRegistry.rocketGuiMap = new HashMap<Class<? extends WorldProvider>, ResourceLocation>();
        GalacticraftRegistry.dungeonLootMap = new HashMap<Integer, List<ItemStack>>();
        GalacticraftRegistry.worldProviderIDs = new ArrayList<Integer>();
        GalacticraftRegistry.gameScreens = new ArrayList<IGameScreen>();
    }
}
