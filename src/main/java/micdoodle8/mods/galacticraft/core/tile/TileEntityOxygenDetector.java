package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class TileEntityOxygenDetector extends TileEntityAdvanced
{
    public void updateEntity() {
        super.updateEntity();
        if (this.worldObj != null && !this.worldObj.isRemote && this.ticks % 50 == 0) {
            this.blockType = this.getBlockType();
            if (this.blockType != null && this.blockType instanceof BlockOxygenDetector) {
                ((BlockOxygenDetector)this.blockType).updateOxygenState(this.worldObj, this.xCoord, this.yCoord, this.zCoord, OxygenUtil.isAABBInBreathableAirBlock(this.worldObj, AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)(this.yCoord - 1), (double)(this.zCoord - 1), (double)(this.xCoord + 2), (double)(this.yCoord + 2), (double)(this.zCoord + 2))));
            }
        }
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
}
