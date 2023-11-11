package micdoodle8.mods.galacticraft.core.network;

import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import java.io.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.player.*;

public class PacketDynamicInventory implements IPacket
{
    private int type;
    private Object[] data;
    private ItemStack[] stacks;
    
    public PacketDynamicInventory() {
    }
    
    public PacketDynamicInventory(final Entity entity) {
        assert entity instanceof IInventory : "Entity does not implement " + IInventory.class.getSimpleName();
        this.type = 0;
        this.data = new Object[] { entity.getEntityId() };
        this.stacks = new ItemStack[((IInventory)entity).getSizeInventory()];
        for (int i = 0; i < this.stacks.length; ++i) {
            this.stacks[i] = ((IInventory)entity).getStackInSlot(i);
        }
    }
    
    public PacketDynamicInventory(final TileEntity chest) {
        assert chest instanceof IInventory : "Tile does not implement " + IInventory.class.getSimpleName();
        this.type = 1;
        this.data = new Object[] { chest.xCoord, chest.yCoord, chest.zCoord };
        this.stacks = new ItemStack[((IInventory)chest).getSizeInventory()];
        for (int i = 0; i < this.stacks.length; ++i) {
            this.stacks[i] = ((IInventory)chest).getStackInSlot(i);
        }
    }
    
    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.type);
        switch (this.type) {
            case 0: {
                buffer.writeInt((int)this.data[0]);
                break;
            }
            case 1: {
                buffer.writeInt((int)this.data[0]);
                buffer.writeInt((int)this.data[1]);
                buffer.writeInt((int)this.data[2]);
                break;
            }
        }
        buffer.writeInt(this.stacks.length);
        for (int i = 0; i < this.stacks.length; ++i) {
            try {
                NetworkUtil.writeItemStack(this.stacks[i], buffer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        switch (this.type = buffer.readInt()) {
            case 0: {
                (this.data = new Object[1])[0] = buffer.readInt();
                break;
            }
            case 1: {
                (this.data = new Object[3])[0] = buffer.readInt();
                this.data[1] = buffer.readInt();
                this.data[2] = buffer.readInt();
                break;
            }
        }
        this.stacks = new ItemStack[buffer.readInt()];
        for (int i = 0; i < this.stacks.length; ++i) {
            try {
                this.stacks[i] = NetworkUtil.readItemStack(buffer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void handleClientSide(final EntityPlayer player) {
        if (player.worldObj == null) {
            return;
        }
        switch (this.type) {
            case 0: {
                final Entity entity = player.worldObj.getEntityByID((int)this.data[0]);
                if (entity instanceof IInventorySettable) {
                    this.setInventoryStacks((IInventorySettable)entity);
                    break;
                }
                break;
            }
            case 1: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data[0], (int)this.data[1], (int)this.data[2]);
                if (tile instanceof IInventorySettable) {
                    this.setInventoryStacks((IInventorySettable)tile);
                    break;
                }
                break;
            }
        }
    }
    
    public void handleServerSide(final EntityPlayer player) {
        switch (this.type) {
            case 0: {
                final Entity entity = player.worldObj.getEntityByID((int)this.data[0]);
                if (entity instanceof IInventorySettable) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketDynamicInventory(entity), (EntityPlayerMP)player);
                    break;
                }
                break;
            }
            case 1: {
                final TileEntity tile = player.worldObj.getTileEntity((int)this.data[0], (int)this.data[1], (int)this.data[2]);
                if (tile instanceof IInventorySettable) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketDynamicInventory(tile), (EntityPlayerMP)player);
                    break;
                }
                break;
            }
        }
    }
    
    private void setInventoryStacks(final IInventorySettable inv) {
        inv.setSizeInventory(this.stacks.length);
        for (int i = 0; i < this.stacks.length; ++i) {
            inv.setInventorySlotContents(i, this.stacks[i]);
        }
    }
}
