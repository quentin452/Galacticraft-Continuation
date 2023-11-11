package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import net.minecraft.nbt.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import com.google.common.collect.*;

public class SpaceRace
{
    public static final String DEFAULT_NAME = "gui.spaceRace.unnamed";
    private static int lastSpaceRaceID;
    private int spaceRaceID;
    private List<String> playerNames;
    public String teamName;
    private FlagData flagData;
    private Vector3 teamColor;
    private int ticksSpent;
    private Map<CelestialBody, Integer> celestialBodyStatusList;

    public SpaceRace() {
        this.playerNames = Lists.newArrayList();
        this.celestialBodyStatusList = new HashMap<CelestialBody, Integer>();
    }

    public SpaceRace(final List<String> playerNames, final String teamName, final FlagData flagData, final Vector3 teamColor) {
        this.playerNames = Lists.newArrayList();
        this.celestialBodyStatusList = new HashMap<CelestialBody, Integer>();
        this.playerNames = playerNames;
        this.teamName = new String(teamName);
        this.ticksSpent = 0;
        this.flagData = flagData;
        this.teamColor = teamColor;
        this.spaceRaceID = ++SpaceRace.lastSpaceRaceID;
    }

    public void loadFromNBT(final NBTTagCompound nbt) {
        this.teamName = nbt.getString("TeamName");
        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Loading spacerace data for team " + this.teamName);
        }
        this.spaceRaceID = nbt.getInteger("SpaceRaceID");
        this.ticksSpent = (int)nbt.getLong("TicksSpent");
        this.flagData = FlagData.readFlagData(nbt);
        this.teamColor = new Vector3(nbt.getDouble("teamColorR"), nbt.getDouble("teamColorG"), nbt.getDouble("teamColorB"));
        NBTTagList tagList = nbt.getTagList("PlayerList", 10);
        for (int i = 0; i < tagList.tagCount(); ++i) {
            final NBTTagCompound tagAt = tagList.getCompoundTagAt(i);
            this.playerNames.add(tagAt.getString("PlayerName"));
        }
        tagList = nbt.getTagList("CelestialBodyList", 10);
        for (int i = 0; i < tagList.tagCount(); ++i) {
            final NBTTagCompound tagAt = tagList.getCompoundTagAt(i);
            final CelestialBody body = GalaxyRegistry.getCelestialBodyFromUnlocalizedName(tagAt.getString("CelestialBodyName"));
            if (body != null) {
                this.celestialBodyStatusList.put(body, tagAt.getInteger("TimeTaken"));
            }
        }
        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Loaded spacerace team data OK.");
        }
    }

    public void saveToNBT(final NBTTagCompound nbt) {
        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Saving spacerace data for team " + this.teamName);
        }
        nbt.setString("TeamName", this.teamName);
        nbt.setInteger("SpaceRaceID", this.spaceRaceID);
        nbt.setLong("TicksSpent", (long)this.ticksSpent);
        this.flagData.saveFlagData(nbt);
        nbt.setDouble("teamColorR", this.teamColor.x);
        nbt.setDouble("teamColorG", this.teamColor.y);
        nbt.setDouble("teamColorB", this.teamColor.z);
        NBTTagList tagList = new NBTTagList();
        for (final String player : this.playerNames) {
            final NBTTagCompound tagComp = new NBTTagCompound();
            tagComp.setString("PlayerName", player);
            tagList.appendTag((NBTBase)tagComp);
        }
        nbt.setTag("PlayerList", (NBTBase)tagList);
        tagList = new NBTTagList();
        for (final Map.Entry<CelestialBody, Integer> celestialBody : this.celestialBodyStatusList.entrySet()) {
            final NBTTagCompound tagComp = new NBTTagCompound();
            tagComp.setString("CelestialBodyName", celestialBody.getKey().getUnlocalizedName());
            tagComp.setInteger("TimeTaken", (int)celestialBody.getValue());
            tagList.appendTag((NBTBase)tagComp);
        }
        nbt.setTag("CelestialBodyList", (NBTBase)tagList);
        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Saved spacerace team data OK.");
        }
    }

    public void tick() {
        ++this.ticksSpent;
    }

    public String getTeamName() {
        String ret = this.teamName;
        if ("gui.spaceRace.unnamed".equals(ret)) {
            ret = GCCoreUtil.translate("gui.spaceRace.unnamed");
        }
        return ret;
    }

    public List<String> getPlayerNames() {
        return this.playerNames;
    }

    public FlagData getFlagData() {
        return this.flagData;
    }

    public void setFlagData(final FlagData flagData) {
        this.flagData = flagData;
    }

    public Vector3 getTeamColor() {
        return this.teamColor;
    }

    public void setTeamColor(final Vector3 teamColor) {
        this.teamColor = teamColor;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public void setPlayerNames(final List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public void setSpaceRaceID(final int raceID) {
        this.spaceRaceID = raceID;
    }

    public int getSpaceRaceID() {
        return this.spaceRaceID;
    }

    public Map<CelestialBody, Integer> getCelestialBodyStatusList() {
        return (Map<CelestialBody, Integer>)ImmutableMap.copyOf((Map)this.celestialBodyStatusList);
    }

    public void setCelestialBodyReached(final CelestialBody body) {
        this.celestialBodyStatusList.put(body, this.ticksSpent);
    }

    public int getTicksSpent() {
        return this.ticksSpent;
    }

    @Override
    public int hashCode() {
        return this.spaceRaceID;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof SpaceRace && ((SpaceRace)other).getSpaceRaceID() == this.getSpaceRaceID();
    }

    static {
        SpaceRace.lastSpaceRaceID = 0;
    }
}
