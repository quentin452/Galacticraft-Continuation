package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.api.power.*;

public class TileEntityBeamReflector extends TileEntityBeamOutput implements ILaserNode
{
    public Vector3 color;
    private EnergyStorage storage;
    
    public TileEntityBeamReflector() {
        this.color = new Vector3(0.0, 1.0, 0.0);
        this.storage = new EnergyStorage(10.0f, 1.0f);
    }
    
    public void updateEntity() {
        super.updateEntity();
    }
    
    public Vector3 getInputPoint() {
        final float distance = 0.15f;
        final Vector3 deviation = new Vector3(Math.sin(Math.toRadians(this.yaw - 180.0f)) * distance, 0.0, Math.cos(Math.toRadians(this.yaw - 180.0f)) * distance);
        final Vector3 headVec = new Vector3(this.xCoord + 0.5, this.yCoord + 0.56614, this.zCoord + 0.5);
        headVec.translate(deviation.clone().invert());
        return headVec;
    }
    
    public Vector3 getOutputPoint(final boolean offset) {
        return new Vector3(this.xCoord + 0.5, this.yCoord + 0.56614, this.zCoord + 0.5);
    }
    
    public double getPacketRange() {
        return 24.0;
    }
    
    public int getPacketCooldown() {
        return 3;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
    
    public Vector3 getColor() {
        return this.color;
    }
    
    public boolean canConnectTo(final ILaserNode laserNode) {
        return this.color.equals((Object)laserNode.getColor());
    }
    
    public float receiveEnergyGC(final EnergySource from, final float amount, final boolean simulate) {
        if (this.getTarget() == null) {
            return this.storage.receiveEnergyGC(amount, simulate);
        }
        if (!(from instanceof EnergySource.EnergySourceWireless)) {
            return 0.0f;
        }
        if (((EnergySource.EnergySourceWireless)from).nodes.contains(this.getTarget())) {
            return 0.0f;
        }
        ((EnergySource.EnergySourceWireless)from).nodes.add(this);
        return this.getTarget().receiveEnergyGC(from, amount, simulate);
    }
    
    public float extractEnergyGC(final EnergySource from, final float amount, final boolean simulate) {
        return 0.0f;
    }
    
    public boolean nodeAvailable(final EnergySource from) {
        return from instanceof EnergySource.EnergySourceWireless;
    }
    
    public float getEnergyStoredGC(final EnergySource from) {
        return this.storage.getEnergyStoredGC();
    }
    
    public float getMaxEnergyStoredGC(final EnergySource from) {
        return this.storage.getCapacityGC();
    }
    
    public void setTarget(final ILaserNode target) {
        super.setTarget(target);
    }
}
