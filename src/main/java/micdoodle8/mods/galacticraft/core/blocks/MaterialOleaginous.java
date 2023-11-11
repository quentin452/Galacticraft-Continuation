package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class MaterialOleaginous extends MaterialLiquid
{
    private Class blockLiquidName;
    private Class blockLiquidStaticName;
    private Class blockLiquidDynamicName;
    
    public MaterialOleaginous(final MapColor color) {
        super(color);
        this.blockLiquidName = BlockLiquid.class;
        this.blockLiquidStaticName = BlockStaticLiquid.class;
        this.blockLiquidDynamicName = BlockDynamicLiquid.class;
        this.setNoPushMobility();
    }
    
    public boolean blocksMovement() {
        return JavaUtil.instance.isCalledBy(this.blockLiquidStaticName, this.blockLiquidName, this.blockLiquidDynamicName);
    }
}
