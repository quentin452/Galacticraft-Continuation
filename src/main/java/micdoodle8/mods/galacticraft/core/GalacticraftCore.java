package micdoodle8.mods.galacticraft.core;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import api.player.server.ServerPlayerAPI;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.client.IGameScreen;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidGC;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.blocks.MaterialOleaginous;
import micdoodle8.mods.galacticraft.core.client.gui.GuiHandler;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GameScreenBasic;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GameScreenCelestial;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GameScreenText;
import micdoodle8.mods.galacticraft.core.command.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.grid.ChunkPowerHandler;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerBaseMP;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler;
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemBlockGC;
import micdoodle8.mods.galacticraft.core.items.ItemBucketGC;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.network.ConnectionEvents;
import micdoodle8.mods.galacticraft.core.network.ConnectionPacket;
import micdoodle8.mods.galacticraft.core.network.GalacticraftChannelHandler;
import micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore;
import micdoodle8.mods.galacticraft.core.recipe.RecipeManagerGC;
import micdoodle8.mods.galacticraft.core.schematic.SchematicAdd;
import micdoodle8.mods.galacticraft.core.schematic.SchematicMoonBuggy;
import micdoodle8.mods.galacticraft.core.schematic.SchematicRocketT1;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback;
import micdoodle8.mods.galacticraft.core.world.gen.OreGenOtherMods;
import micdoodle8.mods.galacticraft.core.world.gen.OverworldGenerator;

@Mod(
    modid = Constants.MOD_ID_CORE,
    name = GalacticraftCore.NAME,
    version = Constants.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    useMetadata = true,
    dependencies = "required-after:NotEnoughItems;required-after:unimixins;required-after:optimizationsandtweaks;required-after:falsepatternlib;before:GalaxySpace;after:IC2;after:TConstruct;after:Mantle;after:BuildCraft|Core;after:BuildCraft|Energy;after:PlayerAPI@[1.3,)",
    guiFactory = "micdoodle8.mods.galacticraft.core.client.gui.screen.ConfigGuiFactoryCore")
public class GalacticraftCore {

    public static final String NAME = "Galacticraft Core";

    @SidedProxy(
        clientSide = "micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore",
        serverSide = "micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore")
    public static CommonProxyCore proxy;

    @Instance(Constants.MOD_ID_CORE)
    public static GalacticraftCore instance;

    public static boolean isPlanetsLoaded;
    public static boolean isGalaxySpaceLoaded;
    public static boolean isHeightConflictingModInstalled;

    public static GalacticraftChannelHandler packetPipeline;
    public static GCPlayerHandler handler;

    public static CreativeTabs galacticraftBlocksTab;
    public static CreativeTabs galacticraftItemsTab;

    public static SolarSystem solarSystemSol;
    public static Planet planetMercury;
    public static Planet planetVenus;
    public static Planet planetMars; // Used only if GCPlanets not loaded
    public static Planet planetOverworld;
    public static Planet planetJupiter;
    public static Planet planetSaturn;
    public static Planet planetUranus;
    public static Planet planetNeptune;
    public static Moon moonMoon;
    public static Satellite satelliteSpaceStation;

    public static final String CONFIG_FILE = "Galacticraft/core.conf";
    public static final String POWER_CONFIG_FILE = "Galacticraft/power-GC3.conf";
    public static final String CHUNKLOADER_CONFIG_FILE = "Galacticraft/chunkloading.conf";

    public static String ASSET_PREFIX = "galacticraftcore";
    public static String TEXTURE_PREFIX = ASSET_PREFIX + ":";
    public static String ASSET_PREFIX_MOON = "galacticraftmoon";
    public static String TEXTURE_PREFIX_MOON = ASSET_PREFIX_MOON + ":";
    public static String PREFIX = "micdoodle8.";

    public static Fluid fluidOil;
    public static Fluid fluidFuel;
    public static Material materialOil = new MaterialOleaginous(MapColor.brownColor);

    public static HashMap<String, ItemStack> itemList = new HashMap<>();
    public static HashMap<String, ItemStack> blocksList = new HashMap<>();

    public static ImageWriter jpgWriter;
    public static ImageWriteParam writeParam;
    public static boolean enableJPEG = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        isPlanetsLoaded = Loader.isModLoaded(Constants.MOD_ID_PLANETS);
        isGalaxySpaceLoaded = Loader.isModLoaded(Constants.MOD_ID_GALAXYSPACE);
        GCCoreUtil.nextID = 0;

        if (Loader.isModLoaded("SmartMoving")) {
            isHeightConflictingModInstalled = true;
        }

        if (Loader.isModLoaded("witchery")) {
            isHeightConflictingModInstalled = true;
        }

        MinecraftForge.EVENT_BUS.register(new EventHandlerGC());
        handler = new GCPlayerHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
        proxy.preInit(event);

        ConnectionPacket.bus = NetworkRegistry.INSTANCE.newEventDrivenChannel(ConnectionPacket.CHANNEL);
        ConnectionPacket.bus.register(new ConnectionPacket());

        ConfigManagerCore.initialize(new File(event.getModConfigurationDirectory(), CONFIG_FILE));
        EnergyConfigHandler.setDefaultValues(new File(event.getModConfigurationDirectory(), POWER_CONFIG_FILE));
        ChunkLoadingCallback.loadConfig(new File(event.getModConfigurationDirectory(), CHUNKLOADER_CONFIG_FILE));

        this.registerOilandFuel();

        if (Loader.isModLoaded("PlayerAPI")) {
            ServerPlayerAPI.register(Constants.MOD_ID_CORE, GCPlayerBaseMP.class);
        }

        GCBlocks.initBlocks();
        GCItems.initItems();

        // Allow canisters to be filled from other mods' tanks containing fuel / oil
        // fluids
        FluidContainerRegistry.registerFluidContainer(
            new FluidContainerData(
                new FluidStack(fluidFuel, 1000),
                new ItemStack(GCItems.fuelCanister, 1, 1),
                new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
        FluidContainerRegistry.registerFluidContainer(
            new FluidContainerData(
                new FluidStack(fluidOil, 1000),
                new ItemStack(GCItems.oilCanister, 1, 1),
                new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        galacticraftBlocksTab = new CreativeTabGC(
            CreativeTabs.getNextID(),
            "GalacticraftBlocks",
            Item.getItemFromBlock(GCBlocks.machineBase2),
            0);
        galacticraftItemsTab = new CreativeTabGC(CreativeTabs.getNextID(), "GalacticraftItems", GCItems.rocketTier1, 0);
        proxy.init(event);

        packetPipeline = GalacticraftChannelHandler.init();

        solarSystemSol = new SolarSystem("sol", "milkyWay").setMapPosition(new Vector3(0.0, 0.0, 0.0));
        final Star starSol = (Star) new Star("sol").setParentSolarSystem(solarSystemSol)
            .setTierRequired(-1);
        starSol.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/sun.png"));
        solarSystemSol.setMainStar(starSol);

        planetOverworld = (Planet) new Planet("overworld").setParentSolarSystem(solarSystemSol)
            .setRingColorRGB(0.1F, 0.9F, 0.6F)
            .setPhaseShift(0.0F);
        planetOverworld.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/earth.png"));
        planetOverworld.setDimensionInfo(ConfigManagerCore.idDimensionOverworld, WorldProvider.class, false)
            .setTierRequired(1)
            .setAllowSatellite(true);
        planetOverworld.atmosphereComponent(IAtmosphericGas.NITROGEN)
            .atmosphereComponent(IAtmosphericGas.OXYGEN)
            .atmosphereComponent(IAtmosphericGas.ARGON)
            .atmosphereComponent(IAtmosphericGas.WATER);

        moonMoon = (Moon) new Moon("moon").setParentPlanet(planetOverworld)
            .setRelativeSize(0.2667F)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(13F, 13F))
            .setRelativeOrbitTime(1 / 0.01F);
        moonMoon.setDimensionInfo(ConfigManagerCore.idDimensionMoon, WorldProviderMoon.class)
            .setTierRequired(1);
        moonMoon.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/moon.png"));

        // Satellites must always have a WorldProvider implementing IOrbitDimension
        satelliteSpaceStation = (Satellite) new Satellite("spaceStation.overworld").setParentBody(planetOverworld)
            .setRelativeSize(0.2667F)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(9F, 9F))
            .setRelativeOrbitTime(1 / 0.05F);
        satelliteSpaceStation
            .setDimensionInfo(
                ConfigManagerCore.idDimensionOverworldOrbit,
                ConfigManagerCore.idDimensionOverworldOrbitStatic,
                WorldProviderOrbit.class)
            .setTierRequired(1);
        satelliteSpaceStation
            .setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/spaceStation.png"));

        ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoadingCallback());
        FMLCommonHandler.instance()
            .bus()
            .register(new ConnectionEvents());

        SchematicRegistry.registerSchematicRecipe(new SchematicRocketT1());
        SchematicRegistry.registerSchematicRecipe(new SchematicMoonBuggy());
        SchematicRegistry.registerSchematicRecipe(new SchematicAdd());
        ChunkPowerHandler.initiate();
        EnergyConfigHandler.initGas();

        this.registerMicroBlocks();
        this.registerCreatures();
        this.registerOtherEntities();
        this.registerTileEntities();

        GalaxyRegistry.registerSolarSystem(solarSystemSol);
        GalaxyRegistry.registerPlanet(planetOverworld);
        GalaxyRegistry.registerMoon(moonMoon);
        GalaxyRegistry.registerSatellite(satelliteSpaceStation);
        GalacticraftRegistry
            .registerProvider(ConfigManagerCore.idDimensionOverworldOrbit, WorldProviderOrbit.class, false, 0);
        GalacticraftRegistry
            .registerProvider(ConfigManagerCore.idDimensionOverworldOrbitStatic, WorldProviderOrbit.class, true, 0);
        GalacticraftRegistry.registerTeleportType(WorldProviderSurface.class, new TeleportTypeOverworld());
        GalacticraftRegistry.registerTeleportType(WorldProviderOrbit.class, new TeleportTypeOrbit());
        GalacticraftRegistry.registerTeleportType(WorldProviderMoon.class, new TeleportTypeMoon());
        GalacticraftRegistry.registerRocketGui(
            WorldProviderOrbit.class,
            new ResourceLocation(ASSET_PREFIX, "textures/gui/overworldRocketGui.png"));
        GalacticraftRegistry.registerRocketGui(
            WorldProviderSurface.class,
            new ResourceLocation(ASSET_PREFIX, "textures/gui/overworldRocketGui.png"));
        GalacticraftRegistry.registerRocketGui(
            WorldProviderMoon.class,
            new ResourceLocation(ASSET_PREFIX, "textures/gui/moonRocketGui.png"));
        GalacticraftRegistry.addDungeonLoot(1, new ItemStack(GCItems.schematic, 1, 0));
        GalacticraftRegistry.addDungeonLoot(1, new ItemStack(GCItems.schematic, 1, 1));

        if (ConfigManagerCore.enableCopperOreGen) {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 5, 24, 0, 75, 7), 4);
        }

        if (ConfigManagerCore.enableTinOreGen) {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 6, 22, 0, 60, 7), 4);
        }

        if (ConfigManagerCore.enableAluminumOreGen) {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 7, 18, 0, 45, 7), 4);
        }

        if (ConfigManagerCore.enableSiliconOreGen) {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 8, 3, 0, 25, 7), 4);
        }

        FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://www.patreon.com/micdoodle8");
        registerCoreGameScreens();

        // If any other mod has registered "fuel" or "oil" and GC has not, then allow
        // GC's appropriate canisters to be
        // fillable with that one as well
        if (ConfigManagerCore.useOldFuelFluidID && FluidRegistry.isFluidRegistered("fuel")) {
            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(FluidRegistry.getFluid("fuel"), 1000),
                    new ItemStack(GCItems.fuelCanister, 1, 1),
                    new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
        }
        if (ConfigManagerCore.useOldOilFluidID && FluidRegistry.isFluidRegistered("oil")) {
            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(FluidRegistry.getFluid("oil"), 1000),
                    new ItemStack(GCItems.oilCanister, 1, 1),
                    new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
            // And allow Buildcraft oil buckets to be filled with oilgc
            if (CompatibilityManager.isBCraftEnergyLoaded()) {
                FluidContainerRegistry.registerFluidContainer(
                    new FluidContainerData(
                        new FluidStack(fluidOil, 1000),
                        GameRegistry.findItemStack("BuildCraft|Core", "bucketOil", 1),
                        new ItemStack(Items.bucket)));
            }
        }

        // Register now any unregistered "oil", "fuel", "oilgc" and "fuelgc" fluids
        // This is for legacy compatibility with any 'in the world' tanks and items
        // filled in different GC versions or
        // with different GC config
        // In those cases, FluidUtil methods (and TileEntityRefinery) will attempt to
        // fresh containers/tanks with the
        // current fuel or oil type
        if (!FluidRegistry.isFluidRegistered("oil")) {
            FluidRegistry.registerFluid(
                new Fluid("oil").setDensity(800)
                    .setViscosity(1500));
        }
        if (!FluidRegistry.isFluidRegistered("oilgc")) {
            FluidRegistry.registerFluid(
                new Fluid("oilgc").setDensity(800)
                    .setViscosity(1500));
        }
        if (!FluidRegistry.isFluidRegistered("fuel")) {
            FluidRegistry.registerFluid(
                new Fluid("fuel").setDensity(400)
                    .setViscosity(900));
        }
        if (!FluidRegistry.isFluidRegistered("fuelgc")) {
            FluidRegistry.registerFluid(
                new Fluid("fuelgc").setDensity(400)
                    .setViscosity(900));
        }
    }

    private void registerOilandFuel() {
        // NOTE: the way this operates will depend on the order in which different mods
        // initialize (normally
        // alphabetical order)
        // Galacticraft can handle things OK if another mod registers oil or fuel first.
        // The other mod may not be so
        // happy if GC registers oil or fuel first.

        String oilID = "oil";
        String fuelID = "fuel";
        if (ConfigManagerCore.useOldOilFluidID) {
            oilID = "oilgc";
        }
        if (ConfigManagerCore.useOldFuelFluidID) {
            fuelID = "fuelgc";
        }

        // Oil:
        if (!FluidRegistry.isFluidRegistered(oilID)) {
            final Fluid gcFluidOil = new Fluid(oilID).setDensity(800)
                .setViscosity(1500);
            FluidRegistry.registerFluid(gcFluidOil);
        } else {
            GCLog.info("Galacticraft oil is not default, issues may occur.");
        }

        fluidOil = FluidRegistry.getFluid(oilID);

        if (fluidOil.getBlock() == null) {
            GCBlocks.crudeOil = new BlockFluidGC(fluidOil, "oil");
            ((BlockFluidGC) GCBlocks.crudeOil).setQuantaPerBlock(3);
            GCBlocks.crudeOil.setBlockName("crudeOilStill");
            GameRegistry.registerBlock(GCBlocks.crudeOil, ItemBlockGC.class, GCBlocks.crudeOil.getUnlocalizedName());
            fluidOil.setBlock(GCBlocks.crudeOil);
        } else {
            GCBlocks.crudeOil = fluidOil.getBlock();
        }

        if (GCBlocks.crudeOil != null && Item.itemRegistry.getObject("buildcraftenergy:items/bucketOil") == null) {
            GCItems.bucketOil = new ItemBucketGC(GCBlocks.crudeOil, TEXTURE_PREFIX);
            GCItems.bucketOil.setUnlocalizedName("bucketOil");
            GCItems.registerItem(GCItems.bucketOil);
            FluidContainerRegistry.registerFluidContainer(
                FluidRegistry.getFluidStack(oilID, FluidContainerRegistry.BUCKET_VOLUME),
                new ItemStack(GCItems.bucketOil),
                new ItemStack(Items.bucket));
        }

        EventHandlerGC.bucketList.put(GCBlocks.crudeOil, GCItems.bucketOil);

        // Fuel:
        if (!FluidRegistry.isFluidRegistered(fuelID)) {
            final Fluid gcFluidFuel = new Fluid(fuelID).setDensity(400)
                .setViscosity(900);
            FluidRegistry.registerFluid(gcFluidFuel);
        } else {
            GCLog.info("Galacticraft fuel is not default, issues may occur.");
        }

        fluidFuel = FluidRegistry.getFluid(fuelID);

        if (fluidFuel.getBlock() == null) {
            GCBlocks.fuel = new BlockFluidGC(fluidFuel, "fuel");
            ((BlockFluidGC) GCBlocks.fuel).setQuantaPerBlock(3);
            GCBlocks.fuel.setBlockName("fuel");
            GameRegistry.registerBlock(GCBlocks.fuel, ItemBlockGC.class, GCBlocks.fuel.getUnlocalizedName());
            fluidFuel.setBlock(GCBlocks.fuel);
        } else {
            GCBlocks.fuel = fluidFuel.getBlock();
        }

        if (GCBlocks.fuel != null && Item.itemRegistry.getObject("buildcraftenergy:items/bucketFuel") == null) {
            GCItems.bucketFuel = new ItemBucketGC(GCBlocks.fuel, TEXTURE_PREFIX);
            GCItems.bucketFuel.setUnlocalizedName("bucketFuel");
            GCItems.registerItem(GCItems.bucketFuel);
            FluidContainerRegistry.registerFluidContainer(
                FluidRegistry.getFluidStack(fuelID, FluidContainerRegistry.BUCKET_VOLUME),
                new ItemStack(GCItems.bucketFuel),
                new ItemStack(Items.bucket));
        }

        EventHandlerGC.bucketList.put(GCBlocks.fuel, GCItems.bucketFuel);
    }

    public static void registerCoreGameScreens() {
        final IGameScreen rendererBasic = new GameScreenBasic();
        final IGameScreen rendererCelest = new GameScreenCelestial();
        GalacticraftRegistry.registerScreen(rendererBasic); // Type 0 - blank
        GalacticraftRegistry.registerScreen(rendererBasic); // Type 1 - local satellite view
        GalacticraftRegistry.registerScreen(rendererCelest); // Type 2 - solar system
        GalacticraftRegistry.registerScreen(rendererCelest); // Type 3 - local planet
        GalacticraftRegistry.registerScreen(rendererCelest); // Type 4 - render test
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        planetMercury = this.makeUnreachablePlanet("mercury", solarSystemSol);
        if (planetMercury != null) {
            planetMercury.setRingColorRGB(0.1F, 0.9F, 0.6F)
                .setPhaseShift(1.45F)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.5F, 0.5F))
                .setRelativeOrbitTime(0.24096385542168674698795180722892F);
        }
        planetVenus = this.makeUnreachablePlanet("venus", solarSystemSol);
        if (planetVenus != null) {
            planetVenus.setRingColorRGB(0.1F, 0.9F, 0.6F)
                .setPhaseShift(2.0F)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.75F, 0.75F))
                .setRelativeOrbitTime(0.61527929901423877327491785323111F);
        }
        planetMars = this.makeUnreachablePlanet("mars", solarSystemSol);
        if (planetMars != null) {
            planetMars.setRingColorRGB(0.67F, 0.1F, 0.1F)
                .setPhaseShift(0.1667F)
                .setRelativeSize(0.5319F)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.25F, 1.25F))
                .setRelativeOrbitTime(1.8811610076670317634173055859803F);
        }
        planetJupiter = this.makeUnreachablePlanet("jupiter", solarSystemSol);
        if (planetJupiter != null) {
            planetJupiter.setRingColorRGB(0.1F, 0.9F, 0.6F)
                .setPhaseShift((float) Math.PI)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.5F, 1.5F))
                .setRelativeOrbitTime(11.861993428258488499452354874042F);
        }
        planetSaturn = this.makeUnreachablePlanet("saturn", solarSystemSol);
        if (planetSaturn != null) {
            planetSaturn.setRingColorRGB(0.1F, 0.9F, 0.6F)
                .setPhaseShift(5.45F)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.75F, 1.75F))
                .setRelativeOrbitTime(29.463307776560788608981380065717F);
        }
        planetUranus = this.makeUnreachablePlanet("uranus", solarSystemSol);
        if (planetUranus != null) {
            planetUranus.setRingColorRGB(0.1F, 0.9F, 0.6F)
                .setPhaseShift(1.38F)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.0F, 2.0F))
                .setRelativeOrbitTime(84.063526834611171960569550930997F);
        }
        planetNeptune = this.makeUnreachablePlanet("neptune", solarSystemSol);
        if (planetNeptune != null) {
            planetNeptune.setRingColorRGB(0.1F, 0.9F, 0.6F)
                .setPhaseShift(1.0F)
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.25F, 2.25F))
                .setRelativeOrbitTime(164.84118291347207009857612267251F);
        }

        MinecraftForge.EVENT_BUS.register(new OreGenOtherMods());

        proxy.postInit(event);

        final ArrayList<CelestialBody> cBodyList = new ArrayList<>();
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredPlanets()
                .values());
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredMoons()
                .values());

        for (final CelestialBody body : cBodyList) {
            if (body.shouldAutoRegister()) {
                final int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, body.getDimensionID());
                // It's important this is done in the same order as planets will be registered
                // by
                // WorldUtil.registerPlanet();
                if (!GalacticraftRegistry.registerProvider(
                    body.getDimensionID(),
                    body.getWorldProvider(),
                    body.getForceStaticLoad() || id < 0,
                    0)) {
                    body.setUnreachable();
                }
            }
        }

        CompatibilityManager.checkForCompatibleMods();
        RecipeManagerGC.loadRecipes();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new TickHandlerServer());
        GalaxyRegistry.refreshGalaxies();

        GalacticraftRegistry.registerScreen(new GameScreenText()); // Screen API demo
        // Note: add-ons can register their own screens in postInit by calling
        // GalacticraftRegistry.registerScreen(IGameScreen) like this.
        // [Called on both client and server: do not include any client-specific code in
        // the new game screen's
        // constructor method.]

        try {
            jpgWriter = ImageIO.getImageWritersByFormatName("jpg")
                .next();
            writeParam = jpgWriter.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(1.0f);
            enableJPEG = true;
        } catch (final UnsatisfiedLinkError e) {
            GCLog.severe(
                "Error initialising JPEG compressor - this is likely caused by OpenJDK - see https://wiki.micdoodle8.com/wiki/Compatibility#For_Linux_servers_running_OpenJDK");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void serverInit(FMLServerStartedEvent event) {
        TickHandlerServer.restart();
        BlockVec3.chunkCacheDim = Integer.MAX_VALUE;
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSpaceStationAddOwner());
        event.registerServerCommand(new CommandSpaceStationChangeOwner());
        event.registerServerCommand(new CommandSpaceStationRemoveOwner());
        event.registerServerCommand(new CommandPlanetTeleport());
        event.registerServerCommand(new CommandKeepDim());
        event.registerServerCommand(new CommandGCInv());
        event.registerServerCommand(new CommandGCHelp());
        event.registerServerCommand(new CommandGCEnergyUnits());
        event.registerServerCommand(new CommandJoinSpaceRace());

        WorldUtil.initialiseDimensionNames();
        WorldUtil.registerSpaceStations(
            event.getServer()
                .worldServerForDimension(0)
                .getSaveHandler()
                .getMapFileFromName("dummy")
                .getParentFile());

        final ArrayList<CelestialBody> cBodyList = new ArrayList<>();
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredPlanets()
                .values());
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredMoons()
                .values());

        for (final CelestialBody body : cBodyList) {
            if (body.shouldAutoRegister() && !WorldUtil.registerPlanet(body.getDimensionID(), body.getReachable(), 0)) {
                body.setUnreachable();
            }
        }

        RecipeManagerGC.setConfigurableRecipes();
    }

    @EventHandler
    public void unregisterDims(FMLServerStoppedEvent var1) {
        WorldUtil.unregisterPlanets();
        WorldUtil.unregisterSpaceStations();
    }

    private void registerMicroBlocks() {
        try {
            final Class<?> clazz = Class.forName("codechicken.microblock.MicroMaterialRegistry");
            if (clazz != null) {
                Method registerMethod = null;
                final Method[] methodz = clazz.getMethods();
                for (final Method m : methodz) {
                    if ("registerMaterial".equals(m.getName())) {
                        registerMethod = m;
                        break;
                    }
                }
                final Class<?> clazzbm = Class.forName("codechicken.microblock.BlockMicroMaterial");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.basicBlock, 3),
                    "tile.gcBlockCore.decoblock1");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.basicBlock, 4),
                    "tile.gcBlockCore.decoblock2");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.basicBlock, 9),
                    "tile.gcBlockCore.copperBlock");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.basicBlock, 10),
                    "tile.gcBlockCore.tinBlock");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.basicBlock, 11),
                    "tile.gcBlockCore.aluminumBlock");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.basicBlock, 12),
                    "tile.gcBlockCore.meteorironBlock");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.blockMoon, 3),
                    "tile.moonBlock.moondirt");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.blockMoon, 4),
                    "tile.moonBlock.moonstone");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.blockMoon, 5),
                    "tile.moonBlock.moongrass");
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block.class, int.class)
                        .newInstance(GCBlocks.blockMoon, 14),
                    "tile.moonBlock.bricks");
            }
        } catch (final Exception e) {}
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(
            TileEntityTreasureChest.class,
            CompatibilityManager.isAIILoaded() ? "Space Treasure Chest" : "Treasure Chest");
        GameRegistry.registerTileEntity(TileEntityOxygenDistributor.class, "Air Distributor");
        GameRegistry.registerTileEntity(TileEntityOxygenCollector.class, "Air Collector");
        GameRegistry.registerTileEntity(TileEntityOxygenPipe.class, "Oxygen Pipe");
        GameRegistry.registerTileEntity(TileEntityAirLock.class, "Air Lock Frame");
        GameRegistry.registerTileEntity(TileEntityRefinery.class, "Refinery");
        GameRegistry.registerTileEntity(TileEntityNasaWorkbench.class, "NASA Workbench");
        GameRegistry.registerTileEntity(TileEntityOxygenCompressor.class, "Air Compressor");
        GameRegistry.registerTileEntity(TileEntityFuelLoader.class, "Fuel Loader");
        GameRegistry.registerTileEntity(TileEntityLandingPadSingle.class, "Landing Pad");
        GameRegistry.registerTileEntity(TileEntityLandingPad.class, "Landing Pad Full");
        GameRegistry.registerTileEntity(TileEntitySpaceStationBase.class, "Space Station");
        GameRegistry.registerTileEntity(TileEntityMulti.class, "Dummy Block");
        GameRegistry.registerTileEntity(TileEntityOxygenSealer.class, "Air Sealer");
        GameRegistry.registerTileEntity(TileEntityDungeonSpawner.class, "Dungeon Boss Spawner");
        GameRegistry.registerTileEntity(TileEntityOxygenDetector.class, "Oxygen Detector");
        GameRegistry.registerTileEntity(TileEntityBuggyFueler.class, "Buggy Fueler");
        GameRegistry.registerTileEntity(TileEntityBuggyFuelerSingle.class, "Buggy Fueler Single");
        GameRegistry.registerTileEntity(TileEntityCargoLoader.class, "Cargo Loader");
        GameRegistry.registerTileEntity(TileEntityCargoUnloader.class, "Cargo Unloader");
        GameRegistry.registerTileEntity(TileEntityParaChest.class, "Parachest Tile");
        GameRegistry.registerTileEntity(TileEntitySolar.class, "Galacticraft Solar Panel");
        GameRegistry.registerTileEntity(TileEntityDish.class, "Radio Telescope");
        GameRegistry.registerTileEntity(TileEntityEnergyStorageModule.class, "Energy Storage Module");
        GameRegistry.registerTileEntity(TileEntityCoalGenerator.class, "Galacticraft Coal Generator");
        GameRegistry.registerTileEntity(TileEntityElectricFurnace.class, "Galacticraft Electric Furnace");
        GameRegistry.registerTileEntity(TileEntityAluminumWire.class, "Galacticraft Aluminum Wire");
        GameRegistry.registerTileEntity(TileEntityFallenMeteor.class, "Fallen Meteor");
        GameRegistry.registerTileEntity(TileEntityIngotCompressor.class, "Ingot Compressor");
        GameRegistry.registerTileEntity(TileEntityElectricIngotCompressor.class, "Electric Ingot Compressor");
        GameRegistry.registerTileEntity(TileEntityCircuitFabricator.class, "Circuit Fabricator");
        GameRegistry.registerTileEntity(TileEntityAirLockController.class, "Air Lock Controller");
        GameRegistry.registerTileEntity(TileEntityOxygenStorageModule.class, "Oxygen Storage Module");
        GameRegistry.registerTileEntity(TileEntityOxygenDecompressor.class, "Oxygen Decompressor");
        GameRegistry.registerTileEntity(TileEntityThruster.class, "Space Station Thruster");
        GameRegistry.registerTileEntity(TileEntityArclamp.class, "Arc Lamp");
        GameRegistry.registerTileEntity(TileEntityScreen.class, "View Screen");
        GameRegistry.registerTileEntity(TileEntityTelemetry.class, "Telemetry Unit");
    }

    public void registerCreatures() {
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedSpider.class, "EvolvedSpider", 3419431, 11013646);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedZombie.class, "EvolvedZombie", 44975, 7969893);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedCreeper.class, "EvolvedCreeper", 894731, 0);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedSkeleton.class, "EvolvedSkeleton", 12698049, 4802889);
        GCCoreUtil.registerGalacticraftCreature(EntitySkeletonBoss.class, "EvolvedSkeletonBoss", 12698049, 4802889);
        GCCoreUtil.registerGalacticraftCreature(
            EntityAlienVillager.class,
            "AlienVillager",
            ColorUtil.to32BitColor(255, 103, 145, 181),
            12422002);
    }

    public void registerOtherEntities() {
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityTier1Rocket.class, "Spaceship", 150, 1, false);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMeteor.class, "Meteor", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityBuggy.class, "Buggy", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityFlag.class, "GCFlag", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityParachest.class, "ParaChest", 150, 5, true);
        // GCCoreUtil.registerGalacticraftNonMobEntity(EntityBubble.class,
        // "OxygenBubble", 150, 20, false);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityLander.class, "Lander", 150, 5, false);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMeteorChunk.class, "MeteorChunk", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityCelestialFake.class, "CelestialScreen", 150, 5, false);
    }

    public Planet makeUnreachablePlanet(String name, SolarSystem system) {
        final ArrayList<CelestialBody> cBodyList = new ArrayList<>();
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredPlanets()
                .values());
        for (final CelestialBody body : cBodyList) {
            if (body instanceof Planet && name.equals(body.getName())
                && ((Planet) body).getParentSolarSystem() == system) {
                return null;
            }
        }

        final Planet planet = new Planet(name).setParentSolarSystem(system);
        planet.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/" + name + ".png"));
        GalaxyRegistry.registerPlanet(planet);
        return planet;
    }
}
