package micdoodle8.mods.galacticraft.core.energy;

import java.lang.reflect.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import mekanism.api.energy.*;
import cofh.api.energy.*;
import buildcraft.api.mj.*;
import net.minecraft.world.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.util.*;
import buildcraft.api.power.*;
import ic2.api.energy.tile.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class EnergyUtil
{
    private static boolean isMekLoaded;
    private static boolean isRFLoaded;
    private static boolean isRF1Loaded;
    private static boolean isRF2Loaded;
    private static boolean isIC2Loaded;
    private static boolean isBCLoaded;
    private static boolean isBC6Loaded;
    private static boolean isBCReallyLoaded;
    public static boolean voltageParameterIC2;
    public static Method demandedEnergyIC2;
    public static Method injectEnergyIC2;
    public static Method offeredEnergyIC2;
    public static Method drawEnergyIC2;
    private static Class<?> clazzMekCable;
    public static Class<?> clazzEnderIOCable;
    public static Class<?> clazzMFRRednetEnergyCable;
    public static Class<?> clazzRailcraftEngine;
    private static Class<?> clazzPipeTile;
    private static Class<?> clazzPipeWood;
    public static boolean initialisedIC2Methods;
    
    public static TileEntity[] getAdjacentPowerConnections(final TileEntity tile) {
        final TileEntity[] adjacentConnections = new TileEntity[6];
        final BlockVec3 thisVec = new BlockVec3(tile);
        final World world = tile.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);
            Label_0578: {
                if (tileEntity != null) {
                    if (tileEntity instanceof IConnector) {
                        if (((IConnector)tileEntity).canConnect(direction.getOpposite(), NetworkType.POWER)) {
                            adjacentConnections[direction.ordinal()] = tileEntity;
                        }
                    }
                    else {
                        Label_0233: {
                            if (EnergyUtil.isMekLoaded) {
                                if (!(tileEntity instanceof IStrictEnergyAcceptor)) {
                                    if (!(tileEntity instanceof ICableOutputter)) {
                                        break Label_0233;
                                    }
                                }
                                try {
                                    if (EnergyUtil.clazzMekCable != null && EnergyUtil.clazzMekCable.isInstance(tileEntity)) {
                                        break Label_0578;
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (tileEntity instanceof IStrictEnergyAcceptor && ((IStrictEnergyAcceptor)tileEntity).canReceiveEnergy(direction.getOpposite())) {
                                    adjacentConnections[direction.ordinal()] = tileEntity;
                                    break Label_0578;
                                }
                                if (tileEntity instanceof ICableOutputter && ((ICableOutputter)tileEntity).canOutputTo(direction.getOpposite())) {
                                    adjacentConnections[direction.ordinal()] = tileEntity;
                                }
                                break Label_0578;
                            }
                        }
                        if (!EnergyUtil.isBCReallyLoaded || !EnergyUtil.clazzPipeTile.isInstance(tileEntity)) {
                            if (EnergyUtil.isIC2Loaded && tileEntity instanceof IEnergyTile) {
                                if (tileEntity instanceof IEnergyConductor) {
                                    break Label_0578;
                                }
                                boolean doneIC2 = false;
                                if (tileEntity instanceof IEnergyAcceptor) {
                                    doneIC2 = true;
                                    if (((IEnergyAcceptor)tileEntity).acceptsEnergyFrom(tile, direction.getOpposite())) {
                                        adjacentConnections[direction.ordinal()] = tileEntity;
                                    }
                                }
                                if (tileEntity instanceof IEnergyEmitter) {
                                    doneIC2 = true;
                                    if (((IEnergyEmitter)tileEntity).emitsEnergyTo(tile, direction.getOpposite())) {
                                        adjacentConnections[direction.ordinal()] = tileEntity;
                                    }
                                }
                                if (doneIC2) {
                                    break Label_0578;
                                }
                            }
                            if (EnergyUtil.isRFLoaded && tileEntity instanceof IEnergyConnection) {
                                if ((EnergyUtil.isRF2Loaded && (tileEntity instanceof IEnergyProvider || tileEntity instanceof IEnergyReceiver)) || (EnergyUtil.isRF1Loaded && tileEntity instanceof IEnergyHandler) || (EnergyUtil.clazzRailcraftEngine != null && EnergyUtil.clazzRailcraftEngine.isInstance(tileEntity))) {
                                    if (EnergyUtil.clazzEnderIOCable == null || !EnergyUtil.clazzEnderIOCable.isInstance(tileEntity)) {
                                        if (EnergyUtil.clazzMFRRednetEnergyCable == null || !EnergyUtil.clazzMFRRednetEnergyCable.isInstance(tileEntity)) {
                                            if (((IEnergyConnection)tileEntity).canConnectEnergy(direction.getOpposite())) {
                                                adjacentConnections[direction.ordinal()] = tileEntity;
                                            }
                                        }
                                    }
                                }
                            }
                            else if (EnergyUtil.isBCLoaded) {
                                if (EnergyUtil.isBC6Loaded && MjAPI.getMjBattery((Object)tileEntity, "buildcraft.kinesis", direction.getOpposite()) != null) {
                                    adjacentConnections[direction.ordinal()] = tileEntity;
                                }
                                else if (tileEntity instanceof IPowerReceptor && ((IPowerReceptor)tileEntity).getPowerReceiver(direction.getOpposite()) != null) {
                                    adjacentConnections[direction.ordinal()] = tileEntity;
                                }
                            }
                        }
                    }
                }
            }
        }
        return adjacentConnections;
    }
    
    public static void setAdjacentPowerConnections(final TileEntity conductor, final List<TileEntity> acceptors, final List<ForgeDirection> directions) throws Exception {
        final BlockVec3 thisVec = new BlockVec3(conductor);
        final World world = conductor.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);
            if (tileEntity != null) {
                if (!(tileEntity instanceof IConductor)) {
                    final ForgeDirection sideFrom = direction.getOpposite();
                    if (tileEntity instanceof IElectrical) {
                        if (((IElectrical)tileEntity).canConnect(sideFrom, NetworkType.POWER)) {
                            acceptors.add(tileEntity);
                            directions.add(sideFrom);
                        }
                    }
                    else if (EnergyUtil.isMekLoaded && tileEntity instanceof IStrictEnergyAcceptor) {
                        if (EnergyUtil.clazzMekCable == null || !EnergyUtil.clazzMekCable.isInstance(tileEntity)) {
                            if (((IStrictEnergyAcceptor)tileEntity).canReceiveEnergy(sideFrom)) {
                                acceptors.add(tileEntity);
                                directions.add(sideFrom);
                            }
                        }
                    }
                    else if (!EnergyUtil.isBCReallyLoaded || !EnergyUtil.clazzPipeTile.isInstance(tileEntity)) {
                        if (EnergyUtil.isIC2Loaded && tileEntity instanceof IEnergyAcceptor) {
                            if (!(tileEntity instanceof IEnergyConductor)) {
                                if (((IEnergyAcceptor)tileEntity).acceptsEnergyFrom(conductor, sideFrom)) {
                                    acceptors.add(tileEntity);
                                    directions.add(sideFrom);
                                }
                            }
                        }
                        else if ((EnergyUtil.isRF2Loaded && tileEntity instanceof IEnergyReceiver) || (EnergyUtil.isRF1Loaded && tileEntity instanceof IEnergyHandler)) {
                            if (EnergyUtil.clazzEnderIOCable == null || !EnergyUtil.clazzEnderIOCable.isInstance(tileEntity)) {
                                if (EnergyUtil.clazzMFRRednetEnergyCable == null || !EnergyUtil.clazzMFRRednetEnergyCable.isInstance(tileEntity)) {
                                    if (((IEnergyConnection)tileEntity).canConnectEnergy(sideFrom)) {
                                        acceptors.add(tileEntity);
                                        directions.add(sideFrom);
                                    }
                                }
                            }
                        }
                        else if (EnergyUtil.isBC6Loaded && MjAPI.getMjBattery((Object)tileEntity, "buildcraft.kinesis", sideFrom) != null) {
                            acceptors.add(tileEntity);
                            directions.add(sideFrom);
                        }
                        else if (EnergyUtil.isBCLoaded && tileEntity instanceof IPowerReceptor && ((IPowerReceptor)tileEntity).getPowerReceiver(sideFrom) != null && (!(tileEntity instanceof IPowerEmitter) || !((IPowerEmitter)tileEntity).canEmitPowerFrom(sideFrom))) {
                            acceptors.add(tileEntity);
                            directions.add(sideFrom);
                        }
                    }
                }
            }
        }
    }
    
    public static float otherModsEnergyTransfer(final TileEntity tileAdj, final ForgeDirection inputAdj, final float toSend, final boolean simulate) {
        if (EnergyUtil.isMekLoaded && !EnergyConfigHandler.disableMekanismOutput && tileAdj instanceof IStrictEnergyAcceptor) {
            final IStrictEnergyAcceptor tileMek = (IStrictEnergyAcceptor)tileAdj;
            if (tileMek.canReceiveEnergy(inputAdj)) {
                float transferredMek;
                if (simulate) {
                    transferredMek = (tileMek.canReceiveEnergy(inputAdj) ? ((float)(tileMek.getMaxEnergy() - tileMek.getEnergy())) : 0.0f);
                }
                else {
                    transferredMek = (float)tileMek.transferEnergyToAcceptor(inputAdj, (double)(toSend * EnergyConfigHandler.TO_MEKANISM_RATIO));
                }
                return transferredMek / EnergyConfigHandler.TO_MEKANISM_RATIO;
            }
        }
        else if (EnergyUtil.isIC2Loaded && !EnergyConfigHandler.disableIC2Output && tileAdj instanceof IEnergySink) {
            double demanded = 0.0;
            try {
                demanded = (double)EnergyUtil.demandedEnergyIC2.invoke(tileAdj, new Object[0]);
            }
            catch (Exception ex) {
                if (ConfigManagerCore.enableDebug) {
                    ex.printStackTrace();
                }
            }
            if (simulate) {
                return Math.min(toSend, (float)demanded / EnergyConfigHandler.TO_IC2_RATIO);
            }
            final double energySendingIC2 = Math.min(toSend * EnergyConfigHandler.TO_IC2_RATIO, demanded);
            if (energySendingIC2 >= 1.0) {
                double result = 0.0;
                try {
                    if (EnergyUtil.voltageParameterIC2) {
                        result = energySendingIC2 - (double)EnergyUtil.injectEnergyIC2.invoke(tileAdj, inputAdj, energySendingIC2, 120.0);
                    }
                    else {
                        result = energySendingIC2 - (double)EnergyUtil.injectEnergyIC2.invoke(tileAdj, inputAdj, energySendingIC2);
                    }
                }
                catch (Exception ex2) {
                    if (ConfigManagerCore.enableDebug) {
                        ex2.printStackTrace();
                    }
                }
                if (result < 0.0) {
                    return 0.0f;
                }
                return (float)result / EnergyConfigHandler.TO_IC2_RATIO;
            }
        }
        else {
            if (EnergyUtil.isRF1Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyHandler) {
                final float sent = ((IEnergyHandler)tileAdj).receiveEnergy(inputAdj, MathHelper.floor_float(toSend * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
                return sent;
            }
            if (EnergyUtil.isRF2Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyReceiver) {
                final float sent = ((IEnergyReceiver)tileAdj).receiveEnergy(inputAdj, MathHelper.floor_float(toSend * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
                return sent;
            }
            if (EnergyUtil.isBC6Loaded && !EnergyConfigHandler.disableBuildCraftOutput && MjAPI.getMjBattery((Object)tileAdj, "buildcraft.kinesis", inputAdj) != null) {
                final double toSendBC = Math.min(toSend * EnergyConfigHandler.TO_BC_RATIO, MjAPI.getMjBattery((Object)tileAdj, "buildcraft.kinesis", inputAdj).getEnergyRequested());
                if (simulate) {
                    return (float)toSendBC / EnergyConfigHandler.TO_BC_RATIO;
                }
                final float sent2 = (float)MjAPI.getMjBattery((Object)tileAdj, "buildcraft.kinesis", inputAdj).addEnergy(toSendBC) / EnergyConfigHandler.TO_BC_RATIO;
                return sent2;
            }
            else if (EnergyUtil.isBCLoaded && !EnergyConfigHandler.disableBuildCraftOutput && tileAdj instanceof IPowerReceptor) {
                final PowerHandler.PowerReceiver receiver = ((IPowerReceptor)tileAdj).getPowerReceiver(inputAdj);
                if (receiver != null) {
                    final double toSendBC2 = Math.min(toSend * EnergyConfigHandler.TO_BC_RATIO, Math.min(receiver.powerRequest(), receiver.getMaxEnergyReceived()));
                    if (simulate) {
                        return (float)toSendBC2 / EnergyConfigHandler.TO_BC_RATIO;
                    }
                    final float rec = (float)receiver.receiveEnergy(PowerHandler.Type.PIPE, toSendBC2, inputAdj);
                    return rec / EnergyConfigHandler.TO_BC_RATIO;
                }
            }
        }
        return 0.0f;
    }
    
    public static float otherModsEnergyExtract(final TileEntity tileAdj, final ForgeDirection inputAdj, final float toPull, final boolean simulate) {
        if (EnergyUtil.isIC2Loaded && !EnergyConfigHandler.disableIC2Input && tileAdj instanceof IEnergySource) {
            double offered = 0.0;
            try {
                offered = (double)EnergyUtil.offeredEnergyIC2.invoke(tileAdj, new Object[0]);
            }
            catch (Exception ex) {
                if (ConfigManagerCore.enableDebug) {
                    ex.printStackTrace();
                }
            }
            if (simulate) {
                return Math.min(toPull, (float)offered / EnergyConfigHandler.TO_IC2_RATIO);
            }
            final double energySendingIC2 = Math.min(toPull * EnergyConfigHandler.TO_IC2_RATIO, offered);
            if (energySendingIC2 >= 1.0) {
                double resultIC2 = 0.0;
                try {
                    resultIC2 = energySendingIC2 - (double)EnergyUtil.drawEnergyIC2.invoke(tileAdj, energySendingIC2);
                }
                catch (Exception ex2) {
                    if (ConfigManagerCore.enableDebug) {
                        ex2.printStackTrace();
                    }
                }
                if (resultIC2 < 0.0) {
                    resultIC2 = 0.0;
                }
                return (float)resultIC2 / EnergyConfigHandler.TO_IC2_RATIO;
            }
        }
        else {
            if (EnergyUtil.isRF2Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyProvider) {
                final float sent = ((IEnergyProvider)tileAdj).extractEnergy(inputAdj, MathHelper.floor_float(toPull * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
                return sent;
            }
            if (EnergyUtil.isRF1Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyHandler) {
                final float sent = ((IEnergyHandler)tileAdj).extractEnergy(inputAdj, MathHelper.floor_float(toPull * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
                return sent;
            }
        }
        return 0.0f;
    }
    
    public static boolean otherModCanReceive(final TileEntity tileAdj, final ForgeDirection inputAdj) {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile) {
            return false;
        }
        if (EnergyUtil.isMekLoaded && tileAdj instanceof IStrictEnergyAcceptor) {
            return ((IStrictEnergyAcceptor)tileAdj).canReceiveEnergy(inputAdj);
        }
        if (EnergyUtil.isIC2Loaded && tileAdj instanceof IEnergyAcceptor) {
            return ((IEnergyAcceptor)tileAdj).acceptsEnergyFrom((TileEntity)null, inputAdj);
        }
        if ((EnergyUtil.isRF1Loaded && tileAdj instanceof IEnergyHandler) || (EnergyUtil.isRF2Loaded && tileAdj instanceof IEnergyReceiver)) {
            return ((IEnergyConnection)tileAdj).canConnectEnergy(inputAdj);
        }
        return (EnergyUtil.isBC6Loaded && MjAPI.getMjBattery((Object)tileAdj, "buildcraft.kinesis", inputAdj) != null) || (EnergyUtil.isBCLoaded && tileAdj instanceof IPowerReceptor && ((IPowerReceptor)tileAdj).getPowerReceiver(inputAdj) != null);
    }
    
    public static boolean otherModCanProduce(final TileEntity tileAdj, final ForgeDirection side) {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile) {
            return false;
        }
        if (EnergyUtil.isIC2Loaded && tileAdj instanceof IEnergyEmitter) {
            return ((IEnergyEmitter)tileAdj).emitsEnergyTo((TileEntity)null, side);
        }
        return ((EnergyUtil.isRF1Loaded && tileAdj instanceof IEnergyHandler) || (EnergyUtil.isRF2Loaded && tileAdj instanceof IEnergyProvider)) && ((IEnergyConnection)tileAdj).canConnectEnergy(side);
    }
    
    public static boolean initialiseIC2Methods() {
        try {
            EnergyUtil.clazzMekCable = Class.forName("codechicken.multipart.TileMultipart");
        }
        catch (Exception ex) {}
        try {
            EnergyUtil.clazzEnderIOCable = Class.forName("crazypants.enderio.conduit.TileConduitBundle");
        }
        catch (Exception ex2) {}
        try {
            EnergyUtil.clazzMFRRednetEnergyCable = Class.forName("powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy");
        }
        catch (Exception ex3) {}
        try {
            EnergyUtil.clazzRailcraftEngine = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngine");
        }
        catch (Exception ex4) {}
        try {
            EnergyUtil.clazzPipeTile = Class.forName("buildcraft.transport.TileGenericPipe");
        }
        catch (Exception ex5) {}
        try {
            EnergyUtil.clazzPipeWood = Class.forName("buildcraft.transport.pipes.PipePowerWood");
        }
        catch (Exception ex6) {}
        if (EnergyUtil.isIC2Loaded) {
            GCLog.debug("Initialising IC2 methods OK");
            try {
                final Class<?> clazz = Class.forName("ic2.api.energy.tile.IEnergySink");
                GCLog.debug("Found IC2 IEnergySink class OK");
                try {
                    EnergyUtil.demandedEnergyIC2 = clazz.getMethod("demandedEnergyUnits", (Class<?>[])new Class[0]);
                }
                catch (Exception e2) {
                    try {
                        EnergyUtil.demandedEnergyIC2 = clazz.getMethod("getDemandedEnergy", (Class<?>[])new Class[0]);
                    }
                    catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                GCLog.debug("Set IC2 demandedEnergy method OK");
                try {
                    EnergyUtil.injectEnergyIC2 = clazz.getMethod("injectEnergyUnits", ForgeDirection.class, Double.TYPE);
                    GCLog.debug("IC2 inject 1.7.2 succeeded");
                }
                catch (Exception e2) {
                    try {
                        EnergyUtil.injectEnergyIC2 = clazz.getMethod("injectEnergy", ForgeDirection.class, Double.TYPE, Double.TYPE);
                        EnergyUtil.voltageParameterIC2 = true;
                        GCLog.debug("IC2 inject 1.7.10 succeeded");
                    }
                    catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                final Class<?> clazzSource = Class.forName("ic2.api.energy.tile.IEnergySource");
                EnergyUtil.offeredEnergyIC2 = clazzSource.getMethod("getOfferedEnergy", (Class<?>[])new Class[0]);
                EnergyUtil.drawEnergyIC2 = clazzSource.getMethod("drawEnergy", Double.TYPE);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (EnergyUtil.clazzPipeTile == null) {
            EnergyUtil.isBCReallyLoaded = false;
        }
        return true;
    }
    
    static {
        EnergyUtil.isMekLoaded = EnergyConfigHandler.isMekanismLoaded();
        EnergyUtil.isRFLoaded = EnergyConfigHandler.isRFAPILoaded();
        EnergyUtil.isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded();
        EnergyUtil.isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded();
        EnergyUtil.isIC2Loaded = EnergyConfigHandler.isIndustrialCraft2Loaded();
        EnergyUtil.isBCLoaded = EnergyConfigHandler.isBuildcraftLoaded();
        EnergyUtil.isBC6Loaded = (EnergyUtil.isBCLoaded && EnergyConfigHandler.getBuildcraftVersion() == 6);
        EnergyUtil.isBCReallyLoaded = EnergyConfigHandler.isBuildcraftReallyLoaded();
        EnergyUtil.voltageParameterIC2 = false;
        EnergyUtil.demandedEnergyIC2 = null;
        EnergyUtil.injectEnergyIC2 = null;
        EnergyUtil.offeredEnergyIC2 = null;
        EnergyUtil.drawEnergyIC2 = null;
        EnergyUtil.clazzMekCable = null;
        EnergyUtil.clazzEnderIOCable = null;
        EnergyUtil.clazzMFRRednetEnergyCable = null;
        EnergyUtil.clazzRailcraftEngine = null;
        EnergyUtil.clazzPipeTile = null;
        EnergyUtil.clazzPipeWood = null;
        EnergyUtil.initialisedIC2Methods = initialiseIC2Methods();
    }
}
