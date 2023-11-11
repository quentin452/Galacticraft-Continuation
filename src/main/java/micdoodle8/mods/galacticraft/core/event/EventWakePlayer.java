package micdoodle8.mods.galacticraft.core.event;

import net.minecraftforge.event.entity.player.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.player.*;

@Cancelable
public class EventWakePlayer extends PlayerEvent
{
    public EntityPlayer.EnumStatus result;
    public final int x;
    public final int y;
    public final int z;
    public final boolean flag1;
    public final boolean flag2;
    public final boolean flag3;
    public final boolean bypassed;
    
    public EventWakePlayer(final EntityPlayer player, final int x, final int y, final int z, final boolean flag1, final boolean flag2, final boolean flag3, final boolean bypassed) {
        super(player);
        this.result = null;
        this.x = x;
        this.y = y;
        this.z = z;
        this.flag1 = flag1;
        this.flag2 = flag2;
        this.flag3 = flag3;
        this.bypassed = bypassed;
    }
}
