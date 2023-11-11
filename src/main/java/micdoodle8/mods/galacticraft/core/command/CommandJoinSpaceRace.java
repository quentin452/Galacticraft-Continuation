package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;

public class CommandJoinSpaceRace extends CommandBase
{
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName();
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }

    public String getCommandName() {
        return "joinrace";
    }

    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
        if (astring.length == 0) {
            try {
                if (playerBase == null) {
                    throw new Exception("Could not find player with name: " + astring[0]);
                }
                final GCPlayerStats stats = GCPlayerStats.get(playerBase);
                if (stats.spaceRaceInviteTeamID > 0) {
                    SpaceRaceManager.sendSpaceRaceData(playerBase, SpaceRaceManager.getSpaceRaceFromID(stats.spaceRaceInviteTeamID));
                    GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_OPEN_JOIN_RACE_GUI, new Object[] { stats.spaceRaceInviteTeamID }), playerBase);
                    return;
                }
                throw new Exception("You haven't been invited to a space race team!");
            }
            catch (Exception var6) {
                throw new CommandException(var6.getMessage());
            }
            //throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.joinrace.noTeam", this.getCommandUsage(icommandsender)), new Object[0]);
        }
        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.joinrace.noTeam", this.getCommandUsage(icommandsender)));
    }
}
