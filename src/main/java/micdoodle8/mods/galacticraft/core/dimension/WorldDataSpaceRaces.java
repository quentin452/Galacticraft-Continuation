package micdoodle8.mods.galacticraft.core.dimension;

import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class WorldDataSpaceRaces extends WorldSavedData
{
    public static final String saveDataID = "GCSpaceRaceData";
    private NBTTagCompound dataCompound;
    
    public WorldDataSpaceRaces(final String id) {
        super(id);
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        SpaceRaceManager.loadSpaceRaces(nbt);
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        SpaceRaceManager.saveSpaceRaces(nbt);
    }
    
    public static WorldDataSpaceRaces initWorldData(final World world) {
        WorldDataSpaceRaces worldData = (WorldDataSpaceRaces)world.loadItemData((Class)WorldDataSpaceRaces.class, "GCSpaceRaceData");
        if (worldData == null) {
            worldData = new WorldDataSpaceRaces("GCSpaceRaceData");
            world.setItemData("GCSpaceRaceData", (WorldSavedData)worldData);
            worldData.dataCompound = new NBTTagCompound();
            worldData.markDirty();
        }
        return worldData;
    }
    
    public boolean isDirty() {
        return true;
    }
}
