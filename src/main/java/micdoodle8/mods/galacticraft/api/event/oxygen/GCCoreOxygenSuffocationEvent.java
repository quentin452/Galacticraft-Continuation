package micdoodle8.mods.galacticraft.api.event.oxygen;

import net.minecraftforge.event.entity.living.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import cpw.mods.fml.common.eventhandler.*;

public abstract class GCCoreOxygenSuffocationEvent extends LivingEvent
{
    public final WorldProvider provider;
    
    public GCCoreOxygenSuffocationEvent(final EntityLivingBase entity) {
        super(entity);
        this.provider = entity.worldObj.provider;
    }
    
    @Cancelable
    public static class Pre extends GCCoreOxygenSuffocationEvent
    {
        public Pre(final EntityLivingBase entity) {
            super(entity);
        }
    }
    
    public static class Post extends GCCoreOxygenSuffocationEvent
    {
        public Post(final EntityLivingBase entity) {
            super(entity);
        }
    }
}
