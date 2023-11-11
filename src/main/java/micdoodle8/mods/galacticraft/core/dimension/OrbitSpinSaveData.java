package micdoodle8.mods.galacticraft.core.dimension;

import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class OrbitSpinSaveData extends WorldSavedData
{
    public static final String saveDataID = "GCSpinData";
    public NBTTagCompound datacompound;
    private NBTTagCompound alldata;
    private int dim;
    
    public OrbitSpinSaveData(final String s) {
        super("GCSpinData");
        this.dim = 0;
        this.datacompound = new NBTTagCompound();
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        this.alldata = nbt;
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        if (this.dim != 0) {
            nbt.setTag("" + this.dim, (NBTBase)this.datacompound);
        }
    }
    
    public static OrbitSpinSaveData initWorldData(final World world) {
        OrbitSpinSaveData worldData = (OrbitSpinSaveData)world.loadItemData((Class)OrbitSpinSaveData.class, "GCSpinData");
        if (worldData == null) {
            worldData = new OrbitSpinSaveData("");
            world.setItemData("GCSpinData", (WorldSavedData)worldData);
            if (world.provider instanceof WorldProviderSpaceStation) {
                worldData.dim = world.provider.dimensionId;
                ((WorldProviderSpaceStation)world.provider).getSpinManager().writeToNBT(worldData.datacompound);
            }
            worldData.markDirty();
        }
        else if (world.provider instanceof WorldProviderSpaceStation) {
            worldData.dim = world.provider.dimensionId;
            worldData.datacompound = null;
            if (worldData.alldata != null) {
                worldData.datacompound = worldData.alldata.getCompoundTag("" + worldData.dim);
            }
            if (worldData.datacompound == null) {
                worldData.datacompound = new NBTTagCompound();
            }
        }
        return worldData;
    }
}
