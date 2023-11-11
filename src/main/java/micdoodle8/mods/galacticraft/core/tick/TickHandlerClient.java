package micdoodle8.mods.galacticraft.core.tick;

import micdoodle8.mods.galacticraft.core.proxy.*;
import com.google.common.collect.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.client.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.client.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;
import net.minecraft.client.settings.*;
import net.minecraft.client.resources.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.api.block.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import java.util.*;
import net.minecraft.tileentity.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.*;
import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.client.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.block.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.renderer.*;

public class TickHandlerClient
{
    public static int airRemaining;
    public static int airRemaining2;
    public static boolean checkedVersion;
    private static boolean lastInvKeyPressed;
    private static long tickCount;
    public static boolean spaceRaceGuiScheduled;
    private static ThreadRequirementMissing missingRequirementThread;
    public static HashSet<TileEntityScreen> screenConnectionsUpdateList;

    public static void registerDetectableBlocks(final boolean logging) {
        ClientProxyCore.detectableBlocks.clear();
        for (final String s : ConfigManagerCore.detectableIDs) {
            final BlockTuple bt = ConfigManagerCore.stringToBlock(s, "External Detectable IDs", logging);
            if (bt != null) {
                int meta = bt.meta;
                if (meta == -1) {
                    meta = 0;
                }
                boolean flag = false;
                for (final BlockMetaList blockMetaList : ClientProxyCore.detectableBlocks) {
                    if (blockMetaList.getBlock() == bt.block) {
                        if (!blockMetaList.getMetaList().contains(meta)) {
                            blockMetaList.getMetaList().add(meta);
                        }
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    final List<Integer> metaList = Lists.newArrayList();
                    metaList.add(meta);
                    ClientProxyCore.detectableBlocks.add(new BlockMetaList(bt.block, metaList));
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(final TickEvent.RenderTickEvent event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayerSP player = (EntityPlayerSP)minecraft.thePlayer;
        final EntityClientPlayerMP playerBaseClient = PlayerUtil.getPlayerBaseClientFromPlayer((EntityPlayer)player, false);
        GCPlayerStatsClient stats = null;
        if (player != null) {
            stats = GCPlayerStatsClient.get((EntityPlayerSP)playerBaseClient);
        }
        if (event.phase == TickEvent.Phase.END) {
            if (minecraft.currentScreen instanceof GuiIngameMenu) {
                final int i = Mouse.getEventX() * minecraft.currentScreen.width / minecraft.displayWidth;
                final int j = minecraft.currentScreen.height - Mouse.getEventY() * minecraft.currentScreen.height / minecraft.displayHeight - 1;
                int k = Mouse.getEventButton();
                if (Minecraft.isRunningOnMac && k == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
                    k = 1;
                }
                int deltaColor = 0;
                if (i > minecraft.currentScreen.width - 100 && j > minecraft.currentScreen.height - 35) {
                    deltaColor = 20;
                    if (k == 0 && Mouse.getEventButtonState()) {
                        minecraft.displayGuiScreen((GuiScreen)new GuiNewSpaceRace((EntityPlayer)playerBaseClient));
                    }
                }
                this.drawGradientRect(minecraft.currentScreen.width - 100, minecraft.currentScreen.height - 35, minecraft.currentScreen.width, minecraft.currentScreen.height, ColorUtil.to32BitColor(150, 10 + deltaColor, 10 + deltaColor, 10 + deltaColor), ColorUtil.to32BitColor(250, 10 + deltaColor, 10 + deltaColor, 10 + deltaColor));
                minecraft.fontRenderer.drawString(GCCoreUtil.translate("gui.spaceRace.create.title.name.0"), minecraft.currentScreen.width - 50 - minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.spaceRace.create.title.name.0")) / 2, minecraft.currentScreen.height - 26, ColorUtil.to32BitColor(255, 240, 240, 240));
                minecraft.fontRenderer.drawString(GCCoreUtil.translate("gui.spaceRace.create.title.name.1"), minecraft.currentScreen.width - 50 - minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.spaceRace.create.title.name.1")) / 2, minecraft.currentScreen.height - 16, ColorUtil.to32BitColor(255, 240, 240, 240));
                Gui.drawRect(minecraft.currentScreen.width - 100, minecraft.currentScreen.height - 35, minecraft.currentScreen.width - 99, minecraft.currentScreen.height, ColorUtil.to32BitColor(255, 0, 0, 0));
                Gui.drawRect(minecraft.currentScreen.width - 100, minecraft.currentScreen.height - 35, minecraft.currentScreen.width, minecraft.currentScreen.height - 34, ColorUtil.to32BitColor(255, 0, 0, 0));
            }
            if (player != null) {
                ClientProxyCore.playerPosX = player.prevPosX + (player.posX - player.prevPosX) * event.renderTickTime;
                ClientProxyCore.playerPosY = player.prevPosY + (player.posY - player.prevPosY) * event.renderTickTime;
                ClientProxyCore.playerPosZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.renderTickTime;
                ClientProxyCore.playerRotationYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.renderTickTime;
                ClientProxyCore.playerRotationPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.renderTickTime;
            }
            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityTier1Rocket) {
                float f = (((EntityTier1Rocket)player.ridingEntity).timeSinceLaunch - 250.0f) / 175.0f;
                if (f < 0.0f) {
                    f = 0.0f;
                }
                if (f > 1.0f) {
                    f = 1.0f;
                }
                final ScaledResolution scaledresolution = ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
                scaledresolution.getScaledWidth();
                scaledresolution.getScaledHeight();
                minecraft.entityRenderer.setupOverlayRendering();
                GL11.glEnable(3042);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, f);
                GL11.glDisable(3008);
                GL11.glDepthMask(true);
                GL11.glEnable(2929);
                GL11.glEnable(3008);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            if (minecraft.currentScreen == null && player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySpaceshipBase && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
                OverlayRocket.renderSpaceshipOverlay(((EntitySpaceshipBase)player.ridingEntity).getSpaceshipGui());
            }
            if (minecraft.currentScreen == null && player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
                OverlayLander.renderLanderOverlay();
            }
            if (minecraft.currentScreen == null && player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityAutoRocket && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
                OverlayDockingRocket.renderDockingOverlay();
            }
            if (minecraft.currentScreen == null && player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySpaceshipBase && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI && ((EntitySpaceshipBase)minecraft.thePlayer.ridingEntity).launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
                OverlayLaunchCountdown.renderCountdownOverlay();
            }
            if (player != null && player.worldObj.provider instanceof IGalacticraftWorldProvider && OxygenUtil.shouldDisplayTankGui(minecraft.currentScreen) && OxygenUtil.noAtmosphericCombustion(player.worldObj.provider)) {
                int var6 = (TickHandlerClient.airRemaining - 90) * -1;
                if (TickHandlerClient.airRemaining <= 0) {
                    var6 = 90;
                }
                int var7 = (TickHandlerClient.airRemaining2 - 90) * -1;
                if (TickHandlerClient.airRemaining2 <= 0) {
                    var7 = 90;
                }
                final int thermalLevel = stats.thermalLevel + 22;
                OverlayOxygenTanks.renderOxygenTankIndicator(thermalLevel, var6, var7, !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom, Math.abs(thermalLevel - 22) >= 10 && !stats.thermalLevelNormalising);
            }
            if (playerBaseClient != null && player.worldObj.provider instanceof IGalacticraftWorldProvider && !stats.oxygenSetupValid && OxygenUtil.noAtmosphericCombustion(player.worldObj.provider) && minecraft.currentScreen == null && !playerBaseClient.capabilities.isCreativeMode) {
                OverlayOxygenWarning.renderOxygenWarningOverlay();
            }
            try {
                final Class clazz = Class.forName("micdoodle8.mods.galacticraft.core.atoolkit.ProcessGraphic");
                clazz.getMethod("onTick", (Class[])new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception ex) {}
        }
    }

    @SubscribeEvent
    public void onPreGuiRender(final RenderGameOverlayEvent.Pre event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityClientPlayerMP player = minecraft.thePlayer;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && player != null && player.ridingEntity != null && player.ridingEntity instanceof IIgnoreShift && ((IIgnoreShift)player.ridingEntity).shouldIgnoreShiftExit()) {
            final String str = I18n.format("mount.onboard", new Object[] { GameSettings.getKeyDisplayString(minecraft.gameSettings.keyBindSneak.getKeyCode()) });
            if (minecraft.ingameGUI.recordPlaying.equals(str)) {
                minecraft.ingameGUI.recordPlaying = "";
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final WorldClient world = minecraft.theWorld;
        final EntityClientPlayerMP player = minecraft.thePlayer;
        if (event.phase == TickEvent.Phase.START) {
            if (TickHandlerClient.tickCount >= Long.MAX_VALUE) {
                TickHandlerClient.tickCount = 0L;
            }
            ++TickHandlerClient.tickCount;
            if (TickHandlerClient.tickCount % 20L == 0L) {
                for (final List<Footprint> fpList : ClientProxyCore.footprintRenderer.footprints.values()) {
                    final Iterator<Footprint> fpIt = fpList.iterator();
                    while (fpIt.hasNext()) {
                        final Footprint footprint;
                        final Footprint fp = footprint = fpIt.next();
                        footprint.age += 20;
                        if (fp.age >= 3200) {
                            fpIt.remove();
                        }
                    }
                }
                if (player != null && player.inventory.armorItemInSlot(3) != null && player.inventory.armorItemInSlot(3).getItem() instanceof ItemSensorGlasses) {
                    ClientProxyCore.valueableBlocks.clear();
                    for (int i = -4; i < 5; ++i) {
                        final int x = MathHelper.floor_double(player.posX + i);
                        for (int j = -4; j < 5; ++j) {
                            final int y = MathHelper.floor_double(player.posY + j);
                            for (int k = -4; k < 5; ++k) {
                                final int z = MathHelper.floor_double(player.posZ + k);
                                final Block block = player.worldObj.getBlock(x, y, z);
                                if (block.getMaterial() != Material.air) {
                                    final int metadata = world.getBlockMetadata(x, y, z);
                                    boolean isDetectable = false;
                                    for (final BlockMetaList blockMetaList : ClientProxyCore.detectableBlocks) {
                                        if (blockMetaList.getBlock() == block && blockMetaList.getMetaList().contains(metadata)) {
                                            isDetectable = true;
                                            break;
                                        }
                                    }
                                    if (isDetectable || (block instanceof IDetectableResource && ((IDetectableResource)block).isValueable(metadata))) {
                                        ClientProxyCore.valueableBlocks.add(new BlockVec3(x, y, z));
                                    }
                                }
                            }
                        }
                    }
                    final TileEntityOxygenSealer nearestSealer = TileEntityOxygenSealer.getNearestSealer((World)world, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
                    if (nearestSealer != null) {
                        (ClientProxyCore.leakTrace = new ArrayList()).add(new BlockVec3((TileEntity)nearestSealer).translate(0, 1, 0));
                    }
                }
                if (MapUtil.resetClientFlag.getAndSet(false)) {
                    MapUtil.resetClientBody();
                }
            }
            if (minecraft.currentScreen instanceof GuiMainMenu) {
                ClientProxyCore.reset();
                if (TickHandlerClient.missingRequirementThread == null) {
                    (TickHandlerClient.missingRequirementThread = new ThreadRequirementMissing(Side.CLIENT)).start();
                }
            }
            if (world != null && TickHandlerClient.spaceRaceGuiScheduled && minecraft.currentScreen == null && ConfigManagerCore.enableSpaceRaceManagerPopup) {
                player.openGui((Object)GalacticraftCore.instance, 6, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
                TickHandlerClient.spaceRaceGuiScheduled = false;
            }
            if (world != null && TickHandlerClient.checkedVersion) {
                ThreadVersionCheck.startCheck();
                TickHandlerClient.checkedVersion = false;
            }
            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySpaceshipBase) {
                final EntitySpaceshipBase rocket = (EntitySpaceshipBase)player.ridingEntity;
                if (rocket.prevRotationPitch != rocket.rotationPitch || rocket.prevRotationYaw != rocket.rotationYaw) {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketRotateRocket(player.ridingEntity));
                }
            }
            if (world != null) {
                if (world.provider instanceof WorldProviderSurface) {
                    if (world.provider.getSkyRenderer() == null && player.ridingEntity instanceof EntitySpaceshipBase && player.ridingEntity.posY > 200.0) {
                        world.provider.setSkyRenderer((IRenderHandler)new SkyProviderOverworld());
                    }
                    else if (world.provider.getSkyRenderer() instanceof SkyProviderOverworld && player.posY <= 200.0) {
                        world.provider.setSkyRenderer((IRenderHandler)null);
                    }
                }
                else if (world.provider instanceof WorldProviderSpaceStation) {
                    if (world.provider.getSkyRenderer() == null) {
                        ((WorldProviderSpaceStation)world.provider).createSkyProvider();
                        GCPlayerStatsClient.get((EntityPlayerSP)player).inFreefallFirstCheck = false;
                    }
                }
                else if (world.provider instanceof WorldProviderMoon) {
                    if (world.provider.getSkyRenderer() == null) {
                        world.provider.setSkyRenderer((IRenderHandler)new SkyProviderMoon());
                    }
                    if (world.provider.getCloudRenderer() == null) {
                        world.provider.setCloudRenderer((IRenderHandler)new CloudRenderer());
                    }
                }
            }
            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySpaceshipBase) {
                final EntitySpaceshipBase ship = (EntitySpaceshipBase)player.ridingEntity;
                boolean hasChanged = false;
                if (minecraft.gameSettings.keyBindLeft.getIsKeyPressed()) {
                    ship.turnYaw(-1.0f);
                    hasChanged = true;
                }
                if (minecraft.gameSettings.keyBindRight.getIsKeyPressed()) {
                    ship.turnYaw(1.0f);
                    hasChanged = true;
                }
                if (minecraft.gameSettings.keyBindForward.getIsKeyPressed() && ship.getLaunched()) {
                    ship.turnPitch(-0.7f);
                    hasChanged = true;
                }
                if (minecraft.gameSettings.keyBindBack.getIsKeyPressed() && ship.getLaunched()) {
                    ship.turnPitch(0.7f);
                    hasChanged = true;
                }
                if (hasChanged) {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketRotateRocket((Entity)ship));
                }
            }
            if (world != null) {
                final List entityList = world.loadedEntityList;
                for (final Object e : entityList) {
                    if (e instanceof IEntityNoisy) {
                        final IEntityNoisy vehicle = (IEntityNoisy)e;
                        if (vehicle.getSoundUpdater() != null) {
                            continue;
                        }
                        final ISound noise = vehicle.setSoundUpdater((EntityPlayerSP)FMLClientHandler.instance().getClient().thePlayer);
                        FMLClientHandler.instance().getClient().getSoundHandler().playSound(noise);
                    }
                }
            }
            if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection) {
                player.motionY = 0.0;
            }
            if (world != null && world.provider instanceof IGalacticraftWorldProvider && OxygenUtil.noAtmosphericCombustion(world.provider)) {
                world.setRainStrength(0.0f);
            }
            if (!KeyHandlerClient.spaceKey.getIsKeyPressed()) {
                ClientProxyCore.lastSpacebarDown = false;
            }
            if (player != null && player.ridingEntity != null && KeyHandlerClient.spaceKey.getIsKeyPressed() && !ClientProxyCore.lastSpacebarDown) {
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_IGNITE_ROCKET, new Object[0]));
                ClientProxyCore.lastSpacebarDown = true;
            }
            if (!TickHandlerClient.screenConnectionsUpdateList.isEmpty()) {
                final HashSet<TileEntityScreen> updateListCopy = (HashSet<TileEntityScreen>)TickHandlerClient.screenConnectionsUpdateList.clone();
                TickHandlerClient.screenConnectionsUpdateList.clear();
                for (final TileEntityScreen te : updateListCopy) {
                    if (te.refreshOnUpdate) {
                        te.refreshConnections(true);
                    }
                }
            }
        }
    }

    private boolean alreadyContainsBlock(final int x1, final int y1, final int z1) {
        return ClientProxyCore.valueableBlocks.contains(new BlockVec3(x1, y1, z1));
    }

    public static void zoom(final float value) {
        FMLClientHandler.instance().getClient().entityRenderer.thirdPersonDistance = value;
        FMLClientHandler.instance().getClient().entityRenderer.thirdPersonDistanceTemp = value;
    }

    private void drawGradientRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final float f = (par5 >> 24 & 0xFF) / 255.0f;
        final float f2 = (par5 >> 16 & 0xFF) / 255.0f;
        final float f3 = (par5 >> 8 & 0xFF) / 255.0f;
        final float f4 = (par5 & 0xFF) / 255.0f;
        final float f5 = (par6 >> 24 & 0xFF) / 255.0f;
        final float f6 = (par6 >> 16 & 0xFF) / 255.0f;
        final float f7 = (par6 >> 8 & 0xFF) / 255.0f;
        final float f8 = (par6 & 0xFF) / 255.0f;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(7425);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f2, f3, f4, f);
        tessellator.addVertex((double)par3, (double)par2, 0.0);
        tessellator.addVertex((double)par1, (double)par2, 0.0);
        tessellator.setColorRGBA_F(f6, f7, f8, f5);
        tessellator.addVertex((double)par1, (double)par4, 0.0);
        tessellator.addVertex((double)par3, (double)par4, 0.0);
        tessellator.draw();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    static {
        TickHandlerClient.checkedVersion = true;
        TickHandlerClient.spaceRaceGuiScheduled = false;
        TickHandlerClient.screenConnectionsUpdateList = new HashSet<TileEntityScreen>();
        registerDetectableBlocks(true);
    }
}
