package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.*;
import java.util.*;
import net.minecraft.nbt.*;

public class TileEntityFallenMeteor extends TileEntityAdvanced
{
    public static final int MAX_HEAT_LEVEL = 5000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int heatLevel;
    private boolean sentOnePacket;
    
    public TileEntityFallenMeteor() {
        this.heatLevel = 5000;
        this.sentOnePacket = false;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote && this.heatLevel > 0) {
            --this.heatLevel;
        }
    }
    
    public int getHeatLevel() {
        return this.heatLevel;
    }
    
    public void setHeatLevel(final int heatLevel) {
        this.heatLevel = heatLevel;
    }
    
    public float getScaledHeatLevel() {
        return this.heatLevel / 5000.0f;
    }
    
    public void readExtraNetworkedData(final ByteBuf dataStream) {
        if (this.worldObj.isRemote) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
    }
    
    public void addExtraNetworkedData(final List<Object> networkedList) {
        this.sentOnePacket = true;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heatLevel = nbt.getInteger("MeteorHeatLevel");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("MeteorHeatLevel", this.heatLevel);
    }
    
    public double getPacketRange() {
        return 50.0;
    }
    
    public int getPacketCooldown() {
        return this.sentOnePacket ? 100 : 1;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
}
