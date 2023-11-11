package micdoodle8.mods.galacticraft.core.wrappers;

import micdoodle8.mods.galacticraft.api.vector.*;

public class Footprint
{
    public static final short MAX_AGE = 3200;
    public final int dimension;
    public final float rotation;
    public final Vector3 position;
    public short age;
    public final String owner;
    
    public Footprint(final int dimension, final Vector3 position, final float rotation, final String ownerUUID) {
        this(dimension, position, rotation, (short)0, ownerUUID);
    }
    
    public Footprint(final int dimension, final Vector3 position, final float rotation, final short age, final String ownerUUID) {
        this.dimension = dimension;
        this.position = position;
        this.rotation = rotation;
        this.age = age;
        this.owner = ownerUUID;
    }
}
