package micdoodle8.mods.galacticraft.planets.mars.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss;

public class TileEntityDungeonSpawnerMars extends TileEntityDungeonSpawner {

    public TileEntityDungeonSpawnerMars() {
        super(EntityCreeperBoss.class);
    }

    @Override
    public List<Class<? extends EntityLiving>> getDisabledCreatures() {
        final List<Class<? extends EntityLiving>> list = new ArrayList<>();
        list.add(EntityEvolvedSkeleton.class);
        list.add(EntityEvolvedZombie.class);
        list.add(EntityEvolvedSpider.class);
        return list;
    }

    @Override
    public void playSpawnSound(Entity entity) {
        this.worldObj.playSoundAtEntity(entity, GalacticraftCore.TEXTURE_PREFIX + "ambience.scaryscape", 9.0F, 1.4F);
    }
}
