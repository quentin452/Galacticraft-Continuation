package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.util.Annotations.NetworkedField;

public class TileEntityTelepadFake extends TileBaseElectricBlock {

    // The the position of the main block
    @NetworkedField(targetSide = Side.CLIENT)
    public BlockVec3 mainBlockPosition;

    private WeakReference<TileEntityShortRangeTelepad> mainTelepad = null;

    public void setMainBlock(BlockVec3 mainBlock) {
        this.mainBlockPosition = mainBlock.clone();

        if (!this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    public void onBlockRemoval() {
        final TileEntityShortRangeTelepad telepad = this.getBaseTelepad();

        if (telepad != null) {
            telepad.onDestroy(this);
        }
    }

    public boolean onActivated(EntityPlayer par5EntityPlayer) {
        final TileEntityShortRangeTelepad telepad = this.getBaseTelepad();
        return telepad != null && telepad.onActivated(par5EntityPlayer);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        final TileEntityShortRangeTelepad telepad = this.getBaseTelepad();

        if (telepad != null) {
            this.storage.setCapacity(telepad.storage.getCapacityGC());
            this.storage.setMaxExtract(telepad.storage.getMaxExtract());
            this.storage.setMaxReceive(telepad.storage.getMaxReceive());
            this.extractEnergyGC(null, telepad.receiveEnergyGC(null, this.getEnergyStoredGC(), false), false);
        }
    }

    private TileEntityShortRangeTelepad getBaseTelepad() {
        if (this.mainBlockPosition == null) {
            return null;
        }

        if (this.mainTelepad == null) {
            final TileEntity tileEntity = this.mainBlockPosition.getTileEntity(this.worldObj);

            if (tileEntity != null && tileEntity instanceof TileEntityShortRangeTelepad) {
                this.mainTelepad = new WeakReference<>((TileEntityShortRangeTelepad) tileEntity);
            }
        }

        if (this.mainTelepad == null) {
            this.worldObj.setBlockToAir(this.mainBlockPosition.x, this.mainBlockPosition.y, this.mainBlockPosition.z);
        } else {
            final TileEntityShortRangeTelepad telepad = this.mainTelepad.get();

            if (telepad != null) {
                return telepad;
            }
            this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mainBlockPosition = new BlockVec3(nbt.getCompoundTag("mainBlockPosition"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (this.mainBlockPosition != null) {
            nbt.setTag("mainBlockPosition", this.mainBlockPosition.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public double getPacketRange() {
        return 30.0D;
    }

    @Override
    public int getPacketCooldown() {
        return 50;
    }

    @Override
    public boolean isNetworkedTile() {
        return true;
    }

    @Override
    public void getNetworkedData(ArrayList<Object> sendData) {
        if (this.mainBlockPosition == null && (this.worldObj.isRemote || !this.resetMainBlockPosition())) {
            return;
        }
        super.getNetworkedData(sendData);
    }

    private boolean resetMainBlockPosition() {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -2; y < 1; y += 2) {
                    final BlockVec3 vecToCheck = new BlockVec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
                    if (vecToCheck.getTileEntity(this.worldObj) instanceof TileEntityShortRangeTelepad) {
                        this.setMainBlock(vecToCheck);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldUseEnergy() {
        return false;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        if (this.getBlockMetadata() != 0) {
            return null;
        }

        return ForgeDirection.UP;
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return null;
    }
}
