package micdoodle8.mods.galacticraft.api.power;

public interface IEnergyStorageGC
{
    float receiveEnergyGC(final float p0, final boolean p1);
    
    float extractEnergyGC(final float p0, final boolean p1);
    
    float getEnergyStoredGC();
    
    float getCapacityGC();
}
