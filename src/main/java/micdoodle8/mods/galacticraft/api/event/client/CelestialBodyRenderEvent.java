package micdoodle8.mods.galacticraft.api.event.client;

import micdoodle8.mods.galacticraft.api.galaxies.*;
import cpw.mods.fml.common.eventhandler.*;
import org.lwjgl.util.vector.*;
import net.minecraft.util.*;

public abstract class CelestialBodyRenderEvent extends Event
{
    public final CelestialBody celestialBody;
    
    public CelestialBodyRenderEvent(final CelestialBody celestialBody) {
        this.celestialBody = celestialBody;
    }
    
    public static class CelestialRingRenderEvent extends CelestialBodyRenderEvent
    {
        public CelestialRingRenderEvent(final CelestialBody celestialBody) {
            super(celestialBody);
        }
        
        @Cancelable
        public static class Pre extends CelestialBodyRenderEvent
        {
            public final Vector3f parentOffset;
            
            public Pre(final CelestialBody celestialBody, final Vector3f parentOffset) {
                super(celestialBody);
                this.parentOffset = parentOffset;
            }
        }
        
        public static class Post extends CelestialBodyRenderEvent
        {
            public Post(final CelestialBody celestialBody) {
                super(celestialBody);
            }
        }
    }
    
    @Cancelable
    public static class Pre extends CelestialBodyRenderEvent
    {
        public ResourceLocation celestialBodyTexture;
        public int textureSize;
        
        public Pre(final CelestialBody celestialBody, final ResourceLocation celestialBodyTexture, final int textureSize) {
            super(celestialBody);
            this.celestialBodyTexture = celestialBodyTexture;
            this.textureSize = textureSize;
        }
    }
    
    public static class Post extends CelestialBodyRenderEvent
    {
        public Post(final CelestialBody celestialBody) {
            super(celestialBody);
        }
    }
}
