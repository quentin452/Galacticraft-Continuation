package micdoodle8.mods.galacticraft.core.network;

import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import net.minecraft.entity.player.*;
import java.util.*;

public class PacketEntityUpdate implements IPacket
{
    private int entityID;
    private Vector3 position;
    private float rotationYaw;
    private float rotationPitch;
    private Vector3 motion;
    private boolean onGround;
    
    public PacketEntityUpdate() {
    }
    
    public PacketEntityUpdate(final int entityID, final Vector3 position, final Vector2 rotation, final Vector3 motion, final boolean onGround) {
        this.entityID = entityID;
        this.position = position;
        this.rotationYaw = (float)rotation.x;
        this.rotationPitch = (float)rotation.y;
        this.motion = motion;
        this.onGround = onGround;
    }
    
    public PacketEntityUpdate(final Entity entity) {
        this(entity.getEntityId(), new Vector3(entity.posX, entity.posY, entity.posZ), new Vector2((double)entity.rotationYaw, (double)entity.rotationPitch), new Vector3(entity.motionX, entity.motionY, entity.motionZ), entity.onGround);
    }
    
    public void encodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        buffer.writeInt(this.entityID);
        buffer.writeDouble(this.position.x);
        buffer.writeDouble(this.position.y);
        buffer.writeDouble(this.position.z);
        buffer.writeFloat(this.rotationYaw);
        buffer.writeFloat(this.rotationPitch);
        buffer.writeDouble(this.motion.x);
        buffer.writeDouble(this.motion.y);
        buffer.writeDouble(this.motion.z);
        buffer.writeBoolean(this.onGround);
    }
    
    public void decodeInto(final ChannelHandlerContext context, final ByteBuf buffer) {
        this.entityID = buffer.readInt();
        this.position = new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.rotationYaw = buffer.readFloat();
        this.rotationPitch = buffer.readFloat();
        this.motion = new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.onGround = buffer.readBoolean();
    }
    
    public void handleClientSide(final EntityPlayer player) {
        this.setEntityData(player);
    }
    
    public void handleServerSide(final EntityPlayer player) {
        this.setEntityData(player);
    }
    
    private void setEntityData(final EntityPlayer player) {
        final Entity entity = player.worldObj.getEntityByID(this.entityID);
        if (entity instanceof IEntityFullSync && (player.worldObj.isRemote || player.getUniqueID().equals(((IEntityFullSync)entity).getOwnerUUID()) || ((IEntityFullSync)entity).getOwnerUUID() == null)) {
            final IEntityFullSync controllable = (IEntityFullSync)entity;
            controllable.setPositionRotationAndMotion(this.position.x, this.position.y, this.position.z, this.rotationYaw, this.rotationPitch, this.motion.x, this.motion.y, this.motion.z, this.onGround);
        }
    }
    
    public interface IEntityFullSync
    {
        void setPositionRotationAndMotion(final double p0, final double p1, final double p2, final float p3, final float p4, final double p5, final double p6, final double p7, final boolean p8);
        
        UUID getOwnerUUID();
    }
}
