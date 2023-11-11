package micdoodle8.mods.galacticraft.core.client.model.block;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelSolarPanel extends ModelBase
{
    ModelRenderer panelMain;
    ModelRenderer sideHorizontal0;
    ModelRenderer sideVertical0;
    ModelRenderer sideVertical2;
    ModelRenderer sideVertical1;
    ModelRenderer sideHorizontal1;
    ModelRenderer sideHorizontal3;
    ModelRenderer sideHorizontal2;
    ModelRenderer pole;
    
    public ModelSolarPanel() {
        this(0.0f);
    }
    
    public ModelSolarPanel(final float var1) {
        this.textureWidth = 256;
        this.textureHeight = 128;
        (this.panelMain = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-23.0f, -0.5f, -23.0f, 46, 1, 46);
        this.panelMain.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.panelMain.setTextureSize(256, 128);
        this.panelMain.mirror = true;
        this.setRotation(this.panelMain, 0.0f, 0.0f, 0.0f);
        (this.sideHorizontal0 = new ModelRenderer((ModelBase)this, 0, 48)).addBox(-24.0f, -1.111f, -23.0f, 1, 1, 46);
        this.sideHorizontal0.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideHorizontal0.setTextureSize(256, 128);
        this.sideHorizontal0.mirror = true;
        this.setRotation(this.sideHorizontal0, 0.0f, 0.0f, 0.0f);
        (this.sideVertical0 = new ModelRenderer((ModelBase)this, 94, 48)).addBox(-24.0f, -1.1f, 23.0f, 48, 1, 1);
        this.sideVertical0.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideVertical0.setTextureSize(256, 128);
        this.sideVertical0.mirror = true;
        this.setRotation(this.sideVertical0, 0.0f, 0.0f, 0.0f);
        (this.sideVertical2 = new ModelRenderer((ModelBase)this, 94, 48)).addBox(-24.0f, -1.1f, -24.0f, 48, 1, 1);
        this.sideVertical2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideVertical2.setTextureSize(256, 128);
        this.sideVertical2.mirror = true;
        this.setRotation(this.sideVertical2, 0.0f, 0.0f, 0.0f);
        (this.sideVertical1 = new ModelRenderer((ModelBase)this, 94, 48)).addBox(-24.0f, -1.1f, -0.5f, 48, 1, 1);
        this.sideVertical1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideVertical1.setTextureSize(256, 128);
        this.sideVertical1.mirror = true;
        this.setRotation(this.sideVertical1, 0.0f, 0.0f, 0.0f);
        (this.sideHorizontal1 = new ModelRenderer((ModelBase)this, 0, 48)).addBox(-9.0f, -1.111f, -23.0f, 1, 1, 46);
        this.sideHorizontal1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideHorizontal1.setTextureSize(256, 128);
        this.sideHorizontal1.mirror = true;
        this.setRotation(this.sideHorizontal1, 0.0f, 0.0f, 0.0f);
        (this.sideHorizontal3 = new ModelRenderer((ModelBase)this, 0, 48)).addBox(23.0f, -1.111f, -23.0f, 1, 1, 46);
        this.sideHorizontal3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideHorizontal3.setTextureSize(256, 128);
        this.sideHorizontal3.mirror = true;
        this.setRotation(this.sideHorizontal3, 0.0f, 0.0f, 0.0f);
        (this.sideHorizontal2 = new ModelRenderer((ModelBase)this, 0, 48)).addBox(8.0f, -1.111f, -23.0f, 1, 1, 46);
        this.sideHorizontal2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.sideHorizontal2.setTextureSize(256, 128);
        this.sideHorizontal2.mirror = true;
        this.setRotation(this.sideHorizontal2, 0.0f, 0.0f, 0.0f);
        (this.pole = new ModelRenderer((ModelBase)this, 94, 50)).addBox(-1.5f, 0.0f, -1.5f, 3, 24, 3);
        this.pole.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.pole.setTextureSize(256, 128);
        this.pole.mirror = true;
        this.setRotation(this.pole, 0.0f, 0.0f, 0.0f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void renderPanel() {
        this.panelMain.render(0.0625f);
        this.sideHorizontal0.render(0.0625f);
        this.sideVertical0.render(0.0625f);
        this.sideVertical2.render(0.0625f);
        this.sideVertical1.render(0.0625f);
        this.sideHorizontal1.render(0.0625f);
        this.sideHorizontal3.render(0.0625f);
        this.sideHorizontal2.render(0.0625f);
    }
    
    public void renderPole() {
        this.pole.render(0.0625f);
    }
    
    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5, final Entity e) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
    }
}
