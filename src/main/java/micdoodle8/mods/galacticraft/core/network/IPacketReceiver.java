package micdoodle8.mods.galacticraft.core.network;

import java.util.*;
import io.netty.buffer.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;

public interface IPacketReceiver
{
    void getNetworkedData(final ArrayList<Object> p0);
    
    void decodePacketdata(final ByteBuf p0);
    
    void handlePacketData(final Side p0, final EntityPlayer p1);
}
