package micdoodle8.mods.galacticraft.core.energy.tile;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import mekanism.api.energy.ICableOutputter;
import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper;
import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import micdoodle8.mods.galacticraft.core.util.VersionUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Method;
import java.util.EnumSet;

@InterfaceList({ @Interface(modid = "IC2API", iface = "ic2.api.energy.tile.IEnergySource"),
    @Interface(modid = "MekanismAPI|energy", iface = "mekanism.api.energy.ICableOutputter"), })
public class TileBaseUniversalElectricalSource extends TileBaseUniversalElectrical
    implements ICableOutputter {

    /*
     * The main function to output energy each tick from a source. The source will attempt to produce into its
     * outputDirections whatever energy it has available, and will reduce its stored energy by the amount which is in
     * fact used. Max output = this.storage.maxExtract.
     * @return The amount of energy that was used.
     */
    public float produce() {
        this.storage.maxExtractRemaining = this.storage.maxExtract;
        final float produced = this.extractEnergyGC(null, this.produce(false), false);
        this.storage.maxExtractRemaining -= produced;
        if (this.storage.maxExtractRemaining < 0) {
            this.storage.maxExtractRemaining = 0;
        }
        return produced;
    }

    /*
     * Function to produce energy each tick into the outputs of a source. If simulate is true, no energy is in fact
     * transferred. Note: even if simulate is false this does NOT reduce the source's own energy storage by the amount
     * produced, that needs to be done elsewhere See this.produce() for an example.
     */
    public float produce(boolean simulate) {
        float amountProduced = 0;

        if (!this.worldObj.isRemote) {
            final EnumSet<ForgeDirection> outputDirections = this.getElectricalOutputDirections();
            outputDirections.remove(ForgeDirection.UNKNOWN);

            final BlockVec3 thisVec = new BlockVec3(this);
            for (final ForgeDirection direction : outputDirections) {
                final TileEntity tileAdj = thisVec.getTileEntityOnSide(this.worldObj, direction);

                if (tileAdj != null) {
                    final float toSend = this.extractEnergyGC(
                        null,
                        Math.min(
                            this.getEnergyStoredGC() - amountProduced,
                            this.getEnergyStoredGC() / outputDirections.size()),
                        true);
                    if (toSend <= 0) {
                        continue;
                    }

                    if (tileAdj instanceof TileBaseConductor) {
                        final IElectricityNetwork network = ((IConductor) tileAdj).getNetwork();
                        if (network != null) {
                            amountProduced += toSend - network.produce(toSend, !simulate, this.tierGC, this);
                        }
                    } else if (tileAdj instanceof TileBaseUniversalElectrical) {
                        amountProduced += ((TileBaseUniversalElectrical) tileAdj)
                            .receiveElectricity(direction.getOpposite(), toSend, this.tierGC, !simulate);
                    } else {
                        amountProduced += EnergyUtil
                            .otherModsEnergyTransfer(tileAdj, direction.getOpposite(), toSend, simulate);
                    }
                }
            }
        }

        return amountProduced;
    }

    /**
     * Recharges electric item.
     */
    public void recharge(ItemStack itemStack) {
        if (itemStack != null) {
            final Item item = itemStack.getItem();
            final float maxExtractSave = this.storage.getMaxExtract();
            if (this.tierGC > 1) {
                this.storage.setMaxExtract(maxExtractSave * 2.5F);
            }
            final float energyToCharge = this.storage.extractEnergyGC(this.storage.getMaxExtract(), true);

            if (item instanceof IItemElectric) {
                this.storage.extractEnergyGC(ElectricItemHelper.chargeItem(itemStack, energyToCharge), false);
            } else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem) {
                this.storage.extractEnergyGC(
                    ((IEnergyContainerItem) item)
                        .receiveEnergy(itemStack, (int) (energyToCharge * EnergyConfigHandler.TO_RF_RATIO), false)
                        / EnergyConfigHandler.TO_RF_RATIO,
                    false);
            }
            // else if (GCCoreCompatibilityManager.isTELoaded() && itemStack.getItem()
            // instanceof
            // IEnergyContainerItem)
            // {
            // int accepted = ((IEnergyContainerItem)
            // itemStack.getItem()).receiveEnergy(itemStack, (int)
            // Math.floor(this.getProvide(ForgeDirection.UNKNOWN) *
            // EnergyConfigHandler.TO_TE_RATIO), false);
            // this.provideElectricity(accepted * EnergyConfigHandler.TE_RATIO, true);
            // }

            if (this.tierGC > 1) {
                this.storage.setMaxExtract(maxExtractSave);
            }
        }
    }

    @Override
    public boolean canOutputTo(ForgeDirection side) {
        return this.getElectricalOutputDirections()
            .contains(side);
    }

    @Override
    public float getProvide(ForgeDirection direction) {

        if (this.getElectricalOutputDirections()
            .contains(direction)) {
            return this.storage.extractEnergyGC(Float.MAX_VALUE, true);
        }

        return 0F;
    }

    public ForgeDirection getElectricalOutputDirectionMain() {
        return ForgeDirection.UNKNOWN;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        if (EnergyConfigHandler.disableRFOutput || !this.getElectricalOutputDirections()
            .contains(from)) {
            return 0;
        }

        return MathHelper.floor_float(
            this.storage.extractEnergyGC(maxExtract / EnergyConfigHandler.TO_RF_RATIO, !simulate)
                * EnergyConfigHandler.TO_RF_RATIO);
    }
}
