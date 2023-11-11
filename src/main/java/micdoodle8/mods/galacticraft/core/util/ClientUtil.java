package micdoodle8.mods.galacticraft.core.util;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.api.vector.*;

@SideOnly(Side.CLIENT)
public class ClientUtil
{
    public static ScaledResolution getScaledRes(final Minecraft minecraft, final int width, final int height) {
        return VersionUtil.getScaledRes(minecraft, width, height);
    }
    
    public static FlagData updateFlagData(final String playerName, final boolean sendPacket) {
        final SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(playerName);
        if (race != null) {
            return race.getFlagData();
        }
        if (!ClientProxyCore.flagRequestsSent.contains(playerName) && sendPacket) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_FLAG_DATA, new Object[] { playerName }));
            ClientProxyCore.flagRequestsSent.add(playerName);
        }
        return null;
    }
    
    public static Vector3 updateTeamColor(final String playerName, final boolean sendPacket) {
        final SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(playerName);
        if (race != null) {
            return race.getTeamColor();
        }
        if (!ClientProxyCore.flagRequestsSent.contains(playerName) && sendPacket) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_FLAG_DATA, new Object[] { playerName }));
            ClientProxyCore.flagRequestsSent.add(playerName);
        }
        return new Vector3(1.0, 1.0, 1.0);
    }
}
