package micdoodle8.mods.galacticraft.planets.mars;

import micdoodle8.mods.galacticraft.planets.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.planets.mars.client.render.block.*;
import cpw.mods.fml.common.event.*;
import micdoodle8.mods.galacticraft.planets.mars.client.render.tile.TileEntityTreasureChestRenderer;
import net.minecraft.util.*;
import cpw.mods.fml.client.registry.*;
import net.minecraft.client.renderer.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.client.render.tile.*;
import micdoodle8.mods.galacticraft.core.client.render.tile.*;
import net.minecraft.client.renderer.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.client.model.*;
import micdoodle8.mods.galacticraft.core.client.render.entities.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.core.client.render.item.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.client.render.item.*;
import net.minecraftforge.client.model.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.client.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.planets.mars.client.fx.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import java.util.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.planets.mars.client.gui.*;
import cpw.mods.fml.common.gameevent.*;
import micdoodle8.mods.galacticraft.planets.mars.dimension.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.planets.mars.client.*;
import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.client.*;
import net.minecraft.client.multiplayer.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.eventhandler.*;

public class MarsModuleClient implements IPlanetsModuleClient
{
    private static int vineRenderID;
    private static int eggRenderID;
    private static int treasureRenderID;
    private static int machineRenderID;
    private static int renderIdHydrogenPipe;

    public void preInit(final FMLPreInitializationEvent event) {
    }

    public void init(final FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register((Object)new TickHandlerClient());
        MarsModuleClient.vineRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererCavernousVines(MarsModuleClient.vineRenderID));
        MarsModuleClient.eggRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererEgg(MarsModuleClient.eggRenderID));
        MarsModuleClient.treasureRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererTier2TreasureChest(MarsModuleClient.treasureRenderID));
        MarsModuleClient.machineRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererMachine(MarsModuleClient.machineRenderID));
        MarsModuleClient.renderIdHydrogenPipe = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererHydrogenPipe(MarsModuleClient.renderIdHydrogenPipe));
    }

    public void postInit(final FMLPostInitializationEvent event) {
        final IModelCustom chamberModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftmars", "models/chamber.obj"));
        final IModelCustom cargoRocketModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftmars", "models/cargoRocket.obj"));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityTreasureChestMars.class, (TileEntitySpecialRenderer)new TileEntityTreasureChestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityCryogenicChamber.class, (TileEntitySpecialRenderer)new TileEntityCryogenicChamberRenderer(chamberModel));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityTerraformer.class, (TileEntitySpecialRenderer)new TileEntityBubbleProviderRenderer(0.25f, 1.0f, 0.25f));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntitySludgeling.class, (Render)new RenderSludgeling());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntitySlimeling.class, (Render)new RenderSlimeling());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityCreeperBoss.class, (Render)new RenderCreeperBoss());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityTier2Rocket.class, (Render)new RenderTier1Rocket((ModelBase)new ModelTier2Rocket(), "galacticraftmars", "rocketT2"));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityProjectileTNT.class, (Render)new RenderProjectileTNT());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityLandingBalloons.class, (Render)new RenderLandingBalloons());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityLandingBalloons.class, (Render)new RenderLandingBalloons());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityCargoRocket.class, (Render)new RenderCargoRocket(cargoRocketModel));
        RenderingRegistry.addNewArmourRendererPrefix("desh");
        MinecraftForgeClient.registerItemRenderer(MarsItems.spaceship, (IItemRenderer)new ItemRendererTier2Rocket(cargoRocketModel));
        MinecraftForgeClient.registerItemRenderer(MarsItems.key, (IItemRenderer)new ItemRendererKey(new ResourceLocation("galacticraftmars", "textures/model/treasure.png")));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MarsBlocks.machine), (IItemRenderer)new ItemRendererMachine(chamberModel));
    }

    public Object getGuiElement(final Side side, final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        if (side == Side.CLIENT) {
            final TileEntity tile = world.getTileEntity(x, y, z);
            if (ID == 2) {
                if (tile instanceof TileEntityTerraformer) {
                    return new GuiTerraformer(player.inventory, (TileEntityTerraformer)tile);
                }
                if (tile instanceof TileEntityLaunchController) {
                    return new GuiLaunchController(player.inventory, (TileEntityLaunchController)tile);
                }
                if (tile instanceof TileEntityElectrolyzer) {
                    return new GuiWaterElectrolyzer(player.inventory, (TileEntityElectrolyzer)tile);
                }
                if (tile instanceof TileEntityGasLiquefier) {
                    return new GuiGasLiquefier(player.inventory, (TileEntityGasLiquefier)tile);
                }
                if (tile instanceof TileEntityMethaneSynthesizer) {
                    return new GuiMethaneSynthesizer(player.inventory, (TileEntityMethaneSynthesizer)tile);
                }
            }
        }
        return null;
    }

    public int getBlockRenderID(final Block block) {
        if (block == MarsBlocks.vine) {
            return MarsModuleClient.vineRenderID;
        }
        if (block == MarsBlocks.hydrogenPipe) {
            return MarsModuleClient.renderIdHydrogenPipe;
        }
        if (block == MarsBlocks.rock) {
            return MarsModuleClient.eggRenderID;
        }
        if (block == MarsBlocks.machine || block == MarsBlocks.machineT2) {
            return MarsModuleClient.machineRenderID;
        }
        if (block == MarsBlocks.tier2TreasureChest) {
            return MarsModuleClient.treasureRenderID;
        }
        return -1;
    }

    public void spawnParticle(final String particleID, final Vector3 position, final Vector3 motion, final Object... extraData) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
            final double dPosX = mc.renderViewEntity.posX - position.x;
            final double dPosY = mc.renderViewEntity.posY - position.y;
            final double dPosZ = mc.renderViewEntity.posZ - position.z;
            EntityFX particle = null;
            final double maxDistSqrd = 64.0;
            if (dPosX * dPosX + dPosY * dPosY + dPosZ * dPosZ < 4096.0) {
                if (particleID.equals("sludgeDrip")) {
                    particle = (EntityFX)new EntityDropParticleFX((World)mc.theWorld, position.x, position.y, position.z, Material.water);
                }
                else if (particleID.equals("bacterialDrip")) {
                    particle = (EntityFX)new EntityBacterialDripFX((World)mc.theWorld, position.x, position.y, position.z);
                }
            }
            if (particle != null) {
                particle.prevPosX = particle.posX;
                particle.prevPosY = particle.posY;
                particle.prevPosZ = particle.posZ;
                mc.effectRenderer.addEffect(particle);
            }
        }
    }

    public void getGuiIDs(final List<Integer> idList) {
        idList.add(2);
    }

    public static void openSlimelingGui(final EntitySlimeling slimeling, final int gui) {
        switch (gui) {
            case 0: {
                FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen)new GuiSlimeling(slimeling));
                break;
            }
            case 1: {
                FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen)new GuiSlimelingFeed(slimeling));
                break;
            }
        }
    }

    public static class TickHandlerClient
    {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onClientTick(final TickEvent.ClientTickEvent event) {
            final Minecraft minecraft = FMLClientHandler.instance().getClient();
            final WorldClient world = minecraft.theWorld;
            if (world != null && world.provider instanceof WorldProviderMars) {
                if (world.provider.getSkyRenderer() == null) {
                    world.provider.setSkyRenderer((IRenderHandler)new SkyProviderMars((IGalacticraftWorldProvider)world.provider));
                }
                if (world.provider.getCloudRenderer() == null) {
                    world.provider.setCloudRenderer((IRenderHandler)new CloudRenderer());
                }
            }
        }
    }
}
