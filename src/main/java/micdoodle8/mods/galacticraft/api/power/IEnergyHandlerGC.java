package micdoodle8.mods.galacticraft.api.power;

public interface IEnergyHandlerGC
{
    float receiveEnergyGC(final EnergySource p0, final float p1, final boolean p2);
    
    float extractEnergyGC(final EnergySource p0, final float p1, final boolean p2);
    
    boolean nodeAvailable(final EnergySource p0);
    
    float getEnergyStoredGC(final EnergySource p0);
    
    float getMaxEnergyStoredGC(final EnergySource p0);
}
