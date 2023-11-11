package micdoodle8.mods.galacticraft.core.network;

import io.netty.channel.*;
import io.netty.buffer.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.*;

public class PacketControllableEntity implements IPacket
{
    private int keyPressed;
    
    public PacketControllableEntity() {
    }
    
    public PacketControllableEntity(final int keyPressed) {
        this.keyPressed = keyPressed;
    }
    
    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.keyPressed);
    }
    
    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        this.keyPressed = buffer.readInt();
    }
    
    public void handleClientSide(final EntityPlayer player) {
        this.handleKeyPress(player);
    }
    
    public void handleServerSide(final EntityPlayer player) {
        this.handleKeyPress(player);
    }
    
    private void handleKeyPress(final EntityPlayer player) {
        if (player.ridingEntity != null && player.ridingEntity instanceof IControllableEntity) {
            ((IControllableEntity)player.ridingEntity).pressKey(this.keyPressed);
        }
    }
}
