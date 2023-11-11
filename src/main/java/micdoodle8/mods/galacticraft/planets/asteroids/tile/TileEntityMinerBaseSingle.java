package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import java.util.*;

public class TileEntityMinerBaseSingle extends TileEntity
{
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            final ArrayList<TileEntity> attachedBaseBlocks = new ArrayList<TileEntity>();
        Label_0111:
            for (int x = this.xCoord; x < this.xCoord + 2; ++x) {
                for (int y = this.yCoord; y < this.yCoord + 2; ++y) {
                    for (int z = this.zCoord; z < this.zCoord + 2; ++z) {
                        final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
                        if (!(tile instanceof TileEntityMinerBaseSingle)) {
                            break Label_0111;
                        }
                        attachedBaseBlocks.add(tile);
                    }
                }
            }
            if (attachedBaseBlocks.size() == 8) {
                for (final TileEntity tile2 : attachedBaseBlocks) {
                    tile2.invalidate();
                    tile2.getWorldObj().setBlock(tile2.xCoord, tile2.yCoord, tile2.zCoord, AsteroidBlocks.minerBaseFull, 0, 3);
                }
                for (int x = this.xCoord; x < this.xCoord + 2; ++x) {
                    for (int y = this.yCoord; y < this.yCoord + 2; ++y) {
                        for (int z = this.zCoord; z < this.zCoord + 2; ++z) {
                            final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
                            if (tile instanceof TileEntityMinerBase) {
                                ((TileEntityMinerBase)tile).setMainBlockPos(this.xCoord, this.yCoord, this.zCoord);
                            }
                        }
                    }
                }
            }
        }
    }
}
