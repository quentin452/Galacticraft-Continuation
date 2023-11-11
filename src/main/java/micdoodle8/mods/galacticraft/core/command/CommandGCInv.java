package micdoodle8.mods.galacticraft.core.command;

import net.minecraft.item.*;
import net.minecraft.server.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import java.util.*;

public class CommandGCInv extends CommandBase
{
    protected static final Map<String, ItemStack[]> savedata;
    private static final Set<String> dontload;
    private static boolean firstuse;
    private static GCInvSaveData savefile;
    
    public String getCommandUsage(final ICommandSender var1) {
        return "/" + this.getCommandName() + " [save|restore|drop|clear] <playername>";
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    public String getCommandName() {
        return "gcinv";
    }
    
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr) {
        if (par2ArrayOfStr.length == 1) {
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] { "save", "restore", "drop", "clear" });
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
        if (CommandGCInv.firstuse) {
            CommandGCInv.firstuse = false;
            initialise();
        }
        if (astring.length == 2) {
            try {
                final EntityPlayerMP thePlayer = PlayerUtil.getPlayerBaseServerFromPlayerUsername(astring[1], true);
                if (thePlayer != null && !thePlayer.isDead && thePlayer.worldObj != null) {
                    final GCPlayerStats stats = GCPlayerStats.get(thePlayer);
                    if (astring[0].equalsIgnoreCase("drop")) {
                        final InventoryExtended gcInventory = stats.extendedInventory;
                        gcInventory.dropExtendedItems((EntityPlayer)thePlayer);
                    }
                    else if (astring[0].equalsIgnoreCase("save")) {
                        final InventoryExtended gcInventory = stats.extendedInventory;
                        final ItemStack[] saveinv = new ItemStack[gcInventory.getSizeInventory()];
                        for (int i = 0; i < gcInventory.getSizeInventory(); ++i) {
                            saveinv[i] = gcInventory.getStackInSlot(i);
                            gcInventory.setInventorySlotContents(i, null);
                        }
                        CommandGCInv.savedata.put(astring[1].toLowerCase(), saveinv);
                        CommandGCInv.dontload.add(astring[1].toLowerCase());
                        writefile();
                        System.out.println("[GCInv] Saving and clearing GC inventory slots of " + thePlayer.getGameProfile().getName());
                    }
                    else if (astring[0].equalsIgnoreCase("restore")) {
                        final ItemStack[] saveinv2 = CommandGCInv.savedata.get(astring[1].toLowerCase());
                        CommandGCInv.dontload.remove(astring[1].toLowerCase());
                        if (saveinv2 == null) {
                            System.out.println("[GCInv] Tried to restore but player " + thePlayer.getGameProfile().getName() + " had no saved GC inventory items.");
                            return;
                        }
                        doLoad(thePlayer);
                    }
                    else {
                        if (!astring[0].equalsIgnoreCase("clear")) {
                            throw new WrongUsageException("Invalid GCInv command. Usage: " + this.getCommandUsage(icommandsender), new Object[0]);
                        }
                        final InventoryExtended gcInventory = stats.extendedInventory;
                        for (int j = 0; j < gcInventory.getSizeInventory(); ++j) {
                            gcInventory.setInventorySlotContents(j, null);
                        }
                    }
                }
                else {
                    if (astring[0].equalsIgnoreCase("restore")) {
                        final ItemStack[] saveinv3 = CommandGCInv.savedata.get(astring[1].toLowerCase());
                        if (saveinv3 != null) {
                            System.out.println("[GCInv] Restore command for offline player " + astring[1] + ", setting to restore GCInv on next login.");
                            CommandGCInv.dontload.remove(astring[1].toLowerCase());
                            return;
                        }
                    }
                    if (!astring[0].equalsIgnoreCase("clear") && !astring[0].equalsIgnoreCase("save") && !astring[0].equalsIgnoreCase("drop")) {
                        throw new WrongUsageException("Invalid GCInv command. Usage: " + this.getCommandUsage(icommandsender), new Object[0]);
                    }
                    System.out.println("GCInv command: player " + astring[1] + " not found.");
                }
            }
            catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            return;
        }
        throw new WrongUsageException("Not enough command arguments! Usage: " + this.getCommandUsage(icommandsender), new Object[0]);
    }
    
    public static void doLoad(final EntityPlayerMP thePlayer) {
        final String theName = thePlayer.getGameProfile().getName().toLowerCase();
        if (!CommandGCInv.dontload.contains(theName)) {
            final ItemStack[] saveinv = CommandGCInv.savedata.get(theName);
            final InventoryExtended gcInventory = GCPlayerStats.get(thePlayer).extendedInventory;
            for (int i = 0; i < gcInventory.getSizeInventory(); ++i) {
                gcInventory.setInventorySlotContents(i, saveinv[i]);
            }
            CommandGCInv.savedata.remove(theName);
            writefile();
            System.out.println("[GCInv] Restored GC inventory slots of " + thePlayer.getGameProfile().getName());
        }
        else {
            System.out.println("[GCInv] Player " + thePlayer.getGameProfile().getName() + " was spawned without restoring the GCInv save.  Run /gcinv restore playername to restore it.");
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
        CommandGCInv.savefile = (GCInvSaveData)world0.loadItemData((Class)GCInvSaveData.class, "GCInv_savefile");
        if (CommandGCInv.savefile == null) {
            world0.setItemData("GCInv_savefile", (WorldSavedData)(CommandGCInv.savefile = new GCInvSaveData()));
        }
    }
    
    public static ItemStack[] getSaveData(final String p) {
        if (CommandGCInv.firstuse) {
            CommandGCInv.firstuse = false;
            initialise();
        }
        return CommandGCInv.savedata.get(p);
    }
    
    static {
        savedata = new HashMap<String, ItemStack[]>();
        dontload = new HashSet<String>();
        CommandGCInv.firstuse = true;
    }
}
