package micdoodle8.mods.galacticraft.planets.asteroids;

import micdoodle8.mods.galacticraft.planets.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.block.*;
import micdoodle8.mods.galacticraft.planets.asteroids.event.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.*;
import cpw.mods.fml.client.registry.*;
import net.minecraft.client.renderer.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.craftguide.*;
import net.minecraftforge.client.model.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.fx.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;

public class AsteroidsModuleClient implements IPlanetsModuleClient
{
    private static int walkwayRenderID;
    private static int treasureChestID;
    
    @Override
    public void preInit(final FMLPreInitializationEvent event) {
    }
    
    @Override
    public void init(final FMLInitializationEvent event) {
        AsteroidsModuleClient.walkwayRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererWalkway(AsteroidsModuleClient.walkwayRenderID));
        AsteroidsModuleClient.treasureChestID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererTier3TreasureChest(AsteroidsModuleClient.treasureChestID));
        final AsteroidsEventHandlerClient clientEventHandler = new AsteroidsEventHandlerClient();
        FMLCommonHandler.instance().bus().register((Object)clientEventHandler);
        MinecraftForge.EVENT_BUS.register((Object)clientEventHandler);
        FluidTexturesGC.init();
    }
    
    @Override
    public void postInit(final FMLPostInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((Class)EntitySmallAsteroid.class, (Render)new RenderSmallAsteroid());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityGrapple.class, (Render)new RenderGrapple());
        final IModelCustom podModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/pod.obj"));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityEntryPod.class, (Render)new RenderEntryPod(podModel));
        final IModelCustom rocketModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/tier3rocket.obj"));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityTier3Rocket.class, (Render)new RenderTier3Rocket(rocketModel, "galacticraftasteroids", "tier3rocket"));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityAstroMiner.class, (Render)new RenderAstroMiner());
        final IModelCustom grappleModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/grapple.obj"));
        MinecraftForgeClient.registerItemRenderer(AsteroidsItems.grapple, (IItemRenderer)new ItemRendererGrappleHook(grappleModel));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AsteroidBlocks.beamReceiver), (IItemRenderer)new ItemRendererBeamReceiver());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AsteroidBlocks.beamReflector), (IItemRenderer)new ItemRendererBeamReflector());
        MinecraftForgeClient.registerItemRenderer(AsteroidsItems.tier3Rocket, (IItemRenderer)new ItemRendererTier3Rocket(rocketModel));
        MinecraftForgeClient.registerItemRenderer(AsteroidsItems.astroMiner, (IItemRenderer)new ItemRendererAstroMiner());
        MinecraftForgeClient.registerItemRenderer(AsteroidsItems.thermalPadding, (IItemRenderer)new ItemRendererThermalArmor());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AsteroidBlocks.shortRangeTelepad), (IItemRenderer)new ItemRendererShortRangeTelepad());
        MinecraftForgeClient.registerItemRenderer((Item)AsteroidsItems.heavyNoseCone, (IItemRenderer)new ItemRendererHeavyNoseCone());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AsteroidBlocks.blockWalkway), (IItemRenderer)new ItemRendererWalkway());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AsteroidBlocks.blockWalkwayOxygenPipe), (IItemRenderer)new ItemRendererWalkway());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AsteroidBlocks.blockWalkwayWire), (IItemRenderer)new ItemRendererWalkway());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityBeamReflector.class, (TileEntitySpecialRenderer)new TileEntityBeamReflectorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityBeamReceiver.class, (TileEntitySpecialRenderer)new TileEntityBeamReceiverRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityMinerBase.class, (TileEntitySpecialRenderer)new TileEntityMinerBaseRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityShortRangeTelepad.class, (TileEntitySpecialRenderer)new TileEntityShortRangeTelepadRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityTreasureChestAsteroids.class, (TileEntitySpecialRenderer)new TileEntityTreasureChestRenderer());
        if (Loader.isModLoaded("craftguide")) {
            CraftGuideIntegration.register();
        }
    }
    
    @Override
    public void getGuiIDs(final List<Integer> idList) {
        idList.add(3);
    }
    
    @Override
    public Object getGuiElement(final Side side, final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        switch (ID) {
            case 3: {
                if (tile instanceof TileEntityShortRangeTelepad) {
                    return new GuiShortRangeTelepad(player.inventory, (TileEntityShortRangeTelepad)tile);
                }
                if (tile instanceof TileEntityMinerBase) {
                    return new GuiAstroMinerDock(player.inventory, (TileEntityMinerBase)tile);
                }
                break;
            }
        }
        return null;
    }
    
    @Override
    public int getBlockRenderID(final Block block) {
        if (block == AsteroidBlocks.blockWalkway || block == AsteroidBlocks.blockWalkwayWire || block == AsteroidBlocks.blockWalkwayOxygenPipe) {
            return AsteroidsModuleClient.walkwayRenderID;
        }
        if (block == AsteroidBlocks.treasureChestTier3) {
            return AsteroidsModuleClient.treasureChestID;
        }
        return 0;
    }
    
    @Override
    public void spawnParticle(final String particleID, final Vector3 position, final Vector3 motion, final Object... extraData) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
            final double dX = mc.renderViewEntity.posX - position.x;
            final double dY = mc.renderViewEntity.posY - position.y;
            final double dZ = mc.renderViewEntity.posZ - position.z;
            EntityFX particle = null;
            final double viewDistance = 64.0;
            if (dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance && particleID.equals("portalBlue")) {
                particle = new EntityFXTeleport((World)mc.theWorld, position, motion, (TileEntityShortRangeTelepad)extraData[0], (boolean)extraData[1]);
            }
            if (particle != null) {
                particle.prevPosX = particle.posX;
                particle.prevPosY = particle.posY;
                particle.prevPosZ = particle.posZ;
                mc.effectRenderer.addEffect(particle);
            }
        }
    }
}
