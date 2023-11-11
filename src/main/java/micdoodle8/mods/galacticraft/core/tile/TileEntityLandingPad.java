package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.util.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.client.*;
import net.minecraft.block.*;
import net.minecraftforge.fluids.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;

public class TileEntityLandingPad extends TileEntityMulti implements IMultiBlock, IFuelable, IFuelDock, ICargoEntity
{
    private IDockable dockedEntity;
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            final List<?> list = (List<?>)this.worldObj.getEntitiesWithinAABB((Class)IFuelable.class, AxisAlignedBB.getBoundingBox(this.xCoord - 0.5, (double)this.yCoord, this.zCoord - 0.5, this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord + 0.5));
            boolean docked = false;
            for (final Object o : list) {
                if (o instanceof IDockable && !((Entity)o).isDead) {
                    docked = true;
                    final IDockable fuelable = (IDockable)o;
                    if (fuelable == this.dockedEntity || !fuelable.isDockValid((IFuelDock)this)) {
                        break;
                    }
                    if (fuelable instanceof ILandable) {
                        ((ILandable)fuelable).landEntity(this.xCoord, this.yCoord, this.zCoord);
                        break;
                    }
                    fuelable.setPad((IFuelDock)this);
                    break;
                }
            }
            if (!docked) {
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
                    ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, 2);
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
    
    public HashSet<ILandingPadAttachable> getConnectedTiles() {
        final HashSet<ILandingPadAttachable> connectedTiles = new HashSet<ILandingPadAttachable>();
        for (int x = this.xCoord - 1; x < this.xCoord + 2; ++x) {
            this.testConnectedTile(x, this.zCoord - 2, connectedTiles);
            this.testConnectedTile(x, this.zCoord + 2, connectedTiles);
        }
        for (int z = this.zCoord - 1; z < this.zCoord + 2; ++z) {
            this.testConnectedTile(this.xCoord - 2, z, connectedTiles);
            this.testConnectedTile(this.xCoord + 2, z, connectedTiles);
        }
        return connectedTiles;
    }
    
    private void testConnectedTile(final int x, final int z, final HashSet<ILandingPadAttachable> connectedTiles) {
        if (!this.worldObj.blockExists(x, this.yCoord, z)) {
            return;
        }
        final TileEntity tile = this.worldObj.getTileEntity(x, this.yCoord, z);
        if (tile instanceof ILandingPadAttachable && ((ILandingPadAttachable)tile).canAttachToLandingPad((IBlockAccess)this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            connectedTiles.add((ILandingPadAttachable)tile);
            if (GalacticraftCore.isPlanetsLoaded && tile instanceof TileEntityLaunchController) {
                ((TileEntityLaunchController)tile).setAttachedPad((IFuelDock)this);
            }
        }
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
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)this.yCoord, (double)(this.zCoord - 1), (double)(this.xCoord + 2), this.yCoord + 0.4, (double)(this.zCoord + 2));
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
