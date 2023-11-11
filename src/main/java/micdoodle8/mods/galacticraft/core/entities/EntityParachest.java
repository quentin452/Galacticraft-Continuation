package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.entity.item.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.fluids.*;
import net.minecraft.tileentity.*;

public class EntityParachest extends Entity
{
    public ItemStack[] cargo;
    public int fuelLevel;
    private boolean placedChest;
    
    public EntityParachest(final World world, final ItemStack[] cargo, final int fuelLevel) {
        this(world);
        this.cargo = cargo.clone();
        this.placedChest = false;
        this.fuelLevel = fuelLevel;
    }
    
    public EntityParachest(final World world) {
        super(world);
        this.setSize(1.0f, 1.0f);
    }
    
    protected void entityInit() {
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        int size = 56;
        if (nbt.hasKey("CargoLength")) {
            size = nbt.getInteger("CargoLength");
        }
        this.cargo = new ItemStack[size];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.cargo.length) {
                this.cargo[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        this.placedChest = nbt.getBoolean("placedChest");
        this.fuelLevel = nbt.getInteger("FuelLevel");
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        nbt.setInteger("CargoLength", this.cargo.length);
        final NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.cargo.length; ++var3) {
            if (this.cargo[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.cargo[var3].writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        nbt.setTag("Items", (NBTBase)var2);
        nbt.setBoolean("placedChest", this.placedChest);
        nbt.setInteger("FuelLevel", this.fuelLevel);
    }
    
    public void onUpdate() {
        if (!this.placedChest) {
            if (this.onGround && !this.worldObj.isRemote) {
                for (int i = 0; i < 100; ++i) {
                    final int x = MathHelper.floor_double(this.posX);
                    final int y = MathHelper.floor_double(this.posY);
                    final int z = MathHelper.floor_double(this.posZ);
                    final Block block = this.worldObj.getBlock(x, y + i, z);
                    if (block.getMaterial().isReplaceable()) {
                        if (this.placeChest(x, y + i, z)) {
                            this.setDead();
                            return;
                        }
                        if (this.cargo != null) {
                            for (final ItemStack stack : this.cargo) {
                                final EntityItem e = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, stack);
                                this.worldObj.spawnEntityInWorld((Entity)e);
                            }
                            return;
                        }
                    }
                }
                if (this.cargo != null) {
                    for (final ItemStack stack2 : this.cargo) {
                        final EntityItem e2 = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, stack2);
                        this.worldObj.spawnEntityInWorld((Entity)e2);
                    }
                }
            }
            else {
                this.motionY = -0.25;
            }
            this.moveEntity(0.0, this.motionY, 0.0);
        }
    }
    
    private boolean placeChest(final int x, final int y, final int z) {
        this.worldObj.setBlock(x, y, z, GCBlocks.parachest, 0, 3);
        final TileEntity te = this.worldObj.getTileEntity(x, y, z);
        if (te instanceof TileEntityParaChest && this.cargo != null) {
            final TileEntityParaChest chest = (TileEntityParaChest)te;
            chest.chestContents = new ItemStack[this.cargo.length + 1];
            for (int i = 0; i < this.cargo.length; ++i) {
                chest.chestContents[i] = this.cargo[i];
            }
            chest.fuelTank.fill(FluidRegistry.getFluidStack(GalacticraftCore.fluidFuel.getName().toLowerCase(), this.fuelLevel), true);
            return true;
        }
        return this.placedChest = true;
    }
}
