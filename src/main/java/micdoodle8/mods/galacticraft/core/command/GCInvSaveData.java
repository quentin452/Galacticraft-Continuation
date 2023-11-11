package micdoodle8.mods.galacticraft.core.command;

import net.minecraft.world.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.nbt.*;

public class GCInvSaveData extends WorldSavedData
{
    public static final String SAVE_ID = "GCInv_savefile";
    
    public GCInvSaveData() {
        super("GCInv_savefile");
    }
    
    public GCInvSaveData(final String name) {
        super(name);
    }
    
    public void readFromNBT(final NBTTagCompound filedata) {
        for (final Object obj : filedata.func_150296_c()) {
            if (obj instanceof NBTTagList) {
                final NBTTagList entry = (NBTTagList)obj;
                final String name = entry.toString();
                final ItemStack[] saveinv = new ItemStack[6];
                if (entry.tagCount() > 0) {
                    for (int j = 0; j < entry.tagCount(); ++j) {
                        final NBTTagCompound obj2 = entry.getCompoundTagAt(j);
                        if (obj2 != null) {
                            final int i = obj2.getByte("Slot") & 0x7;
                            if (i >= 6) {
                                System.out.println("GCInv error retrieving savefile: slot was outside range 0-5");
                                return;
                            }
                            saveinv[i] = ItemStack.loadItemStackFromNBT(obj2);
                        }
                    }
                }
                CommandGCInv.savedata.put(name.toLowerCase(), saveinv);
            }
        }
    }
    
    public void writeToNBT(final NBTTagCompound toSave) {
        for (final String name : CommandGCInv.savedata.keySet()) {
            final NBTTagList par1NBTTagList = new NBTTagList();
            final ItemStack[] saveinv = CommandGCInv.savedata.get(name);
            for (int i = 0; i < 6; ++i) {
                if (saveinv[i] != null) {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte)(i + 200));
                    saveinv[i].writeToNBT(nbttagcompound);
                    par1NBTTagList.appendTag((NBTBase)nbttagcompound);
                }
            }
            toSave.setTag(name, (NBTBase)par1NBTTagList);
        }
    }
}
