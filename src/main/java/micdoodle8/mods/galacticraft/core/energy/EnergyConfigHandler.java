package micdoodle8.mods.galacticraft.core.energy;

import net.minecraftforge.common.config.*;
import java.io.*;
import mekanism.api.gas.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.lang.reflect.*;
import java.util.*;

public class EnergyConfigHandler
{
    private static Configuration config;
    public static float BC3_RATIO;
    public static float RF_RATIO;
    public static float IC2_RATIO;
    public static float MEKANISM_RATIO;
    private static int conversionLossFactor;
    public static float TO_BC_RATIO;
    public static float TO_RF_RATIO;
    public static float TO_IC2_RATIO;
    public static float TO_MEKANISM_RATIO;
    public static float TO_BC_RATIOdisp;
    public static float TO_RF_RATIOdisp;
    public static float TO_IC2_RATIOdisp;
    public static float TO_MEKANISM_RATIOdisp;
    public static Object gasOxygen;
    public static Object gasHydrogen;
    public static boolean displayEnergyUnitsBC;
    public static boolean displayEnergyUnitsIC2;
    public static boolean displayEnergyUnitsMek;
    public static boolean displayEnergyUnitsRF;
    private static boolean cachedIC2Loaded;
    private static boolean cachedIC2LoadedValue;
    private static boolean cachedBCLoaded;
    private static boolean cachedBCLoadedValue;
    private static boolean cachedBCReallyLoaded;
    private static boolean cachedBCReallyLoadedValue;
    private static int cachedBCVersion;
    private static boolean cachedMekLoaded;
    private static boolean cachedMekLoadedValue;
    private static boolean cachedRFLoaded;
    private static boolean cachedRFLoadedValue;
    private static boolean cachedRF1LoadedValue;
    private static boolean cachedRF2LoadedValue;
    private static boolean disableMJinterface;
    public static boolean disableBuildCraftInput;
    public static boolean disableBuildCraftOutput;
    public static boolean disableRFInput;
    public static boolean disableRFOutput;
    public static boolean disableIC2Input;
    public static boolean disableIC2Output;
    public static boolean disableMekanismInput;
    public static boolean disableMekanismOutput;
    
    public static void setDefaultValues(final File file) {
        if (EnergyConfigHandler.config == null) {
            EnergyConfigHandler.config = new Configuration(file);
        }
        EnergyConfigHandler.config.load();
        EnergyConfigHandler.IC2_RATIO = (float)EnergyConfigHandler.config.get("Compatibility", "IndustrialCraft Conversion Ratio", (double)EnergyConfigHandler.IC2_RATIO).getDouble((double)EnergyConfigHandler.IC2_RATIO);
        EnergyConfigHandler.RF_RATIO = (float)EnergyConfigHandler.config.get("Compatibility", "RF Conversion Ratio", (double)EnergyConfigHandler.RF_RATIO).getDouble((double)EnergyConfigHandler.RF_RATIO);
        EnergyConfigHandler.BC3_RATIO = (float)EnergyConfigHandler.config.get("Compatibility", "BuildCraft Conversion Ratio", (double)EnergyConfigHandler.BC3_RATIO).getDouble((double)EnergyConfigHandler.BC3_RATIO);
        EnergyConfigHandler.MEKANISM_RATIO = (float)EnergyConfigHandler.config.get("Compatibility", "Mekanism Conversion Ratio", (double)EnergyConfigHandler.MEKANISM_RATIO).getDouble((double)EnergyConfigHandler.MEKANISM_RATIO);
        EnergyConfigHandler.conversionLossFactor = EnergyConfigHandler.config.get("Compatibility", "Loss factor when converting energy as a percentage (100 = no loss, 90 = 10% loss ...)", 100).getInt(100);
        if (EnergyConfigHandler.conversionLossFactor > 100) {
            EnergyConfigHandler.conversionLossFactor = 100;
        }
        if (EnergyConfigHandler.conversionLossFactor < 5) {
            EnergyConfigHandler.conversionLossFactor = 5;
        }
        updateRatios();
        EnergyConfigHandler.displayEnergyUnitsBC = EnergyConfigHandler.config.get("Display", "If BuildCraft is loaded, show Galacticraft machines energy as MJ instead of gJ?", false).getBoolean(false);
        EnergyConfigHandler.displayEnergyUnitsIC2 = EnergyConfigHandler.config.get("Display", "If IndustrialCraft2 is loaded, show Galacticraft machines energy as EU instead of gJ?", false).getBoolean(false);
        EnergyConfigHandler.displayEnergyUnitsMek = EnergyConfigHandler.config.get("Display", "If Mekanism is loaded, show Galacticraft machines energy as Joules (J) instead of gJ?", false).getBoolean(false);
        EnergyConfigHandler.displayEnergyUnitsRF = EnergyConfigHandler.config.get("Display", "Show Galacticraft machines energy in RF instead of gJ?", false).getBoolean(false);
        EnergyConfigHandler.disableMJinterface = EnergyConfigHandler.config.get("Compatibility", "Disable old Buildcraft API (MJ) interfacing completely?", false).getBoolean(false);
        EnergyConfigHandler.disableBuildCraftInput = EnergyConfigHandler.config.get("Compatibility", "Disable INPUT of BuildCraft energy", false).getBoolean(false);
        EnergyConfigHandler.disableBuildCraftOutput = EnergyConfigHandler.config.get("Compatibility", "Disable OUTPUT of BuildCraft energy", false).getBoolean(false);
        EnergyConfigHandler.disableRFInput = EnergyConfigHandler.config.get("Compatibility", "Disable INPUT of RF energy", false).getBoolean(false);
        EnergyConfigHandler.disableRFOutput = EnergyConfigHandler.config.get("Compatibility", "Disable OUTPUT of RF energy", false).getBoolean(false);
        EnergyConfigHandler.disableIC2Input = EnergyConfigHandler.config.get("Compatibility", "Disable INPUT of IC2 energy", false).getBoolean(false);
        EnergyConfigHandler.disableIC2Output = EnergyConfigHandler.config.get("Compatibility", "Disable OUTPUT of IC2 energy", false).getBoolean(false);
        EnergyConfigHandler.disableMekanismInput = EnergyConfigHandler.config.get("Compatibility", "Disable INPUT of Mekanism energy", false).getBoolean(false);
        EnergyConfigHandler.disableMekanismOutput = EnergyConfigHandler.config.get("Compatibility", "Disable OUTPUT of Mekanism energy", false).getBoolean(false);
        if (!isBuildcraftLoaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
        }
        if (!isIndustrialCraft2Loaded()) {
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
        }
        if (!isMekanismLoaded()) {
            EnergyConfigHandler.displayEnergyUnitsMek = false;
        }
        if (EnergyConfigHandler.displayEnergyUnitsIC2) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
        }
        if (EnergyConfigHandler.displayEnergyUnitsMek) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
        }
        if (EnergyConfigHandler.displayEnergyUnitsRF) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
        }
        EnergyConfigHandler.config.save();
    }
    
    public static void initGas() {
        if (isMekanismLoaded()) {
            final Gas oxygen = GasRegistry.getGas("oxygen");
            if (oxygen == null) {
                EnergyConfigHandler.gasOxygen = GasRegistry.register(new Gas("oxygen")).registerFluid();
            }
            else {
                EnergyConfigHandler.gasOxygen = oxygen;
            }
            final Gas hydrogen = GasRegistry.getGas("hydrogen");
            if (hydrogen == null) {
                EnergyConfigHandler.gasHydrogen = GasRegistry.register(new Gas("hydrogen")).registerFluid();
            }
            else {
                EnergyConfigHandler.gasHydrogen = hydrogen;
            }
        }
    }
    
    public static boolean isIndustrialCraft2Loaded() {
        if (!EnergyConfigHandler.cachedIC2Loaded) {
            EnergyConfigHandler.cachedIC2Loaded = true;
            EnergyConfigHandler.cachedIC2LoadedValue = Loader.isModLoaded("IC2");
        }
        return EnergyConfigHandler.cachedIC2LoadedValue;
    }
    
    public static boolean isBuildcraftLoaded() {
        if (!EnergyConfigHandler.cachedBCLoaded) {
            EnergyConfigHandler.cachedBCLoaded = true;
            EnergyConfigHandler.cachedBCLoadedValue = false;
            if (EnergyConfigHandler.disableMJinterface) {
                return false;
            }
            int count = 0;
            try {
                if (Class.forName("buildcraft.api.mj.MjAPI") != null) {
                    ++count;
                }
                if (Class.forName("buildcraft.api.power.IPowerReceptor") != null) {
                    ++count;
                }
                if (Class.forName("buildcraft.api.power.PowerHandler") != null) {
                    ++count;
                }
                if (Class.forName("buildcraft.api.power.IPowerEmitter") != null) {
                    ++count;
                }
                if (Class.forName("buildcraft.api.mj.IBatteryObject") != null) {
                    ++count;
                }
                if (Class.forName("buildcraft.api.mj.ISidedBatteryProvider") != null) {
                    ++count;
                }
            }
            catch (Exception ex) {}
            if (count < 6) {
                return false;
            }
            try {
                final Class clazz = Class.forName("buildcraft.api.core.JavaTools");
                final Method methodz = clazz.getMethod("getAllFields", Class.class);
                if (methodz != null && methodz.getReturnType() == List.class) {
                    return EnergyConfigHandler.cachedBCLoadedValue = true;
                }
            }
            catch (Exception ex2) {}
            GCLog.severe("Other mods with two different versions of Buildcraft API detected.  Galacticraft cannot use MJ until this is fixed.  You may have more serious problems with other mods.  More info at: http://wiki.micdoodle8.com/wiki/Compatibility.");
        }
        return EnergyConfigHandler.cachedBCLoadedValue;
    }
    
    public static boolean isBuildcraftReallyLoaded() {
        if (!EnergyConfigHandler.cachedBCReallyLoaded) {
            EnergyConfigHandler.cachedBCReallyLoaded = true;
            EnergyConfigHandler.cachedBCReallyLoadedValue = Loader.isModLoaded("BuildCraft|Energy");
        }
        return EnergyConfigHandler.cachedBCReallyLoadedValue;
    }
    
    public static int getBuildcraftVersion() {
        if (EnergyConfigHandler.cachedBCVersion != -1) {
            return EnergyConfigHandler.cachedBCVersion;
        }
        if (EnergyConfigHandler.cachedBCLoaded) {
            boolean bc6Found = true;
            try {
                Class.forName("buildcraft.api.mj.MjAPI");
            }
            catch (Throwable t) {
                bc6Found = false;
            }
            if (bc6Found) {
                EnergyConfigHandler.cachedBCVersion = 6;
            }
            else {
                EnergyConfigHandler.cachedBCVersion = 5;
            }
        }
        return EnergyConfigHandler.cachedBCVersion;
    }
    
    public static boolean isRFAPILoaded() {
        if (!EnergyConfigHandler.cachedRFLoaded) {
            initialiseRF();
        }
        return EnergyConfigHandler.cachedRFLoadedValue;
    }
    
    public static boolean isRFAPIv1Loaded() {
        if (!EnergyConfigHandler.cachedRFLoaded) {
            initialiseRF();
        }
        return EnergyConfigHandler.cachedRF1LoadedValue;
    }
    
    public static boolean isRFAPIv2Loaded() {
        if (!EnergyConfigHandler.cachedRFLoaded) {
            initialiseRF();
        }
        return EnergyConfigHandler.cachedRF2LoadedValue;
    }
    
    private static void initialiseRF() {
        EnergyConfigHandler.cachedRFLoaded = true;
        EnergyConfigHandler.cachedRFLoadedValue = false;
        EnergyConfigHandler.cachedRF2LoadedValue = false;
        int count = 0;
        int count2 = 0;
        try {
            if (Class.forName("cofh.api.energy.IEnergyConnection") != null) {
                ++count;
            }
            if (Class.forName("cofh.api.energy.IEnergyHandler") != null) {
                count += 2;
            }
        }
        catch (Exception ex) {}
        try {
            if (Class.forName("cofh.api.energy.IEnergyProvider") != null) {
                ++count2;
            }
        }
        catch (Exception ex2) {}
        try {
            if (Class.forName("cofh.api.energy.IEnergyReceiver") != null) {
                ++count2;
            }
        }
        catch (Exception ex3) {}
        if ((count + count2 == 3 && count2 != 1) || count + count2 == 5) {
            EnergyConfigHandler.cachedRFLoadedValue = true;
            EnergyConfigHandler.cachedRF1LoadedValue = (count == 3);
            EnergyConfigHandler.cachedRF2LoadedValue = (count2 == 2);
        }
        else if (count > 0 || count2 > 0) {
            GCLog.severe("Incomplete Redstone Flux API detected: Galacticraft will not support RF energy connections until this is fixed.");
        }
    }
    
    public static boolean isMekanismLoaded() {
        if (!EnergyConfigHandler.cachedMekLoaded) {
            EnergyConfigHandler.cachedMekLoaded = true;
            EnergyConfigHandler.cachedMekLoadedValue = Loader.isModLoaded("Mekanism");
        }
        return EnergyConfigHandler.cachedMekLoadedValue;
    }
    
    private static void updateRatios() {
        if (EnergyConfigHandler.IC2_RATIO < 0.01f) {
            EnergyConfigHandler.IC2_RATIO = 0.01f;
        }
        if (EnergyConfigHandler.RF_RATIO < 0.001f) {
            EnergyConfigHandler.RF_RATIO = 0.001f;
        }
        if (EnergyConfigHandler.BC3_RATIO < 0.01f) {
            EnergyConfigHandler.BC3_RATIO = 0.01f;
        }
        if (EnergyConfigHandler.MEKANISM_RATIO < 0.001f) {
            EnergyConfigHandler.MEKANISM_RATIO = 0.001f;
        }
        if (EnergyConfigHandler.IC2_RATIO > 1000.0f) {
            EnergyConfigHandler.IC2_RATIO = 1000.0f;
        }
        if (EnergyConfigHandler.RF_RATIO > 100.0f) {
            EnergyConfigHandler.RF_RATIO = 100.0f;
        }
        if (EnergyConfigHandler.BC3_RATIO > 1000.0f) {
            EnergyConfigHandler.BC3_RATIO = 1000.0f;
        }
        if (EnergyConfigHandler.MEKANISM_RATIO > 100.0f) {
            EnergyConfigHandler.MEKANISM_RATIO = 100.0f;
        }
        final float factor = EnergyConfigHandler.conversionLossFactor / 100.0f;
        EnergyConfigHandler.TO_BC_RATIO = factor / EnergyConfigHandler.BC3_RATIO;
        EnergyConfigHandler.TO_RF_RATIO = factor / EnergyConfigHandler.RF_RATIO;
        EnergyConfigHandler.TO_IC2_RATIO = factor / EnergyConfigHandler.IC2_RATIO;
        EnergyConfigHandler.TO_MEKANISM_RATIO = factor / EnergyConfigHandler.MEKANISM_RATIO;
        EnergyConfigHandler.TO_BC_RATIOdisp = 1.0f / EnergyConfigHandler.BC3_RATIO;
        EnergyConfigHandler.TO_RF_RATIOdisp = 1.0f / EnergyConfigHandler.RF_RATIO;
        EnergyConfigHandler.TO_IC2_RATIOdisp = 1.0f / EnergyConfigHandler.IC2_RATIO;
        EnergyConfigHandler.TO_MEKANISM_RATIOdisp = 1.0f / EnergyConfigHandler.MEKANISM_RATIO;
        EnergyConfigHandler.BC3_RATIO *= factor;
        EnergyConfigHandler.RF_RATIO *= factor;
        EnergyConfigHandler.IC2_RATIO *= factor;
        EnergyConfigHandler.MEKANISM_RATIO *= factor;
    }
    
    public static void serverConfigOverride(final ArrayList<Object> returnList) {
        returnList.add(EnergyConfigHandler.BC3_RATIO);
        returnList.add(EnergyConfigHandler.RF_RATIO);
        returnList.add(EnergyConfigHandler.IC2_RATIO);
        returnList.add(EnergyConfigHandler.MEKANISM_RATIO);
        returnList.add(EnergyConfigHandler.conversionLossFactor);
    }
    
    public static void setConfigOverride(final float sBC3, final float sRF, final float sIC2, final float sMEK, final int sLossRatio) {
        EnergyConfigHandler.BC3_RATIO = sBC3;
        EnergyConfigHandler.RF_RATIO = sRF;
        EnergyConfigHandler.IC2_RATIO = sIC2;
        EnergyConfigHandler.MEKANISM_RATIO = sMEK;
        EnergyConfigHandler.conversionLossFactor = sLossRatio;
        updateRatios();
    }
    
    static {
        EnergyConfigHandler.BC3_RATIO = 16.0f;
        EnergyConfigHandler.RF_RATIO = EnergyConfigHandler.BC3_RATIO / 10.0f;
        EnergyConfigHandler.IC2_RATIO = EnergyConfigHandler.BC3_RATIO / 2.44f;
        EnergyConfigHandler.MEKANISM_RATIO = EnergyConfigHandler.IC2_RATIO / 10.0f;
        EnergyConfigHandler.conversionLossFactor = 100;
        EnergyConfigHandler.TO_BC_RATIO = 1.0f / EnergyConfigHandler.BC3_RATIO;
        EnergyConfigHandler.TO_RF_RATIO = 1.0f / EnergyConfigHandler.RF_RATIO;
        EnergyConfigHandler.TO_IC2_RATIO = 1.0f / EnergyConfigHandler.IC2_RATIO;
        EnergyConfigHandler.TO_MEKANISM_RATIO = 1.0f / EnergyConfigHandler.MEKANISM_RATIO;
        EnergyConfigHandler.TO_BC_RATIOdisp = 1.0f / EnergyConfigHandler.BC3_RATIO;
        EnergyConfigHandler.TO_RF_RATIOdisp = 1.0f / EnergyConfigHandler.RF_RATIO;
        EnergyConfigHandler.TO_IC2_RATIOdisp = 1.0f / EnergyConfigHandler.IC2_RATIO;
        EnergyConfigHandler.TO_MEKANISM_RATIOdisp = 1.0f / EnergyConfigHandler.MEKANISM_RATIO;
        EnergyConfigHandler.gasOxygen = null;
        EnergyConfigHandler.gasHydrogen = null;
        EnergyConfigHandler.displayEnergyUnitsBC = false;
        EnergyConfigHandler.displayEnergyUnitsIC2 = false;
        EnergyConfigHandler.displayEnergyUnitsMek = false;
        EnergyConfigHandler.displayEnergyUnitsRF = false;
        EnergyConfigHandler.cachedIC2Loaded = false;
        EnergyConfigHandler.cachedIC2LoadedValue = false;
        EnergyConfigHandler.cachedBCLoaded = false;
        EnergyConfigHandler.cachedBCLoadedValue = false;
        EnergyConfigHandler.cachedBCReallyLoaded = false;
        EnergyConfigHandler.cachedBCReallyLoadedValue = false;
        EnergyConfigHandler.cachedBCVersion = -1;
        EnergyConfigHandler.cachedMekLoaded = false;
        EnergyConfigHandler.cachedMekLoadedValue = false;
        EnergyConfigHandler.cachedRFLoaded = false;
        EnergyConfigHandler.cachedRFLoadedValue = false;
        EnergyConfigHandler.cachedRF1LoadedValue = false;
        EnergyConfigHandler.cachedRF2LoadedValue = false;
        EnergyConfigHandler.disableMJinterface = false;
        EnergyConfigHandler.disableBuildCraftInput = false;
        EnergyConfigHandler.disableBuildCraftOutput = false;
        EnergyConfigHandler.disableRFInput = false;
        EnergyConfigHandler.disableRFOutput = false;
        EnergyConfigHandler.disableIC2Input = false;
        EnergyConfigHandler.disableIC2Output = false;
        EnergyConfigHandler.disableMekanismInput = false;
        EnergyConfigHandler.disableMekanismOutput = false;
    }
}
