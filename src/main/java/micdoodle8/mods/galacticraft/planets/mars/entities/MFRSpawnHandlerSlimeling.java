package micdoodle8.mods.galacticraft.planets.mars.entities;

import powercrystals.minefactoryreloaded.api.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;

public class MFRSpawnHandlerSlimeling implements IMobSpawnHandler
{
    public Class<? extends EntityLivingBase> getMobClass() {
        return (Class<? extends EntityLivingBase>)EntitySlimeling.class;
    }
    
    public void onMobSpawn(final EntityLivingBase entity) {
    }
    
    public void onMobExactSpawn(final EntityLivingBase entity) {
        final EntitySlimeling ent = (EntitySlimeling)entity;
        ent.slimelingInventory = new InventorySlimeling(ent);
    }
}
