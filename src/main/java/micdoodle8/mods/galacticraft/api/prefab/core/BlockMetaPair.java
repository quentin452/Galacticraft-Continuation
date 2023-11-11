package micdoodle8.mods.galacticraft.api.prefab.core;

import net.minecraft.block.*;

public class BlockMetaPair
{
    private final Block block;
    private final byte metadata;
    
    public BlockMetaPair(final Block block, final byte metadata) {
        this.block = block;
        this.metadata = metadata;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public byte getMetadata() {
        return this.metadata;
    }
}
