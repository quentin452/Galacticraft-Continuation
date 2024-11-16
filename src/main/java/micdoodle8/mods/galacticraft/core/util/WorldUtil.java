package micdoodle8.mods.galacticraft.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav;
import micdoodle8.mods.galacticraft.api.entity.IWorldTransferCallback;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.item.IArmorGravity;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.api.world.SpaceStationType;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.SkyProviderOverworld;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.entities.EntityArrowGC;
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.items.ItemParaChute;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry;

// import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner;

public class WorldUtil {

    public static HashMap<Integer, Integer> registeredSpaceStations; // Dimension IDs and providers (providers are -26
    // or -27 by default)
    public static Map<Integer, String> dimNames = new TreeMap<>(); // Dimension IDs and provider names
    public static Map<EntityPlayerMP, HashMap<String, Integer>> celestialMapCache = new MapMaker().weakKeys()
        .makeMap();
    public static List<Integer> registeredPlanets;
    private static IWorldGenerator generatorGCGreg = null;
    private static IWorldGenerator generatorCoFH = null;
    private static IWorldGenerator generatorDenseOres = null;
    private static IWorldGenerator generatorTCAuraNodes = null;
    private static IWorldGenerator generatorAE2meteors = null;
    private static Method generateTCAuraNodes = null;
    private static boolean generatorsInitialised = false;

    public static double getGravityForEntity(Entity entity) {
        if (entity.worldObj.provider instanceof IGalacticraftWorldProvider) {
            if (entity instanceof EntityChicken
                && !OxygenUtil.isAABBInBreathableAirBlock(entity.worldObj, entity.boundingBox)) {
                return 0.08D;
            }

            final IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider) entity.worldObj.provider;
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).inventory != null) {
                EntityPlayer player = (EntityPlayer) entity;
                int armorModLowGrav = 100;
                int armorModHighGrav = 100;

                for (int i = 0; i < 4; i++) {
                    final ItemStack armorPiece = player.getCurrentArmor(i);

                    if (armorPiece != null && armorPiece.getItem() instanceof IArmorGravity) {
                        IArmorGravity armorItem = (IArmorGravity) armorPiece.getItem();
                        armorModLowGrav -= armorItem.gravityOverrideIfLow(player);
                        armorModHighGrav -= armorItem.gravityOverrideIfHigh(player);
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
                if (customProvider.getGravity() > 0) {
                    return 0.08D - customProvider.getGravity() * armorModLowGrav / 100;
                }
                return 0.08D - customProvider.getGravity() * armorModHighGrav / 100;
            }
            return 0.08D - customProvider.getGravity();
        }
        if (entity instanceof IAntiGrav) {
            return 0;
        }
        return 0.08D;
    }

    public static float getGravityFactor(Entity entity) {
        if (entity.worldObj.provider instanceof IGalacticraftWorldProvider) {
            IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider) entity.worldObj.provider;

            float returnValue = MathHelper.sqrt_float(0.08F / (0.08F - customProvider.getGravity()));
            if (returnValue > 2.5F) {
                returnValue = 2.5F;
            }
            if (returnValue < 0.75F) {
                returnValue = 0.75F;
            }

            return returnValue;
        }

        return 1F;
    }

    public static double getItemGravity(EntityItem e) {
        if (e.worldObj.provider instanceof IGalacticraftWorldProvider) {
            IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider) e.worldObj.provider;

            double gravity = 0.03999999910593033D;

            if (customProvider instanceof IOrbitDimension) {
                gravity = Math.max(0.002D, gravity - 0.05999999910593033D / 1.75D);
            } else {
                gravity = Math.max(0.002D, gravity - customProvider.getGravity() / 1.75D);
            }

            return gravity;
        }

        return 0.03999999910593033D;
    }

    public static float getArrowGravity(EntityArrow e) {
        if (e.worldObj.provider instanceof IGalacticraftWorldProvider) {
            return 0.005F;
        }
        return 0.05F;
    }

    public static float getRainStrength(World world, float partialTicks) {
        if (world.isRemote && world.provider.getSkyRenderer() instanceof SkyProviderOverworld) {
            return 0.0F;
        }

        return world.prevRainingStrength + (world.rainingStrength - world.prevRainingStrength) * partialTicks;
    }

    public static boolean shouldRenderFire(Entity entity) {
        if (entity.worldObj == null || !(entity.worldObj.provider instanceof IGalacticraftWorldProvider)) {
            return entity.isBurning();
        }

        if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityArrow)
            && !(entity instanceof EntityArrowGC)) {
            return entity.isBurning();
        }

        if (entity.isBurning()) {
            if (OxygenUtil.noAtmosphericCombustion(entity.worldObj.provider)) {
                return OxygenUtil.isAABBInBreathableAirBlock(entity.worldObj, entity.boundingBox);
            }
            return true;
            // Disable fire on Galacticraft worlds with no oxygen
        }

        return false;
    }

    public static Vector3 getWorldColor(World world) {
        return new Vector3(1, 1, 1);
    }

    @SideOnly(Side.CLIENT)
    public static float getWorldBrightness(WorldClient world) {
        if (world.provider instanceof WorldProviderMoon) {
            final float f1 = world.getCelestialAngle(1.0F);
            float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.2F);

            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            f2 = 1.0F - f2;
            return f2 * 0.8F;
        }

        return world.getSunBrightness(1.0F);
    }

    public static float getColorRed(World world) {
        return (float) WorldUtil.getWorldColor(world).x;
    }

    public static float getColorGreen(World world) {
        return (float) WorldUtil.getWorldColor(world).y;
    }

    public static float getColorBlue(World world) {
        return (float) WorldUtil.getWorldColor(world).z;
    }

    public static Vec3 getFogColorHook(World world) {
        final EntityClientPlayerMP player = FMLClientHandler.instance()
            .getClient().thePlayer;
        if (world.provider.getSkyRenderer() instanceof SkyProviderOverworld) {
            float var20 = ((float) player.posY - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 1000.0F;
            var20 = MathHelper.sqrt_float(var20);
            final Vec3 vec = world.getFogColor(1.0F);

            return Vec3.createVectorHelper(
                vec.xCoord * Math.max(1.0F - var20 * 1.29F, 0.0F),
                vec.yCoord * Math.max(1.0F - var20 * 1.29F, 0.0F),
                vec.zCoord * Math.max(1.0F - var20 * 1.29F, 0.0F));
        }

        return world.getFogColor(1.0F);
    }

    public static Vec3 getSkyColorHook(World world) {
        final EntityClientPlayerMP player = FMLClientHandler.instance()
            .getClient().thePlayer;

        if (player == null || world == null || world.provider == null) {
            return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        }

        if (world.provider.getSkyRenderer() instanceof SkyProviderOverworld
            || player.posY > Constants.OVERWORLD_CLOUD_HEIGHT && player.ridingEntity instanceof EntitySpaceshipBase) {
            final float f1 = world.getCelestialAngle(1.0F);
            float f2 = MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            final int i = MathHelper.floor_double(player.posX);
            final int j = MathHelper.floor_double(player.posY);
            final int k = MathHelper.floor_double(player.posZ);
            final int l = ForgeHooksClient.getSkyBlendColour(world, i, j, k);
            float f4 = (l >> 16 & 255) / 255.0F;
            float f5 = (l >> 8 & 255) / 255.0F;
            float f6 = (l & 255) / 255.0F;
            f4 *= f2;
            f5 *= f2;
            f6 *= f2;

            if (player.posY <= Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                final Vec3 vec = world.getSkyColor(
                    FMLClientHandler.instance()
                        .getClient().renderViewEntity,
                    1.0F);

                if (vec == null) {
                    return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
                }

                final double blend = (player.posY - Constants.OVERWORLD_CLOUD_HEIGHT)
                    / (Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT - Constants.OVERWORLD_CLOUD_HEIGHT);
                final double ablend = 1 - blend;
                return Vec3.createVectorHelper(
                    f4 * blend + vec.xCoord * ablend,
                    f5 * blend + vec.yCoord * ablend,
                    f6 * blend + vec.zCoord * ablend);
            }
            // float blackness = ((float) (player.posY) -
            // Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) /
            // 1000.0F;
            // final float var21 = Math.max(1.0F - blackness * blackness * 4.0F, 0.0F);
            // return Vec3.createVectorHelper(f4 * var21, f5 * var21, f6 * var21);
            double blend = Math.min(1.0D, (player.posY - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 300.0D);
            final double ablend = 1.0D - blend;
            blend /= 255.0D;
            return Vec3.createVectorHelper(
                f4 * ablend + blend * 31.0D,
                f5 * ablend + blend * 8.0D,
                f6 * ablend + blend * 99.0D);
        }

        Entity renderViewEntity = FMLClientHandler.instance()
            .getClient().renderViewEntity;
        if (renderViewEntity == null) {
            return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        }

        return world.getSkyColor(renderViewEntity, 1.0F);
    }

    public static WorldProvider getProviderForNameServer(String par1String) {
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
                return WorldUtil.getProviderForDimensionServer(element.getKey());
            }
        }

        GCLog.info("Failed to find matching world for '" + par1String + "'");
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static WorldProvider getProviderForNameClient(String par1String) {
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
                return WorldUtil.getProviderForDimensionClient(element.getKey());
            }
        }

        GCLog.info("Failed to find matching world for '" + par1String + "'");
        return null;
    }

    public static void initialiseDimensionNames() {
        final WorldProvider provider = WorldUtil.getProviderForDimensionServer(ConfigManagerCore.idDimensionOverworld);
        assert provider != null;
        WorldUtil.dimNames.put(ConfigManagerCore.idDimensionOverworld, provider.getDimensionName());
    }

    /**
     * This will *load* all the GC dimensions which the player has access to (taking account of space station
     * permissions). Loading the dimensions through Forge activates any chunk loaders or forced chunks in that
     * dimension, if the dimension was not previously loaded. This may place load on the server.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for space station permissions)
     * @return a List of integers which are the dimension IDs
     */
    public static List<Integer> getPossibleDimensionsForSpaceshipTier(int tier, EntityPlayerMP playerBase) {
        final List<Integer> temp = new ArrayList<>();

        if (!ConfigManagerCore.disableRocketsToOverworld) {
            temp.add(ConfigManagerCore.idDimensionOverworld);
        }

        for (final Integer element : WorldUtil.registeredPlanets) {
            if (element == ConfigManagerCore.idDimensionOverworld) {
                continue;
            }
            final WorldProvider provider = WorldUtil.getProviderForDimensionServer(element);

            if (provider != null) {
                if (provider instanceof IGalacticraftWorldProvider) {
                    if (((IGalacticraftWorldProvider) provider).canSpaceshipTierPass(tier)) {
                        temp.add(element);
                    }
                } else {
                    temp.add(element);
                }
            }
        }

        for (final Integer element : WorldUtil.registeredSpaceStations.keySet()) {
            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, element, null);

            if (!ConfigManagerCore.spaceStationsRequirePermission || data.getAllowedAll()
                || data.getAllowedPlayers()
                    .contains(
                        playerBase.getGameProfile()
                            .getName())
                || VersionUtil.isPlayerOpped(playerBase)) {
                // Satellites always reachable from their own homeworld or from its other
                // satellites
                if (playerBase != null) {
                    // Player is on homeworld
                    if (playerBase.dimension == data.getHomePlanet()) {
                        temp.add(element);
                        continue;
                    }
                    if (playerBase.worldObj.provider instanceof IOrbitDimension) {
                        // Player is currently on another space station around the same planet
                        final SpaceStationWorldData dataCurrent = SpaceStationWorldData
                            .getStationData(playerBase.worldObj, playerBase.dimension, null);
                        if (dataCurrent.getHomePlanet() == data.getHomePlanet()) {
                            temp.add(element);
                            continue;
                        }
                    }
                }

                // Testing dimension is a satellite, but with a different homeworld - test its
                // tier
                final WorldProvider homeWorld = WorldUtil.getProviderForDimensionServer(data.getHomePlanet());
                final WorldProvider provider = WorldUtil.getProviderForDimensionServer(element);

                if (homeWorld != null) {
                    if (homeWorld instanceof IGalacticraftWorldProvider) {
                        if (((IGalacticraftWorldProvider) homeWorld).canSpaceshipTierPass(tier)
                            // if space stations at unreachable planets are allowed, we have to ask the
                            // satellite's
                            // WorldProvider instead
                            || ConfigManagerCore.allowSSatUnreachable
                                && ((IGalacticraftWorldProvider) provider).canSpaceshipTierPass(tier)) {
                            temp.add(element);
                        }
                    } else {
                        temp.add(element);
                    }
                }
            }
        }

        return temp;
    }

    public static CelestialBody getReachableCelestialBodiesForDimensionID(int id) {
        final List<CelestialBody> celestialBodyList = Lists.newArrayList();
        celestialBodyList.addAll(
            GalaxyRegistry.getRegisteredMoons()
                .values());
        celestialBodyList.addAll(
            GalaxyRegistry.getRegisteredPlanets()
                .values());
        celestialBodyList.addAll(
            GalaxyRegistry.getRegisteredSatellites()
                .values());

        for (final CelestialBody cBody : celestialBodyList) {
            if (cBody.getReachable() && cBody.getDimensionID() == id) {
                return cBody;
            }
        }

        return null;
    }

    public static CelestialBody getReachableCelestialBodiesForName(String name) {
        final List<CelestialBody> celestialBodyList = Lists.newArrayList();
        celestialBodyList.addAll(
            GalaxyRegistry.getRegisteredMoons()
                .values());
        celestialBodyList.addAll(
            GalaxyRegistry.getRegisteredPlanets()
                .values());
        celestialBodyList.addAll(
            GalaxyRegistry.getRegisteredSatellites()
                .values());

        for (final CelestialBody cBody : celestialBodyList) {
            if (cBody.getReachable() && cBody.getName()
                .equals(name)) {
                return cBody;
            }
        }

        return null;
    }

    /**
     * CAUTION: this loads the dimension if it is not already loaded. This can cause server load if used too frequently
     * or with a list of multiple dimensions.
     *
     * @param id
     * @return
     */
    public static WorldProvider getProviderForDimensionServer(int id) {
        final MinecraftServer theServer = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        if (theServer == null) {
            GCLog.debug("Called WorldUtil server side method but FML returned no server - is this a bug?");
            return null;
        }
        final World ws = theServer.worldServerForDimension(id);
        if (ws != null) {
            return ws.provider;
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static WorldProvider getProviderForDimensionClient(int id) {
        final World ws = ClientProxyCore.mc.theWorld;
        if (ws != null && ws.provider.dimensionId == id) {
            return ws.provider;
        }
        return WorldProvider.getProviderForDimension(id);
    }

    /**
     * This will *load* all the GC dimensions which the player has access to (taking account of space station
     * permissions). Loading the dimensions through Forge activates any chunk loaders or forced chunks in that
     * dimension, if the dimension was not previously loaded. This may place load on the server.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for checking space station permissions)
     * @return a Map of the names of the dimension vs. the dimension IDs
     */
    public static HashMap<String, Integer> getArrayOfPossibleDimensions(int tier, EntityPlayerMP playerBase) {
        final List<Integer> ids = WorldUtil.getPossibleDimensionsForSpaceshipTier(tier, playerBase);
        final HashMap<String, Integer> map = new HashMap<>();

        for (final Integer id : ids) {
            CelestialBody celestialBody = getReachableCelestialBodiesForDimensionID(id);

            // It's a space station
            if (id > 0 && celestialBody == null) {
                celestialBody = GalacticraftCore.satelliteSpaceStation;
                // This no longer checks whether a WorldProvider can be created, for performance
                // reasons (that causes
                // the dimension to load unnecessarily at map building stage)
                if (playerBase != null) {
                    final SpaceStationWorldData data = SpaceStationWorldData
                        .getStationData(playerBase.worldObj, id, null);
                    map.put(
                        celestialBody.getName() + "$"
                            + data.getOwner()
                            + "$"
                            + data.getSpaceStationName()
                            + "$"
                            + id
                            + "$"
                            + data.getHomePlanet(),
                        id);
                }
            } else if (celestialBody == GalacticraftCore.planetOverworld) {
                map.put(celestialBody.getName(), id);
            } else {
                final WorldProvider provider = WorldUtil.getProviderForDimensionServer(id);
                if (celestialBody != null && provider != null) {
                    if (provider instanceof IGalacticraftWorldProvider && !(provider instanceof IOrbitDimension)
                        || provider.dimensionId == 0) {
                        map.put(celestialBody.getName(), provider.dimensionId);
                    }
                }
            }
        }

        final ArrayList<CelestialBody> cBodyList = new ArrayList<>();
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredPlanets()
                .values());
        cBodyList.addAll(
            GalaxyRegistry.getRegisteredMoons()
                .values());

        for (final CelestialBody body : cBodyList) {
            if (!body.getReachable()) {
                map.put(body.getLocalizedName() + "*", body.getDimensionID());
            }
        }

        WorldUtil.celestialMapCache.put(playerBase, map);
        return map;
    }

    /**
     * Get the cached version of getArrayOfPossibleDimensions() to reduce server load + unwanted dimension loading The
     * cache will be updated every time the 'proper' version of getArrayOfPossibleDimensions is called.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for checking space station permissions)
     * @return a Map of the names of the dimension vs. the dimension IDs
     */
    public static HashMap<String, Integer> getArrayOfPossibleDimensionsAgain(int tier, EntityPlayerMP playerBase) {
        final HashMap<String, Integer> map = WorldUtil.celestialMapCache.get(playerBase);
        if (map != null) {
            return map;
        }
        return getArrayOfPossibleDimensions(tier, playerBase);
    }

    public static void unregisterSpaceStations() {
        if (WorldUtil.registeredSpaceStations != null) {
            for (final Integer registeredID : WorldUtil.registeredSpaceStations.keySet()) {
                if (DimensionManager.isDimensionRegistered(registeredID)) {
                    DimensionManager.unregisterDimension(registeredID);
                }
            }

            WorldUtil.registeredSpaceStations = null;
        }
    }

    public static void registerSpaceStations(File spaceStationList) {
        // WorldUtil.registeredSpaceStations =
        // WorldUtil.getExistingSpaceStationList(spaceStationList);
        WorldUtil.registeredSpaceStations = Maps.newHashMap();
        final MinecraftServer theServer = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        if (theServer == null) {
            return;
        }

        final File[] var2 = spaceStationList.listFiles();

        if (var2 != null) {
            for (final File var5 : var2) {
                if (var5.getName()
                    .contains("spacestation_")) {
                    try {
                        // Note: this is kind of a hacky way of doing this, loading the NBT from each
                        // space station file
                        // during dimension registration, to find out what each space station's provider
                        // IDs are.

                        String name = var5.getName();
                        final SpaceStationWorldData worldDataTemp = new SpaceStationWorldData(name);
                        name = name.substring(13, name.length() - 4);
                        final int registeredID = Integer.parseInt(name);

                        final FileInputStream fileinputstream = new FileInputStream(var5);
                        final NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
                        fileinputstream.close();
                        worldDataTemp.readFromNBT(nbttagcompound.getCompoundTag("data"));

                        // Search for id in server-defined statically loaded dimensions
                        final int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, registeredID);

                        if (!DimensionManager.isDimensionRegistered(registeredID)) {
                            if (id >= 0) {
                                DimensionManager.registerDimension(registeredID, worldDataTemp.getDimensionIdStatic());
                                WorldUtil.registeredSpaceStations
                                    .put(registeredID, worldDataTemp.getDimensionIdStatic());
                                theServer.worldServerForDimension(registeredID);
                            } else {
                                DimensionManager.registerDimension(registeredID, worldDataTemp.getDimensionIdDynamic());
                                WorldUtil.registeredSpaceStations
                                    .put(registeredID, worldDataTemp.getDimensionIdDynamic());
                            }
                            WorldUtil.dimNames.put(registeredID, "Space Station " + registeredID);
                        } else {
                            GCLog.severe(
                                "Dimension already registered to another mod: unable to register space station dimension "
                                    + registeredID);
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // for (Integer registeredID : WorldUtil.registeredSpaceStations)
        // {
        // int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions,
        // registeredID);
        //
        // if (!DimensionManager.isDimensionRegistered(registeredID))
        // {
        // if (id >= 0)
        // {
        // DimensionManager.registerDimension(registeredID,
        // ConfigManagerCore.idDimensionOverworldOrbitStatic);
        // theServer.worldServerForDimension(registeredID);
        // }
        // else
        // {
        // DimensionManager.registerDimension(registeredID,
        // ConfigManagerCore.idDimensionOverworldOrbit);
        // }
        // }
        // else
        // {
        // GCLog.severe("Dimension already registered to another mod: unable to register
        // space station
        // dimension " + registeredID);
        // }
        // }
    }

    /**
     * Call this on FMLServerStartingEvent to register a planet which has a dimension ID. Now returns a boolean to
     * indicate whether registration was successful.
     * <p>
     * NOTE: Planets and Moons dimensions should normally be initialised at server init If you do not do this, you must
     * find your own way to register the dimension in DimensionManager and you must find your own way to include the
     * cached provider name in WorldUtil.dimNames
     * <p>
     * IMPORTANT: GalacticraftRegistry.registerProvider() must always be called in parallel with this meaning the
     * CelestialBodies are iterated in the same order when registered there and here.
     */
    public static boolean registerPlanet(int planetID, boolean initialiseDimensionAtServerInit, int defaultID) {
        if (WorldUtil.registeredPlanets == null) {
            WorldUtil.registeredPlanets = new ArrayList<>();
        }

        if (initialiseDimensionAtServerInit) {
            if (DimensionManager.isDimensionRegistered(planetID)) {
                GCLog.severe(
                    "Dimension already registered to another mod: unable to register planet dimension " + planetID);
                // Add 0 to the list to preserve the correct order of the other planets (e.g. if
                // server/client
                // initialise with different dimension IDs in configs, the order becomes
                // important for figuring out what
                // is going on)
                WorldUtil.registeredPlanets.add(defaultID);
                return false;
            }
            DimensionManager.registerDimension(planetID, planetID);
            GCLog.info("Registered Dimension: " + planetID);
            WorldUtil.registeredPlanets.add(planetID);
            final World w = FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .worldServerForDimension(planetID);
            WorldUtil.dimNames.put(planetID, getDimensionName(w.provider));
            return true;
        }

        // Not to be initialised - still add to the registered planets list (for
        // hotloading later?)
        WorldUtil.registeredPlanets.add(planetID);
        return true;
    }

    public static void unregisterPlanets() {
        if (WorldUtil.registeredPlanets != null) {
            for (final Integer var1 : WorldUtil.registeredPlanets) {
                if (DimensionManager.isDimensionRegistered(var1)) {
                    DimensionManager.unregisterDimension(var1);
                    GCLog.info("Unregistered Dimension: " + var1);
                } else {
                    GCLog.info("Unregistered Dimension: " + var1 + " - already unregistered");
                }
            }

            WorldUtil.registeredPlanets = null;
        }
        WorldUtil.dimNames.clear();
    }

    /**
     * You should now use WorldUtil.registerPlanet(int planetID, boolean initialiseDimensionAtServerInit, int defaultID)
     * which returns a boolean indicating that the dimension could be successfully created (if
     * initialiseDimensionAtServerInit is true). Always returns true if if initialiseDimensionAtServerInit is false.
     *
     * @param planetID
     * @param initialiseDimensionAtServerInit
     */
    @Deprecated
    public static void registerPlanet(int planetID, boolean initialiseDimensionAtServerInit) {
        WorldUtil.registerPlanet(planetID, initialiseDimensionAtServerInit, 0);
    }

    public static void registerPlanetClient(Integer dimID, int providerIndex) {
        final int providerID = GalacticraftRegistry.getProviderID(providerIndex);

        if (providerID == 0) {
            GCLog.severe("Server dimension " + dimID + " has no match on client due to earlier registration problem.");
        } else if (dimID == 0) {
            GCLog.severe(
                "Client dimension " + providerID
                    + " has no match on server - probably a server dimension ID conflict problem.");
        } else if (!WorldUtil.registeredPlanets.contains(dimID)) {
            WorldUtil.registeredPlanets.add(dimID);
            DimensionManager.registerDimension(dimID, providerID);
        } else {
            GCLog.severe("Dimension already registered to another mod: unable to register planet dimension " + dimID);
        }
    }

    /**
     * This doesn't check if player is using the correct rocket, this is just a total list of all space dimensions. It
     * does not load the dimensions.
     */
    public static Integer[] getArrayOfPossibleDimensions() {
        final ArrayList<Integer> temp = new ArrayList<>();

        temp.add(ConfigManagerCore.idDimensionOverworld);

        temp.addAll(WorldUtil.registeredPlanets);

        if (WorldUtil.registeredSpaceStations != null) {
            temp.addAll(WorldUtil.registeredSpaceStations.keySet());
        }

        final Integer[] finalArray = new Integer[temp.size()];

        int count = 0;

        for (final Integer integ : temp) {
            finalArray[count] = integ;
            count++;
        }

        return finalArray;
    }

    public static SpaceStationWorldData bindSpaceStationToNewDimension(World world, EntityPlayerMP player,
        int homePlanetID) {
        int dynamicProviderID = -1;
        int staticProviderID = -1;
        for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites()
            .values()) {
            if (satellite.getParentPlanet()
                .getDimensionID() == homePlanetID) {
                dynamicProviderID = satellite.getDimensionID();
                staticProviderID = satellite.getDimensionIdStatic();
            }
        }
        if (dynamicProviderID == -1 || staticProviderID == -1) {
            throw new RuntimeException("Space station being bound on bad provider IDs!");
        }
        final int newID = DimensionManager.getNextFreeDimId();
        final SpaceStationWorldData data = WorldUtil
            .createSpaceStation(world, newID, homePlanetID, dynamicProviderID, staticProviderID, player);
        dimNames.put(newID, "Space Station " + newID);
        final GCPlayerStats stats = GCPlayerStats.get(player);
        stats.spaceStationDimensionData.put(homePlanetID, newID);
        GalacticraftCore.packetPipeline.sendTo(
            new PacketSimple(
                EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID,
                new Object[] { WorldUtil.spaceStationDataToString(stats.spaceStationDimensionData) }),
            player);
        return data;
    }

    public static SpaceStationWorldData createSpaceStation(World world, int dimID, int homePlanetID,
        int dynamicProviderID, int staticProviderID, EntityPlayerMP player) {
        if (!DimensionManager.isDimensionRegistered(dimID)) {
            if (ConfigManagerCore.keepLoadedNewSpaceStations) {
                ConfigManagerCore.setLoaded(dimID);
            }

            final int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, dimID);

            if (id >= 0) {
                DimensionManager.registerDimension(dimID, staticProviderID);
                WorldUtil.registeredSpaceStations.put(dimID, staticProviderID);
            } else {
                DimensionManager.registerDimension(dimID, dynamicProviderID);
                WorldUtil.registeredSpaceStations.put(dimID, dynamicProviderID);
            }
        } else {
            GCLog.severe(
                "Dimension already registered to another mod: unable to register space station dimension " + dimID);
        }

        GalacticraftCore.packetPipeline
            .sendToAll(new PacketSimple(EnumSimplePacket.C_UPDATE_SPACESTATION_LIST, WorldUtil.getSpaceStationList()));
        return SpaceStationWorldData
            .getStationData(world, dimID, homePlanetID, dynamicProviderID, staticProviderID, player);
    }

    public static Entity transferEntityToDimension(Entity entity, int dimensionID, WorldServer world) {
        return WorldUtil.transferEntityToDimension(entity, dimensionID, world, true, null);
    }

    public static Entity cancelTeleportation(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            GCPlayerStats stats = GCPlayerStats.get(player);
            stats.usingPlanetSelectionGui = false;
        }
        return entity;
    }

    /**
     * It is not necessary to use entity.setDead() following calling this method. If the entity left the old world it
     * was in, it will now automatically be removed from that old world before the next update tick. (See
     * WorldUtil.removeEntityFromWorld())
     */
    public static Entity transferEntityToDimension(Entity entity, int dimensionID, WorldServer world,
        boolean transferInv, EntityAutoRocket ridingRocket) {
        if (!world.isRemote) {
            // GalacticraftCore.packetPipeline.sendToAll(new
            // PacketSimple(EnumSimplePacket.C_UPDATE_PLANETS_LIST,
            // WorldUtil.getPlanetList()));

            final MinecraftServer mcServer = FMLCommonHandler.instance()
                .getMinecraftServerInstance();

            if (mcServer != null) {
                final WorldServer var6 = mcServer.worldServerForDimension(dimensionID);

                if (var6 == null) {
                    System.err.println(
                        "Cannot Transfer Entity to Dimension: Could not get World for Dimension " + dimensionID);
                    return null;
                }

                final ITeleportType type = GalacticraftRegistry.getTeleportTypeForDimension(var6.provider.getClass());

                if (type != null) {
                    return WorldUtil.teleportEntity(var6, entity, dimensionID, type, transferInv, ridingRocket);
                }
            }
        }

        return null;
    }

    private static Entity teleportEntity(World worldNew, Entity entity, int dimID, ITeleportType type,
        boolean transferInv, EntityAutoRocket ridingRocket) {
        Entity otherRiddenEntity = null;
        if (entity.ridingEntity != null) {
            if (entity.ridingEntity instanceof EntitySpaceshipBase) {
                entity.mountEntity(entity.ridingEntity);
            } else {
                if (entity.ridingEntity instanceof EntityCelestialFake) {
                    entity.ridingEntity.setDead();
                } else {
                    otherRiddenEntity = entity.ridingEntity;
                }
                entity.mountEntity(null);
            }
        }

        final boolean dimChange = entity.worldObj != worldNew;
        // Make sure the entity is added to the correct chunk in the OLD world so that
        // it will be properly removed later
        // if it needs to be unloaded from that world
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        EntityPlayerMP player = null;
        Vector3 spawnPos = null;
        final int oldDimID = entity.worldObj.provider.dimensionId;

        if (ridingRocket != null) {
            final NBTTagCompound nbt = new NBTTagCompound();
            ridingRocket.isDead = false;
            ridingRocket.riddenByEntity = null;
            ridingRocket.writeToNBTOptional(nbt);

            removeEntityFromWorld(ridingRocket.worldObj, ridingRocket, true);

            ridingRocket = (EntityAutoRocket) EntityList.createEntityFromNBT(nbt, worldNew);

            if (ridingRocket != null) {
                ridingRocket.setWaitForPlayer(true);

                if (ridingRocket instanceof IWorldTransferCallback) {
                    ((IWorldTransferCallback) ridingRocket).onWorldTransferred(worldNew);
                }
            }
        }

        if (dimChange) {
            if (entity instanceof EntityPlayerMP) {
                player = (EntityPlayerMP) entity;
                final World worldOld = player.worldObj;
                if (ConfigManagerCore.enableDebug) {
                    try {
                        GCLog.info("DEBUG: Attempting to remove player from old dimension " + oldDimID);
                        ((WorldServer) worldOld).getPlayerManager()
                            .removePlayer(player);
                        GCLog.info("DEBUG: Successfully removed player from old dimension " + oldDimID);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ((WorldServer) worldOld).getPlayerManager()
                            .removePlayer(player);
                    } catch (final Exception e) {}
                }

                final GCPlayerStats stats = GCPlayerStats.get(player);
                stats.usingPlanetSelectionGui = false;

                player.dimension = dimID;
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("DEBUG: Sending respawn packet to player for dim " + dimID);
                }
                player.playerNetServerHandler.sendPacket(
                    new S07PacketRespawn(
                        dimID,
                        player.worldObj.difficultySetting,
                        player.worldObj.getWorldInfo()
                            .getTerrainType(),
                        player.theItemInWorldManager.getGameType()));

                if (worldNew.provider instanceof WorldProviderOrbit
                    && WorldUtil.registeredSpaceStations.containsKey(dimID))
                // TODO This has never been effective before due to the earlier bug - what does
                // it actually do?
                {
                    final NBTTagCompound var2 = new NBTTagCompound();
                    SpaceStationWorldData.getStationData(worldNew, dimID, player)
                        .writeToNBT(var2);
                    GalacticraftCore.packetPipeline.sendTo(
                        new PacketSimple(EnumSimplePacket.C_UPDATE_SPACESTATION_DATA, new Object[] { dimID, var2 }),
                        player);
                }

                removeEntityFromWorld(worldOld, player, true);
                if (worldNew.provider instanceof WorldProviderSpaceStation) {
                    GalacticraftCore.packetPipeline
                        .sendTo(new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[] {}), player);
                }

                if (ridingRocket != null) {
                    spawnPos = new Vector3(ridingRocket);
                } else {
                    spawnPos = type.getPlayerSpawnLocation((WorldServer) worldNew, player);
                }
                forceMoveEntityToPos(entity, (WorldServer) worldNew, spawnPos, true);

                player.mcServer.getConfigurationManager()
                    .func_72375_a(player, (WorldServer) worldNew);
                GCLog.info(
                    "Server attempting to transfer player " + player.getGameProfile()
                        .getName() + " to dimension " + worldNew.provider.dimensionId);

                player.theItemInWorldManager.setWorld((WorldServer) worldNew);
                player.mcServer.getConfigurationManager()
                    .updateTimeAndWeatherForPlayer(player, (WorldServer) worldNew);
                player.mcServer.getConfigurationManager()
                    .syncPlayerInventory(player);

                for (final Object o : player.getActivePotionEffects()) {
                    final PotionEffect var10 = (PotionEffect) o;
                    player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), var10));
                }

                player.playerNetServerHandler.sendPacket(
                    new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
            } else
            // Non-player entity transfer i.e. it's an EntityCargoRocket or an empty rocket
            {
                ArrayList<TileEntityTelemetry> tList = null;
                if (entity instanceof EntitySpaceshipBase) {
                    tList = ((EntitySpaceshipBase) entity).getTelemetry();
                }
                WorldUtil.removeEntityFromWorld(entity.worldObj, entity, true);

                final NBTTagCompound nbt = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(nbt);
                entity = EntityList.createEntityFromNBT(nbt, worldNew);

                if (entity == null) {
                    return null;
                }

                if (entity instanceof IWorldTransferCallback) {
                    ((IWorldTransferCallback) entity).onWorldTransferred(worldNew);
                }

                forceMoveEntityToPos(entity, (WorldServer) worldNew, new Vector3(entity), true);

                if (tList != null && tList.size() > 0) {
                    for (final TileEntityTelemetry t : tList) {
                        t.addTrackedEntity(entity);
                    }
                }
            }
        } else // Same dimension player transfer
            if (entity instanceof EntityPlayerMP) {
                player = (EntityPlayerMP) entity;
                player.closeScreen();
                final GCPlayerStats stats = GCPlayerStats.get(player);
                stats.usingPlanetSelectionGui = false;

                if (worldNew.provider instanceof WorldProviderSpaceStation) {
                    GalacticraftCore.packetPipeline
                        .sendTo(new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[] {}), player);
                }

                if (ridingRocket != null) {
                    spawnPos = new Vector3(ridingRocket);
                } else {
                    spawnPos = type.getPlayerSpawnLocation((WorldServer) entity.worldObj, (EntityPlayerMP) entity);
                }
                forceMoveEntityToPos(entity, (WorldServer) worldNew, spawnPos, false);

                GCLog.info(
                    "Server attempting to transfer player " + player.getGameProfile()
                        .getName() + " within same dimension " + worldNew.provider.dimensionId);
            }

        // Update PlayerStatsGC
        if (player != null) {
            final GCPlayerStats playerStats = GCPlayerStats.get(player);
            GCPlayerHandler.setUsingParachute(
                player,
                playerStats,
                ridingRocket == null && type.useParachute()
                    && playerStats.extendedInventory.getStackInSlot(4) != null
                    && playerStats.extendedInventory.getStackInSlot(4)
                        .getItem() instanceof ItemParaChute);

            if (playerStats.rocketStacks != null && playerStats.rocketStacks.length > 0) {
                for (int stack = 0; stack < playerStats.rocketStacks.length; stack++) {
                    if (transferInv) {
                        if (playerStats.rocketStacks[stack] == null) {
                            if (stack == playerStats.rocketStacks.length - 1) {
                                if (playerStats.rocketItem != null) {
                                    final ItemStack rocket = new ItemStack(
                                        playerStats.rocketItem,
                                        1,
                                        playerStats.rocketType);
                                    final NBTTagCompound nbt = new NBTTagCompound();
                                    nbt.setInteger("RocketFuel", playerStats.fuelLevel);
                                    rocket.setTagCompound(nbt);
                                    playerStats.fuelLevel = 0;
                                    playerStats.rocketStacks[stack] = rocket;
                                }
                            } else if (stack == playerStats.rocketStacks.length - 2) {
                                playerStats.rocketStacks[stack] = playerStats.launchpadStack;
                                playerStats.launchpadStack = null;
                            }
                        }
                    } else {
                        playerStats.rocketStacks[stack] = null;
                    }
                }
            }

            if (transferInv && playerStats.chestSpawnCooldown == 0) {
                playerStats.chestSpawnVector = type
                    .getParaChestSpawnLocation((WorldServer) entity.worldObj, player, new Random());
                playerStats.chestSpawnCooldown = 200;
            }
        }

        if (ridingRocket != null) {
            worldNew.spawnEntityInWorld(ridingRocket);
            ridingRocket.setWorld(worldNew);
            worldNew.updateEntityWithOptionalForce(ridingRocket, true);
            entity.mountEntity(ridingRocket);
            GCLog.debug(
                "Entering rocket at : " + entity.posX
                    + ","
                    + entity.posZ
                    + " rocket at: "
                    + ridingRocket.posX
                    + ","
                    + ridingRocket.posZ);
        } else if (otherRiddenEntity != null) {
            if (dimChange) {
                final World worldOld = otherRiddenEntity.worldObj;
                final NBTTagCompound nbt = new NBTTagCompound();
                otherRiddenEntity.writeToNBTOptional(nbt);
                removeEntityFromWorld(worldOld, otherRiddenEntity, true);
                otherRiddenEntity = EntityList.createEntityFromNBT(nbt, worldNew);
                worldNew.spawnEntityInWorld(otherRiddenEntity);
                otherRiddenEntity.setWorld(worldNew);
            }
            otherRiddenEntity.setPositionAndRotation(
                entity.posX,
                entity.posY - 10,
                entity.posZ,
                otherRiddenEntity.rotationYaw,
                otherRiddenEntity.rotationPitch);
            worldNew.updateEntityWithOptionalForce(otherRiddenEntity, true);
        }

        if (entity instanceof EntityPlayerMP) {
            if (dimChange) {
                FMLCommonHandler.instance()
                    .firePlayerChangedDimensionEvent((EntityPlayerMP) entity, oldDimID, dimID);
            }
            // Spawn in a lander if appropriate
            if (ridingRocket == null) {
                type.onSpaceDimensionChanged(worldNew, (EntityPlayerMP) entity, ridingRocket != null);
            }
        }

        return entity;
    }

    /**
     * This correctly positions an entity at spawnPos in worldNew loading and adding it to the chunk as required.
     *
     * @param entity
     * @param worldNew
     * @param spawnPos
     */
    public static void forceMoveEntityToPos(Entity entity, WorldServer worldNew, Vector3 spawnPos,
        boolean spawnRequired) {
        final ChunkCoordIntPair pair = worldNew.getChunkFromChunkCoords(spawnPos.intX() >> 4, spawnPos.intZ() >> 4)
            .getChunkCoordIntPair();
        GCLog.debug("Loading first chunk in new dimension at " + pair.chunkXPos + "," + pair.chunkZPos);
        worldNew.theChunkProviderServer.loadChunk(pair.chunkXPos, pair.chunkZPos);
        if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP) entity).playerNetServerHandler
                .setPlayerLocation(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
        }
        entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
        if (spawnRequired) {
            worldNew.spawnEntityInWorld(entity);
            entity.setWorld(worldNew);
        }
        worldNew.updateEntityWithOptionalForce(entity, true);
    }

    public static WorldServer getStartWorld(WorldServer worldOld) {
        if (ConfigManagerCore.challengeSpawnHandling) {
            final WorldProvider wp = WorldUtil.getProviderForNameServer("planet.asteroids");
            final WorldServer worldNew = wp == null ? null : (WorldServer) wp.worldObj;
            if (worldNew != null) {
                return worldNew;
            }
        }
        return worldOld;
    }

    @SideOnly(Side.CLIENT)
    public static EntityPlayer forceRespawnClient(int dimID, int par2, String par3, int par4) {
        final S07PacketRespawn fakePacket = new S07PacketRespawn(
            dimID,
            EnumDifficulty.getDifficultyEnum(par2),
            WorldType.parseWorldType(par3),
            WorldSettings.GameType.getByID(par4));
        Minecraft.getMinecraft()
            .getNetHandler()
            .handleRespawn(fakePacket);
        return FMLClientHandler.instance()
            .getClientPlayerEntity();
    }

    private static void removeEntityFromWorld(World var0, Entity var1, boolean directlyRemove) {
        if (var1 instanceof EntityPlayer) {
            EntityPlayer var2 = (EntityPlayer) var1;
            var2.closeScreen();
            var0.playerEntities.remove(var2);
            var0.updateAllPlayersSleepingFlag();
        }

        if (directlyRemove) {
            final List<Entity> l = new ArrayList<>();
            l.add(var1);
            var0.unloadEntities(l);
            // This will automatically remove the entity from the world and the chunk prior
            // to the world's next update
            // entities tick
            // It is important NOT to directly modify World.loadedEntityList here, as the
            // World will be currently
            // iterating through that list when updating each entity (see the line
            // "this.loadedEntityList.remove(i--);"
            // in World.updateEntities()
        }

        var1.isDead = false;
    }

    public static SpaceStationRecipe getSpaceStationRecipe(int planetID) {
        for (final SpaceStationType type : GalacticraftRegistry.getSpaceStationData()) {
            if (type.getWorldToOrbitID() == planetID) {
                return type.getRecipeForSpaceStation();
            }
        }

        return null;
    }

    /**
     * This must return planets in the same order their provider IDs were registered in GalacticraftRegistry by
     * GalacticraftCore.
     */
    public static List<Object> getPlanetList() {
        final List<Object> objList = new ArrayList<>();
        objList.add(getPlanetListInts());
        return objList;
    }

    public static Integer[] getPlanetListInts() {
        final Integer[] iArray = new Integer[WorldUtil.registeredPlanets.size()];

        for (int i = 0; i < iArray.length; i++) {
            iArray[i] = WorldUtil.registeredPlanets.get(i);
        }

        return iArray;
    }

    /**
     * What's important here is that Galacticraft and the server both register the same reachable Galacticraft planets
     * (and their provider types) in the same order. See WorldUtil.registerPlanet().
     * <p>
     * Even if there are dimension conflicts or other problems, the planets must be registered in the same order on both
     * client and server. This should happen automatically if Galacticraft versions match, and if planets modules match
     * (including Galacticraft-Planets and any other sub-mods).
     * <p>
     * It is NOT a good idea for sub-mods to make the registration order of planets variable or dependent on configs.
     */
    public static void decodePlanetsListClient(List<Object> data) {
        try {
            if (ConfigManagerCore.enableDebug) {
                GCLog.info("GC connecting to server: received planets dimension ID list.");
            }
            if (WorldUtil.registeredPlanets != null) {
                for (final Integer registeredID : WorldUtil.registeredPlanets) {
                    if (DimensionManager.isDimensionRegistered(registeredID)) {
                        DimensionManager.unregisterDimension(registeredID);
                    }
                }
            }
            WorldUtil.registeredPlanets = new ArrayList<>();

            StringBuilder ids = new StringBuilder();
            if (data.size() > 0) {
                // Start the provider index at offset 2 to skip the two Overworld Orbit
                // dimensions
                // (this will be iterating through GalacticraftRegistry.worldProviderIDs)
                int providerIndex = GalaxyRegistry.getRegisteredSatellites()
                    .size() * 2;
                if (data.get(0) instanceof Integer) {
                    for (final Object o : data) {
                        WorldUtil.registerPlanetClient((Integer) o, providerIndex);
                        providerIndex++;
                        ids.append(((Integer) o).toString())
                            .append(" ");
                    }
                } else if (data.get(0) instanceof Integer[]) {
                    for (final Object o : (Integer[]) data.get(0)) {
                        WorldUtil.registerPlanetClient((Integer) o, providerIndex);
                        providerIndex++;
                        ids.append(((Integer) o).toString())
                            .append(" ");
                    }
                }
            }
            if (ConfigManagerCore.enableDebug) {
                GCLog.debug("GC clientside planet dimensions registered: " + ids.toString());
                final WorldProvider dimMoon = WorldUtil.getProviderForNameClient("moon.moon");
                if (dimMoon != null) {
                    GCLog.debug("Crosscheck: Moon is " + dimMoon.dimensionId);
                }
                final WorldProvider dimMars = WorldUtil.getProviderForNameClient("planet.mars");
                if (dimMars != null) {
                    GCLog.debug("Crosscheck: Mars is " + dimMars.dimensionId);
                }
                final WorldProvider dimAst = WorldUtil.getProviderForNameClient("planet.asteroids");
                if (dimAst != null) {
                    GCLog.debug("Crosscheck: Asteroids is " + dimAst.dimensionId);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Object> getSpaceStationList() {
        final List<Object> objList = new ArrayList<>();
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

        // for (int i = 0; i < iArray.length; i++)
        // {
        // iArray[i] = WorldUtil.registeredSpaceStations.get(i);
        // }

        return iArray;
    }

    public static void decodeSpaceStationListClient(List<Object> data) {
        try {
            if (WorldUtil.registeredSpaceStations != null) {
                for (final Integer registeredID : WorldUtil.registeredSpaceStations.keySet()) {
                    if (DimensionManager.isDimensionRegistered(registeredID)) {
                        DimensionManager.unregisterDimension(registeredID);
                    }
                }
            }
            WorldUtil.registeredSpaceStations = Maps.newHashMap();

            if (data.size() > 0) {
                if (data.get(0) instanceof Integer) {
                    for (int i = 0; i < data.size(); i += 2) {
                        registerSSdim((Integer) data.get(i), (Integer) data.get(i + 1));
                    }
                    // for (Object dimID : data)
                    // {
                    // registerSSdim((Integer) dimID);
                    // }
                } else if (data.get(0) instanceof Integer[]) {
                    final Integer[] array = (Integer[]) data.get(0);
                    for (int i = 0; i < array.length; i += 2) {
                        registerSSdim(array[i], array[i + 1]);
                    }
                    // for (Object dimID : (Integer[]) data.get(0))
                    // {
                    // registerSSdim((Integer) dimID);
                    // }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerSSdim(Integer dimID, Integer providerKey) {
        if (!WorldUtil.registeredSpaceStations.containsKey(dimID)) {
            if (!DimensionManager.isDimensionRegistered(dimID)) {
                WorldUtil.registeredSpaceStations.put(dimID, providerKey);
                DimensionManager.registerDimension(dimID, providerKey);
            } else {
                GCLog.severe(
                    "Dimension already registered on client: unable to register space station dimension " + dimID);
            }
        }
    }

    public static boolean otherModPreventGenerate(int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (!(world.provider instanceof IGalacticraftWorldProvider)) {
            return false;
        }
        if (world.provider instanceof WorldProviderSpaceStation) {
            return true;
        }
        if (ConfigManagerCore.enableOtherModsFeatures) {
            return false;
        }

        if (!generatorsInitialised) {
            generatorsInitialised = true;

            try {
                final Class<?> GCGreg = Class.forName("bloodasp.galacticgreg.GT_Worldgenerator_Space");
                if (GCGreg != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry")
                        .getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>) regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (GCGreg.isInstance(gen)) {
                            generatorGCGreg = gen;
                            break;
                        }
                    }
                }
            } catch (final Exception e) {}

            try {
                final Class<?> cofh = Class.forName("cofh.core.world.WorldHandler");
                if (cofh != null && ConfigManagerCore.whitelistCoFHCoreGen) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry")
                        .getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>) regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (cofh.isInstance(gen)) {
                            generatorCoFH = gen;
                            break;
                        }
                    }
                }
            } catch (final Exception e) {}

            try {
                final Class<?> denseOres = Class.forName("com.rwtema.denseores.WorldGenOres");
                if (denseOres != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry")
                        .getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>) regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (denseOres.isInstance(gen)) {
                            generatorDenseOres = gen;
                            break;
                        }
                    }
                }
            } catch (final Exception e) {}

            try {
                Class<?> ae2meteorPlace = null;
                try {
                    ae2meteorPlace = Class.forName("appeng.hooks.MeteoriteWorldGen");
                } catch (final ClassNotFoundException e) {}

                if (ae2meteorPlace == null) {
                    try {
                        ae2meteorPlace = Class.forName("appeng.worldgen.MeteoriteWorldGen");
                    } catch (final ClassNotFoundException e) {}
                }

                if (ae2meteorPlace != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry")
                        .getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>) regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (ae2meteorPlace.isInstance(gen)) {
                            generatorAE2meteors = gen;
                            break;
                        }
                    }
                }
            } catch (final Exception e) {}

            try {
                final Class<?> genThaumCraft = Class.forName("thaumcraft.common.lib.world.ThaumcraftWorldGenerator");
                if (genThaumCraft != null) {
                    final Field regField = Class.forName("cpw.mods.fml.common.registry.GameRegistry")
                        .getDeclaredField("worldGenerators");
                    regField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Set<IWorldGenerator> registeredGenerators = (Set<IWorldGenerator>) regField.get(null);
                    for (final IWorldGenerator gen : registeredGenerators) {
                        if (genThaumCraft.isInstance(gen)) {
                            generatorTCAuraNodes = gen;
                            break;
                        }
                    }
                    if (generatorTCAuraNodes != null && ConfigManagerCore.enableThaumCraftNodes) {
                        generateTCAuraNodes = genThaumCraft.getDeclaredMethod(
                            "generateWildNodes",
                            World.class,
                            Random.class,
                            int.class,
                            int.class,
                            boolean.class,
                            boolean.class);
                        generateTCAuraNodes.setAccessible(true);
                    }
                }

            } catch (final Exception e) {}

            if (generatorGCGreg != null) {
                System.out.println("Whitelisting GalacticGreg oregen on planets.");
            }
            if (generatorCoFH != null) {
                System.out.println("Whitelisting CoFHCore custom oregen on planets.");
            }
            if (generatorDenseOres != null) {
                System.out.println("Whitelisting Dense Ores oregen on planets.");
            }
            if (generatorAE2meteors != null) {
                System.out.println("Whitelisting AE2 meteorites worldgen on planets.");
            }
            if (generatorTCAuraNodes != null && generateTCAuraNodes != null) {
                System.out.println("Whitelisting ThaumCraft aura node generation on planets.");
            }
        }

        if (generatorGCGreg != null || generatorCoFH != null
            || generatorDenseOres != null
            || generatorTCAuraNodes != null
            || generatorAE2meteors != null) {
            try {
                final long worldSeed = world.getSeed();
                final Random fmlRandom = new Random(worldSeed);
                final long xSeed = fmlRandom.nextLong() >> 2 + 1L;
                final long zSeed = fmlRandom.nextLong() >> 2 + 1L;
                final long chunkSeed = xSeed * chunkX + zSeed * chunkZ ^ worldSeed;
                fmlRandom.setSeed(chunkSeed);

                if (generatorCoFH != null) {
                    generatorCoFH.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                }
                if (generatorDenseOres != null) {
                    generatorDenseOres.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                }
                if (generatorGCGreg != null) {
                    generatorGCGreg.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                }
                if (generatorAE2meteors != null) {
                    generatorAE2meteors.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                }
                if (generateTCAuraNodes != null) {
                    generateTCAuraNodes.invoke(generatorTCAuraNodes, world, fmlRandom, chunkX, chunkZ, false, true);
                }

            } catch (final Exception e) {
                GCLog.severe("Error in another mod's worldgen.  This is NOT a Galacticraft bug.");
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void toCelestialSelection(EntityPlayerMP player, GCPlayerStats stats, int tier,
        GuiCelestialSelection.MapMode mapMode) {
        player.mountEntity(null);
        stats.spaceshipTier = tier;

        final HashMap<String, Integer> map = WorldUtil.getArrayOfPossibleDimensions(tier, player);
        String dimensionList = "";
        int count = 0;
        for (final Entry<String, Integer> entry : map.entrySet()) {
            dimensionList = dimensionList.concat(
                entry.getKey() + (count < map.entrySet()
                    .size() - 1 ? "?" : ""));
            count++;
        }

        GalacticraftCore.packetPipeline.sendTo(
            new PacketSimple(
                EnumSimplePacket.C_UPDATE_DIMENSION_LIST,
                new Object[] { player.getGameProfile()
                    .getName(), dimensionList, mapMode.ordinal() }),
            player);
        stats.usingPlanetSelectionGui = true;
        stats.currentMapMode = mapMode;
        stats.savedPlanetList = dimensionList;
    }

    public static Vector3 getFootprintPosition(World world, float rotation, Vector3 startPosition,
        BlockVec3 playerCenter) {
        final Vector3 position = startPosition.clone();
        final float footprintScale = 0.375F;

        int mainPosX = position.intX();
        final int mainPosY = position.intY();
        int mainPosZ = position.intZ();

        // If the footprint is hovering over air...
        final Block b1 = world.getBlock(mainPosX, mainPosY, mainPosZ);
        if (b1 != null && b1.isAir(world, mainPosX, mainPosY, mainPosZ)) {
            position.x += playerCenter.x - mainPosX;
            position.z += playerCenter.z - mainPosZ;

            // If the footprint is still over air....
            final Block b2 = world.getBlock(position.intX(), position.intY(), position.intZ());
            if (b2 != null && b2.isAir(world, position.intX(), position.intY(), position.intZ())) {
                for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                    if (direction != ForgeDirection.DOWN && direction != ForgeDirection.UP) {
                        final Block b3 = world
                            .getBlock(mainPosX + direction.offsetX, mainPosY, mainPosZ + direction.offsetZ);
                        if (b3 != null
                            && !b3.isAir(world, mainPosX + direction.offsetX, mainPosY, mainPosZ + direction.offsetZ)) {
                            position.x += direction.offsetX;
                            position.z += direction.offsetZ;
                            break;
                        }
                    }
                }
            }
        }

        mainPosX = position.intX();
        mainPosZ = position.intZ();

        final double x0 = Math.sin((45 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double x1 = Math.sin((135 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double x2 = Math.sin((225 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double x3 = Math.sin((315 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double z0 = Math.cos((45 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;
        final double z1 = Math.cos((135 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;
        final double z2 = Math.cos((225 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;
        final double z3 = Math.cos((315 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;

        final double xMin = Math.min(Math.min(x0, x1), Math.min(x2, x3));
        final double xMax = Math.max(Math.max(x0, x1), Math.max(x2, x3));
        final double zMin = Math.min(Math.min(z0, z1), Math.min(z2, z3));
        final double zMax = Math.max(Math.max(z0, z1), Math.max(z2, z3));

        if (xMin < mainPosX) {
            position.x += mainPosX - xMin;
        }

        if (xMax > mainPosX + 1) {
            position.x -= xMax - (mainPosX + 1);
        }

        if (zMin < mainPosZ) {
            position.z += mainPosZ - zMin;
        }

        if (zMax > mainPosZ + 1) {
            position.z -= zMax - (mainPosZ + 1);
        }

        return position;
    }

    public static String spaceStationDataToString(HashMap<Integer, Integer> data) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<Map.Entry<Integer, Integer>> it = data.entrySet()
            .iterator();
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

    public static HashMap<Integer, Integer> stringToSpaceStationData(String input) {
        final HashMap<Integer, Integer> data = Maps.newHashMap();
        if (!input.isEmpty()) {
            final String[] str0 = input.split("\\?");
            for (final String element : str0) {
                final String[] str1 = element.split("\\$");
                data.put(Integer.parseInt(str1[0]), Integer.parseInt(str1[1]));
            }
        }
        return data;
    }

    public static String getDimensionName(WorldProvider wp) {
        if (wp instanceof IGalacticraftWorldProvider) {
            final CelestialBody cb = ((IGalacticraftWorldProvider) wp).getCelestialBody();
            if (cb != null && !(cb instanceof Satellite)) {
                return cb.getUnlocalizedName();
            }
        }

        if (wp.dimensionId == ConfigManagerCore.idDimensionOverworld) {
            return "Overworld";
        }

        return wp.getDimensionName();
    }

    public static void setNextMorning(WorldServer world) {
        final long current = world.getWorldInfo()
            .getWorldTime();
        long dayLength = 24000L;
        long newTime = current - current % dayLength + dayLength;
        if (world.provider instanceof WorldProviderSpace) {
            dayLength = ((WorldProviderSpace) world.provider).getDayLength();
            if (dayLength <= 0) {
                return;
            }
            newTime = current - current % dayLength + dayLength;
        } else {
            final long diff = newTime - current;
            for (final WorldServer worldServer : MinecraftServer.getServer().worldServers) {
                if (worldServer == world) {
                    continue;
                }
                if (worldServer.provider instanceof WorldProviderSpace) {
                    ((WorldProviderSpace) worldServer.provider).adjustTimeOffset(diff);
                }
            }
        }
        world.provider.setWorldTime(newTime);
    }
}
