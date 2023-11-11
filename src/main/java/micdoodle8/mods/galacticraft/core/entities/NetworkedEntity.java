package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import cpw.mods.fml.common.network.*;

public abstract class NetworkedEntity extends Entity implements IPacketReceiver
{
    public NetworkedEntity(final World par1World) {
        super(par1World);
        if (par1World != null && par1World.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketDynamic(this));
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        final PacketDynamic packet = new PacketDynamic(this);
        if (this.networkedDataChanged()) {
            if (!this.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendToAllAround(packet, new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, this.getPacketRange()));
            }
            else {
                GalacticraftCore.packetPipeline.sendToServer(packet);
            }
        }
    }
    
    public abstract boolean networkedDataChanged();
    
    public abstract double getPacketRange();
}
