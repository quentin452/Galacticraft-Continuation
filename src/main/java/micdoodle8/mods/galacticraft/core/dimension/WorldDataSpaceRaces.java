package micdoodle8.mods.galacticraft.core.dimension;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class WorldDataSpaceRaces extends WorldSavedData {

    public static final String saveDataID = "GCSpaceRaceData";

    public WorldDataSpaceRaces(String id) {
        super(id);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        SpaceRaceManager.loadSpaceRaces(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        SpaceRaceManager.saveSpaceRaces(nbt);
    }

    public static WorldDataSpaceRaces initWorldData(World world) {
        WorldDataSpaceRaces worldData = (WorldDataSpaceRaces) world
            .loadItemData(WorldDataSpaceRaces.class, WorldDataSpaceRaces.saveDataID);

        if (worldData == null) {
            worldData = new WorldDataSpaceRaces(WorldDataSpaceRaces.saveDataID);
            world.setItemData(WorldDataSpaceRaces.saveDataID, worldData);
            worldData.markDirty();
        }

        return worldData;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
