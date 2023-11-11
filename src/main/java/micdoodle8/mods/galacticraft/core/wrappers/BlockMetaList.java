package micdoodle8.mods.galacticraft.core.wrappers;

import net.minecraft.block.*;
import java.util.*;

public class BlockMetaList
{
    private Block block;
    private List<Integer> metaList;
    
    public BlockMetaList(final Block blockID, final Integer... metadata) {
        this(blockID, Arrays.asList(metadata));
    }
    
    public BlockMetaList(final Block blockID, final List<Integer> metadata) {
        this.block = blockID;
        this.metaList = metadata;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public List<Integer> getMetaList() {
        return this.metaList;
    }
    
    public void addMetadata(final int meta) {
        this.metaList.add(meta);
    }
    
    public void removeMetadata(final int meta) {
        this.metaList.remove(meta);
    }
    
    @Override
    public int hashCode() {
        return this.block.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof BlockMetaList && obj == this;
    }
}
