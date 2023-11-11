package micdoodle8.mods.galacticraft.core.proxy;

import cpw.mods.fml.common.eventhandler.Event;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import net.minecraft.client.audio.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import java.nio.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.core.client.*;
import api.player.client.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.common.util.*;
import cpw.mods.fml.common.event.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.core.recipe.craftguide.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.client.sounds.*;
import java.lang.reflect.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import api.player.model.*;
import api.player.render.*;
import micdoodle8.mods.galacticraft.core.client.render.entities.*;
import net.minecraft.item.*;
import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.client.render.item.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraft.client.renderer.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.client.render.tile.*;
import cpw.mods.fml.client.registry.*;
import micdoodle8.mods.galacticraft.core.client.render.block.*;
import cpw.mods.fml.common.*;
import java.net.*;
import tconstruct.client.tabs.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.client.fx.*;
import org.lwjgl.opengl.*;
import net.minecraftforge.fluids.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.client.event.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import micdoodle8.mods.galacticraft.core.client.render.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.network.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.api.event.client.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import java.util.*;
import java.util.List;

import net.minecraft.util.*;
import org.lwjgl.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import com.google.common.collect.*;

public class ClientProxyCore extends CommonProxyCore
{
    private static int renderIdTreasureChest;
    private static int renderIdParachest;
    private static int renderIdTorchUnlit;
    private static int renderIdBreathableAir;
    private static int renderIdOxygenPipe;
    private static int renderIdMeteor;
    private static int renderIdCraftingTable;
    private static int renderIdLandingPad;
    private static int renderIdMachine;
    public static FootprintRenderer footprintRenderer;
    public static List<String> flagRequestsSent;
    private static int renderIndexHeavyArmor;
    private static int renderIndexSensorGlasses;
    public static Set<BlockVec3> valueableBlocks;
    public static HashSet<BlockMetaList> detectableBlocks;
    public static List<BlockVec3> leakTrace;
    public static Map<String, PlayerGearData> playerItemData;
    public static double playerPosX;
    public static double playerPosY;
    public static double playerPosZ;
    public static float playerRotationYaw;
    public static float playerRotationPitch;
    public static boolean lastSpacebarDown;
    public static boolean sneakRenderOverride;
    public static HashMap<Integer, Integer> clientSpaceStationID;
    public static MusicTicker.MusicType MUSIC_TYPE_MARS;
    public static EnumRarity galacticraftItem;
    public static Map<String, String> capeMap;
    public static InventoryExtended dummyInventory;
    private static final ResourceLocation underOilTexture;
    private static float[] numbers;
    private static FloatBuffer scaleup;
    public static float globalRadius;
    public static double offsetY;
    public static float terrainHeight;
    private static boolean smallMoonActive;
    private static Map<String, ResourceLocation> capesMap;
    public static IPlayerClient playerClientHandler;
    public static Minecraft mc;
    public static List<String> gearDataRequests;
    public static DynamicTextureProper overworldTextureClient;
    public static DynamicTextureProper overworldTextureWide;
    public static DynamicTextureProper overworldTextureLarge;
    public static boolean overworldTextureRequestSent;
    public static boolean overworldTexturesValid;
    public static float PLAYER_Y_OFFSET;
    private static final ResourceLocation saturnRingTexture;
    private static final ResourceLocation uranusRingTexture;

    public static void reset() {
        ClientProxyCore.playerItemData.clear();
        ClientProxyCore.overworldTextureRequestSent = false;
        ClientProxyCore.flagRequestsSent.clear();
    }

    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        ClientProxyCore.scaleup.put(ClientProxyCore.numbers, 0, 16);
        ClientProxyCore.renderIndexSensorGlasses = RenderingRegistry.addNewArmourRendererPrefix("sensor");
        ClientProxyCore.renderIndexHeavyArmor = RenderingRegistry.addNewArmourRendererPrefix("titanium");
        if (Loader.isModLoaded("PlayerAPI")) {
            ClientPlayerAPI.register("GalacticraftCore", (Class)GCPlayerBaseSP.class);
        }
    }

    @Override
    public void init(final FMLInitializationEvent event) {
        final Class[][] commonTypes = { { MusicTicker.MusicType.class, ResourceLocation.class, Integer.TYPE, Integer.TYPE } };
        ClientProxyCore.MUSIC_TYPE_MARS = (MusicTicker.MusicType)EnumHelper.addEnum(commonTypes, (Class)MusicTicker.MusicType.class, "MARS_JC", new Object[] { new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "galacticraft.musicSpace"), 12000, 24000 });
        registerHandlers();
        registerTileEntityRenderers();
        registerBlockHandlers();
        setupCapes();
    }

    @Override
    public void postInit(final FMLPostInitializationEvent event) {
        registerInventoryTabs();
        registerEntityRenderers();
        registerItemRenderers();
        MinecraftForge.EVENT_BUS.register((Object)new TabRegistry());
        if (Loader.isModLoaded("craftguide")) {
            CraftGuideIntegration.register();
        }
        try {
            final Field ftc = Minecraft.getMinecraft().getClass().getDeclaredField(VersionUtil.getNameDynamic("mcMusicTicker"));
            ftc.setAccessible(true);
            ftc.set(Minecraft.getMinecraft(), new MusicTickerGC(Minecraft.getMinecraft()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityTier1Rocket.class, (Render)new RenderTier1Rocket((ModelBase)new ModelRocketTier1(), GalacticraftCore.ASSET_PREFIX, "rocketT1"));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityEvolvedSpider.class, (Render)new RenderEvolvedSpider());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityEvolvedZombie.class, (Render)new RenderEvolvedZombie());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityEvolvedCreeper.class, (Render)new RenderEvolvedCreeper());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityEvolvedSkeleton.class, (Render)new RenderEvolvedSkeleton());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntitySkeletonBoss.class, (Render)new RenderEvolvedSkeletonBoss());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityMeteor.class, (Render)new RenderMeteor());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityBuggy.class, (Render)new RenderBuggy());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityMeteorChunk.class, (Render)new RenderMeteorChunk());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityFlag.class, (Render)new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityParachest.class, (Render)new RenderParaChest());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityAlienVillager.class, (Render)new RenderAlienVillager());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityLander.class, (Render)new RenderLander());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityCelestialFake.class, (Render)new RenderEntityFake());
        if (Loader.isModLoaded("RenderPlayerAPI")) {
            ModelPlayerAPI.register("GalacticraftCore", (Class)ModelPlayerBaseGC.class);
            RenderPlayerAPI.register("GalacticraftCore", (Class)RenderPlayerBaseGC.class);
        }
        else {
            RenderingRegistry.registerEntityRenderingHandler((Class)EntityPlayerSP.class, (Render)new RenderPlayerGC());
            RenderingRegistry.registerEntityRenderingHandler((Class)EntityOtherPlayerMP.class, (Render)new RenderPlayerGC());
        }
    }

    public static void registerItemRenderers() {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GCBlocks.unlitTorch), (IItemRenderer)new ItemRendererUnlitTorch());
        MinecraftForgeClient.registerItemRenderer(GCItems.rocketTier1, (IItemRenderer)new ItemRendererTier1Rocket((EntitySpaceshipBase)new EntityTier1Rocket((World)ClientProxyCore.mc.theWorld), (ModelBase)new ModelRocketTier1(), new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/rocketT1.png")));
        MinecraftForgeClient.registerItemRenderer(GCItems.buggy, (IItemRenderer)new ItemRendererBuggy());
        MinecraftForgeClient.registerItemRenderer(GCItems.flag, (IItemRenderer)new ItemRendererFlag());
        MinecraftForgeClient.registerItemRenderer(GCItems.key, (IItemRenderer)new ItemRendererKey(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/treasure.png")));
        MinecraftForgeClient.registerItemRenderer(GCItems.meteorChunk, (IItemRenderer)new ItemRendererMeteorChunk());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GCBlocks.spinThruster), (IItemRenderer)new ItemRendererThruster());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GCBlocks.brightLamp), (IItemRenderer)new ItemRendererArclamp());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GCBlocks.screen), (IItemRenderer)new ItemRendererScreen());
    }

    public static void registerHandlers() {
        final TickHandlerClient tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register((Object)tickHandlerClient);
        MinecraftForge.EVENT_BUS.register((Object)tickHandlerClient);
        FMLCommonHandler.instance().bus().register((Object)new KeyHandlerClient());
        ClientRegistry.registerKeyBinding(KeyHandlerClient.galaxyMap);
        ClientRegistry.registerKeyBinding(KeyHandlerClient.openFuelGui);
        ClientRegistry.registerKeyBinding(KeyHandlerClient.toggleAdvGoggles);
        MinecraftForge.EVENT_BUS.register((Object)GalacticraftCore.proxy);
    }

    public static void registerTileEntityRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityAluminumWire.class, (TileEntitySpecialRenderer)new TileEntityAluminumWireRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityTreasureChest.class, (TileEntitySpecialRenderer)new TileEntityTreasureChestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityParaChest.class, (TileEntitySpecialRenderer)new TileEntityParachestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityNasaWorkbench.class, (TileEntitySpecialRenderer)new TileEntityNasaWorkbenchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntitySolar.class, (TileEntitySpecialRenderer)new TileEntitySolarPanelRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityOxygenDistributor.class, (TileEntitySpecialRenderer)new TileEntityBubbleProviderRenderer(0.25f, 0.25f, 1.0f));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityDish.class, (TileEntitySpecialRenderer)new TileEntityDishRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityThruster.class, (TileEntitySpecialRenderer)new TileEntityThrusterRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityArclamp.class, (TileEntitySpecialRenderer)new TileEntityArclampRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityScreen.class, (TileEntitySpecialRenderer)new TileEntityScreenRenderer());
    }

    public static void registerBlockHandlers() {
        ClientProxyCore.renderIdTreasureChest = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdTorchUnlit = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdBreathableAir = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdOxygenPipe = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdMeteor = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdCraftingTable = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdLandingPad = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdMachine = RenderingRegistry.getNextAvailableRenderId();
        ClientProxyCore.renderIdParachest = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererTreasureChest(ClientProxyCore.renderIdTreasureChest));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererParachest(ClientProxyCore.renderIdParachest));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererUnlitTorch(ClientProxyCore.renderIdTorchUnlit));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererBreathableAir(ClientProxyCore.renderIdBreathableAir));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererOxygenPipe(ClientProxyCore.renderIdOxygenPipe));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererMeteor(ClientProxyCore.renderIdMeteor));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererNasaWorkbench(ClientProxyCore.renderIdCraftingTable));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererLandingPad(ClientProxyCore.renderIdLandingPad));
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)new BlockRendererMachine(ClientProxyCore.renderIdMachine));
    }

    public static void setupCapes() {
        try {
            updateCapeList();
        }
        catch (Exception e) {
            FMLLog.severe("Error while setting up Galacticraft donor capes", new Object[0]);
            e.printStackTrace();
        }
    }

    private static void updateCapeList() {
        final int timeout = 10000;
        URL capeListUrl = null;
        try {
            capeListUrl = new URL("https://raw.github.com/micdoodle8/Galacticraft/master/capes.txt");
        }
        catch (MalformedURLException e) {
            FMLLog.severe("Error getting capes list URL", new Object[0]);
            e.printStackTrace();
            return;
        }
        URLConnection connection = null;
        try {
            connection = capeListUrl.openConnection();
        }
        catch (IOException e2) {
            e2.printStackTrace();
            return;
        }
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream stream = null;
        try {
            stream = connection.getInputStream();
        }
        catch (IOException e3) {
            e3.printStackTrace();
            return;
        }
        final InputStreamReader streamReader = new InputStreamReader(stream);
        final BufferedReader reader = new BufferedReader(streamReader);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    final int splitLocation = line.indexOf(":");
                    final String username = line.substring(0, splitLocation);
                    final String capeUrl = "https://raw.github.com/micdoodle8/Galacticraft/master/capes/" + line.substring(splitLocation + 1) + ".png";
                    ClientProxyCore.capeMap.put(username, capeUrl);
                }
            }
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            reader.close();
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            streamReader.close();
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            stream.close();
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    public static void registerInventoryTabs() {
        if (!Loader.isModLoaded("TConstruct") && TabRegistry.getTabList().size() < 1) {
            TabRegistry.registerTab((AbstractTab)new InventoryTabVanilla());
        }
        TabRegistry.registerTab((AbstractTab)new InventoryTabGalacticraft());
    }

    public static void renderPlanets(final float par3) {
    }

    @Override
    public int getBlockRender(final Block blockID) {
        if (blockID == GCBlocks.breatheableAir || blockID == GCBlocks.brightBreatheableAir) {
            return ClientProxyCore.renderIdBreathableAir;
        }
        if (blockID == GCBlocks.oxygenPipe) {
            return ClientProxyCore.renderIdOxygenPipe;
        }
        if (blockID == GCBlocks.fallenMeteor) {
            return ClientProxyCore.renderIdMeteor;
        }
        if (blockID == GCBlocks.nasaWorkbench) {
            return ClientProxyCore.renderIdCraftingTable;
        }
        if (blockID == GCBlocks.landingPadFull) {
            return ClientProxyCore.renderIdLandingPad;
        }
        if (blockID instanceof BlockUnlitTorch || blockID == GCBlocks.glowstoneTorch) {
            return ClientProxyCore.renderIdTorchUnlit;
        }
        if (blockID == GCBlocks.fuelLoader || blockID == GCBlocks.cargoLoader || blockID == GCBlocks.machineBase || blockID == GCBlocks.machineBase2 || blockID == GCBlocks.machineTiered || blockID == GCBlocks.oxygenCollector || blockID == GCBlocks.oxygenCompressor || blockID == GCBlocks.oxygenDetector || blockID == GCBlocks.oxygenDistributor || blockID == GCBlocks.oxygenSealer || blockID == GCBlocks.refinery || blockID == GCBlocks.telemetry) {
            return ClientProxyCore.renderIdMachine;
        }
        if (blockID == GCBlocks.treasureChestTier1) {
            return ClientProxyCore.renderIdTreasureChest;
        }
        if (blockID == GCBlocks.parachest) {
            return ClientProxyCore.renderIdParachest;
        }
        return -1;
    }

    @Override
    public World getClientWorld() {
        return (World)ClientProxyCore.mc.theWorld;
    }

    @Override
    public int getTitaniumArmorRenderIndex() {
        return ClientProxyCore.renderIndexHeavyArmor;
    }

    @Override
    public int getSensorArmorRenderIndex() {
        return ClientProxyCore.renderIndexSensorGlasses;
    }

    @Override
    public void spawnParticle(final String particleID, final Vector3 position, final Vector3 motion, final Object[] otherInfo) {
        EffectHandler.spawnParticle(particleID, position, motion, otherInfo);
    }

    public static void renderLiquidOverlays(final float partialTicks) {
        if (isInsideOfFluid((Entity)ClientProxyCore.mc.thePlayer, GalacticraftCore.fluidOil)) {
            ClientProxyCore.mc.getTextureManager().bindTexture(ClientProxyCore.underOilTexture);
            final Tessellator tessellator = Tessellator.instance;
            final float f1 = ClientProxyCore.mc.thePlayer.getBrightness(partialTicks) / 3.0f;
            GL11.glColor4f(f1, f1, f1, 1.0f);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glPushMatrix();
            final float f2 = 4.0f;
            final float f3 = -1.0f;
            final float f4 = 1.0f;
            final float f5 = -1.0f;
            final float f6 = 1.0f;
            final float f7 = -0.5f;
            final float f8 = -ClientProxyCore.mc.thePlayer.rotationYaw / 64.0f;
            final float f9 = ClientProxyCore.mc.thePlayer.rotationPitch / 64.0f;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)f3, (double)f5, (double)f7, (double)(f2 + f8), (double)(f2 + f9));
            tessellator.addVertexWithUV((double)f4, (double)f5, (double)f7, (double)(0.0f + f8), (double)(f2 + f9));
            tessellator.addVertexWithUV((double)f4, (double)f6, (double)f7, (double)(0.0f + f8), (double)(0.0f + f9));
            tessellator.addVertexWithUV((double)f3, (double)f6, (double)f7, (double)(f2 + f8), (double)(0.0f + f9));
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(3042);
        }
    }

    public static boolean isInsideOfFluid(final Entity entity, final Fluid fluid) {
        final double d0 = entity.posY + entity.getEyeHeight();
        final int i = MathHelper.floor_double(entity.posX);
        final int j = MathHelper.floor_float((float)MathHelper.floor_double(d0));
        final int k = MathHelper.floor_double(entity.posZ);
        final Block block = entity.worldObj.getBlock(i, j, k);
        if (block == null || !(block instanceof IFluidBlock) || ((IFluidBlock)block).getFluid() == null || !((IFluidBlock)block).getFluid().getName().equals(fluid.getName())) {
            return false;
        }
        double filled = ((IFluidBlock)block).getFilledPercentage(entity.worldObj, i, j, k);
        if (filled < 0.0) {
            filled *= -1.0;
            return d0 > j + (1.0 - filled);
        }
        return d0 < j + filled;
    }

    public static void renderFootprints(final float partialTicks) {
        ClientProxyCore.footprintRenderer.renderFootprints((EntityPlayer)ClientProxyCore.mc.thePlayer, partialTicks);
        MinecraftForge.EVENT_BUS.post((Event)new EventSpecialRender(partialTicks));
    }

    @Override
    public World getWorldForID(final int dimensionID) {
        final World world = (World)ClientProxyCore.mc.theWorld;
        if (world != null && world.provider.dimensionId == dimensionID) {
            return world;
        }
        return null;
    }

    @SubscribeEvent
    public void onRenderPlayerPre(final RenderPlayerEvent.Pre event) {
        GL11.glPushMatrix();
        final EntityPlayer player = event.entityPlayer;
        if (player.ridingEntity instanceof ICameraZoomEntity && player == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            final Entity entity = player.ridingEntity;
            float rotateOffset = ((ICameraZoomEntity)entity).getRotateOffset();
            if (rotateOffset > -10.0f) {
                rotateOffset += ClientProxyCore.PLAYER_Y_OFFSET;
                GL11.glTranslatef(0.0f, -rotateOffset, 0.0f);
                final float anglePitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.partialRenderTick;
                final float angleYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialRenderTick;
                GL11.glRotatef(-angleYaw, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(anglePitch, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(0.0f, rotateOffset, 0.0f);
            }
        }
        if (player instanceof EntityPlayerSP) {
            ClientProxyCore.sneakRenderOverride = true;
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(final RenderPlayerEvent.Post event) {
        GL11.glPopMatrix();
        final EntityPlayer player = event.entityPlayer;
        if (player instanceof EntityPlayerSP) {
            ClientProxyCore.sneakRenderOverride = false;
        }
    }

    @SubscribeEvent
    public void onRenderPlayerEquipped(final RenderPlayerEvent.Specials.Pre event) {
        final Entity ridden = event.entityPlayer.ridingEntity;
        if (ridden instanceof EntityAutoRocket || ridden instanceof EntityLanderBase) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPostRender(final RenderPlayerEvent.Specials.Post event) {
        final AbstractClientPlayer player = (AbstractClientPlayer)event.entityPlayer;
        final boolean flag = ClientProxyCore.capeMap.containsKey(event.entityPlayer.getCommandSenderName());
        if (flag && !player.isInvisible() && !player.getHideCape()) {
            final String url = ClientProxyCore.capeMap.get(player.getCommandSenderName());
            ResourceLocation capeLoc = ClientProxyCore.capesMap.get(url);
            if (!ClientProxyCore.capesMap.containsKey(url)) {
                try {
                    final String dirName = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
                    File directory = new File(dirName, "assets");
                    boolean success = true;
                    if (!directory.exists()) {
                        success = directory.mkdir();
                    }
                    if (success) {
                        directory = new File(directory, "gcCapes");
                        if (!directory.exists()) {
                            success = directory.mkdir();
                        }
                        if (success) {
                            final String hash = String.valueOf(player.getCommandSenderName().hashCode());
                            final File file1 = new File(directory, hash.substring(0, 2));
                            final File file2 = new File(file1, hash);
                            final ResourceLocation resourcelocation = new ResourceLocation("gcCapes/" + hash);
                            final ThreadDownloadImageDataGC threaddownloadimagedata = new ThreadDownloadImageDataGC(file2, url, (ResourceLocation)null, (IImageBuffer)new IImageBuffer() {
                                public BufferedImage parseUserSkin(BufferedImage p_78432_1_) {
                                    if (p_78432_1_ == null) {
                                        return null;
                                    }
                                    final BufferedImage bufferedimage1 = new BufferedImage(512, 256, 2);
                                    final Graphics graphics = bufferedimage1.getGraphics();
                                    graphics.drawImage(p_78432_1_, 0, 0, null);
                                    graphics.dispose();
                                    p_78432_1_ = bufferedimage1;
                                    return p_78432_1_;
                                }

                                public void func_152634_a() {
                                }
                            });
                            if (ClientProxyCore.mc.getTextureManager().loadTexture(resourcelocation, (ITextureObject)threaddownloadimagedata)) {
                                capeLoc = resourcelocation;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                ClientProxyCore.capesMap.put(url, capeLoc);
            }
            if (capeLoc != null) {
                ClientProxyCore.mc.getTextureManager().bindTexture(capeLoc);
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0f, 0.0f, 0.125f);
                final double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * event.partialRenderTick - (player.prevPosX + (player.posX - player.prevPosX) * event.partialRenderTick);
                final double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * event.partialRenderTick - (player.prevPosY + (player.posY - player.prevPosY) * event.partialRenderTick);
                final double d5 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * event.partialRenderTick - (player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialRenderTick);
                final float f4 = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick) / 57.29578f;
                final double d6 = MathHelper.sin(f4);
                final double d7 = -MathHelper.cos(f4);
                float f5 = (float)d4 * 10.0f;
                if (f5 < -6.0f) {
                    f5 = -6.0f;
                }
                if (f5 > 32.0f) {
                    f5 = 32.0f;
                }
                float f6 = (float)(d3 * d6 + d5 * d7) * 100.0f;
                final float f7 = (float)(d3 * d7 - d5 * d6) * 100.0f;
                if (f6 < 0.0f) {
                    f6 = 0.0f;
                }
                final float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * event.partialRenderTick;
                f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * event.partialRenderTick) * 6.0f) * 32.0f * f8;
                if (player.isSneaking()) {
                    f5 += 25.0f;
                }
                GL11.glRotatef(6.0f + f6 / 2.0f + f5, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(f7 / 2.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-f7 / 2.0f, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                event.renderer.modelBipedMain.renderCloak(0.0625f);
                GL11.glPopMatrix();
            }
        }
    }

    public static void adjustRenderPos(final Entity entity, final double offsetX, final double offsetY, final double offsetZ) {
        GL11.glPushMatrix();
        if (ClientProxyCore.smallMoonActive && (offsetX != 0.0 || offsetY != 0.0 || offsetZ != 0.0)) {
            final EntityPlayerSP player = (EntityPlayerSP)ClientProxyCore.mc.thePlayer;
            if (player.posY > ClientProxyCore.terrainHeight + 8.0f && player.ridingEntity != entity && player != entity) {
                final double globalArc = ClientProxyCore.globalRadius / 57.2957795;
                final int pX = MathHelper.floor_double(player.posX / 16.0) << 4;
                final int pZ = MathHelper.floor_double(player.posZ / 16.0) << 4;
                final int eX = MathHelper.floor_double(entity.posX / 16.0) << 4;
                final int eY = MathHelper.floor_double(entity.posY / 16.0) << 4;
                final int eZ = MathHelper.floor_double(entity.posZ / 16.0) << 4;
                float dX = (float)(eX - pX);
                float dZ = (float)(eZ - pZ);
                final float floatPX = (float)player.posX;
                final float floatPZ = (float)player.posZ;
                if (dX > 0.0f) {
                    dX -= 16.0f;
                    if (dX > 0.0f) {
                        dX -= floatPX - pX;
                    }
                }
                else if (dX < 0.0f) {
                    dX += 16.0f;
                    if (dX < 0.0f) {
                        dX += 16.0f - floatPX + pX;
                    }
                }
                if (dZ > 0.0f) {
                    dZ -= 16.0f;
                    if (dZ > 0.0f) {
                        dZ -= floatPZ - pZ;
                    }
                }
                else if (dZ < 0.0f) {
                    dZ += 16.0f;
                    if (dZ < 0.0f) {
                        dZ += 16.0f - floatPZ + pZ;
                    }
                }
                float theta = (float)MathHelper.wrapAngleTo180_double(dX / globalArc);
                float phi = (float)MathHelper.wrapAngleTo180_double(dZ / globalArc);
                if (theta < 0.0f) {
                    theta += 360.0f;
                }
                if (phi < 0.0f) {
                    phi += 360.0f;
                }
                final float ytranslate = ClientProxyCore.globalRadius + (float)(player.posY - entity.posY) + eY - ClientProxyCore.terrainHeight;
                GL11.glTranslatef(-dX + eX - floatPX + 8.0f, -ytranslate, -dZ + eZ - floatPZ + 8.0f);
                if (theta > 0.0f) {
                    GL11.glRotatef(theta, 0.0f, 0.0f, -1.0f);
                }
                if (phi > 0.0f) {
                    GL11.glRotatef(phi, 1.0f, 0.0f, 0.0f);
                }
                GL11.glTranslatef(floatPX - eX - 8.0f, ytranslate, floatPZ - eZ - 8.0f);
            }
        }
    }

    public static void adjustTileRenderPos(final TileEntity tile, final double offsetX, final double offsetY, final double offsetZ) {
        GL11.glPushMatrix();
        if (ClientProxyCore.smallMoonActive && (offsetX != 0.0 || offsetY != 0.0 || offsetZ != 0.0)) {
            final EntityPlayerSP player = (EntityPlayerSP)ClientProxyCore.mc.thePlayer;
            final WorldProvider provider = ClientProxyCore.mc.theWorld.provider;
            if (provider instanceof WorldProviderMoon && player.posY > ClientProxyCore.terrainHeight + 8.0f) {
                final double globalArc = ClientProxyCore.globalRadius / 57.2957795;
                final int pX = MathHelper.floor_double(player.posX / 16.0) << 4;
                final int pZ = MathHelper.floor_double(player.posZ / 16.0) << 4;
                final int eX = tile.xCoord / 16 << 4;
                final int eY = tile.yCoord / 16 << 4;
                final int eZ = tile.zCoord / 16 << 4;
                float dX = (float)(eX - pX);
                float dZ = (float)(eZ - pZ);
                final float floatPX = (float)player.posX;
                final float floatPZ = (float)player.posZ;
                if (dX > 0.0f) {
                    dX -= 16.0f;
                    if (dX > 0.0f) {
                        dX -= floatPX - pX;
                    }
                }
                else if (dX < 0.0f) {
                    dX += 16.0f;
                    if (dX < 0.0f) {
                        dX += 16.0f - floatPX + pX;
                    }
                }
                if (dZ > 0.0f) {
                    dZ -= 16.0f;
                    if (dZ > 0.0f) {
                        dZ -= floatPZ - pZ;
                    }
                }
                else if (dZ < 0.0f) {
                    dZ += 16.0f;
                    if (dZ < 0.0f) {
                        dZ += 16.0f - floatPZ + pZ;
                    }
                }
                float theta = (float)MathHelper.wrapAngleTo180_double(dX / globalArc);
                float phi = (float)MathHelper.wrapAngleTo180_double(dZ / globalArc);
                if (theta < 0.0f) {
                    theta += 360.0f;
                }
                if (phi < 0.0f) {
                    phi += 360.0f;
                }
                final float ytranslate = ClientProxyCore.globalRadius + (float)player.posY - tile.yCoord + eY - ClientProxyCore.terrainHeight;
                GL11.glTranslatef(-dX - floatPX + eX + 8.0f, -ytranslate, -dZ - floatPZ + eZ + 8.0f);
                if (theta > 0.0f) {
                    GL11.glRotatef(theta, 0.0f, 0.0f, -1.0f);
                }
                if (phi > 0.0f) {
                    GL11.glRotatef(phi, 1.0f, 0.0f, 0.0f);
                }
                GL11.glTranslatef(floatPX - eX - 8.0f, ytranslate, floatPZ - eZ - 8.0f);
            }
        }
    }

    public static void orientCamera(final float partialTicks) {
        final EntityClientPlayerMP player = ClientProxyCore.mc.thePlayer;
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)player);
        final EntityLivingBase entityLivingBase = ClientProxyCore.mc.renderViewEntity;
        if (player.ridingEntity instanceof ICameraZoomEntity && ClientProxyCore.mc.gameSettings.thirdPersonView == 0) {
            final Entity entity = player.ridingEntity;
            float offset = ((ICameraZoomEntity)entity).getRotateOffset();
            if (offset > -10.0f) {
                offset += ClientProxyCore.PLAYER_Y_OFFSET;
                GL11.glTranslatef(0.0f, -offset, 0.0f);
                final float anglePitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                final float angleYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
                GL11.glRotatef(-anglePitch, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(angleYaw, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(0.0f, offset, 0.0f);
            }
        }
        if (entityLivingBase.worldObj.provider instanceof WorldProviderSpaceStation && !entityLivingBase.isPlayerSleeping()) {
            final float f1 = entityLivingBase.yOffset - 1.62f;
            final float pitch = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * partialTicks;
            final float yaw = entityLivingBase.prevRotationYaw + (entityLivingBase.rotationYaw - entityLivingBase.prevRotationYaw) * partialTicks + 180.0f;
            final float eyeHeightChange = entityLivingBase.yOffset - entityLivingBase.width / 2.0f;
            GL11.glTranslatef(0.0f, -f1, 0.0f);
            GL11.glRotatef(-yaw, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-pitch, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(0.0f, 0.0f, 0.1f);
            GL11.glRotatef(180.0f * stats.gdir.getThetaX(), 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(180.0f * stats.gdir.getThetaZ(), 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(pitch * stats.gdir.getPitchGravityX(), 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(pitch * stats.gdir.getPitchGravityY(), 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(yaw * stats.gdir.getYawGravityX(), 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(yaw * stats.gdir.getYawGravityY(), 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(yaw * stats.gdir.getYawGravityZ(), 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(eyeHeightChange * stats.gdir.getEyeVecX(), eyeHeightChange * stats.gdir.getEyeVecY(), eyeHeightChange * stats.gdir.getEyeVecZ());
            if (stats.gravityTurnRate < 1.0f) {
                GL11.glRotatef(90.0f * (stats.gravityTurnRatePrev + (stats.gravityTurnRate - stats.gravityTurnRatePrev) * partialTicks), stats.gravityTurnVecX, stats.gravityTurnVecY, stats.gravityTurnVecZ);
            }
        }
    }

    public static void adjustRenderCamera() {
        GL11.glPushMatrix();
    }

    public static void setPositionList(final WorldRenderer rend, final int glRenderList) {
        GL11.glNewList(glRenderList + 3, 4864);
        final EntityLivingBase entitylivingbase = ClientProxyCore.mc.renderViewEntity;
        if (entitylivingbase != null) {
            if (rend.worldObj.provider instanceof WorldProviderMoon) {
                ClientProxyCore.globalRadius = 300.0f;
                ClientProxyCore.terrainHeight = 64.0f;
                if (entitylivingbase.posY > ClientProxyCore.terrainHeight + 8.0f) {
                    ClientProxyCore.smallMoonActive = true;
                    final double globalArc = ClientProxyCore.globalRadius / 57.2957795;
                    final float globeRadius = ClientProxyCore.globalRadius - ClientProxyCore.terrainHeight;
                    final int pX = MathHelper.floor_double(entitylivingbase.posX / 16.0) << 4;
                    final int pZ = MathHelper.floor_double(entitylivingbase.posZ / 16.0) << 4;
                    float dX = (float)(rend.posX - pX);
                    float dZ = (float)(rend.posZ - pZ);
                    float scalerX = 0.0f;
                    float scalerZ = 0.0f;
                    if (dX > 0.0f) {
                        dX -= 16.0f;
                        if (dX > 0.0f) {
                            dX -= (float)(entitylivingbase.posX - pX);
                            if (dX < 16.0f) {
                                scalerX = 16.0f - ((float)entitylivingbase.posX - pX);
                            }
                            else {
                                scalerX = 16.0f;
                            }
                        }
                    }
                    else if (dX < 0.0f) {
                        dX += 16.0f;
                        if (dX < 0.0f) {
                            dX += (float)(16.0 - (entitylivingbase.posX - pX));
                            if (dX > -16.0f) {
                                scalerX = (float)entitylivingbase.posX - pX;
                            }
                            else {
                                scalerX = 16.0f;
                            }
                        }
                    }
                    if (dZ > 0.0f) {
                        dZ -= 16.0f;
                        if (dZ > 0.0f) {
                            dZ -= (float)(entitylivingbase.posZ - pZ);
                            if (dZ < 16.0f) {
                                scalerZ = 16.0f - ((float)entitylivingbase.posZ - pZ);
                            }
                            else {
                                scalerZ = 16.0f;
                            }
                        }
                    }
                    else if (dZ < 0.0f) {
                        dZ += 16.0f;
                        if (dZ < 0.0f) {
                            dZ += (float)(16.0 - (entitylivingbase.posZ - pZ));
                            if (dZ > -16.0f) {
                                scalerZ = (float)entitylivingbase.posZ - pZ;
                            }
                            else {
                                scalerZ = 16.0f;
                            }
                        }
                    }
                    final float origClipX = (float)rend.posXClip;
                    final float origClipY = (float)rend.posYClip;
                    final float origClipZ = (float)rend.posZClip;
                    float theta = (float)MathHelper.wrapAngleTo180_double(dX / globalArc);
                    float phi = (float)MathHelper.wrapAngleTo180_double(dZ / globalArc);
                    if (theta < 0.0f) {
                        theta += 360.0f;
                    }
                    if (phi < 0.0f) {
                        phi += 360.0f;
                    }
                    GL11.glTranslatef(origClipX - dX + 8.0f, -globeRadius + 8.0f, origClipZ - dZ + 8.0f);
                    if (theta > 0.0f) {
                        GL11.glRotatef(theta, 0.0f, 0.0f, -1.0f);
                    }
                    if (phi > 0.0f) {
                        GL11.glRotatef(phi, 1.0f, 0.0f, 0.0f);
                    }
                    GL11.glTranslatef(-8.0f, origClipY + globeRadius - 8.0f, -8.0f);
                    if (dX != 0.0f || dZ != 0.0f) {
                        final float scalex = (ClientProxyCore.globalRadius * 2.0f + scalerX) / ClientProxyCore.globalRadius / 2.0f;
                        final float scalez = (ClientProxyCore.globalRadius * 2.0f + scalerZ) / ClientProxyCore.globalRadius / 2.0f;
                        ClientProxyCore.scaleup.rewind();
                        ClientProxyCore.scaleup.put(scalex);
                        ClientProxyCore.scaleup.position(10);
                        ClientProxyCore.scaleup.put(scalez);
                        ClientProxyCore.scaleup.rewind();
                        GL11.glMultMatrix(ClientProxyCore.scaleup);
                        GL11.glTranslatef(-8.0f * (scalex - 1.0f), 0.0f, -8.0f * (scalez - 1.0f));
                    }
                    GL11.glTranslatef(-origClipX, -origClipY, -origClipZ);
                    ClientProxyCore.offsetY = rend.posY - ClientProxyCore.terrainHeight;
                }
                else {
                    ClientProxyCore.smallMoonActive = false;
                    ClientProxyCore.offsetY = 0.0;
                }
            }
            else {
                ClientProxyCore.terrainHeight = Float.MAX_VALUE;
                ClientProxyCore.globalRadius = Float.MAX_VALUE;
                ClientProxyCore.smallMoonActive = false;
                ClientProxyCore.offsetY = 0.0;
            }
        }
        GL11.glEndList();
    }

    @Override
    public EntityPlayer getPlayerFromNetHandler(final INetHandler handler) {
        if (handler instanceof NetHandlerPlayServer) {
            return (EntityPlayer)((NetHandlerPlayServer)handler).playerEntity;
        }
        return (EntityPlayer)FMLClientHandler.instance().getClientPlayerEntity();
    }

    public void addVertex(double x, final double y, double z) {
        final double var7 = 1.0 + (y + ClientProxyCore.offsetY) / ClientProxyCore.globalRadius;
        x += (x % 16.0 - 8.0) * var7 + 8.0;
        z += (z % 16.0 - 8.0) * var7 + 8.0;
    }

    @SubscribeEvent
    public void onRenderPlanetPre(final CelestialBodyRenderEvent.Pre event) {
        if (event.celestialBody == GalacticraftCore.planetOverworld) {
            if (!ClientProxyCore.overworldTextureRequestSent) {
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE, new Object[0]));
                ClientProxyCore.overworldTextureRequestSent = true;
            }
            if (ClientProxyCore.overworldTexturesValid) {
                event.celestialBodyTexture = null;
                GL11.glBindTexture(3553, ClientProxyCore.overworldTextureClient.getGlTextureId());
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlanetPost(final CelestialBodyRenderEvent.Post event) {
        if (ClientProxyCore.mc.currentScreen instanceof GuiCelestialSelection) {
            if (event.celestialBody == GalacticraftCore.planetSaturn) {
                ClientProxyCore.mc.renderEngine.bindTexture(ClientProxyCore.saturnRingTexture);
                final float size = GuiCelestialSelection.getWidthForCelestialBodyStatic(event.celestialBody) / 6.0f;
                ((GuiCelestialSelection)ClientProxyCore.mc.currentScreen).drawTexturedModalRect(-7.5f * size, -1.75f * size, 15.0f * size, 3.5f * size, 0.0f, 0.0f, 30.0f, 7.0f, false, false, 30.0f, 7.0f);
            }
            else if (event.celestialBody == GalacticraftCore.planetUranus) {
                ClientProxyCore.mc.renderEngine.bindTexture(ClientProxyCore.uranusRingTexture);
                final float size = GuiCelestialSelection.getWidthForCelestialBodyStatic(event.celestialBody) / 6.0f;
                ((GuiCelestialSelection)ClientProxyCore.mc.currentScreen).drawTexturedModalRect(-1.75f * size, -7.0f * size, 3.5f * size, 14.0f * size, 0.0f, 0.0f, 28.0f, 7.0f, false, false, 28.0f, 7.0f);
            }
        }
    }

    static {
        ClientProxyCore.footprintRenderer = new FootprintRenderer();
        ClientProxyCore.flagRequestsSent = new ArrayList<>();
        ClientProxyCore.valueableBlocks = Sets.newHashSet();
        ClientProxyCore.detectableBlocks = Sets.newHashSet();
        ClientProxyCore.playerItemData = Maps.newHashMap();
        ClientProxyCore.clientSpaceStationID = Maps.newHashMap();
        ClientProxyCore.galacticraftItem = EnumHelper.addRarity("GCRarity", EnumChatFormatting.BLUE, "Space");
        ClientProxyCore.capeMap = new HashMap<>();
        ClientProxyCore.dummyInventory = new InventoryExtended();
        underOilTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/underoil.png");
        ClientProxyCore.numbers = new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };
        ClientProxyCore.scaleup = BufferUtils.createFloatBuffer(512);
        ClientProxyCore.globalRadius = Float.MAX_VALUE;
        ClientProxyCore.offsetY = 0.0;
        ClientProxyCore.terrainHeight = Float.MAX_VALUE;
        ClientProxyCore.smallMoonActive = false;
        ClientProxyCore.capesMap = Maps.newHashMap();
        ClientProxyCore.playerClientHandler = new PlayerClient();
        ClientProxyCore.mc = FMLClientHandler.instance().getClient();
        ClientProxyCore.gearDataRequests = Lists.newArrayList();
        ClientProxyCore.PLAYER_Y_OFFSET = 1.62f;
        saturnRingTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/saturnRings.png");
        uranusRingTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/uranusRings.png");
    }

    public static class EventSpecialRender extends Event
    {
        public final float partialTicks;

        public EventSpecialRender(final float partialTicks) {
            this.partialTicks = partialTicks;
        }
    }
}
