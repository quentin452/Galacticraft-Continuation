package micdoodle8.mods.galacticraft.core;

import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.event.*;
import java.io.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.world.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import api.player.server.*;
import net.minecraft.world.biome.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.schematic.*;
import micdoodle8.mods.galacticraft.core.energy.grid.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.api.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.*;
import net.minecraftforge.fluids.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.client.*;
import micdoodle8.mods.galacticraft.core.world.gen.*;
import micdoodle8.mods.galacticraft.core.recipe.*;
import micdoodle8.mods.galacticraft.core.client.gui.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import javax.imageio.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.command.*;
import micdoodle8.mods.galacticraft.core.command.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.block.*;
import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.blocks.*;

@Mod(modid = "GalacticraftCore", name = "Galacticraft Core", version = "3.0.12", acceptedMinecraftVersions = "[1.7.2],[1.7.10]", useMetadata = true, dependencies = "required-after:Forge@[10.12.2.1147,); required-after:FML@[7.2.217.1147,); required-after:Micdoodlecore; after:IC2; after:TConstruct; after:Mantle; after:BuildCraft|Core; after:BuildCraft|Energy; after:PlayerAPI@[1.3,)", guiFactory = "micdoodle8.mods.galacticraft.core.client.gui.screen.ConfigGuiFactoryCore")
public class GalacticraftCore
{
    public static final String NAME = "Galacticraft Core";
    @SidedProxy(clientSide = "micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore", serverSide = "micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore")
    public static CommonProxyCore proxy;
    @Mod.Instance("GalacticraftCore")
    public static GalacticraftCore instance;
    public static boolean isPlanetsLoaded;
    public static boolean isHeightConflictingModInstalled;
    public static GalacticraftChannelHandler packetPipeline;
    public static GCPlayerHandler handler;
    public static CreativeTabs galacticraftBlocksTab;
    public static CreativeTabs galacticraftItemsTab;
    public static SolarSystem solarSystemSol;
    public static Planet planetMercury;
    public static Planet planetVenus;
    public static Planet planetMars;
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
    public static String ASSET_PREFIX;
    public static String TEXTURE_PREFIX;
    public static String PREFIX;
    public static Fluid fluidOil;
    public static Fluid fluidFuel;
    public static Material materialOil;
    public static HashMap<String, ItemStack> itemList;
    public static HashMap<String, ItemStack> blocksList;
    public static ImageWriter jpgWriter;
    public static ImageWriteParam writeParam;
    public static boolean enableJPEG;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        GalacticraftCore.isPlanetsLoaded = Loader.isModLoaded("GalacticraftMars");
        GCCoreUtil.nextID = 0;
        if (Loader.isModLoaded("SmartMoving")) {
            GalacticraftCore.isHeightConflictingModInstalled = true;
        }
        if (Loader.isModLoaded("witchery")) {
            GalacticraftCore.isHeightConflictingModInstalled = true;
        }
        MinecraftForge.EVENT_BUS.register((Object)new EventHandlerGC());
        GalacticraftCore.handler = new GCPlayerHandler();
        MinecraftForge.EVENT_BUS.register((Object)GalacticraftCore.handler);
        FMLCommonHandler.instance().bus().register((Object)GalacticraftCore.handler);
        GalacticraftCore.proxy.preInit(event);
        (ConnectionPacket.bus = NetworkRegistry.INSTANCE.newEventDrivenChannel("galacticraft")).register((Object)new ConnectionPacket());
        ConfigManagerCore.initialize(new File(event.getModConfigurationDirectory(), "Galacticraft/core.conf"));
        EnergyConfigHandler.setDefaultValues(new File(event.getModConfigurationDirectory(), "Galacticraft/power-GC3.conf"));
        ChunkLoadingCallback.loadConfig(new File(event.getModConfigurationDirectory(), "Galacticraft/chunkloading.conf"));
        this.registerOilandFuel();
        if (Loader.isModLoaded("PlayerAPI")) {
            ServerPlayerAPI.register("GalacticraftCore", (Class)GCPlayerBaseMP.class);
        }
        GCBlocks.initBlocks();
        GCItems.initItems();
        FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(GalacticraftCore.fluidFuel, 1000), new ItemStack(GCItems.fuelCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
        FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(GalacticraftCore.fluidOil, 1000), new ItemStack(GCItems.oilCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
        final BiomeGenBase biomeOrbitPreInit = BiomeGenBaseOrbit.space;
        final BiomeGenBase biomeMoonPreInit = BiomeGenBaseMoon.moonFlat;
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        GalacticraftCore.galacticraftBlocksTab = new CreativeTabGC(CreativeTabs.getNextID(), "GalacticraftBlocks", Item.getItemFromBlock(GCBlocks.machineBase2), 0);
        GalacticraftCore.galacticraftItemsTab = new CreativeTabGC(CreativeTabs.getNextID(), "GalacticraftItems", GCItems.rocketTier1, 0);
        GalacticraftCore.proxy.init(event);
        GalacticraftCore.packetPipeline = GalacticraftChannelHandler.init();
        GalacticraftCore.solarSystemSol = new SolarSystem("sol", "milkyWay").setMapPosition(new Vector3(0.0f, 0.0f));
        final Star starSol = (Star)new Star("sol").setParentSolarSystem(GalacticraftCore.solarSystemSol).setTierRequired(-1);
        starSol.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/sun.png"));
        GalacticraftCore.solarSystemSol.setMainStar(starSol);
        (GalacticraftCore.planetOverworld = (Planet)new Planet("overworld").setParentSolarSystem(GalacticraftCore.solarSystemSol).setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(0.0f)).setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png"));
        GalacticraftCore.planetOverworld.setDimensionInfo(ConfigManagerCore.idDimensionOverworld, (Class)WorldProvider.class, false).setTierRequired(1);
        GalacticraftCore.planetOverworld.atmosphereComponent(IAtmosphericGas.NITROGEN).atmosphereComponent(IAtmosphericGas.OXYGEN).atmosphereComponent(IAtmosphericGas.ARGON).atmosphereComponent(IAtmosphericGas.WATER);
        GalacticraftCore.moonMoon = (Moon)new Moon("moon").setParentPlanet(GalacticraftCore.planetOverworld).setRelativeSize(0.2667f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(13.0f, 13.0f)).setRelativeOrbitTime(100.0f);
        GalacticraftCore.moonMoon.setDimensionInfo(ConfigManagerCore.idDimensionMoon, (Class)WorldProviderMoon.class).setTierRequired(1);
        GalacticraftCore.moonMoon.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/moon.png"));
        GalacticraftCore.satelliteSpaceStation = (Satellite)new Satellite("spaceStation.overworld").setParentBody(GalacticraftCore.planetOverworld).setRelativeSize(0.2667f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(9.0f, 9.0f)).setRelativeOrbitTime(20.0f);
        GalacticraftCore.satelliteSpaceStation.setDimensionInfo(ConfigManagerCore.idDimensionOverworldOrbit, ConfigManagerCore.idDimensionOverworldOrbitStatic, (Class)WorldProviderOrbit.class).setTierRequired(1);
        GalacticraftCore.satelliteSpaceStation.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/spaceStation.png"));
        ForgeChunkManager.setForcedChunkLoadingCallback((Object)GalacticraftCore.instance, (ForgeChunkManager.LoadingCallback)new ChunkLoadingCallback());
        FMLCommonHandler.instance().bus().register((Object)new ConnectionEvents());
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicRocketT1());
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicMoonBuggy());
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicAdd());
        ChunkPowerHandler.initiate();
        EnergyConfigHandler.initGas();
        this.registerMicroBlocks();
        this.registerCreatures();
        this.registerOtherEntities();
        this.registerTileEntities();
        GalaxyRegistry.registerSolarSystem(GalacticraftCore.solarSystemSol);
        GalaxyRegistry.registerPlanet(GalacticraftCore.planetOverworld);
        GalaxyRegistry.registerMoon(GalacticraftCore.moonMoon);
        GalaxyRegistry.registerSatellite(GalacticraftCore.satelliteSpaceStation);
        GalacticraftRegistry.registerProvider(ConfigManagerCore.idDimensionOverworldOrbit, (Class)WorldProviderOrbit.class, false, 0);
        GalacticraftRegistry.registerProvider(ConfigManagerCore.idDimensionOverworldOrbitStatic, (Class)WorldProviderOrbit.class, true, 0);
        GalacticraftRegistry.registerTeleportType((Class)WorldProviderSurface.class, (ITeleportType)new TeleportTypeOverworld());
        GalacticraftRegistry.registerTeleportType((Class)WorldProviderOrbit.class, (ITeleportType)new TeleportTypeOrbit());
        GalacticraftRegistry.registerTeleportType((Class)WorldProviderMoon.class, (ITeleportType)new TeleportTypeMoon());
        GalacticraftRegistry.registerRocketGui((Class)WorldProviderOrbit.class, new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/overworldRocketGui.png"));
        GalacticraftRegistry.registerRocketGui((Class)WorldProviderSurface.class, new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/overworldRocketGui.png"));
        GalacticraftRegistry.registerRocketGui((Class)WorldProviderMoon.class, new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/moonRocketGui.png"));
        GalacticraftRegistry.addDungeonLoot(1, new ItemStack(GCItems.schematic, 1, 0));
        GalacticraftRegistry.addDungeonLoot(1, new ItemStack(GCItems.schematic, 1, 1));
        if (ConfigManagerCore.enableCopperOreGen) {
            GameRegistry.registerWorldGenerator((IWorldGenerator)new OverworldGenerator(GCBlocks.basicBlock, 5, 24, 0, 75, 7), 4);
        }
        if (ConfigManagerCore.enableTinOreGen) {
            GameRegistry.registerWorldGenerator((IWorldGenerator)new OverworldGenerator(GCBlocks.basicBlock, 6, 22, 0, 60, 7), 4);
        }
        if (ConfigManagerCore.enableAluminumOreGen) {
            GameRegistry.registerWorldGenerator((IWorldGenerator)new OverworldGenerator(GCBlocks.basicBlock, 7, 18, 0, 45, 7), 4);
        }
        if (ConfigManagerCore.enableSiliconOreGen) {
            GameRegistry.registerWorldGenerator((IWorldGenerator)new OverworldGenerator(GCBlocks.basicBlock, 8, 3, 0, 25, 7), 4);
        }
        FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://www.patreon.com/micdoodle8");
        registerCoreGameScreens();
        if (ConfigManagerCore.useOldFuelFluidID && FluidRegistry.isFluidRegistered("fuel")) {
            FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(FluidRegistry.getFluid("fuel"), 1000), new ItemStack(GCItems.fuelCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
        }
        if (ConfigManagerCore.useOldOilFluidID && FluidRegistry.isFluidRegistered("oil")) {
            FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(FluidRegistry.getFluid("oil"), 1000), new ItemStack(GCItems.oilCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
            if (CompatibilityManager.isBCraftEnergyLoaded()) {
                FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(GalacticraftCore.fluidOil, 1000), GameRegistry.findItemStack("BuildCraft|Core", "bucketOil", 1), new ItemStack(Items.bucket)));
            }
        }
        if (!FluidRegistry.isFluidRegistered("oil")) {
            FluidRegistry.registerFluid(new Fluid("oil").setDensity(800).setViscosity(1500));
        }
        if (!FluidRegistry.isFluidRegistered("oilgc")) {
            FluidRegistry.registerFluid(new Fluid("oilgc").setDensity(800).setViscosity(1500));
        }
        if (!FluidRegistry.isFluidRegistered("fuel")) {
            FluidRegistry.registerFluid(new Fluid("fuel").setDensity(400).setViscosity(900));
        }
        if (!FluidRegistry.isFluidRegistered("fuelgc")) {
            FluidRegistry.registerFluid(new Fluid("fuelgc").setDensity(400).setViscosity(900));
        }
    }
    
    private void registerOilandFuel() {
        String oilID = "oil";
        String fuelID = "fuel";
        if (ConfigManagerCore.useOldOilFluidID) {
            oilID = "oilgc";
        }
        if (ConfigManagerCore.useOldFuelFluidID) {
            fuelID = "fuelgc";
        }
        if (!FluidRegistry.isFluidRegistered(oilID)) {
            final Fluid gcFluidOil = new Fluid(oilID).setDensity(800).setViscosity(1500);
            FluidRegistry.registerFluid(gcFluidOil);
        }
        else {
            GCLog.info("Galacticraft oil is not default, issues may occur.");
        }
        GalacticraftCore.fluidOil = FluidRegistry.getFluid(oilID);
        if (GalacticraftCore.fluidOil.getBlock() == null) {
            GCBlocks.crudeOil = (Block)new BlockFluidGC(GalacticraftCore.fluidOil, "oil");
            ((BlockFluidGC)GCBlocks.crudeOil).setQuantaPerBlock(3);
            GCBlocks.crudeOil.setBlockName("crudeOilStill");
            GameRegistry.registerBlock(GCBlocks.crudeOil, (Class)ItemBlockGC.class, GCBlocks.crudeOil.getUnlocalizedName());
            GalacticraftCore.fluidOil.setBlock(GCBlocks.crudeOil);
        }
        else {
            GCBlocks.crudeOil = GalacticraftCore.fluidOil.getBlock();
        }
        if (GCBlocks.crudeOil != null && Item.itemRegistry.getObject("buildcraftenergy:items/bucketOil") == null) {
            (GCItems.bucketOil = (Item)new ItemBucketGC(GCBlocks.crudeOil, GalacticraftCore.TEXTURE_PREFIX)).setUnlocalizedName("bucketOil");
            GCItems.registerItem(GCItems.bucketOil);
            FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack(oilID, 1000), new ItemStack(GCItems.bucketOil), new ItemStack(Items.bucket));
        }
        EventHandlerGC.bucketList.put(GCBlocks.crudeOil, GCItems.bucketOil);
        if (!FluidRegistry.isFluidRegistered(fuelID)) {
            final Fluid gcFluidFuel = new Fluid(fuelID).setDensity(400).setViscosity(900);
            FluidRegistry.registerFluid(gcFluidFuel);
        }
        else {
            GCLog.info("Galacticraft fuel is not default, issues may occur.");
        }
        GalacticraftCore.fluidFuel = FluidRegistry.getFluid(fuelID);
        if (GalacticraftCore.fluidFuel.getBlock() == null) {
            GCBlocks.fuel = (Block)new BlockFluidGC(GalacticraftCore.fluidFuel, "fuel");
            ((BlockFluidGC)GCBlocks.fuel).setQuantaPerBlock(3);
            GCBlocks.fuel.setBlockName("fuel");
            GameRegistry.registerBlock(GCBlocks.fuel, (Class)ItemBlockGC.class, GCBlocks.fuel.getUnlocalizedName());
            GalacticraftCore.fluidFuel.setBlock(GCBlocks.fuel);
        }
        else {
            GCBlocks.fuel = GalacticraftCore.fluidFuel.getBlock();
        }
        if (GCBlocks.fuel != null && Item.itemRegistry.getObject("buildcraftenergy:items/bucketFuel") == null) {
            (GCItems.bucketFuel = (Item)new ItemBucketGC(GCBlocks.fuel, GalacticraftCore.TEXTURE_PREFIX)).setUnlocalizedName("bucketFuel");
            GCItems.registerItem(GCItems.bucketFuel);
            FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack(fuelID, 1000), new ItemStack(GCItems.bucketFuel), new ItemStack(Items.bucket));
        }
        EventHandlerGC.bucketList.put(GCBlocks.fuel, GCItems.bucketFuel);
    }
    
    public static void registerCoreGameScreens() {
        final IGameScreen rendererBasic = (IGameScreen)new GameScreenBasic();
        final IGameScreen rendererCelest = (IGameScreen)new GameScreenCelestial();
        GalacticraftRegistry.registerScreen(rendererBasic);
        GalacticraftRegistry.registerScreen(rendererBasic);
        GalacticraftRegistry.registerScreen(rendererCelest);
        GalacticraftRegistry.registerScreen(rendererCelest);
        GalacticraftRegistry.registerScreen(rendererCelest);
    }
    
    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        GalacticraftCore.planetMercury = this.makeUnreachablePlanet("mercury", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetMercury != null) {
            GalacticraftCore.planetMercury.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(1.45f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.5f, 0.5f)).setRelativeOrbitTime(0.24096386f);
        }
        GalacticraftCore.planetVenus = this.makeUnreachablePlanet("venus", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetVenus != null) {
            GalacticraftCore.planetVenus.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(2.0f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.75f, 0.75f)).setRelativeOrbitTime(0.6152793f);
        }
        GalacticraftCore.planetMars = this.makeUnreachablePlanet("mars", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetMars != null) {
            GalacticraftCore.planetMars.setRingColorRGB(0.67f, 0.1f, 0.1f).setPhaseShift(0.1667f).setRelativeSize(0.5319f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.25f, 1.25f)).setRelativeOrbitTime(1.881161f);
        }
        GalacticraftCore.planetJupiter = this.makeUnreachablePlanet("jupiter", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetJupiter != null) {
            GalacticraftCore.planetJupiter.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(3.1415927f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.5f, 1.5f)).setRelativeOrbitTime(11.861994f);
        }
        GalacticraftCore.planetSaturn = this.makeUnreachablePlanet("saturn", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetSaturn != null) {
            GalacticraftCore.planetSaturn.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(5.45f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.75f, 1.75f)).setRelativeOrbitTime(29.463308f);
        }
        GalacticraftCore.planetUranus = this.makeUnreachablePlanet("uranus", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetUranus != null) {
            GalacticraftCore.planetUranus.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(1.38f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.0f, 2.0f)).setRelativeOrbitTime(84.06353f);
        }
        GalacticraftCore.planetNeptune = this.makeUnreachablePlanet("neptune", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetNeptune != null) {
            GalacticraftCore.planetNeptune.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(1.0f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.25f, 2.25f)).setRelativeOrbitTime(164.84119f);
        }
        MinecraftForge.EVENT_BUS.register((Object)new OreGenOtherMods());
        GalacticraftCore.proxy.postInit(event);
        final ArrayList<CelestialBody> cBodyList = new ArrayList<CelestialBody>();
        cBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        cBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        for (final CelestialBody body : cBodyList) {
            if (body.shouldAutoRegister()) {
                final int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, body.getDimensionID());
                if (GalacticraftRegistry.registerProvider(body.getDimensionID(), body.getWorldProvider(), body.getForceStaticLoad() || id < 0, 0)) {
                    continue;
                }
                body.setUnreachable();
            }
        }
        CompatibilityManager.checkForCompatibleMods();
        RecipeManagerGC.loadRecipes();
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)GalacticraftCore.instance, (IGuiHandler)new GuiHandler());
        FMLCommonHandler.instance().bus().register((Object)new TickHandlerServer());
        GalaxyRegistry.refreshGalaxies();
        GalacticraftRegistry.registerScreen((IGameScreen)new GameScreenText());
        try {
            GalacticraftCore.jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            (GalacticraftCore.writeParam = GalacticraftCore.jpgWriter.getDefaultWriteParam()).setCompressionMode(2);
            GalacticraftCore.writeParam.setCompressionQuality(1.0f);
            GalacticraftCore.enableJPEG = true;
        }
        catch (UnsatisfiedLinkError e) {
            GCLog.severe("Error initialising JPEG compressor - this is likely caused by OpenJDK - see https://wiki.micdoodle8.com/wiki/Compatibility#For_Linux_servers_running_OpenJDK");
            e.printStackTrace();
        }
    }
    
    @Mod.EventHandler
    public void serverInit(final FMLServerStartedEvent event) {
        if (ThreadRequirementMissing.INSTANCE == null) {
            ThreadRequirementMissing.beginCheck(FMLCommonHandler.instance().getEffectiveSide());
        }
        ThreadVersionCheck.startCheck();
        TickHandlerServer.restart();
        BlockVec3.chunkCacheDim = Integer.MAX_VALUE;
    }
    
    @Mod.EventHandler
    public void serverStarting(final FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandSpaceStationAddOwner());
        event.registerServerCommand((ICommand)new CommandSpaceStationChangeOwner());
        event.registerServerCommand((ICommand)new CommandSpaceStationRemoveOwner());
        event.registerServerCommand((ICommand)new CommandPlanetTeleport());
        event.registerServerCommand((ICommand)new CommandKeepDim());
        event.registerServerCommand((ICommand)new CommandGCInv());
        event.registerServerCommand((ICommand)new CommandGCHelp());
        event.registerServerCommand((ICommand)new CommandGCEnergyUnits());
        event.registerServerCommand((ICommand)new CommandJoinSpaceRace());
        WorldUtil.initialiseDimensionNames();
        WorldUtil.registerSpaceStations(event.getServer().worldServerForDimension(0).getSaveHandler().getMapFileFromName("dummy").getParentFile());
        final ArrayList<CelestialBody> cBodyList = new ArrayList<CelestialBody>();
        cBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        cBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        for (final CelestialBody body : cBodyList) {
            if (body.shouldAutoRegister() && !WorldUtil.registerPlanet(body.getDimensionID(), body.getReachable(), 0)) {
                body.setUnreachable();
            }
        }
        RecipeManagerGC.setConfigurableRecipes();
    }
    
    @Mod.EventHandler
    public void unregisterDims(final FMLServerStoppedEvent var1) {
        WorldUtil.unregisterPlanets();
        WorldUtil.unregisterSpaceStations();
    }
    
    private void registerMicroBlocks() {
        try {
            final Class clazz = Class.forName("codechicken.microblock.MicroMaterialRegistry");
            if (clazz != null) {
                Method registerMethod = null;
                final Method[] methods;
                final Method[] methodz = methods = clazz.getMethods();
                for (final Method m : methods) {
                    if (m.getName().equals("registerMaterial")) {
                        registerMethod = m;
                        break;
                    }
                }
                final Class clazzbm = Class.forName("codechicken.microblock.BlockMicroMaterial");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.basicBlock, 3), "tile.gcBlockCore.decoblock1");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.basicBlock, 4), "tile.gcBlockCore.decoblock2");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.basicBlock, 9), "tile.gcBlockCore.copperBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.basicBlock, 10), "tile.gcBlockCore.tinBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.basicBlock, 11), "tile.gcBlockCore.aluminumBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.basicBlock, 12), "tile.gcBlockCore.meteorironBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.blockMoon, 3), "tile.moonBlock.moondirt");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.blockMoon, 4), "tile.moonBlock.moonstone");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.blockMoon, 5), "tile.moonBlock.moongrass");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(GCBlocks.blockMoon, 14), "tile.moonBlock.bricks");
            }
        }
        catch (Exception ex) {}
    }
    
    public void registerTileEntities() {
        GameRegistry.registerTileEntity((Class)TileEntityTreasureChest.class, CompatibilityManager.isAIILoaded() ? "Space Treasure Chest" : "Treasure Chest");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenDistributor.class, "Air Distributor");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenCollector.class, "Air Collector");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenPipe.class, "Oxygen Pipe");
        GameRegistry.registerTileEntity((Class)TileEntityAirLock.class, "Air Lock Frame");
        GameRegistry.registerTileEntity((Class)TileEntityRefinery.class, "Refinery");
        GameRegistry.registerTileEntity((Class)TileEntityNasaWorkbench.class, "NASA Workbench");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenCompressor.class, "Air Compressor");
        GameRegistry.registerTileEntity((Class)TileEntityFuelLoader.class, "Fuel Loader");
        GameRegistry.registerTileEntity((Class)TileEntityLandingPadSingle.class, "Landing Pad");
        GameRegistry.registerTileEntity((Class)TileEntityLandingPad.class, "Landing Pad Full");
        GameRegistry.registerTileEntity((Class)TileEntitySpaceStationBase.class, "Space Station");
        GameRegistry.registerTileEntity((Class)TileEntityMulti.class, "Dummy Block");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenSealer.class, "Air Sealer");
        GameRegistry.registerTileEntity((Class)TileEntityDungeonSpawner.class, "Dungeon Boss Spawner");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenDetector.class, "Oxygen Detector");
        GameRegistry.registerTileEntity((Class)TileEntityBuggyFueler.class, "Buggy Fueler");
        GameRegistry.registerTileEntity((Class)TileEntityBuggyFuelerSingle.class, "Buggy Fueler Single");
        GameRegistry.registerTileEntity((Class)TileEntityCargoLoader.class, "Cargo Loader");
        GameRegistry.registerTileEntity((Class)TileEntityCargoUnloader.class, "Cargo Unloader");
        GameRegistry.registerTileEntity((Class)TileEntityParaChest.class, "Parachest Tile");
        GameRegistry.registerTileEntity((Class)TileEntitySolar.class, "Galacticraft Solar Panel");
        GameRegistry.registerTileEntity((Class)TileEntityDish.class, "Radio Telescope");
        GameRegistry.registerTileEntity((Class)TileEntityEnergyStorageModule.class, "Energy Storage Module");
        GameRegistry.registerTileEntity((Class)TileEntityCoalGenerator.class, "Galacticraft Coal Generator");
        GameRegistry.registerTileEntity((Class)TileEntityElectricFurnace.class, "Galacticraft Electric Furnace");
        GameRegistry.registerTileEntity((Class)TileEntityAluminumWire.class, "Galacticraft Aluminum Wire");
        GameRegistry.registerTileEntity((Class)TileEntityFallenMeteor.class, "Fallen Meteor");
        GameRegistry.registerTileEntity((Class)TileEntityIngotCompressor.class, "Ingot Compressor");
        GameRegistry.registerTileEntity((Class)TileEntityElectricIngotCompressor.class, "Electric Ingot Compressor");
        GameRegistry.registerTileEntity((Class)TileEntityCircuitFabricator.class, "Circuit Fabricator");
        GameRegistry.registerTileEntity((Class)TileEntityAirLockController.class, "Air Lock Controller");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenStorageModule.class, "Oxygen Storage Module");
        GameRegistry.registerTileEntity((Class)TileEntityOxygenDecompressor.class, "Oxygen Decompressor");
        GameRegistry.registerTileEntity((Class)TileEntityThruster.class, "Space Station Thruster");
        GameRegistry.registerTileEntity((Class)TileEntityArclamp.class, "Arc Lamp");
        GameRegistry.registerTileEntity((Class)TileEntityScreen.class, "View Screen");
        GameRegistry.registerTileEntity((Class)TileEntityTelemetry.class, "Telemetry Unit");
    }
    
    public void registerCreatures() {
        GCCoreUtil.registerGalacticraftCreature((Class<? extends Entity>)EntityEvolvedSpider.class, "EvolvedSpider", 3419431, 11013646);
        GCCoreUtil.registerGalacticraftCreature((Class<? extends Entity>)EntityEvolvedZombie.class, "EvolvedZombie", 44975, 7969893);
        GCCoreUtil.registerGalacticraftCreature((Class<? extends Entity>)EntityEvolvedCreeper.class, "EvolvedCreeper", 894731, 0);
        GCCoreUtil.registerGalacticraftCreature((Class<? extends Entity>)EntityEvolvedSkeleton.class, "EvolvedSkeleton", 12698049, 4802889);
        GCCoreUtil.registerGalacticraftCreature((Class<? extends Entity>)EntitySkeletonBoss.class, "EvolvedSkeletonBoss", 12698049, 4802889);
        GCCoreUtil.registerGalacticraftCreature((Class<? extends Entity>)EntityAlienVillager.class, "AlienVillager", ColorUtil.to32BitColor(255, 103, 145, 181), 12422002);
    }
    
    public void registerOtherEntities() {
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityTier1Rocket.class, "Spaceship", 150, 1, false);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityMeteor.class, "Meteor", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityBuggy.class, "Buggy", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityFlag.class, "GCFlag", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityParachest.class, "ParaChest", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityLander.class, "Lander", 150, 5, false);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityMeteorChunk.class, "MeteorChunk", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityCelestialFake.class, "CelestialScreen", 150, 5, false);
    }
    
    public Planet makeUnreachablePlanet(final String name, final SolarSystem system) {
        final ArrayList<CelestialBody> cBodyList = new ArrayList<CelestialBody>();
        cBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        for (final CelestialBody body : cBodyList) {
            if (body instanceof Planet && name.equals(body.getName()) && ((Planet)body).getParentSolarSystem() == system) {
                return null;
            }
        }
        final Planet planet = new Planet(name).setParentSolarSystem(system);
        planet.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/" + name + ".png"));
        GalaxyRegistry.registerPlanet(planet);
        return planet;
    }
    
    static {
        GalacticraftCore.ASSET_PREFIX = "galacticraftcore";
        GalacticraftCore.TEXTURE_PREFIX = GalacticraftCore.ASSET_PREFIX + ":";
        GalacticraftCore.PREFIX = "micdoodle8.";
        GalacticraftCore.materialOil = (Material)new MaterialOleaginous(MapColor.brownColor);
        GalacticraftCore.itemList = new HashMap<String, ItemStack>();
        GalacticraftCore.blocksList = new HashMap<String, ItemStack>();
        GalacticraftCore.enableJPEG = false;
    }
}
