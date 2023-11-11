package micdoodle8.mods.galacticraft.core.dimension;

import net.minecraft.server.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import com.google.common.collect.*;

public class SpaceRaceManager
{
    private static final Set<SpaceRace> spaceRaces;
    
    public static SpaceRace addSpaceRace(final SpaceRace spaceRace) {
        SpaceRaceManager.spaceRaces.remove(spaceRace);
        SpaceRaceManager.spaceRaces.add(spaceRace);
        return spaceRace;
    }
    
    public static void removeSpaceRace(final SpaceRace race) {
        SpaceRaceManager.spaceRaces.remove(race);
    }
    
    public static void tick() {
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            boolean playerOnline = false;
            for (int j = 0; j < MinecraftServer.getServer().getConfigurationManager().playerEntityList.size(); ++j) {
                final Object o = MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(j);
                if (o instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer)o;
                    if (race.getPlayerNames().contains(player.getGameProfile().getName())) {
                        final CelestialBody body = GalaxyRegistry.getCelestialBodyFromDimensionID(player.worldObj.provider.dimensionId);
                        if (body != null && !race.getCelestialBodyStatusList().containsKey(body)) {
                            race.setCelestialBodyReached(body);
                        }
                        playerOnline = true;
                    }
                }
            }
            if (playerOnline) {
                race.tick();
            }
        }
    }
    
    public static void loadSpaceRaces(final NBTTagCompound nbt) {
        final NBTTagList tagList = nbt.getTagList("SpaceRaceList", 10);
        for (int i = 0; i < tagList.tagCount(); ++i) {
            final NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
            final SpaceRace race = new SpaceRace();
            race.loadFromNBT(nbt2);
            SpaceRaceManager.spaceRaces.add(race);
        }
    }
    
    public static void saveSpaceRaces(final NBTTagCompound nbt) {
        final NBTTagList tagList = new NBTTagList();
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            final NBTTagCompound nbt2 = new NBTTagCompound();
            race.saveToNBT(nbt2);
            tagList.appendTag((NBTBase)nbt2);
        }
        nbt.setTag("SpaceRaceList", (NBTBase)tagList);
    }
    
    public static SpaceRace getSpaceRaceFromPlayer(final String username) {
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            if (race.getPlayerNames().contains(username)) {
                return race;
            }
        }
        return null;
    }
    
    public static SpaceRace getSpaceRaceFromID(final int teamID) {
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            if (race.getSpaceRaceID() == teamID) {
                return race;
            }
        }
        return null;
    }
    
    public static void sendSpaceRaceData(final EntityPlayerMP toPlayer, final SpaceRace spaceRace) {
        if (spaceRace != null) {
            final List<Object> objList = new ArrayList<Object>();
            objList.add(spaceRace.getSpaceRaceID());
            objList.add(spaceRace.getTeamName());
            objList.add(spaceRace.getFlagData());
            objList.add(spaceRace.getTeamColor());
            objList.add(spaceRace.getPlayerNames().toArray(new String[spaceRace.getPlayerNames().size()]));
            if (toPlayer != null) {
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA, objList), toPlayer);
            }
            else {
                GalacticraftCore.packetPipeline.sendToAll(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA, objList));
            }
        }
    }
    
    public static ImmutableSet<SpaceRace> getSpaceRaces() {
        return (ImmutableSet<SpaceRace>)ImmutableSet.copyOf((Collection)new HashSet(SpaceRaceManager.spaceRaces));
    }
    
    public static void onPlayerRemoval(final String player, final SpaceRace race) {
        for (final String member : race.getPlayerNames()) {
            final EntityPlayerMP memberObj = PlayerUtil.getPlayerForUsernameVanilla(MinecraftServer.getServer(), member);
            if (memberObj != null) {
                memberObj.addChatMessage(new ChatComponentText(EnumColor.DARK_AQUA + GCCoreUtil.translateWithFormat("gui.spaceRace.chat.removeSuccess", EnumColor.RED + player + EnumColor.DARK_AQUA)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA)));
            }
        }
        final List<String> playerList = new ArrayList<String>();
        playerList.add(player);
        final SpaceRace newRace = addSpaceRace(new SpaceRace((List)playerList, "gui.spaceRace.unnamed", new FlagData(48, 32), new Vector3(1.0, 1.0, 1.0)));
        final EntityPlayerMP playerToRemove = PlayerUtil.getPlayerBaseServerFromPlayerUsername(player, true);
        if (playerToRemove != null) {
            sendSpaceRaceData(playerToRemove, newRace);
            sendSpaceRaceData(playerToRemove, race);
        }
    }
    
    static {
        spaceRaces = Sets.newHashSet();
    }
}
