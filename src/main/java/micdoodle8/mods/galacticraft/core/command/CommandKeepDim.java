package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;

public class CommandKeepDim extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " <dimension id>";
    }
    
    public int getRequiredPermissionLevel() {
        return 4;
    }
    
    public String getCommandName() {
        return "gckeeploaded";
    }
    
    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        if (astring.length > 1) {
            throw new WrongUsageException("Too many command arguments! Usage: " + this.getCommandUsage(icommandsender), new Object[0]);
        }
        try {
            final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
            if (playerBase != null) {
                int dimID;
                if (astring.length == 0) {
                    dimID = playerBase.dimension;
                }
                else {
                    try {
                        dimID = CommandBase.parseInt(icommandsender, astring[0]);
                    }
                    catch (Exception e) {
                        throw new WrongUsageException("Needs a dimension number! Usage: " + this.getCommandUsage(icommandsender), new Object[0]);
                    }
                }
                if (ConfigManagerCore.setLoaded(dimID)) {
                    playerBase.addChatMessage((IChatComponent)new ChatComponentText("[GCKeepLoaded] Successfully set dimension " + dimID + " to load staticly"));
                }
                else if (ConfigManagerCore.setUnloaded(dimID)) {
                    playerBase.addChatMessage((IChatComponent)new ChatComponentText("[GCKeepLoaded] Successfully set dimension " + dimID + " to not load staticly"));
                }
                else {
                    playerBase.addChatMessage((IChatComponent)new ChatComponentText("[GCKeepLoaded] Failed to set dimension as not static"));
                }
            }
        }
        catch (Exception var6) {
            throw new CommandException(var6.getMessage(), new Object[0]);
        }
    }
}
