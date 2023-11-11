package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;

public class CommandGCEnergyUnits extends CommandBase
{
    public String getCommandUsage(final ICommandSender var1) {
        String options = " [gJ";
        if (EnergyConfigHandler.isBuildcraftLoaded()) {
            options += "|MJ";
        }
        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            options += "|EU";
        }
        if (EnergyConfigHandler.isMekanismLoaded()) {
            options += "|J";
        }
        options += "|RF";
        return "/" + this.getCommandName() + options + "]";
    }
    
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    public boolean canCommandSenderUseCommand(final ICommandSender par1ICommandSender) {
        return true;
    }
    
    public String getCommandName() {
        return "gcenergyunits";
    }
    
    public void processCommand(final ICommandSender icommandsender, final String[] astring) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
        if (playerBase == null) {
            return;
        }
        if (astring.length == 1) {
            final String param = astring[0].toLowerCase();
            if (param.length() <= 2) {
                int paramvalue = 0;
                if ("gj".equals(param)) {
                    paramvalue = 1;
                }
                else if ("mj".equals(param) && EnergyConfigHandler.isBuildcraftLoaded()) {
                    paramvalue = 2;
                }
                else if ("eu".equals(param) && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                    paramvalue = 3;
                }
                else if ("j".equals(param) && EnergyConfigHandler.isMekanismLoaded()) {
                    paramvalue = 4;
                }
                else if ("rf".equals(param)) {
                    paramvalue = 5;
                }
                if (paramvalue > 0) {
                    GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_ENERGYUNITS, new Object[] { paramvalue }), playerBase);
                    return;
                }
            }
            throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.gcenergyunits.invalidUnits", this.getCommandUsage(icommandsender)), new Object[0]);
        }
        throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.gcenergyunits.noUnits", this.getCommandUsage(icommandsender)), new Object[0]);
    }
    
    public static void handleParamClientside(final int param) {
        if (param == 1) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }
        if (param == 2 && EnergyConfigHandler.isBuildcraftLoaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = true;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }
        if (param == 3 && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = true;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }
        if (param == 4 && EnergyConfigHandler.isMekanismLoaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = true;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }
        if (param == 5) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = true;
        }
    }
}
