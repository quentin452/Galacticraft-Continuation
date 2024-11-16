package micdoodle8.mods.galacticraft.core.energy;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor;

public class EnergyUtil {

    private static final boolean isRFLoaded = EnergyConfigHandler.isRFAPILoaded();
    private static final boolean isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded();
    private static final boolean isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded();
    private static boolean isBCReallyLoaded = EnergyConfigHandler.isBuildcraftReallyLoaded();

    public static boolean voltageParameterIC2 = false;
    public static Method demandedEnergyIC2 = null;
    public static Method injectEnergyIC2 = null;
    public static Method offeredEnergyIC2 = null;
    public static Method drawEnergyIC2 = null;
    public static Class<?> clazzEnderIOCable = null;
    public static Class<?> clazzMFRRednetEnergyCable = null;
    public static Class<?> clazzRailcraftEngine = null;
    private static Class<?> clazzPipeTile = null;
    public static boolean initialisedIC2Methods = EnergyUtil.initialiseIC2Methods();

    /**
     * Tests whether an IConductor tile (a GC Aluminium Wire) can connect on each its 6 sides Returns a 6 member array,
     * containing for each of the 6 standard directions: the connectable TileEntity if a connection was found, or else
     * null. (This saves on the calling code having to use World.getTileEntity(x, y, z) a second time.)
     *
     * @param tile
     * @return
     */
    public static TileEntity[] getAdjacentPowerConnections(TileEntity tile) {
        final TileEntity[] adjacentConnections = new TileEntity[6];

        final BlockVec3 thisVec = new BlockVec3(tile);
        final World world = tile.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);

            if (tileEntity == null) {
                continue;
            }

            if (tileEntity instanceof IConnector) {
                if (((IConnector) tileEntity).canConnect(direction.getOpposite(), NetworkType.POWER)) {
                    adjacentConnections[direction.ordinal()] = tileEntity;
                }
                continue;
            }

            // Do not connect GC wires directly to BC pipes of any type
            if (isBCReallyLoaded && clazzPipeTile.isInstance(tileEntity)) {
                continue;
            }

            if (isRFLoaded && tileEntity instanceof IEnergyConnection) {
                if (isRF2Loaded && (tileEntity instanceof IEnergyProvider || tileEntity instanceof IEnergyReceiver)
                    || isRF1Loaded && tileEntity instanceof IEnergyHandler
                    || clazzRailcraftEngine != null && clazzRailcraftEngine.isInstance(tileEntity)) {
                    // Do not connect GC wires directly to power conduits
                    if (clazzEnderIOCable != null && clazzEnderIOCable.isInstance(tileEntity)
                        || clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable.isInstance(tileEntity)) {
                        continue;
                    }

                    if (((IEnergyConnection) tileEntity).canConnectEnergy(direction.getOpposite())) {
                        adjacentConnections[direction.ordinal()] = tileEntity;
                    }
                }
                continue;
            }
        }

        return adjacentConnections;
    }

    /**
     * Similar to getAdjacentPowerConnections but specific to energy receivers only Adds the adjacent power connections
     * found to the passed acceptors, directions parameter Lists (Note: an acceptor can therefore sometimes be entered
     * in the Lists more than once, with a different direction each time: this would represent GC wires connected to the
     * acceptor on more than one side.)
     *
     * @param conductor
     * @param acceptors
     * @param directions
     * @throws Exception
     */
    public static void setAdjacentPowerConnections(TileEntity conductor, List<TileEntity> acceptors,
        List<ForgeDirection> directions) throws Exception {
        final BlockVec3 thisVec = new BlockVec3(conductor);
        final World world = conductor.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);

            if (tileEntity == null || tileEntity instanceof IConductor) // world.getTileEntity will not have returned an
            // invalid tile, invalid tiles are
            // null
            {
                continue;
            }

            final ForgeDirection sideFrom = direction.getOpposite();

            if (tileEntity instanceof IElectrical) {
                if (((IElectrical) tileEntity).canConnect(sideFrom, NetworkType.POWER)) {
                    acceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
                continue;
            }

            if (isBCReallyLoaded && clazzPipeTile.isInstance(tileEntity)) {
                continue;
            }

            if (isRF2Loaded && tileEntity instanceof IEnergyReceiver
                || isRF1Loaded && tileEntity instanceof IEnergyHandler) {
                if (clazzEnderIOCable != null && clazzEnderIOCable.isInstance(tileEntity)
                    || clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable.isInstance(tileEntity)) {
                    continue;
                }

                if (((IEnergyConnection) tileEntity).canConnectEnergy(sideFrom)) {
                    acceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
            }
        }
    }

    public static float otherModsEnergyTransfer(TileEntity tileAdj, ForgeDirection inputAdj, float toSend,
        boolean simulate) {
        if (isRF1Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyHandler) {

            // GCLog.debug("Beam/storage offering RF1 up to " + toSend + " into pipe, it
            // accepted " + sent);
            return ((IEnergyHandler) tileAdj)
                .receiveEnergy(inputAdj, MathHelper.floor_float(toSend * EnergyConfigHandler.TO_RF_RATIO), simulate)
                / EnergyConfigHandler.TO_RF_RATIO;
        } else if (isRF2Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyReceiver) {

            // GCLog.debug("Beam/storage offering RF2 up to " + toSend + " into pipe, it
            // accepted " + sent);
            return ((IEnergyReceiver) tileAdj)
                .receiveEnergy(inputAdj, MathHelper.floor_float(toSend * EnergyConfigHandler.TO_RF_RATIO), simulate)
                / EnergyConfigHandler.TO_RF_RATIO;
        }
        return 0F;
    }

    public static float otherModsEnergyExtract(TileEntity tileAdj, ForgeDirection inputAdj, float toPull,
        boolean simulate) {
        if (isRF2Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyProvider) {
            return ((IEnergyProvider) tileAdj)
                .extractEnergy(inputAdj, MathHelper.floor_float(toPull * EnergyConfigHandler.TO_RF_RATIO), simulate)
                / EnergyConfigHandler.TO_RF_RATIO;
        } else if (isRF1Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyHandler) {
            return ((IEnergyHandler) tileAdj)
                .extractEnergy(inputAdj, MathHelper.floor_float(toPull * EnergyConfigHandler.TO_RF_RATIO), simulate)
                / EnergyConfigHandler.TO_RF_RATIO;
        }

        return 0F;
    }

    /**
     * Test whether an energy connection can be made to a tile using other mods' energy methods.
     * <p>
     * Parameters:
     *
     * @param tileAdj  - the tile under test, it might be an energy tile from another mod
     * @param inputAdj - the energy input side for that tile which is under test
     */
    public static boolean otherModCanReceive(TileEntity tileAdj, ForgeDirection inputAdj) {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile) {
            return false; // Do not try using other mods' methods to connect to GC's own tiles
        }

        if (isRF1Loaded && tileAdj instanceof IEnergyHandler || isRF2Loaded && tileAdj instanceof IEnergyReceiver) {
            return ((IEnergyConnection) tileAdj).canConnectEnergy(inputAdj);
        }

        return false;
    }

    /**
     * Test whether a tile can output energy using other mods' energy methods. Currently restricted to IC2 and RF mods -
     * Mekanism tiles do not provide an interface to "output" energy
     * <p>
     * Parameters:
     *
     * @param tileAdj - the tile under test, it might be an energy tile from another mod
     * @param side    - the energy output side for that tile which is under test
     */
    public static boolean otherModCanProduce(TileEntity tileAdj, ForgeDirection side) {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile) {
            return false; // Do not try using other mods' methods to connect to GC's own tiles
        }

        if (isRF1Loaded && tileAdj instanceof IEnergyHandler || isRF2Loaded && tileAdj instanceof IEnergyProvider) {
            return ((IEnergyConnection) tileAdj).canConnectEnergy(side);
        }

        return false;
    }

    public static boolean initialiseIC2Methods() {
        // Initialise a couple of non-IC2 classes
        try {
            clazzEnderIOCable = Class.forName("crazypants.enderio.conduit.TileConduitBundle");
        } catch (final Exception e) {}
        try {
            clazzMFRRednetEnergyCable = Class
                .forName("powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy");
        } catch (final Exception e) {}
        try {
            clazzRailcraftEngine = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngine");
        } catch (final Exception e) {}
        try {
            clazzPipeTile = Class.forName("buildcraft.transport.TileGenericPipe");
        } catch (final Exception e) {}

        if (clazzPipeTile == null) {
            isBCReallyLoaded = false;
        }

        return true;
    }
}
