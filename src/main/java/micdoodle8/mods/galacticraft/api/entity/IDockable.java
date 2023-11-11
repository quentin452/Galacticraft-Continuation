package micdoodle8.mods.galacticraft.api.entity;

import micdoodle8.mods.galacticraft.api.tile.*;

public interface IDockable extends IFuelable, ICargoEntity
{
    void setPad(final IFuelDock p0);
    
    IFuelDock getLandingPad();
    
    void onPadDestroyed();
    
    boolean isDockValid(final IFuelDock p0);
}
