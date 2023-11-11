package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.*;

public class TileEntityDungeonSpawnerMars extends TileEntityDungeonSpawner
{
    public TileEntityDungeonSpawnerMars() {
        super((Class)EntityCreeperBoss.class);
    }
    
    public List<Class<? extends EntityLiving>> getDisabledCreatures() {
        final List<Class<? extends EntityLiving>> list = new ArrayList<Class<? extends EntityLiving>>();
        list.add((Class<? extends EntityLiving>)EntityEvolvedSkeleton.class);
        list.add((Class<? extends EntityLiving>)EntityEvolvedZombie.class);
        list.add((Class<? extends EntityLiving>)EntityEvolvedSpider.class);
        return list;
    }
    
    public void playSpawnSound(final Entity entity) {
        this.worldObj.playSoundAtEntity(entity, GalacticraftCore.TEXTURE_PREFIX + "ambience.scaryscape", 9.0f, 1.4f);
    }
}
