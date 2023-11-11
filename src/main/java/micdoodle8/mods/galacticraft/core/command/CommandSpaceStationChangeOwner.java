package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.command.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;

public class CommandSpaceStationChangeOwner extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " <dim#> <player>";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandName() {
        return "ssnewowner";
    }

    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        String oldOwner = null;
        String newOwner = "ERROR";
        int stationID = -1;
        final EntityPlayerMP playerAdmin = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
        if (astring.length > 1) {
            newOwner = astring[1];
            try {
                stationID = Integer.parseInt(astring[0]);
            }
            catch (Exception var6) {
                throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssnewowner.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
            }
            if (stationID < 2) {
                throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssnewowner.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
            }
            Label_0429: {
                try {
                    final SpaceStationWorldData stationData = SpaceStationWorldData.getMPSpaceStationData(null, stationID, null);
                    if (stationData == null) {
                        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssnewowner.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
                    }
                    oldOwner = stationData.getOwner();
                    stationData.getAllowedPlayers().remove(oldOwner);
                    if (stationData.getSpaceStationName().equals("Station: " + oldOwner)) {
                        stationData.setSpaceStationName("Station: " + newOwner);
                    }
                    stationData.getAllowedPlayers().add(newOwner);
                    stationData.setOwner(newOwner);
                    final EntityPlayerMP oldPlayer = PlayerUtil.getPlayerBaseServerFromPlayerUsername(oldOwner, true);
                    final EntityPlayerMP newPlayer = PlayerUtil.getPlayerBaseServerFromPlayerUsername(newOwner, true);
                    if (oldPlayer != null) {
                        final GCPlayerStats stats = GCPlayerStats.get(oldPlayer);
                        SpaceStationWorldData.updateSSOwnership(oldPlayer, oldOwner, stats, stationID, stationData);
                        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID, new Object[] { WorldUtil.spaceStationDataToString(stats.spaceStationDimensionData) }), oldPlayer);
                    }
                    if (newPlayer != null) {
                        final GCPlayerStats stats = GCPlayerStats.get(newPlayer);
                        SpaceStationWorldData.updateSSOwnership(newPlayer, newOwner.replace(".", ""), stats, stationID, stationData);
                        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID, new Object[] { WorldUtil.spaceStationDataToString(stats.spaceStationDimensionData) }), newPlayer);
                    }
                    break Label_0429;
                }
                catch (Exception var6) {
                    throw new CommandException(var6.getMessage());
                }
            //    throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
            }
            if (playerAdmin != null) {
                playerAdmin.addChatMessage(new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.changesuccess", oldOwner, newOwner)));
            }
            else {
                System.out.println(GCCoreUtil.translateWithFormat("gui.spacestation.changesuccess", oldOwner, newOwner));
            }
            return;
        }
        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)));
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
