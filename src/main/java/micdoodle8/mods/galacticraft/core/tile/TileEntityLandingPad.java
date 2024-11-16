package micdoodle8.mods.galacticraft.core.tile;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController;

public class TileEntityLandingPad extends TileEntityMulti
    implements IMultiBlock, IFuelableTiered, IFuelDock, ICargoEntity {

    private IDockable dockedEntity;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.worldObj.isRemote) {
            final List<?> list = this.worldObj.getEntitiesWithinAABB(
                IFuelable.class,
                AxisAlignedBB.getBoundingBox(
                    this.xCoord - 0.5D,
                    this.yCoord,
                    this.zCoord - 0.5D,
                    this.xCoord + 0.5D,
                    this.yCoord + 1.0D,
                    this.zCoord + 0.5D));

            boolean docked = false;

            for (final Object o : list) {
                if (o instanceof IDockable && !((Entity) o).isDead) {
                    docked = true;

                    final IDockable fuelable = (IDockable) o;

                    if (fuelable != this.dockedEntity && fuelable.isDockValid(this)) {
                        if (fuelable instanceof ILandable) {
                            ((ILandable) fuelable).landEntity(this.xCoord, this.yCoord, this.zCoord);
                        } else {
                            fuelable.setPad(this);
                        }
                    }

                    break;
                }
            }

            if (!docked) {
                this.dockedEntity = null;
            }
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    public void onCreate(BlockVec3 placedPosition) {
        this.mainBlockPosition = placedPosition;
        this.markDirty();

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                final BlockVec3 vecToAdd = new BlockVec3(placedPosition.x + x, placedPosition.y, placedPosition.z + z);

                if (!vecToAdd.equals(placedPosition)) {
                    ((BlockMulti) GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, 2);
                }
            }
        }
    }

    @Override
    public void onDestroy(TileEntity callingBlock) {
        final BlockVec3 thisBlock = new BlockVec3(this);

        this.worldObj.func_147480_a(thisBlock.x, thisBlock.y, thisBlock.z, true);

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                if (this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.1D) {
                    FMLClientHandler.instance()
                        .getClient().effectRenderer.addBlockDestroyEffects(
                            thisBlock.x + x,
                            thisBlock.y,
                            thisBlock.z + z,
                            GCBlocks.landingPad,
                            Block.getIdFromBlock(GCBlocks.landingPad) >> 12 & 255);
                }

                this.worldObj.func_147480_a(thisBlock.x + x, thisBlock.y, thisBlock.z + z, false);
            }
        }

        if (this.dockedEntity != null) {
            this.dockedEntity.onPadDestroyed();
            this.dockedEntity = null;
        }
    }

    @Override
    public int addFuel(FluidStack liquid, boolean doFill) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.addFuel(liquid, doFill);
        }

        return 0;
    }

    @Override
    public FluidStack removeFuel(int amount) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.removeFuel(amount);
        }

        return null;
    }

    @Override
    public int getRocketTier() {
        if (this.dockedEntity != null && this.dockedEntity instanceof EntityTieredRocket) {
            return ((EntityTieredRocket) this.dockedEntity).getRocketTier();
        }
        return 0;
    }

    @Override
    public HashSet<ILandingPadAttachable> getConnectedTiles() {
        final HashSet<ILandingPadAttachable> connectedTiles = new HashSet<>();

        for (int x = this.xCoord - 1; x < this.xCoord + 2; x++) {
            this.testConnectedTile(x, this.zCoord - 2, connectedTiles);
            this.testConnectedTile(x, this.zCoord + 2, connectedTiles);
        }

        for (int z = this.zCoord - 1; z < this.zCoord + 2; z++) {
            this.testConnectedTile(this.xCoord - 2, z, connectedTiles);
            this.testConnectedTile(this.xCoord + 2, z, connectedTiles);
        }

        return connectedTiles;
    }

    private void testConnectedTile(int x, int z, HashSet<ILandingPadAttachable> connectedTiles) {
        if (!this.worldObj.blockExists(x, this.yCoord, z)) {
            return;
        }

        final TileEntity tile = this.worldObj.getTileEntity(x, this.yCoord, z);

        if (tile instanceof ILandingPadAttachable && ((ILandingPadAttachable) tile)
            .canAttachToLandingPad(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            connectedTiles.add((ILandingPadAttachable) tile);
            if (GalacticraftCore.isPlanetsLoaded && tile instanceof TileEntityLaunchController) {
                ((TileEntityLaunchController) tile).setAttachedPad(this);
            }
        }
    }

    @Override
    public EnumCargoLoadingState addCargo(ItemStack stack, boolean doAdd) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.addCargo(stack, doAdd);
        }

        return EnumCargoLoadingState.NOTARGET;
    }

    @Override
    public RemovalResult removeCargo(boolean doRemove) {
        if (this.dockedEntity != null) {
            return this.dockedEntity.removeCargo(doRemove);
        }

        return new RemovalResult(EnumCargoLoadingState.NOTARGET, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(
            this.xCoord - 1,
            this.yCoord,
            this.zCoord - 1,
            this.xCoord + 2,
            this.yCoord + 0.4D,
            this.zCoord + 2);
    }

    @Override
    public boolean isBlockAttachable(IBlockAccess world, int x, int y, int z) {
        final TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof ILandingPadAttachable) {
            return ((ILandingPadAttachable) tile).canAttachToLandingPad(world, this.xCoord, this.yCoord, this.zCoord);
        }

        return false;
    }

    @Override
    public IDockable getDockedEntity() {
        return this.dockedEntity;
    }

    @Override
    public void dockEntity(IDockable entity) {
        this.dockedEntity = entity;
    }
}
