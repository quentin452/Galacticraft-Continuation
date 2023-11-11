package micdoodle8.mods.galacticraft.core.network;

import net.minecraft.entity.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import net.minecraft.entity.player.*;

public class PacketRotateRocket implements IPacket
{
    private int entityID;
    private float entityPitch;
    private float entityYaw;
    
    public PacketRotateRocket() {
    }
    
    public PacketRotateRocket(final Entity rotateableEntity) {
        this.entityID = rotateableEntity.getEntityId();
        this.entityPitch = rotateableEntity.rotationPitch;
        this.entityYaw = rotateableEntity.rotationYaw;
    }
    
    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.entityID);
        buffer.writeFloat(this.entityPitch);
        buffer.writeFloat(this.entityYaw);
    }
    
    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        this.entityID = buffer.readInt();
        this.entityPitch = buffer.readFloat();
        this.entityYaw = buffer.readFloat();
    }
    
    public void handleClientSide(final EntityPlayer player) {
    }
    
    public void handleServerSide(final EntityPlayer player) {
        final Entity entity = player.worldObj.getEntityByID(this.entityID);
        if (entity != null) {
            entity.rotationPitch = this.entityPitch;
            entity.rotationYaw = this.entityYaw;
        }
    }
}
