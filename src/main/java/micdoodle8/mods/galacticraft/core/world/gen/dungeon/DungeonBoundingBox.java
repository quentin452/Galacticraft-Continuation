package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

public class DungeonBoundingBox
{
    int minX;
    int minZ;
    int maxX;
    int maxZ;
    
    public DungeonBoundingBox(final int minX, final int minZ, final int maxX, final int maxZ) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }
    
    public boolean isOverlapping(final DungeonBoundingBox bb) {
        return this.minX < bb.maxX && this.minZ < bb.maxZ && this.maxX > bb.minX && this.maxZ > bb.minZ;
    }
}
