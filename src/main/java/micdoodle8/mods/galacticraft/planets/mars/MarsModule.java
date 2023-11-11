package micdoodle8.mods.galacticraft.planets.mars;

import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.core.event.*;
import micdoodle8.mods.galacticraft.planets.mars.schematic.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.planets.mars.dimension.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.planets.mars.recipe.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.block.*;
import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.planets.*;
import cpw.mods.fml.common.registry.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.config.*;
import net.minecraft.block.material.*;

public class MarsModule implements IPlanetsModule
{
    public static final String ASSET_PREFIX = "galacticraftmars";
    public static final String TEXTURE_PREFIX = "galacticraftmars:";
    public static Fluid sludge;
    public static Fluid sludgeGC;
    public static Material sludgeMaterial;
    public static Planet planetMars;
    
    public void preInit(final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register((Object)new EventHandlerMars());
        if (!FluidRegistry.isFluidRegistered("bacterialsludge")) {
            FluidRegistry.registerFluid(MarsModule.sludgeGC = new Fluid("bacterialsludge").setDensity(800).setViscosity(1500));
        }
        else {
            GCLog.info("Galacticraft sludge is not default, issues may occur.");
        }
        MarsModule.sludge = FluidRegistry.getFluid("bacterialsludge");
        if (MarsModule.sludge.getBlock() == null) {
            MarsBlocks.blockSludge = new BlockSludge().setBlockName("sludge");
            ((BlockSludge)MarsBlocks.blockSludge).setQuantaPerBlock(3);
            GameRegistry.registerBlock(MarsBlocks.blockSludge, (Class)ItemBlockDesc.class, MarsBlocks.blockSludge.getUnlocalizedName());
            MarsModule.sludge.setBlock(MarsBlocks.blockSludge);
        }
        else {
            MarsBlocks.blockSludge = MarsModule.sludge.getBlock();
        }
        if (MarsBlocks.blockSludge != null) {
            MarsItems.registerItem(MarsItems.bucketSludge = new ItemBucketGC(MarsBlocks.blockSludge, "galacticraftmars:").setUnlocalizedName("bucketSludge"));
            FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("bacterialsludge", 1000), new ItemStack(MarsItems.bucketSludge), new ItemStack(Items.bucket));
        }
        EventHandlerGC.bucketList.put(MarsBlocks.blockSludge, MarsItems.bucketSludge);
        MarsBlocks.initBlocks();
        MarsBlocks.registerBlocks();
        MarsBlocks.setHarvestLevels();
        MarsBlocks.oreDictRegistration();
        MarsItems.initItems();
    }
    
    public void init(final FMLInitializationEvent event) {
        this.registerMicroBlocks();
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicTier2Rocket());
        SchematicRegistry.registerSchematicRecipe((ISchematicPage)new SchematicCargoRocket());
        GalacticraftCore.packetPipeline.addDiscriminator(6, (Class)PacketSimpleMars.class);
        this.registerTileEntities();
        this.registerCreatures();
        this.registerOtherEntities();
        (MarsModule.planetMars = (Planet)new Planet("mars").setParentSolarSystem(GalacticraftCore.solarSystemSol).setRingColorRGB(0.67f, 0.1f, 0.1f).setPhaseShift(0.1667f).setRelativeSize(0.5319f).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.25f, 1.25f)).setRelativeOrbitTime(1.881161f)).setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/mars.png"));
        MarsModule.planetMars.setDimensionInfo(ConfigManagerMars.dimensionIDMars, (Class)WorldProviderMars.class).setTierRequired(2);
        MarsModule.planetMars.atmosphereComponent(IAtmosphericGas.CO2).atmosphereComponent(IAtmosphericGas.ARGON).atmosphereComponent(IAtmosphericGas.NITROGEN);
        GalaxyRegistry.registerPlanet(MarsModule.planetMars);
        GalacticraftRegistry.registerTeleportType((Class)WorldProviderMars.class, (ITeleportType)new TeleportTypeMars());
        GalacticraftRegistry.registerRocketGui((Class)WorldProviderMars.class, new ResourceLocation("galacticraftmars", "textures/gui/marsRocketGui.png"));
        GalacticraftRegistry.addDungeonLoot(2, new ItemStack(MarsItems.schematic, 1, 0));
        GalacticraftRegistry.addDungeonLoot(2, new ItemStack(MarsItems.schematic, 1, 1));
        GalacticraftRegistry.addDungeonLoot(2, new ItemStack(MarsItems.schematic, 1, 2));
        CompressorRecipes.addShapelessRecipe(new ItemStack(MarsItems.marsItemBasic, 1, 3), new Object[] { new ItemStack(GCItems.heavyPlatingTier1), new ItemStack(GCItems.meteoricIronIngot, 1, 1) });
        CompressorRecipes.addShapelessRecipe(new ItemStack(MarsItems.marsItemBasic, 1, 5), new Object[] { ConfigManagerCore.recipesRequireGCAdvancedMetals ? new ItemStack(MarsItems.marsItemBasic, 1, 2) : "ingotDesh" });
    }
    
    public void postInit(final FMLPostInitializationEvent event) {
        RecipeManagerMars.loadRecipes();
    }
    
    public void serverStarting(final FMLServerStartingEvent event) {
    }
    
    public void serverInit(final FMLServerStartedEvent event) {
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
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(MarsBlocks.marsBlock, 4), "tile.mars.marscobblestone");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(MarsBlocks.marsBlock, 5), "tile.mars.marsgrass");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(MarsBlocks.marsBlock, 6), "tile.mars.marsdirt");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(MarsBlocks.marsBlock, 7), "tile.mars.marsdungeon");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(MarsBlocks.marsBlock, 8), "tile.mars.marsdeco");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, Integer.TYPE).newInstance(MarsBlocks.marsBlock, 9), "tile.mars.marsstone");
            }
        }
        catch (Exception ex) {}
    }
    
    public void registerTileEntities() {
        GameRegistry.registerTileEntity((Class)TileEntitySlimelingEgg.class, "Slimeling Egg");
        GameRegistry.registerTileEntity((Class)TileEntityTreasureChestMars.class, "Tier 2 Treasure Chest");
        GameRegistry.registerTileEntity((Class)TileEntityTerraformer.class, "Planet Terraformer");
        GameRegistry.registerTileEntity((Class)TileEntityCryogenicChamber.class, "Cryogenic Chamber");
        GameRegistry.registerTileEntity((Class)TileEntityGasLiquefier.class, "Gas Liquefier");
        GameRegistry.registerTileEntity((Class)TileEntityMethaneSynthesizer.class, "Methane Synthesizer");
        GameRegistry.registerTileEntity((Class)TileEntityElectrolyzer.class, "Water Electrolyzer");
        GameRegistry.registerTileEntity((Class)TileEntityDungeonSpawnerMars.class, "Mars Dungeon Spawner");
        GameRegistry.registerTileEntity((Class)TileEntityLaunchController.class, "Launch Controller");
        GameRegistry.registerTileEntity((Class)TileEntityHydrogenPipe.class, "Hydrogen Pipe");
    }
    
    public void registerCreatures() {
        this.registerGalacticraftCreature((Class<? extends Entity>)EntitySludgeling.class, "Sludgeling", ColorUtil.to32BitColor(255, 0, 50, 0), ColorUtil.to32BitColor(255, 0, 150, 0));
        this.registerGalacticraftCreature((Class<? extends Entity>)EntitySlimeling.class, "Slimeling", ColorUtil.to32BitColor(255, 0, 50, 0), ColorUtil.to32BitColor(255, 0, 150, 0));
        this.registerGalacticraftCreature((Class<? extends Entity>)EntityCreeperBoss.class, "CreeperBoss", ColorUtil.to32BitColor(255, 0, 50, 0), ColorUtil.to32BitColor(255, 0, 150, 0));
    }
    
    public void registerOtherEntities() {
        registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityTier2Rocket.class, "SpaceshipT2", 150, 1, false);
        registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityProjectileTNT.class, "ProjectileTNT", 150, 1, true);
        registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityLandingBalloons.class, "LandingBalloons", 150, 5, true);
        registerGalacticraftNonMobEntity((Class<? extends Entity>)EntityCargoRocket.class, "CargoRocket", 150, 1, false);
    }
    
    public void registerGalacticraftCreature(final Class<? extends Entity> var0, final String var1, final int back, final int fore) {
        registerGalacticraftNonMobEntity(var0, var1, 80, 3, true);
        final int nextEggID = GCCoreUtil.getNextValidEggID();
        if (nextEggID < 65536) {
            EntityList.IDtoClassMapping.put(nextEggID, var0);
            VersionUtil.putClassToIDMapping(var0, nextEggID);
            EntityList.entityEggs.put(nextEggID, new EntityList.EntityEggInfo(nextEggID, back, fore));
        }
    }
    
    public static void registerGalacticraftNonMobEntity(final Class<? extends Entity> var0, final String var1, final int trackingDistance, final int updateFreq, final boolean sendVel) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            LanguageRegistry.instance().addStringLocalization("entity.GalacticraftMars." + var1 + ".name", GCCoreUtil.translate("entity." + var1 + ".name"));
        }
        EntityRegistry.registerModEntity((Class)var0, var1, GCCoreUtil.nextInternalID(), (Object)GalacticraftPlanets.instance, trackingDistance, updateFreq, sendVel);
    }
    
    public void getGuiIDs(final List<Integer> idList) {
        idList.add(2);
    }
    
    public Object getGuiElement(final Side side, final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        if (side == Side.SERVER) {
            final TileEntity tile = world.getTileEntity(x, y, z);
            if (ID == 2) {
                if (tile instanceof TileEntityTerraformer) {
                    return new ContainerTerraformer(player.inventory, (TileEntityTerraformer)tile);
                }
                if (tile instanceof TileEntityLaunchController) {
                    return new ContainerLaunchController(player.inventory, (TileEntityLaunchController)tile);
                }
                if (tile instanceof TileEntityElectrolyzer) {
                    return new ContainerElectrolyzer(player.inventory, (TileEntityElectrolyzer)tile);
                }
                if (tile instanceof TileEntityGasLiquefier) {
                    return new ContainerGasLiquefier(player.inventory, (TileEntityGasLiquefier)tile);
                }
                if (tile instanceof TileEntityMethaneSynthesizer) {
                    return new ContainerMethaneSynthesizer(player.inventory, (TileEntityMethaneSynthesizer)tile);
                }
            }
        }
        return null;
    }
    
    public Configuration getConfiguration() {
        return ConfigManagerMars.config;
    }
    
    public void syncConfig() {
        ConfigManagerMars.syncConfig(false, false);
    }
    
    static {
        MarsModule.sludgeMaterial = (Material)new MaterialLiquid(MapColor.foliageColor);
    }
}
