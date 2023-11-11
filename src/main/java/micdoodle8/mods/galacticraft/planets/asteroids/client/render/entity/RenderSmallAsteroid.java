package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;

public class RenderSmallAsteroid extends Render
{
    private RenderBlocks blockRenderer;
    
    public RenderSmallAsteroid() {
        this.blockRenderer = new RenderBlocks();
    }
    
    public void doRender(final Entity entity, final double x, final double y, final double z, final float f, final float partialTickTime) {
        GL11.glDisable(32826);
        final EntitySmallAsteroid asteroid = (EntitySmallAsteroid)entity;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y + 0.5f, (float)z);
        GL11.glRotatef(asteroid.rotationPitch, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(asteroid.rotationYaw, 0.0f, 1.0f, 0.0f);
        this.bindEntityTexture((Entity)asteroid);
        this.blockRenderer.renderBlockAsItem(AsteroidBlocks.blockBasic, 0, 1.0f);
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.locationBlocksTexture;
    }
}
