package micdoodle8.mods.galacticraft.core.command;

import java.util.*;
import net.minecraft.server.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;

public class CommandGCAstroMiner extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " [show|reset|set<number>] <playername>";
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    public String getCommandName() {
        return "gcastrominer";
    }
    
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr) {
        if (par2ArrayOfStr.length == 1) {
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] { "show", "set", "reset" });
        }
        if (par2ArrayOfStr.length == 2) {
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers());
        }
        return null;
    }
    
    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
    
    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2) {
        return par2 == 1;
    }
    
    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        if (astring.length > 2) {
            throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.dimensiontp.tooMany", this.getCommandUsage(icommandsender)), new Object[0]);
        }
        if (astring.length < 1) {
            throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
        }
        int type = 0;
        int newvalue = 0;
        if (astring[0].equalsIgnoreCase("show")) {
            type = 1;
        }
        else if (astring[0].equalsIgnoreCase("reset")) {
            type = 2;
        }
        else if (astring[0].length() > 3 && astring[0].substring(0, 3).equalsIgnoreCase("set")) {
            final String number = astring[0].substring(3);
            try {
                newvalue = Integer.parseInt(number);
                if (newvalue > 0) {
                    type = 3;
                }
            }
            catch (NumberFormatException ex) {}
        }
        if (type > 0) {
            EntityPlayerMP playerBase = null;
            try {
                if (astring.length == 2) {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(astring[1], true);
                }
                else {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
                }
                if (playerBase == null) {
                    throw new Exception("Could not find player with name: " + astring[1]);
                }
                final GCPlayerStats stats = GCPlayerStats.get(playerBase);
                switch (type) {
                    case 1: {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("command.gcastrominer.count", playerBase.getGameProfile().getName(), "" + stats.astroMinerCount)));
                        break;
                    }
                    case 2: {
                        stats.astroMinerCount = 0;
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("command.gcastrominer.count", playerBase.getGameProfile().getName(), "0")));
                        break;
                    }
                    case 3: {
                        stats.astroMinerCount = newvalue;
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("command.gcastrominer.count", playerBase.getGameProfile().getName(), "" + newvalue)));
                        break;
                    }
                }
            }
            catch (Exception e) {
                throw new CommandException(e.getMessage(), new Object[0]);
            }
        }
    }
}
