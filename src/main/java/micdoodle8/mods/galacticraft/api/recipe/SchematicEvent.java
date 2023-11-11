package micdoodle8.mods.galacticraft.api.recipe;

import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.player.*;

public abstract class SchematicEvent extends Event
{
    public ISchematicPage page;
    
    public SchematicEvent(final ISchematicPage page) {
        this.page = page;
    }
    
    public static class Unlock extends SchematicEvent
    {
        public EntityPlayerMP player;
        
        public Unlock(final EntityPlayerMP player, final ISchematicPage page) {
            super(page);
            this.player = player;
        }
    }
    
    public static class FlipPage extends SchematicEvent
    {
        public int index;
        public int direction;
        
        public FlipPage(final ISchematicPage page, final int index, final int direction) {
            super(page);
            this.index = index;
            this.direction = direction;
        }
    }
}
