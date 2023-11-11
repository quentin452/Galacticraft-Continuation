package micdoodle8.mods.galacticraft.core.network;

import io.netty.channel.*;
import io.netty.buffer.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.core.client.fx.*;
import net.minecraft.server.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraft.client.settings.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.core.command.*;
import net.minecraft.entity.player.*;
import com.mojang.authlib.properties.*;
import java.io.*;
import java.util.*;
import net.minecraft.client.particle.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.tileentity.*;
import com.mojang.authlib.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.network.play.server.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import cpw.mods.fml.server.*;
import com.google.common.collect.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.network.*;
import cpw.mods.fml.common.*;

public class PacketSimple extends Packet implements IPacket
{
    private EnumSimplePacket type;
    private List<Object> data;
    private static String spamCheckString;

    public PacketSimple() {
    }

    public PacketSimple(final EnumSimplePacket packetType, final Object[] data) {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimple(final EnumSimplePacket packetType, final List<Object> data) {
        if (packetType.getDecodeClasses().length != data.size()) {
            GCLog.info("Simple Packet Core found data length different than packet type");
            new RuntimeException().printStackTrace();
        }
        this.type = packetType;
        this.data = data;
    }

    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.type.ordinal());
        try {
            NetworkUtil.encodeData(buffer, (Collection)this.data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        this.type = EnumSimplePacket.values()[buffer.readInt()];
        try {
            if (this.type.getDecodeClasses().length > 0) {
                this.data = (List<Object>)NetworkUtil.decodeData((Class[])this.type.getDecodeClasses(), buffer);
            }
            if (buffer.readableBytes() > 0) {
                GCLog.severe("Galacticraft packet length problem for packet type " + this.type.toString());
            }
        }
        catch (Exception e) {
            System.err.println("[Galacticraft] Error handling simple packet type: " + this.type.toString() + " " + buffer.toString());
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClientSide(final EntityPlayer player) {
        EntityClientPlayerMP playerBaseClient = null;
        GCPlayerStatsClient stats = null;
        if (player instanceof EntityClientPlayerMP) {
            playerBaseClient = (EntityClientPlayerMP)player;
            stats = GCPlayerStatsClient.get((EntityPlayerSP)playerBaseClient);
        }
        else if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && this.type != EnumSimplePacket.C_UPDATE_CONFIGS) {
            return;
        }
        switch (this.type) {
            case C_AIR_REMAINING: {
                if (String.valueOf(this.data.get(2)).equals(String.valueOf(FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName()))) {
                    TickHandlerClient.airRemaining = (int) this.data.get(0);
                    TickHandlerClient.airRemaining2 = (int) this.data.get(1);
                    break;
                }
                break;
            }
            case C_UPDATE_DIMENSION_LIST: {
                if (String.valueOf(this.data.get(0)).equals(FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName())) {
                    final String dimensionList = (String) this.data.get(1);
                    if (ConfigManagerCore.enableDebug && !dimensionList.equals(PacketSimple.spamCheckString)) {
                        GCLog.info("DEBUG info: " + dimensionList);
                        PacketSimple.spamCheckString = new String(dimensionList);
                    }
                    final String[] destinations = dimensionList.split("\\?");
                    final List<CelestialBody> possibleCelestialBodies = Lists.newArrayList();
                    final Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> spaceStationData = Maps.newHashMap();
                    for (final String str : destinations) {
                        CelestialBody celestialBody = WorldUtil.getReachableCelestialBodiesForName(str);
                        if (celestialBody == null && str.contains("$")) {
                            final String[] values = str.split("\\$");
                            final int homePlanetID = Integer.parseInt(values[4]);
                            for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
                                if (satellite.getParentPlanet().getDimensionID() == homePlanetID) {
                                    celestialBody = satellite;
                                    break;
                                }
                            }
                            if (!spaceStationData.containsKey(homePlanetID)) {
                                spaceStationData.put(homePlanetID, new HashMap<>());
                            }
                            spaceStationData.get(homePlanetID).put(values[1], new GuiCelestialSelection.StationDataGUI(values[2], Integer.valueOf(Integer.parseInt(values[3]))));
                        }
                        if (celestialBody != null) {
                            possibleCelestialBodies.add(celestialBody);
                        }
                    }
                    if (FMLClientHandler.instance().getClient().theWorld != null) {
                        if (!(FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection)) {
                            final GuiCelestialSelection gui = new GuiCelestialSelection(false, (List)possibleCelestialBodies);
                            gui.spaceStationMap = spaceStationData;
                            FMLClientHandler.instance().getClient().displayGuiScreen(gui);
                        }
                        else {
                            ((GuiCelestialSelection)FMLClientHandler.instance().getClient().currentScreen).possibleBodies = possibleCelestialBodies;
                            ((GuiCelestialSelection)FMLClientHandler.instance().getClient().currentScreen).spaceStationMap = spaceStationData;
                        }
                    }
                    break;
                }
                break;
            }
            case C_SPAWN_SPARK_PARTICLES: {
                final int x = (int) this.data.get(0);
                final int y = (int) this.data.get(1);
                final int z = (int) this.data.get(2);
                final Minecraft mc = Minecraft.getMinecraft();
                for (int i = 0; i < 4; ++i) {
                    if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null && mc.theWorld != null) {
                        final EntityFX fx = new EntityFXSparks(mc.theWorld, x - 0.15 + 0.5, y + 1.2, z + 0.15 + 0.5, mc.theWorld.rand.nextDouble() / 20.0 - mc.theWorld.rand.nextDouble() / 20.0, mc.theWorld.rand.nextDouble() / 20.0 - mc.theWorld.rand.nextDouble() / 20.0);
                        if (fx != null) {
                            mc.effectRenderer.addEffect(fx);
                        }
                    }
                }
                break;
            }
            case C_UPDATE_GEAR_SLOT: {
                final int subtype = (int) this.data.get(2);
                EntityPlayer gearDataPlayer = null;
                final MinecraftServer server = MinecraftServer.getServer();
                final String gearName = (String) this.data.get(0);
                if (server != null) {
                    gearDataPlayer = (EntityPlayer)PlayerUtil.getPlayerForUsernameVanilla(server, gearName);
                }
                else {
                    gearDataPlayer = player.worldObj.getPlayerEntityByName(gearName);
                }
                if (gearDataPlayer != null) {
                    PlayerGearData gearData = ClientProxyCore.playerItemData.get(gearDataPlayer.getGameProfile().getName());
                    if (gearData == null) {
                        gearData = new PlayerGearData(player);
                        if (!ClientProxyCore.gearDataRequests.contains(gearName)) {
                            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(EnumSimplePacket.S_REQUEST_GEAR_DATA, new Object[] { gearName }));
                            ClientProxyCore.gearDataRequests.add(gearName);
                        }
                    }
                    else {
                        ClientProxyCore.gearDataRequests.remove(gearName);
                    }
                    final GCPlayerHandler.EnumModelPacket type = GCPlayerHandler.EnumModelPacket.values()[(int) this.data.get(1)];
                    switch (type) {
                        case ADDMASK: {
                            gearData.setMask(0);
                            break;
                        }
                        case REMOVEMASK: {
                            gearData.setMask(-1);
                            break;
                        }
                        case ADDGEAR: {
                            gearData.setGear(0);
                            break;
                        }
                        case REMOVEGEAR: {
                            gearData.setGear(-1);
                            break;
                        }
                        case ADDLEFTGREENTANK: {
                            gearData.setLeftTank(0);
                            break;
                        }
                        case ADDLEFTORANGETANK: {
                            gearData.setLeftTank(1);
                            break;
                        }
                        case ADDLEFTREDTANK: {
                            gearData.setLeftTank(2);
                            break;
                        }
                        case ADDRIGHTGREENTANK: {
                            gearData.setRightTank(0);
                            break;
                        }
                        case ADDRIGHTORANGETANK: {
                            gearData.setRightTank(1);
                            break;
                        }
                        case ADDRIGHTREDTANK: {
                            gearData.setRightTank(2);
                            break;
                        }
                        case REMOVE_LEFT_TANK: {
                            gearData.setLeftTank(-1);
                            break;
                        }
                        case REMOVE_RIGHT_TANK: {
                            gearData.setRightTank(-1);
                            break;
                        }
                        case ADD_PARACHUTE: {
                            String name = "";
                            if (subtype != -1) {
                                name = ItemParaChute.names[subtype];
                                gearData.setParachute(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/parachute/" + name + ".png"));
                                break;
                            }
                            break;
                        }
                        case REMOVE_PARACHUTE: {
                            gearData.setParachute(null);
                            break;
                        }
                        case ADD_FREQUENCY_MODULE: {
                            gearData.setFrequencyModule(0);
                            break;
                        }
                        case REMOVE_FREQUENCY_MODULE: {
                            gearData.setFrequencyModule(-1);
                            break;
                        }
                        case ADD_THERMAL_HELMET: {
                            gearData.setThermalPadding(0, 0);
                            break;
                        }
                        case ADD_THERMAL_CHESTPLATE: {
                            gearData.setThermalPadding(1, 0);
                            break;
                        }
                        case ADD_THERMAL_LEGGINGS: {
                            gearData.setThermalPadding(2, 0);
                            break;
                        }
                        case ADD_THERMAL_BOOTS: {
                            gearData.setThermalPadding(3, 0);
                            break;
                        }
                        case REMOVE_THERMAL_HELMET: {
                            gearData.setThermalPadding(0, -1);
                            break;
                        }
                        case REMOVE_THERMAL_CHESTPLATE: {
                            gearData.setThermalPadding(1, -1);
                            break;
                        }
                        case REMOVE_THERMAL_LEGGINGS: {
                            gearData.setThermalPadding(2, -1);
                            break;
                        }
                        case REMOVE_THERMAL_BOOTS: {
                            gearData.setThermalPadding(3, -1);
                            break;
                        }
                    }
                    ClientProxyCore.playerItemData.put(gearName, gearData);
                    break;
                }
                break;
            }
            case C_CLOSE_GUI: {
                FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen)null);
                break;
            }
            case C_RESET_THIRD_PERSON: {
                FMLClientHandler.instance().getClient().gameSettings.thirdPersonView = stats.thirdPersonView;
                break;
            }
            case C_UPDATE_SPACESTATION_LIST: {
                WorldUtil.decodeSpaceStationListClient(this.data);
                break;
            }
            case C_UPDATE_SPACESTATION_DATA: {
                final SpaceStationWorldData var4 = SpaceStationWorldData.getMPSpaceStationData(player.worldObj, (int)this.data.get(0), player);
                var4.readFromNBT((NBTTagCompound)this.data.get(1));
                break;
            }
            case C_UPDATE_SPACESTATION_CLIENT_ID: {
                ClientProxyCore.clientSpaceStationID = WorldUtil.stringToSpaceStationData((String) this.data.get(0));
                break;
            }
            case C_UPDATE_PLANETS_LIST: {
                WorldUtil.decodePlanetsListClient(this.data);
                break;
            }
            case C_UPDATE_CONFIGS: {
                ConfigManagerCore.saveClientConfigOverrideable();
                ConfigManagerCore.setConfigOverride(this.data);
                break;
            }
            case C_ADD_NEW_SCHEMATIC: {
                final ISchematicPage page = SchematicRegistry.getMatchingRecipeForID((int)this.data.get(0));
                if (!stats.unlockedSchematics.contains(page)) {
                    stats.unlockedSchematics.add(page);
                    break;
                }
                break;
            }
            case C_UPDATE_SCHEMATIC_LIST: {
                for (final Object o : this.data) {
                    final Integer schematicID = (Integer)o;
                    if (schematicID != -2) {
                        Collections.sort(stats.unlockedSchematics);
                        if (stats.unlockedSchematics.contains(SchematicRegistry.getMatchingRecipeForID((int)schematicID))) {
                            continue;
                        }
                        stats.unlockedSchematics.add(SchematicRegistry.getMatchingRecipeForID((int)schematicID));
                    }
                }
                break;
            }
            case C_PLAY_SOUND_BOSS_DEATH: {
                player.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.bossdeath", 10.0f, 0.8f);
                break;
            }
            case C_PLAY_SOUND_EXPLODE: {
                player.playSound("random.explode", 10.0f, 0.7f);
                break;
            }
            case C_PLAY_SOUND_BOSS_LAUGH: {
                player.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.bosslaugh", 10.0f, 0.2f);
                break;
            }
            case C_PLAY_SOUND_BOW: {
                player.playSound("random.bow", 10.0f, 0.2f);
                break;
            }
            case C_UPDATE_OXYGEN_VALIDITY: {
                stats.oxygenSetupValid = (boolean) this.data.get(0);
                break;
            }
            case C_OPEN_PARACHEST_GUI: {
                if (Objects.equals(this.data.get(1), 0)) {
                    if (player.ridingEntity instanceof EntityBuggy) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiBuggy(player.inventory, (IInventory) player.ridingEntity, ((EntityBuggy) player.ridingEntity).getType()));
                        player.openContainer.windowId = (int) this.data.get(0);
                    }
                } else if (Objects.equals(this.data.get(1), 1)) {
                    final int entityID = (int) this.data.get(2);
                    final Entity entity = player.worldObj.getEntityByID(entityID);
                    if (entity instanceof IInventorySettable) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiParaChest(player.inventory, (IInventory) entity));
                    }
                    player.openContainer.windowId = (int) this.data.get(0);
                }

                break;
            }
            case C_UPDATE_WIRE_BOUNDS: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                if (tile instanceof TileBaseConductor) {
                    ((TileBaseConductor)tile).adjacentConnections = null;
                    player.worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord).setBlockBoundsBasedOnState((IBlockAccess)player.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
                    break;
                }
                break;
            }
            case C_OPEN_SPACE_RACE_GUI: {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    TickHandlerClient.spaceRaceGuiScheduled = false;
                    player.openGui((Object)GalacticraftCore.instance, 6, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
                    break;
                }
                TickHandlerClient.spaceRaceGuiScheduled = true;
                break;
            }
            case C_UPDATE_SPACE_RACE_DATA: {
                final Integer teamID = (Integer) this.data.get(0);
                final String teamName = (String) this.data.get(1);
                final FlagData flagData = (FlagData) this.data.get(2);
                final Vector3 teamColor = (Vector3) this.data.get(3);
                final List<String> playerList = new ArrayList<>();
                for (int j = 4; j < this.data.size(); ++j) {
                    final String playerName = (String) this.data.get(j);
                    ClientProxyCore.flagRequestsSent.remove(playerName);
                    playerList.add(playerName);
                }
                final SpaceRace race = new SpaceRace(playerList, teamName, flagData, teamColor);
                race.setSpaceRaceID((int)teamID);
                SpaceRaceManager.addSpaceRace(race);
                break;
            }
            case C_OPEN_JOIN_RACE_GUI: {
                stats.spaceRaceInviteTeamID = (int) this.data.get(0);
                player.openGui(GalacticraftCore.instance, 7, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
                break;
            }
            case C_UPDATE_FOOTPRINT_LIST: {
                final List<Footprint> printList = new ArrayList<>();
                final long chunkKey = (long) this.data.get(0);
                for (int k = 1; k < this.data.size(); ++k) {
                    final Footprint print = (Footprint) this.data.get(k);
                    if (!print.owner.equals(player.getCommandSenderName())) {
                        printList.add(print);
                    }
                }
                ClientProxyCore.footprintRenderer.setFootprints(chunkKey, printList);
                break;
            }
            case C_FOOTPRINTS_REMOVED: {
                final long chunkKey2 = (long) this.data.get(0);
                final BlockVec3 position = (BlockVec3) this.data.get(1);
                final List<Footprint> footprintList = ClientProxyCore.footprintRenderer.footprints.get(chunkKey2);
                final List<Footprint> toRemove = new ArrayList<>();
                if (footprintList != null) {
                    for (final Footprint footprint : footprintList) {
                        if (footprint.position.x > position.x && footprint.position.x < position.x + 1 && footprint.position.z > position.z && footprint.position.z < position.z + 1) {
                            toRemove.add(footprint);
                        }
                    }
                }
                if (!toRemove.isEmpty()) {
                    footprintList.removeAll(toRemove);
                    ClientProxyCore.footprintRenderer.footprints.put(chunkKey2, footprintList);
                    break;
                }
                break;
            }
            case C_UPDATE_STATION_SPIN: {
                if (playerBaseClient.worldObj.provider instanceof WorldProviderSpaceStation) {
                    ((WorldProviderSpaceStation)playerBaseClient.worldObj.provider).getSpinManager().setSpinRate((float)this.data.get(0), (boolean)this.data.get(1));
                    break;
                }
                break;
            }
            case C_UPDATE_STATION_DATA: {
                if (playerBaseClient.worldObj.provider instanceof WorldProviderSpaceStation) {
                    ((WorldProviderSpaceStation)playerBaseClient.worldObj.provider).getSpinManager().setSpinCentre((double)this.data.get(0), (double)this.data.get(1));
                    break;
                }
                break;
            }
            case C_UPDATE_STATION_BOX: {
                if (playerBaseClient.worldObj.provider instanceof WorldProviderSpaceStation) {
                    ((WorldProviderSpaceStation)playerBaseClient.worldObj.provider).getSpinManager().setSpinBox((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2), (int)this.data.get(3), (int)this.data.get(4), (int)this.data.get(5));
                    break;
                }
                break;
            }
            case C_UPDATE_THERMAL_LEVEL: {
                stats.thermalLevel = (int) this.data.get(0);
                stats.thermalLevelNormalising = (boolean) this.data.get(1);
                break;
            }
            case C_DISPLAY_ROCKET_CONTROLS: {
                player.addChatMessage(new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode()) + "  - " + GCCoreUtil.translate("gui.rocket.launch.name")));
                player.addChatMessage(new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.leftKey.getKeyCode()) + " / " + GameSettings.getKeyDisplayString(KeyHandlerClient.rightKey.getKeyCode()) + "  - " + GCCoreUtil.translate("gui.rocket.turn.name")));
                player.addChatMessage(new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.accelerateKey.getKeyCode()) + " / " + GameSettings.getKeyDisplayString(KeyHandlerClient.decelerateKey.getKeyCode()) + "  - " + GCCoreUtil.translate("gui.rocket.updown.name")));
                player.addChatMessage(new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.openFuelGui.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.rocket.inv.name")));
                break;
            }
            case C_GET_CELESTIAL_BODY_LIST: {
                String str2 = "";
                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredPlanets().values()) {
                    str2 = str2.concat(cBody.getUnlocalizedName() + ";");
                }
                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredMoons().values()) {
                    str2 = str2.concat(cBody.getUnlocalizedName() + ";");
                }
                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredSatellites().values()) {
                    str2 = str2.concat(cBody.getUnlocalizedName() + ";");
                }
                for (final SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values()) {
                    str2 = str2.concat(solarSystem.getUnlocalizedName() + ";");
                }
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE, new Object[] { str2 }));
                break;
            }
            case C_UPDATE_ENERGYUNITS: {
                CommandGCEnergyUnits.handleParamClientside((int)this.data.get(0));
                break;
            }
            case C_RESPAWN_PLAYER: {
                final WorldProvider provider = WorldUtil.getProviderForNameClient((String) this.data.get(0));
                final int dimID = provider.dimensionId;
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("DEBUG: Client receiving respawn packet for dim " + dimID);
                }
                final int par2 = (int) this.data.get(1);
                final String par3 = (String) this.data.get(2);
                final int par4 = (int) this.data.get(3);
                WorldUtil.forceRespawnClient(dimID, par2, par3, par4);
                break;
            }
            case C_UPDATE_ARCLAMP_FACING: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                final int facingNew = (int) this.data.get(3);
                if (tile instanceof TileEntityArclamp) {
                    ((TileEntityArclamp)tile).facing = facingNew;
                    break;
                }
                break;
            }
            case C_UPDATE_STATS: {
                stats.buildFlags = (int) this.data.get(0);
                break;
            }
            case C_UPDATE_VIEWSCREEN: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                if (tile instanceof TileEntityScreen) {
                    final TileEntityScreen screenTile = (TileEntityScreen)tile;
                    final int screenType = (int) this.data.get(3);
                    final int flags = (int) this.data.get(4);
                    screenTile.imageType = screenType;
                    screenTile.connectedUp = ((flags & 0x8) != 0x0);
                    screenTile.connectedDown = ((flags & 0x4) != 0x0);
                    screenTile.connectedLeft = ((flags & 0x2) != 0x0);
                    screenTile.connectedRight = ((flags & 0x1) != 0x0);
                    screenTile.refreshNextTick(true);
                    break;
                }
                break;
            }
            case C_UPDATE_TELEMETRY: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                if (tile instanceof TileEntityTelemetry) {
                    final String name2 = (String) this.data.get(3);
                    if (name2.startsWith("$")) {
                        ((TileEntityTelemetry)tile).clientClass = EntityPlayerMP.class;
                        final String strName = name2.substring(1);
                        ((TileEntityTelemetry)tile).clientName = strName;
                        GameProfile profile = FMLClientHandler.instance().getClientPlayerEntity().getGameProfile();
                        if (!strName.equals(profile.getName())) {
                            profile = PlayerUtil.getOtherPlayerProfile(strName);
                            if (profile == null) {
                                final String strUUID = (String) this.data.get(9);
                                profile = PlayerUtil.makeOtherPlayerProfile(strName, strUUID);
                            }
                            if (VersionUtil.mcVersion1_7_10 && !profile.getProperties().containsKey((Object)"textures")) {
                                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(EnumSimplePacket.S_REQUEST_PLAYERSKIN, new Object[] { strName }));
                            }
                        }
                        ((TileEntityTelemetry)tile).clientGameProfile = profile;
                    }
                    else {
                        ((TileEntityTelemetry)tile).clientClass = (Class) EntityList.stringToClassMapping.get(name2);
                    }
                    ((TileEntityTelemetry)tile).clientData = new int[5];
                    for (int l = 4; l < 9; ++l) {
                        ((TileEntityTelemetry)tile).clientData[l - 4] = (int) this.data.get(l);
                    }
                    break;
                }
                break;
            }
            case C_SEND_PLAYERSKIN: {
                final String strName2 = (String) this.data.get(0);
                final String s1 = (String) this.data.get(1);
                final String s2 = (String) this.data.get(2);
                final String strUUID = (String) this.data.get(3);
                GameProfile gp = PlayerUtil.getOtherPlayerProfile(strName2);
                if (gp == null) {
                    gp = PlayerUtil.makeOtherPlayerProfile(strName2, strUUID);
                }
                gp.getProperties().put("textures", new Property("textures", s1, s2));
                break;
            }
            case C_SEND_OVERWORLD_IMAGE: {
                try {
                    final int cx = (int) this.data.get(0);
                    final int cz = (int) this.data.get(1);
                    final byte[] bytes = (byte[]) this.data.get(2);
                    try {
                        final File folder = new File(FMLClientHandler.instance().getClient().mcDataDir, "assets/temp");
                        if (folder.exists() || folder.mkdir()) {
                            MapUtil.getOverworldImageFromRaw(folder, cx, cz, bytes);
                        }
                        else {
                            System.err.println("Cannot create directory %minecraftDir%/assets/temp!");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception ex) {}
                break;
            }
        }
    }

    public void handleServerSide(final EntityPlayer player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        if (playerBase == null) {
            return;
        }
        final GCPlayerStats stats = GCPlayerStats.get(playerBase);
        Label_3781: {
            switch (this.type) {
                case S_RESPAWN_PLAYER: {
                    playerBase.playerNetServerHandler.sendPacket((Packet)new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), playerBase.theItemInWorldManager.getGameType()));
                    break;
                }
                case S_TELEPORT_ENTITY: {
                    try {
                        final WorldProvider provider = WorldUtil.getProviderForNameServer((String) this.data.get(0));
                        final Integer dim = provider.dimensionId;
                        GCLog.info("Found matching world (" + dim.toString() + ") for name: " + this.data.get(0));
                        if (playerBase.worldObj instanceof WorldServer) {
                            final WorldServer world = (WorldServer)playerBase.worldObj;
                            WorldUtil.transferEntityToDimension((Entity)playerBase, dim, world);
                        }
                        stats.teleportCooldown = 10;
                        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(EnumSimplePacket.C_CLOSE_GUI, new Object[0]), playerBase);
                    }
                    catch (Exception e) {
                        GCLog.severe("Error occurred when attempting to transfer entity to dimension: " + this.data.get(0));
                        e.printStackTrace();
                    }
                    break;
                }
                case S_IGNITE_ROCKET: {
                    if (!player.worldObj.isRemote && !player.isDead && player.ridingEntity != null && !player.ridingEntity.isDead && player.ridingEntity instanceof EntityTieredRocket) {
                        final EntityTieredRocket ship = (EntityTieredRocket)player.ridingEntity;
                        if (!ship.landing) {
                            if (ship.hasValidFuel()) {
                                final ItemStack stack2 = stats.extendedInventory.getStackInSlot(4);
                                if ((stack2 != null && stack2.getItem() instanceof ItemParaChute) || stats.launchAttempts > 0) {
                                    ship.igniteCheckingCooldown();
                                    stats.launchAttempts = 0;
                                }
                                else if (stats.chatCooldown == 0 && stats.launchAttempts == 0) {
                                    player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.rocket.warning.noparachute")));
                                    stats.chatCooldown = 250;
                                    stats.launchAttempts = 1;
                                }
                            }
                            else if (stats.chatCooldown == 0) {
                                player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.rocket.warning.nofuel")));
                                stats.chatCooldown = 250;
                            }
                        }
                        break;
                    }
                    break;
                }
                case S_OPEN_SCHEMATIC_PAGE: {
                    if (player != null) {
                        final ISchematicPage page = SchematicRegistry.getMatchingRecipeForID((int)this.data.get(0));
                        player.openGui((Object)GalacticraftCore.instance, page.getGuiID(), player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
                        break;
                    }
                    break;
                }
                case S_OPEN_FUEL_GUI: {
                    if (player.ridingEntity instanceof EntityBuggy) {
                        GCCoreUtil.openBuggyInv(playerBase, (IInventory)player.ridingEntity, ((EntityBuggy)player.ridingEntity).getType());
                        break;
                    }
                    if (player.ridingEntity instanceof EntitySpaceshipBase) {
                        player.openGui((Object)GalacticraftCore.instance, 4, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
                        break;
                    }
                    break;
                }
                case S_UPDATE_SHIP_YAW: {
                    if (player.ridingEntity instanceof EntitySpaceshipBase) {
                        final EntitySpaceshipBase ship2 = (EntitySpaceshipBase)player.ridingEntity;
                        if (ship2 != null) {
                            ship2.rotationYaw = (float) this.data.get(0);
                        }
                        break;
                    }
                    break;
                }
                case S_UPDATE_SHIP_PITCH: {
                    if (player.ridingEntity instanceof EntitySpaceshipBase) {
                        final EntitySpaceshipBase ship2 = (EntitySpaceshipBase)player.ridingEntity;
                        if (ship2 != null) {
                            ship2.rotationPitch = (float) this.data.get(0);
                        }
                        break;
                    }
                    break;
                }
                case S_SET_ENTITY_FIRE: {
                    final Entity entity = player.worldObj.getEntityByID((int)this.data.get(0));
                    if (entity instanceof EntityLivingBase) {
                        ((EntityLivingBase)entity).setFire(3);
                        break;
                    }
                    break;
                }
                case S_BIND_SPACE_STATION_ID: {
                    final int homeID = (int) this.data.get(0);
                    if ((!stats.spaceStationDimensionData.containsKey(homeID) || stats.spaceStationDimensionData.get(homeID) == -1 || stats.spaceStationDimensionData.get(homeID) == 0) && !ConfigManagerCore.disableSpaceStationCreation && (playerBase.capabilities.isCreativeMode || WorldUtil.getSpaceStationRecipe(homeID).matches((EntityPlayer)playerBase, true))) {
                        WorldUtil.bindSpaceStationToNewDimension(playerBase.worldObj, playerBase, homeID);
                        break;
                    }
                    break;
                }
                case S_UNLOCK_NEW_SCHEMATIC: {
                    final Container container = player.openContainer;
                    if (container instanceof ContainerSchematic) {
                        final ContainerSchematic schematicContainer = (ContainerSchematic)container;
                        ItemStack stack3 = schematicContainer.craftMatrix.getStackInSlot(0);
                        if (stack3 != null) {
                            final ISchematicPage page2 = SchematicRegistry.getMatchingRecipeForItemStack(stack3);
                            if (page2 != null) {
                                SchematicRegistry.unlockNewPage(playerBase, stack3);
                                final ItemStack itemStack = stack3;
                                if (--itemStack.stackSize <= 0) {
                                    stack3 = null;
                                }
                                schematicContainer.craftMatrix.setInventorySlotContents(0, stack3);
                                schematicContainer.craftMatrix.markDirty();
                                GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(EnumSimplePacket.C_ADD_NEW_SCHEMATIC, new Object[] { page2.getPageID() }), playerBase);
                            }
                        }
                        break;
                    }
                    break;
                }
                case S_UPDATE_DISABLEABLE_BUTTON: {
                    final TileEntity tileAt = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                    if (tileAt instanceof IDisableableMachine) {
                        final IDisableableMachine machine = (IDisableableMachine)tileAt;
                        machine.setDisabled((int)this.data.get(3), !machine.getDisabled((int)this.data.get(3)));
                        break;
                    }
                    break;
                }
                case S_ON_FAILED_CHEST_UNLOCK: {
                    if (stats.chatCooldown == 0) {
                        player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("gui.chest.warning.wrongkey", this.data.get(0))));
                        stats.chatCooldown = 100;
                        break;
                    }
                    break;
                }
                case S_RENAME_SPACE_STATION: {
                    final SpaceStationWorldData ssdata = SpaceStationWorldData.getStationData(playerBase.worldObj, (int)this.data.get(1), (EntityPlayer)playerBase);
                    if (ssdata != null && ssdata.getOwner().equalsIgnoreCase(player.getGameProfile().getName())) {
                        ssdata.setSpaceStationName((String)this.data.get(0));
                        ssdata.setDirty(true);
                        break;
                    }
                    break;
                }
                case S_OPEN_EXTENDED_INVENTORY: {
                    player.openGui(GalacticraftCore.instance, 5, player.worldObj, 0, 0, 0);
                    break;
                }
                case S_ON_ADVANCED_GUI_CLICKED_INT: {
                    final TileEntity tile1 = player.worldObj.getTileEntity((int)this.data.get(1), (int)this.data.get(2), (int)this.data.get(3));
                    switch ((Integer) this.data.get(0)) {
                        case 0: {
                            if (tile1 instanceof TileEntityAirLockController) {
                                final TileEntityAirLockController airlockController = (TileEntityAirLockController) tile1;
                                airlockController.redstoneActivation = Objects.equals(this.data.get(4), 1);
                                break Label_3781;
                            }
                            break Label_3781;
                        }
                        case 1: {
                            if (tile1 instanceof TileEntityAirLockController) {
                                final TileEntityAirLockController airlockController = (TileEntityAirLockController) tile1;
                                airlockController.playerDistanceActivation = Objects.equals(this.data.get(4), 1);
                                break Label_3781;
                            }
                            break Label_3781;
                        }
                        case 2: {
                            if (tile1 instanceof TileEntityAirLockController) {
                                final TileEntityAirLockController airlockController = (TileEntityAirLockController)tile1;
                                airlockController.playerDistanceSelection = (int) this.data.get(4);
                                break Label_3781;
                            }
                            break Label_3781;
                        }
                        case 3: {
                            if (tile1 instanceof TileEntityAirLockController) {
                                final TileEntityAirLockController airlockController = (TileEntityAirLockController) tile1;
                                airlockController.playerNameMatches = Objects.equals(this.data.get(4), 1);
                                break Label_3781;
                            }
                            break Label_3781;
                        }

                        case 4: {
                            if (tile1 instanceof TileEntityAirLockController) {
                                final TileEntityAirLockController airlockController = (TileEntityAirLockController) tile1;
                                airlockController.invertSelection = Objects.equals(this.data.get(4), 1);
                                break Label_3781;
                            }
                            break Label_3781;
                        }

                        case 5: {
                            if (tile1 instanceof TileEntityAirLockController) {
                                final TileEntityAirLockController airlockController = (TileEntityAirLockController) tile1;
                                airlockController.lastHorizontalModeEnabled = airlockController.horizontalModeEnabled;
                                airlockController.horizontalModeEnabled = Objects.equals(this.data.get(4), 1);
                                break Label_3781;
                            }
                            break Label_3781;
                        }

                        case 6: {
                            if (tile1 instanceof IBubbleProvider) {
                                final IBubbleProvider distributor = (IBubbleProvider) tile1;
                                distributor.setBubbleVisible(Objects.equals(this.data.get(4), 1));
                                break Label_3781;
                            }
                            break Label_3781;
                        }
                        default: {
                            break Label_3781;
                        }
                    }
                }
                case S_ON_ADVANCED_GUI_CLICKED_STRING: {
                    final TileEntity tile2 = player.worldObj.getTileEntity((int)this.data.get(1), (int)this.data.get(2), (int)this.data.get(3));
                    if (Objects.equals(this.data.get(0), 0)) {
                        if (tile2 instanceof TileEntityAirLockController) {
                            final TileEntityAirLockController airlockController2 = (TileEntityAirLockController) tile2;
                            airlockController2.playerToOpenFor = (String) this.data.get(4);
                            break Label_3781;
                        }
                        break Label_3781;
                    }
                    break Label_3781;
                }
                case S_UPDATE_SHIP_MOTION_Y: {
                    final int entityID = (int) this.data.get(0);
                    final boolean up = (boolean) this.data.get(1);
                    final Entity entity2 = player.worldObj.getEntityByID(entityID);
                    if (entity2 instanceof EntityAutoRocket) {
                        final EntityAutoRocket entityAutoRocket;
                        final EntityAutoRocket autoRocket = entityAutoRocket = (EntityAutoRocket)entity2;
                        entityAutoRocket.motionY += (up ? 0.019999999552965164 : -0.019999999552965164);
                        break;
                    }
                    break;
                }
                case S_START_NEW_SPACE_RACE: {
                    final Integer teamID = (Integer) this.data.get(0);
                    final String teamName = (String) this.data.get(1);
                    final FlagData flagData = (FlagData) this.data.get(2);
                    final Vector3 teamColor = (Vector3) this.data.get(3);
                    final List<String> playerList = new ArrayList<String>();
                    for (int i = 4; i < this.data.size(); ++i) {
                        playerList.add((String) this.data.get(i));
                    }
                    final boolean previousData = SpaceRaceManager.getSpaceRaceFromID((int)teamID) != null;
                    final SpaceRace newRace = new SpaceRace((List)playerList, teamName, flagData, teamColor);
                    if (teamID > 0) {
                        newRace.setSpaceRaceID((int)teamID);
                    }
                    SpaceRaceManager.addSpaceRace(newRace);
                    if (previousData) {
                        SpaceRaceManager.sendSpaceRaceData((EntityPlayerMP)null, SpaceRaceManager.getSpaceRaceFromPlayer(playerBase.getGameProfile().getName()));
                        break;
                    }
                    break;
                }
                case S_REQUEST_FLAG_DATA: {
                    SpaceRaceManager.sendSpaceRaceData(playerBase, SpaceRaceManager.getSpaceRaceFromPlayer((String)this.data.get(0)));
                    break;
                }
                case S_INVITE_RACE_PLAYER: {
                    final EntityPlayerMP playerInvited = PlayerUtil.getPlayerBaseServerFromPlayerUsername((String) this.data.get(0), true);
                    if (playerInvited != null) {
                        final Integer teamInvitedTo = (Integer) this.data.get(1);
                        final SpaceRace race = SpaceRaceManager.getSpaceRaceFromID((int)teamInvitedTo);
                        if (race != null) {
                            GCPlayerStats.get(playerInvited).spaceRaceInviteTeamID = teamInvitedTo;
                            final String dA = EnumColor.DARK_AQUA.getCode();
                            final String bG = EnumColor.BRIGHT_GREEN.getCode();
                            final String dB = EnumColor.PURPLE.getCode();
                            String teamNameTotal = "";
                            final String[] split;
                            final String[] teamNameSplit = split = race.getTeamName().split(" ");
                            for (final String teamNamePart : split) {
                                teamNameTotal = teamNameTotal.concat(dB + teamNamePart + " ");
                            }
                            playerInvited.addChatMessage(new ChatComponentText(dA + GCCoreUtil.translateWithFormat("gui.spaceRace.chat.inviteReceived", bG + player.getGameProfile().getName() + dA) + "  " + GCCoreUtil.translateWithFormat("gui.spaceRace.chat.toJoin", teamNameTotal, EnumColor.AQUA + "/joinrace" + dA)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA)));
                        }
                        break;
                    }
                    break;
                }
                case S_REMOVE_RACE_PLAYER: {
                    final Integer teamInvitedTo = (Integer) this.data.get(1);
                    final SpaceRace race = SpaceRaceManager.getSpaceRaceFromID((int)teamInvitedTo);
                    if (race != null) {
                        final String playerToRemove = (String) this.data.get(0);
                        if (!race.getPlayerNames().remove(playerToRemove)) {
                            player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spaceRace.chat.notFound", playerToRemove)));
                        }
                        else {
                            SpaceRaceManager.onPlayerRemoval(playerToRemove, race);
                        }
                        break;
                    }
                    break;
                }
                case S_ADD_RACE_PLAYER: {
                    final Integer teamToAddPlayer = (Integer) this.data.get(1);
                    final SpaceRace spaceRaceToAddPlayer = SpaceRaceManager.getSpaceRaceFromID((int)teamToAddPlayer);
                    if (spaceRaceToAddPlayer != null) {
                        final String playerToAdd = (String) this.data.get(0);
                        if (!spaceRaceToAddPlayer.getPlayerNames().contains(playerToAdd)) {
                            SpaceRace oldRace = null;
                            while ((oldRace = SpaceRaceManager.getSpaceRaceFromPlayer(playerToAdd)) != null) {
                                SpaceRaceManager.removeSpaceRace(oldRace);
                            }
                            spaceRaceToAddPlayer.getPlayerNames().add(playerToAdd);
                            SpaceRaceManager.sendSpaceRaceData(null, spaceRaceToAddPlayer);
                            for (final String member : spaceRaceToAddPlayer.getPlayerNames()) {
                                final EntityPlayerMP memberObj = PlayerUtil.getPlayerForUsernameVanilla(MinecraftServer.getServer(), member);
                                if (memberObj != null) {
                                    memberObj.addChatMessage(new ChatComponentText(EnumColor.DARK_AQUA + GCCoreUtil.translateWithFormat("gui.spaceRace.chat.addSuccess", EnumColor.BRIGHT_GREEN + playerToAdd + EnumColor.DARK_AQUA)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA)));
                                }
                            }
                        }
                        else {
                            player.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.spaceRace.chat.alreadyPart")).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED)));
                        }
                        break;
                    }
                    break;
                }
                case S_COMPLETE_CBODY_HANDSHAKE: {
                    final String completeList = (String) this.data.get(0);
                    final List<String> clientObjects = Arrays.asList(completeList.split(";"));
                    final List<String> serverObjects = Lists.newArrayList();
                    String missingObjects = "";
                    for (final CelestialBody cBody : GalaxyRegistry.getRegisteredPlanets().values()) {
                        serverObjects.add(cBody.getUnlocalizedName());
                    }
                    for (final CelestialBody cBody : GalaxyRegistry.getRegisteredMoons().values()) {
                        serverObjects.add(cBody.getUnlocalizedName());
                    }
                    for (final CelestialBody cBody : GalaxyRegistry.getRegisteredSatellites().values()) {
                        serverObjects.add(cBody.getUnlocalizedName());
                    }
                    for (final SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values()) {
                        serverObjects.add(solarSystem.getUnlocalizedName());
                    }
                    for (final String str : serverObjects) {
                        if (!clientObjects.contains(str)) {
                            missingObjects = missingObjects.concat(str + "\n");
                        }
                    }
                    if (missingObjects.length() > 0) {
                        playerBase.playerNetServerHandler.kickPlayerFromServer("Missing Galacticraft Celestial Objects:\n\n " + missingObjects);
                        break;
                    }
                    break;
                }
                case S_REQUEST_GEAR_DATA: {
                    final String name = (String) this.data.get(0);
                    final EntityPlayerMP e2 = PlayerUtil.getPlayerBaseServerFromPlayerUsername(name, true);
                    if (e2 != null) {
                        GCPlayerHandler.checkGear(e2, GCPlayerStats.get(e2), true);
                        break;
                    }
                    break;
                }
                case S_REQUEST_ARCLAMP_FACING: {
                    final TileEntity tileAL = player.worldObj.getTileEntity((int)this.data.get(0), (int)this.data.get(1), (int)this.data.get(2));
                    if (tileAL instanceof TileEntityArclamp) {
                        ((TileEntityArclamp)tileAL).updateClientFlag = true;
                        break;
                    }
                    break;
                }
                case S_BUILDFLAGS_UPDATE: {
                    stats.buildFlags = (int) this.data.get(0);
                    break;
                }
                case S_UPDATE_VIEWSCREEN_REQUEST: {
                    final int screenDim = (int) this.data.get(0);
                    final TileEntity tile3 = player.worldObj.getTileEntity((int)this.data.get(1), (int)this.data.get(2), (int)this.data.get(3));
                    if (tile3 instanceof TileEntityScreen) {
                        ((TileEntityScreen)tile3).updateClients();
                        break;
                    }
                    break;
                }
                case S_REQUEST_OVERWORLD_IMAGE: {
                    MapUtil.sendOverworldToClient(playerBase);
                    break;
                }
                case S_REQUEST_MAP_IMAGE: {
                    final int dim2 = (int) this.data.get(0);
                    final int cx = (int) this.data.get(1);
                    final int cz = (int) this.data.get(2);
                    MapUtil.sendOrCreateMap(WorldUtil.getProviderForDimensionServer(dim2).worldObj, cx, cz, playerBase);
                    break;
                }
                case S_REQUEST_PLAYERSKIN: {
                    final String strName = (String) this.data.get(0);
                    final EntityPlayerMP playerRequested = FMLServerHandler.instance().getServer().getConfigurationManager().func_152612_a(strName);
                    if (playerRequested == null) {
                        return;
                    }
                    final GameProfile gp = playerRequested.getGameProfile();
                    if (gp == null) {
                        return;
                    }
                    final Property property = (Property)Iterables.getFirst(gp.getProperties().get("textures"), (Object)null);
                    if (property == null) {
                        return;
                    }
                    GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_SEND_PLAYERSKIN, new Object[] { strName, property.getValue(), property.getSignature(), playerRequested.getUniqueID().toString() }), playerBase);
                    break;
                }
            }
        }
    }

    public void readPacketData(final PacketBuffer var1) {
        this.decodeInto(null, var1);
    }

    public void writePacketData(final PacketBuffer var1) {
        this.encodeInto(null, var1);
    }

    @SideOnly(Side.CLIENT)
    public void processPacket(final INetHandler var1) {
        if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && this.type != EnumSimplePacket.C_UPDATE_CONFIGS) {
            return;
        }
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            this.handleClientSide((EntityPlayer)FMLClientHandler.instance().getClientPlayerEntity());
        }
    }

    public enum EnumSimplePacket
    {
        S_RESPAWN_PLAYER(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_TELEPORT_ENTITY(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_IGNITE_ROCKET(Side.SERVER, (Class<?>[])new Class[0]),
        S_OPEN_SCHEMATIC_PAGE(Side.SERVER, (Class<?>[])new Class[] { Integer.class }),
        S_OPEN_FUEL_GUI(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_UPDATE_SHIP_YAW(Side.SERVER, (Class<?>[])new Class[] { Float.class }),
        S_UPDATE_SHIP_PITCH(Side.SERVER, (Class<?>[])new Class[] { Float.class }),
        S_SET_ENTITY_FIRE(Side.SERVER, (Class<?>[])new Class[] { Integer.class }),
        S_BIND_SPACE_STATION_ID(Side.SERVER, (Class<?>[])new Class[] { Integer.class }),
        S_UNLOCK_NEW_SCHEMATIC(Side.SERVER, new Class[0]),
        S_UPDATE_DISABLEABLE_BUTTON(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class }),
        S_ON_FAILED_CHEST_UNLOCK(Side.SERVER, (Class<?>[])new Class[] { Integer.class }),
        S_RENAME_SPACE_STATION(Side.SERVER, (Class<?>[])new Class[] { String.class, Integer.class }),
        S_OPEN_EXTENDED_INVENTORY(Side.SERVER, (Class<?>[])new Class[0]),
        S_ON_ADVANCED_GUI_CLICKED_INT(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class }),
        S_ON_ADVANCED_GUI_CLICKED_STRING(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, String.class }),
        S_UPDATE_SHIP_MOTION_Y(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Boolean.class }),
        S_START_NEW_SPACE_RACE(Side.SERVER, (Class<?>[])new Class[] { Integer.class, String.class, FlagData.class, Vector3.class, String[].class }),
        S_REQUEST_FLAG_DATA(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_INVITE_RACE_PLAYER(Side.SERVER, (Class<?>[])new Class[] { String.class, Integer.class }),
        S_REMOVE_RACE_PLAYER(Side.SERVER, (Class<?>[])new Class[] { String.class, Integer.class }),
        S_ADD_RACE_PLAYER(Side.SERVER, (Class<?>[])new Class[] { String.class, Integer.class }),
        S_COMPLETE_CBODY_HANDSHAKE(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_REQUEST_GEAR_DATA(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_REQUEST_ARCLAMP_FACING(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        S_REQUEST_OVERWORLD_IMAGE(Side.SERVER, (Class<?>[])new Class[0]),
        S_REQUEST_MAP_IMAGE(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        S_REQUEST_PLAYERSKIN(Side.SERVER, (Class<?>[])new Class[] { String.class }),
        S_UPDATE_VIEWSCREEN_REQUEST(Side.SERVER, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class }),
        S_BUILDFLAGS_UPDATE(Side.SERVER, (Class<?>[])new Class[] { Integer.class }),
        C_AIR_REMAINING(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, String.class }),
        C_UPDATE_DIMENSION_LIST(Side.CLIENT, (Class<?>[])new Class[] { String.class, String.class }),
        C_SPAWN_SPARK_PARTICLES(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        C_UPDATE_GEAR_SLOT(Side.CLIENT, (Class<?>[])new Class[] { String.class, Integer.class, Integer.class }),
        C_CLOSE_GUI(Side.CLIENT, (Class<?>[])new Class[0]),
        C_RESET_THIRD_PERSON(Side.CLIENT, (Class<?>[])new Class[0]),
        C_UPDATE_SPACESTATION_LIST(Side.CLIENT, (Class<?>[])new Class[] { Integer[].class }),
        C_UPDATE_SPACESTATION_DATA(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, NBTTagCompound.class }),
        C_UPDATE_SPACESTATION_CLIENT_ID(Side.CLIENT, (Class<?>[])new Class[] { String.class }),
        C_UPDATE_PLANETS_LIST(Side.CLIENT, (Class<?>[])new Class[] { Integer[].class }),
        C_UPDATE_CONFIGS(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Double.class, Integer.class, Integer.class, Integer.class, String.class, Float.class, Float.class, Float.class, Float.class, Integer.class, String[].class }),
        C_UPDATE_STATS(Side.CLIENT, (Class<?>[])new Class[] { Integer.class }),
        C_ADD_NEW_SCHEMATIC(Side.CLIENT, (Class<?>[])new Class[] { Integer.class }),
        C_UPDATE_SCHEMATIC_LIST(Side.CLIENT, (Class<?>[])new Class[] { Integer[].class }),
        C_PLAY_SOUND_BOSS_DEATH(Side.CLIENT, (Class<?>[])new Class[0]),
        C_PLAY_SOUND_EXPLODE(Side.CLIENT, (Class<?>[])new Class[0]),
        C_PLAY_SOUND_BOSS_LAUGH(Side.CLIENT, (Class<?>[])new Class[0]),
        C_PLAY_SOUND_BOW(Side.CLIENT, (Class<?>[])new Class[0]),
        C_UPDATE_OXYGEN_VALIDITY(Side.CLIENT, (Class<?>[])new Class[] { Boolean.class }),
        C_OPEN_PARACHEST_GUI(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        C_UPDATE_WIRE_BOUNDS(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class }),
        C_OPEN_SPACE_RACE_GUI(Side.CLIENT, (Class<?>[])new Class[0]),
        C_UPDATE_SPACE_RACE_DATA(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, String.class, FlagData.class, Vector3.class, String[].class }),
        C_OPEN_JOIN_RACE_GUI(Side.CLIENT, (Class<?>[])new Class[] { Integer.class }),
        C_UPDATE_FOOTPRINT_LIST(Side.CLIENT, (Class<?>[])new Class[] { Long.class, Footprint[].class }),
        C_FOOTPRINTS_REMOVED(Side.CLIENT, (Class<?>[])new Class[] { Long.class, BlockVec3.class }),
        C_UPDATE_STATION_SPIN(Side.CLIENT, (Class<?>[])new Class[] { Float.class, Boolean.class }),
        C_UPDATE_STATION_DATA(Side.CLIENT, (Class<?>[])new Class[] { Double.class, Double.class }),
        C_UPDATE_STATION_BOX(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class }),
        C_UPDATE_THERMAL_LEVEL(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Boolean.class }),
        C_DISPLAY_ROCKET_CONTROLS(Side.CLIENT, (Class<?>[])new Class[0]),
        C_GET_CELESTIAL_BODY_LIST(Side.CLIENT, (Class<?>[])new Class[0]),
        C_UPDATE_ENERGYUNITS(Side.CLIENT, (Class<?>[])new Class[] { Integer.class }),
        C_RESPAWN_PLAYER(Side.CLIENT, (Class<?>[])new Class[] { String.class, Integer.class, String.class, Integer.class }),
        C_UPDATE_ARCLAMP_FACING(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class }),
        C_UPDATE_VIEWSCREEN(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class }),
        C_UPDATE_TELEMETRY(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class }),
        C_SEND_PLAYERSKIN(Side.CLIENT, (Class<?>[])new Class[] { String.class, String.class, String.class, String.class }),
        C_SEND_OVERWORLD_IMAGE(Side.CLIENT, (Class<?>[])new Class[] { Integer.class, Integer.class, byte[].class });

        private Side targetSide;
        private Class<?>[] decodeAs;

        private EnumSimplePacket(final Side targetSide, final Class<?>[] decodeAs) {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide() {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses() {
            return this.decodeAs;
        }
    }
}
