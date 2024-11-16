package micdoodle8.mods.galacticraft.core.command;

import java.util.*;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

public class CommandGCInv extends CommandBase {

    protected static final Map<String, ItemStack[]> savedata = new HashMap<>();
    private static final Set<String> dontload = new HashSet<>();
    private static boolean firstuse = true;
    private static GCInvSaveData savefile;

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + this.getCommandName() + " [save|restore|drop|clear] <playername>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandName() {
        return "gcinv";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        if (par2ArrayOfStr.length == 1) {
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, "save", "restore", "drop", "clear");
        }
        if (par2ArrayOfStr.length == 2) {
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers());
        }
        return null;
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer()
            .getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return par2 == 1;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (CommandGCInv.firstuse) {
            CommandGCInv.firstuse = false;
            CommandGCInv.initialise();
        }

        if (astring.length != 2) {
            throw new WrongUsageException(
                "Not enough command arguments! Usage: " + this.getCommandUsage(icommandsender));
        }
        try {
            final EntityPlayerMP thePlayer = PlayerUtil.getPlayerBaseServerFromPlayerUsername(astring[1], true);
            if (thePlayer != null && !thePlayer.isDead && thePlayer.worldObj != null) {
                final GCPlayerStats stats = GCPlayerStats.get(thePlayer);

                if ("drop".equalsIgnoreCase(astring[0])) {
                    final InventoryExtended gcInventory = stats.extendedInventory;
                    gcInventory.dropExtendedItems(thePlayer);
                } else if ("save".equalsIgnoreCase(astring[0])) {
                    final InventoryExtended gcInventory = stats.extendedInventory;
                    final ItemStack[] saveinv = new ItemStack[gcInventory.getSizeInventory()];
                    for (int i = 0; i < gcInventory.getSizeInventory(); i++) {
                        saveinv[i] = gcInventory.getStackInSlot(i);
                        gcInventory.setInventorySlotContents(i, null);
                    }

                    CommandGCInv.savedata.put(astring[1].toLowerCase(), saveinv);
                    CommandGCInv.dontload.add(astring[1].toLowerCase());
                    CommandGCInv.writefile();
                    System.out.println(
                        "[GCInv] Saving and clearing GC inventory slots of " + thePlayer.getGameProfile()
                            .getName());
                } else if ("restore".equalsIgnoreCase(astring[0])) {
                    final ItemStack[] saveinv = CommandGCInv.savedata.get(astring[1].toLowerCase());
                    CommandGCInv.dontload.remove(astring[1].toLowerCase());
                    if (saveinv == null) {
                        System.out.println(
                            "[GCInv] Tried to restore but player " + thePlayer.getGameProfile()
                                .getName() + " had no saved GC inventory items.");
                        return;
                    }

                    CommandGCInv.doLoad(thePlayer);
                } else if ("clear".equalsIgnoreCase(astring[0])) {
                    final InventoryExtended gcInventory = stats.extendedInventory;
                    for (int i = 0; i < gcInventory.getSizeInventory(); i++) {
                        gcInventory.setInventorySlotContents(i, null);
                    }
                } else {
                    throw new WrongUsageException(
                        "Invalid GCInv command. Usage: " + this.getCommandUsage(icommandsender));
                }
            } else {
                // Special rule for 'restore' command if player not found -
                // look to see if the player is offline (i.e. had a saved
                // inventory already)
                if ("restore".equalsIgnoreCase(astring[0])) {
                    final ItemStack[] saveinv = CommandGCInv.savedata.get(astring[1].toLowerCase());
                    if (saveinv != null) {
                        System.out.println(
                            "[GCInv] Restore command for offline player " + astring[1]
                                + ", setting to restore GCInv on next login.");
                        CommandGCInv.dontload.remove(astring[1].toLowerCase());
                        // Now it can autoload on next player logon
                        return;
                    }
                }

                // No player found, and not a 'restore' command
                if (!"clear".equalsIgnoreCase(astring[0]) && !"save".equalsIgnoreCase(astring[0])
                    && !"drop".equalsIgnoreCase(astring[0])) {
                    throw new WrongUsageException(
                        "Invalid GCInv command. Usage: " + this.getCommandUsage(icommandsender));
                }
                System.out.println("GCInv command: player " + astring[1] + " not found.");
            }
        } catch (final Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public static void doLoad(EntityPlayerMP thePlayer) {
        final String theName = thePlayer.getGameProfile()
            .getName()
            .toLowerCase();
        if (!CommandGCInv.dontload.contains(theName))
        // This is a simple flag: if the playername is in dontload then no
        // restore command has yet been run.
        // Dontload resets to nothing on server restart so that all will
        // auto-restore on a server restart.
        {
            final ItemStack[] saveinv = CommandGCInv.savedata.get(theName);
            final InventoryExtended gcInventory = GCPlayerStats.get(thePlayer).extendedInventory;
            for (int i = 0; i < gcInventory.getSizeInventory(); i++) {
                gcInventory.setInventorySlotContents(i, saveinv[i]);
            }
            CommandGCInv.savedata.remove(theName);
            CommandGCInv.writefile();
            System.out.println(
                "[GCInv] Restored GC inventory slots of " + thePlayer.getGameProfile()
                    .getName());

        } else {
            System.out.println(
                "[GCInv] Player " + thePlayer.getGameProfile()
                    .getName()
                    + " was spawned without restoring the GCInv save.  Run /gcinv restore playername to restore it.");
        }
    }

    private static void writefile() {
        CommandGCInv.savefile.writeToNBT(new NBTTagCompound());
        CommandGCInv.savefile.markDirty();
    }

    private static void initialise() {
        final World world0 = GalacticraftCore.proxy.getWorldForID(0);
        if (world0 == null) {
            return;
        }
        CommandGCInv.savefile = (GCInvSaveData) world0.loadItemData(GCInvSaveData.class, GCInvSaveData.SAVE_ID);
        if (CommandGCInv.savefile == null) {
            CommandGCInv.savefile = new GCInvSaveData();
            world0.setItemData(GCInvSaveData.SAVE_ID, CommandGCInv.savefile);
        }
    }

    public static ItemStack[] getSaveData(String p) {
        if (CommandGCInv.firstuse) {
            CommandGCInv.firstuse = false;
            CommandGCInv.initialise();
        }

        return CommandGCInv.savedata.get(p);
    }
}
