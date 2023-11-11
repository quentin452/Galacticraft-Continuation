package micdoodle8.mods.galacticraft.planets.asteroids.dimension;

import net.minecraft.world.*;
import net.minecraft.nbt.*;

public class AsteroidSaveData extends WorldSavedData
{
    public static final String saveDataID = "GCAsteroidData";
    public NBTTagCompound datacompound;
    
    public AsteroidSaveData(final String s) {
        super("GCAsteroidData");
        this.datacompound = new NBTTagCompound();
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        this.datacompound = nbt.getCompoundTag("asteroids");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        nbt.setTag("asteroids", (NBTBase)this.datacompound);
    }
}
