package micdoodle8.mods.galacticraft.api.inventory;

import java.lang.reflect.*;
import net.minecraft.entity.player.*;

public class AccessInventoryGC
{
    private static Class<?> playerStatsClass;
    private static Method getMethod;
    private static Field extendedInventoryField;
    
    public static IInventoryGC getGCInventoryForPlayer(final EntityPlayerMP player) {
        try {
            if (AccessInventoryGC.playerStatsClass == null || AccessInventoryGC.getMethod == null || AccessInventoryGC.extendedInventoryField == null) {
                AccessInventoryGC.playerStatsClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats");
                AccessInventoryGC.getMethod = AccessInventoryGC.playerStatsClass.getMethod("get", EntityPlayerMP.class);
                AccessInventoryGC.extendedInventoryField = AccessInventoryGC.playerStatsClass.getField("extendedInventory");
            }
            final Object stats = AccessInventoryGC.getMethod.invoke(null, player);
            if (stats == null) {
                return null;
            }
            return (IInventoryGC)AccessInventoryGC.extendedInventoryField.get(stats);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
