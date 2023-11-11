package micdoodle8.mods.galacticraft.core.util;

import java.io.*;
import cpw.mods.fml.common.registry.*;
import net.minecraft.init.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.*;
import com.google.common.primitives.*;
import java.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.*;
import cpw.mods.fml.client.config.*;
import net.minecraftforge.common.config.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.core.recipe.*;
import cpw.mods.fml.relauncher.*;

public class ConfigManagerCore
{
    static Configuration config;
    public static boolean forceOverworldRespawn;
    public static boolean hardMode;
    public static boolean quickMode;
    public static boolean challengeMode;
    private static int challengeFlags;
    public static boolean challengeRecipes;
    public static boolean challengeMobDropsAndSpawning;
    public static boolean challengeSpawnHandling;
    public static boolean challengeAsteroidPopulation;
    public static boolean disableRocketsToOverworld;
    public static boolean disableSpaceStationCreation;
    public static boolean spaceStationsRequirePermission;
    public static boolean disableUpdateCheck;
    public static boolean enableSpaceRaceManagerPopup;
    public static boolean enableDebug;
    public static boolean enableSealerEdgeChecks;
    public static boolean disableLander;
    public static boolean recipesRequireGCAdvancedMetals;
    public static int idDimensionOverworld;
    public static int idDimensionOverworldOrbit;
    public static int idDimensionOverworldOrbitStatic;
    public static int idDimensionMoon;
    public static int biomeIDbase;
    public static boolean disableBiomeTypeRegistrations;
    public static int[] staticLoadDimensions;
    public static int[] disableRocketLaunchDimensions;
    public static boolean disableRocketLaunchAllNonGC;
    public static int otherPlanetWorldBorders;
    public static boolean keepLoadedNewSpaceStations;
    public static int idSchematicRocketT1;
    public static int idSchematicMoonBuggy;
    public static int idSchematicAddSchematic;
    public static int idAchievBase;
    public static boolean moreStars;
    public static boolean disableSpaceshipParticles;
    public static boolean disableVehicleCameraChanges;
    public static boolean oxygenIndicatorLeft;
    public static boolean oxygenIndicatorBottom;
    public static boolean overrideCapes;
    public static double dungeonBossHealthMod;
    public static int suffocationCooldown;
    public static int suffocationDamage;
    public static int rocketFuelFactor;
    public static double meteorSpawnMod;
    public static boolean meteorBlockDamageEnabled;
    public static boolean disableSpaceshipGrief;
    public static double spaceStationEnergyScalar;
    public static boolean enableCopperOreGen;
    public static boolean enableTinOreGen;
    public static boolean enableAluminumOreGen;
    public static boolean enableSiliconOreGen;
    public static boolean disableCheeseMoon;
    public static boolean disableTinMoon;
    public static boolean disableCopperMoon;
    public static boolean disableMoonVillageGen;
    public static int[] externalOilGen;
    public static double oilGenFactor;
    public static boolean retrogenOil;
    public static String[] oregenIDs;
    public static boolean enableOtherModsFeatures;
    public static boolean whitelistCoFHCoreGen;
    public static boolean enableThaumCraftNodes;
    public static String[] sealableIDs;
    public static String[] detectableIDs;
    public static boolean alternateCanisterRecipe;
    public static String otherModsSilicon;
    public static boolean useOldOilFluidID;
    public static boolean useOldFuelFluidID;
    public static String keyOverrideMap;
    public static String keyOverrideFuelLevel;
    public static String keyOverrideToggleAdvGoggles;
    public static int keyOverrideMapI;
    public static int keyOverrideFuelLevelI;
    public static int keyOverrideToggleAdvGogglesI;
    public static float mapMouseScrollSensitivity;
    public static boolean invertMapMouseScroll;
    public static ArrayList<Object> clientSave;

    public static void initialize(final File file) {
        ConfigManagerCore.config = new Configuration(file);
        syncConfig(true);
    }

    public static void forceSave() {
        ConfigManagerCore.config.save();
    }

    public static void syncConfig(final boolean load) {
        final List<String> propOrder = new ArrayList<String>();
        try {
            if (!ConfigManagerCore.config.isChild && load) {
                ConfigManagerCore.config.load();
            }
            Property prop = ConfigManagerCore.config.get("general", "Enable Debug Messages", false);
            prop.comment = "If this is enabled, debug messages will appear in the console. This is useful for finding bugs in the mod.";
            prop.setLanguageKey("gc.configgui.enableDebug");
            ConfigManagerCore.enableDebug = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "idDimensionOverworld", 0);
            prop.comment = "Dimension ID for the Overworld (as seen in the Celestial Map)";
            prop.setLanguageKey("gc.configgui.idDimensionOverworld").setRequiresMcRestart(true);
            ConfigManagerCore.idDimensionOverworld = prop.getInt();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "idDimensionMoon", -28);
            prop.comment = "Dimension ID for the Moon";
            prop.setLanguageKey("gc.configgui.idDimensionMoon").setRequiresMcRestart(true);
            ConfigManagerCore.idDimensionMoon = prop.getInt();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "idDimensionOverworldOrbit", -27);
            prop.comment = "WorldProvider ID for Overworld Space Stations (advanced: do not change unless you have conflicts)";
            prop.setLanguageKey("gc.configgui.idDimensionOverworldOrbit").setRequiresMcRestart(true);
            ConfigManagerCore.idDimensionOverworldOrbit = prop.getInt();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "idDimensionOverworldOrbitStatic", -26);
            prop.comment = "WorldProvider ID for Static Space Stations (advanced: do not change unless you have conflicts)";
            prop.setLanguageKey("gc.configgui.idDimensionOverworldOrbitStatic").setRequiresMcRestart(true);
            ConfigManagerCore.idDimensionOverworldOrbitStatic = prop.getInt();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "biomeIDBase", 102);
            prop.comment = "Biome ID for Moon (Mars will be this + 1, Asteroids + 2 etc). Allowed range 40-250.";
            prop.setLanguageKey("gc.configgui.biomeIDBase").setRequiresMcRestart(true);
            ConfigManagerCore.biomeIDbase = prop.getInt();
            if (ConfigManagerCore.biomeIDbase < 40 || ConfigManagerCore.biomeIDbase > 250) {
                ConfigManagerCore.biomeIDbase = 102;
            }
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "Static Loaded Dimensions", ConfigManagerCore.staticLoadDimensions);
            prop.comment = "IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded";
            prop.setLanguageKey("gc.configgui.staticLoadedDimensions");
            ConfigManagerCore.staticLoadDimensions = prop.getIntList();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "Set new Space Stations to be static loaded", false);
            prop.comment = "Set this to true to have an automatic /gckeeploaded for any new Space Station created.";
            prop.setLanguageKey("gc.configgui.staticLoadedNewSS");
            ConfigManagerCore.keepLoadedNewSpaceStations = prop.getBoolean();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "Dimensions where rockets cannot launch", new String[] { "1", "-1" });
            prop.comment = "IDs of dimensions where rockets should not launch - this should always include the Nether.";
            prop.setLanguageKey("gc.configgui.rocketDisabledDimensions");
            ConfigManagerCore.disableRocketLaunchDimensions = prop.getIntList();
            ConfigManagerCore.disableRocketLaunchAllNonGC = searchAsterisk(prop.getStringList());
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "Disable rockets from returning to Overworld", false);
            prop.comment = "If true, rockets will be unable to reach the Overworld (only use this in special modpacks!)";
            prop.setLanguageKey("gc.configgui.rocketDisableOverworldReturn");
            ConfigManagerCore.disableRocketsToOverworld = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("dimensions", "World border for landing location on other planets (Moon, Mars, etc)", 0);
            prop.comment = "Set this to 0 for no borders (default).  If set to e.g. 2000, players will land on the Moon inside the x,z range -2000 to 2000.)";
            prop.setLanguageKey("gc.configgui.planetWorldBorders");
            ConfigManagerCore.otherPlanetWorldBorders = prop.getInt(0);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Force Overworld Spawn", false);
            prop.comment = "By default, you will respawn on galacticraft dimensions if you die. If you set this to true, you will respawn back on earth.";
            prop.setLanguageKey("gc.configgui.forceOverworldRespawn");
            ConfigManagerCore.forceOverworldRespawn = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("schematic", "idSchematicRocketT1", 0);
            prop.comment = "Schematic ID for Tier 1 Rocket, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicRocketT1");
            ConfigManagerCore.idSchematicRocketT1 = prop.getInt(0);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("schematic", "idSchematicMoonBuggy", 1);
            prop.comment = "Schematic ID for Moon Buggy, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicMoonBuggy");
            ConfigManagerCore.idSchematicMoonBuggy = prop.getInt(1);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("schematic", "idSchematicAddSchematic", Integer.MAX_VALUE);
            prop.comment = "Schematic ID for \"Add Schematic\" Page, must be unique";
            prop.setLanguageKey("gc.configgui.idSchematicAddSchematic");
            ConfigManagerCore.idSchematicAddSchematic = prop.getInt(Integer.MAX_VALUE);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("achievements", "idAchievBase", 1784);
            prop.comment = "Base Achievement ID. All achievement IDs will start at this number.";
            prop.setLanguageKey("gc.configgui.idAchievBase");
            ConfigManagerCore.idAchievBase = prop.getInt(1784);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "More Stars", true);
            prop.comment = "Setting this to false will revert night skies back to default minecraft star count";
            prop.setLanguageKey("gc.configgui.moreStars");
            ConfigManagerCore.moreStars = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Spaceship Particles", false);
            prop.comment = "If you have FPS problems, setting this to true will help if rocket particles are in your sights";
            prop.setLanguageKey("gc.configgui.disableSpaceshipParticles");
            ConfigManagerCore.disableSpaceshipParticles = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Vehicle Third-Person and Zoom", false);
            prop.comment = "If you're using this mod in virtual reality, or if you don't want the camera changes when entering a Galacticraft vehicle, set this to true.";
            prop.setLanguageKey("gc.configgui.disableVehicleCameraChanges");
            ConfigManagerCore.disableVehicleCameraChanges = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Minimap Left", false);
            prop.comment = "If true, this will move the Oxygen Indicator to the left side. You can combine this with \"Minimap Bottom\"";
            prop.setLanguageKey("gc.configgui.oxygenIndicatorLeft");
            ConfigManagerCore.oxygenIndicatorLeft = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Minimap Bottom", false);
            prop.comment = "If true, this will move the Oxygen Indicator to the bottom. You can combine this with \"Minimap Left\"";
            prop.setLanguageKey("gc.configgui.oxygenIndicatorBottom");
            ConfigManagerCore.oxygenIndicatorBottom = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Oil Generation Factor", 1.8);
            prop.comment = "Increasing this will increase amount of oil that will generate in each chunk.";
            prop.setLanguageKey("gc.configgui.oilGenFactor");
            ConfigManagerCore.oilGenFactor = prop.getDouble(1.8);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Oil gen in external dimensions", new int[] { 0 });
            prop.comment = "List of non-galacticraft dimension IDs to generate oil in.";
            prop.setLanguageKey("gc.configgui.externalOilGen");
            ConfigManagerCore.externalOilGen = prop.getIntList();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Retro Gen of GC Oil in existing map chunks", false);
            prop.comment = "If this is enabled, GC oil will be added to existing Overworld maps where possible.";
            prop.setLanguageKey("gc.configgui.enableRetrogenOil");
            ConfigManagerCore.retrogenOil = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Enable Copper Ore Gen", true);
            prop.comment = "If this is enabled, copper ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableCopperOreGen").setRequiresMcRestart(true);
            ConfigManagerCore.enableCopperOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Enable Tin Ore Gen", true);
            prop.comment = "If this is enabled, tin ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableTinOreGen").setRequiresMcRestart(true);
            ConfigManagerCore.enableTinOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Enable Aluminum Ore Gen", true);
            prop.comment = "If this is enabled, aluminum ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableAluminumOreGen").setRequiresMcRestart(true);
            ConfigManagerCore.enableAluminumOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Enable Silicon Ore Gen", true);
            prop.comment = "If this is enabled, silicon ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableSiliconOreGen").setRequiresMcRestart(true);
            ConfigManagerCore.enableSiliconOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Cheese Ore Gen on Moon", false);
            prop.comment = "Disable Cheese Ore Gen on Moon.";
            prop.setLanguageKey("gc.configgui.disableCheeseMoon");
            ConfigManagerCore.disableCheeseMoon = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Tin Ore Gen on Moon", false);
            prop.comment = "Disable Tin Ore Gen on Moon.";
            prop.setLanguageKey("gc.configgui.disableTinMoon");
            ConfigManagerCore.disableTinMoon = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Copper Ore Gen on Moon", false);
            prop.comment = "Disable Copper Ore Gen on Moon.";
            prop.setLanguageKey("gc.configgui.disableCopperMoon");
            ConfigManagerCore.disableCopperMoon = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Moon Village Gen", false);
            prop.comment = "If true, moon villages will not generate.";
            prop.setLanguageKey("gc.configgui.disableMoonVillageGen");
            ConfigManagerCore.disableMoonVillageGen = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Generate all other mods features on planets", false);
            prop.comment = "If this is enabled, other mods' standard ores and all other features (eg. plants) can generate on the Moon and planets. Apart from looking wrong, this make cause 'Already Decorating!' type crashes.  NOT RECOMMENDED!  See Wiki.";
            prop.setLanguageKey("gc.configgui.enableOtherModsFeatures");
            ConfigManagerCore.enableOtherModsFeatures = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Whitelist CoFHCore worldgen to generate its ores and lakes on planets", false);
            prop.comment = "If generate other mods features is disabled as recommended, this setting can whitelist CoFHCore custom worldgen on planets.";
            prop.setLanguageKey("gc.configgui.whitelistCoFHCoreGen");
            ConfigManagerCore.whitelistCoFHCoreGen = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Generate ThaumCraft wild nodes on planetary surfaces", true);
            prop.comment = "If ThaumCraft is installed, ThaumCraft wild nodes can generate on the Moon and planets.";
            prop.setLanguageKey("gc.configgui.enableThaumCraftNodes");
            ConfigManagerCore.enableThaumCraftNodes = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Other mods ores for GC to generate on the Moon and planets", new String[0]);
            prop.comment = "Enter IDs of other mods' ores here for Galacticraft to generate them on the Moon and other planets. Format is BlockName or BlockName:metadata. Use optional parameters at end of each line: /RARE /UNCOMMON or /COMMON for rarity in a chunk; /DEEP /SHALLOW or /BOTH for height; /SINGLE /STANDARD or /LARGE for clump size; /XTRARANDOM for ores sometimes there sometimes not at all.  /ONLYMOON or /ONLYMARS if wanted on one planet only.  If nothing specified, defaults are /COMMON, /BOTH and /STANDARD.  Repeat lines to generate a huge quantity of ores.";
            prop.setLanguageKey("gc.configgui.otherModOreGenIDs");
            ConfigManagerCore.oregenIDs = prop.getStringList();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Use legacy oilgc fluid registration", false);
            prop.comment = "Set to true to make Galacticraft oil register as oilgc, for backwards compatibility with previously generated worlds.";
            prop.setLanguageKey("gc.configgui.useOldOilFluidID");
            ConfigManagerCore.useOldOilFluidID = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Use legacy fuelgc fluid registration", false);
            prop.comment = "Set to true to make Galacticraft fuel register as fuelgc, for backwards compatibility with previously generated worlds.";
            prop.setLanguageKey("gc.configgui.useOldFuelFluidID");
            ConfigManagerCore.useOldFuelFluidID = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable lander on Moon and other planets", false);
            prop.comment = "If this is true, the player will parachute onto the Moon instead - use only in debug situations.";
            prop.setLanguageKey("gc.configgui.disableLander");
            ConfigManagerCore.disableLander = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Spaceship Explosion", false);
            prop.comment = "Spaceships will not explode on contact if set to true.";
            prop.setLanguageKey("gc.configgui.disableSpaceshipGrief");
            ConfigManagerCore.disableSpaceshipGrief = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Space Stations Require Permission", true);
            prop.comment = "While true, space stations require you to invite other players using /ssinvite <playername>";
            prop.setLanguageKey("gc.configgui.spaceStationsRequirePermission");
            ConfigManagerCore.spaceStationsRequirePermission = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Space Station creation", false);
            prop.comment = "If set to true on a server, players will be completely unable to create space stations.";
            prop.setLanguageKey("gc.configgui.disableSpaceStationCreation");
            ConfigManagerCore.disableSpaceStationCreation = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Override Capes", true);
            prop.comment = "By default, Galacticraft will override capes with the mod's donor cape. Set to false to disable.";
            prop.setLanguageKey("gc.configgui.overrideCapes");
            ConfigManagerCore.overrideCapes = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Space Station Solar Energy Multiplier", 2.0);
            prop.comment = "Solar panels will work (default 2x) more effective on space stations.";
            prop.setLanguageKey("gc.configgui.spaceStationEnergyScalar");
            ConfigManagerCore.spaceStationEnergyScalar = prop.getDouble(2.0);
            propOrder.add(prop.getName());
            try {
                prop = ConfigManagerCore.config.get("general", "External Sealable IDs", new String[] { GameData.getBlockRegistry().getNameForObject((Object)Blocks.glass_pane) + ":0" });
                prop.comment = "List non-opaque blocks from other mods (for example, special types of glass) that the Oxygen Sealer should recognize as solid seals. Format is BlockName or BlockName:metadata";
                prop.setLanguageKey("gc.configgui.sealableIDs").setRequiresMcRestart(true);
                ConfigManagerCore.sealableIDs = prop.getStringList();
                propOrder.add(prop.getName());
            }
            catch (Exception e) {
                FMLLog.severe("[Galacticraft] It appears you have installed the 'Dev' version of Galacticraft instead of the regular version (or vice versa).  Please re-install.", new Object[0]);
            }
            prop = ConfigManagerCore.config.get("general", "External Detectable IDs", new String[] { GameData.getBlockRegistry().getNameForObject((Object)Blocks.coal_ore), GameData.getBlockRegistry().getNameForObject((Object)Blocks.diamond_ore), GameData.getBlockRegistry().getNameForObject((Object)Blocks.gold_ore), GameData.getBlockRegistry().getNameForObject((Object)Blocks.iron_ore), GameData.getBlockRegistry().getNameForObject((Object)Blocks.lapis_ore), GameData.getBlockRegistry().getNameForObject((Object)Blocks.redstone_ore), GameData.getBlockRegistry().getNameForObject((Object)Blocks.lit_redstone_ore) });
            prop.comment = "List blocks from other mods that the Sensor Glasses should recognize as solid blocks. Format is BlockName or BlockName:metadata.";
            prop.setLanguageKey("gc.configgui.detectableIDs").setRequiresMcRestart(true);
            ConfigManagerCore.detectableIDs = prop.getStringList();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Suffocation Cooldown", 100);
            prop.comment = "Lower/Raise this value to change time between suffocation damage ticks";
            prop.setLanguageKey("gc.configgui.suffocationCooldown");
            ConfigManagerCore.suffocationCooldown = prop.getInt(100);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Suffocation Damage", 2);
            prop.comment = "Change this value to modify the damage taken per suffocation tick";
            prop.setLanguageKey("gc.configgui.suffocationDamage");
            ConfigManagerCore.suffocationDamage = prop.getInt(2);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Dungeon Boss Health Modifier", 1.0);
            prop.comment = "Change this if you wish to balance the mod (if you have more powerful weapon mods).";
            prop.setLanguageKey("gc.configgui.dungeonBossHealthMod");
            ConfigManagerCore.dungeonBossHealthMod = prop.getDouble(1.0);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Harder Difficulty", false);
            prop.comment = "Set this to true for increased difficulty in modpacks (see forum for more info).";
            prop.setLanguageKey("gc.configgui.hardMode");
            ConfigManagerCore.hardMode = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Quick Game Mode", false);
            prop.comment = "Set this to true for less metal use in Galacticraft recipes (makes the game easier).";
            prop.setLanguageKey("gc.configgui.quickMode");
            ConfigManagerCore.quickMode = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Adventure Game Mode", false);
            prop.comment = "Set this to true for a challenging adventure where the player starts the game stranded in the Asteroids dimension with low resources (only effective if Galacticraft Planets installed).";
            prop.setLanguageKey("gc.configgui.asteroidsStart");
            ConfigManagerCore.challengeMode = prop.getBoolean(false);
            if (!GalacticraftCore.isPlanetsLoaded) {
                ConfigManagerCore.challengeMode = false;
            }
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Adventure Game Mode Flags", 15);
            prop.comment = "Add together flags 8, 4, 2, 1 to enable the four elements of adventure game mode. Default 15.  1 = extended compressor recipes.  2 = mob drops and spawning.  4 = more trees in hollow asteroids.  8 = start stranded in Asteroids.";
            prop.setLanguageKey("gc.configgui.asteroidsFlags");
            ConfigManagerCore.challengeFlags = prop.getInt(15);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Enable Sealed edge checks", true);
            prop.comment = "If this is enabled, areas sealed by Oxygen Sealers will run a seal check when the player breaks or places a block (or on block updates).  This should be enabled for a 100% accurate sealed status, but can be disabled on servers for performance reasons.";
            prop.setLanguageKey("gc.configgui.enableSealerEdgeChecks");
            ConfigManagerCore.enableSealerEdgeChecks = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Alternate recipe for canisters", false);
            prop.comment = "Enable this if the standard canister recipe causes a conflict.";
            prop.setLanguageKey("gc.configgui.alternateCanisterRecipe").setRequiresMcRestart(true);
            ConfigManagerCore.alternateCanisterRecipe = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "OreDict name of other mod's silicon usable in Galacticraft", "itemSilicon");
            prop.comment = "This needs to match the OreDictionary name used in the other mod. Set a nonsense name to disable.";
            prop.setLanguageKey("gc.configgui.oreDictSilicon").setRequiresMcRestart(true);
            ConfigManagerCore.otherModsSilicon = prop.getString();
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Must use GC's own space metals in recipes", true);
            prop.comment = "Should normally be true. If you set this to false, in a modpack with other mods with the same metals, players may be able to craft advanced GC items without travelling to Moon, Mars, Asteroids etc.";
            prop.setLanguageKey("gc.configgui.disableOreDictSpaceMetals").setRequiresMcRestart(true);
            ConfigManagerCore.recipesRequireGCAdvancedMetals = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Open Galaxy Map", "KEY_M");
            prop.comment = "Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted";
            prop.setLanguageKey("gc.configgui.overrideMap").setRequiresMcRestart(true);
            ConfigManagerCore.keyOverrideMap = prop.getString();
            ConfigManagerCore.keyOverrideMapI = parseKeyValue(ConfigManagerCore.keyOverrideMap);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Open Fuel GUI", "KEY_F");
            prop.comment = "Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted";
            prop.setLanguageKey("gc.configgui.keyOverrideFuelLevel").setRequiresMcRestart(true);
            ConfigManagerCore.keyOverrideFuelLevel = prop.getString();
            ConfigManagerCore.keyOverrideFuelLevelI = parseKeyValue(ConfigManagerCore.keyOverrideFuelLevel);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Toggle Advanced Goggles", "KEY_K");
            prop.comment = "Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted";
            prop.setLanguageKey("gc.configgui.keyOverrideToggleAdvGoggles").setRequiresMcRestart(true);
            ConfigManagerCore.keyOverrideToggleAdvGoggles = prop.getString();
            ConfigManagerCore.keyOverrideToggleAdvGogglesI = parseKeyValue(ConfigManagerCore.keyOverrideToggleAdvGoggles);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Rocket fuel factor", 1);
            prop.comment = "The normal factor is 1.  Increase this to 2 - 5 if other mods with a lot of oil (e.g. BuildCraft) are installed to increase GC rocket fuel requirement.";
            prop.setLanguageKey("gc.configgui.rocketFuelFactor");
            ConfigManagerCore.rocketFuelFactor = prop.getInt(1);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Map Scroll Mouse Sensitivity", 1.0);
            prop.comment = "Increase to make the mouse drag scroll more sensitive, decrease to lower sensitivity.";
            prop.setLanguageKey("gc.configgui.mapScrollSensitivity");
            ConfigManagerCore.mapMouseScrollSensitivity = (float)prop.getDouble(1.0);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Map Scroll Mouse Invert", false);
            prop.comment = "Set to true to invert the mouse scroll feature on the galaxy map.";
            prop.setLanguageKey("gc.configgui.mapScrollInvert");
            ConfigManagerCore.invertMapMouseScroll = prop.getBoolean(false);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Meteor Spawn Modifier", 1.0);
            prop.comment = "Set to a value between 0.0 and 1.0 to decrease meteor spawn chance (all dimensions).";
            prop.setLanguageKey("gc.configgui.meteorSpawnMod");
            ConfigManagerCore.meteorSpawnMod = prop.getDouble(1.0);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Meteor Block Damage Enabled", true);
            prop.comment = "Set to false to stop meteors from breaking blocks on contact.";
            prop.setLanguageKey("gc.configgui.meteorBlockDamage");
            ConfigManagerCore.meteorBlockDamageEnabled = prop.getBoolean(true);
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Disable Update Check", false);
            prop.comment = "Update check will not run if this is set to true.";
            prop.setLanguageKey("gc.configgui.disableUpdateCheck");
            ConfigManagerCore.disableUpdateCheck = prop.getBoolean(false);
            propOrder.add(prop.getName());
            final boolean thisIsMC172 = VersionUtil.mcVersion1_7_2;
            prop = ConfigManagerCore.config.get("general", "Disable Biome Type Registrations", thisIsMC172);
            prop.comment = "Biome Types will not be registered in the BiomeDictionary if this is set to true. Ignored (always true) for MC 1.7.2.";
            prop.setLanguageKey("gc.configgui.disableBiomeTypeRegistrations");
            ConfigManagerCore.disableBiomeTypeRegistrations = prop.getBoolean(thisIsMC172);
            if (thisIsMC172) {
                ConfigManagerCore.disableBiomeTypeRegistrations = true;
            }
            propOrder.add(prop.getName());
            prop = ConfigManagerCore.config.get("general", "Enable Space Race Manager Popup", false);
            prop.comment = "Space Race Manager will show on-screen after login, if enabled.";
            prop.setLanguageKey("gc.configgui.enableSpaceRaceManagerPopup");
            ConfigManagerCore.enableSpaceRaceManagerPopup = prop.getBoolean(false);
            propOrder.add(prop.getName());
            ConfigManagerCore.config.setCategoryPropertyOrder("general", (List)propOrder);
            if (ConfigManagerCore.config.hasChanged()) {
                ConfigManagerCore.config.save();
            }
            challengeModeUpdate();
        }
        catch (Exception e2) {
            GCLog.severe("Problem loading core config (\"core.conf\")");
        }
    }

    public static boolean setLoaded(final int newID) {
        boolean found = false;
        for (final int staticLoadDimension : ConfigManagerCore.staticLoadDimensions) {
            if (staticLoadDimension == newID) {
                found = true;
                break;
            }
        }
        if (!found) {
            final int[] oldIDs = ConfigManagerCore.staticLoadDimensions;
            System.arraycopy(oldIDs, 0, ConfigManagerCore.staticLoadDimensions = new int[ConfigManagerCore.staticLoadDimensions.length + 1], 0, oldIDs.length);
            ConfigManagerCore.staticLoadDimensions[ConfigManagerCore.staticLoadDimensions.length - 1] = newID;
            final String[] values = new String[ConfigManagerCore.staticLoadDimensions.length];
            Arrays.sort(ConfigManagerCore.staticLoadDimensions);
            for (int i = 0; i < values.length; ++i) {
                values[i] = String.valueOf(ConfigManagerCore.staticLoadDimensions[i]);
            }
            final Property prop = ConfigManagerCore.config.get("dimensions", "Static Loaded Dimensions", ConfigManagerCore.staticLoadDimensions);
            prop.comment = "IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded";
            prop.setLanguageKey("gc.configgui.staticLoadedDimensions");
            prop.set(values);
            ConfigManagerCore.config.save();
        }
        return !found;
    }

    public static boolean setUnloaded(final int idToRemove) {
        int foundCount = 0;
        for (final int staticLoadDimension : ConfigManagerCore.staticLoadDimensions) {
            if (staticLoadDimension == idToRemove) {
                ++foundCount;
            }
        }
        if (foundCount > 0) {
            final List<Integer> idArray = new ArrayList<Integer>(Ints.asList(ConfigManagerCore.staticLoadDimensions));
            idArray.removeAll(Collections.singleton(idToRemove));
            ConfigManagerCore.staticLoadDimensions = new int[idArray.size()];
            for (int i = 0; i < idArray.size(); ++i) {
                ConfigManagerCore.staticLoadDimensions[i] = idArray.get(i);
            }
            final String[] values = new String[ConfigManagerCore.staticLoadDimensions.length];
            Arrays.sort(ConfigManagerCore.staticLoadDimensions);
            for (int j = 0; j < values.length; ++j) {
                values[j] = String.valueOf(ConfigManagerCore.staticLoadDimensions[j]);
            }
            final Property prop = ConfigManagerCore.config.get("dimensions", "Static Loaded Dimensions", ConfigManagerCore.staticLoadDimensions);
            prop.comment = "IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded";
            prop.setLanguageKey("gc.configgui.staticLoadedDimensions");
            prop.set(values);
            ConfigManagerCore.config.save();
        }
        return foundCount > 0;
    }

    private static void challengeModeUpdate() {
        if (ConfigManagerCore.challengeMode) {
            ConfigManagerCore.challengeRecipes = ((ConfigManagerCore.challengeFlags & 0x1) > 0);
            ConfigManagerCore.challengeMobDropsAndSpawning = ((ConfigManagerCore.challengeFlags & 0x2) > 0);
            ConfigManagerCore.challengeAsteroidPopulation = ((ConfigManagerCore.challengeFlags & 0x4) > 0);
            ConfigManagerCore.challengeSpawnHandling = ((ConfigManagerCore.challengeFlags & 0x8) > 0);
        }
        else {
            ConfigManagerCore.challengeRecipes = false;
            ConfigManagerCore.challengeMobDropsAndSpawning = false;
            ConfigManagerCore.challengeAsteroidPopulation = false;
            ConfigManagerCore.challengeSpawnHandling = false;
        }
        if (GalacticraftCore.isPlanetsLoaded) {
            ((BiomeGenBaseAsteroids)BiomeGenBaseAsteroids.asteroid).resetMonsterListByMode(ConfigManagerCore.challengeMobDropsAndSpawning);
        }
    }

    private static boolean searchAsterisk(final String[] strings) {
        for (final String s : strings) {
            if (s != null && "*".equals(s.trim())) {
                return true;
            }
        }
        return false;
    }

    public static List<IConfigElement> getConfigElements() {
        final List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.addAll(new ConfigElement(ConfigManagerCore.config.getCategory("dimensions")).getChildElements());
        list.addAll(new ConfigElement(ConfigManagerCore.config.getCategory("schematic")).getChildElements());
        list.addAll(new ConfigElement(ConfigManagerCore.config.getCategory("achievements")).getChildElements());
        list.addAll(new ConfigElement(ConfigManagerCore.config.getCategory("entities")).getChildElements());
        list.addAll(new ConfigElement(ConfigManagerCore.config.getCategory("general")).getChildElements());
        return list;
    }

    public static BlockTuple stringToBlock(final String s, final String caller, final boolean logging) {
        final int lastColon = s.lastIndexOf(58);
        int meta = -1;
        if (lastColon > 0) {
            try {
                meta = Integer.parseInt(s.substring(lastColon + 1, s.length()));
            }
            catch (NumberFormatException ex) {}
        }
        String name;
        if (meta == -1) {
            name = s;
        }
        else {
            name = s.substring(0, lastColon);
        }
        Block block = Block.getBlockFromName(name);
        if (block == null) {
            final Item item = (Item)Item.itemRegistry.getObject(name);
            if (item instanceof ItemBlock) {
                block = ((ItemBlock)item).field_150939_a;
            }
            if (block == null) {
                if (logging) {
                    GCLog.severe("[config] " + caller + ": unrecognised block name '" + s + "'.");
                }
                return null;
            }
        }
        try {
            Integer.parseInt(name);
            final String bName = GameData.getBlockRegistry().getNameForObject((Object)block);
            if (logging) {
                GCLog.info("[config] " + caller + ": the use of numeric IDs is discouraged, please use " + bName + " instead of " + name);
            }
        }
        catch (NumberFormatException ex2) {}
        if (Blocks.air == block) {
            if (logging) {
                GCLog.info("[config] " + caller + ": not a good idea to specify air, skipping that!");
            }
            return null;
        }
        return new BlockTuple(block, meta);
    }

    public static List<Object> getServerConfigOverride() {
        final ArrayList<Object> returnList = new ArrayList<Object>();
        int modeFlags = ConfigManagerCore.hardMode ? 1 : 0;
        modeFlags += (ConfigManagerCore.quickMode ? 2 : 0);
        modeFlags += (ConfigManagerCore.challengeMode ? 4 : 0);
        modeFlags += (ConfigManagerCore.disableSpaceStationCreation ? 8 : 0);
        modeFlags += (ConfigManagerCore.recipesRequireGCAdvancedMetals ? 16 : 0);
        modeFlags += (ConfigManagerCore.challengeRecipes ? 32 : 0);
        returnList.add(modeFlags);
        returnList.add(ConfigManagerCore.dungeonBossHealthMod);
        returnList.add(ConfigManagerCore.suffocationDamage);
        returnList.add(ConfigManagerCore.suffocationCooldown);
        returnList.add(ConfigManagerCore.rocketFuelFactor);
        returnList.add(ConfigManagerCore.otherModsSilicon);
        EnergyConfigHandler.serverConfigOverride((ArrayList)returnList);
        returnList.add(ConfigManagerCore.detectableIDs.clone());
        return returnList;
    }

    @SideOnly(Side.CLIENT)
    public static void setConfigOverride(final List<Object> configs) {
        int dataCount = 0;
        final int modeFlag = (int) configs.get(dataCount++);
        ConfigManagerCore.hardMode = ((modeFlag & 0x1) != 0x0);
        ConfigManagerCore.quickMode = ((modeFlag & 0x2) != 0x0);
        ConfigManagerCore.challengeMode = ((modeFlag & 0x4) != 0x0);
        ConfigManagerCore.disableSpaceStationCreation = ((modeFlag & 0x8) != 0x0);
        ConfigManagerCore.recipesRequireGCAdvancedMetals = ((modeFlag & 0x10) != 0x0);
        ConfigManagerCore.challengeRecipes = ((modeFlag & 0x20) != 0x0);
        ConfigManagerCore.dungeonBossHealthMod = (double) configs.get(dataCount++);
        ConfigManagerCore.suffocationDamage = (int) configs.get(dataCount++);
        ConfigManagerCore.suffocationCooldown = (int) configs.get(dataCount++);
        ConfigManagerCore.rocketFuelFactor = (int) configs.get(dataCount++);
        ConfigManagerCore.otherModsSilicon = (String) configs.get(dataCount++);
        EnergyConfigHandler.setConfigOverride((float)configs.get(dataCount++), (float)configs.get(dataCount++), (float)configs.get(dataCount++), (float)configs.get(dataCount++), (int)configs.get(dataCount++));
        final int sizeIDs = configs.size() - dataCount;
        if (sizeIDs > 0) {
            final Object dataLast = configs.get(dataCount);
            if (dataLast instanceof String) {
                ConfigManagerCore.detectableIDs = new String[sizeIDs];
                for (int j = 0; j < sizeIDs; ++j) {
                    ConfigManagerCore.detectableIDs[j] = new String((String) configs.get(dataCount++));
                }
            }
            else if (dataLast instanceof String[]) {
                ConfigManagerCore.detectableIDs = (String[])dataLast;
            }
            TickHandlerClient.registerDetectableBlocks(false);
        }
        challengeModeUpdate();
        RecipeManagerGC.setConfigurableRecipes();
    }

    public static void saveClientConfigOverrideable() {
        if (ConfigManagerCore.clientSave == null) {
            ConfigManagerCore.clientSave = (ArrayList<Object>)(ArrayList)getServerConfigOverride();
        }
    }

    public static void restoreClientConfigOverrideable() {
        if (ConfigManagerCore.clientSave != null) {
            setConfigOverride(ConfigManagerCore.clientSave);
        }
    }

    private static int parseKeyValue(final String key) {
        if (key.equals("KEY_A")) {
            return 30;
        }
        if (key.equals("KEY_B")) {
            return 48;
        }
        if (key.equals("KEY_C")) {
            return 46;
        }
        if (key.equals("KEY_D")) {
            return 32;
        }
        if (key.equals("KEY_E")) {
            return 18;
        }
        if (key.equals("KEY_F")) {
            return 33;
        }
        if (key.equals("KEY_G")) {
            return 34;
        }
        if (key.equals("KEY_H")) {
            return 35;
        }
        if (key.equals("KEY_I")) {
            return 23;
        }
        if (key.equals("KEY_J")) {
            return 36;
        }
        if (key.equals("KEY_K")) {
            return 37;
        }
        if (key.equals("KEY_L")) {
            return 38;
        }
        if (key.equals("KEY_M")) {
            return 50;
        }
        if (key.equals("KEY_N")) {
            return 49;
        }
        if (key.equals("KEY_O")) {
            return 24;
        }
        if (key.equals("KEY_P")) {
            return 25;
        }
        if (key.equals("KEY_Q")) {
            return 16;
        }
        if (key.equals("KEY_R")) {
            return 19;
        }
        if (key.equals("KEY_S")) {
            return 31;
        }
        if (key.equals("KEY_T")) {
            return 20;
        }
        if (key.equals("KEY_U")) {
            return 22;
        }
        if (key.equals("KEY_V")) {
            return 47;
        }
        if (key.equals("KEY_W")) {
            return 17;
        }
        if (key.equals("KEY_X")) {
            return 45;
        }
        if (key.equals("KEY_Y")) {
            return 21;
        }
        if (key.equals("KEY_Z")) {
            return 44;
        }
        if (key.equals("KEY_1")) {
            return 2;
        }
        if (key.equals("KEY_2")) {
            return 3;
        }
        if (key.equals("KEY_3")) {
            return 4;
        }
        if (key.equals("KEY_4")) {
            return 5;
        }
        if (key.equals("KEY_5")) {
            return 6;
        }
        if (key.equals("KEY_6")) {
            return 7;
        }
        if (key.equals("KEY_7")) {
            return 8;
        }
        if (key.equals("KEY_8")) {
            return 9;
        }
        if (key.equals("KEY_9")) {
            return 10;
        }
        if (key.equals("KEY_0")) {
            return 11;
        }
        GCLog.severe("Failed to parse keyboard key: " + key + "... Use values A-Z or 0-9");
        return 0;
    }

    static {
        ConfigManagerCore.recipesRequireGCAdvancedMetals = true;
        ConfigManagerCore.biomeIDbase = 102;
        ConfigManagerCore.staticLoadDimensions = new int[0];
        ConfigManagerCore.disableRocketLaunchDimensions = new int[] { -1, 1 };
        ConfigManagerCore.otherPlanetWorldBorders = 0;
        ConfigManagerCore.oregenIDs = new String[0];
        ConfigManagerCore.sealableIDs = new String[0];
        ConfigManagerCore.detectableIDs = new String[0];
        ConfigManagerCore.clientSave = null;
    }
}
