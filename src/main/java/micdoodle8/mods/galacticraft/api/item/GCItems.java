package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.*;
import java.util.*;
import java.lang.reflect.*;

public class GCItems
{
    private static Object blocksList;
    private static Object itemsList;
    
    public static ItemStack requestItem(final String key, final int amount) {
        try {
            if (GCItems.itemsList == null) {
                final Class<?> clazz = Class.forName(getItemListClass());
                final Field f = clazz.getDeclaredField("itemList");
                GCItems.itemsList = f.get(null);
            }
            if (GCItems.itemsList instanceof HashMap) {
                final HashMap<String, ItemStack> blockMap = (HashMap<String, ItemStack>)GCItems.itemsList;
                final ItemStack stack = blockMap.get(key);
                return new ItemStack(stack.getItem(), amount, stack.getItemDamage());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static ItemStack requestBlock(final String key, final int amount) {
        try {
            if (GCItems.blocksList == null) {
                final Class<?> clazz = Class.forName(getItemListClass());
                final Field f = clazz.getDeclaredField("blocksList");
                GCItems.blocksList = f.get(null);
            }
            if (GCItems.blocksList instanceof HashMap) {
                final HashMap<String, ItemStack> blockMap = (HashMap<String, ItemStack>)GCItems.blocksList;
                final ItemStack stack = blockMap.get(key);
                return new ItemStack(stack.getItem(), amount, stack.getItemDamage());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static String getItemListClass() {
        return "micdoodle8.mods.galacticraft.core.GalacticraftCore";
    }
}
