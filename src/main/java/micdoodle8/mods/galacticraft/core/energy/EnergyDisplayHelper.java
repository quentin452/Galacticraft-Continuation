package micdoodle8.mods.galacticraft.core.energy;

import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class EnergyDisplayHelper {

    public static void getEnergyDisplayTooltip(float energyVal, float maxEnergy, List<String> strList) {
        strList.add(
            EnumChatFormatting.GREEN + StatCollector.translateToLocal("gui.message.energy")
                + ": "
                + getEnergyDisplayS(energyVal));
        strList.add(
            EnumChatFormatting.RED + StatCollector.translateToLocal("gui.message.maxEnergy")
                + ": "
                + getEnergyDisplayS(maxEnergy));
    }

    public static String getEnergyDisplayS(float energyVal) {
        if (EnergyConfigHandler.displayEnergyUnitsIC2) {
            return getEnergyDisplayIC2(energyVal * EnergyConfigHandler.TO_IC2_RATIOdisp);
        }
        if (EnergyConfigHandler.displayEnergyUnitsBC) {
            return getEnergyDisplayBC(energyVal * EnergyConfigHandler.TO_BC_RATIOdisp);
        }
        if (EnergyConfigHandler.displayEnergyUnitsMek) {
            return getEnergyDisplayMek(energyVal * EnergyConfigHandler.TO_MEKANISM_RATIOdisp);
        } else if (EnergyConfigHandler.displayEnergyUnitsRF) {
            return getEnergyDisplayRF(energyVal * EnergyConfigHandler.TO_RF_RATIOdisp);
        }
        final String val = String.valueOf(getEnergyDisplayI(energyVal));
        StringBuilder newVal = new StringBuilder();

        for (int i = val.length() - 1; i >= 0; i--) {
            newVal.append(val.charAt(val.length() - 1 - i));
            if (i % 3 == 0 && i != 0) {
                newVal.append(',');
            }
        }

        return newVal.append(" gJ")
            .toString();
    }

    public static String getEnergyDisplayIC2(float energyVal) {
        final String val = String.valueOf(getEnergyDisplayI(energyVal));
        StringBuilder newVal = new StringBuilder();

        for (int i = val.length() - 1; i >= 0; i--) {
            newVal.append(val.charAt(val.length() - 1 - i));
            if (i % 3 == 0 && i != 0) {
                newVal.append(',');
            }
        }

        return newVal.append(" EU")
            .toString();
    }

    public static String getEnergyDisplayBC(float energyVal) {
        final String val = String.valueOf(getEnergyDisplayI(energyVal));

        return val + " MJ";
    }

    public static String getEnergyDisplayMek(float energyVal) {
        if (energyVal < 1000) {
            final String val = String.valueOf(getEnergyDisplayI(energyVal));
            return val + " J";
        }
        if (energyVal < 1000000) {
            final String val = getEnergyDisplay1DP(energyVal / 1000);
            return val + " kJ";
        }
        final String val = getEnergyDisplay1DP(energyVal / 1000000);
        return val + " MJ";
    }

    public static String getEnergyDisplayRF(float energyVal) {
        final String val = String.valueOf(getEnergyDisplayI(energyVal));

        return val + " RF";
    }

    public static int getEnergyDisplayI(float energyVal) {
        return MathHelper.floor_float(energyVal);
    }

    public static String getEnergyDisplay1DP(float energyVal) {
        return "" + MathHelper.floor_float(energyVal)
            + "."
            + MathHelper.floor_float(energyVal * 10) % 10
            + MathHelper.floor_float(energyVal * 100) % 10;
    }
}
