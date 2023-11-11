package micdoodle8.mods.galacticraft.api.entity;

public interface ITelemetry
{
    void transmitData(final int[] p0);
    
    void receiveData(final int[] p0, final String[] p1);
    
    void adjustDisplay(final int[] p0);
}
