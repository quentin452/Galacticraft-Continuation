package micdoodle8.mods.galacticraft.core.util;

import com.mojang.authlib.*;
import net.minecraft.server.*;
import net.minecraft.entity.player.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.entity.*;
import java.util.*;

public class PlayerUtil
{
    public static HashMap<String, GameProfile> knownSkins;

    public static EntityPlayerMP getPlayerForUsernameVanilla(final MinecraftServer server, final String username) {
        return VersionUtil.getPlayerForUsername(server, username);
    }

    public static EntityPlayerMP getPlayerBaseServerFromPlayerUsername(final String username, final boolean ignoreCase) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            GCLog.severe("Warning: Could not find player base server instance for player " + username);
            return null;
        }
        if (ignoreCase) {
            return getPlayerForUsernameVanilla(server, username);
        }
        List<EntityPlayerMP> playerList =
            (List<EntityPlayerMP>) server.getConfigurationManager().playerEntityList;
        for (EntityPlayerMP entityplayermp : playerList) {
            if (entityplayermp.getCommandSenderName().equalsIgnoreCase(username)) {
                return entityplayermp;
            }
        }

        return null;
    }

    public static EntityPlayerMP getPlayerBaseServerFromPlayer(final EntityPlayer player, final boolean ignoreCase) {
        if (player == null) {
            return null;
        }
        if (player instanceof EntityPlayerMP) {
            return (EntityPlayerMP)player;
        }
        return getPlayerBaseServerFromPlayerUsername(player.getCommandSenderName(), ignoreCase);
    }

    @SideOnly(Side.CLIENT)
    public static EntityClientPlayerMP getPlayerBaseClientFromPlayer(final EntityPlayer player, final boolean ignoreCase) {
        final EntityClientPlayerMP clientPlayer = FMLClientHandler.instance().getClientPlayerEntity();
        if (clientPlayer == null && player != null) {
            GCLog.severe("Warning: Could not find player base client instance for player " + player.getGameProfile().getName());
        }
        return clientPlayer;
    }

    @SideOnly(Side.CLIENT)
    public static GameProfile getOtherPlayerProfile(final String name) {
        return PlayerUtil.knownSkins.get(name);
    }

    @SideOnly(Side.CLIENT)
    public static GameProfile makeOtherPlayerProfile(final String strName, final String strUUID) {
        GameProfile profile = null;
        for (final Object e : FMLClientHandler.instance().getWorldClient().getLoadedEntityList()) {
            if (e instanceof AbstractClientPlayer) {
                final GameProfile gp2 = ((AbstractClientPlayer)e).getGameProfile();
                if (gp2.getName().equals(strName)) {
                    profile = gp2;
                    break;
                }
                continue;
            }
        }
        if (profile == null) {
            try {
                final UUID uuid = strUUID.isEmpty() ? UUID.randomUUID() : UUID.fromString(strUUID);
                profile = VersionUtil.constructGameProfile(uuid, strName);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (profile == null) {
            profile = VersionUtil.constructGameProfile(UUID.randomUUID(), strName);
        }
        PlayerUtil.knownSkins.put(strName, profile);
        return profile;
    }

    public static EntityPlayerMP getPlayerByUUID(final UUID theUUID) {
        final List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (int i = players.size() - 1; i >= 0; --i) {
            final EntityPlayerMP entityplayermp = (EntityPlayerMP) players.get(i);
            if (entityplayermp.getUniqueID().equals(theUUID)) {
                return entityplayermp;
            }
        }
        return null;
    }

    public static boolean isPlayerOnline(final EntityPlayerMP player) {
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList.contains(player);
    }

    static {
        PlayerUtil.knownSkins = new HashMap<>();
    }
}
