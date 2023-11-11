package micdoodle8.mods.galacticraft.core.client.model;

import org.lwjgl.opengl.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.model.*;

import java.util.ArrayList;
import java.util.List;

public class ModelRendererGC extends ModelRenderer
{
    private boolean compiled;
    private int displayList;

    public ModelRendererGC(final ModelBase par1ModelBase, final int par2, final int par3) {
        super(par1ModelBase, par2, par3);
    }

    @SideOnly(Side.CLIENT)
    public void render(final float par1) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(par1);
            }
            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0f && this.rotateAngleY == 0.0f && this.rotateAngleZ == 0.0f) {
                if (this.rotationPointX == 0.0f && this.rotationPointY == 0.0f && this.rotationPointZ == 0.0f) {
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for (Object childModelObj : this.childModels) {
                            if (childModelObj instanceof ModelRendererGC) {
                                ModelRendererGC childModel = (ModelRendererGC) childModelObj;
                                childModel.render(par1);
                            }
                        }
                    }

                }
                else {
                    GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for (Object childModelObj : this.childModels) {
                            if (childModelObj instanceof ModelRendererGC) {
                                ModelRendererGC childModel = (ModelRendererGC) childModelObj;
                                childModel.render(par1);
                            }
                        }
                    }
                    GL11.glTranslatef(-this.rotationPointX * par1, -this.rotationPointY * par1, -this.rotationPointZ * par1);
                }
            }
            else {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                if (this.rotateAngleY != 0.0f) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (this.rotateAngleX != 0.0f) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                if (this.rotateAngleZ != 0.0f) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                GL11.glCallList(this.displayList);
                if (this.childModels != null) {
                    for (Object childModelObj : this.childModels) {
                        if (childModelObj instanceof ModelRendererGC) {
                            ModelRendererGC childModel = (ModelRendererGC) childModelObj;
                            childModel.render(par1);
                        }
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }
    }

    @SideOnly(Side.CLIENT)
    private void compileDisplayList(final float par1) {
        GL11.glNewList(this.displayList = GLAllocation.generateDisplayLists(1), 4864);
        for (Object o : this.cubeList) {
            if (o instanceof ModelRendererGC) {
                ModelRendererGC modelRenderer = (ModelRendererGC) o;
                modelRenderer.render(par1);
            }
        }
        GL11.glEndList();
        this.compiled = true;
    }
}
