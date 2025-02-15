package micdoodle8.mods.galacticraft.core.energy.grid;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.FMLLog;
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.core.util.GCLog;

// import buildcraft.api.power.PowerHandler.Type;

/**
 * A universal network that works with multiple energy systems.
 *
 * @author radfast, micdoodle8, Calclavia, Aidancbrady
 */
public class EnergyNetwork implements IElectricityNetwork {

    private final boolean isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded() && !EnergyConfigHandler.disableRFOutput;
    private final boolean isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded() && !EnergyConfigHandler.disableRFOutput;

    /*
     * Re-written by radfast for better performance Imagine a 30 producer, 80 acceptor network... Before: it would have
     * called the inner loop in produce() 2400 times each tick. Not good! After: the inner loop runs 80 times - part of
     * it is in doTickStartCalc() at/near the tick start, and part of it is in doProduce() at the end of the tick
     */
    public static int tickCount = 0;
    private int tickDone = -1;
    private float totalRequested = 0F;
    private float totalStorageExcess = 0F;
    private float totalEnergy = 0F;
    private float totalSent = 0F;
    private boolean doneScheduled = false;
    private boolean spamstop = false;
    private boolean loopPrevention = false;
    public int networkTierGC = 1;
    private int producersTierGC = 1;

    /*
     * connectedAcceptors is all the acceptors connected to this network connectedDirections is the directions of those
     * connections (from the point of view of the acceptor tile) Note: each position in those two linked lists matches
     * so, an acceptor connected on two sides will be in connectedAcceptors twice
     */
    private final List<TileEntity> connectedAcceptors = new LinkedList<>();
    private final List<ForgeDirection> connectedDirections = new LinkedList<>();

    /*
     * availableAcceptors is the acceptors which can receive energy (this tick) availableconnectedDirections is a map of
     * those acceptors and the directions they will receive from (from the point of view of the acceptor tile) Note:
     * each acceptor will only be included once in these collections (there is no point trying to put power into a
     * machine twice from two different sides)
     */
    private final Set<TileEntity> availableAcceptors = new HashSet<>();
    private final Map<TileEntity, ForgeDirection> availableconnectedDirections = new HashMap<>();

    private final Map<TileEntity, Float> energyRequests = new HashMap<>();
    private final List<TileEntity> ignoreAcceptors = new LinkedList<>();

    private final Set<IConductor> conductors = new HashSet<>();

    // This is an energy per tick which exceeds what any normal machine will
    // request, so the requester must be an energy
    // storage - for example, a battery or an energy cube
    private static final float ENERGY_STORAGE_LEVEL = 200F;

    @Override
    public Set<IConductor> getTransmitters() {
        return this.conductors;
    }

    /**
     * Get the total energy request in this network
     *
     * @param ignoreTiles Tiles to ignore in the request calculations (NOTE: only used in initial (internal) check.
     * @return Amount of energy requested in this network
     */
    @Override
    public float getRequest(TileEntity... ignoreTiles) {
        if (EnergyNetwork.tickCount != this.tickDone) {
            // Start the new tick - initialise everything
            this.ignoreAcceptors.clear();
            this.ignoreAcceptors.addAll(Arrays.asList(ignoreTiles));
            this.doTickStartCalc();
        }
        return this.totalRequested - this.totalEnergy - this.totalSent;
    }

    /**
     * Produce energy into the network
     *
     * @param energy      Amount of energy to send into the network
     * @param doReceive   Whether to put energy into the network (true) or just simulate (false)
     * @param ignoreTiles TileEntities to ignore for energy transfers.
     * @return Amount of energy REMAINING from the passed energy parameter
     */
    @Override
    public float produce(float energy, boolean doReceive, int producerTier, TileEntity... ignoreTiles) {
        if (this.loopPrevention) {
            return energy;
        }

        if (energy > 0F) {
            if (EnergyNetwork.tickCount != this.tickDone) {
                this.tickDone = EnergyNetwork.tickCount;
                // Start the new tick - initialise everything
                this.ignoreAcceptors.clear();
                this.ignoreAcceptors.addAll(Arrays.asList(ignoreTiles));
                this.producersTierGC = 1;
                this.doTickStartCalc();
            } else {
                this.ignoreAcceptors.addAll(Arrays.asList(ignoreTiles));
            }

            if (!this.doneScheduled && this.totalRequested > 0.0F) {
                TickHandlerServer.scheduleNetworkTick(this);
                this.doneScheduled = true;
            }

            // On a regular mid-tick produce(), just figure out how much is totalEnergy this
            // tick and return the used
            // amount
            // This will return 0 if totalRequested is 0 - for example a network with no
            // acceptors
            final float totalEnergyLast = this.totalEnergy;

            // Add the energy for distribution by this grid later this tick
            // Note: totalEnergy cannot exceed totalRequested
            if (doReceive) {
                this.totalEnergy += Math.min(energy, this.totalRequested - totalEnergyLast);
                if (producerTier > 1) {
                    this.producersTierGC = 2;
                }
            }

            if (this.totalRequested >= totalEnergyLast + energy) {
                return 0F; // All the electricity will be used
            }
            if (totalEnergyLast >= this.totalRequested) {
                return energy; // None of the electricity will be used
            }
            return totalEnergyLast + energy - this.totalRequested; // Some of the electricity will be used
        }
        return energy;
    }

    /**
     * Called on server tick end, from the Galacticraft Core tick handler.
     */
    public void tickEnd() {
        this.doneScheduled = false;
        this.loopPrevention = true;

        // Finish the last tick if there was some to send and something to receive it
        if (this.totalEnergy > 0F) {
            // Call doTickStartCalc a second time in case anything has updated meanwhile
            this.doTickStartCalc();

            if (this.totalRequested > 0F) {
                this.totalSent = this.doProduce();
                if (this.totalSent < this.totalEnergy) {
                    // Any spare energy left is retained for the next tick
                    this.totalEnergy -= this.totalSent;
                } else {
                    this.totalEnergy = 0F;
                }
            } else {
                this.totalEnergy = 0F;
            }
        } else {
            this.totalEnergy = 0F;
        }

        this.loopPrevention = false;
    }

    /**
     * Refreshes all tiles in network, and updates requested energy
     *
     * @param ignoreTiles TileEntities to ignore for energy calculations.
     */
    private void doTickStartCalc() {
        this.tickDone = EnergyNetwork.tickCount;
        this.totalSent = 0F;
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
        this.totalRequested = 0.0F;
        this.totalStorageExcess = 0F;

        if (!this.connectedAcceptors.isEmpty()) {
            float e;
            final Iterator<ForgeDirection> acceptorDirection = this.connectedDirections.iterator();
            for (final TileEntity acceptor : this.connectedAcceptors) {
                // This tries all sides of the acceptor which are connected (see
                // refreshAcceptors())
                final ForgeDirection sideFrom = acceptorDirection.next();

                // But the grid will only put energy into the acceptor from one side - once it's
                // in availableAcceptors
                if (!this.ignoreAcceptors.contains(acceptor) && !this.availableAcceptors.contains(acceptor)) {
                    e = 0.0F;

                    if (acceptor instanceof IElectrical) {
                        e = ((IElectrical) acceptor).getRequest(sideFrom);
                    } else if (this.isRF2Loaded && acceptor instanceof IEnergyReceiver) {
                        e = ((IEnergyReceiver) acceptor).receiveEnergy(sideFrom, Integer.MAX_VALUE, true)
                            / EnergyConfigHandler.TO_RF_RATIO;
                    } else if (this.isRF1Loaded && acceptor instanceof IEnergyHandler) {
                        e = ((IEnergyHandler) acceptor).receiveEnergy(sideFrom, Integer.MAX_VALUE, true)
                            / EnergyConfigHandler.TO_RF_RATIO;
                    }

                    if (e > 0.0F) {
                        this.availableAcceptors.add(acceptor);
                        this.availableconnectedDirections.put(acceptor, sideFrom);
                        this.energyRequests.put(acceptor, e);
                        this.totalRequested += e;
                        if (e > EnergyNetwork.ENERGY_STORAGE_LEVEL) {
                            this.totalStorageExcess += e - EnergyNetwork.ENERGY_STORAGE_LEVEL;
                        }
                    }
                }
            }
        }

        this.loopPrevention = false;
    }

    /**
     * Complete the energy transfer. Called internally on server tick end.
     *
     * @return Amount of energy SENT to all acceptors
     */
    private float doProduce() {
        float sent = 0.0F;

        if (!this.availableAcceptors.isEmpty()) {
            float energyNeeded = this.totalRequested;
            final float energyAvailable = this.totalEnergy;
            float reducor = 1.0F;
            float energyStorageReducor = 1.0F;

            if (energyNeeded > energyAvailable) {
                // If not enough energy, try reducing what goes into energy storage (if any)
                energyNeeded -= this.totalStorageExcess;
                // If there's still not enough, put the minimum into energy storage (if any)
                // and, anyhow, reduce
                // everything proportionately
                if (energyNeeded > energyAvailable) {
                    energyStorageReducor = 0F;
                    reducor = energyAvailable / energyNeeded;
                } else {
                    // Energyavailable exceeds the total needed but only if storage does not fill
                    // all in one go - this
                    // is a common situation
                    energyStorageReducor = (energyAvailable - energyNeeded) / this.totalStorageExcess;
                }
            }

            float currentSending;
            float sentToAcceptor;
            final int tierProduced = Math.min(this.producersTierGC, this.networkTierGC);

            TileEntity debugTE = null;
            try {
                for (final TileEntity tileEntity : this.availableAcceptors) {
                    debugTE = tileEntity;
                    // Exit the loop if there is no energy left at all (should normally not happen,
                    // should be some even
                    // for the last acceptor)
                    if (sent >= energyAvailable) {
                        break;
                    }

                    // The base case is to give each acceptor what it is requesting
                    currentSending = this.energyRequests.get(tileEntity);

                    // If it's an energy store, we may need to damp it down if energyStorageReducor
                    // is less than 1
                    if (currentSending > EnergyNetwork.ENERGY_STORAGE_LEVEL) {
                        currentSending = EnergyNetwork.ENERGY_STORAGE_LEVEL
                            + (currentSending - EnergyNetwork.ENERGY_STORAGE_LEVEL) * energyStorageReducor;
                    }

                    // Reduce everything proportionately if there is not enough energy for all needs
                    currentSending *= reducor;

                    if (currentSending > energyAvailable - sent) {
                        currentSending = energyAvailable - sent;
                    }

                    final ForgeDirection sideFrom = this.availableconnectedDirections.get(tileEntity);

                    if (tileEntity instanceof IElectrical) {
                        sentToAcceptor = ((IElectrical) tileEntity)
                            .receiveElectricity(sideFrom, currentSending, tierProduced, true);
                    } else if (this.isRF2Loaded && tileEntity instanceof IEnergyReceiver) {
                        final int currentSendinginRF = currentSending
                            >= Integer.MAX_VALUE / EnergyConfigHandler.TO_RF_RATIO ? Integer.MAX_VALUE
                                : (int) (currentSending * EnergyConfigHandler.TO_RF_RATIO);
                        sentToAcceptor = ((IEnergyReceiver) tileEntity)
                            .receiveEnergy(sideFrom, currentSendinginRF, false) / EnergyConfigHandler.TO_RF_RATIO;
                    } else if (this.isRF1Loaded && tileEntity instanceof IEnergyHandler) {
                        final int currentSendinginRF = currentSending
                            >= Integer.MAX_VALUE / EnergyConfigHandler.TO_RF_RATIO ? Integer.MAX_VALUE
                                : (int) (currentSending * EnergyConfigHandler.TO_RF_RATIO);
                        sentToAcceptor = ((IEnergyHandler) tileEntity)
                            .receiveEnergy(sideFrom, currentSendinginRF, false) / EnergyConfigHandler.TO_RF_RATIO;
                    } else {
                        sentToAcceptor = 0F;
                    }

                    if (sentToAcceptor / currentSending > 1.002F && sentToAcceptor > 0.01F) {
                        if (!this.spamstop) {
                            FMLLog.info(
                                "Energy network: acceptor took too much energy, offered " + currentSending
                                    + ", took "
                                    + sentToAcceptor
                                    + ". "
                                    + tileEntity.toString());
                            this.spamstop = true;
                        }
                        sentToAcceptor = currentSending;
                    }

                    sent += sentToAcceptor;
                }
            } catch (final Exception e) {
                GCLog.severe("DEBUG Energy network loop issue, please report this");
                if (debugTE != null) {
                    GCLog.severe(
                        "Problem was likely caused by tile in dim " + debugTE.getWorldObj().provider.dimensionId
                            + " at "
                            + debugTE.xCoord
                            + ","
                            + debugTE.yCoord
                            + ","
                            + debugTE.zCoord
                            + " Type:"
                            + debugTE.getClass()
                                .getSimpleName());
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
        if (returnvalue < 0F) {
            returnvalue = 0F;
        }
        return returnvalue;
    }

    /**
     * Refresh validity of each conductor in the network
     */
    public void refreshWithChecks() {
        int tierfound = 2;
        final Iterator<IConductor> it = this.conductors.iterator();
        while (it.hasNext()) {
            final IConductor conductor = it.next();

            if (conductor == null) {
                it.remove();
                continue;
            }

            final TileEntity tile = (TileEntity) conductor;
            final World world = tile.getWorldObj();
            // Remove any conductors in unloaded chunks
            if (tile.isInvalid() || world == null
                || !world.blockExists(tile.xCoord, tile.yCoord, tile.zCoord)
                || conductor != world.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord)) {
                it.remove();
                continue;
            }

            if (conductor.getTierGC() < 2) {
                tierfound = 1;
            }

            if (conductor.getNetwork() != this) {
                conductor.setNetwork(this);
                conductor.onNetworkChanged();
            }
        }

        // This will set the network tier to 2 if all the conductors are tier 2
        this.networkTierGC = tierfound;
    }

    @Override
    public void refresh() {
        int tierfound = 2;
        final Iterator<IConductor> it = this.conductors.iterator();
        while (it.hasNext()) {
            final IConductor conductor = it.next();

            if (conductor == null) {
                it.remove();
                continue;
            }

            final TileEntity tile = (TileEntity) conductor;
            final World world = tile.getWorldObj();
            // Remove any conductors in unloaded chunks
            if (tile.isInvalid() || world == null) {
                it.remove();
                continue;
            }

            if (conductor.getTierGC() < 2) {
                tierfound = 1;
            }

            if (conductor.getNetwork() != this) {
                conductor.setNetwork(this);
                conductor.onNetworkChanged();
            }
        }

        // This will set the network tier to 2 if all the conductors are tier 2
        this.networkTierGC = tierfound;
    }

    /**
     * Refresh all energy acceptors in the network
     */
    private void refreshAcceptors() {
        this.connectedAcceptors.clear();
        this.connectedDirections.clear();

        this.refreshWithChecks();

        try {
            final LinkedList<IConductor> conductorsCopy = new LinkedList<>(this.conductors);
            // This prevents concurrent modifications if something in the loop causes chunk
            // loading
            // (Chunk loading can change the network if new conductors are found)
            for (final IConductor conductor : conductorsCopy) {
                EnergyUtil.setAdjacentPowerConnections(
                    (TileEntity) conductor,
                    this.connectedAcceptors,
                    this.connectedDirections);
            }
        } catch (final Exception e) {
            FMLLog.severe("GC Aluminium Wire: Error when testing whether another mod's tileEntity can accept energy.");
            e.printStackTrace();
        }
    }

    /**
     * Combine this network with another electricitynetwork
     *
     * @param network Network to merge with
     * @return The final, joined network
     */
    @Override
    public IElectricityNetwork merge(IElectricityNetwork network) {
        if (network != null && network != this) {
            final Set<IConductor> thisNetwork = this.conductors;
            final Set<IConductor> thatNetwork = network.getTransmitters();
            if (thisNetwork.size() < thatNetwork.size()) {
                thatNetwork.addAll(thisNetwork);
                network.refresh();
                this.destroy();
                return network;
            }
            thisNetwork.addAll(thatNetwork);
            this.refresh();
            if (network instanceof EnergyNetwork) {
                ((EnergyNetwork) network).destroy();
            }
        }

        return this;
    }

    private void destroy() {
        this.conductors.clear();
        this.connectedAcceptors.clear();
        this.availableAcceptors.clear();
        this.totalEnergy = 0F;
        this.totalRequested = 0F;
        try {
            final Class<?> clazz = Class.forName("micdoodle8.mods.galacticraft.core.tick.TickHandlerServer");
            clazz.getMethod("removeNetworkTick", this.getClass())
                .invoke(null, this);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void split(IConductor splitPoint) {
        if (splitPoint instanceof TileEntity) {
            this.getTransmitters()
                .remove(splitPoint);
            splitPoint.setNetwork(null);

            // If the size of the residual network is 1, it should simply be preserved
            if (this.getTransmitters()
                .size() > 1) {
                final World world = ((TileEntity) splitPoint).getWorldObj();

                if (this.getTransmitters()
                    .size() > 0) {
                    final TileEntity[] nextToSplit = new TileEntity[6];
                    final boolean[] toDo = { true, true, true, true, true, true };
                    TileEntity tileEntity;

                    final int xCoord = ((TileEntity) splitPoint).xCoord;
                    final int yCoord = ((TileEntity) splitPoint).yCoord;
                    final int zCoord = ((TileEntity) splitPoint).zCoord;

                    for (int j = 0; j < 6; j++) {
                        switch (j) {
                            case 0:
                                tileEntity = world.getTileEntity(xCoord, yCoord - 1, zCoord);
                                break;
                            case 1:
                                tileEntity = world.getTileEntity(xCoord, yCoord + 1, zCoord);
                                break;
                            case 2:
                                tileEntity = world.getTileEntity(xCoord, yCoord, zCoord - 1);
                                break;
                            case 3:
                                tileEntity = world.getTileEntity(xCoord, yCoord, zCoord + 1);
                                break;
                            case 4:
                                tileEntity = world.getTileEntity(xCoord - 1, yCoord, zCoord);
                                break;
                            case 5:
                                tileEntity = world.getTileEntity(xCoord + 1, yCoord, zCoord);
                                break;
                            default:
                                // Not reachable, only to prevent uninitiated compile errors
                                tileEntity = null;
                                break;
                        }

                        if (tileEntity instanceof IConductor) {
                            nextToSplit[j] = tileEntity;
                        } else {
                            toDo[j] = false;
                        }
                    }

                    for (int i1 = 0; i1 < 6; i1++) {
                        if (toDo[i1]) {
                            final TileEntity connectedBlockA = nextToSplit[i1];
                            final NetworkFinder finder = new NetworkFinder(
                                world,
                                new BlockVec3(connectedBlockA),
                                new BlockVec3((TileEntity) splitPoint));
                            final List<IConductor> partNetwork = finder.exploreNetwork();

                            // Mark any others still to do in the nextToSplit array which are connected to
                            // this, as
                            // dealt with
                            for (int i2 = i1 + 1; i2 < 6; i2++) {
                                final TileEntity connectedBlockB = nextToSplit[i2];

                                if (toDo[i2] && partNetwork.contains(connectedBlockB)) {
                                    toDo[i2] = false;
                                }
                            }

                            // Now make the new network from partNetwork
                            final EnergyNetwork newNetwork = new EnergyNetwork();
                            newNetwork.getTransmitters()
                                .addAll(partNetwork);
                            newNetwork.refreshWithChecks();
                        }
                    }

                    this.destroy();
                }
            }
            // Splitting a 1-block network leaves nothing
            else if (this.getTransmitters()
                .size() == 0) {
                    this.destroy();
                }
        }
    }

    @Override
    public String toString() {
        return "EnergyNetwork[" + this.hashCode()
            + "|Wires:"
            + this.getTransmitters()
                .size()
            + "|Acceptors:"
            + this.connectedAcceptors.size()
            + "]";
    }
}
