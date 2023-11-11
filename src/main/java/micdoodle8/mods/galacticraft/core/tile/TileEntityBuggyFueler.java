package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.client.*;
import net.minecraft.block.*;
import net.minecraftforge.fluids.*;
import net.minecraft.item.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.world.*;

public class TileEntityBuggyFueler extends TileEntityMulti implements IMultiBlock, IFuelable, IFuelDock, ICargoEntity
{
    private IDockable dockedEntity;
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            final List<?> list = (List<?>)this.worldObj.getEntitiesWithinAABB((Class)IFuelable.class, AxisAlignedBB.getBoundingBox(this.xCoord - 1.5, this.yCoord - 2.0, this.zCoord - 1.5, this.xCoord + 1.5, this.yCoord + 4.0, this.zCoord + 1.5));
            boolean changed = false;
            for (final Object o : list) {
                if (o != null && o instanceof IDockable && !this.worldObj.isRemote) {
                    final IDockable fuelable = (IDockable)o;
                    if (!fuelable.isDockValid((IFuelDock)this)) {
                        continue;
                    }
                    (this.dockedEntity = fuelable).setPad((IFuelDock)this);
                    changed = true;
                }
            }
            if (!changed) {
                if (this.dockedEntity != null) {
                    this.dockedEntity.setPad((IFuelDock)null);
                }
                this.dockedEntity = null;
            }
        }
    }
    
    public boolean canUpdate() {
        return true;
    }
    
    public boolean onActivated(final EntityPlayer entityPlayer) {
        return false;
    }
    
    public void onCreate(final BlockVec3 placedPosition) {
        this.mainBlockPosition = placedPosition;
        this.markDirty();
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                final BlockVec3 vecToAdd = new BlockVec3(placedPosition.x + x, placedPosition.y, placedPosition.z + z);
                if (!vecToAdd.equals((Object)placedPosition)) {
                    ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, 6);
                }
            }
        }
    }
    
    public void onDestroy(final TileEntity callingBlock) {
        final BlockVec3 thisBlock = new BlockVec3((TileEntity)this);
        this.worldObj.func_147480_a(thisBlock.x, thisBlock.y, thisBlock.z, true);
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                if (this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.1) {
                    FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(thisBlock.x + x, thisBlock.y, thisBlock.z + z, GCBlocks.landingPad, Block.getIdFromBlock(GCBlocks.landingPad) >> 12 & 0xFF);
                }
                this.worldObj.func_147480_a(thisBlock.x + x, thisBlock.y, thisBlock.z + z, false);
            }
        }
        if (this.dockedEntity != null) {
            this.dockedEntity.onPadDestroyed();
            this.dockedEntity = null;
        }
    }
    
    public int addFuel(final FluidStack liquid, final boolean doFill) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.addFuel(liquid, doFill);
        }
        return 0;
    }
    
    public FluidStack removeFuel(final int amount) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.removeFuel(amount);
        }
        return null;
    }
    
    public ICargoEntity.EnumCargoLoadingState addCargo(final ItemStack stack, final boolean doAdd) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.addCargo(stack, doAdd);
        }
        return ICargoEntity.EnumCargoLoadingState.NOTARGET;
    }
    
    public ICargoEntity.RemovalResult removeCargo(final boolean doRemove) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.removeCargo(doRemove);
        }
        return new ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.NOTARGET, (ItemStack)null);
    }
    
    public HashSet<ILandingPadAttachable> getConnectedTiles() {
        final HashSet<ILandingPadAttachable> connectedTiles = new HashSet<ILandingPadAttachable>();
        for (int x = -2; x < 3; ++x) {
            for (int z = -2; z < 3; ++z) {
                if ((x == -2 || x == 2 || z == -2 || z == 2) && Math.abs(x) != Math.abs(z)) {
                    final TileEntity tile = this.worldObj.getTileEntity(this.xCoord + x, this.yCoord, this.zCoord + z);
                    if (tile != null && tile instanceof ILandingPadAttachable && ((ILandingPadAttachable)tile).canAttachToLandingPad((IBlockAccess)this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
                        connectedTiles.add((ILandingPadAttachable)tile);
                    }
                }
            }
        }
        return connectedTiles;
    }
    
    public boolean isBlockAttachable(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tile = world.getTileEntity(x, y, z);
        return tile != null && tile instanceof ILandingPadAttachable && ((ILandingPadAttachable)tile).canAttachToLandingPad(world, this.xCoord, this.yCoord, this.zCoord);
    }
    
    public IDockable getDockedEntity() {
        return this.dockedEntity;
    }
    
    public void dockEntity(final IDockable entity) {
        this.dockedEntity = entity;
    }
}
