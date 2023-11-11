package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.client.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;

public class TileEntityNasaWorkbench extends TileEntityMulti implements IMultiBlock
{
    public boolean onActivated(final EntityPlayer entityPlayer) {
        entityPlayer.openGui((Object)GalacticraftCore.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }
    
    public void onCreate(final BlockVec3 placedPosition) {
        this.mainBlockPosition = placedPosition;
        this.markDirty();
        final int buildHeight = this.worldObj.getHeight() - 1;
        for (int y = 1; y < 3; ++y) {
            if (placedPosition.y + y > buildHeight) {
                return;
            }
            for (int x = -1; x < 2; ++x) {
                for (int z = -1; z < 2; ++z) {
                    final BlockVec3 vecToAdd = new BlockVec3(placedPosition.x + x, placedPosition.y + y, placedPosition.z + z);
                    if (!vecToAdd.equals((Object)placedPosition) && (Math.abs(x) != 1 || Math.abs(z) != 1)) {
                        ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, 3);
                    }
                }
            }
        }
        if (placedPosition.y + 3 > buildHeight) {
            return;
        }
        final BlockVec3 vecToAdd2 = new BlockVec3(placedPosition.x, placedPosition.y + 3, placedPosition.z);
        ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd2, placedPosition, 3);
    }
    
    public void onDestroy(final TileEntity callingBlock) {
        final BlockVec3 thisBlock = new BlockVec3((TileEntity)this);
        for (int x = -1; x < 2; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = -1; z < 2; ++z) {
                    if (Math.abs(x) != 1 || Math.abs(z) != 1) {
                        if ((y == 0 || y == 3) && x == 0 && z == 0) {
                            if (this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.05) {
                                FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(thisBlock.x + x, thisBlock.y + y, thisBlock.z + z, GCBlocks.nasaWorkbench, Block.getIdFromBlock(GCBlocks.nasaWorkbench) >> 12 & 0xFF);
                            }
                            if (y == 0) {
                                this.worldObj.func_147480_a(thisBlock.x, thisBlock.y, thisBlock.z, true);
                            }
                            else {
                                this.worldObj.setBlockToAir(thisBlock.x + x, thisBlock.y + y, thisBlock.z + z);
                            }
                        }
                        else if (y != 0 && y != 3) {
                            if (this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.05) {
                                FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(thisBlock.x + x, thisBlock.y + y, thisBlock.z + z, GCBlocks.nasaWorkbench, Block.getIdFromBlock(GCBlocks.nasaWorkbench) >> 12 & 0xFF);
                            }
                            this.worldObj.setBlockToAir(thisBlock.x + x, thisBlock.y + y, thisBlock.z + z);
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)this.yCoord, (double)(this.zCoord - 1), (double)(this.xCoord + 2), (double)(this.yCoord + 4), (double)(this.zCoord + 2));
    }
}
