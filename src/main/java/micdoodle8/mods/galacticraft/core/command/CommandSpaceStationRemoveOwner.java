package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.command.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import com.google.common.collect.*;
import java.util.*;

public class CommandSpaceStationRemoveOwner extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " <player>";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public boolean canCommandSenderUseCommand(final ICommandSender par1ICommandSender) {
        return true;
    }

    public String getCommandName() {
        return "ssuninvite";
    }

    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        String var3 = null;
        EntityPlayerMP playerBase = null;
        if (astring.length > 0) {
            var3 = astring[0];
            Label_0308: {
                try {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), false);
                    if (playerBase != null) {
                        final GCPlayerStats stats = GCPlayerStats.get(playerBase);
                        if (stats.spaceStationDimensionData.isEmpty()) {
                            throw new WrongUsageException(GCCoreUtil.translate("commands.ssinvite.notFound"), new Object[0]);
                        }
                        for (final Map.Entry<Integer, Integer> e : stats.spaceStationDimensionData.entrySet()) {
                            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, e.getValue(), (EntityPlayer)playerBase);
                            String str = null;
                            for (final String name : data.getAllowedPlayers()) {
                                if (name.equalsIgnoreCase(var3)) {
                                    str = name;
                                    break;
                                }
                            }
                            if (str == null) {
                                throw new CommandException(GCCoreUtil.translateWithFormat("commands.ssuninvite.noPlayer", "\"" + var3 + "\""), new Object[0]);
                            }
                            data.getAllowedPlayers().remove(str);
                            data.markDirty();
                        }
                    }
                    break Label_0308;
                }
                catch (Exception var4) {
                    throw new CommandException(var4.getMessage(), new Object[0]);
                }
               // throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
            }
            if (playerBase != null) {
                playerBase.addChatMessage(new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.removesuccess", var3)));
            }
            return;
        }
        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
    }

    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr) {
        return (par2ArrayOfStr.length == 1) ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers(par1ICommandSender)) : null;
    }

    protected String[] getPlayers(final ICommandSender icommandsender) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), false);
        if (playerBase != null) {
            final GCPlayerStats stats = GCPlayerStats.get(playerBase);
            if (!stats.spaceStationDimensionData.isEmpty()) {
                final String[] allNames = MinecraftServer.getServer().getAllUsernames();
                final HashSet<String> allowedNames = Sets.newHashSet();
                for (final Map.Entry<Integer, Integer> e : stats.spaceStationDimensionData.entrySet()) {
                    final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, e.getValue(), playerBase);
                    allowedNames.addAll(data.getAllowedPlayers());
                }
                final Iterator<String> itName = allowedNames.iterator();
                final ArrayList<String> replaceNames = new ArrayList<String>();
                while (itName.hasNext()) {
                    final String name = itName.next();
                    for (final String allEntry : allNames) {
                        if (name.equalsIgnoreCase(allEntry)) {
                            itName.remove();
                            replaceNames.add(allEntry);
                        }
                    }
                }
                allowedNames.addAll(replaceNames);
                final String[] rvsize = new String[allowedNames.size()];
                return allowedNames.toArray(rvsize);
            }
        }
        final String[] returnvalue = { "" };
        return returnvalue;
    }

    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2) {
        return par2 == 0;
    }
}
