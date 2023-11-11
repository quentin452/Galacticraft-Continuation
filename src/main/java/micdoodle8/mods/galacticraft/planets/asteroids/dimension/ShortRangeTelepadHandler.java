package micdoodle8.mods.galacticraft.planets.asteroids.dimension;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.nbt.*;
import java.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tick.*;
import com.google.common.collect.*;
import org.apache.commons.lang3.builder.*;

public class ShortRangeTelepadHandler extends WorldSavedData
{
    public static final String saveDataID = "ShortRangeTelepads";
    private static Map<Integer, TelepadEntry> tileMap;

    public ShortRangeTelepadHandler(final String saveDataID) {
        super(saveDataID);
    }

    public void readFromNBT(final NBTTagCompound nbt) {
        final NBTTagList tagList = nbt.getTagList("TelepadList", 10);
        ShortRangeTelepadHandler.tileMap.clear();
        for (int i = 0; i < tagList.tagCount(); ++i) {
            final NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
            final int address = nbt2.getInteger("Address");
            final int dimID = nbt2.getInteger("DimID");
            final int posX = nbt2.getInteger("PosX");
            final int posY = nbt2.getInteger("PosY");
            final int posZ = nbt2.getInteger("PosZ");
            ShortRangeTelepadHandler.tileMap.put(address, new TelepadEntry(dimID, new BlockVec3(posX, posY, posZ)));
        }
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        final NBTTagList tagList = new NBTTagList();
        for (final Map.Entry<Integer, TelepadEntry> e : ShortRangeTelepadHandler.tileMap.entrySet()) {
            final NBTTagCompound nbt2 = new NBTTagCompound();
            nbt2.setInteger("Address", (int)e.getKey());
            nbt2.setInteger("DimID", e.getValue().dimensionID);
            nbt2.setInteger("PosX", e.getValue().position.x);
            nbt2.setInteger("PosY", e.getValue().position.y);
            nbt2.setInteger("PosZ", e.getValue().position.z);
            tagList.appendTag((NBTBase)nbt2);
        }
        nbt.setTag("TelepadList", (NBTBase)tagList);
    }

    public static void addShortRangeTelepad(final TileEntityShortRangeTelepad telepad) {
        if (!telepad.getWorld().isRemote && telepad.addressValid) {
            final TelepadEntry newEntry = new TelepadEntry(telepad.getWorldObj().provider.dimensionId, new BlockVec3((TileEntity)telepad));
            final TelepadEntry previous = ShortRangeTelepadHandler.tileMap.put(telepad.address, newEntry);
            if (previous == null || !previous.equals(newEntry)) {
                AsteroidsTickHandlerServer.spaceRaceData.setDirty(true);
            }
        }
    }

    public static void removeShortRangeTeleporter(final TileEntityShortRangeTelepad telepad) {
        if (!telepad.getWorld().isRemote && telepad.addressValid) {
            ShortRangeTelepadHandler.tileMap.remove(telepad.address);
            AsteroidsTickHandlerServer.spaceRaceData.setDirty(true);
        }
    }

    public static TelepadEntry getLocationFromAddress(final int address) {
        return ShortRangeTelepadHandler.tileMap.get(address);
    }

    static {
        ShortRangeTelepadHandler.tileMap = Maps.newHashMap();
    }

    public static class TelepadEntry
    {
        public int dimensionID;
        public BlockVec3 position;

        public TelepadEntry(final int dimID, final BlockVec3 position) {
            this.dimensionID = dimID;
            this.position = position;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.dimensionID).append(this.position.hashCode()).toHashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return other instanceof TelepadEntry && new EqualsBuilder().append(((TelepadEntry)other).dimensionID, this.dimensionID).append((Object)((TelepadEntry)other).position, (Object)this.position).isEquals();
        }
    }
}
