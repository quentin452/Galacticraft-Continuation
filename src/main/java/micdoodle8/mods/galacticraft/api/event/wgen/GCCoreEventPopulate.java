package micdoodle8.mods.galacticraft.api.event.wgen;

import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.world.*;
import java.util.*;

public class GCCoreEventPopulate extends Event
{
    public final World worldObj;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    
    public GCCoreEventPopulate(final World worldObj, final Random rand, final int chunkX, final int chunkZ) {
        this.worldObj = worldObj;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }
    
    public static class Pre extends GCCoreEventPopulate
    {
        public Pre(final World world, final Random rand, final int worldX, final int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
    
    public static class Post extends GCCoreEventPopulate
    {
        public Post(final World world, final Random rand, final int worldX, final int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
}
