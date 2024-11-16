package micdoodle8.mods.galacticraft.core.util;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.google.common.primitives.Ints;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.vector.BlockTuple;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.recipe.RecipeManagerGC;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.BiomeGenBaseAsteroids;

public class ConfigManagerCore {

    static Configuration config;

    // GAME CONTROL
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
    public static boolean recipesRequireGCAdvancedMetals = true;
    public static boolean alwaysDisplayOxygenHUD = false;
    public static boolean allowSSatUnreachable;
    // public static int mapfactor;
    // public static int mapsize;

    // DIMENSIONS
    public static int idDimensionOverworld;
    public static int idDimensionOverworldOrbit;
    public static int idDimensionOverworldOrbitStatic;
    public static int idDimensionMoon;
    public static int biomeIDbase = 102;
    public static boolean disableBiomeTypeRegistrations;
    public static int[] staticLoadDimensions = {};
    public static int[] disableRocketLaunchDimensions = { -1, 1 };
    public static boolean disableRocketLaunchAllNonGC;
    public static int otherPlanetWorldBorders = 0;
    public static boolean keepLoadedNewSpaceStations;

    // SCHEMATICS
    public static int idSchematicRocketT1;
    public static int idSchematicMoonBuggy;
    public static int idSchematicAddSchematic;

    // ACHIEVEMENTS
    public static int idAchievBase;

    // CLIENT / VISUAL FX
    public static boolean moreStars;
    public static boolean disableSpaceshipParticles;
    public static boolean disableVehicleCameraChanges;
    public static boolean oxygenIndicatorLeft;
    public static boolean oxygenIndicatorBottom;
    public static boolean overrideCapes;

    // DIFFICULTY
    public static double dungeonBossHealthMod;
    public static int suffocationCooldown;
    public static int suffocationDamage;
    public static int rocketFuelFactor;
    public static double meteorSpawnMod;
    public static boolean handfillOxygen;
    public static boolean meteorBlockDamageEnabled;
    public static boolean disableSpaceshipGrief;
    public static double spaceStationEnergyScalar;

    // WORLDGEN
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
    public static String[] oregenIDs = {};
    public static boolean enableOtherModsFeatures;
    public static boolean whitelistCoFHCoreGen;
    public static boolean enableThaumCraftNodes;

    // COMPATIBILITY
    public static String[] sealableIDs = {};
    public static String[] detectableIDs = {};
    public static boolean alternateCanisterRecipe;
    public static String otherModsSilicon;
    public static boolean useOldOilFluidID;
    public static boolean useOldFuelFluidID;

    // KEYBOARD AND MOUSE
    public static float mapMouseScrollSensitivity;
    public static boolean invertMapMouseScroll;
    // Kept to ensure backwards-compatibility with certain Galacticraft addons
    public static int keyOverrideFuelLevelI = 0;
    public static ArrayList<Object> clientSave = null;

    public static void initialize(File file) {
        ConfigManagerCore.config = new Configuration(file);
        ConfigManagerCore.syncConfig(true);
    }

    public static void forceSave() {
        ConfigManagerCore.config.save();
    }

    public static void syncConfig(boolean load) {
        final List<String> propOrder = new ArrayList<>();

        try {
            Property prop;

            if (!config.isChild && load) {
                config.load();
            }

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Debug Messages", false);
            prop.comment = "If this is enabled, debug messages will appear in the console. This is useful for finding bugs in the mod.";
            prop.setLanguageKey("gc.configgui.enableDebug");
            enableDebug = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionOverworld", 0);
            prop.comment = "Dimension ID for the Overworld (as seen in the Celestial Map)";
            prop.setLanguageKey("gc.configgui.idDimensionOverworld")
                .setRequiresMcRestart(true);
            idDimensionOverworld = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionMoon", -28);
            prop.comment = "Dimension ID for the Moon";
            prop.setLanguageKey("gc.configgui.idDimensionMoon")
                .setRequiresMcRestart(true);
            idDimensionMoon = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionOverworldOrbit", -27);
            prop.comment = "WorldProvider ID for Overworld Space Stations (advanced: do not change unless you have conflicts)";
            prop.setLanguageKey("gc.configgui.idDimensionOverworldOrbit")
                .setRequiresMcRestart(true);
            idDimensionOverworldOrbit = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionOverworldOrbitStatic", -26);
            prop.comment = "WorldProvider ID for Static Space Stations (advanced: do not change unless you have conflicts)";
            prop.setLanguageKey("gc.configgui.idDimensionOverworldOrbitStatic")
                .setRequiresMcRestart(true);
            idDimensionOverworldOrbitStatic = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "biomeIDBase", 102);
            prop.comment = "Biome ID for Moon (Mars will be this + 1, Asteroids + 2 etc). Allowed range 40-250.";
            prop.setLanguageKey("gc.configgui.biomeIDBase")
                .setRequiresMcRestart(true);
            biomeIDbase = prop.getInt();
            if (biomeIDbase < 40 || biomeIDbase > 250) {
                biomeIDbase = 102;
            }
            propOrder.add(prop.getName());

            prop = config.get(
                Constants.CONFIG_CATEGORY_DIMENSIONS,
                "Static Loaded Dimensions",
                ConfigManagerCore.staticLoadDimensions);
            prop.comment = "IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded";
            prop.setLanguageKey("gc.configgui.staticLoadedDimensions");
            staticLoadDimensions = prop.getIntList();
            propOrder.add(prop.getName());

            prop = config
                .get(Constants.CONFIG_CATEGORY_DIMENSIONS, "Set new Space Stations to be static loaded", false);
            prop.comment = "Set this to true to have an automatic /gckeeploaded for any new Space Station created.";
            prop.setLanguageKey("gc.configgui.staticLoadedNewSS");
            keepLoadedNewSpaceStations = prop.getBoolean();
            propOrder.add(prop.getName());

            prop = config.get(
                Constants.CONFIG_CATEGORY_DIMENSIONS,
                "Dimensions where rockets cannot launch",
                new String[] { "1", "-1" });
            prop.comment = "IDs of dimensions where rockets should not launch - this should always include the Nether.";
            prop.setLanguageKey("gc.configgui.rocketDisabledDimensions");
            disableRocketLaunchDimensions = prop.getIntList();
            disableRocketLaunchAllNonGC = searchAsterisk(prop.getStringList());
            propOrder.add(prop.getName());

            prop = config
                .get(Constants.CONFIG_CATEGORY_DIMENSIONS, "Disable rockets from returning to Overworld", false);
            prop.comment = "If true, rockets will be unable to reach the Overworld (only use this in special modpacks!)";
            prop.setLanguageKey("gc.configgui.rocketDisableOverworldReturn");
            disableRocketsToOverworld = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(
                Constants.CONFIG_CATEGORY_DIMENSIONS,
                "World border for landing location on other planets (Moon, Mars, etc)",
                0);
            prop.comment = "Set this to 0 for no borders (default).  If set to e.g. 2000, players will land on the Moon inside the x,z range -2000 to 2000.)";
            prop.setLanguageKey("gc.configgui.planetWorldBorders");
            otherPlanetWorldBorders = prop.getInt(0);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Force Overworld Spawn", false);
            prop.comment = "By default, you will respawn on galacticraft dimensions if you die. If you set this to true, you will respawn back on earth.";
            prop.setLanguageKey("gc.configgui.forceOverworldRespawn");
            forceOverworldRespawn = prop.getBoolean(false);
            propOrder.add(prop.getName());

            //

            prop = config.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicRocketT1", 0);
            prop.comment = "Schematic ID for Tier 1 Rocket, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicRocketT1");
            idSchematicRocketT1 = prop.getInt(0);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicMoonBuggy", 1);
            prop.comment = "Schematic ID for Moon Buggy, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicMoonBuggy");
            idSchematicMoonBuggy = prop.getInt(1);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicAddSchematic", Integer.MAX_VALUE);
            prop.comment = "Schematic ID for \"Add Schematic\" Page, must be unique";
            prop.setLanguageKey("gc.configgui.idSchematicAddSchematic");
            idSchematicAddSchematic = prop.getInt(Integer.MAX_VALUE);
            propOrder.add(prop.getName());

            //

            prop = config.get(Constants.CONFIG_CATEGORY_ACHIEVEMENTS, "idAchievBase", 1784);
            prop.comment = "Base Achievement ID. All achievement IDs will start at this number.";
            prop.setLanguageKey("gc.configgui.idAchievBase");
            idAchievBase = prop.getInt(1784);
            propOrder.add(prop.getName());

            // Client side

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "More Stars", true);
            prop.comment = "Setting this to false will revert night skies back to default minecraft star count";
            prop.setLanguageKey("gc.configgui.moreStars");
            moreStars = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Spaceship Particles", false);
            prop.comment = "If you have FPS problems, setting this to true will help if rocket particles are in your sights";
            prop.setLanguageKey("gc.configgui.disableSpaceshipParticles");
            disableSpaceshipParticles = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Vehicle Third-Person and Zoom", false);
            prop.comment = "If you're using this mod in virtual reality, or if you don't want the camera changes when entering a Galacticraft vehicle, set this to true.";
            prop.setLanguageKey("gc.configgui.disableVehicleCameraChanges");
            disableVehicleCameraChanges = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Minimap Left", false);
            prop.comment = "If true, this will move the Oxygen Indicator to the left side. You can combine this with \"Minimap Bottom\"";
            prop.setLanguageKey("gc.configgui.oxygenIndicatorLeft");
            oxygenIndicatorLeft = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Minimap Bottom", false);
            prop.comment = "If true, this will move the Oxygen Indicator to the bottom. You can combine this with \"Minimap Left\"";
            prop.setLanguageKey("gc.configgui.oxygenIndicatorBottom");
            oxygenIndicatorBottom = prop.getBoolean(false);
            propOrder.add(prop.getName());

            // World gen

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Oil Generation Factor", 1.8);
            prop.comment = "Increasing this will increase amount of oil that will generate in each chunk.";
            prop.setLanguageKey("gc.configgui.oilGenFactor");
            oilGenFactor = prop.getDouble(1.8);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Oil gen in external dimensions", new int[] { 0 });
            prop.comment = "List of non-galacticraft dimension IDs to generate oil in.";
            prop.setLanguageKey("gc.configgui.externalOilGen");
            externalOilGen = prop.getIntList();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Retro Gen of GC Oil in existing map chunks", false);
            prop.comment = "If this is enabled, GC oil will be added to existing Overworld maps where possible.";
            prop.setLanguageKey("gc.configgui.enableRetrogenOil");
            retrogenOil = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Copper Ore Gen", true);
            prop.comment = "If this is enabled, copper ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableCopperOreGen")
                .setRequiresMcRestart(true);
            enableCopperOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Tin Ore Gen", true);
            prop.comment = "If this is enabled, tin ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableTinOreGen")
                .setRequiresMcRestart(true);
            enableTinOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Aluminum Ore Gen", true);
            prop.comment = "If this is enabled, aluminum ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableAluminumOreGen")
                .setRequiresMcRestart(true);
            enableAluminumOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Silicon Ore Gen", true);
            prop.comment = "If this is enabled, silicon ore will generate on the overworld.";
            prop.setLanguageKey("gc.configgui.enableSiliconOreGen")
                .setRequiresMcRestart(true);
            enableSiliconOreGen = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Cheese Ore Gen on Moon", false);
            prop.comment = "Disable Cheese Ore Gen on Moon.";
            prop.setLanguageKey("gc.configgui.disableCheeseMoon");
            disableCheeseMoon = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Tin Ore Gen on Moon", false);
            prop.comment = "Disable Tin Ore Gen on Moon.";
            prop.setLanguageKey("gc.configgui.disableTinMoon");
            disableTinMoon = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Copper Ore Gen on Moon", false);
            prop.comment = "Disable Copper Ore Gen on Moon.";
            prop.setLanguageKey("gc.configgui.disableCopperMoon");
            disableCopperMoon = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Moon Village Gen", false);
            prop.comment = "If true, moon villages will not generate.";
            prop.setLanguageKey("gc.configgui.disableMoonVillageGen");
            disableMoonVillageGen = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Generate all other mods features on planets", false);
            prop.comment = "If this is enabled, other mods' standard ores and all other features (eg. plants) can generate on the Moon and planets. Apart from looking wrong, this make cause 'Already Decorating!' type crashes.  NOT RECOMMENDED!  See Wiki.";
            prop.setLanguageKey("gc.configgui.enableOtherModsFeatures");
            enableOtherModsFeatures = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(
                Constants.CONFIG_CATEGORY_GENERAL,
                "Whitelist CoFHCore worldgen to generate its ores and lakes on planets",
                false);
            prop.comment = "If generate other mods features is disabled as recommended, this setting can whitelist CoFHCore custom worldgen on planets.";
            prop.setLanguageKey("gc.configgui.whitelistCoFHCoreGen");
            whitelistCoFHCoreGen = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config
                .get(Constants.CONFIG_CATEGORY_GENERAL, "Generate ThaumCraft wild nodes on planetary surfaces", true);
            prop.comment = "If ThaumCraft is installed, ThaumCraft wild nodes can generate on the Moon and planets.";
            prop.setLanguageKey("gc.configgui.enableThaumCraftNodes");
            enableThaumCraftNodes = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(
                Constants.CONFIG_CATEGORY_GENERAL,
                "Other mods ores for GC to generate on the Moon and planets",
                new String[] {});
            prop.comment = "Enter IDs of other mods' ores here for Galacticraft to generate them on the Moon and other planets. Format is BlockName or BlockName:metadata. Use optional parameters at end of each line: /RARE /UNCOMMON or /COMMON for rarity in a chunk; /DEEP /SHALLOW or /BOTH for height; /SINGLE /STANDARD or /LARGE for clump size; /XTRARANDOM for ores sometimes there sometimes not at all.  /ONLYMOON or /ONLYMARS if wanted on one planet only.  If nothing specified, defaults are /COMMON, /BOTH and /STANDARD.  Repeat lines to generate a huge quantity of ores.";
            prop.setLanguageKey("gc.configgui.otherModOreGenIDs");
            oregenIDs = prop.getStringList();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Use legacy oilgc fluid registration", false);
            prop.comment = "Set to true to make Galacticraft oil register as oilgc, for backwards compatibility with previously generated worlds.";
            prop.setLanguageKey("gc.configgui.useOldOilFluidID");
            useOldOilFluidID = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Use legacy fuelgc fluid registration", false);
            prop.comment = "Set to true to make Galacticraft fuel register as fuelgc, for backwards compatibility with previously generated worlds.";
            prop.setLanguageKey("gc.configgui.useOldFuelFluidID");
            useOldFuelFluidID = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable lander on Moon and other planets", false);
            prop.comment = "If this is true, the player will parachute onto the Moon instead - use only in debug situations.";
            prop.setLanguageKey("gc.configgui.disableLander");
            disableLander = prop.getBoolean(false);
            propOrder.add(prop.getName());

            // Server side

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Spaceship Explosion", false);
            prop.comment = "Spaceships will not explode on contact if set to true.";
            prop.setLanguageKey("gc.configgui.disableSpaceshipGrief");
            disableSpaceshipGrief = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Space Stations Require Permission", true);
            prop.comment = "While true, space stations require you to invite other players using /ssinvite <playername>";
            prop.setLanguageKey("gc.configgui.spaceStationsRequirePermission");
            spaceStationsRequirePermission = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Space Station creation", false);
            prop.comment = "If set to true on a server, players will be completely unable to create space stations.";
            prop.setLanguageKey("gc.configgui.disableSpaceStationCreation");
            disableSpaceStationCreation = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Override Capes", true);
            prop.comment = "By default, Galacticraft will override capes with the mod's donor cape. Set to false to disable.";
            prop.setLanguageKey("gc.configgui.overrideCapes");
            overrideCapes = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Space Station Solar Energy Multiplier", 2.0);
            prop.comment = "Solar panels will work (default 2x) more effective on space stations.";
            prop.setLanguageKey("gc.configgui.spaceStationEnergyScalar");
            spaceStationEnergyScalar = prop.getDouble(2.0);
            propOrder.add(prop.getName());

            try {
                prop = config.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "External Sealable IDs",
                    new String[] { GameData.getBlockRegistry()
                        .getNameForObject(Blocks.glass_pane) + ":0" });
                prop.comment = "List non-opaque blocks from other mods (for example, special types of glass) that the Oxygen Sealer should recognize as solid seals. Format is BlockName or BlockName:metadata";
                prop.setLanguageKey("gc.configgui.sealableIDs")
                    .setRequiresMcRestart(true);
                sealableIDs = prop.getStringList();
                propOrder.add(prop.getName());
            } catch (final Exception e) {
                FMLLog.severe(
                    "[Galacticraft] It appears you have installed the 'Dev' version of Galacticraft instead of the regular version (or vice versa).  Please re-install.");
            }

            prop = config.get(
                Constants.CONFIG_CATEGORY_GENERAL,
                "External Detectable IDs",
                new String[] { GameData.getBlockRegistry()
                    .getNameForObject(Blocks.coal_ore),
                    GameData.getBlockRegistry()
                        .getNameForObject(Blocks.diamond_ore),
                    GameData.getBlockRegistry()
                        .getNameForObject(Blocks.gold_ore),
                    GameData.getBlockRegistry()
                        .getNameForObject(Blocks.iron_ore),
                    GameData.getBlockRegistry()
                        .getNameForObject(Blocks.lapis_ore),
                    GameData.getBlockRegistry()
                        .getNameForObject(Blocks.redstone_ore),
                    GameData.getBlockRegistry()
                        .getNameForObject(Blocks.lit_redstone_ore) });
            prop.comment = "List blocks from other mods that the Sensor Glasses should recognize as solid blocks. Format is BlockName or BlockName:metadata.";
            prop.setLanguageKey("gc.configgui.detectableIDs")
                .setRequiresMcRestart(true);
            detectableIDs = prop.getStringList();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Suffocation Cooldown", 100);
            prop.comment = "Lower/Raise this value to change time between suffocation damage ticks";
            prop.setLanguageKey("gc.configgui.suffocationCooldown");
            suffocationCooldown = prop.getInt(100);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Suffocation Damage", 2);
            prop.comment = "Change this value to modify the damage taken per suffocation tick";
            prop.setLanguageKey("gc.configgui.suffocationDamage");
            suffocationDamage = prop.getInt(2);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Dungeon Boss Health Modifier", 1.0);
            prop.comment = "Change this if you wish to balance the mod (if you have more powerful weapon mods).";
            prop.setLanguageKey("gc.configgui.dungeonBossHealthMod");
            dungeonBossHealthMod = prop.getDouble(1.0);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Harder Difficulty", false);
            prop.comment = "Set this to true for increased difficulty in modpacks (see forum for more info).";
            prop.setLanguageKey("gc.configgui.hardMode");
            hardMode = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Quick Game Mode", false);
            prop.comment = "Set this to true for less metal use in Galacticraft recipes (makes the game easier).";
            prop.setLanguageKey("gc.configgui.quickMode");
            quickMode = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Adventure Game Mode", false);
            prop.comment = "Set this to true for a challenging adventure where the player starts the game stranded in the Asteroids dimension with low resources (only effective if Galacticraft Planets installed).";
            prop.setLanguageKey("gc.configgui.asteroidsStart");
            challengeMode = prop.getBoolean(false);
            if (!GalacticraftCore.isPlanetsLoaded) {
                challengeMode = false;
            }
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Adventure Game Mode Flags", 15);
            prop.comment = "Add together flags 8, 4, 2, 1 to enable the four elements of adventure game mode. Default 15.  1 = extended compressor recipes.  2 = mob drops and spawning.  4 = more trees in hollow asteroids.  8 = start stranded in Asteroids.";
            prop.setLanguageKey("gc.configgui.asteroidsFlags");
            challengeFlags = prop.getInt(15);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Sealed edge checks", true);
            prop.comment = "If this is enabled, areas sealed by Oxygen Sealers will run a seal check when the player breaks or places a block (or on block updates).  This should be enabled for a 100% accurate sealed status, but can be disabled on servers for performance reasons.";
            prop.setLanguageKey("gc.configgui.enableSealerEdgeChecks");
            enableSealerEdgeChecks = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Alternate recipe for canisters", false);
            prop.comment = "Enable this if the standard canister recipe causes a conflict.";
            prop.setLanguageKey("gc.configgui.alternateCanisterRecipe")
                .setRequiresMcRestart(true);
            alternateCanisterRecipe = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(
                Constants.CONFIG_CATEGORY_GENERAL,
                "OreDict name of other mod's silicon usable in Galacticraft",
                "itemSilicon");
            prop.comment = "This needs to match the OreDictionary name used in the other mod. Set a nonsense name to disable.";
            prop.setLanguageKey("gc.configgui.oreDictSilicon")
                .setRequiresMcRestart(true);
            otherModsSilicon = prop.getString();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Must use GC's own space metals in recipes", true);
            prop.comment = "Should normally be true. If you set this to false, in a modpack with other mods with the same metals, players may be able to craft advanced GC items without travelling to Moon, Mars, Asteroids etc.";
            prop.setLanguageKey("gc.configgui.disableOreDictSpaceMetals")
                .setRequiresMcRestart(true);
            recipesRequireGCAdvancedMetals = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Always show Oxygen HUD", false);
            prop.comment = "Toggle this to always display the Oxygen HUD, if off it will only be shown on GC planets which require Oxygen.";
            prop.setLanguageKey("gc.configgui.alwaysDisplayOxygenHUD")
                .setRequiresMcRestart(false);
            alwaysDisplayOxygenHUD = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Allow Stations at Unreachables", true);
            prop.comment = "Allow the creation of Space Station at unreachable Celestial Bodies";
            prop.setRequiresMcRestart(false);
            allowSSatUnreachable = prop.getBoolean();
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Rocket fuel factor", 1);
            prop.comment = "The normal factor is 1.  Increase this to 2 - 5 if other mods with a lot of oil (e.g. BuildCraft) are installed to increase GC rocket fuel requirement.";
            prop.setLanguageKey("gc.configgui.rocketFuelFactor");
            rocketFuelFactor = prop.getInt(1);
            propOrder.add(prop.getName());

            // prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Map factor", 1);
            // prop.comment = "Allowed values 1-4 etc";
            // prop.setLanguageKey("gc.configgui.mapFactor");
            // mapfactor = prop.getInt(1);
            // propOrder.add(prop.getName());
            //
            // prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Map size", 400);
            // prop.comment = "Suggested value 400";
            // prop.setLanguageKey("gc.configgui.mapSize");
            // mapsize = prop.getInt(400);
            // propOrder.add(prop.getName());
            //
            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Map Scroll Mouse Sensitivity", 1.0);
            prop.comment = "Increase to make the mouse drag scroll more sensitive, decrease to lower sensitivity.";
            prop.setLanguageKey("gc.configgui.mapScrollSensitivity");
            mapMouseScrollSensitivity = (float) prop.getDouble(1.0);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Map Scroll Mouse Invert", false);
            prop.comment = "Set to true to invert the mouse scroll feature on the galaxy map.";
            prop.setLanguageKey("gc.configgui.mapScrollInvert");
            invertMapMouseScroll = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Meteor Spawn Modifier", 1.0);
            prop.comment = "Set to a value between 0.0 and 1.0 to decrease meteor spawn chance (all dimensions).";
            prop.setLanguageKey("gc.configgui.meteorSpawnMod");
            meteorSpawnMod = prop.getDouble(1.0);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Meteor Block Damage Enabled", true);
            prop.comment = "Set to false to stop meteors from breaking blocks on contact.";
            prop.setLanguageKey("gc.configgui.meteorBlockDamage");
            meteorBlockDamageEnabled = prop.getBoolean(true);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Hand fill", false);
            prop.comment = "You will be able to refill oxygen tank by crafting it with LOX cells if this is set to true.";
            prop.setLanguageKey("gc.configgui.handfill");
            prop.setRequiresMcRestart(true);
            handfillOxygen = prop.getBoolean(false);
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Update Check", false);
            prop.comment = "Update check will not run if this is set to true.";
            prop.setLanguageKey("gc.configgui.disableUpdateCheck");
            disableUpdateCheck = prop.getBoolean(false);
            propOrder.add(prop.getName());

            final boolean thisIsMC172 = VersionUtil.mcVersion1_7_2;
            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Disable Biome Type Registrations", thisIsMC172);
            prop.comment = "Biome Types will not be registered in the BiomeDictionary if this is set to true. Ignored (always true) for MC 1.7.2.";
            prop.setLanguageKey("gc.configgui.disableBiomeTypeRegistrations");
            disableBiomeTypeRegistrations = prop.getBoolean(thisIsMC172);
            if (thisIsMC172) {
                disableBiomeTypeRegistrations = true;
            }
            propOrder.add(prop.getName());

            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "Enable Space Race Manager Popup", false);
            prop.comment = "Space Race Manager will show on-screen after login, if enabled.";
            prop.setLanguageKey("gc.configgui.enableSpaceRaceManagerPopup");
            enableSpaceRaceManagerPopup = prop.getBoolean(false);
            propOrder.add(prop.getName());

            config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrder);

            if (config.hasChanged()) {
                config.save();
            }

            challengeModeUpdate();
        } catch (final Exception e) {
            GCLog.severe("Problem loading core config (\"core.conf\")");
        }
    }

    public static boolean setLoaded(int newID) {
        boolean found = false;

        for (final int staticLoadDimension : ConfigManagerCore.staticLoadDimensions) {
            if (staticLoadDimension == newID) {
                found = true;
                break;
            }
        }

        if (!found) {
            final int[] oldIDs = ConfigManagerCore.staticLoadDimensions;
            ConfigManagerCore.staticLoadDimensions = new int[ConfigManagerCore.staticLoadDimensions.length + 1];
            System.arraycopy(oldIDs, 0, staticLoadDimensions, 0, oldIDs.length);

            ConfigManagerCore.staticLoadDimensions[ConfigManagerCore.staticLoadDimensions.length - 1] = newID;
            final String[] values = new String[ConfigManagerCore.staticLoadDimensions.length];
            Arrays.sort(ConfigManagerCore.staticLoadDimensions);

            for (int i = 0; i < values.length; i++) {
                values[i] = String.valueOf(ConfigManagerCore.staticLoadDimensions[i]);
            }

            final Property prop = config.get(
                Constants.CONFIG_CATEGORY_DIMENSIONS,
                "Static Loaded Dimensions",
                ConfigManagerCore.staticLoadDimensions);
            prop.comment = "IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded";
            prop.setLanguageKey("gc.configgui.staticLoadedDimensions");
            prop.set(values);

            ConfigManagerCore.config.save();
        }

        return !found;
    }

    public static boolean setUnloaded(int idToRemove) {
        int foundCount = 0;

        for (final int staticLoadDimension : ConfigManagerCore.staticLoadDimensions) {
            if (staticLoadDimension == idToRemove) {
                foundCount++;
            }
        }

        if (foundCount > 0) {
            final List<Integer> idArray = new ArrayList<>(Ints.asList(ConfigManagerCore.staticLoadDimensions));
            idArray.removeAll(Collections.singleton(idToRemove));

            ConfigManagerCore.staticLoadDimensions = new int[idArray.size()];

            for (int i = 0; i < idArray.size(); i++) {
                ConfigManagerCore.staticLoadDimensions[i] = idArray.get(i);
            }

            final String[] values = new String[ConfigManagerCore.staticLoadDimensions.length];
            Arrays.sort(ConfigManagerCore.staticLoadDimensions);

            for (int i = 0; i < values.length; i++) {
                values[i] = String.valueOf(ConfigManagerCore.staticLoadDimensions[i]);
            }

            final Property prop = config.get(
                Constants.CONFIG_CATEGORY_DIMENSIONS,
                "Static Loaded Dimensions",
                ConfigManagerCore.staticLoadDimensions);
            prop.comment = "IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded";
            prop.setLanguageKey("gc.configgui.staticLoadedDimensions");
            prop.set(values);

            ConfigManagerCore.config.save();
        }

        return foundCount > 0;
    }

    private static void challengeModeUpdate() {
        if (challengeMode) {
            challengeRecipes = (challengeFlags & 1) != 0;
            challengeMobDropsAndSpawning = (challengeFlags & 2) != 0;
            challengeAsteroidPopulation = (challengeFlags & 4) != 0;
            challengeSpawnHandling = (challengeFlags & 8) != 0;
        } else {
            challengeRecipes = false;
            challengeMobDropsAndSpawning = false;
            challengeAsteroidPopulation = false;
            challengeSpawnHandling = false;
        }

        // This enables Endermen on Asteroids in Asteroids Challenge mode
        if (GalacticraftCore.isPlanetsLoaded) {
            ((BiomeGenBaseAsteroids) BiomeGenBaseAsteroids.asteroid)
                .resetMonsterListByMode(challengeMobDropsAndSpawning);
            // TODO: could also increase mob spawn frequency in Hard Mode on various
            // dimensions e.g. Moon and Mars?
        }
    }

    /**
     * Note for this to be effective, the prop = config.get() call has to provide a String[] as the default values If
     * you use an Integer[] then the config parser deletes all non-numerical lines from the config before GC even sees
     * them
     */
    private static boolean searchAsterisk(String[] strings) {
        for (final String s : strings) {
            if (s != null && "*".equals(s.trim())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<IConfigElement> getConfigElements() {
        final List<IConfigElement> list = new ArrayList<>(
            new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ENTITIES)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements());
        return list;
    }

    public static BlockTuple stringToBlock(String s, String caller, boolean logging) {
        final int lastColon = s.lastIndexOf(':');
        int meta = -1;
        String name;

        if (lastColon > 0) {
            try {
                meta = Integer.parseInt(s.substring(lastColon + 1));
            } catch (final NumberFormatException ex) {}
        }

        if (meta == -1) {
            name = s;
        } else {
            name = s.substring(0, lastColon);
        }

        Block block = Block.getBlockFromName(name);
        if (block == null) {
            final Item item = (Item) Item.itemRegistry.getObject(name);
            if (item instanceof ItemBlock) {
                block = ((ItemBlock) item).field_150939_a;
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
            final String bName = GameData.getBlockRegistry()
                .getNameForObject(block);
            if (logging) {
                GCLog.info(
                    "[config] " + caller
                        + ": the use of numeric IDs is discouraged, please use "
                        + bName
                        + " instead of "
                        + name);
            }
        } catch (final NumberFormatException ex) {}
        if (Blocks.air == block) {
            if (logging) {
                GCLog.info("[config] " + caller + ": not a good idea to specify air, skipping that!");
            }
            return null;
        }

        return new BlockTuple(block, meta);
    }

    public static List<Object> getServerConfigOverride() {
        final ArrayList<Object> returnList = new ArrayList<>();
        int modeFlags = ConfigManagerCore.hardMode ? 1 : 0;
        modeFlags += ConfigManagerCore.quickMode ? 2 : 0;
        modeFlags += ConfigManagerCore.challengeMode ? 4 : 0;
        modeFlags += ConfigManagerCore.disableSpaceStationCreation ? 8 : 0;
        modeFlags += ConfigManagerCore.recipesRequireGCAdvancedMetals ? 16 : 0;
        modeFlags += ConfigManagerCore.challengeRecipes ? 32 : 0;
        returnList.add(modeFlags);
        returnList.add(ConfigManagerCore.dungeonBossHealthMod);
        returnList.add(ConfigManagerCore.suffocationDamage);
        returnList.add(ConfigManagerCore.suffocationCooldown);
        returnList.add(ConfigManagerCore.rocketFuelFactor);
        returnList.add(ConfigManagerCore.otherModsSilicon);
        // If changing this, update definition of EnumSimplePacket.C_UPDATE_CONFIGS -
        // see comment in setConfigOverride()
        // below
        EnergyConfigHandler.serverConfigOverride(returnList);

        returnList.add(ConfigManagerCore.detectableIDs.clone());
        // TODO Should this include any other client-side configurables too?
        return returnList;
    }

    @SideOnly(Side.CLIENT)
    public static void setConfigOverride(List<Object> configs) {
        int dataCount = 0;
        final int modeFlag = (Integer) configs.get(dataCount);
        dataCount++;
        ConfigManagerCore.hardMode = (modeFlag & 1) != 0;
        ConfigManagerCore.quickMode = (modeFlag & 2) != 0;
        ConfigManagerCore.challengeMode = (modeFlag & 4) != 0;
        ConfigManagerCore.disableSpaceStationCreation = (modeFlag & 8) != 0;
        ConfigManagerCore.recipesRequireGCAdvancedMetals = (modeFlag & 16) != 0;
        ConfigManagerCore.challengeRecipes = (modeFlag & 32) != 0;
        ConfigManagerCore.dungeonBossHealthMod = (Double) configs.get(dataCount++);
        ConfigManagerCore.suffocationDamage = (Integer) configs.get(dataCount++);
        ConfigManagerCore.suffocationCooldown = (Integer) configs.get(dataCount++);
        ConfigManagerCore.rocketFuelFactor = (Integer) configs.get(dataCount++);
        ConfigManagerCore.otherModsSilicon = (String) configs.get(dataCount++);
        // If adding any additional data objects here, also remember to update the
        // packet definition of
        // EnumSimplePacket.C_UPDATE_CONFIGS in PacketSimple
        // Current working packet definition: Integer.class, Double.class,
        // Integer.class, Integer.class, Integer.class,
        // String.class, Float.class, Float.class, Float.class, Float.class,
        // Integer.class, String[].class

        EnergyConfigHandler.setConfigOverride(
            (Float) configs.get(dataCount++),
            (Float) configs.get(dataCount++),
            (Float) configs.get(dataCount++),
            (Float) configs.get(dataCount++),
            (Integer) configs.get(dataCount++));

        final int sizeIDs = configs.size() - dataCount;
        if (sizeIDs > 0) {
            final Object dataLast = configs.get(dataCount);
            if (dataLast instanceof String) {
                ConfigManagerCore.detectableIDs = new String[sizeIDs];
                for (int j = 0; j < sizeIDs; j++) {
                    ConfigManagerCore.detectableIDs[j] = (String) configs.get(dataCount++);
                }
            } else if (dataLast instanceof String[]) {
                ConfigManagerCore.detectableIDs = (String[]) dataLast;
            }
            TickHandlerClient.registerDetectableBlocks(false);
        }

        challengeModeUpdate();
        RecipeManagerGC.setConfigurableRecipes();
    }

    public static void saveClientConfigOverrideable() {
        if (ConfigManagerCore.clientSave == null) {
            ConfigManagerCore.clientSave = (ArrayList<Object>) ConfigManagerCore.getServerConfigOverride();
        }
    }

    public static void restoreClientConfigOverrideable() {
        if (ConfigManagerCore.clientSave != null) {
            ConfigManagerCore.setConfigOverride(clientSave);
        }
    }
}
