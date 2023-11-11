package micdoodle8.mods.galacticraft.core.command;

import net.minecraft.command.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;

public class CommandGCHelp extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName();
    }
    
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    public boolean canCommandSenderUseCommand(final ICommandSender par1ICommandSender) {
        return true;
    }
    
    public String getCommandName() {
        return "gchelp";
    }
    
    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
        if (playerBase == null) {
            return;
        }
        playerBase.addChatMessage(IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + GCCoreUtil.translate("gui.message.help1") + ": \",\"color\":\"white\"},{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + GalacticraftCore.PREFIX + "com/wiki\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + GCCoreUtil.translate("gui.message.clicklink") + "\",\"color\":\"yellow\"}},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://wiki." + GalacticraftCore.PREFIX + "com/wiki\"}}]"));
    }
}
