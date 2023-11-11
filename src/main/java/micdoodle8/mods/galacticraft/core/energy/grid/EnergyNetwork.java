package micdoodle8.mods.galacticraft.core.energy.grid;

import net.minecraft.tileentity.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import mekanism.api.energy.*;
import ic2.api.energy.tile.*;
import cofh.api.energy.*;
import buildcraft.api.mj.*;
import buildcraft.api.power.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;

public class EnergyNetwork implements IElectricityNetwork
{
    private boolean isMekLoaded;
    private boolean isRF1Loaded;
    private boolean isRF2Loaded;
    private boolean isIC2Loaded;
    private boolean isBCLoaded;
    public static int tickCount;
    private int tickDone;
    private float totalRequested;
    private float totalStorageExcess;
    private float totalEnergy;
    private float totalSent;
    private boolean doneScheduled;
    private boolean spamstop;
    private boolean loopPrevention;
    public int networkTierGC;
    private int producersTierGC;
    private List<TileEntity> connectedAcceptors;
    private List<ForgeDirection> connectedDirections;
    private Set<TileEntity> availableAcceptors;
    private Map<TileEntity, ForgeDirection> availableconnectedDirections;
    private Map<TileEntity, Float> energyRequests;
    private List<TileEntity> ignoreAcceptors;
    private final Set<IConductor> conductors;
    private static final float ENERGY_STORAGE_LEVEL = 200.0f;
    
    public EnergyNetwork() {
        this.isMekLoaded = (EnergyConfigHandler.isMekanismLoaded() && !EnergyConfigHandler.disableMekanismOutput);
        this.isRF1Loaded = (EnergyConfigHandler.isRFAPIv1Loaded() && !EnergyConfigHandler.disableRFOutput);
        this.isRF2Loaded = (EnergyConfigHandler.isRFAPIv2Loaded() && !EnergyConfigHandler.disableRFOutput);
        this.isIC2Loaded = (EnergyConfigHandler.isIndustrialCraft2Loaded() && !EnergyConfigHandler.disableIC2Output);
        this.isBCLoaded = (EnergyConfigHandler.isBuildcraftLoaded() && !EnergyConfigHandler.disableBuildCraftOutput);
        this.tickDone = -1;
        this.totalRequested = 0.0f;
        this.totalStorageExcess = 0.0f;
        this.totalEnergy = 0.0f;
        this.totalSent = 0.0f;
        this.doneScheduled = false;
        this.spamstop = false;
        this.loopPrevention = false;
        this.networkTierGC = 1;
        this.producersTierGC = 1;
        this.connectedAcceptors = new LinkedList<TileEntity>();
        this.connectedDirections = new LinkedList<ForgeDirection>();
        this.availableAcceptors = new HashSet<TileEntity>();
        this.availableconnectedDirections = new HashMap<TileEntity, ForgeDirection>();
        this.energyRequests = new HashMap<TileEntity, Float>();
        this.ignoreAcceptors = new LinkedList<TileEntity>();
        this.conductors = new HashSet<IConductor>();
    }
    
    public Set<IConductor> getTransmitters() {
        return this.conductors;
    }
    
    public float getRequest(final TileEntity... ignoreTiles) {
        if (EnergyNetwork.tickCount != this.tickDone) {
            this.ignoreAcceptors.clear();
            this.ignoreAcceptors.addAll(Arrays.asList(ignoreTiles));
            this.doTickStartCalc();
            if (EnergyConfigHandler.isBuildcraftLoaded()) {
                for (final IConductor wire : this.conductors) {
                    if (wire instanceof TileBaseUniversalConductor) {
                        ((TileBaseUniversalConductor)wire).reconfigureBC();
                    }
                }
            }
        }
        return this.totalRequested - this.totalEnergy - this.totalSent;
    }
    
    public float produce(final float energy, final boolean doReceive, final int producerTier, final TileEntity... ignoreTiles) {
        if (this.loopPrevention) {
            return energy;
        }
        if (energy <= 0.0f) {
            return energy;
        }
        if (EnergyNetwork.tickCount != this.tickDone) {
            this.tickDone = EnergyNetwork.tickCount;
            this.ignoreAcceptors.clear();
            this.ignoreAcceptors.addAll(Arrays.asList(ignoreTiles));
            this.producersTierGC = 1;
            this.doTickStartCalc();
        }
        else {
            this.ignoreAcceptors.addAll(Arrays.asList(ignoreTiles));
        }
        if (!this.doneScheduled && this.totalRequested > 0.0f) {
            TickHandlerServer.scheduleNetworkTick(this);
            this.doneScheduled = true;
        }
        final float totalEnergyLast = this.totalEnergy;
        if (doReceive) {
            this.totalEnergy += Math.min(energy, this.totalRequested - totalEnergyLast);
            if (producerTier > 1) {
                this.producersTierGC = 2;
            }
        }
        if (this.totalRequested >= totalEnergyLast + energy) {
            return 0.0f;
        }
        if (totalEnergyLast >= this.totalRequested) {
            return energy;
        }
        return totalEnergyLast + energy - this.totalRequested;
    }
    
    public void tickEnd() {
        this.doneScheduled = false;
        this.loopPrevention = true;
        if (this.totalEnergy > 0.0f) {
            this.doTickStartCalc();
            if (this.totalRequested > 0.0f) {
                this.totalSent = this.doProduce();
                if (this.totalSent < this.totalEnergy) {
                    this.totalEnergy -= this.totalSent;
                }
                else {
                    this.totalEnergy = 0.0f;
                }
            }
            else {
                this.totalEnergy = 0.0f;
            }
        }
        else {
            this.totalEnergy = 0.0f;
        }
        this.loopPrevention = false;
    }
    
    private void doTickStartCalc() {
        this.tickDone = EnergyNetwork.tickCount;
        this.totalSent = 0.0f;
        this.refreshAcceptors();
        if (!EnergyUtil.initialisedIC2Methods) {
            EnergyUtil.initialiseIC2Methods();
        }
        if (this.conductors.size() == 0) {
            return;
        }
        this.loopPrevention = true;
        this.availableAcceptors.clear();
        this.availableconnectedDirections.clear();
        this.energyRequests.clear();
        this.totalRequested = 0.0f;
        this.totalStorageExcess = 0.0f;
        if (!this.connectedAcceptors.isEmpty()) {
            final Iterator<ForgeDirection> acceptorDirection = this.connectedDirections.iterator();
            for (final TileEntity acceptor : this.connectedAcceptors) {
                final ForgeDirection sideFrom = acceptorDirection.next();
                if (!this.ignoreAcceptors.contains(acceptor) && !this.availableAcceptors.contains(acceptor)) {
                    float e = 0.0f;
                    if (acceptor instanceof IElectrical) {
                        e = ((IElectrical)acceptor).getRequest(sideFrom);
                    }
                    else if (this.isMekLoaded && acceptor instanceof IStrictEnergyAcceptor) {
                        e = (float)((((IStrictEnergyAcceptor)acceptor).getMaxEnergy() - ((IStrictEnergyAcceptor)acceptor).getEnergy()) / EnergyConfigHandler.TO_MEKANISM_RATIO);
                    }
                    else if (this.isIC2Loaded && acceptor instanceof IEnergySink) {
                        double result = 0.0;
                        try {
                            result = (double)EnergyUtil.demandedEnergyIC2.invoke(acceptor, new Object[0]);
                        }
                        catch (Exception ex) {
                            if (ConfigManagerCore.enableDebug) {
                                ex.printStackTrace();
                            }
                        }
                        result = Math.min(result, (this.networkTierGC == 2) ? 256.0 : 128.0);
                        e = (float)result / EnergyConfigHandler.TO_IC2_RATIO;
                    }
                    else if (this.isRF2Loaded && acceptor instanceof IEnergyReceiver) {
                        e = ((IEnergyReceiver)acceptor).receiveEnergy(sideFrom, Integer.MAX_VALUE, true) / EnergyConfigHandler.TO_RF_RATIO;
                    }
                    else if (this.isRF1Loaded && acceptor instanceof IEnergyHandler) {
                        e = ((IEnergyHandler)acceptor).receiveEnergy(sideFrom, Integer.MAX_VALUE, true) / EnergyConfigHandler.TO_RF_RATIO;
                    }
                    else if (this.isBCLoaded && EnergyConfigHandler.getBuildcraftVersion() == 6 && MjAPI.getMjBattery((Object)acceptor, "buildcraft.kinesis", sideFrom) != null) {
                        e = (float)MjAPI.getMjBattery((Object)acceptor, "buildcraft.kinesis", sideFrom).getEnergyRequested() / EnergyConfigHandler.TO_BC_RATIO;
                    }
                    else if (this.isBCLoaded && acceptor instanceof IPowerReceptor) {
                        final PowerHandler.PowerReceiver BCreceiver = ((IPowerReceptor)acceptor).getPowerReceiver(sideFrom);
                        if (BCreceiver != null) {
                            e = (float)BCreceiver.powerRequest() / EnergyConfigHandler.TO_BC_RATIO;
                        }
                    }
                    if (e <= 0.0f) {
                        continue;
                    }
                    this.availableAcceptors.add(acceptor);
                    this.availableconnectedDirections.put(acceptor, sideFrom);
                    this.energyRequests.put(acceptor, e);
                    this.totalRequested += e;
                    if (e <= 200.0f) {
                        continue;
                    }
                    this.totalStorageExcess += e - 200.0f;
                }
            }
        }
        this.loopPrevention = false;
    }
    
    private float doProduce() {
        float sent = 0.0f;
        if (!this.availableAcceptors.isEmpty()) {
            float energyNeeded = this.totalRequested;
            final float energyAvailable = this.totalEnergy;
            float reducor = 1.0f;
            float energyStorageReducor = 1.0f;
            if (energyNeeded > energyAvailable) {
                energyNeeded -= this.totalStorageExcess;
                if (energyNeeded > energyAvailable) {
                    energyStorageReducor = 0.0f;
                    reducor = energyAvailable / energyNeeded;
                }
                else {
                    energyStorageReducor = (energyAvailable - energyNeeded) / this.totalStorageExcess;
                }
            }
            final int tierProduced = Math.min(this.producersTierGC, this.networkTierGC);
            TileEntity debugTE = null;
            try {
                final Iterator<TileEntity> iterator = this.availableAcceptors.iterator();
                while (iterator.hasNext()) {
                    final TileEntity tileEntity = debugTE = iterator.next();
                    if (sent >= energyAvailable) {
                        break;
                    }
                    float currentSending = this.energyRequests.get(tileEntity);
                    if (currentSending > 200.0f) {
                        currentSending = 200.0f + (currentSending - 200.0f) * energyStorageReducor;
                    }
                    currentSending *= reducor;
                    if (currentSending > energyAvailable - sent) {
                        currentSending = energyAvailable - sent;
                    }
                    final ForgeDirection sideFrom = this.availableconnectedDirections.get(tileEntity);
                    float sentToAcceptor;
                    if (tileEntity instanceof IElectrical) {
                        sentToAcceptor = ((IElectrical)tileEntity).receiveElectricity(sideFrom, currentSending, tierProduced, true);
                    }
                    else if (this.isMekLoaded && tileEntity instanceof IStrictEnergyAcceptor) {
                        sentToAcceptor = (float)((IStrictEnergyAcceptor)tileEntity).transferEnergyToAcceptor(sideFrom, (double)(currentSending * EnergyConfigHandler.TO_MEKANISM_RATIO)) / EnergyConfigHandler.TO_MEKANISM_RATIO;
                    }
                    else if (this.isIC2Loaded && tileEntity instanceof IEnergySink) {
                        final double energySendingIC2 = currentSending * EnergyConfigHandler.TO_IC2_RATIO;
                        if (energySendingIC2 >= 1.0) {
                            double result = 0.0;
                            try {
                                if (EnergyUtil.voltageParameterIC2) {
                                    result = (double)EnergyUtil.injectEnergyIC2.invoke(tileEntity, sideFrom, energySendingIC2, 120.0);
                                }
                                else {
                                    result = (double)EnergyUtil.injectEnergyIC2.invoke(tileEntity, sideFrom, energySendingIC2);
                                }
                            }
                            catch (Exception ex) {
                                if (ConfigManagerCore.enableDebug) {
                                    ex.printStackTrace();
                                }
                            }
                            sentToAcceptor = currentSending - (float)result / EnergyConfigHandler.TO_IC2_RATIO;
                            if (sentToAcceptor < 0.0f) {
                                sentToAcceptor = 0.0f;
                            }
                        }
                        else {
                            sentToAcceptor = 0.0f;
                        }
                    }
                    else if (this.isRF2Loaded && tileEntity instanceof IEnergyReceiver) {
                        final int currentSendinginRF = (currentSending >= 2.14748365E9f / EnergyConfigHandler.TO_RF_RATIO) ? Integer.MAX_VALUE : ((int)(currentSending * EnergyConfigHandler.TO_RF_RATIO));
                        sentToAcceptor = ((IEnergyReceiver)tileEntity).receiveEnergy(sideFrom, currentSendinginRF, false) / EnergyConfigHandler.TO_RF_RATIO;
                    }
                    else if (this.isRF1Loaded && tileEntity instanceof IEnergyHandler) {
                        final int currentSendinginRF = (currentSending >= 2.14748365E9f / EnergyConfigHandler.TO_RF_RATIO) ? Integer.MAX_VALUE : ((int)(currentSending * EnergyConfigHandler.TO_RF_RATIO));
                        sentToAcceptor = ((IEnergyHandler)tileEntity).receiveEnergy(sideFrom, currentSendinginRF, false) / EnergyConfigHandler.TO_RF_RATIO;
                    }
                    else if (this.isBCLoaded && EnergyConfigHandler.getBuildcraftVersion() == 6 && MjAPI.getMjBattery((Object)tileEntity, "buildcraft.kinesis", sideFrom) != null) {
                        sentToAcceptor = (float)MjAPI.getMjBattery((Object)tileEntity, "buildcraft.kinesis", sideFrom).addEnergy((double)(currentSending * EnergyConfigHandler.TO_BC_RATIO)) / EnergyConfigHandler.TO_BC_RATIO;
                    }
                    else if (this.isBCLoaded && tileEntity instanceof IPowerReceptor) {
                        final PowerHandler.PowerReceiver receiver = ((IPowerReceptor)tileEntity).getPowerReceiver(sideFrom);
                        if (receiver != null) {
                            final double toSendBC = Math.min(currentSending * EnergyConfigHandler.TO_BC_RATIO, receiver.powerRequest());
                            sentToAcceptor = (float)receiver.receiveEnergy(PowerHandler.Type.PIPE, toSendBC, sideFrom) / EnergyConfigHandler.TO_BC_RATIO;
                        }
                        else {
                            sentToAcceptor = 0.0f;
                        }
                    }
                    else {
                        sentToAcceptor = 0.0f;
                    }
                    if (sentToAcceptor / currentSending > 1.002f && sentToAcceptor > 0.01f) {
                        if (!this.spamstop) {
                            FMLLog.info("Energy network: acceptor took too much energy, offered " + currentSending + ", took " + sentToAcceptor + ". " + tileEntity.toString(), new Object[0]);
                            this.spamstop = true;
                        }
                        sentToAcceptor = currentSending;
                    }
                    sent += sentToAcceptor;
                }
            }
            catch (Exception e) {
                GCLog.severe("DEBUG Energy network loop issue, please report this");
                if (debugTE != null) {
                    GCLog.severe("Problem was likely caused by tile in dim " + debugTE.getWorldObj().provider.dimensionId + " at " + debugTE.xCoord + "," + debugTE.yCoord + "," + debugTE.zCoord + " Type:" + debugTE.getClass().getSimpleName());
                }
            }
        }
        if (EnergyNetwork.tickCount % 200 == 0) {
            this.spamstop = false;
        }
        float returnvalue = sent;
        if (returnvalue > this.totalEnergy) {
            returnvalue = this.totalEnergy;
        }
        if (returnvalue < 0.0f) {
            returnvalue = 0.0f;
        }
        return returnvalue;
    }
    
    public void refreshWithChecks() {
        int tierfound = 2;
        final Iterator<IConductor> it = this.conductors.iterator();
        while (it.hasNext()) {
            final IConductor conductor = it.next();
            if (conductor == null) {
                it.remove();
            }
            else {
                final TileEntity tile = (TileEntity)conductor;
                final World world = tile.getWorldObj();
                if (tile.isInvalid() || world == null || !world.blockExists(tile.xCoord, tile.yCoord, tile.zCoord)) {
                    it.remove();
                }
                else if (conductor != world.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord)) {
                    it.remove();
                }
                else {
                    if (conductor.getTierGC() < 2) {
                        tierfound = 1;
                    }
                    if (conductor.getNetwork() == this) {
                        continue;
                    }
                    conductor.setNetwork((IGridNetwork)this);
                    conductor.onNetworkChanged();
                }
            }
        }
        this.networkTierGC = tierfound;
    }
    
    public void refresh() {
        int tierfound = 2;
        final Iterator<IConductor> it = this.conductors.iterator();
        while (it.hasNext()) {
            final IConductor conductor = it.next();
            if (conductor == null) {
                it.remove();
            }
            else {
                final TileEntity tile = (TileEntity)conductor;
                final World world = tile.getWorldObj();
                if (tile.isInvalid() || world == null) {
                    it.remove();
                }
                else {
                    if (conductor.getTierGC() < 2) {
                        tierfound = 1;
                    }
                    if (conductor.getNetwork() == this) {
                        continue;
                    }
                    conductor.setNetwork((IGridNetwork)this);
                    conductor.onNetworkChanged();
                }
            }
        }
        this.networkTierGC = tierfound;
    }
    
    private void refreshAcceptors() {
        this.connectedAcceptors.clear();
        this.connectedDirections.clear();
        this.refreshWithChecks();
        try {
            final LinkedList<IConductor> conductorsCopy = new LinkedList<IConductor>();
            conductorsCopy.addAll(this.conductors);
            for (final IConductor conductor : conductorsCopy) {
                EnergyUtil.setAdjacentPowerConnections((TileEntity)conductor, (List)this.connectedAcceptors, (List)this.connectedDirections);
            }
        }
        catch (Exception e) {
            FMLLog.severe("GC Aluminium Wire: Error when testing whether another mod's tileEntity can accept energy.", new Object[0]);
            e.printStackTrace();
        }
    }
    
    public IElectricityNetwork merge(final IElectricityNetwork network) {
        if (network == null || network == this) {
            return (IElectricityNetwork)this;
        }
        final Set<IConductor> thisNetwork = this.conductors;
        final Set<IConductor> thatNetwork = (Set<IConductor>)network.getTransmitters();
        if (thisNetwork.size() >= thatNetwork.size()) {
            thisNetwork.addAll(thatNetwork);
            this.refresh();
            if (network instanceof EnergyNetwork) {
                ((EnergyNetwork)network).destroy();
            }
            return (IElectricityNetwork)this;
        }
        thatNetwork.addAll(thisNetwork);
        network.refresh();
        this.destroy();
        return network;
    }
    
    private void destroy() {
        this.conductors.clear();
        this.connectedAcceptors.clear();
        this.availableAcceptors.clear();
        this.totalEnergy = 0.0f;
        this.totalRequested = 0.0f;
        try {
            final Class<?> clazz = Class.forName("micdoodle8.mods.galacticraft.core.tick.TickHandlerServer");
            clazz.getMethod("removeNetworkTick", this.getClass()).invoke(null, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void split(final IConductor splitPoint) {
        if (splitPoint instanceof TileEntity) {
            this.getTransmitters().remove(splitPoint);
            splitPoint.setNetwork((IGridNetwork)null);
            if (this.getTransmitters().size() > 1) {
                final World world = ((TileEntity)splitPoint).getWorldObj();
                if (this.getTransmitters().size() > 0) {
                    final TileEntity[] nextToSplit = new TileEntity[6];
                    final boolean[] toDo = { true, true, true, true, true, true };
                    final int xCoord = ((TileEntity)splitPoint).xCoord;
                    final int yCoord = ((TileEntity)splitPoint).yCoord;
                    final int zCoord = ((TileEntity)splitPoint).zCoord;
                    for (int j = 0; j < 6; ++j) {
                        TileEntity tileEntity = null;
                        switch (j) {
                            case 0: {
                                tileEntity = world.getTileEntity(xCoord, yCoord - 1, zCoord);
                                break;
                            }
                            case 1: {
                                tileEntity = world.getTileEntity(xCoord, yCoord + 1, zCoord);
                                break;
                            }
                            case 2: {
                                tileEntity = world.getTileEntity(xCoord, yCoord, zCoord - 1);
                                break;
                            }
                            case 3: {
                                tileEntity = world.getTileEntity(xCoord, yCoord, zCoord + 1);
                                break;
                            }
                            case 4: {
                                tileEntity = world.getTileEntity(xCoord - 1, yCoord, zCoord);
                                break;
                            }
                            case 5: {
                                tileEntity = world.getTileEntity(xCoord + 1, yCoord, zCoord);
                                break;
                            }
                            default: {
                                tileEntity = null;
                                break;
                            }
                        }
                        if (tileEntity instanceof IConductor) {
                            nextToSplit[j] = tileEntity;
                        }
                        else {
                            toDo[j] = false;
                        }
                    }
                    for (int i1 = 0; i1 < 6; ++i1) {
                        if (toDo[i1]) {
                            final TileEntity connectedBlockA = nextToSplit[i1];
                            final NetworkFinder finder = new NetworkFinder(world, new BlockVec3(connectedBlockA), new BlockVec3((TileEntity)splitPoint));
                            final List<IConductor> partNetwork = finder.exploreNetwork();
                            for (int i2 = i1 + 1; i2 < 6; ++i2) {
                                final TileEntity connectedBlockB = nextToSplit[i2];
                                if (toDo[i2] && partNetwork.contains(connectedBlockB)) {
                                    toDo[i2] = false;
                                }
                            }
                            final EnergyNetwork newNetwork = new EnergyNetwork();
                            newNetwork.getTransmitters().addAll(partNetwork);
                            newNetwork.refreshWithChecks();
                        }
                    }
                    this.destroy();
                }
            }
            else if (this.getTransmitters().size() == 0) {
                this.destroy();
            }
        }
    }
    
    @Override
    public String toString() {
        return "EnergyNetwork[" + this.hashCode() + "|Wires:" + this.getTransmitters().size() + "|Acceptors:" + this.connectedAcceptors.size() + "]";
    }
    
    static {
        EnergyNetwork.tickCount = 0;
    }
}
