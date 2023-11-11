package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.api.vector.*;

public class ModelFlag extends ModelBase
{
    ModelRenderer base;
    ModelRenderer pole;
    
    public ModelFlag() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.base = new ModelRenderer((ModelBase)this, 4, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 1, 3);
        this.base.setRotationPoint(0.0f, 23.0f, 0.0f);
        this.base.setTextureSize(128, 64);
        this.base.mirror = true;
        this.setRotation(this.base, 0.0f, 0.0f, 0.0f);
        (this.pole = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -40.0f, -0.5f, 1, 40, 1);
        this.pole.setRotationPoint(0.0f, 23.0f, 0.0f);
        this.pole.setTextureSize(128, 64);
        this.pole.mirror = true;
        this.setRotation(this.pole, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        if (entity instanceof EntityFlag) {
            final EntityFlag flag = (EntityFlag)entity;
            this.renderPole(flag, f5);
            this.renderFlag(flag, (float)flag.ticksExisted);
        }
    }
    
    public void renderPole(final Entity entity, final float f5) {
        this.base.render(f5);
        this.pole.render(f5);
    }
    
    public void renderFlag(final EntityFlag entity, final float ticks) {
        if (entity.flagData != null) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            GL11.glTranslatef(0.0f, -1.1f, 0.0f);
            GL11.glDisable(3553);
            GL11.glDisable(2884);
            float windLevel = 1.0f;
            if (entity.worldObj.provider instanceof IGalacticraftWorldProvider) {
                windLevel = ((IGalacticraftWorldProvider)entity.worldObj.provider).getWindLevel();
            }
            for (int i = 0; i < entity.flagData.getWidth(); ++i) {
                for (int j = 0; j < entity.flagData.getHeight(); ++j) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(0.0f, -1.0f, 0.0f);
                    float offset = 0.0f;
                    float offsetAhead = 0.0f;
                    if (windLevel > 0.0f) {
                        offset = (float)(Math.sin(ticks / 2.0f + i * 50 + 3.0f) / 25.0) * i / 30.0f;
                        offsetAhead = (float)(Math.sin(ticks / 2.0f + (i + 1) * 50 + 3.0f) / 25.0) * (i + 1) / 30.0f;
                        offset *= windLevel;
                        offsetAhead *= windLevel;
                    }
                    final Vector3 col = entity.flagData.getColorAt(i, j);
                    GL11.glColor3f(col.floatX(), col.floatY(), col.floatZ());
                    final Tessellator tess = Tessellator.instance;
                    tess.startDrawing(4);
                    tess.addVertex(i / 24.0f + 0.0, j / 24.0f + 0.0 + offset, (double)offset);
                    tess.addVertex(i / 24.0f + 0.0, j / 24.0f + 0.041666666666666664 + offset, (double)offset);
                    tess.addVertex(i / 24.0f + 0.041666666666666664, j / 24.0f + 0.041666666666666664 + offsetAhead, (double)offsetAhead);
                    tess.addVertex(i / 24.0f + 0.0, j / 24.0f + 0.0 + offset, (double)offset);
                    tess.addVertex(i / 24.0f + 0.041666666666666664, j / 24.0f + 0.041666666666666664 + offsetAhead, (double)offsetAhead);
                    tess.addVertex(i / 24.0f + 0.041666666666666664, j / 24.0f + 0.0 + offsetAhead, (double)offsetAhead);
                    tess.draw();
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
            }
            GL11.glEnable(3553);
            GL11.glEnable(2884);
            GL11.glPopMatrix();
        }
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
