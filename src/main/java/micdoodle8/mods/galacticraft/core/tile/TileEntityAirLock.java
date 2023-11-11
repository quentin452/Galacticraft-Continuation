package micdoodle8.mods.galacticraft.core.tile;

public class TileEntityAirLock extends TileEntityAdvanced
{
    public void updateEntity() {
        super.updateEntity();
    }
    
    public double getPacketRange() {
        return 0.0;
    }
    
    public int getPacketCooldown() {
        return 0;
    }
    
    public boolean isNetworkedTile() {
        return false;
    }
}
