package micdoodle8.mods.galacticraft.api.world;

import java.lang.reflect.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;

public class OxygenHooks
{
    private static Class<?> oxygenUtilClass;
    private static Method combusionTestMethod;
    private static Method breathableAirBlockMethod;
    private static Method breathableAirBlockEntityMethod;
    private static Method torchHasOxygenMethod;
    private static Method oxygenBubbleMethod;
    private static Method validOxygenSetupMethod;
    
    public static boolean noAtmosphericCombustion(final WorldProvider provider) {
        try {
            if (OxygenHooks.combusionTestMethod == null) {
                if (OxygenHooks.oxygenUtilClass == null) {
                    OxygenHooks.oxygenUtilClass = Class.forName("micdoodle8.mods.galacticraft.core.util.OxygenUtil");
                }
                OxygenHooks.combusionTestMethod = OxygenHooks.oxygenUtilClass.getDeclaredMethod("noAtmosphericCombustion", WorldProvider.class);
            }
            return (boolean)OxygenHooks.combusionTestMethod.invoke(null, provider);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isAABBInBreathableAirBlock(final World world, final AxisAlignedBB bb) {
        try {
            if (OxygenHooks.breathableAirBlockMethod == null) {
                if (OxygenHooks.oxygenUtilClass == null) {
                    OxygenHooks.oxygenUtilClass = Class.forName("micdoodle8.mods.galacticraft.core.util.OxygenUtil");
                }
                OxygenHooks.breathableAirBlockMethod = OxygenHooks.oxygenUtilClass.getDeclaredMethod("isAABBInBreathableAirBlock", World.class, AxisAlignedBB.class);
            }
            return (boolean)OxygenHooks.breathableAirBlockMethod.invoke(null, world, bb);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isAABBInBreathableAirBlock(final EntityLivingBase entity) {
        try {
            if (OxygenHooks.breathableAirBlockEntityMethod == null) {
                if (OxygenHooks.oxygenUtilClass == null) {
                    OxygenHooks.oxygenUtilClass = Class.forName("micdoodle8.mods.galacticraft.core.util.OxygenUtil");
                }
                OxygenHooks.breathableAirBlockEntityMethod = OxygenHooks.oxygenUtilClass.getDeclaredMethod("isAABBInBreathableAirBlock", EntityLivingBase.class);
            }
            return (boolean)OxygenHooks.breathableAirBlockEntityMethod.invoke(null, entity);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean checkTorchHasOxygen(final World world, final Block block, final int x, final int y, final int z) {
        try {
            if (OxygenHooks.torchHasOxygenMethod == null) {
                if (OxygenHooks.oxygenUtilClass == null) {
                    OxygenHooks.oxygenUtilClass = Class.forName("micdoodle8.mods.galacticraft.core.util.OxygenUtil");
                }
                OxygenHooks.torchHasOxygenMethod = OxygenHooks.oxygenUtilClass.getDeclaredMethod("checkTorchHasOxygen", World.class, Block.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            }
            return (boolean)OxygenHooks.torchHasOxygenMethod.invoke(null, world, block, x, y, z);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean inOxygenBubble(final World worldObj, final double avgX, final double avgY, final double avgZ) {
        try {
            if (OxygenHooks.oxygenBubbleMethod == null) {
                if (OxygenHooks.oxygenUtilClass == null) {
                    OxygenHooks.oxygenUtilClass = Class.forName("micdoodle8.mods.galacticraft.core.util.OxygenUtil");
                }
                OxygenHooks.oxygenBubbleMethod = OxygenHooks.oxygenUtilClass.getDeclaredMethod("inOxygenBubble", World.class, Double.TYPE, Double.TYPE, Double.TYPE);
            }
            return (boolean)OxygenHooks.oxygenBubbleMethod.invoke(null, worldObj, avgX, avgY, avgZ);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasValidOxygenSetup(final EntityPlayerMP player) {
        try {
            if (OxygenHooks.validOxygenSetupMethod == null) {
                if (OxygenHooks.oxygenUtilClass == null) {
                    OxygenHooks.oxygenUtilClass = Class.forName("micdoodle8.mods.galacticraft.core.util.OxygenUtil");
                }
                OxygenHooks.validOxygenSetupMethod = OxygenHooks.oxygenUtilClass.getDeclaredMethod("hasValidOxygenSetup", EntityPlayerMP.class);
            }
            return (boolean)OxygenHooks.validOxygenSetupMethod.invoke(null, player);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
