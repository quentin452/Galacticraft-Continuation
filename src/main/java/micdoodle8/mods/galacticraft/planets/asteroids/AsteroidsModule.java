package micdoodle8.mods.galacticraft.planets.asteroids;

import micdoodle8.mods.galacticraft.planets.*;
import java.io.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.player.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.planets.asteroids.event.*;
import net.minecraftforge.oredict.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.network.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tick.*;
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.recipe.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import java.util.zip.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import java.util.regex.*;
import micdoodle8.mods.galacticraft.core.command.*;
import net.minecraft.command.*;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.*;
import cpw.mods.fml.common.event.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import java.lang.reflect.*;
import cpw.mods.fml.common.registry.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraftforge.common.config.*;

public class AsteroidsModule implements IPlanetsModule
{
    public static Planet planetAsteroids;
    private File GCPlanetsSource;
    public static final String ASSET_PREFIX = "galacticraftasteroids";
    public static final String TEXTURE_PREFIX = "galacticraftasteroids:";
    public static AsteroidsPlayerHandler playerHandler;
    public static Fluid fluidMethaneGas;
    public static Fluid fluidOxygenGas;
    public static Fluid fluidNitrogenGas;
    public static Fluid fluidLiquidMethane;
    public static Fluid fluidLiquidOxygen;
    public static Fluid fluidLiquidNitrogen;
    public static Fluid fluidLiquidArgon;
    public static Fluid fluidAtmosphericGases;
    
    private Fluid registerFluid(final String fluidName, final int density, final int viscosity, final int temperature, final boolean gaseous) {
        Fluid returnFluid = FluidRegistry.getFluid(fluidName);
        if (returnFluid == null) {
            FluidRegistry.registerFluid(new Fluid(fluidName).setDensity(density).setViscosity(viscosity).setTemperature(temperature).setGaseous(gaseous));
            returnFluid = FluidRegistry.getFluid(fluidName);
        }
        return returnFluid;
    }
    
    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        this.GCPlanetsSource = event.getSourceFile();
        AsteroidsModule.playerHandler = new AsteroidsPlayerHandler();
        MinecraftForge.EVENT_BUS.register((Object)AsteroidsModule.playerHandler);
        FMLCommonHandler.instance().bus().register((Object)AsteroidsModule.playerHandler);
        final AsteroidsEventHandler eventHandler = new AsteroidsEventHandler();
        MinecraftForge.EVENT_BUS.register((Object)eventHandler);
        FMLCommonHandler.instance().bus().register((Object)eventHandler);
        RecipeSorter.register("galacticraftmars:canisterRecipe", (Class)CanisterRecipes.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        AsteroidsModule.fluidMethaneGas = this.registerFluid("methane", 1, 11, 295, true);
        AsteroidsModule.fluidAtmosphericGases = this.registerFluid("atmosphericgases", 1, 13, 295, true);
        AsteroidsModule.fluidLiquidMethane = this.registerFluid("liquidmethane", 450, 120, 109, false);
        AsteroidsModule.fluidLiquidOxygen = this.registerFluid("liquidoxygen", 1141, 140, 90, false);
        AsteroidsModule.fluidOxygenGas = this.registerFluid("oxygen", 1, 13, 295, true);
        AsteroidsModule.fluidLiquidNitrogen = this.registerFluid("liquidnitrogen", 808, 130, 90, false);
        AsteroidsModule.fluidNitrogenGas = this.registerFluid("nitrogen", 1, 12, 295, true);
        this.registerFluid("carbondioxide", 2, 20, 295, true);
        this.registerFluid("hydrogen", 1, 1, 295, true);
        this.registerFluid("argon", 1, 4, 295, true);
        AsteroidsModule.fluidLiquidArgon = this.registerFluid("liquidargon", 900, 100, 87, false);
        this.registerFluid("helium", 1, 1, 295, true);
        AsteroidBlocks.initBlocks();
        AsteroidBlocks.registerBlocks();
        AsteroidBlocks.setHarvestLevels();
        AsteroidBlocks.oreDictRegistration();
        AsteroidsItems.initItems();
        FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(AsteroidsModule.fluidMethaneGas, 1000), new ItemStack(AsteroidsItems.methaneCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
        FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(AsteroidsModule.fluidLiquidOxygen, 1000), new ItemStack(AsteroidsItems.canisterLOX, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
        FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(AsteroidsModule.fluidLiquidNitrogen, 1000), new ItemStack(AsteroidsItems.canisterLN2, 1, 1), new ItemStack(GCItems.oilCanister, 1, 1001)));
    }
    
    @Override
    public void init(final FMLInitializationEvent event) {
        this.registerMicroBlocks();
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicTier3Rocket());
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicAstroMiner());
        GalacticraftCore.packetPipeline.addDiscriminator(7, (Class)PacketSimpleAsteroids.class);
        final AsteroidsTickHandlerServer eventHandler = new AsteroidsTickHandlerServer();
        FMLCommonHandler.instance().bus().register((Object)eventHandler);
        MinecraftForge.EVENT_BUS.register((Object)eventHandler);
        this.registerEntities();
        RecipeManagerAsteroids.loadRecipes();
        AsteroidsModule.planetAsteroids = new Planet("asteroids").setParentSolarSystem(GalacticraftCore.solarSystemSol);
        AsteroidsModule.planetAsteroids.setDimensionInfo(ConfigManagerAsteroids.dimensionIDAsteroids, (Class)WorldProviderAsteroids.class).setTierRequired(3);
        AsteroidsModule.planetAsteroids.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.375f, 1.375f)).setRelativeOrbitTime(45.0f).setPhaseShift((float)(Math.random() * 6.283185307179586));
        AsteroidsModule.planetAsteroids.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/asteroid.png"));
        GalaxyRegistry.registerPlanet(AsteroidsModule.planetAsteroids);
        GalacticraftRegistry.registerTeleportType((Class)WorldProviderAsteroids.class, (ITeleportType)new TeleportTypeAsteroids());
        HashMap<Integer, ItemStack> input = new HashMap<Integer, ItemStack>();
        input.put(1, new ItemStack((Item)AsteroidsItems.heavyNoseCone));
        input.put(2, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(3, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(4, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(5, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(6, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(7, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(8, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(9, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(10, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(11, new ItemStack(AsteroidsItems.basicItem, 1, 0));
        input.put(12, new ItemStack(GCItems.rocketEngine, 1, 1));
        input.put(13, new ItemStack(AsteroidsItems.basicItem, 1, 2));
        input.put(14, new ItemStack(AsteroidsItems.basicItem, 1, 2));
        input.put(15, new ItemStack(AsteroidsItems.basicItem, 1, 1));
        input.put(16, new ItemStack(GCItems.rocketEngine, 1, 1));
        input.put(17, new ItemStack(AsteroidsItems.basicItem, 1, 2));
        input.put(18, new ItemStack(AsteroidsItems.basicItem, 1, 2));
        input.put(19, null);
        input.put(20, null);
        input.put(21, null);
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 0), (HashMap)input));
        HashMap<Integer, ItemStack> input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack((Block)Blocks.chest));
        input2.put(20, null);
        input2.put(21, null);
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), (HashMap)input2));
        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, null);
        input2.put(20, new ItemStack((Block)Blocks.chest));
        input2.put(21, null);
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), (HashMap)input2));
        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, null);
        input2.put(20, null);
        input2.put(21, new ItemStack((Block)Blocks.chest));
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), (HashMap)input2));
        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack((Block)Blocks.chest));
        input2.put(20, new ItemStack((Block)Blocks.chest));
        input2.put(21, null);
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), (HashMap)input2));
        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack((Block)Blocks.chest));
        input2.put(20, null);
        input2.put(21, new ItemStack((Block)Blocks.chest));
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), (HashMap)input2));
        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, null);
        input2.put(20, new ItemStack((Block)Blocks.chest));
        input2.put(21, new ItemStack((Block)Blocks.chest));
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), (HashMap)input2));
        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack((Block)Blocks.chest));
        input2.put(20, new ItemStack((Block)Blocks.chest));
        input2.put(21, new ItemStack((Block)Blocks.chest));
        GalacticraftRegistry.addT3RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 3), (HashMap)input2));
        input = new HashMap<Integer, ItemStack>();
        input.put(1, new ItemStack(GCItems.heavyPlatingTier1));
        input.put(3, new ItemStack(GCItems.heavyPlatingTier1));
        input.put(5, new ItemStack(GCItems.heavyPlatingTier1));
        input.put(11, new ItemStack(GCItems.heavyPlatingTier1));
        input.put(2, new ItemStack(AsteroidsItems.orionDrive));
        input.put(4, new ItemStack(AsteroidsItems.orionDrive));
        input.put(9, new ItemStack(AsteroidsItems.orionDrive));
        input.put(10, new ItemStack(AsteroidsItems.orionDrive));
        input.put(12, new ItemStack(AsteroidsItems.orionDrive));
        input.put(6, new ItemStack(GCItems.basicItem, 1, 14));
        input.put(7, new ItemStack((Block)Blocks.chest));
        input.put(8, new ItemStack((Block)Blocks.chest));
        input.put(13, new ItemStack(AsteroidsItems.basicItem, 1, 8));
        input.put(14, new ItemStack(GCItems.flagPole));
        GalacticraftRegistry.addAstroMinerRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.astroMiner, 1, 0), (HashMap)input));
    }
    
    @Override
    public void postInit(final FMLPostInitializationEvent event) {
        try {
            final ZipFile zf = new ZipFile(this.GCPlanetsSource);
            final Pattern assetENUSLang = Pattern.compile("assets/(.*)/lang/(?:.+/|)([\\w_-]+).lang");
            for (final ZipEntry ze : Collections.list(zf.entries())) {
                if (!ze.getName().contains("galacticraftasteroids/lang")) {
                    continue;
                }
                final Matcher matcher = assetENUSLang.matcher(ze.getName());
                if (!matcher.matches()) {
                    continue;
                }
                final String lang = matcher.group(2);
                LanguageRegistry.instance().injectLanguage(lang, StringTranslate.parseLangFile(zf.getInputStream(ze)));
                if (!"en_US".equals(lang) || event.getSide() != Side.SERVER) {
                    continue;
                }
                StringTranslate.inject(zf.getInputStream(ze));
            }
            zf.close();
        }
        catch (Exception ex) {}
    }
    
    @Override
    public void serverStarting(final FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandGCAstroMiner());
        ChunkProviderAsteroids.reset();
    }
    
    @Override
    public void serverInit(final FMLServerStartedEvent event) {
        AsteroidsTickHandlerServer.restart();
    }
    
    @Override
    public void getGuiIDs(final List<Integer> idList) {
        idList.add(3);
    }
    
    @Override
    public Object getGuiElement(final Side side, final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        if (side == Side.SERVER) {
            final TileEntity tile = world.getTileEntity(x, y, z);
            switch (ID) {
                case 3: {
                    if (tile instanceof TileEntityShortRangeTelepad) {
                        return new ContainerShortRangeTelepad(player.inventory, (TileEntityShortRangeTelepad)tile);
                    }
                    if (tile instanceof TileEntityMinerBase) {
                        return new ContainerAstroMinerDock(player.inventory, (IInventory)tile);
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    private void registerEntities() {
        this.registerCreatures();
        this.registerNonMobEntities();
        this.registerTileEntities();
    }
    
    private void registerCreatures() {
    }
    
    private void registerNonMobEntities() {
        MarsModule.registerGalacticraftNonMobEntity(EntitySmallAsteroid.class, "SmallAsteroidGC", 150, 3, true);
        MarsModule.registerGalacticraftNonMobEntity(EntityGrapple.class, "GrappleHookGC", 150, 1, true);
        MarsModule.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityTier3Rocket.class, "Tier3RocketGC", 150, 1, false);
        MarsModule.registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityEntryPod.class, "EntryPodAsteroids", 150, 1, true);
        MarsModule.registerGalacticraftNonMobEntity(EntityAstroMiner.class, "AstroMiner", 80, 1, true);
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
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(AsteroidBlocks.blockBasic, 0), "tile.asteroidsBlock.asteroid0");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(AsteroidBlocks.blockBasic, 1), "tile.asteroidsBlock.asteroid1");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(AsteroidBlocks.blockBasic, 2), "tile.asteroidsBlock.asteroid2");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(AsteroidBlocks.blockDenseIce, 0), "tile.denseIce");
            }
        }
        catch (Exception ex) {}
    }
    
    private void registerTileEntities() {
        GameRegistry.registerTileEntity((Class)TileEntityBeamReflector.class, "Beam Reflector");
        GameRegistry.registerTileEntity((Class)TileEntityBeamReceiver.class, "Beam Receiver");
        GameRegistry.registerTileEntity((Class)TileEntityShortRangeTelepad.class, "Short Range Telepad");
        GameRegistry.registerTileEntity((Class)TileEntityTelepadFake.class, "Fake Short Range Telepad");
        GameRegistry.registerTileEntity((Class)TileEntityTreasureChestAsteroids.class, "Asteroids Treasure Chest");
        GameRegistry.registerTileEntity((Class)TileEntityMinerBaseSingle.class, "Astro Miner Base Builder");
        GameRegistry.registerTileEntity((Class)TileEntityMinerBase.class, "Astro Miner Base");
    }
    
    @Override
    public Configuration getConfiguration() {
        return ConfigManagerAsteroids.config;
    }
    
    @Override
    public void syncConfig() {
        ConfigManagerAsteroids.syncConfig(false, false);
    }
}
