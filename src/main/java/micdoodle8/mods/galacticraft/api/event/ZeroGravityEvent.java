package micdoodle8.mods.galacticraft.api.event;

import net.minecraftforge.event.entity.living.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import cpw.mods.fml.common.eventhandler.*;

public abstract class ZeroGravityEvent extends LivingEvent
{
    public final WorldProvider provider;
    
    public ZeroGravityEvent(final EntityLivingBase entity) {
        super(entity);
        this.provider = entity.worldObj.provider;
    }
    
    @Cancelable
    public static class InFreefall extends ZeroGravityEvent
    {
        public InFreefall(final EntityLivingBase entity) {
            super(entity);
        }
    }
    
    @Cancelable
    public static class Motion extends ZeroGravityEvent
    {
        public Motion(final EntityLivingBase entity) {
            super(entity);
        }
    }
    
    @Cancelable
    public static class SneakOverride extends ZeroGravityEvent
    {
        public SneakOverride(final EntityLivingBase entity) {
            super(entity);
        }
    }
}
