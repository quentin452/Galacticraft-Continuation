package micdoodle8.mods.galacticraft.core.wrappers;

import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.block.*;

public class ScheduledBlockChange
{
    private BlockVec3 changePosition;
    private Block change;
    private int changeMeta;
    private int changeUpdateFlag;
    
    public ScheduledBlockChange(final BlockVec3 changePosition, final Block change, final int changeMeta) {
        this(changePosition, change, changeMeta, 3);
    }
    
    public ScheduledBlockChange(final BlockVec3 changePosition, final Block change, final int changeMeta, final int changeUpdateFlag) {
        this.changePosition = changePosition;
        this.change = change;
        this.changeMeta = changeMeta;
        this.changeUpdateFlag = changeUpdateFlag;
    }
    
    public BlockVec3 getChangePosition() {
        return this.changePosition;
    }
    
    public void setChangePosition(final BlockVec3 changePosition) {
        this.changePosition = changePosition;
    }
    
    public Block getChangeID() {
        return this.change;
    }
    
    public void setChangeID(final Block change) {
        this.change = change;
    }
    
    public int getChangeMeta() {
        return this.changeMeta;
    }
    
    public void setChangeMeta(final int changeMeta) {
        this.changeMeta = changeMeta;
    }
    
    public int getChangeUpdateFlag() {
        return this.changeUpdateFlag;
    }
    
    public void setChangeUpdateFlag(final int changeUpdateFlag) {
        this.changeUpdateFlag = changeUpdateFlag;
    }
}
