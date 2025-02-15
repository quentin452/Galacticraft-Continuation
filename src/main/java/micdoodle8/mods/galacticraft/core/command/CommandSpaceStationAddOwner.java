package micdoodle8.mods.galacticraft.core.command;

import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

public class CommandSpaceStationAddOwner extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + this.getCommandName() + " [ <player> | +all | -all ]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ssinvite";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP playerBase = null;

        if (astring.length <= 0) {
            throw new WrongUsageException(
                GCCoreUtil.translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)));
        }
        String var3 = astring[0];

        try {
            playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);

            if (playerBase != null) {
                final GCPlayerStats stats = GCPlayerStats.get(playerBase);

                if (stats.spaceStationDimensionData.isEmpty()) {
                    throw new WrongUsageException(GCCoreUtil.translate("commands.ssinvite.notFound"));
                }
                for (final Map.Entry<Integer, Integer> ownedStations : stats.spaceStationDimensionData.entrySet()) {
                    final SpaceStationWorldData data = SpaceStationWorldData
                        .getStationData(playerBase.worldObj, ownedStations.getValue(), playerBase);

                    if ("+all".equalsIgnoreCase(var3)) {
                        data.setAllowedAll(true);
                        playerBase.addChatMessage(
                            new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.allowAllTrue")));
                        return;
                    }
                    if ("-all".equalsIgnoreCase(var3)) {
                        data.setAllowedAll(false);
                        playerBase.addChatMessage(
                            new ChatComponentText(
                                GCCoreUtil.translateWithFormat("gui.spacestation.allowAllFalse", var3)));
                        return;
                    }

                    if (!data.getAllowedPlayers()
                        .contains(var3)) {
                        data.getAllowedPlayers()
                            .add(var3);
                        data.markDirty();
                    }
                }

                final EntityPlayerMP playerToAdd = PlayerUtil.getPlayerBaseServerFromPlayerUsername(var3, true);

                if (playerToAdd != null) {
                    playerToAdd.addChatMessage(
                        new ChatComponentText(
                            GCCoreUtil.translateWithFormat(
                                "gui.spacestation.added",
                                playerBase.getGameProfile()
                                    .getName())));
                }
            }
        } catch (final Exception var6) {
            throw new CommandException(var6.getMessage());
        }

        if (playerBase != null) {
            playerBase.addChatMessage(
                new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.addsuccess", var3)));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers()) : null;
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer()
            .getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return par2 == 0;
    }
}
