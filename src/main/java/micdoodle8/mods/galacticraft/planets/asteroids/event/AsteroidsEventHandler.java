package micdoodle8.mods.galacticraft.planets.asteroids.event;

import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import cpw.mods.fml.common.eventhandler.*;

public class AsteroidsEventHandler
{
    @SubscribeEvent
    public void onThermalArmorEvent(final GCPlayerHandler.ThermalArmorEvent event) {
        if (event.armorStack == null) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.REMOVE);
            return;
        }
        if (event.armorStack.getItem() == AsteroidsItems.thermalPadding && event.armorStack.getItemDamage() == event.armorIndex) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.ADD);
            return;
        }
        event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.NOTHING);
    }
}
