package micdoodle8.mods.galacticraft.core.energy;

import java.util.*;
import net.minecraft.util.*;

public class EnergyDisplayHelper
{
    public static void getEnergyDisplayTooltip(final float energyVal, final float maxEnergy, final List strList) {
        strList.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("gui.message.energy") + ": " + getEnergyDisplayS(energyVal));
        strList.add(EnumChatFormatting.RED + StatCollector.translateToLocal("gui.message.maxEnergy") + ": " + getEnergyDisplayS(maxEnergy));
    }
    
    public static String getEnergyDisplayS(final float energyVal) {
        if (EnergyConfigHandler.displayEnergyUnitsIC2) {
            return getEnergyDisplayIC2(energyVal * EnergyConfigHandler.TO_IC2_RATIOdisp);
        }
        if (EnergyConfigHandler.displayEnergyUnitsBC) {
            return getEnergyDisplayBC(energyVal * EnergyConfigHandler.TO_BC_RATIOdisp);
        }
        if (EnergyConfigHandler.displayEnergyUnitsMek) {
            return getEnergyDisplayMek(energyVal * EnergyConfigHandler.TO_MEKANISM_RATIOdisp);
        }
        if (EnergyConfigHandler.displayEnergyUnitsRF) {
            return getEnergyDisplayRF(energyVal * EnergyConfigHandler.TO_RF_RATIOdisp);
        }
        final String val = String.valueOf(getEnergyDisplayI(energyVal));
        String newVal = "";
        for (int i = val.length() - 1; i >= 0; --i) {
            newVal += val.charAt(val.length() - 1 - i);
            if (i % 3 == 0 && i != 0) {
                newVal += ',';
            }
        }
        return newVal + " gJ";
    }
    
    public static String getEnergyDisplayIC2(final float energyVal) {
        final String val = String.valueOf(getEnergyDisplayI(energyVal));
        String newVal = "";
        for (int i = val.length() - 1; i >= 0; --i) {
            newVal += val.charAt(val.length() - 1 - i);
            if (i % 3 == 0 && i != 0) {
                newVal += ',';
            }
        }
        return newVal + " EU";
    }
    
    public static String getEnergyDisplayBC(final float energyVal) {
        final String val = String.valueOf(getEnergyDisplayI(energyVal));
        return val + " MJ";
    }
    
    public static String getEnergyDisplayMek(final float energyVal) {
        if (energyVal < 1000.0f) {
            final String val = String.valueOf(getEnergyDisplayI(energyVal));
            return val + " J";
        }
        if (energyVal < 1000000.0f) {
            final String val = getEnergyDisplay1DP(energyVal / 1000.0f);
            return val + " kJ";
        }
        final String val = getEnergyDisplay1DP(energyVal / 1000000.0f);
        return val + " MJ";
    }
    
    public static String getEnergyDisplayRF(final float energyVal) {
        final String val = String.valueOf(getEnergyDisplayI(energyVal));
        return val + " RF";
    }
    
    public static int getEnergyDisplayI(final float energyVal) {
        return MathHelper.floor_float(energyVal);
    }
    
    public static String getEnergyDisplay1DP(final float energyVal) {
        return "" + MathHelper.floor_float(energyVal) + "." + MathHelper.floor_float(energyVal * 10.0f) % 10 + MathHelper.floor_float(energyVal * 100.0f) % 10;
    }
}
