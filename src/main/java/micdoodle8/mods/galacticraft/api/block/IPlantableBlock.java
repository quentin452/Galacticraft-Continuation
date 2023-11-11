package micdoodle8.mods.galacticraft.api.block;

public interface IPlantableBlock
{
    int requiredLiquidBlocksNearby();
    
    boolean isPlantable(final int p0);
}
