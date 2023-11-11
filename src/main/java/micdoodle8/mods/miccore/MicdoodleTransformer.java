package micdoodle8.mods.miccore;

import cpw.mods.fml.relauncher.*;
import net.minecraft.launchwrapper.*;
import cpw.mods.fml.common.*;
import java.util.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.*;
import cpw.mods.fml.common.versioning.*;

@IFMLLoadingPlugin.TransformerExclusions({ "micdoodle8.mods.miccore" })
public class MicdoodleTransformer implements IClassTransformer
{
    HashMap<String, ObfuscationEntry> nodemap;
    private boolean deobfuscated;
    private boolean optifinePresent;
    private boolean isServer;
    private boolean playerApiActive;
    private DefaultArtifactVersion mcVersion;
    private String nameForgeHooksClient;
    private String nameConfManager;
    private String namePlayerController;
    private String nameEntityLiving;
    private String nameEntityItem;
    private String nameEntityRenderer;
    private String nameItemRenderer;
    private String nameGuiSleep;
    private String nameEffectRenderer;
    private String nameNetHandlerPlay;
    private String nameWorldRenderer;
    private String nameRenderGlobal;
    private String nameRenderManager;
    private String nameTileEntityRenderer;
    private String nameEntity;
    private String nameChunkProviderServer;
    private String nameEntityArrow;
    private String nameRendererLivingEntity;
    private String nameEntityGolem;
    private String nameWorld;
    private static final String KEY_CLASS_PLAYER_MP = "PlayerMP";
    private static final String KEY_CLASS_WORLD = "worldClass";
    private static final String KEY_CLASS_CONF_MANAGER = "confManagerClass";
    private static final String KEY_CLASS_GAME_PROFILE = "gameProfileClass";
    private static final String KEY_CLASS_ITEM_IN_WORLD_MANAGER = "itemInWorldManagerClass";
    private static final String KEY_CLASS_PLAYER_CONTROLLER = "playerControllerClass";
    private static final String KEY_CLASS_PLAYER_SP = "playerClient";
    private static final String KEY_CLASS_STAT_FILE_WRITER = "statFileWriterClass";
    private static final String KEY_CLASS_NET_HANDLER_PLAY = "netHandlerPlayClientClass";
    private static final String KEY_CLASS_ENTITY_LIVING = "entityLivingClass";
    private static final String KEY_CLASS_ENTITY_ITEM = "entityItemClass";
    private static final String KEY_CLASS_ENTITY_RENDERER = "entityRendererClass";
    private static final String KEY_CLASS_WORLD_RENDERER = "worldRendererClass";
    private static final String KEY_CLASS_RENDER_GLOBAL = "renderGlobalClass";
    private static final String KEY_CLASS_RENDER_MANAGER = "renderManagerClass";
    private static final String KEY_CLASS_TESSELLATOR = "tessellatorClass";
    private static final String KEY_CLASS_TILEENTITY_RENDERER = "tileEntityRendererClass";
    private static final String KEY_CLASS_CONTAINER_PLAYER = "containerPlayer";
    private static final String KEY_CLASS_MINECRAFT = "minecraft";
    private static final String KEY_CLASS_SESSION = "session";
    private static final String KEY_CLASS_GUI_SCREEN = "guiScreen";
    private static final String KEY_CLASS_ITEM_RENDERER = "itemRendererClass";
    private static final String KEY_CLASS_VEC3 = "vecClass";
    private static final String KEY_CLASS_ENTITY = "entityClass";
    private static final String KEY_CLASS_TILEENTITY = "tileEntityClass";
    private static final String KEY_CLASS_GUI_SLEEP = "guiSleepClass";
    private static final String KEY_CLASS_EFFECT_RENDERER = "effectRendererClass";
    private static final String KEY_CLASS_FORGE_HOOKS_CLIENT = "forgeHooks";
    private static final String KEY_CLASS_CUSTOM_PLAYER_MP = "customPlayerMP";
    private static final String KEY_CLASS_CUSTOM_PLAYER_SP = "customPlayerSP";
    private static final String KEY_CLASS_CUSTOM_OTHER_PLAYER = "customEntityOtherPlayer";
    private static final String KEY_CLASS_PACKET_SPAWN_PLAYER = "packetSpawnPlayer";
    private static final String KEY_CLASS_ENTITY_OTHER_PLAYER = "entityOtherPlayer";
    private static final String KEY_CLASS_SERVER = "minecraftServer";
    private static final String KEY_CLASS_WORLD_SERVER = "worldServer";
    private static final String KEY_CLASS_WORLD_CLIENT = "worldClient";
    private static final String KEY_CLASS_CHUNK_PROVIDER_SERVER = "chunkProviderServer";
    private static final String KEY_CLASS_ICHUNKPROVIDER = "IChunkProvider";
    private static final String KEY_NET_HANDLER_LOGIN_SERVER = "netHandlerLoginServer";
    private static final String KEY_CLASS_ENTITY_ARROW = "entityArrow";
    private static final String KEY_CLASS_RENDERER_LIVING_ENTITY = "rendererLivingEntity";
    private static final String KEY_CLASS_ENTITYGOLEM = "entityGolem";
    private static final String KEY_FIELD_THE_PLAYER = "thePlayer";
    private static final String KEY_FIELD_WORLDRENDERER_GLRENDERLIST = "glRenderList";
    private static final String KEY_FIELD_CPS_WORLDOBJ = "cps_worldObj";
    private static final String KEY_FIELD_CPS_CURRENT_CHUNKPROV = "CurrentChunkProvider";
    private static final String KEY_METHOD_CREATE_PLAYER = "createPlayerMethod";
    private static final String KEY_METHOD_RESPAWN_PLAYER = "respawnPlayerMethod";
    private static final String KEY_METHOD_CREATE_CLIENT_PLAYER = "createClientPlayerMethod";
    private static final String KEY_METHOD_MOVE_ENTITY = "moveEntityMethod";
    private static final String KEY_METHOD_ON_UPDATE = "onUpdateMethod";
    private static final String KEY_METHOD_UPDATE_LIGHTMAP = "updateLightmapMethod";
    private static final String KEY_METHOD_RENDER_OVERLAYS = "renderOverlaysMethod";
    private static final String KEY_METHOD_UPDATE_FOG_COLOR = "updateFogColorMethod";
    private static final String KEY_METHOD_GET_FOG_COLOR = "getFogColorMethod";
    private static final String KEY_METHOD_GET_SKY_COLOR = "getSkyColorMethod";
    private static final String KEY_METHOD_WAKE_ENTITY = "wakeEntityMethod";
    private static final String KEY_METHOD_BED_ORIENT_CAMERA = "orientBedCamera";
    private static final String KEY_METHOD_RENDER_PARTICLES = "renderParticlesMethod";
    private static final String KEY_METHOD_CUSTOM_PLAYER_MP = "customPlayerMPConstructor";
    private static final String KEY_METHOD_CUSTOM_PLAYER_SP = "customPlayerSPConstructor";
    private static final String KEY_METHOD_ATTEMPT_LOGIN_BUKKIT = "attemptLoginMethodBukkit";
    private static final String KEY_METHOD_HANDLE_SPAWN_PLAYER = "handleSpawnPlayerMethod";
    private static final String KEY_METHOD_ORIENT_CAMERA = "orientCamera";
    private static final String KEY_METHOD_RENDERMANAGER = "renderManagerMethod";
    private static final String KEY_METHOD_PRERENDER_BLOCKS = "preRenderBlocksMethod";
    private static final String KEY_METHOD_SETUP_GL = "setupGLTranslationMethod";
    private static final String KEY_METHOD_SET_POSITION = "setPositionMethod";
    private static final String KEY_METHOD_WORLDRENDERER_UPDATERENDERER = "updateRendererMethod";
    private static final String KEY_METHOD_LOAD_RENDERERS = "loadRenderersMethod";
    private static final String KEY_METHOD_RENDERGLOBAL_INIT = "renderGlobalInitMethod";
    private static final String KEY_METHOD_RENDERGLOBAL_SORTANDRENDER = "sortAndRenderMethod";
    private static final String KEY_METHOD_TESSELLATOR_ADDVERTEX = "addVertexMethod";
    private static final String KEY_METHOD_TILERENDERER_RENDERTILEAT = "renderTileAtMethod";
    private static final String KEY_METHOD_START_GAME = "startGame";
    private static final String KEY_METHOD_CAN_RENDER_FIRE = "canRenderOnFire";
    private static final String KEY_METHOD_CGS_POPULATE = "CGSpopulate";
    private static final String KEY_METHOD_RENDER_MODEL = "renderModel";
    private static final String KEY_METHOD_RAIN_STRENGTH = "getRainStrength";
    private static final String KEY_METHOD_REGISTEROF = "registerOF";
    private static final String CLASS_RUNTIME_INTERFACE = "micdoodle8/mods/miccore/Annotations$RuntimeInterface";
    private static final String CLASS_ALT_FORVERSION = "micdoodle8/mods/miccore/Annotations$AltForVersion";
    private static final String CLASS_VERSION_SPECIFIC = "micdoodle8/mods/miccore/Annotations$VersionSpecific";
    private static final String CLASS_MICDOODLE_PLUGIN = "micdoodle8/mods/miccore/MicdoodlePlugin";
    private static final String CLASS_CLIENT_PROXY_MAIN = "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore";
    private static final String CLASS_WORLD_UTIL = "micdoodle8/mods/galacticraft/core/util/WorldUtil";
    private static final String CLASS_GL11 = "org/lwjgl/opengl/GL11";
    private static final String CLASS_RENDER_PLAYER_GC = "micdoodle8/mods/galacticraft/core/client/render/entities/RenderPlayerGC";
    private static final String CLASS_IENTITYBREATHABLE = "micdoodle8/mods/galacticraft/api/entity/IEntityBreathable";
    private static final String CLASS_SYNCMOD_CLONEPLAYER = "sync/common/tileentity/TileEntityDualVertical";
    private static final String CLASS_RENDERPLAYEROF = "RenderPlayerOF";
    private static int operationCount;
    private static int injectionCount;

    public MicdoodleTransformer() {
        this.nodemap = new HashMap<String, ObfuscationEntry>();
        this.deobfuscated = true;
        this.mcVersion = new DefaultArtifactVersion((String)FMLInjectionData.data()[4]);
        try {
            this.deobfuscated = (Launch.classLoader.getClassBytes("net.minecraft.world.World") != null);
            this.optifinePresent = (Launch.classLoader.getClassBytes("CustomColorizer") != null);
            this.playerApiActive = (Launch.classLoader.getClassBytes("api.player.forge.PlayerAPITransformer") != null);
        }
        catch (Exception ex) {}
        Launch.classLoader.addTransformerExclusion("micdoodle8/mods/galacticraft/api/entity/IEntityBreathable".replace('/', '.'));
        if (this.mcVersionMatches("[1.7.2]")) {
            this.nodemap.put("PlayerMP", new ObfuscationEntry("net/minecraft/entity/player/EntityPlayerMP", "mm"));
            this.nodemap.put("worldClass", new ObfuscationEntry("net/minecraft/world/World", "afn"));
            this.nodemap.put("confManagerClass", new ObfuscationEntry("net/minecraft/server/management/ServerConfigurationManager", "ld"));
            this.nodemap.put("gameProfileClass", new ObfuscationEntry("com/mojang/authlib/GameProfile"));
            this.nodemap.put("itemInWorldManagerClass", new ObfuscationEntry("net/minecraft/server/management/ItemInWorldManager", "mn"));
            this.nodemap.put("playerControllerClass", new ObfuscationEntry("net/minecraft/client/multiplayer/PlayerControllerMP", "biy"));
            this.nodemap.put("playerClient", new ObfuscationEntry("net/minecraft/client/entity/EntityClientPlayerMP", "bje"));
            this.nodemap.put("statFileWriterClass", new ObfuscationEntry("net/minecraft/stats/StatFileWriter", "oe"));
            this.nodemap.put("netHandlerPlayClientClass", new ObfuscationEntry("net/minecraft/client/network/NetHandlerPlayClient", "biv"));
            this.nodemap.put("entityLivingClass", new ObfuscationEntry("net/minecraft/entity/EntityLivingBase", "rh"));
            this.nodemap.put("entityItemClass", new ObfuscationEntry("net/minecraft/entity/item/EntityItem", "vw"));
            this.nodemap.put("entityRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/EntityRenderer", "bll"));
            this.nodemap.put("worldRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/WorldRenderer", "blg"));
            this.nodemap.put("renderGlobalClass", new ObfuscationEntry("net/minecraft/client/renderer/RenderGlobal", "bls"));
            this.nodemap.put("tessellatorClass", new ObfuscationEntry("net/minecraft/client/renderer/Tessellator", "blz"));
            this.nodemap.put("renderManagerClass", new ObfuscationEntry("net/minecraft/client/renderer/entity/RenderManager", "bnf"));
            this.nodemap.put("tileEntityRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "bmc"));
            this.nodemap.put("containerPlayer", new ObfuscationEntry("net/minecraft/inventory/ContainerPlayer", "zb"));
            this.nodemap.put("minecraft", new ObfuscationEntry("net/minecraft/client/Minecraft", "azd"));
            this.nodemap.put("session", new ObfuscationEntry("net/minecraft/util/Session", "baf"));
            this.nodemap.put("guiScreen", new ObfuscationEntry("net/minecraft/client/gui/GuiScreen", "bcd"));
            this.nodemap.put("itemRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/ItemRenderer", "blq"));
            this.nodemap.put("vecClass", new ObfuscationEntry("net/minecraft/util/Vec3", "ayk"));
            this.nodemap.put("entityClass", new ObfuscationEntry("net/minecraft/entity/Entity", "qn"));
            this.nodemap.put("guiSleepClass", new ObfuscationEntry("net/minecraft/client/gui/GuiSleepMP", "bbp"));
            this.nodemap.put("effectRendererClass", new ObfuscationEntry("net/minecraft/client/particle/EffectRenderer", "bkg"));
            this.nodemap.put("forgeHooks", new ObfuscationEntry("net/minecraftforge/client/ForgeHooksClient"));
            this.nodemap.put("customPlayerMP", new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityPlayerMP"));
            this.nodemap.put("customPlayerSP", new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityClientPlayerMP"));
            this.nodemap.put("customEntityOtherPlayer", new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityOtherPlayerMP"));
            this.nodemap.put("packetSpawnPlayer", new ObfuscationEntry("net/minecraft/network/play/server/S0CPacketSpawnPlayer", "fs"));
            this.nodemap.put("entityOtherPlayer", new ObfuscationEntry("net/minecraft/client/entity/EntityOtherPlayerMP", "bld"));
            this.nodemap.put("minecraftServer", new ObfuscationEntry("net/minecraft/server/MinecraftServer"));
            this.nodemap.put("worldServer", new ObfuscationEntry("net/minecraft/world/WorldServer", "mj"));
            this.nodemap.put("worldClient", new ObfuscationEntry("net/minecraft/client/multiplayer/WorldClient", "biz"));
            this.nodemap.put("tileEntityClass", new ObfuscationEntry("net/minecraft/tileentity/TileEntity", "and"));
            this.nodemap.put("chunkProviderServer", new ObfuscationEntry("net/minecraft/world/gen/ChunkProviderServer", "mi"));
            this.nodemap.put("IChunkProvider", new ObfuscationEntry("IChunkProvider", "aog"));
            this.nodemap.put("netHandlerLoginServer", new ObfuscationEntry("net/minecraft/server/network/NetHandlerLoginServer", "nd"));
            this.nodemap.put("entityArrow", new ObfuscationEntry("net/minecraft/entity/projectile/EntityArrow", "xo"));
            this.nodemap.put("rendererLivingEntity", new ObfuscationEntry("net/minecraft/client/renderer/entity/RendererLivingEntity", "bnz"));
            this.nodemap.put("entityGolem", new ObfuscationEntry("net/minecraft/entity/monster/EntityGolem", "ux"));
            this.nodemap.put("thePlayer", new FieldObfuscationEntry("thePlayer", "h"));
            this.nodemap.put("glRenderList", new FieldObfuscationEntry("glRenderList", "z"));
            this.nodemap.put("cps_worldObj", new FieldObfuscationEntry("worldObj", "i"));
            this.nodemap.put("CurrentChunkProvider", new FieldObfuscationEntry("currentChunkProvider", "e"));
            this.nodemap.put("createPlayerMethod", new MethodObfuscationEntry("createPlayerForUser", "a", "(L" + this.getNameDynamic("gameProfileClass") + ";)L" + this.getNameDynamic("PlayerMP") + ";"));
            this.nodemap.put("respawnPlayerMethod", new MethodObfuscationEntry("respawnPlayer", "a", "(L" + this.getNameDynamic("PlayerMP") + ";IZ)L" + this.getNameDynamic("PlayerMP") + ";"));
            this.nodemap.put("createClientPlayerMethod", new MethodObfuscationEntry("func_147493_a", "a", "(L" + this.getNameDynamic("worldClass") + ";L" + this.getNameDynamic("statFileWriterClass") + ";)L" + this.getNameDynamic("playerClient") + ";"));
            this.nodemap.put("moveEntityMethod", new MethodObfuscationEntry("moveEntityWithHeading", "e", "(FF)V"));
            this.nodemap.put("onUpdateMethod", new MethodObfuscationEntry("onUpdate", "h", "()V"));
            this.nodemap.put("updateLightmapMethod", new MethodObfuscationEntry("updateLightmap", "h", "(F)V"));
            this.nodemap.put("renderOverlaysMethod", new MethodObfuscationEntry("renderOverlays", "b", "(F)V"));
            this.nodemap.put("updateFogColorMethod", new MethodObfuscationEntry("updateFogColor", "i", "(F)V"));
            this.nodemap.put("getFogColorMethod", new MethodObfuscationEntry("getFogColor", "f", "(F)L" + this.getNameDynamic("vecClass") + ";"));
            this.nodemap.put("getSkyColorMethod", new MethodObfuscationEntry("getSkyColor", "a", "(L" + this.getNameDynamic("entityClass") + ";F)L" + this.getNameDynamic("vecClass") + ";"));
            this.nodemap.put("wakeEntityMethod", new MethodObfuscationEntry("func_146418_g", "g", "()V"));
            this.nodemap.put("orientBedCamera", new MethodObfuscationEntry("orientBedCamera", "(L" + this.getNameDynamic("minecraft") + ";L" + this.getNameDynamic("entityLivingClass") + ";)V"));
            this.nodemap.put("renderParticlesMethod", new MethodObfuscationEntry("renderParticles", "a", "(L" + this.getNameDynamic("entityClass") + ";F)V"));
            this.nodemap.put("customPlayerMPConstructor", new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic("minecraftServer") + ";L" + this.getNameDynamic("worldServer") + ";L" + this.getNameDynamic("gameProfileClass") + ";L" + this.getNameDynamic("itemInWorldManagerClass") + ";)V"));
            this.nodemap.put("customPlayerSPConstructor", new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic("minecraft") + ";L" + this.getNameDynamic("worldClass") + ";L" + this.getNameDynamic("session") + ";L" + this.getNameDynamic("netHandlerPlayClientClass") + ";L" + this.getNameDynamic("statFileWriterClass") + ";)V"));
            this.nodemap.put("handleSpawnPlayerMethod", new MethodObfuscationEntry("handleSpawnPlayer", "a", "(L" + this.getNameDynamic("packetSpawnPlayer") + ";)V"));
            this.nodemap.put("orientCamera", new MethodObfuscationEntry("orientCamera", "g", "(F)V"));
            this.nodemap.put("renderManagerMethod", new MethodObfuscationEntry("func_147939_a", "a", "(L" + this.getNameDynamic("entityClass") + ";DDDFFZ)Z"));
            this.nodemap.put("setupGLTranslationMethod", new MethodObfuscationEntry("setupGLTranslation", "f", "()V"));
            this.nodemap.put("preRenderBlocksMethod", new MethodObfuscationEntry("preRenderBlocks", "b", "(I)V"));
            this.nodemap.put("setPositionMethod", new MethodObfuscationEntry("setPosition", "a", "(III)V"));
            this.nodemap.put("updateRendererMethod", new MethodObfuscationEntry("updateRenderer", "a", "(L" + this.getNameDynamic("entityLivingClass") + ";)V"));
            this.nodemap.put("loadRenderersMethod", new MethodObfuscationEntry("loadRenderers", "a", "()V"));
            this.nodemap.put("renderGlobalInitMethod", new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic("minecraft") + ";)V"));
            this.nodemap.put("sortAndRenderMethod", new MethodObfuscationEntry("sortAndRender", "a", "(L" + this.getNameDynamic("entityLivingClass") + ";ID)I"));
            this.nodemap.put("addVertexMethod", new MethodObfuscationEntry("addVertex", "a", "(DDD)V"));
            this.nodemap.put("renderTileAtMethod", new MethodObfuscationEntry("renderTileEntityAt", "a", "(L" + this.getNameDynamic("tileEntityClass") + ";DDDF)V"));
            this.nodemap.put("startGame", new MethodObfuscationEntry("startGame", "Z", "()V"));
            this.nodemap.put("canRenderOnFire", new MethodObfuscationEntry("canRenderOnFire", "aA", "()Z"));
            this.nodemap.put("CGSpopulate", new MethodObfuscationEntry("populate", "a", "(Laog;II)V"));
            this.nodemap.put("attemptLoginMethodBukkit", new MethodObfuscationEntry("attemptLogin", "attemptLogin", "(L" + this.getNameDynamic("netHandlerLoginServer") + ";L" + this.getNameDynamic("gameProfileClass") + ";Ljava/lang/String;)L" + this.getNameDynamic("PlayerMP") + ";"));
            this.nodemap.put("renderModel", new MethodObfuscationEntry("renderModel", "a", "(L" + this.getNameDynamic("entityLivingClass") + ";FFFFFF)V"));
            this.nodemap.put("getRainStrength", new MethodObfuscationEntry("getRainStrength", "j", "(F)F"));
        }
        else if (this.mcVersionMatches("[1.7.10]")) {
            this.nodemap.put("PlayerMP", new ObfuscationEntry("net/minecraft/entity/player/EntityPlayerMP", "mw"));
            this.nodemap.put("worldClass", new ObfuscationEntry("net/minecraft/world/World", "ahb"));
            this.nodemap.put("confManagerClass", new ObfuscationEntry("net/minecraft/server/management/ServerConfigurationManager", "oi"));
            this.nodemap.put("gameProfileClass", new ObfuscationEntry("com/mojang/authlib/GameProfile"));
            this.nodemap.put("itemInWorldManagerClass", new ObfuscationEntry("net/minecraft/server/management/ItemInWorldManager", "mx"));
            this.nodemap.put("playerControllerClass", new ObfuscationEntry("net/minecraft/client/multiplayer/PlayerControllerMP", "bje"));
            this.nodemap.put("playerClient", new ObfuscationEntry("net/minecraft/client/entity/EntityClientPlayerMP", "bjk"));
            this.nodemap.put("statFileWriterClass", new ObfuscationEntry("net/minecraft/stats/StatFileWriter", "pq"));
            this.nodemap.put("netHandlerPlayClientClass", new ObfuscationEntry("net/minecraft/client/network/NetHandlerPlayClient", "bjb"));
            this.nodemap.put("entityLivingClass", new ObfuscationEntry("net/minecraft/entity/EntityLivingBase", "sv"));
            this.nodemap.put("entityItemClass", new ObfuscationEntry("net/minecraft/entity/item/EntityItem", "xk"));
            this.nodemap.put("entityRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/EntityRenderer", "blt"));
            this.nodemap.put("worldRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/WorldRenderer", "blo"));
            this.nodemap.put("renderGlobalClass", new ObfuscationEntry("net/minecraft/client/renderer/RenderGlobal", "bma"));
            this.nodemap.put("tessellatorClass", new ObfuscationEntry("net/minecraft/client/renderer/Tessellator", "bmh"));
            this.nodemap.put("renderManagerClass", new ObfuscationEntry("net/minecraft/client/renderer/entity/RenderManager", "bnn"));
            this.nodemap.put("tileEntityRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "bmk"));
            this.nodemap.put("containerPlayer", new ObfuscationEntry("net/minecraft/inventory/ContainerPlayer", "aap"));
            this.nodemap.put("minecraft", new ObfuscationEntry("net/minecraft/client/Minecraft", "bao"));
            this.nodemap.put("session", new ObfuscationEntry("net/minecraft/util/Session", "bbs"));
            this.nodemap.put("guiScreen", new ObfuscationEntry("net/minecraft/client/gui/GuiScreen", "bdw"));
            this.nodemap.put("itemRendererClass", new ObfuscationEntry("net/minecraft/client/renderer/ItemRenderer", "bly"));
            this.nodemap.put("vecClass", new ObfuscationEntry("net/minecraft/util/Vec3", "azw"));
            this.nodemap.put("entityClass", new ObfuscationEntry("net/minecraft/entity/Entity", "sa"));
            this.nodemap.put("guiSleepClass", new ObfuscationEntry("net/minecraft/client/gui/GuiSleepMP", "bdi"));
            this.nodemap.put("effectRendererClass", new ObfuscationEntry("net/minecraft/client/particle/EffectRenderer", "bkn"));
            this.nodemap.put("forgeHooks", new ObfuscationEntry("net/minecraftforge/client/ForgeHooksClient"));
            this.nodemap.put("customPlayerMP", new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityPlayerMP"));
            this.nodemap.put("customPlayerSP", new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityClientPlayerMP"));
            this.nodemap.put("customEntityOtherPlayer", new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityOtherPlayerMP"));
            this.nodemap.put("packetSpawnPlayer", new ObfuscationEntry("net/minecraft/network/play/server/S0CPacketSpawnPlayer", "gb"));
            this.nodemap.put("entityOtherPlayer", new ObfuscationEntry("net/minecraft/client/entity/EntityOtherPlayerMP", "bll"));
            this.nodemap.put("minecraftServer", new ObfuscationEntry("net/minecraft/server/MinecraftServer"));
            this.nodemap.put("worldServer", new ObfuscationEntry("net/minecraft/world/WorldServer", "mt"));
            this.nodemap.put("worldClient", new ObfuscationEntry("net/minecraft/client/multiplayer/WorldClient", "bjf"));
            this.nodemap.put("tileEntityClass", new ObfuscationEntry("net/minecraft/tileentity/TileEntity", "aor"));
            this.nodemap.put("chunkProviderServer", new ObfuscationEntry("net/minecraft/world/gen/ChunkProviderServer", "ms"));
            this.nodemap.put("IChunkProvider", new ObfuscationEntry("IChunkProvider", "apu"));
            this.nodemap.put("netHandlerLoginServer", new ObfuscationEntry("net/minecraft/server/network/NetHandlerLoginServer", "nn"));
            this.nodemap.put("entityArrow", new ObfuscationEntry("net/minecraft/entity/projectile/EntityArrow", "zc"));
            this.nodemap.put("rendererLivingEntity", new ObfuscationEntry("net/minecraft/client/renderer/entity/RendererLivingEntity", "boh"));
            this.nodemap.put("entityGolem", new ObfuscationEntry("net/minecraft/entity/monster/EntityGolem", "wl"));
            this.nodemap.put("thePlayer", new FieldObfuscationEntry("thePlayer", "h"));
            this.nodemap.put("glRenderList", new FieldObfuscationEntry("glRenderList", "z"));
            this.nodemap.put("cps_worldObj", new FieldObfuscationEntry("worldObj", "i"));
            this.nodemap.put("CurrentChunkProvider", new FieldObfuscationEntry("currentChunkProvider", "e"));
            this.nodemap.put("createPlayerMethod", new MethodObfuscationEntry("createPlayerForUser", "f", "(L" + this.getNameDynamic("gameProfileClass") + ";)L" + this.getNameDynamic("PlayerMP") + ";"));
            this.nodemap.put("respawnPlayerMethod", new MethodObfuscationEntry("respawnPlayer", "a", "(L" + this.getNameDynamic("PlayerMP") + ";IZ)L" + this.getNameDynamic("PlayerMP") + ";"));
            this.nodemap.put("createClientPlayerMethod", new MethodObfuscationEntry("func_147493_a", "a", "(L" + this.getNameDynamic("worldClass") + ";L" + this.getNameDynamic("statFileWriterClass") + ";)L" + this.getNameDynamic("playerClient") + ";"));
            this.nodemap.put("moveEntityMethod", new MethodObfuscationEntry("moveEntityWithHeading", "e", "(FF)V"));
            this.nodemap.put("onUpdateMethod", new MethodObfuscationEntry("onUpdate", "h", "()V"));
            this.nodemap.put("updateLightmapMethod", new MethodObfuscationEntry("updateLightmap", "i", "(F)V"));
            this.nodemap.put("renderOverlaysMethod", new MethodObfuscationEntry("renderOverlays", "b", "(F)V"));
            this.nodemap.put("updateFogColorMethod", new MethodObfuscationEntry("updateFogColor", "j", "(F)V"));
            this.nodemap.put("getFogColorMethod", new MethodObfuscationEntry("getFogColor", "f", "(F)L" + this.getNameDynamic("vecClass") + ";"));
            this.nodemap.put("getSkyColorMethod", new MethodObfuscationEntry("getSkyColor", "a", "(L" + this.getNameDynamic("entityClass") + ";F)L" + this.getNameDynamic("vecClass") + ";"));
            this.nodemap.put("wakeEntityMethod", new MethodObfuscationEntry("func_146418_g", "f", "()V"));
            this.nodemap.put("orientBedCamera", new MethodObfuscationEntry("orientBedCamera", "(L" + this.getNameDynamic("minecraft") + ";L" + this.getNameDynamic("entityLivingClass") + ";)V"));
            this.nodemap.put("renderParticlesMethod", new MethodObfuscationEntry("renderParticles", "a", "(L" + this.getNameDynamic("entityClass") + ";F)V"));
            this.nodemap.put("customPlayerMPConstructor", new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic("minecraftServer") + ";L" + this.getNameDynamic("worldServer") + ";L" + this.getNameDynamic("gameProfileClass") + ";L" + this.getNameDynamic("itemInWorldManagerClass") + ";)V"));
            this.nodemap.put("customPlayerSPConstructor", new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic("minecraft") + ";L" + this.getNameDynamic("worldClass") + ";L" + this.getNameDynamic("session") + ";L" + this.getNameDynamic("netHandlerPlayClientClass") + ";L" + this.getNameDynamic("statFileWriterClass") + ";)V"));
            this.nodemap.put("handleSpawnPlayerMethod", new MethodObfuscationEntry("handleSpawnPlayer", "a", "(L" + this.getNameDynamic("packetSpawnPlayer") + ";)V"));
            this.nodemap.put("orientCamera", new MethodObfuscationEntry("orientCamera", "h", "(F)V"));
            this.nodemap.put("renderManagerMethod", new MethodObfuscationEntry("func_147939_a", "a", "(L" + this.getNameDynamic("entityClass") + ";DDDFFZ)Z"));
            this.nodemap.put("setupGLTranslationMethod", new MethodObfuscationEntry("setupGLTranslation", "f", "()V"));
            this.nodemap.put("preRenderBlocksMethod", new MethodObfuscationEntry("preRenderBlocks", "b", "(I)V"));
            this.nodemap.put("setPositionMethod", new MethodObfuscationEntry("setPosition", "a", "(III)V"));
            this.nodemap.put("updateRendererMethod", new MethodObfuscationEntry("updateRenderer", "a", "(L" + this.getNameDynamic("entityLivingClass") + ";)V"));
            this.nodemap.put("loadRenderersMethod", new MethodObfuscationEntry("loadRenderers", "a", "()V"));
            this.nodemap.put("renderGlobalInitMethod", new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic("minecraft") + ";)V"));
            this.nodemap.put("sortAndRenderMethod", new MethodObfuscationEntry("sortAndRender", "a", "(L" + this.getNameDynamic("entityLivingClass") + ";ID)I"));
            this.nodemap.put("addVertexMethod", new MethodObfuscationEntry("addVertex", "a", "(DDD)V"));
            this.nodemap.put("renderTileAtMethod", new MethodObfuscationEntry("renderTileEntityAt", "a", "(L" + this.getNameDynamic("tileEntityClass") + ";DDDF)V"));
            this.nodemap.put("startGame", new MethodObfuscationEntry("startGame", "ag", "()V"));
            this.nodemap.put("canRenderOnFire", new MethodObfuscationEntry("canRenderOnFire", "aA", "()Z"));
            this.nodemap.put("CGSpopulate", new MethodObfuscationEntry("populate", "a", "(Lapu;II)V"));
            this.nodemap.put("attemptLoginMethodBukkit", new MethodObfuscationEntry("attemptLogin", "attemptLogin", "(L" + this.getNameDynamic("netHandlerLoginServer") + ";L" + this.getNameDynamic("gameProfileClass") + ";Ljava/lang/String;)L" + this.getNameDynamic("PlayerMP") + ";"));
            this.nodemap.put("renderModel", new MethodObfuscationEntry("renderModel", "a", "(L" + this.getNameDynamic("entityLivingClass") + ";FFFFFF)V"));
            this.nodemap.put("getRainStrength", new MethodObfuscationEntry("getRainStrength", "j", "(F)F"));
            this.nodemap.put("registerOF", new MethodObfuscationEntry("register", "register", "()V"));
        }
        try {
            this.isServer = (Launch.classLoader.getClassBytes(this.getNameDynamic("renderGlobalClass")) == null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (name.contains("galacticraft")) {
            return this.transformCustomAnnotations(bytes);
        }
        if (this.nameForgeHooksClient == null) {
            this.nameForgeHooksClient = this.getName("forgeHooks");
            if (this.deobfuscated) {
                this.populateNamesDeObf();
            }
            else {
                this.populateNamesObf();
            }
        }
        final String testName = name.replace('.', '/');
        if (testName.equals(this.nameForgeHooksClient)) {
            return this.transformForgeHooks(bytes);
        }
        if (testName.equals("sync/common/tileentity/TileEntityDualVertical")) {
            return this.transformSyncMod(bytes);
        }
        if (testName.equals("RenderPlayerOF")) {
            return this.transformOptifine(bytes);
        }
        if (testName.length() <= 3 || this.deobfuscated) {
            return this.transformVanilla(testName, bytes);
        }
        return bytes;
    }

    private void populateNamesDeObf() {
        this.nameConfManager = this.getName("confManagerClass");
        this.namePlayerController = this.getName("playerControllerClass");
        this.nameEntityLiving = this.getName("entityLivingClass");
        this.nameEntityItem = this.getName("entityItemClass");
        this.nameEntityRenderer = this.getName("entityRendererClass");
        this.nameItemRenderer = this.getName("itemRendererClass");
        this.nameGuiSleep = this.getName("guiSleepClass");
        this.nameEffectRenderer = this.getName("effectRendererClass");
        this.nameNetHandlerPlay = this.getName("netHandlerPlayClientClass");
        this.nameWorldRenderer = this.getName("worldRendererClass");
        this.nameRenderGlobal = this.getName("renderGlobalClass");
        this.nameRenderManager = this.getName("renderManagerClass");
        this.nameTileEntityRenderer = this.getName("tileEntityRendererClass");
        this.nameEntity = this.getName("entityClass");
        this.nameChunkProviderServer = this.getName("chunkProviderServer");
        this.nameEntityArrow = this.getName("entityArrow");
        this.nameRendererLivingEntity = this.getName("rendererLivingEntity");
        this.nameEntityGolem = this.getName("entityGolem");
        this.nameWorld = this.getName("worldClass");
    }

    private void populateNamesObf() {
        this.nameConfManager = this.nodemap.get("confManagerClass").obfuscatedName;
        this.namePlayerController = this.nodemap.get("playerControllerClass").obfuscatedName;
        this.nameEntityLiving = this.nodemap.get("entityLivingClass").obfuscatedName;
        this.nameEntityItem = this.nodemap.get("entityItemClass").obfuscatedName;
        this.nameEntityRenderer = this.nodemap.get("entityRendererClass").obfuscatedName;
        this.nameItemRenderer = this.nodemap.get("itemRendererClass").obfuscatedName;
        this.nameGuiSleep = this.nodemap.get("guiSleepClass").obfuscatedName;
        this.nameEffectRenderer = this.nodemap.get("effectRendererClass").obfuscatedName;
        this.nameNetHandlerPlay = this.nodemap.get("netHandlerPlayClientClass").obfuscatedName;
        this.nameWorldRenderer = this.nodemap.get("worldRendererClass").obfuscatedName;
        this.nameRenderGlobal = this.nodemap.get("renderGlobalClass").obfuscatedName;
        this.nameRenderManager = this.nodemap.get("renderManagerClass").obfuscatedName;
        this.nameTileEntityRenderer = this.nodemap.get("tileEntityRendererClass").obfuscatedName;
        this.nameEntity = this.nodemap.get("entityClass").obfuscatedName;
        this.nameChunkProviderServer = this.nodemap.get("chunkProviderServer").obfuscatedName;
        this.nameEntityArrow = this.nodemap.get("entityArrow").obfuscatedName;
        this.nameRendererLivingEntity = this.nodemap.get("rendererLivingEntity").obfuscatedName;
        this.nameEntityGolem = this.nodemap.get("entityGolem").obfuscatedName;
        this.nameWorld = this.nodemap.get("worldClass").obfuscatedName;
    }

    private byte[] transformVanilla(final String testName, final byte[] bytes) {
        if (testName.equals(this.nameConfManager)) {
            return this.transformConfigManager(bytes);
        }
        if (testName.equals(this.namePlayerController)) {
            return this.transformPlayerController(bytes);
        }
        if (testName.equals(this.nameEntityLiving)) {
            return this.transformEntityLiving(bytes);
        }
        if (testName.equals(this.nameEntityItem)) {
            return this.transformEntityItem(bytes);
        }
        if (testName.equals(this.nameEntityRenderer)) {
            return this.transformEntityRenderer(bytes);
        }
        if (testName.equals(this.nameItemRenderer)) {
            return this.transformItemRenderer(bytes);
        }
        if (testName.equals(this.nameGuiSleep)) {
            return this.transformGuiSleep(bytes);
        }
        if (testName.equals(this.nameEffectRenderer)) {
            return this.transformEffectRenderer(bytes);
        }
        if (testName.equals(this.nameNetHandlerPlay)) {
            return this.transformNetHandlerPlay(bytes);
        }
        if (testName.equals(this.nameWorldRenderer)) {
            return this.transformWorldRenderer(bytes);
        }
        if (testName.equals(this.nameRenderGlobal)) {
            return this.transformRenderGlobal(bytes);
        }
        if (testName.equals(this.nameRenderManager)) {
            return this.transformRenderManager(bytes);
        }
        if (testName.equals(this.nameTileEntityRenderer)) {
            return this.transformTileEntityRenderer(bytes);
        }
        if (testName.equals(this.nameEntity)) {
            return this.transformEntityClass(bytes);
        }
        if (testName.equals(this.nameChunkProviderServer)) {
            return this.transformChunkProviderServerClass(bytes);
        }
        if (testName.equals(this.nameEntityArrow)) {
            return this.transformEntityArrow(bytes);
        }
        if (testName.equals(this.nameRendererLivingEntity)) {
            return this.transformRendererLivingEntity(bytes);
        }
        if (testName.equals(this.nameEntityGolem)) {
            return this.transformEntityGolem(bytes);
        }
        if (testName.equals(this.nameWorld)) {
            return this.transformWorld(bytes);
        }
        return bytes;
    }

    public byte[] transformChunkProviderServerClass(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode populateMethod = this.getMethod(node, "CGSpopulate");
        if (populateMethod != null) {
            final LabelNode skipLabel = new LabelNode();
            for (int count = 0; count < populateMethod.instructions.size(); ++count) {
                final AbstractInsnNode list = populateMethod.instructions.get(count);
                if (list instanceof MethodInsnNode) {
                    final MethodInsnNode nodeAt = (MethodInsnNode)list;
                    if (nodeAt.getOpcode() == 185 && nodeAt.desc.equals(populateMethod.desc)) {
                        final InsnList nodesToAdd = new InsnList();
                        nodesToAdd.add((AbstractInsnNode)new VarInsnNode(21, 2));
                        nodesToAdd.add((AbstractInsnNode)new VarInsnNode(21, 3));
                        nodesToAdd.add((AbstractInsnNode)new VarInsnNode(25, 0));
                        nodesToAdd.add((AbstractInsnNode)new FieldInsnNode(180, this.getNameDynamic("chunkProviderServer"), this.getNameDynamic("cps_worldObj"), "L" + this.getNameDynamic("worldServer") + ";"));
                        nodesToAdd.add((AbstractInsnNode)new VarInsnNode(25, 0));
                        nodesToAdd.add((AbstractInsnNode)new FieldInsnNode(180, this.getNameDynamic("chunkProviderServer"), this.getNameDynamic("CurrentChunkProvider"), "L" + this.getNameDynamic("IChunkProvider") + ";"));
                        nodesToAdd.add((AbstractInsnNode)new VarInsnNode(25, 1));
                        nodesToAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "otherModPreventGenerate", "(IIL" + this.getNameDynamic("worldClass") + ";L" + this.getNameDynamic("IChunkProvider") + ";L" + this.getNameDynamic("IChunkProvider") + ";)Z"));
                        nodesToAdd.add((AbstractInsnNode)new JumpInsnNode(154, skipLabel));
                        populateMethod.instructions.insert((AbstractInsnNode)nodeAt, nodesToAdd);
                        ++MicdoodleTransformer.injectionCount;
                    }
                    else if (nodeAt.getOpcode() == 184 && nodeAt.owner.contains("GameRegistry")) {
                        populateMethod.instructions.insert((AbstractInsnNode)nodeAt, (AbstractInsnNode)skipLabel);
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformConfigManager(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final boolean playerAPI = this.isPlayerApiActive();
        final MethodNode attemptLoginMethod = this.getMethod(node, "attemptLoginMethodBukkit");
        MicdoodleTransformer.operationCount = (playerAPI ? 0 : ((attemptLoginMethod == null) ? 4 : 6));
        if (!playerAPI) {
            final MethodNode createPlayerMethod = this.getMethod(node, "createPlayerMethod");
            final MethodNode respawnPlayerMethod = this.getMethod(node, "respawnPlayerMethod");
            if (createPlayerMethod != null) {
                for (int count = 0; count < createPlayerMethod.instructions.size(); ++count) {
                    final AbstractInsnNode list = createPlayerMethod.instructions.get(count);
                    if (list instanceof TypeInsnNode) {
                        final TypeInsnNode nodeAt = (TypeInsnNode)list;
                        if (nodeAt.getOpcode() != 192 && nodeAt.desc.contains(this.getNameDynamic("PlayerMP"))) {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(187, this.getName("customPlayerMP"));
                            createPlayerMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                    else if (list instanceof MethodInsnNode) {
                        final MethodInsnNode nodeAt2 = (MethodInsnNode)list;
                        if (nodeAt2.owner.contains(this.getNameDynamic("PlayerMP")) && nodeAt2.getOpcode() == 183) {
                            createPlayerMethod.instructions.set((AbstractInsnNode)nodeAt2, (AbstractInsnNode)new MethodInsnNode(183, this.getName("customPlayerMP"), this.getName("customPlayerMPConstructor"), this.getDescDynamic("customPlayerMPConstructor")));
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                }
            }
            if (respawnPlayerMethod != null) {
                for (int count = 0; count < respawnPlayerMethod.instructions.size(); ++count) {
                    final AbstractInsnNode list = respawnPlayerMethod.instructions.get(count);
                    if (list instanceof TypeInsnNode) {
                        final TypeInsnNode nodeAt = (TypeInsnNode)list;
                        if (nodeAt.getOpcode() != 192 && nodeAt.desc.contains(this.getNameDynamic("PlayerMP"))) {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(187, this.getName("customPlayerMP"));
                            respawnPlayerMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                    else if (list instanceof MethodInsnNode) {
                        final MethodInsnNode nodeAt2 = (MethodInsnNode)list;
                        if (nodeAt2.name.equals("<init>") && nodeAt2.owner.equals(this.getNameDynamic("PlayerMP"))) {
                            respawnPlayerMethod.instructions.set((AbstractInsnNode)nodeAt2, (AbstractInsnNode)new MethodInsnNode(183, this.getName("customPlayerMP"), this.getName("customPlayerMPConstructor"), this.getDescDynamic("customPlayerMPConstructor")));
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                }
            }
            if (attemptLoginMethod != null) {
                for (int count = 0; count < attemptLoginMethod.instructions.size(); ++count) {
                    final AbstractInsnNode list = attemptLoginMethod.instructions.get(count);
                    if (list instanceof TypeInsnNode) {
                        final TypeInsnNode nodeAt = (TypeInsnNode)list;
                        if (nodeAt.getOpcode() == 187 && nodeAt.desc.contains(this.getNameDynamic("PlayerMP"))) {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(187, this.getName("customPlayerMP"));
                            attemptLoginMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                    else if (list instanceof MethodInsnNode) {
                        final MethodInsnNode nodeAt2 = (MethodInsnNode)list;
                        if (nodeAt2.getOpcode() == 183 && nodeAt2.name.equals("<init>") && nodeAt2.owner.equals(this.getNameDynamic("PlayerMP"))) {
                            final String initDesc = "(L" + this.getNameDynamic("minecraftServer") + ";L" + this.getNameDynamic("worldServer") + ";L" + this.getNameDynamic("gameProfileClass") + ";L" + this.getNameDynamic("itemInWorldManagerClass") + ";)V";
                            attemptLoginMethod.instructions.set((AbstractInsnNode)nodeAt2, (AbstractInsnNode)new MethodInsnNode(183, this.getName("customPlayerMP"), this.getName("customPlayerMPConstructor"), initDesc));
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformSyncMod(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final boolean playerAPI = this.isPlayerApiActive();
        MicdoodleTransformer.operationCount = (playerAPI ? 0 : 2);
        if (!playerAPI) {
            final MethodNode respawnPlayerMethod = this.getMethodNoDesc(node, "updateEntity");
            if (respawnPlayerMethod != null) {
                for (int count = 0; count < respawnPlayerMethod.instructions.size(); ++count) {
                    final AbstractInsnNode list = respawnPlayerMethod.instructions.get(count);
                    if (list instanceof TypeInsnNode) {
                        final TypeInsnNode nodeAt = (TypeInsnNode)list;
                        if (nodeAt.getOpcode() == 187 && nodeAt.desc.contains(this.getName("PlayerMP"))) {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(187, this.getName("customPlayerMP"));
                            respawnPlayerMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                    else if (list instanceof MethodInsnNode) {
                        final MethodInsnNode nodeAt2 = (MethodInsnNode)list;
                        if (nodeAt2.name.equals("<init>") && nodeAt2.owner.equals(this.getName("PlayerMP"))) {
                            respawnPlayerMethod.instructions.set((AbstractInsnNode)nodeAt2, (AbstractInsnNode)new MethodInsnNode(183, this.getName("customPlayerMP"), this.getName("customPlayerMPConstructor"), this.getDescDynamic("customPlayerMPConstructor")));
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformPlayerController(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final boolean playerAPI = this.isPlayerApiActive();
        MicdoodleTransformer.operationCount = (playerAPI ? 0 : 2);
        if (!playerAPI) {
            final MethodNode method = this.getMethod(node, "createClientPlayerMethod");
            if (method != null) {
                for (int count = 0; count < method.instructions.size(); ++count) {
                    final AbstractInsnNode list = method.instructions.get(count);
                    if (list instanceof TypeInsnNode) {
                        final TypeInsnNode nodeAt = (TypeInsnNode)list;
                        if (nodeAt.desc.contains(this.getNameDynamic("playerClient"))) {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(187, this.getName("customPlayerSP"));
                            method.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                    else if (list instanceof MethodInsnNode) {
                        final MethodInsnNode nodeAt2 = (MethodInsnNode)list;
                        if (nodeAt2.name.equals("<init>") && nodeAt2.owner.equals(this.getNameDynamic("playerClient"))) {
                            method.instructions.set((AbstractInsnNode)nodeAt2, (AbstractInsnNode)new MethodInsnNode(183, this.getName("customPlayerSP"), this.getName("customPlayerSPConstructor"), this.getDescDynamic("customPlayerSPConstructor")));
                            ++MicdoodleTransformer.injectionCount;
                        }
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformEntityLiving(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "moveEntityMethod");
        if (method != null) {
            for (int count = 0; count < method.instructions.size(); ++count) {
                final AbstractInsnNode list = method.instructions.get(count);
                if (list instanceof LdcInsnNode) {
                    final LdcInsnNode nodeAt = (LdcInsnNode)list;
                    if (nodeAt.cst.equals(0.08)) {
                        final VarInsnNode beforeNode = new VarInsnNode(25, 0);
                        final MethodInsnNode overwriteNode = new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getGravityForEntity", "(L" + this.getNameDynamic("entityClass") + ";)D");
                        method.instructions.insertBefore((AbstractInsnNode)nodeAt, (AbstractInsnNode)beforeNode);
                        method.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                        ++MicdoodleTransformer.injectionCount;
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformEntityItem(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "onUpdateMethod");
        if (method != null) {
            for (int count = 0; count < method.instructions.size(); ++count) {
                final AbstractInsnNode list = method.instructions.get(count);
                if (list instanceof LdcInsnNode) {
                    final LdcInsnNode nodeAt = (LdcInsnNode)list;
                    if (nodeAt.cst.equals(0.03999999910593033)) {
                        final VarInsnNode beforeNode = new VarInsnNode(25, 0);
                        final MethodInsnNode overwriteNode = new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getItemGravity", "(L" + this.getNameDynamic("entityItemClass") + ";)D");
                        method.instructions.insertBefore((AbstractInsnNode)nodeAt, (AbstractInsnNode)beforeNode);
                        method.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                        ++MicdoodleTransformer.injectionCount;
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformEntityRenderer(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 5;
        final MethodNode updateLightMapMethod = this.getMethod(node, "updateLightmapMethod");
        final MethodNode updateFogColorMethod = this.getMethod(node, "updateFogColorMethod");
        final MethodNode orientCameraMethod = this.getMethod(node, "orientCamera");
        if (orientCameraMethod != null) {
            final InsnList nodesToAdd = new InsnList();
            nodesToAdd.add((AbstractInsnNode)new VarInsnNode(23, 1));
            nodesToAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "orientCamera", "(F)V"));
            orientCameraMethod.instructions.insertBefore(orientCameraMethod.instructions.get(orientCameraMethod.instructions.size() - 3), nodesToAdd);
            ++MicdoodleTransformer.injectionCount;
            if (ConfigManagerMicCore.enableDebug) {
                System.out.println("bll.OrientCamera done");
            }
        }
        if (updateLightMapMethod != null) {
            boolean worldBrightnessInjection = false;
            for (int count = 0; count < updateLightMapMethod.instructions.size(); ++count) {
                final AbstractInsnNode list = updateLightMapMethod.instructions.get(count);
                if (list instanceof MethodInsnNode) {
                    final MethodInsnNode nodeAt = (MethodInsnNode)list;
                    if (!worldBrightnessInjection && nodeAt.owner.equals(this.getNameDynamic("worldClient"))) {
                        updateLightMapMethod.instructions.remove(updateLightMapMethod.instructions.get(count - 1));
                        updateLightMapMethod.instructions.remove(updateLightMapMethod.instructions.get(count - 1));
                        updateLightMapMethod.instructions.insertBefore(updateLightMapMethod.instructions.get(count - 1), (AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getWorldBrightness", "(L" + this.getNameDynamic("worldClient") + ";)F"));
                        ++MicdoodleTransformer.injectionCount;
                        worldBrightnessInjection = true;
                        if (ConfigManagerMicCore.enableDebug) {
                            System.out.println("bll.updateLightMap - worldBrightness done");
                        }
                        continue;
                    }
                }
                if (list instanceof IntInsnNode) {
                    final IntInsnNode nodeAt2 = (IntInsnNode)list;
                    if (nodeAt2.operand == 255) {
                        final InsnList nodesToAdd2 = new InsnList();
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(23, 11));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(25, 2));
                        nodesToAdd2.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getColorRed", "(L" + this.getNameDynamic("worldClass") + ";)F"));
                        nodesToAdd2.add((AbstractInsnNode)new InsnNode(106));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(56, 11));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(23, 12));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(25, 2));
                        nodesToAdd2.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getColorGreen", "(L" + this.getNameDynamic("worldClass") + ";)F"));
                        nodesToAdd2.add((AbstractInsnNode)new InsnNode(106));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(56, 12));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(23, 13));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(25, 2));
                        nodesToAdd2.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getColorBlue", "(L" + this.getNameDynamic("worldClass") + ";)F"));
                        nodesToAdd2.add((AbstractInsnNode)new InsnNode(106));
                        nodesToAdd2.add((AbstractInsnNode)new VarInsnNode(56, 13));
                        updateLightMapMethod.instructions.insertBefore((AbstractInsnNode)nodeAt2, nodesToAdd2);
                        ++MicdoodleTransformer.injectionCount;
                        if (ConfigManagerMicCore.enableDebug) {
                            System.out.println("bll.updateLightMap - getColors done");
                            break;
                        }
                        break;
                    }
                }
            }
        }
        if (updateFogColorMethod != null) {
            for (int count2 = 0; count2 < updateFogColorMethod.instructions.size(); ++count2) {
                final AbstractInsnNode list2 = updateFogColorMethod.instructions.get(count2);
                if (list2 instanceof MethodInsnNode) {
                    final MethodInsnNode nodeAt3 = (MethodInsnNode)list2;
                    if (!this.optifinePresent && this.methodMatches("getFogColorMethod", nodeAt3)) {
                        final InsnList toAdd = new InsnList();
                        toAdd.add((AbstractInsnNode)new VarInsnNode(25, 2));
                        toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getFogColorHook", "(L" + this.getNameDynamic("worldClass") + ";)L" + this.getNameDynamic("vecClass") + ";"));
                        toAdd.add((AbstractInsnNode)new VarInsnNode(58, 9));
                        updateFogColorMethod.instructions.insertBefore(updateFogColorMethod.instructions.get(count2 + 2), toAdd);
                        ++MicdoodleTransformer.injectionCount;
                        if (ConfigManagerMicCore.enableDebug) {
                            System.out.println("bll.updateFogColor - getFogColor (no Optifine) done");
                        }
                    }
                    else if (this.methodMatches("getSkyColorMethod", nodeAt3)) {
                        final InsnList toAdd = new InsnList();
                        toAdd.add((AbstractInsnNode)new VarInsnNode(25, 2));
                        toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getSkyColorHook", "(L" + this.getNameDynamic("worldClass") + ";)L" + this.getNameDynamic("vecClass") + ";"));
                        toAdd.add((AbstractInsnNode)new VarInsnNode(58, 5));
                        updateFogColorMethod.instructions.insertBefore(updateFogColorMethod.instructions.get(count2 + 2), toAdd);
                        ++MicdoodleTransformer.injectionCount;
                        if (ConfigManagerMicCore.enableDebug) {
                            System.out.println("bll.updateFogColor - getSkyColor done");
                        }
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformGuiSleep(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "wakeEntityMethod");
        if (method != null) {
            method.instructions.insertBefore(method.instructions.get(method.instructions.size() - 3), (AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/miccore/MicdoodlePlugin", "onSleepCancelled", "()V"));
            ++MicdoodleTransformer.injectionCount;
        }
        return this.finishInjection(node);
    }

    public byte[] transformForgeHooks(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "orientBedCamera");
        if (method != null) {
            method.instructions.insertBefore(method.instructions.get(0), (AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/miccore/MicdoodlePlugin", "orientCamera", "()V"));
            ++MicdoodleTransformer.injectionCount;
        }
        return this.finishInjection(node);
    }

    public byte[] transformEntityGolem(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 0;
        MicdoodleTransformer.injectionCount = 0;
        final String inter = "micdoodle8/mods/galacticraft/api/entity/IEntityBreathable";
        try {
            Class.forName(inter.replace("/", "."));
            if (!node.interfaces.contains(inter)) {
                node.interfaces.add(inter);
                ++MicdoodleTransformer.injectionCount;
            }
            final MethodNode canBreathe = new MethodNode(1, "canBreath", "()Z", (String)null, (String[])null);
            canBreathe.instructions.add((AbstractInsnNode)new InsnNode(4));
            canBreathe.instructions.add((AbstractInsnNode)new InsnNode(172));
            node.methods.add(canBreathe);
        }
        catch (Exception ex) {}
        return this.finishInjection(node);
    }

    public byte[] transformCustomAnnotations(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 0;
        MicdoodleTransformer.injectionCount = 0;
        final Iterator<MethodNode> methods = node.methods.iterator();
        final List<String> ignoredMods = new ArrayList<String>();
        while (methods.hasNext()) {
            final MethodNode methodnode = methods.next();
            if (methodnode.visibleAnnotations != null && methodnode.visibleAnnotations.size() > 0) {
                for (final AnnotationNode annotation : methodnode.visibleAnnotations) {
                    if (annotation.desc.equals("Lmicdoodle8/mods/miccore/Annotations$VersionSpecific;")) {
                        String toMatch = null;
                        for (int i = 0; i < annotation.values.size(); i += 2) {
                            if ("version".equals(annotation.values.get(i))) {
                                toMatch = String.valueOf(annotation.values.get(i + 1));
                            }
                        }
                        if (toMatch != null) {
                            boolean doRemove = !this.mcVersionMatches(toMatch);
                            if (doRemove) {
                                methods.remove();
                                break;
                            }
                        }
                    }
                    if (annotation.desc.equals("Lmicdoodle8/mods/miccore/Annotations$AltForVersion;")) {
                        String toMatch = null;
                        for (int i = 0; i < annotation.values.size(); i += 2) {
                            if ("version".equals(annotation.values.get(i))) {
                                toMatch = String.valueOf(annotation.values.get(i + 1));
                            }
                        }
                        if (toMatch != null && this.mcVersionMatches(toMatch)) {
                            String existing = new String(methodnode.name);
                            existing = existing.substring(0, existing.length() - 1);
                            if (ConfigManagerMicCore.enableDebug) {
                                this.printLog("Renaming method " + existing + " for version " + toMatch);
                            }
                            methodnode.name = new String(existing);
                            break;
                        }
                    }
                    if (annotation.desc.equals("Lmicdoodle8/mods/miccore/Annotations$RuntimeInterface;")) {
                        final List<String> desiredInterfaces = new ArrayList<String>();
                        String modID = "";
                        for (int j = 0; j < annotation.values.size(); j += 2) {
                            final Object value = annotation.values.get(j);
                            if (value.equals("clazz")) {
                                desiredInterfaces.add(String.valueOf(annotation.values.get(j + 1)));
                            }
                            else if (value.equals("modID")) {
                                modID = String.valueOf(annotation.values.get(j + 1));
                            }
                            else if (value.equals("altClasses")) {
                                desiredInterfaces.addAll((Collection<? extends String>) annotation.values.get(j + 1));
                            }
                        }
                        if (modID.isEmpty() || !ignoredMods.contains(modID)) {
                            final boolean modFound = modID.isEmpty() || Loader.isModLoaded(modID);
                            if (modFound) {
                                for (String inter : desiredInterfaces) {
                                    try {
                                        Class.forName(inter);
                                    }
                                    catch (ClassNotFoundException e) {
                                        if (!ConfigManagerMicCore.enableDebug) {
                                            continue;
                                        }
                                        this.printLog("Galacticraft ignored missing interface \"" + inter + "\" from mod \"" + modID + "\".");
                                        continue;
                                    }
                                    inter = inter.replace(".", "/");
                                    if (!node.interfaces.contains(inter)) {
                                        if (ConfigManagerMicCore.enableDebug) {
                                            this.printLog("Galacticraft added interface \"" + inter + "\" dynamically from \"" + modID + "\" to class \"" + node.name + "\".");
                                        }
                                        node.interfaces.add(inter);
                                        ++MicdoodleTransformer.injectionCount;
                                        break;
                                    }
                                    break;
                                }
                            }
                            else {
                                ignoredMods.add(modID);
                                if (ConfigManagerMicCore.enableDebug) {
                                    this.printLog("Galacticraft ignored dynamic interface insertion since \"" + modID + "\" was not found.");
                                }
                            }
                            break;
                        }
                        break;
                    }
                }
            }
        }
        if (MicdoodleTransformer.injectionCount > 0 && ConfigManagerMicCore.enableDebug) {
            this.printLog("Galacticraft successfully injected bytecode into: " + node.name + " (" + MicdoodleTransformer.injectionCount + ")");
        }
        return this.finishInjection(node, false);
    }

    public byte[] transformEffectRenderer(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode renderParticlesMethod = this.getMethod(node, "renderParticlesMethod");
        if (renderParticlesMethod != null) {
            final InsnList toAdd = new InsnList();
            toAdd.add((AbstractInsnNode)new VarInsnNode(23, 2));
            toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "renderFootprints", "(F)V"));
            renderParticlesMethod.instructions.insert(renderParticlesMethod.instructions.get(0), toAdd);
            ++MicdoodleTransformer.injectionCount;
        }
        return this.finishInjection(node);
    }

    public byte[] transformItemRenderer(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode renderOverlaysMethod = this.getMethod(node, "renderOverlaysMethod");
        if (renderOverlaysMethod != null) {
            for (int count = 0; count < renderOverlaysMethod.instructions.size(); ++count) {
                final AbstractInsnNode glEnable = renderOverlaysMethod.instructions.get(count);
                if (glEnable instanceof MethodInsnNode && ((MethodInsnNode)glEnable).name.equals("glEnable")) {
                    final InsnList toAdd = new InsnList();
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 1));
                    toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "renderLiquidOverlays", "(F)V"));
                    renderOverlaysMethod.instructions.insertBefore(glEnable, toAdd);
                    ++MicdoodleTransformer.injectionCount;
                    break;
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformNetHandlerPlay(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 2;
        final MethodNode handleNamedSpawnMethod = this.getMethod(node, "handleSpawnPlayerMethod");
        if (handleNamedSpawnMethod != null) {
            for (int count = 0; count < handleNamedSpawnMethod.instructions.size(); ++count) {
                final AbstractInsnNode list = handleNamedSpawnMethod.instructions.get(count);
                if (list instanceof TypeInsnNode) {
                    final TypeInsnNode nodeAt = (TypeInsnNode)list;
                    if (nodeAt.desc.contains(this.getNameDynamic("entityOtherPlayer"))) {
                        final TypeInsnNode overwriteNode = new TypeInsnNode(187, this.getNameDynamic("customEntityOtherPlayer"));
                        handleNamedSpawnMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                        ++MicdoodleTransformer.injectionCount;
                    }
                }
                else if (list instanceof MethodInsnNode) {
                    final MethodInsnNode nodeAt2 = (MethodInsnNode)list;
                    if (nodeAt2.name.equals("<init>") && nodeAt2.owner.equals(this.getNameDynamic("entityOtherPlayer"))) {
                        handleNamedSpawnMethod.instructions.set((AbstractInsnNode)nodeAt2, (AbstractInsnNode)new MethodInsnNode(183, this.getNameDynamic("customEntityOtherPlayer"), "<init>", "(L" + this.getNameDynamic("worldClass") + ";L" + this.getNameDynamic("gameProfileClass") + ";)V"));
                        ++MicdoodleTransformer.injectionCount;
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformWorldRenderer(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final Boolean smallMoonsEnabled = this.getSmallMoonsEnabled();
        MicdoodleTransformer.operationCount = (smallMoonsEnabled ? 2 : 0);
        if (smallMoonsEnabled) {
            final MethodNode setPositionMethod = this.getMethod(node, "setPositionMethod");
            if (setPositionMethod != null) {
                int count = 0;
                while (count < setPositionMethod.instructions.size()) {
                    final AbstractInsnNode nodeTest = setPositionMethod.instructions.get(count);
                    if (nodeTest instanceof InsnNode && nodeTest.getOpcode() == 177) {
                        final InsnList toAdd = new InsnList();
                        toAdd.add((AbstractInsnNode)new VarInsnNode(25, 0));
                        toAdd.add((AbstractInsnNode)new VarInsnNode(25, 0));
                        toAdd.add((AbstractInsnNode)new FieldInsnNode(180, this.getNameDynamic("worldRendererClass"), this.getNameDynamic("glRenderList"), "I"));
                        toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "setPositionList", "(L" + this.getNameDynamic("worldRendererClass") + ";I)V"));
                        setPositionMethod.instructions.insertBefore(nodeTest, toAdd);
                        ++MicdoodleTransformer.injectionCount;
                        if (ConfigManagerMicCore.enableDebug) {
                            System.out.println("blg.setPosition - done");
                            break;
                        }
                        break;
                    }
                    else {
                        ++count;
                    }
                }
            }
            final MethodNode setupGLMethod = this.getMethod(node, "setupGLTranslationMethod");
            if (setupGLMethod != null) {
                final InsnList toAdd2 = new InsnList();
                toAdd2.add((AbstractInsnNode)new VarInsnNode(25, 0));
                toAdd2.add((AbstractInsnNode)new FieldInsnNode(180, this.getNameDynamic("worldRendererClass"), this.getNameDynamic("glRenderList"), "I"));
                toAdd2.add((AbstractInsnNode)new InsnNode(6));
                toAdd2.add((AbstractInsnNode)new InsnNode(96));
                toAdd2.add((AbstractInsnNode)new MethodInsnNode(184, "org/lwjgl/opengl/GL11", "glCallList", "(I)V"));
                setupGLMethod.instructions.insertBefore(setupGLMethod.instructions.get(0), toAdd2);
                ++MicdoodleTransformer.injectionCount;
                if (ConfigManagerMicCore.enableDebug) {
                    System.out.println("blg.setupGLMethod - done");
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformRenderGlobal(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final Boolean smallMoonsEnabled = this.getSmallMoonsEnabled();
        MicdoodleTransformer.operationCount = (smallMoonsEnabled ? 5 : 0);
        if (smallMoonsEnabled) {
            final MethodNode initMethod = this.getMethod(node, "renderGlobalInitMethod");
            if (initMethod != null) {
                int count = 0;
                while (count < initMethod.instructions.size()) {
                    final AbstractInsnNode nodeTest = initMethod.instructions.get(count);
                    final AbstractInsnNode nodeTestb = initMethod.instructions.get(count + 1);
                    if (nodeTest instanceof InsnNode && nodeTestb instanceof InsnNode && nodeTest.getOpcode() == 6 && nodeTestb.getOpcode() == 104) {
                        final InsnNode overwriteNode = new InsnNode(7);
                        initMethod.instructions.set(nodeTest, (AbstractInsnNode)overwriteNode);
                        ++MicdoodleTransformer.injectionCount;
                        if (ConfigManagerMicCore.enableDebug) {
                            System.out.println("bls.init - done");
                            break;
                        }
                        break;
                    }
                    else {
                        ++count;
                    }
                }
            }
            final MethodNode loadMethod = this.getMethod(node, "loadRenderersMethod");
            if (loadMethod != null) {
                for (int count2 = 0; count2 < loadMethod.instructions.size(); ++count2) {
                    final AbstractInsnNode nodeTest2 = loadMethod.instructions.get(count2);
                    if (nodeTest2 instanceof IincInsnNode) {
                        final IincInsnNode nodeAt = (IincInsnNode)nodeTest2;
                        if (nodeAt.var == 2 && nodeAt.incr == 3 && !this.optifinePresent) {
                            final IincInsnNode overwriteNode2 = new IincInsnNode(2, 4);
                            loadMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode2);
                            ++MicdoodleTransformer.injectionCount;
                            if (ConfigManagerMicCore.enableDebug) {
                                System.out.println("bls.loadRenderers (no Optifine) done");
                                break;
                            }
                            break;
                        }
                        else if (nodeAt.var == 6 && nodeAt.incr == 3 && this.optifinePresent) {
                            final IincInsnNode overwriteNode2 = new IincInsnNode(6, 4);
                            loadMethod.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode2);
                            ++MicdoodleTransformer.injectionCount;
                            if (ConfigManagerMicCore.enableDebug) {
                                System.out.println("bls.loadRenderers (Optifine present) done");
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            final MethodNode renderMethod = this.getMethod(node, "sortAndRenderMethod");
            if (renderMethod != null) {
                final InsnList toAdd = new InsnList();
                toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "adjustRenderCamera", "()V"));
                renderMethod.instructions.insertBefore(renderMethod.instructions.get(0), toAdd);
                ++MicdoodleTransformer.injectionCount;
                final MethodInsnNode toAdd2 = new MethodInsnNode(184, "org/lwjgl/opengl/GL11", "glPopMatrix", "()V");
                renderMethod.instructions.insertBefore(renderMethod.instructions.get(renderMethod.instructions.size() - 3), (AbstractInsnNode)toAdd2);
                ++MicdoodleTransformer.injectionCount;
                if (ConfigManagerMicCore.enableDebug) {
                    System.out.println("bls.sortAndRender - both done");
                }
                int pos1 = 0;
                int pos2 = 0;
                int pos3 = 0;
                final String fieldRenderersSkippingRenderPass = this.deobfuscated ? "renderersSkippingRenderPass" : "ac";
                final String fieldPrevChunkSortZ = this.deobfuscated ? "prevChunkSortZ" : "k";
                final String methodMarkRenderersForNewPosition = this.deobfuscated ? "markRenderersForNewPosition" : "c";
                for (int count3 = 0; count3 < renderMethod.instructions.size(); ++count3) {
                    final AbstractInsnNode nodeTest3 = renderMethod.instructions.get(count3);
                    if (nodeTest3 instanceof FieldInsnNode && nodeTest3.getOpcode() == 181 && ((FieldInsnNode)nodeTest3).name.equals(fieldRenderersSkippingRenderPass) && ((FieldInsnNode)nodeTest3).desc.equals("I")) {
                        pos1 = count3;
                    }
                    else if (nodeTest3 instanceof FieldInsnNode && nodeTest3.getOpcode() == 181 && ((FieldInsnNode)nodeTest3).name.equals(fieldPrevChunkSortZ) && ((FieldInsnNode)nodeTest3).desc.equals("I")) {
                        pos2 = count3;
                    }
                    else if (nodeTest3 instanceof MethodInsnNode && nodeTest3.getOpcode() == 183 && ((MethodInsnNode)nodeTest3).name.equals(methodMarkRenderersForNewPosition) && ((MethodInsnNode)nodeTest3).desc.equals("(III)V")) {
                        pos3 = count3;
                    }
                }
                if (pos1 > 0 && pos2 > 0 && pos3 > 0) {
                    final AbstractInsnNode[] instructionArray = renderMethod.instructions.toArray();
                    renderMethod.instructions.clear();
                    int count4 = 0;
                    while (count4 <= pos1) {
                        renderMethod.instructions.add(instructionArray[count4++]);
                    }
                    count4 = pos2 + 1;
                    while (count4 <= pos3) {
                        renderMethod.instructions.add(instructionArray[count4++]);
                    }
                    count4 = pos1 + 1;
                    while (count4 <= pos2) {
                        renderMethod.instructions.add(instructionArray[count4++]);
                    }
                    count4 = pos3 + 1;
                    while (count4 < instructionArray.length) {
                        renderMethod.instructions.add(instructionArray[count4++]);
                    }
                    ++MicdoodleTransformer.injectionCount;
                }
                else {
                    System.out.println("[GC] Warning: Unable to modify bytecode for bls.markRenderersForNewPosition");
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformRenderManager(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final Boolean smallMoonsEnabled = this.getSmallMoonsEnabled();
        MicdoodleTransformer.operationCount = (smallMoonsEnabled ? 2 : 0);
        if (smallMoonsEnabled) {
            final MethodNode method = this.getMethod(node, "renderManagerMethod");
            if (method != null) {
                int count;
                for (count = 0; count < method.instructions.size(); ++count) {
                    final AbstractInsnNode nodeTest = method.instructions.get(count);
                    final AbstractInsnNode nodeTestb = method.instructions.get(count + 1);
                    if (nodeTest instanceof VarInsnNode && nodeTestb instanceof VarInsnNode && nodeTest.getOpcode() == 25 && nodeTestb.getOpcode() == 25 && ((VarInsnNode)nodeTest).var == 11 && ((VarInsnNode)nodeTestb).var == 1) {
                        final InsnList toAdd = new InsnList();
                        toAdd.add((AbstractInsnNode)new VarInsnNode(25, 1));
                        toAdd.add((AbstractInsnNode)new VarInsnNode(24, 2));
                        toAdd.add((AbstractInsnNode)new VarInsnNode(24, 4));
                        toAdd.add((AbstractInsnNode)new VarInsnNode(24, 6));
                        toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "adjustRenderPos", "(L" + this.getNameDynamic("entityClass") + ";DDD)V"));
                        method.instructions.insertBefore(nodeTest, toAdd);
                        ++MicdoodleTransformer.injectionCount;
                        break;
                    }
                }
                for (int i = count; i < method.instructions.size(); ++i) {
                    final AbstractInsnNode nodeTest2 = method.instructions.get(i);
                    if (nodeTest2 instanceof FieldInsnNode && nodeTest2.getOpcode() == 178) {
                        final FieldInsnNode f = (FieldInsnNode)nodeTest2;
                        if (f.owner.equals(this.getNameDynamic("renderManagerClass")) && f.desc.equals("Z")) {
                            final MethodInsnNode toAdd2 = new MethodInsnNode(184, "org/lwjgl/opengl/GL11", "glPopMatrix", "()V");
                            method.instructions.insertBefore(nodeTest2, (AbstractInsnNode)toAdd2);
                            ++MicdoodleTransformer.injectionCount;
                            if (ConfigManagerMicCore.enableDebug) {
                                System.out.println("bnf - done2/2");
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformTileEntityRenderer(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        final Boolean smallMoonsEnabled = this.getSmallMoonsEnabled();
        MicdoodleTransformer.operationCount = (smallMoonsEnabled ? 2 : 0);
        if (smallMoonsEnabled) {
            final MethodNode renderMethod = this.getMethod(node, "renderTileAtMethod");
            if (renderMethod != null) {
                final InsnList toAdd = new InsnList();
                toAdd.add((AbstractInsnNode)new VarInsnNode(25, 1));
                toAdd.add((AbstractInsnNode)new VarInsnNode(24, 2));
                toAdd.add((AbstractInsnNode)new VarInsnNode(24, 4));
                toAdd.add((AbstractInsnNode)new VarInsnNode(24, 6));
                toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore", "adjustTileRenderPos", "(L" + this.getNameDynamic("tileEntityClass") + ";DDD)V"));
                renderMethod.instructions.insert(toAdd);
                ++MicdoodleTransformer.injectionCount;
                AbstractInsnNode returnNode = renderMethod.instructions.get(renderMethod.instructions.size() - 1);
                for (int i = 0; i < renderMethod.instructions.size(); ++i) {
                    final AbstractInsnNode insnAt = renderMethod.instructions.get(i);
                    if (insnAt.getOpcode() == 177) {
                        returnNode = insnAt;
                        break;
                    }
                }
                final MethodInsnNode toAdd2 = new MethodInsnNode(184, "org/lwjgl/opengl/GL11", "glPopMatrix", "()V");
                renderMethod.instructions.insertBefore(returnNode, (AbstractInsnNode)toAdd2);
                ++MicdoodleTransformer.injectionCount;
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformEntityClass(final byte[] bytes) {
        if (this.isServer) {
            return bytes;
        }
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "canRenderOnFire");
        if (method != null) {
            for (int i = 0; i < method.instructions.size(); ++i) {
                final AbstractInsnNode nodeAt = method.instructions.get(i);
                if (nodeAt instanceof MethodInsnNode && nodeAt.getOpcode() == 182) {
                    final MethodInsnNode overwriteNode = new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "shouldRenderFire", "(L" + this.getNameDynamic("entityClass") + ";)Z");
                    method.instructions.set(nodeAt, (AbstractInsnNode)overwriteNode);
                    ++MicdoodleTransformer.injectionCount;
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformEntityArrow(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "onUpdateMethod");
        if (method != null) {
            for (int count = 0; count < method.instructions.size(); ++count) {
                final AbstractInsnNode list = method.instructions.get(count);
                if (list instanceof LdcInsnNode) {
                    final LdcInsnNode nodeAt = (LdcInsnNode)list;
                    if (nodeAt.cst.equals(0.05f)) {
                        final VarInsnNode beforeNode = new VarInsnNode(25, 0);
                        final MethodInsnNode overwriteNode = new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getArrowGravity", "(L" + this.getNameDynamic("entityArrow") + ";)F");
                        method.instructions.insertBefore((AbstractInsnNode)nodeAt, (AbstractInsnNode)beforeNode);
                        method.instructions.set((AbstractInsnNode)nodeAt, (AbstractInsnNode)overwriteNode);
                        ++MicdoodleTransformer.injectionCount;
                    }
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformRendererLivingEntity(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "renderModel");
        if (method != null) {
            for (int count = 0; count < method.instructions.size(); ++count) {
                final AbstractInsnNode list = method.instructions.get(count);
                if (list.getOpcode() == 177) {
                    final AbstractInsnNode nodeAbove = method.instructions.get(count - 2);
                    final InsnList toAdd = new InsnList();
                    toAdd.add((AbstractInsnNode)new VarInsnNode(25, 0));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(25, 1));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 2));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 3));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 4));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 5));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 6));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 7));
                    toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/client/render/entities/RenderPlayerGC", "renderModelS", "(L" + this.getNameDynamic("rendererLivingEntity") + ";L" + this.getNameDynamic("entityLivingClass") + ";FFFFFF)V"));
                    method.instructions.insertBefore(nodeAbove, toAdd);
                    ++MicdoodleTransformer.injectionCount;
                    break;
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformWorld(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "getRainStrength");
        if (method != null) {
            for (int count = 0; count < method.instructions.size(); ++count) {
                final AbstractInsnNode list = method.instructions.get(count);
                if (list.getOpcode() == 25) {
                    for (int i = 0; i < 6; ++i) {
                        method.instructions.remove(method.instructions.get(count + i));
                    }
                    final InsnList toAdd = new InsnList();
                    toAdd.add((AbstractInsnNode)new VarInsnNode(25, 0));
                    toAdd.add((AbstractInsnNode)new VarInsnNode(23, 1));
                    toAdd.add((AbstractInsnNode)new MethodInsnNode(184, "micdoodle8/mods/galacticraft/core/util/WorldUtil", "getRainStrength", "(L" + this.getNameDynamic("worldClass") + ";F)F"));
                    toAdd.add((AbstractInsnNode)new InsnNode(174));
                    method.instructions.insertBefore(method.instructions.get(count), toAdd);
                    ++MicdoodleTransformer.injectionCount;
                    break;
                }
            }
        }
        return this.finishInjection(node);
    }

    public byte[] transformOptifine(final byte[] bytes) {
        final ClassNode node = this.startInjection(bytes);
        MicdoodleTransformer.operationCount = 1;
        final MethodNode method = this.getMethod(node, "registerOF");
        if (method != null) {
            final AbstractInsnNode toAdd = (AbstractInsnNode)new InsnNode(177);
            method.instructions.insertBefore(method.instructions.get(0), toAdd);
            ++MicdoodleTransformer.injectionCount;
        }
        return this.finishInjection(node);
    }

    private void printResultsAndReset(final String nodeName) {
        if (MicdoodleTransformer.operationCount > 0) {
            if (MicdoodleTransformer.injectionCount >= MicdoodleTransformer.operationCount) {
                this.printLog("Galacticraft successfully injected bytecode into: " + nodeName + " (" + MicdoodleTransformer.injectionCount + " / " + MicdoodleTransformer.operationCount + ")");
            }
            else {
                System.err.println("Potential problem: Galacticraft did not complete injection of bytecode into: " + nodeName + " (" + MicdoodleTransformer.injectionCount + " / " + MicdoodleTransformer.operationCount + ")");
            }
        }
    }

    private MethodNode getMethod(final ClassNode node, final String keyName) {
        for (final MethodNode methodNode : node.methods) {
            if (this.methodMatches(keyName, methodNode)) {
                return methodNode;
            }
        }
        return null;
    }

    private MethodNode getMethodNoDesc(final ClassNode node, final String methodName) {
        for (final MethodNode methodNode : node.methods) {
            if (methodNode.name.equals(methodName)) {
                return methodNode;
            }
        }
        return null;
    }

    private boolean methodMatches(final String keyName, final MethodInsnNode node) {
        return node.name.equals(this.getNameDynamic(keyName)) && node.desc.equals(this.getDescDynamic(keyName));
    }

    private boolean methodMatches(final String keyName, final MethodNode node) {
        return node.name.equals(this.getNameDynamic(keyName)) && node.desc.equals(this.getDescDynamic(keyName));
    }

    public String getName(final String keyName) {
        return this.nodemap.get(keyName).name;
    }

    public String getObfName(final String keyName) {
        return this.nodemap.get(keyName).obfuscatedName;
    }

    public String getNameDynamic(final String keyName) {
        try {
            if (this.deobfuscated) {
                return this.getName(keyName);
            }
            return this.getObfName(keyName);
        }
        catch (NullPointerException e) {
            System.err.println("Could not find key: " + keyName);
            throw e;
        }
    }

    public String getDescDynamic(final String keyName) {
        return this.nodemap.get(keyName).obfuscatedName;
    }

    private boolean classPathMatches(final String keyName, final String className) {
        return className.replace('.', '/').equals(this.getNameDynamic(keyName));
    }

    private void printLog(final String message) {
        System.out.println(message);
    }

    private ClassNode startInjection(final byte[] bytes) {
        final ClassNode node = new ClassNode();
        final ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);
        MicdoodleTransformer.injectionCount = 0;
        MicdoodleTransformer.operationCount = 0;
        return node;
    }

    private byte[] finishInjection(final ClassNode node) {
        return this.finishInjection(node, true);
    }

    private byte[] finishInjection(final ClassNode node, final boolean printToLog) {
        final ClassWriter writer = new ClassWriter(1);
        node.accept(writer);
        if (printToLog) {
            this.printResultsAndReset(node.name);
        }
        return writer.toByteArray();
    }

    private boolean getSmallMoonsEnabled() {
        return false;
    }

    private boolean isPlayerApiActive() {
        return this.playerApiActive;
    }

    private boolean mcVersionMatches(final String testVersion) {
        return VersionParser.parseRange(testVersion).containsVersion(this.mcVersion);
    }

    static {
        MicdoodleTransformer.operationCount = 0;
        MicdoodleTransformer.injectionCount = 0;
    }

    public static class ObfuscationEntry
    {
        public String name;
        public String obfuscatedName;

        public ObfuscationEntry(final String name, final String obfuscatedName) {
            this.name = name;
            this.obfuscatedName = obfuscatedName;
        }

        public ObfuscationEntry(final String commonName) {
            this(commonName, commonName);
        }
    }

    public static class MethodObfuscationEntry extends ObfuscationEntry
    {
        public String methodDesc;

        public MethodObfuscationEntry(final String name, final String obfuscatedName, final String methodDesc) {
            super(name, obfuscatedName);
            this.methodDesc = methodDesc;
        }

        public MethodObfuscationEntry(final String commonName, final String methodDesc) {
            this(commonName, commonName, methodDesc);
        }
    }

    public static class FieldObfuscationEntry extends ObfuscationEntry
    {
        public FieldObfuscationEntry(final String name, final String obfuscatedName) {
            super(name, obfuscatedName);
        }
    }
}
