package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.nbt.*;

public class TileEntityAluminumWire extends TileBaseUniversalConductor
{
    public int tier;
    
    public TileEntityAluminumWire() {
        this(1);
    }
    
    public TileEntityAluminumWire(final int theTier) {
        this.tier = theTier;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.tier = nbt.getInteger("tier");
        if (this.tier == 0) {
            this.tier = 1;
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("tier", this.tier);
    }
    
    public double getPacketRange() {
        return 0.0;
    }
    
    public int getPacketCooldown() {
        return 0;
    }
    
    public boolean isNetworkedTile() {
        return false;
    }
    
    public int getTierGC() {
        return this.tier;
    }
}
