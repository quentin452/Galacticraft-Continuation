package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.server.*;

public class CommandSpaceStationAddOwner extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " [ <player> | +all | -all ]";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public boolean canCommandSenderUseCommand(final ICommandSender par1ICommandSender) {
        return true;
    }

    public String getCommandName() {
        return "ssinvite";
    }

    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        String var3 = null;
        EntityPlayerMP playerBase = null;
        if (astring.length > 0) {
            var3 = astring[0];
            Label_0331: {
                try {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
                    if (playerBase != null) {
                        final GCPlayerStats stats = GCPlayerStats.get(playerBase);
                        if (stats.spaceStationDimensionData.isEmpty()) {
                            throw new WrongUsageException(GCCoreUtil.translate("commands.ssinvite.notFound"), new Object[0]);
                        }
                        for (final Map.Entry<Integer, Integer> ownedStations : stats.spaceStationDimensionData.entrySet()) {
                            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, ownedStations.getValue(), (EntityPlayer)playerBase);
                            if (var3.equalsIgnoreCase("+all")) {
                                data.setAllowedAll(true);
                                playerBase.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.allowAllTrue", new Object[0])));
                                return;
                            }
                            if (var3.equalsIgnoreCase("-all")) {
                                data.setAllowedAll(false);
                                playerBase.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.allowAllFalse", var3)));
                                return;
                            }
                            if (data.getAllowedPlayers().contains(var3)) {
                                continue;
                            }
                            data.getAllowedPlayers().add(var3);
                            data.markDirty();
                        }
                        final EntityPlayerMP playerToAdd = PlayerUtil.getPlayerBaseServerFromPlayerUsername(var3, true);
                        if (playerToAdd != null) {
                            playerToAdd.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.added", playerBase.getGameProfile().getName())));
                        }
                    }
                    break Label_0331;
                }
                catch (Exception var4) {
                    throw new CommandException(var4.getMessage());
                }
                //throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)), new Object[0]);
            }
            if (playerBase != null) {
                playerBase.addChatMessage(new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.addsuccess", var3)));
            }
            return;
        }
        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)));
    }

    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr) {
        return (par2ArrayOfStr.length == 1) ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers()) : null;
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2) {
        return par2 == 0;
    }
}
