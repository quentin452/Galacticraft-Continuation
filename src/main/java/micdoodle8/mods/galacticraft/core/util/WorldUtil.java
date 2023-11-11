package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.item.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.*;
import micdoodle8.mods.galacticraft.core.client.*;
import net.minecraft.client.multiplayer.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.entity.*;
import net.minecraftforge.client.*;
import cpw.mods.fml.common.*;
import net.minecraft.server.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.common.*;
import java.io.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.network.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.potion.*;
import net.minecraft.network.play.server.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.world.chunk.*;
import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import java.util.*;
import com.google.common.collect.*;

public class WorldUtil
{
    public static HashMap<Integer, Integer> registeredSpaceStations;
    public static Map<Integer, String> dimNames;
    public static Map<EntityPlayerMP, HashMap<String, Integer>> celestialMapCache;
    public static List<Integer> registeredPlanets;
    private static IWorldGenerator generatorGCGreg;
    private static IWorldGenerator generatorCoFH;
    private static IWorldGenerator generatorDenseOres;
    private static IWorldGenerator generatorTCAuraNodes;
    private static IWorldGenerator generatorAE2meteors;
    private static Method generateTCAuraNodes;
    private static boolean generatorsInitialised;

    public static double getGravityForEntity(final Entity entity) {
        if (entity.worldObj.provider instanceof IGalacticraftWorldProvider) {
            if (entity instanceof EntityChicken && !OxygenUtil.isAABBInBreathableAirBlock(entity.worldObj, entity.boundingBox)) {
                return 0.08;
            }
            final IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider)entity.worldObj.provider;
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)entity;
                if (player.inventory != null) {
                    int armorModLowGrav = 100;
                    int armorModHighGrav = 100;
                    for (int i = 0; i < 4; ++i) {
                        final ItemStack armorPiece = player.getCurrentArmor(i);
                        if (armorPiece != null && armorPiece.getItem() instanceof IArmorGravity) {
                            armorModLowGrav -= ((IArmorGravity)armorPiece.getItem()).gravityOverrideIfLow(player);
                            armorModHighGrav -= ((IArmorGravity)armorPiece.getItem()).gravityOverrideIfHigh(player);
                        }
                    }
                    if (armorModLowGrav > 100) {
                        armorModLowGrav = 100;
                    }
                    if (armorModHighGrav > 100) {
                        armorModHighGrav = 100;
                    }
                    if (armorModLowGrav < 0) {
                        armorModLowGrav = 0;
                    }
                    if (armorModHighGrav < 0) {
                        armorModHighGrav = 0;
                    }
                    if (customProvider.getGravity() > 0.0f) {
                        return 0.08 - customProvider.getGravity() * armorModLowGrav / 100.0f;
                    }
                    return 0.08 - customProvider.getGravity() * armorModHighGrav / 100.0f;
                }
            }
            return 0.08 - customProvider.getGravity();
        }
        else {
            if (entity instanceof IAntiGrav) {
                return 0.0;
            }
            return 0.08;
        }
    }

    public static float getGravityFactor(final Entity entity) {
        if (entity.worldObj.provider instanceof IGalacticraftWorldProvider) {
            final IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider)entity.worldObj.provider;
            float returnValue = MathHelper.sqrt_float(0.08f / (0.08f - customProvider.getGravity()));
            if (returnValue > 2.5f) {
                returnValue = 2.5f;
            }
            if (returnValue < 0.75f) {
                returnValue = 0.75f;
            }
            return returnValue;
        }
        if (entity instanceof IAntiGrav) {
            return 1.0f;
        }
        return 1.0f;
    }

    public static double getItemGravity(final EntityItem e) {
        if (e.worldObj.provider instanceof IGalacticraftWorldProvider) {
            final IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider)e.worldObj.provider;
            return Math.max(0.002, 0.03999999910593033 - ((customProvider instanceof IOrbitDimension) ? 0.05999999910593033 : customProvider.getGravity()) / 1.75);
        }
        return 0.03999999910593033;
    }

    public static float getArrowGravity(final EntityArrow e) {
        if (e.worldObj.provider instanceof IGalacticraftWorldProvider) {
            return 0.005f;
        }
        return 0.05f;
    }

    public static float getRainStrength(final World world, final float partialTicks) {
        if (world.isRemote && world.provider.getSkyRenderer() instanceof SkyProviderOverworld) {
            return 0.0f;
        }
        return world.prevRainingStrength + (world.rainingStrength - world.prevRainingStrength) * partialTicks;
    }

    public static boolean shouldRenderFire(final Entity entity) {
        if (entity.worldObj == null || !(entity.worldObj.provider instanceof IGalacticraftWorldProvider)) {
            return entity.isBurning();
        }
        if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityArrow) && !(entity instanceof EntityArrowGC)) {
            return entity.isBurning();
        }
        return entity.isBurning() && (!OxygenUtil.noAtmosphericCombustion(entity.worldObj.provider) || OxygenUtil.isAABBInBreathableAirBlock(entity.worldObj, entity.boundingBox));
    }

    public static Vector3 getWorldColor(final World world) {
        return new Vector3(1.0, 1.0, 1.0);
    }

    @SideOnly(Side.CLIENT)
    public static float getWorldBrightness(final WorldClient world) {
        if (world.provider instanceof WorldProviderMoon) {
            final float f1 = world.getCelestialAngle(1.0f);
            float f2 = 1.0f - (MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.2f);
            if (f2 < 0.0f) {
                f2 = 0.0f;
            }
            if (f2 > 1.0f) {
                f2 = 1.0f;
            }
            f2 = 1.0f - f2;
            return f2 * 0.8f;
        }
        return world.getSunBrightness(1.0f);
    }

    public static float getColorRed(final World world) {
        return (float)getWorldColor(world).x;
    }

    public static float getColorGreen(final World world) {
        return (float)getWorldColor(world).y;
    }

    public static float getColorBlue(final World world) {
        return (float)getWorldColor(world).z;
    }

    public static Vec3 getFogColorHook(final World world) {
        final EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
        if (world.provider.getSkyRenderer() instanceof SkyProviderOverworld) {
            float var20 = ((float)player.posY - 200.0f) / 1000.0f;
            var20 = MathHelper.sqrt_float(var20);
            final float var21 = Math.max(1.0f - var20 * 40.0f, 0.0f);
            final Vec3 vec = world.getFogColor(1.0f);
            return Vec3.createVectorHelper(vec.xCoord * Math.max(1.0f - var20 * 1.29f, 0.0f), vec.yCoord * Math.max(1.0f - var20 * 1.29f, 0.0f), vec.zCoord * Math.max(1.0f - var20 * 1.29f, 0.0f));
        }
        return world.getFogColor(1.0f);
    }

    public static Vec3 getSkyColorHook(final World world) {
        final EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
        if (!(world.provider.getSkyRenderer() instanceof SkyProviderOverworld) && (player == null || player.posY <= 130.0 || !(player.ridingEntity instanceof EntitySpaceshipBase))) {
            return world.getSkyColor((Entity)FMLClientHandler.instance().getClient().renderViewEntity, 1.0f);
        }
        final float f1 = world.getCelestialAngle(1.0f);
        float f2 = MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.5f;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        final int i = MathHelper.floor_double(player.posX);
        final int j = MathHelper.floor_double(player.posY);
        final int k = MathHelper.floor_double(player.posZ);
        final int l = ForgeHooksClient.getSkyBlendColour(world, i, j, k);
        float f3 = (l >> 16 & 0xFF) / 255.0f;
        float f4 = (l >> 8 & 0xFF) / 255.0f;
        float f5 = (l & 0xFF) / 255.0f;
        f3 *= f2;
        f4 *= f2;
        f5 *= f2;
        if (player.posY <= 200.0) {
            final Vec3 vec = world.getSkyColor((Entity)FMLClientHandler.instance().getClient().renderViewEntity, 1.0f);
            final double blend = (player.posY - 130.0) / 70.0;
            final double ablend = 1.0 - blend;
            return Vec3.createVectorHelper(f3 * blend + vec.xCoord * ablend, f4 * blend + vec.yCoord * ablend, f5 * blend + vec.zCoord * ablend);
        }
        double blend2 = Math.min(1.0, (player.posY - 200.0) / 300.0);
        final double ablend2 = 1.0 - blend2;
        blend2 /= 255.0;
        return Vec3.createVectorHelper(f3 * ablend2 + blend2 * 31.0, f4 * ablend2 + blend2 * 8.0, f5 * ablend2 + blend2 * 99.0);
    }

    public static WorldProvider getProviderForNameServer(final String par1String) {
        String nameToFind = par1String;
        if (par1String.contains("$")) {
            final String[] twoDimensions = par1String.split("\\$");
            nameToFind = twoDimensions[0];
        }
        if (nameToFind == null) {
            return null;
        }
        for (final Map.Entry<Integer, String> element : WorldUtil.dimNames.entrySet()) {
            if (nameToFind.equals(element.getValue())) {
                return getProviderForDimensionServer(element.getKey());
            }
        }
        GCLog.info("Failed to find matching world for '" + par1String + "'");
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static WorldProvider getProviderForNameClient(final String par1String) {
        String nameToFind = par1String;
        if (par1String.contains("$")) {
            final String[] twoDimensions = par1String.split("\\$");
            nameToFind = twoDimensions[0];
        }
        if (nameToFind == null) {
            return null;
        }
        for (final Map.Entry<Integer, String> element : WorldUtil.dimNames.entrySet()) {
            if (nameToFind.equals(element.getValue())) {
                return getProviderForDimensionClient(element.getKey());
            }
        }
        GCLog.info("Failed to find matching world for '" + par1String + "'");
        return null;
    }

    public static void initialiseDimensionNames() {
        final WorldProvider provider = getProviderForDimensionServer(ConfigManagerCore.idDimensionOverworld);
        WorldUtil.dimNames.put(ConfigManagerCore.idDimensionOverworld, new String(provider.getDimensionName()));
    }

    public static List<Integer> getPossibleDimensionsForSpaceshipTier(final int tier, final EntityPlayerMP playerBase) {
        final List<Integer> temp = new ArrayList<Integer>();
        if (!ConfigManagerCore.disableRocketsToOverworld) {
            temp.add(ConfigManagerCore.idDimensionOverworld);
        }
        for (final Integer element : WorldUtil.registeredPlanets) {
            if (element == ConfigManagerCore.idDimensionOverworld) {
                continue;
            }
            final WorldProvider provider = getProviderForDimensionServer(element);
            if (provider == null) {
                continue;
            }
            if (provider instanceof IGalacticraftWorldProvider) {
                if (!((IGalacticraftWorldProvider)provider).canSpaceshipTierPass(tier)) {
                    continue;
                }
                temp.add(element);
            }
            else {
                temp.add(element);
            }
        }
        for (final Integer element : WorldUtil.registeredSpaceStations.keySet()) {
            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, (int)element, (EntityPlayer)null);
            if (!ConfigManagerCore.spaceStationsRequirePermission || data.getAllowedAll() || data.getAllowedPlayers().contains(playerBase.getGameProfile().getName()) || VersionUtil.isPlayerOpped(playerBase)) {
                if (playerBase != null) {
                    final int currentWorld = playerBase.dimension;
                    if (currentWorld == data.getHomePlanet()) {
                        temp.add(element);
                        continue;
                    }
                    if (playerBase.worldObj.provider instanceof IOrbitDimension) {
                        final SpaceStationWorldData dataCurrent = SpaceStationWorldData.getStationData(playerBase.worldObj, playerBase.dimension, (EntityPlayer)null);
                        if (dataCurrent.getHomePlanet() == data.getHomePlanet()) {
                            temp.add(element);
                            continue;
                        }
                    }
                }
                final WorldProvider homeWorld = getProviderForDimensionServer(data.getHomePlanet());
                if (homeWorld == null) {
                    continue;
                }
                if (homeWorld instanceof IGalacticraftWorldProvider) {
                    if (!((IGalacticraftWorldProvider)homeWorld).canSpaceshipTierPass(tier)) {
                        continue;
                    }
                    temp.add(element);
                }
                else {
                    temp.add(element);
                }
            }
        }
        return temp;
    }

    public static CelestialBody getReachableCelestialBodiesForDimensionID(final int id) {
        final List<CelestialBody> celestialBodyList = Lists.newArrayList();
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredSatellites().values());
        for (final CelestialBody cBody : celestialBodyList) {
            if (cBody.getReachable() && cBody.getDimensionID() == id) {
                return cBody;
            }
        }
        return null;
    }

    public static CelestialBody getReachableCelestialBodiesForName(final String name) {
        final List<CelestialBody> celestialBodyList = Lists.newArrayList();
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredSatellites().values());
        for (final CelestialBody cBody : celestialBodyList) {
            if (cBody.getReachable() && cBody.getName().equals(name)) {
                return cBody;
            }
        }
        return null;
    }

    public static WorldProvider getProviderForDimensionServer(final int id) {
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer == null) {
            GCLog.debug("Called WorldUtil server side method but FML returned no server - is this a bug?");
            return null;
        }
        final World ws = (World)theServer.worldServerForDimension(id);
        if (ws != null) {
            return ws.provider;
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static WorldProvider getProviderForDimensionClient(final int id) {
        final World ws = (World)ClientProxyCore.mc.theWorld;
        if (ws != null && ws.provider.dimensionId == id) {
            return ws.provider;
        }
        return WorldProvider.getProviderForDimension(id);
    }

    public static HashMap<String, Integer> getArrayOfPossibleDimensions(final int tier, final EntityPlayerMP playerBase) {
        final List<Integer> ids = getPossibleDimensionsForSpaceshipTier(tier, playerBase);
        final HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (final Integer id : ids) {
            CelestialBody celestialBody = getReachableCelestialBodiesForDimensionID(id);
            if (id > 0 && celestialBody == null) {
                celestialBody = (CelestialBody)GalacticraftCore.satelliteSpaceStation;
                if (playerBase == null) {
                    continue;
                }
                final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, (int)id, (EntityPlayer)null);
                map.put(celestialBody.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + id + "$" + data.getHomePlanet(), id);
            }
            else if (celestialBody == GalacticraftCore.planetOverworld) {
                map.put(celestialBody.getName(), id);
            }
            else {
                final WorldProvider provider = getProviderForDimensionServer(id);
                if (celestialBody == null || provider == null || ((provider instanceof IGalacticraftWorldProvider || provider instanceof IOrbitDimension) && provider.dimensionId != 0)) {
                    continue;
                }
                map.put(celestialBody.getName(), provider.dimensionId);
            }
        }
        final ArrayList<CelestialBody> cBodyList = new ArrayList<CelestialBody>();
        cBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        cBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        for (final CelestialBody body : cBodyList) {
            if (!body.getReachable()) {
                map.put(body.getLocalizedName() + "*", body.getDimensionID());
            }
        }
        WorldUtil.celestialMapCache.put(playerBase, map);
        return map;
    }

    public static HashMap<String, Integer> getArrayOfPossibleDimensionsAgain(final int tier, final EntityPlayerMP playerBase) {
        final HashMap<String, Integer> map = WorldUtil.celestialMapCache.get(playerBase);
        if (map != null) {
            return map;
        }
        return getArrayOfPossibleDimensions(tier, playerBase);
    }

    private static List<Integer> getExistingSpaceStationList(final File var0) {
        final ArrayList<Integer> var = new ArrayList<Integer>();
        final File[] var2 = var0.listFiles();
        if (var2 != null) {
            for (final File var3 : var2) {
                if (var3.getName().contains("spacestation_")) {
                    String var4 = var3.getName();
                    var4 = var4.substring(13, var4.length() - 4);
                    var.add(Integer.parseInt(var4));
                }
            }
        }
        return var;
    }

    public static void unregisterSpaceStations() {
        if (WorldUtil.registeredSpaceStations != null) {
            for (final Integer registeredID : WorldUtil.registeredSpaceStations.keySet()) {
                DimensionManager.unregisterDimension((int)registeredID);
            }
            WorldUtil.registeredSpaceStations = null;
        }
    }

    public static void registerSpaceStations(final File spaceStationList) {
        WorldUtil.registeredSpaceStations = Maps.newHashMap();
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer == null) {
            return;
        }
        final File[] var2 = spaceStationList.listFiles();
        if (var2 != null) {
            for (final File var3 : var2) {
                if (var3.getName().contains("spacestation_")) {
                    try {
                        String name = var3.getName();
                        final SpaceStationWorldData worldDataTemp = new SpaceStationWorldData(name);
                        name = name.substring(13, name.length() - 4);
                        final int registeredID = Integer.parseInt(name);
                        final FileInputStream fileinputstream = new FileInputStream(var3);
                        final NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed((InputStream)fileinputstream);
                        fileinputstream.close();
                        worldDataTemp.readFromNBT(nbttagcompound.getCompoundTag("data"));
                        final int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, registeredID);
                        if (!DimensionManager.isDimensionRegistered(registeredID)) {
                            if (id >= 0) {
                                DimensionManager.registerDimension(registeredID, worldDataTemp.getDimensionIdStatic());
                                WorldUtil.registeredSpaceStations.put(registeredID, worldDataTemp.getDimensionIdStatic());
                                theServer.worldServerForDimension(registeredID);
                            }
                            else {
                                DimensionManager.registerDimension(registeredID, worldDataTemp.getDimensionIdDynamic());
                                WorldUtil.registeredSpaceStations.put(registeredID, worldDataTemp.getDimensionIdDynamic());
                            }
                            WorldUtil.dimNames.put(registeredID, "Space Station " + registeredID);
                        }
                        else {
                            GCLog.severe("Dimension already registered to another mod: unable to register space station dimension " + registeredID);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean registerPlanet(final int planetID, final boolean initialiseDimensionAtServerInit, final int defaultID) {
        if (WorldUtil.registeredPlanets == null) {
            WorldUtil.registeredPlanets = new ArrayList<Integer>();
        }
        if (!initialiseDimensionAtServerInit) {
            WorldUtil.registeredPlanets.add(planetID);
            return true;
        }
        if (!DimensionManager.isDimensionRegistered(planetID)) {
            DimensionManager.registerDimension(planetID, planetID);
            GCLog.info("Registered Dimension: " + planetID);
            WorldUtil.registeredPlanets.add(planetID);
            final World w = (World)FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(planetID);
            WorldUtil.dimNames.put(planetID, new String(getDimensionName(w.provider)));
            return true;
        }
        GCLog.severe("Dimension already registered to another mod: unable to register planet dimension " + planetID);
        WorldUtil.registeredPlanets.add(defaultID);
        return false;
    }

    public static void unregisterPlanets() {
        if (WorldUtil.registeredPlanets != null) {
            for (final Integer var1 : WorldUtil.registeredPlanets) {
                DimensionManager.unregisterDimension((int)var1);
                GCLog.info("Unregistered Dimension: " + var1);
            }
            WorldUtil.registeredPlanets = null;
        }
        WorldUtil.dimNames.clear();
    }

    @Deprecated
    public static void registerPlanet(final int planetID, final boolean initialiseDimensionAtServerInit) {
        registerPlanet(planetID, initialiseDimensionAtServerInit, 0);
    }

    public static void registerPlanetClient(final Integer dimID, final int providerIndex) {
        final int providerID = GalacticraftRegistry.getProviderID(providerIndex);
        if (providerID == 0) {
            GCLog.severe("Server dimension " + dimID + " has no match on client due to earlier registration problem.");
        }
        else if (dimID == 0) {
            GCLog.severe("Client dimension " + providerID + " has no match on server - probably a server dimension ID conflict problem.");
        }
        else if (!WorldUtil.registeredPlanets.contains(dimID)) {
            WorldUtil.registeredPlanets.add(dimID);
            DimensionManager.registerDimension((int)dimID, providerID);
        }
        else {
            GCLog.severe("Dimension already registered to another mod: unable to register planet dimension " + dimID);
        }
    }

    public static Integer[] getArrayOfPossibleDimensions() {
        final ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(ConfigManagerCore.idDimensionOverworld);
        for (final Integer i : WorldUtil.registeredPlanets) {
            temp.add(i);
        }
        if (WorldUtil.registeredSpaceStations != null) {
            for (final Integer i : WorldUtil.registeredSpaceStations.keySet()) {
                temp.add(i);
            }
        }
        final Integer[] finalArray = new Integer[temp.size()];
        int count = 0;
        for (final Integer integ : temp) {
            finalArray[count++] = integ;
        }
        return finalArray;
    }

    public static SpaceStationWorldData bindSpaceStationToNewDimension(final World world, final EntityPlayerMP player, final int homePlanetID) {
        int dynamicProviderID = -1;
        int staticProviderID = -1;
        for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
            if (satellite.getParentPlanet().getDimensionID() == homePlanetID) {
                dynamicProviderID = satellite.getDimensionID();
                staticProviderID = satellite.getDimensionIdStatic();
            }
        }
        if (dynamicProviderID == -1 || staticProviderID == -1) {
            throw new RuntimeException("Space station being bound on bad provider IDs!");
        }
        final int newID = DimensionManager.getNextFreeDimId();
        final SpaceStationWorldData data = createSpaceStation(world, newID, homePlanetID, dynamicProviderID, staticProviderID, player);
        WorldUtil.dimNames.put(newID, "Space Station " + newID);
        final GCPlayerStats stats = GCPlayerStats.get(player);
        stats.spaceStationDimensionData.put(homePlanetID, newID);
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID, new Object[] { spaceStationDataToString(stats.spaceStationDimensionData) }), player);
        return data;
    }

    public static SpaceStationWorldData createSpaceStation(final World world, final int dimID, final int homePlanetID, final int dynamicProviderID, final int staticProviderID, final EntityPlayerMP player) {
        if (!DimensionManager.isDimensionRegistered(dimID)) {
            if (ConfigManagerCore.keepLoadedNewSpaceStations) {
                ConfigManagerCore.setLoaded(dimID);
            }
            final int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, dimID);
            if (id >= 0) {
                DimensionManager.registerDimension(dimID, staticProviderID);
                WorldUtil.registeredSpaceStations.put(dimID, staticProviderID);
            }
            else {
                DimensionManager.registerDimension(dimID, dynamicProviderID);
                WorldUtil.registeredSpaceStations.put(dimID, dynamicProviderID);
            }
        }
        else {
            GCLog.severe("Dimension already registered to another mod: unable to register space station dimension " + dimID);
        }
        GalacticraftCore.packetPipeline.sendToAll((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_LIST, (List)getSpaceStationList()));
        return SpaceStationWorldData.getStationData(world, dimID, homePlanetID, dynamicProviderID, staticProviderID, (EntityPlayer)player);
    }

    public static Entity transferEntityToDimension(final Entity entity, final int dimensionID, final WorldServer world) {
        return transferEntityToDimension(entity, dimensionID, world, true, null);
    }

    public static Entity transferEntityToDimension(final Entity entity, final int dimensionID, final WorldServer world, final boolean transferInv, final EntityAutoRocket ridingRocket) {
        if (!world.isRemote) {
            final MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (mcServer != null) {
                final WorldServer var6 = mcServer.worldServerForDimension(dimensionID);
                if (var6 == null) {
                    System.err.println("Cannot Transfer Entity to Dimension: Could not get World for Dimension " + dimensionID);
                    return null;
                }
                final ITeleportType type = GalacticraftRegistry.getTeleportTypeForDimension((Class)var6.provider.getClass());
                if (type != null) {
                    return teleportEntity((World)var6, entity, dimensionID, type, transferInv, ridingRocket);
                }
            }
        }
        return null;
    }

    private static Entity teleportEntity(final World worldNew, Entity entity, final int dimID, final ITeleportType type, final boolean transferInv, EntityAutoRocket ridingRocket) {
        Entity otherRiddenEntity = null;
        if (entity.ridingEntity != null) {
            if (entity.ridingEntity instanceof EntitySpaceshipBase) {
                entity.mountEntity(entity.ridingEntity);
            }
            else if (entity.ridingEntity instanceof EntityCelestialFake) {
                entity.ridingEntity.setDead();
                entity.mountEntity((Entity)null);
            }
            else {
                otherRiddenEntity = entity.ridingEntity;
                entity.mountEntity((Entity)null);
            }
        }
        final boolean dimChange = entity.worldObj != worldNew;
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        EntityPlayerMP player = null;
        Vector3 spawnPos = null;
        final int oldDimID = entity.worldObj.provider.dimensionId;
        if (ridingRocket != null) {
            final ArrayList<TileEntityTelemetry> tList = (ArrayList<TileEntityTelemetry>)ridingRocket.getTelemetry();
            final NBTTagCompound nbt = new NBTTagCompound();
            ridingRocket.isDead = false;
            ridingRocket.riddenByEntity = null;
            ridingRocket.writeToNBTOptional(nbt);
            removeEntityFromWorld(ridingRocket.worldObj, (Entity)ridingRocket, true);
            ridingRocket = (EntityAutoRocket)EntityList.createEntityFromNBT(nbt, worldNew);
            if (ridingRocket != null) {
                ridingRocket.setWaitForPlayer(true);
                if (ridingRocket instanceof IWorldTransferCallback) {
                    ((IWorldTransferCallback)ridingRocket).onWorldTransferred(worldNew);
                }
            }
        }
        if (dimChange) {
            if (entity instanceof EntityPlayerMP) {
                player = (EntityPlayerMP)entity;
                final World worldOld = player.worldObj;
                if (ConfigManagerCore.enableDebug) {
                    try {
                        GCLog.info("DEBUG: Attempting to remove player from old dimension " + oldDimID);
                        ((WorldServer)worldOld).getPlayerManager().removePlayer(player);
                        GCLog.info("DEBUG: Successfully removed player from old dimension " + oldDimID);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        ((WorldServer)worldOld).getPlayerManager().removePlayer(player);
                    }
                    catch (Exception ex) {}
                }
                final GCPlayerStats stats = GCPlayerStats.get(player);
                stats.usingPlanetSelectionGui = false;
                player.dimension = dimID;
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("DEBUG: Sending respawn packet to player for dim " + dimID);
                }
                player.playerNetServerHandler.sendPacket((Packet)new S07PacketRespawn(dimID, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                if (worldNew.provider instanceof WorldProviderOrbit && WorldUtil.registeredSpaceStations.containsKey(dimID)) {
                    final NBTTagCompound var2 = new NBTTagCompound();
                    SpaceStationWorldData.getStationData(worldNew, dimID, (EntityPlayer)player).writeToNBT(var2);
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_DATA, new Object[] { dimID, var2 }), player);
                }
                removeEntityFromWorld(worldOld, (Entity)player, true);
                if (worldNew.provider instanceof WorldProviderSpaceStation) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[0]), player);
                }
                if (ridingRocket != null) {
                    spawnPos = new Vector3((Entity)ridingRocket);
                }
                else {
                    spawnPos = type.getPlayerSpawnLocation((WorldServer)worldNew, player);
                }
                forceMoveEntityToPos(entity, (WorldServer)worldNew, spawnPos, true);
                player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer)worldNew);
                GCLog.info("Server attempting to transfer player " + player.getGameProfile().getName() + " to dimension " + worldNew.provider.dimensionId);
                player.theItemInWorldManager.setWorld((WorldServer)worldNew);
                player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer)worldNew);
                player.mcServer.getConfigurationManager().syncPlayerInventory(player);
                for (final Object o : player.getActivePotionEffects()) {
                    final PotionEffect var3 = (PotionEffect)o;
                    player.playerNetServerHandler.sendPacket((Packet)new S1DPacketEntityEffect(player.getEntityId(), var3));
                }
                player.playerNetServerHandler.sendPacket((Packet)new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
            }
            else {
                ArrayList<TileEntityTelemetry> tList = null;
                if (entity instanceof EntitySpaceshipBase) {
                    tList = (ArrayList<TileEntityTelemetry>)((EntitySpaceshipBase)entity).getTelemetry();
                }
                removeEntityFromWorld(entity.worldObj, entity, true);
                final NBTTagCompound nbt = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(nbt);
                entity = EntityList.createEntityFromNBT(nbt, worldNew);
                if (entity == null) {
                    return null;
                }
                if (entity instanceof IWorldTransferCallback) {
                    ((IWorldTransferCallback)entity).onWorldTransferred(worldNew);
                }
                forceMoveEntityToPos(entity, (WorldServer)worldNew, new Vector3(entity), true);
                if (tList != null && tList.size() > 0) {
                    for (final TileEntityTelemetry t : tList) {
                        t.addTrackedEntity(entity);
                    }
                }
            }
        }
        else if (entity instanceof EntityPlayerMP) {
            player = (EntityPlayerMP)entity;
            player.closeScreen();
            final GCPlayerStats stats2 = GCPlayerStats.get(player);
            stats2.usingPlanetSelectionGui = false;
            if (worldNew.provider instanceof WorldProviderSpaceStation) {
                GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[0]), player);
            }
            if (ridingRocket != null) {
                spawnPos = new Vector3((Entity)ridingRocket);
            }
            else {
                spawnPos = type.getPlayerSpawnLocation((WorldServer)entity.worldObj, (EntityPlayerMP)entity);
            }
            forceMoveEntityToPos(entity, (WorldServer)worldNew, spawnPos, false);
            GCLog.info("Server attempting to transfer player " + player.getGameProfile().getName() + " within same dimension " + worldNew.provider.dimensionId);
        }
        if (player != null) {
            final GCPlayerStats playerStats = GCPlayerStats.get(player);
            if (ridingRocket == null && type.useParachute() && playerStats.extendedInventory.getStackInSlot(4) != null && playerStats.extendedInventory.getStackInSlot(4).getItem() instanceof ItemParaChute) {
                GCPlayerHandler.setUsingParachute(player, playerStats, true);
            }
            else {
                GCPlayerHandler.setUsingParachute(player, playerStats, false);
            }
            if (playerStats.rocketStacks != null && playerStats.rocketStacks.length > 0) {
                for (int stack = 0; stack < playerStats.rocketStacks.length; ++stack) {
                    if (transferInv) {
                        if (playerStats.rocketStacks[stack] == null) {
                            if (stack == playerStats.rocketStacks.length - 1) {
                                if (playerStats.rocketItem != null) {
                                    playerStats.rocketStacks[stack] = new ItemStack(playerStats.rocketItem, 1, playerStats.rocketType);
                                }
                            }
                            else if (stack == playerStats.rocketStacks.length - 2) {
                                playerStats.rocketStacks[stack] = playerStats.launchpadStack;
                                playerStats.launchpadStack = null;
                            }
                        }
                    }
                    else {
                        playerStats.rocketStacks[stack] = null;
                    }
                }
            }
            if (transferInv && playerStats.chestSpawnCooldown == 0) {
                playerStats.chestSpawnVector = type.getParaChestSpawnLocation((WorldServer)entity.worldObj, player, new Random());
                playerStats.chestSpawnCooldown = 200;
            }
        }
        if (ridingRocket != null) {
            worldNew.spawnEntityInWorld((Entity)ridingRocket);
            ridingRocket.setWorld(worldNew);
            worldNew.updateEntityWithOptionalForce((Entity)ridingRocket, true);
            entity.mountEntity((Entity)ridingRocket);
            GCLog.debug("Entering rocket at : " + entity.posX + "," + entity.posZ + " rocket at: " + ridingRocket.posX + "," + ridingRocket.posZ);
        }
        else if (otherRiddenEntity != null) {
            if (dimChange) {
                final World worldOld = otherRiddenEntity.worldObj;
                final NBTTagCompound nbt = new NBTTagCompound();
                otherRiddenEntity.writeToNBTOptional(nbt);
                removeEntityFromWorld(worldOld, otherRiddenEntity, true);
                otherRiddenEntity = EntityList.createEntityFromNBT(nbt, worldNew);
                worldNew.spawnEntityInWorld(otherRiddenEntity);
                otherRiddenEntity.setWorld(worldNew);
            }
            otherRiddenEntity.setPositionAndRotation(entity.posX, entity.posY - 10.0, entity.posZ, otherRiddenEntity.rotationYaw, otherRiddenEntity.rotationPitch);
            worldNew.updateEntityWithOptionalForce(otherRiddenEntity, true);
        }
        if (entity instanceof EntityPlayerMP) {
            if (dimChange) {
                FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayer)entity, oldDimID, dimID);
            }
            type.onSpaceDimensionChanged(worldNew, (EntityPlayerMP)entity, ridingRocket != null);
        }
        return entity;
    }

    public static void forceMoveEntityToPos(final Entity entity, final WorldServer worldNew, final Vector3 spawnPos, final boolean spawnRequired) {
        final ChunkCoordIntPair pair = worldNew.getChunkFromChunkCoords(spawnPos.intX() >> 4, spawnPos.intZ() >> 4).getChunkCoordIntPair();
        GCLog.debug("Loading first chunk in new dimension at " + pair.chunkXPos + "," + pair.chunkZPos);
        worldNew.theChunkProviderServer.loadChunk(pair.chunkXPos, pair.chunkZPos);
        if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)entity).playerNetServerHandler.setPlayerLocation(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
        }
        entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
        if (spawnRequired) {
            worldNew.spawnEntityInWorld(entity);
            entity.setWorld((World)worldNew);
        }
        worldNew.updateEntityWithOptionalForce(entity, true);
    }

    public static WorldServer getStartWorld(final WorldServer worldOld) {
        if (ConfigManagerCore.challengeSpawnHandling) {
            final WorldProvider wp = getProviderForNameServer("planet.asteroids");
            final WorldServer worldNew = (wp == null) ? null : ((WorldServer)wp.worldObj);
            if (worldNew != null) {
                return worldNew;
            }
        }
        return worldOld;
    }

    @SideOnly(Side.CLIENT)
    public static EntityPlayer forceRespawnClient(final int dimID, final int par2, final String par3, final int par4) {
        final S07PacketRespawn fakePacket = new S07PacketRespawn(dimID, EnumDifficulty.getDifficultyEnum(par2), WorldType.parseWorldType(par3), WorldSettings.GameType.getByID(par4));
        Minecraft.getMinecraft().getNetHandler().handleRespawn(fakePacket);
        return (EntityPlayer)FMLClientHandler.instance().getClientPlayerEntity();
    }

    private static void removeEntityFromWorld(final World var0, final Entity var1, final boolean directlyRemove) {
        if (var1 instanceof EntityPlayer) {
            final EntityPlayer var2 = (EntityPlayer)var1;
            var2.closeScreen();
            var0.playerEntities.remove(var2);
            var0.updateAllPlayersSleepingFlag();
        }
        if (directlyRemove) {
            final List l = new ArrayList();
            l.add(var1);
            var0.unloadEntities(l);
        }
        var1.isDead = false;
    }

    public static SpaceStationRecipe getSpaceStationRecipe(final int planetID) {
        for (final SpaceStationType type : GalacticraftRegistry.getSpaceStationData()) {
            if (type.getWorldToOrbitID() == planetID) {
                return type.getRecipeForSpaceStation();
            }
        }
        return null;
    }

    public static List<Object> getPlanetList() {
        final List<Object> objList = new ArrayList<Object>();
        objList.add(getPlanetListInts());
        return objList;
    }

    public static Integer[] getPlanetListInts() {
        final Integer[] iArray = new Integer[WorldUtil.registeredPlanets.size()];
        for (int i = 0; i < iArray.length; ++i) {
            iArray[i] = WorldUtil.registeredPlanets.get(i);
        }
        return iArray;
    }

    public static void decodePlanetsListClient(final List<Object> data) {
        try {
            if (ConfigManagerCore.enableDebug) {
                GCLog.info("GC connecting to server: received planets dimension ID list.");
            }
            if (WorldUtil.registeredPlanets != null) {
                for (final Integer registeredID : WorldUtil.registeredPlanets) {
                    DimensionManager.unregisterDimension((int)registeredID);
                }
            }
            WorldUtil.registeredPlanets = new ArrayList<Integer>();
            String ids = "";
            if (data.size() > 0) {
                int providerIndex = GalaxyRegistry.getRegisteredSatellites().size() * 2;
                if (data.get(0) instanceof Integer) {
                    for (final Object o : data) {
                        registerPlanetClient((Integer)o, providerIndex);
                        ++providerIndex;
                        ids = ids + ((Integer)o).toString() + " ";
                    }
                }
                else if (data.get(0) instanceof Integer[]) {
                    Integer[] integers = (Integer[]) data.get(0);
                    for (final Integer o2 : integers) {
                        registerPlanetClient(o2, providerIndex);
                        ++providerIndex;
                        ids = ids + o2.toString() + " ";
                    }
                }
            }
            if (ConfigManagerCore.enableDebug) {
                GCLog.debug("GC clientside planet dimensions registered: " + ids);
                final WorldProvider dimMoon = getProviderForNameClient("moon.moon");
                if (dimMoon != null) {
                    GCLog.debug("Crosscheck: Moon is " + dimMoon.dimensionId);
                }
                final WorldProvider dimMars = getProviderForNameClient("planet.mars");
                if (dimMars != null) {
                    GCLog.debug("Crosscheck: Mars is " + dimMars.dimensionId);
                }
                final WorldProvider dimAst = getProviderForNameClient("planet.asteroids");
                if (dimAst != null) {
                    GCLog.debug("Crosscheck: Asteroids is " + dimAst.dimensionId);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Object> getSpaceStationList() {
        final List<Object> objList = new ArrayList<Object>();
        objList.add(getSpaceStationListInts());
        return objList;
    }

    public static Integer[] getSpaceStationListInts() {
        final Integer[] iArray = new Integer[WorldUtil.registeredSpaceStations.size() * 2];
        int i = 0;
        for (final Map.Entry<Integer, Integer> e : WorldUtil.registeredSpaceStations.entrySet()) {
            iArray[i] = e.getKey();
            iArray[i + 1] = e.getValue();
            i += 2;
        }
        return iArray;
    }

    public static void decodeSpaceStationListClient(final List<Object> data) {
        try {
            if (WorldUtil.registeredSpaceStations != null) {
                for (final Integer registeredID : WorldUtil.registeredSpaceStations.keySet()) {
                    DimensionManager.unregisterDimension((int)registeredID);
                }
            }
            WorldUtil.registeredSpaceStations = Maps.newHashMap();
            if (data.size() > 0) {
                if (data.get(0) instanceof Integer) {
                    for (int i = 0; i < data.size(); i += 2) {
                        registerSSdim((Integer) data.get(i), (Integer) data.get(i + 1));
                    }
                }
                else if (data.get(0) instanceof Integer[]) {
                    final Integer[] array = (Integer[]) data.get(0);
                    for (int j = 0; j < array.length; j += 2) {
                        registerSSdim(array[j], array[j + 1]);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerSSdim(final Integer dimID, final Integer providerKey) {
        if (!WorldUtil.registeredSpaceStations.containsKey(dimID)) {
            if (!DimensionManager.isDimensionRegistered((int)dimID)) {
                WorldUtil.registeredSpaceStations.put(dimID, providerKey);
                DimensionManager.registerDimension((int)dimID, (int)providerKey);
            }
            else {
                GCLog.severe("Dimension already registered on client: unable to register space station dimension " + dimID);
            }
        }
    }

    public static boolean otherModPreventGenerate(final int chunkX, final int chunkZ, final World world, final IChunkProvider chunkGenerator, final IChunkProvider chunkProvider) {
        if (!(world.provider instanceof IGalacticraftWorldProvider)) {
            return false;
        }
        if (world.provider instanceof WorldProviderSpaceStation) {
            return true;
        }
        if (ConfigManagerCore.enableOtherModsFeatures) {
            return false;
        }
        if (!WorldUtil.generatorsInitialised) {
            WorldUtil.generatorsInitialised = true;
            try {
                final Class GCGreg = Class.forName("bloodasp.galacticgreg.GT_Worldgenerator_Space");
                if (GCGreg != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry").getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>)regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (GCGreg.isInstance(gen)) {
                            WorldUtil.generatorGCGreg = gen;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex) {}
            try {
                final Class cofh = Class.forName("cofh.core.world.WorldHandler");
                if (cofh != null && ConfigManagerCore.whitelistCoFHCoreGen) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry").getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>)regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (cofh.isInstance(gen)) {
                            WorldUtil.generatorCoFH = gen;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex2) {}
            try {
                final Class denseOres = Class.forName("com.rwtema.denseores.WorldGenOres");
                if (denseOres != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry").getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>)regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (denseOres.isInstance(gen)) {
                            WorldUtil.generatorDenseOres = gen;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex3) {}
            try {
                Class ae2meteorPlace = null;
                try {
                    ae2meteorPlace = Class.forName("appeng.hooks.MeteoriteWorldGen");
                }
                catch (ClassNotFoundException ex4) {}
                if (ae2meteorPlace == null) {
                    try {
                        ae2meteorPlace = Class.forName("appeng.worldgen.MeteoriteWorldGen");
                    }
                    catch (ClassNotFoundException ex5) {}
                }
                if (ae2meteorPlace != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry").getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>)regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (ae2meteorPlace.isInstance(gen)) {
                            WorldUtil.generatorAE2meteors = gen;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex6) {}
            try {
                final Class genThaumCraft = Class.forName("thaumcraft.common.lib.world.ThaumcraftWorldGenerator");
                if (genThaumCraft != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry").getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>)regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (genThaumCraft.isInstance(gen)) {
                            WorldUtil.generatorTCAuraNodes = gen;
                            break;
                        }
                    }
                    if (WorldUtil.generatorTCAuraNodes != null && ConfigManagerCore.enableThaumCraftNodes) {
                        (WorldUtil.generateTCAuraNodes = genThaumCraft.getDeclaredMethod("generateWildNodes", World.class, Random.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE)).setAccessible(true);
                    }
                }
            }
            catch (Exception ex7) {}
            if (WorldUtil.generatorGCGreg != null) {
                System.out.println("Whitelisting GalacticGreg oregen on planets.");
            }
            if (WorldUtil.generatorCoFH != null) {
                System.out.println("Whitelisting CoFHCore custom oregen on planets.");
            }
            if (WorldUtil.generatorDenseOres != null) {
                System.out.println("Whitelisting Dense Ores oregen on planets.");
            }
            if (WorldUtil.generatorAE2meteors != null) {
                System.out.println("Whitelisting AE2 meteorites worldgen on planets.");
            }
            if (WorldUtil.generatorTCAuraNodes != null && WorldUtil.generateTCAuraNodes != null) {
                System.out.println("Whitelisting ThaumCraft aura node generation on planets.");
            }
        }
        if (WorldUtil.generatorGCGreg == null && WorldUtil.generatorCoFH == null && WorldUtil.generatorDenseOres == null && WorldUtil.generatorTCAuraNodes == null) {
            if (WorldUtil.generatorAE2meteors == null) {
                return true;
            }
        }
        try {
            final long worldSeed = world.getSeed();
            final Random fmlRandom = new Random(worldSeed);
            final long xSeed = fmlRandom.nextLong() >> 3;
            final long zSeed = fmlRandom.nextLong() >> 3;
            final long chunkSeed = xSeed * chunkX + zSeed * chunkZ ^ worldSeed;
            fmlRandom.setSeed(chunkSeed);
            if (WorldUtil.generatorCoFH != null) {
                WorldUtil.generatorCoFH.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            }
            if (WorldUtil.generatorDenseOres != null) {
                WorldUtil.generatorDenseOres.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            }
            if (WorldUtil.generatorGCGreg != null) {
                WorldUtil.generatorGCGreg.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            }
            if (WorldUtil.generatorAE2meteors != null) {
                WorldUtil.generatorAE2meteors.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            }
            if (WorldUtil.generateTCAuraNodes != null) {
                WorldUtil.generateTCAuraNodes.invoke(WorldUtil.generatorTCAuraNodes, world, fmlRandom, chunkX, chunkZ, false, true);
            }
        }
        catch (Exception e) {
            GCLog.severe("Error in another mod's worldgen.  This is NOT a Galacticraft bug.");
            e.printStackTrace();
        }
        return true;
    }

    public static void toCelestialSelection(final EntityPlayerMP player, final GCPlayerStats stats, final int tier) {
        player.mountEntity((Entity)null);
        stats.spaceshipTier = tier;
        final HashMap<String, Integer> map = getArrayOfPossibleDimensions(tier, player);
        String dimensionList = "";
        int count = 0;
        for (final Map.Entry<String, Integer> entry : map.entrySet()) {
            dimensionList = dimensionList.concat(entry.getKey() + ((count < map.entrySet().size() - 1) ? "?" : ""));
            ++count;
        }
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_DIMENSION_LIST, new Object[] { player.getGameProfile().getName(), dimensionList }), player);
        stats.usingPlanetSelectionGui = true;
        stats.savedPlanetList = new String(dimensionList);
        final Entity fakeEntity = (Entity)new EntityCelestialFake(player.worldObj, player.posX, player.posY, player.posZ, 0.0f);
        player.worldObj.spawnEntityInWorld(fakeEntity);
        player.mountEntity(fakeEntity);
    }

    public static Vector3 getFootprintPosition(final World world, final float rotation, final Vector3 startPosition, final BlockVec3 playerCenter) {
        final Vector3 position = startPosition.clone();
        final float footprintScale = 0.375f;
        int mainPosX = position.intX();
        final int mainPosY = position.intY();
        int mainPosZ = position.intZ();
        final Block b1 = world.getBlock(mainPosX, mainPosY, mainPosZ);
        if (b1 != null && b1.isAir((IBlockAccess)world, mainPosX, mainPosY, mainPosZ)) {
            final Vector3 vector3 = position;
            vector3.x += playerCenter.x - mainPosX;
            final Vector3 vector4 = position;
            vector4.z += playerCenter.z - mainPosZ;
            final Block b2 = world.getBlock(position.intX(), position.intY(), position.intZ());
            if (b2 != null && b2.isAir((IBlockAccess)world, position.intX(), position.intY(), position.intZ())) {
                for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                    if (direction != ForgeDirection.DOWN && direction != ForgeDirection.UP) {
                        final Block b3 = world.getBlock(mainPosX + direction.offsetX, mainPosY, mainPosZ + direction.offsetZ);
                        if (b3 != null && !b3.isAir((IBlockAccess)world, mainPosX + direction.offsetX, mainPosY, mainPosZ + direction.offsetZ)) {
                            final Vector3 vector5 = position;
                            vector5.x += direction.offsetX;
                            final Vector3 vector6 = position;
                            vector6.z += direction.offsetZ;
                            break;
                        }
                    }
                }
            }
        }
        mainPosX = position.intX();
        mainPosZ = position.intZ();
        final double x0 = Math.sin((45.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.x;
        final double x2 = Math.sin((135.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.x;
        final double x3 = Math.sin((225.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.x;
        final double x4 = Math.sin((315.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.x;
        final double z0 = Math.cos((45.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.z;
        final double z2 = Math.cos((135.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.z;
        final double z3 = Math.cos((225.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.z;
        final double z4 = Math.cos((315.0f - rotation) * 3.141592653589793 / 180.0) * footprintScale + position.z;
        final double xMin = Math.min(Math.min(x0, x2), Math.min(x3, x4));
        final double xMax = Math.max(Math.max(x0, x2), Math.max(x3, x4));
        final double zMin = Math.min(Math.min(z0, z2), Math.min(z3, z4));
        final double zMax = Math.max(Math.max(z0, z2), Math.max(z3, z4));
        if (xMin < mainPosX) {
            final Vector3 vector7 = position;
            vector7.x += mainPosX - xMin;
        }
        if (xMax > mainPosX + 1) {
            final Vector3 vector8 = position;
            vector8.x -= xMax - (mainPosX + 1);
        }
        if (zMin < mainPosZ) {
            final Vector3 vector9 = position;
            vector9.z += mainPosZ - zMin;
        }
        if (zMax > mainPosZ + 1) {
            final Vector3 vector10 = position;
            vector10.z -= zMax - (mainPosZ + 1);
        }
        return position;
    }

    public static String spaceStationDataToString(final HashMap<Integer, Integer> data) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<Map.Entry<Integer, Integer>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Integer, Integer> e = it.next();
            builder.append(e.getKey());
            builder.append("$");
            builder.append(e.getValue());
            if (it.hasNext()) {
                builder.append("?");
            }
        }
        return builder.toString();
    }

    public static HashMap<Integer, Integer> stringToSpaceStationData(final String input) {
        final HashMap<Integer, Integer> data = Maps.newHashMap();
        if (!input.isEmpty()) {
            final String[] str0 = input.split("\\?");
            for (int i = 0; i < str0.length; ++i) {
                final String[] str2 = str0[i].split("\\$");
                data.put(Integer.parseInt(str2[0]), Integer.parseInt(str2[1]));
            }
        }
        return data;
    }

    public static String getDimensionName(final WorldProvider wp) {
        if (wp instanceof IGalacticraftWorldProvider) {
            final CelestialBody cb = ((IGalacticraftWorldProvider)wp).getCelestialBody();
            if (cb != null && !(cb instanceof Satellite)) {
                return cb.getUnlocalizedName();
            }
        }
        if (wp.dimensionId == ConfigManagerCore.idDimensionOverworld) {
            return "Overworld";
        }
        return wp.getDimensionName();
    }

    public static void setNextMorning(final WorldServer world) {
        final long current = world.getWorldInfo().getWorldTime();
        long dayLength = 24000L;
        long newTime = current - current % dayLength + dayLength;
        if (world.provider instanceof WorldProviderSpace) {
            dayLength = ((WorldProviderSpace)world.provider).getDayLength();
            if (dayLength <= 0L) {
                return;
            }
            newTime = current - current % dayLength + dayLength;
        }
        else {
            final long diff = newTime - current;
            for (final WorldServer worldServer : MinecraftServer.getServer().worldServers) {
                if (worldServer != world) {
                    if (worldServer.provider instanceof WorldProviderSpace) {
                        ((WorldProviderSpace)worldServer.provider).adjustTimeOffset(diff);
                    }
                }
            }
        }
        world.provider.setWorldTime(newTime);
    }

    static {
        WorldUtil.dimNames = new TreeMap<Integer, String>();
        WorldUtil.celestialMapCache = new MapMaker().weakKeys().makeMap();
        WorldUtil.generatorGCGreg = null;
        WorldUtil.generatorCoFH = null;
        WorldUtil.generatorDenseOres = null;
        WorldUtil.generatorTCAuraNodes = null;
        WorldUtil.generatorAE2meteors = null;
        WorldUtil.generateTCAuraNodes = null;
        WorldUtil.generatorsInitialised = false;
    }
}
