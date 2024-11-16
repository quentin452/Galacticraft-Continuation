package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;

public class TileEntityMinerBaseSingle extends TileEntity {

    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            final ArrayList<TileEntity> attachedBaseBlocks = new ArrayList<>();

            SEARCH: for (int x = this.xCoord; x < this.xCoord + 2; x++) {
                for (int y = this.yCoord; y < this.yCoord + 2; y++) {
                    for (int z = this.zCoord; z < this.zCoord + 2; z++) {
                        final TileEntity tile = this.worldObj.getTileEntity(x, y, z);

                        if (!(tile instanceof TileEntityMinerBaseSingle)) {
                            break SEARCH;
                        }
                        attachedBaseBlocks.add(tile);
                    }
                }
            }

            if (attachedBaseBlocks.size() == 8) {
                for (final TileEntity tile : attachedBaseBlocks) {
                    tile.invalidate();
                    tile.getWorldObj()
                        .setBlock(tile.xCoord, tile.yCoord, tile.zCoord, AsteroidBlocks.minerBaseFull, 0, 3);
                }

                for (int x = this.xCoord; x < this.xCoord + 2; x++) {
                    for (int y = this.yCoord; y < this.yCoord + 2; y++) {
                        for (int z = this.zCoord; z < this.zCoord + 2; z++) {
                            final TileEntity tile = this.worldObj.getTileEntity(x, y, z);

                            if (tile instanceof TileEntityMinerBase) {
                                ((TileEntityMinerBase) tile).setMainBlockPos(this.xCoord, this.yCoord, this.zCoord);
                            }
                        }
                    }
                }
            }
        }
    }
}
