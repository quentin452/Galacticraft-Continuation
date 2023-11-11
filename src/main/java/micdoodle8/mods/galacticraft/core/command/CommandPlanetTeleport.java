package micdoodle8.mods.galacticraft.core.command;

import net.minecraft.server.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;

public class CommandPlanetTeleport extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " [<player>]";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandName() {
        return "dimensiontp";
    }

    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        EntityPlayerMP playerBase = null;
        if (astring.length < 2) {
            try {
                if (astring.length == 1) {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(astring[0], true);
                }
                else {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
                }
                if (playerBase != null) {
                    final MinecraftServer server = MinecraftServer.getServer();
                    final WorldServer worldserver = server.worldServerForDimension(server.worldServers[0].provider.dimensionId);
                    final ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
                    final GCPlayerStats stats = GCPlayerStats.get(playerBase);
                    stats.rocketStacks = new ItemStack[2];
                    stats.rocketType = IRocketType.EnumRocketType.DEFAULT.ordinal();
                    stats.rocketItem = GCItems.rocketTier1;
                    stats.fuelLevel = 1000;
                    stats.coordsTeleportedFromX = chunkcoordinates.posX;
                    stats.coordsTeleportedFromZ = chunkcoordinates.posZ;
                    try {
                        WorldUtil.toCelestialSelection(playerBase, stats, Integer.MAX_VALUE);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                    VersionUtil.notifyAdmins(icommandsender, (ICommand)this, "commands.dimensionteleport", String.valueOf(EnumColor.GREY + "[" + playerBase.getCommandSenderName()), "]");
                    return;
                }
                throw new Exception("Could not find player with name: " + astring[0]);
            }
            catch (Exception var6) {
                throw new CommandException(var6.getMessage());
            }
          //  throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.dimensiontp.tooMany", this.getCommandUsage(icommandsender)), new Object[0]);
        }
        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.dimensiontp.tooMany", this.getCommandUsage(icommandsender)));
    }
}
