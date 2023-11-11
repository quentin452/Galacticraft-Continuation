package micdoodle8.mods.galacticraft.core.util;

import cpw.mods.fml.common.Optional;
import micdoodle8.mods.miccore.*;
import net.minecraft.block.*;
import cpw.mods.fml.common.versioning.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.nbt.*;
import net.minecraft.command.*;
import net.minecraft.server.*;
import net.minecraft.entity.player.*;
import com.mojang.authlib.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.settings.*;
import net.minecraft.client.entity.*;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import com.google.common.collect.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.launchwrapper.*;
import net.minecraft.init.*;

public class VersionUtil
{
    private static DefaultArtifactVersion mcVersion;
    public static boolean mcVersion1_7_2;
    public static boolean mcVersion1_7_10;
    private static boolean deobfuscated;
    private static HashMap<String, MicdoodleTransformer.ObfuscationEntry> nodemap;
    private static HashMap<Integer, Object> reflectionCache;
    private static final String KEY_CLASS_COMPRESSED_STREAM_TOOLS = "compressedStreamTools";
    private static final String KEY_CLASS_NBT_SIZE_TRACKER = "nbtSizeTracker";
    private static final String KEY_CLASS_YGG_CONVERTER = "preYggdrasilConverter";
    private static final String KEY_CLASS_TEXTURE_UTIL = "textureUtil";
    private static final String KEY_CLASS_COMMAND_BASE = "commandBase";
    private static final String KEY_CLASS_SCALED_RES = "scaledResolution";
    private static final String KEY_CLASS_RENDER_PLAYER = "renderPlayer";
    private static final String KEY_CLASS_ENTITYLIST = "entityList";
    private static final String KEY_METHOD_SET_OWNER = "setOwner";
    private static final String KEY_METHOD_GET_OWNER = "getOwnerName";
    private static final String KEY_METHOD_CONVERT_UUID = "yggdrasilConvert";
    private static final String KEY_METHOD_DECOMPRESS_NBT = "decompress";
    private static final String KEY_METHOD_SET_MIPMAP = "setMipMap";
    private static final String KEY_METHOD_NOTIFY_ADMINS = "notifyAdmins";
    private static final String KEY_METHOD_PLAYER_FOR_NAME = "getPlayerForUsername";
    private static final String KEY_METHOD_PLAYER_IS_OPPED = "isPlayerOpped";
    private static final String KEY_METHOD_PLAYER_TEXTURE = "getEntityTexture";
    public static final String KEY_FIELD_FLOATINGTICKCOUNT = "floatingTickCount";
    public static final String KEY_FIELD_BIOMEINDEXLAYER = "biomeIndexLayer";
    public static final String KEY_FIELD_MUSICTICKER = "mcMusicTicker";
    public static final String KEY_FIELD_CAMERA_ZOOM = "cameraZoom";
    public static final String KEY_FIELD_CAMERA_YAW = "cameraYaw";
    public static final String KEY_FIELD_CAMERA_PITCH = "cameraPitch";
    public static final String KEY_FIELD_CLASSTOIDMAPPING = "classToIDMapping";
    public static final String KEY_FIELD_CHUNKCACHE_WORLDOBJ = "chunkCacheWorldObj";
    public static final String KEY_METHOD_ORIENT_CAMERA = "orientCamera";
    public static Block sand;

    public static boolean mcVersionMatches(final String version) {
        return VersionParser.parseRange("[" + version + "]").containsVersion((ArtifactVersion)VersionUtil.mcVersion);
    }

    @Optional.Method(modid = "GalacticraftMars")
    public static void setSlimelingOwner(final EntitySlimeling slimeling, final String ownerName) {
        try {
            Method m = (Method) VersionUtil.reflectionCache.get(0);
            if (m == null) {
                m = slimeling.getClass().getSuperclass().getMethod(getNameDynamic("setOwner"), String.class);
                VersionUtil.reflectionCache.put(0, m);
            }
            m.invoke(slimeling, ownerName);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String getSlimelingOwner(final EntitySlimeling slimeling) {
        try {
            Method m = (Method) VersionUtil.reflectionCache.get(1);
            if (m == null) {
                m = slimeling.getClass().getMethod(getNameDynamic("getOwnerName"), (Class<?>[])new Class[0]);
                VersionUtil.reflectionCache.put(1, m);
            }
            return (String)m.invoke(slimeling, new Object[0]);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return "";
        }
    }

    public static void readSlimelingEggFromNBT(final TileEntitySlimelingEgg egg, final NBTTagCompound nbt) {
        try {
            String s = "";
            if (nbt.hasKey("OwnerUUID", 8)) {
                s = nbt.getString("OwnerUUID");
            }
            else if (VersionUtil.mcVersion1_7_10) {
                Method m =(Method)  VersionUtil.reflectionCache.get(2);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("preYggdrasilConverter").replace('/', '.'));
                    m = c.getMethod(getNameDynamic("yggdrasilConvert"), String.class);
                    VersionUtil.reflectionCache.put(2, m);
                }
                final String s2 = nbt.getString("Owner");
                s = (String)m.invoke(null, s2);
            }
            if (s.length() > 0) {
                egg.lastTouchedPlayerUUID = s;
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static NBTTagCompound decompressNBT(final byte[] compressedNBT) {
        try {
            if (VersionUtil.mcVersion1_7_10) {
                Class<?> c0 = (Class<?>) VersionUtil.reflectionCache.get(4);
                Method m = (Method) VersionUtil.reflectionCache.get(6);
                if (c0 == null) {
                    c0 = Class.forName(getNameDynamic("nbtSizeTracker").replace('/', '.'));
                    VersionUtil.reflectionCache.put(4, c0);
                }
                if (m == null) {
                    final Class<?> c2 = Class.forName(getNameDynamic("compressedStreamTools").replace('/', '.'));
                    m = c2.getMethod(getNameDynamic("decompress"), byte[].class, c0);
                    VersionUtil.reflectionCache.put(6, m);
                }
                final Object nbtSizeTracker = c0.getConstructor(Long.TYPE).newInstance(2097152L);
                return (NBTTagCompound)m.invoke(null, compressedNBT, nbtSizeTracker);
            }
            if (VersionUtil.mcVersion1_7_2) {
                Method i =(Method)  VersionUtil.reflectionCache.get(6);
                if (i == null) {
                    final Class<?> c3 = Class.forName(getNameDynamic("compressedStreamTools").replace('/', '.'));
                    i = c3.getMethod(getNameDynamic("decompress"), byte[].class);
                    VersionUtil.reflectionCache.put(6, i);
                }
                return (NBTTagCompound)i.invoke(null, compressedNBT);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static void setMipMap(final boolean b0, final boolean b1) {
        try {
            if (VersionUtil.mcVersion1_7_10) {
                Method m =(Method)  VersionUtil.reflectionCache.get(8);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("textureUtil").replace('/', '.'));
                    m = c.getMethod(getNameDynamic("setMipMap"), Boolean.TYPE, Boolean.TYPE, Float.TYPE);
                    VersionUtil.reflectionCache.put(8, m);
                }
                m.invoke(null, b0, b1, 1.0f);
            }
            else if (VersionUtil.mcVersion1_7_2) {
                Method m =(Method)  VersionUtil.reflectionCache.get(8);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("textureUtil").replace('/', '.'));
                    m = c.getMethod(getNameDynamic("setMipMap"), Boolean.TYPE, Boolean.TYPE);
                    VersionUtil.reflectionCache.put(8, m);
                }
                m.invoke(null, b0, b1);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void notifyAdmins(final ICommandSender sender, final ICommand command, final String name, final Object... objects) {
        try {
            if (VersionUtil.mcVersion1_7_10) {
                Method m =(Method)  VersionUtil.reflectionCache.get(10);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("commandBase").replace('/', '.'));
                    m = c.getMethod(getNameDynamic("notifyAdmins"), ICommandSender.class, ICommand.class, String.class, Object[].class);
                    VersionUtil.reflectionCache.put(10, m);
                }
                m.invoke(null, sender, command, name, objects);
            }
            else if (VersionUtil.mcVersion1_7_2) {
                Method m =(Method)  VersionUtil.reflectionCache.get(10);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("commandBase").replace('/', '.'));
                    m = c.getMethod(getNameDynamic("notifyAdmins"), ICommandSender.class, String.class, Object[].class);
                    VersionUtil.reflectionCache.put(10, m);
                }
                m.invoke(null, sender, name, objects);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static EntityPlayerMP getPlayerForUsername(final MinecraftServer server, final String username) {
        try {
            Method m =(Method)  VersionUtil.reflectionCache.get(12);
            if (m == null) {
                final Class<?> c = server.getConfigurationManager().getClass();
                m = c.getMethod(getNameDynamic("getPlayerForUsername"), String.class);
                VersionUtil.reflectionCache.put(12, m);
            }
            return (EntityPlayerMP)m.invoke(server.getConfigurationManager(), username);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static boolean isPlayerOpped(final EntityPlayerMP player) {
        try {
            if (VersionUtil.mcVersion1_7_10) {
                Method m =(Method)  VersionUtil.reflectionCache.get(14);
                if (m == null) {
                    final Class<?> c = player.mcServer.getConfigurationManager().getClass();
                    m = c.getMethod(getNameDynamic("isPlayerOpped"), GameProfile.class);
                    VersionUtil.reflectionCache.put(14, m);
                }
                return (boolean)m.invoke(player.mcServer.getConfigurationManager(), player.getGameProfile());
            }
            if (VersionUtil.mcVersion1_7_2) {
                Method m =(Method)  VersionUtil.reflectionCache.get(14);
                if (m == null) {
                    final Class<?> c = player.mcServer.getConfigurationManager().getClass();
                    m = c.getMethod(getNameDynamic("isPlayerOpped"), String.class);
                    VersionUtil.reflectionCache.put(14, m);
                }
                return (boolean)m.invoke(player.mcServer.getConfigurationManager(), player.getGameProfile().getName());
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static ScaledResolution getScaledRes(final Minecraft mc, final int width, final int height) {
        try {
            if (VersionUtil.mcVersion1_7_10) {
                Constructor m = (Constructor) VersionUtil.reflectionCache.get(16);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("scaledResolution").replace('/', '.'));
                    m = c.getConstructor(Minecraft.class, Integer.TYPE, Integer.TYPE);
                    VersionUtil.reflectionCache.put(16, m);
                }
                return (ScaledResolution) m.newInstance(mc, width, height);
            }
            if (VersionUtil.mcVersion1_7_2) {
                Constructor m = (Constructor) VersionUtil.reflectionCache.get(16);
                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic("scaledResolution").replace('/', '.'));
                    m = c.getConstructor(GameSettings.class, Integer.TYPE, Integer.TYPE);
                    VersionUtil.reflectionCache.put(16, m);
                }
                return (ScaledResolution) m.newInstance(mc.gameSettings, width, height);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Method getPlayerTextureMethod() {
        try {
            Method m =(Method)  VersionUtil.reflectionCache.get(18);
            if (m == null) {
                final Class<?> c = Class.forName(getNameDynamic("renderPlayer").replace('/', '.'));
                m = c.getMethod(getNameDynamic("getEntityTexture"), AbstractClientPlayer.class);
                m.setAccessible(true);
                VersionUtil.reflectionCache.put(18, m);
            }
            return m;
        }
        catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static void putClassToIDMapping(final Class mobClazz, final int id) {
        try {
            final Class<?> c = Class.forName(getNameDynamic("entityList").replace('/', '.'));
            final Field f = c.getDeclaredField(getNameDynamic("classToIDMapping"));
            f.setAccessible(true);
            final Map classToIDMapping = (Map)f.get(null);
            classToIDMapping.put(mobClazz, id);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static int getClassToIDMapping(final Class mobClazz) {
        try {
            final Class<?> c = Class.forName(getNameDynamic("entityList").replace('/', '.'));
            final Field f = c.getDeclaredField(getNameDynamic("classToIDMapping"));
            f.setAccessible(true);
            final Map classToIDMapping = (Map)f.get(null);
            final Integer i = (Integer) classToIDMapping.get(mobClazz);
            return (i != null) ? i : 0;
        }
        catch (Throwable t) {
            t.printStackTrace();
            return 0;
        }
    }

    private static String getName(final String keyName) {
        return VersionUtil.nodemap.get(keyName).name;
    }

    private static String getObfName(final String keyName) {
        return VersionUtil.nodemap.get(keyName).obfuscatedName;
    }

    public static String getNameDynamic(final String keyName) {
        try {
            if (VersionUtil.deobfuscated) {
                return getName(keyName);
            }
            return getObfName(keyName);
        }
        catch (NullPointerException e) {
            System.err.println("Could not find key: " + keyName);
            throw e;
        }
    }

    public static GameProfile constructGameProfile(final UUID uuid, final String strName) {
        try {
            Class<?> c = (Class<?>) VersionUtil.reflectionCache.get(19);
            if (c == null) {
                c = Class.forName("com.mojang.authlib.GameProfile");
                VersionUtil.reflectionCache.put(19, c);
            }
            if (VersionUtil.mcVersion1_7_10) {
                return (GameProfile)c.getConstructor(UUID.class, String.class).newInstance(uuid, strName);
            }
            if (VersionUtil.mcVersion1_7_2) {
                return (GameProfile)c.getConstructor(String.class, String.class).newInstance(uuid.toString().replaceAll("-", ""), strName);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static World getWorld(final IBlockAccess world) {
        if (world instanceof World) {
            return (World)world;
        }
        if (world instanceof ChunkCache) {
            try {
                Field f = (Field) VersionUtil.reflectionCache.get(20);
                if (f == null) {
                    final Class c = Class.forName("net.minecraft.world.ChunkCache");
                    f = c.getDeclaredField(getNameDynamic("chunkCacheWorldObj"));
                    f.setAccessible(true);
                    VersionUtil.reflectionCache.put(20, f);
                }
                return (World)f.get(world);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return null;
    }

    public static ItemStack createStack(final Block block, final int meta) {
        try {
            Method m = (Method) VersionUtil.reflectionCache.get(3);
            if (m == null) {
                final Class c = Class.forName("net.minecraft.block.Block");
                final Method[] declaredMethods;
                final Method[] mm = declaredMethods = c.getDeclaredMethods();
                for (final Method testMethod : declaredMethods) {
                    if (testMethod.getName().equals("createStackedBlock")) {
                        m = testMethod;
                        break;
                    }
                }
                m.setAccessible(true);
                VersionUtil.reflectionCache.put(3, m);
            }
            return (ItemStack)m.invoke(block, meta);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    static {
        VersionUtil.mcVersion = null;
        VersionUtil.mcVersion1_7_2 = false;
        VersionUtil.mcVersion1_7_10 = false;
        VersionUtil.deobfuscated = true;
        VersionUtil.nodemap = Maps.newHashMap();
        VersionUtil.reflectionCache = Maps.newHashMap();
        VersionUtil.mcVersion = new DefaultArtifactVersion((String)FMLInjectionData.data()[4]);
        VersionUtil.mcVersion1_7_2 = mcVersionMatches("1.7.2");
        VersionUtil.mcVersion1_7_10 = mcVersionMatches("1.7.10");
        try {
            VersionUtil.deobfuscated = (Launch.classLoader.getClassBytes("net.minecraft.world.World") != null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (VersionUtil.mcVersion1_7_10) {
            VersionUtil.nodemap.put("compressedStreamTools", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/nbt/CompressedStreamTools"));
            VersionUtil.nodemap.put("nbtSizeTracker", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/nbt/NBTSizeTracker"));
            VersionUtil.nodemap.put("preYggdrasilConverter", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/server/management/PreYggdrasilConverter"));
            VersionUtil.nodemap.put("textureUtil", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/client/renderer/texture/TextureUtil"));
            VersionUtil.nodemap.put("commandBase", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/command/CommandBase"));
            VersionUtil.nodemap.put("scaledResolution", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/client/gui/ScaledResolution"));
            VersionUtil.nodemap.put("renderPlayer", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/client/renderer/entity/RenderPlayer"));
            VersionUtil.nodemap.put("entityList", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/entity/EntityList"));
            VersionUtil.nodemap.put("setOwner", new MicdoodleTransformer.MethodObfuscationEntry("func_152115_b", "func_152115_b", ""));
            VersionUtil.nodemap.put("getOwnerName", new MicdoodleTransformer.MethodObfuscationEntry("func_152113_b", "func_152113_b", ""));
            VersionUtil.nodemap.put("yggdrasilConvert", new MicdoodleTransformer.MethodObfuscationEntry("func_152719_a", "func_152719_a", ""));
            VersionUtil.nodemap.put("decompress", new MicdoodleTransformer.MethodObfuscationEntry("func_152457_a", "func_152457_a", ""));
            VersionUtil.nodemap.put("setMipMap", new MicdoodleTransformer.MethodObfuscationEntry("func_152777_a", "func_152777_a", ""));
            VersionUtil.nodemap.put("notifyAdmins", new MicdoodleTransformer.MethodObfuscationEntry("func_152373_a", "func_152373_a", ""));
            VersionUtil.nodemap.put("getPlayerForUsername", new MicdoodleTransformer.MethodObfuscationEntry("func_152612_a", "func_152612_a", ""));
            VersionUtil.nodemap.put("isPlayerOpped", new MicdoodleTransformer.MethodObfuscationEntry("func_152596_g", "func_152596_g", ""));
            VersionUtil.nodemap.put("getEntityTexture", new MicdoodleTransformer.MethodObfuscationEntry("getEntityTexture", "getEntityTexture", ""));
            VersionUtil.sand = (Block)Blocks.sand;
        }
        else if (VersionUtil.mcVersion1_7_2) {
            VersionUtil.nodemap.put("compressedStreamTools", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/nbt/CompressedStreamTools"));
            VersionUtil.nodemap.put("nbtSizeTracker", new MicdoodleTransformer.ObfuscationEntry("", ""));
            VersionUtil.nodemap.put("preYggdrasilConverter", new MicdoodleTransformer.ObfuscationEntry("", ""));
            VersionUtil.nodemap.put("textureUtil", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/client/renderer/texture/TextureUtil"));
            VersionUtil.nodemap.put("commandBase", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/command/CommandBase"));
            VersionUtil.nodemap.put("scaledResolution", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/client/gui/ScaledResolution"));
            VersionUtil.nodemap.put("renderPlayer", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/client/renderer/entity/RenderPlayer"));
            VersionUtil.nodemap.put("entityList", new MicdoodleTransformer.ObfuscationEntry("net/minecraft/entity/EntityList"));
            VersionUtil.nodemap.put("setOwner", new MicdoodleTransformer.MethodObfuscationEntry("setOwner", "setOwner", ""));
            VersionUtil.nodemap.put("getOwnerName", new MicdoodleTransformer.MethodObfuscationEntry("getOwnerName", "getOwnerName", ""));
            VersionUtil.nodemap.put("yggdrasilConvert", new MicdoodleTransformer.MethodObfuscationEntry("", "", ""));
            VersionUtil.nodemap.put("decompress", new MicdoodleTransformer.MethodObfuscationEntry("decompress", "decompress", ""));
            VersionUtil.nodemap.put("setMipMap", new MicdoodleTransformer.MethodObfuscationEntry("func_147950_a", "func_147950_a", ""));
            VersionUtil.nodemap.put("notifyAdmins", new MicdoodleTransformer.MethodObfuscationEntry("notifyAdmins", "notifyAdmins", ""));
            VersionUtil.nodemap.put("getPlayerForUsername", new MicdoodleTransformer.MethodObfuscationEntry("getPlayerForUsername", "getPlayerForUsername", ""));
            VersionUtil.nodemap.put("isPlayerOpped", new MicdoodleTransformer.MethodObfuscationEntry("isPlayerOpped", "isPlayerOpped", ""));
            VersionUtil.nodemap.put("getEntityTexture", new MicdoodleTransformer.MethodObfuscationEntry("getEntityTexture", "getEntityTexture", ""));
            try {
                final Field sandField = Blocks.class.getField(VersionUtil.deobfuscated ? "sand" : "sand");
                VersionUtil.sand = (Block)sandField.get(null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        VersionUtil.nodemap.put("floatingTickCount", new MicdoodleTransformer.ObfuscationEntry("floatingTickCount", "floatingTickCount"));
        VersionUtil.nodemap.put("biomeIndexLayer", new MicdoodleTransformer.ObfuscationEntry("biomeIndexLayer", "biomeIndexLayer"));
        VersionUtil.nodemap.put("mcMusicTicker", new MicdoodleTransformer.ObfuscationEntry("mcMusicTicker", "mcMusicTicker"));
        VersionUtil.nodemap.put("cameraZoom", new MicdoodleTransformer.FieldObfuscationEntry("cameraZoom", "cameraZoom"));
        VersionUtil.nodemap.put("cameraYaw", new MicdoodleTransformer.FieldObfuscationEntry("cameraYaw", "cameraYaw"));
        VersionUtil.nodemap.put("cameraPitch", new MicdoodleTransformer.FieldObfuscationEntry("cameraPitch", "cameraPitch"));
        VersionUtil.nodemap.put("classToIDMapping", new MicdoodleTransformer.FieldObfuscationEntry("classToIDMapping", "classToIDMapping"));
        VersionUtil.nodemap.put("chunkCacheWorldObj", new MicdoodleTransformer.FieldObfuscationEntry("worldObj", "worldObj"));
        VersionUtil.nodemap.put("orientCamera", new MicdoodleTransformer.MethodObfuscationEntry("orientCamera", "orientCamera", ""));
    }
}
