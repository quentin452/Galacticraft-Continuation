package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelMeteor extends ModelBase
{
    ModelRenderer[] shapes;
    
    public ModelMeteor() {
        this.shapes = new ModelRenderer[13];
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.shapes[0] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(0.0f, -7.0f, -13.0f, 2, 4, 4);
        this.shapes[0].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[0].setTextureSize(128, 64);
        this.shapes[0].mirror = true;
        this.setRotation(this.shapes[0], 0.0f, 0.0f, 0.0f);
        (this.shapes[1] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-10.0f, -10.0f, -10.0f, 20, 20, 20);
        this.shapes[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[1].setTextureSize(128, 64);
        this.shapes[1].mirror = true;
        this.setRotation(this.shapes[1], 0.0f, 0.0f, 0.0f);
        (this.shapes[2] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-5.0f, -8.0f, -12.0f, 5, 9, 1);
        this.shapes[2].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[2].setTextureSize(128, 64);
        this.shapes[2].mirror = true;
        this.setRotation(this.shapes[2], 0.0f, 0.0f, 0.0f);
        (this.shapes[3] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(0.0f, -6.0f, 11.0f, 4, 13, 1);
        this.shapes[3].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[3].setTextureSize(128, 64);
        this.shapes[3].mirror = true;
        this.setRotation(this.shapes[3], 0.0f, 0.0f, 0.0f);
        (this.shapes[4] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-9.0f, 10.0f, -9.0f, 18, 1, 18);
        this.shapes[4].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[4].setTextureSize(128, 64);
        this.shapes[4].mirror = true;
        this.setRotation(this.shapes[4], 0.0f, 0.0f, 0.0f);
        (this.shapes[5] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(11.0f, 3.0f, -8.0f, 1, 5, 5);
        this.shapes[5].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[5].setTextureSize(128, 64);
        this.shapes[5].mirror = true;
        this.setRotation(this.shapes[5], 0.0f, 0.0f, 0.0f);
        (this.shapes[6] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-7.0f, -8.0f, 10.0f, 7, 12, 2);
        this.shapes[6].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[6].setTextureSize(128, 64);
        this.shapes[6].mirror = true;
        this.setRotation(this.shapes[6], 0.0f, 0.0f, 0.0f);
        (this.shapes[7] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-9.0f, -9.0f, 10.0f, 18, 18, 1);
        this.shapes[7].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[7].setTextureSize(128, 64);
        this.shapes[7].mirror = true;
        this.setRotation(this.shapes[7], 0.0f, 0.0f, 0.0f);
        (this.shapes[8] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-11.0f, -9.0f, -9.0f, 1, 18, 18);
        this.shapes[8].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[8].setTextureSize(128, 64);
        this.shapes[8].mirror = true;
        this.setRotation(this.shapes[8], 0.0f, 0.0f, 0.0f);
        (this.shapes[9] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(10.0f, -9.0f, -9.0f, 1, 18, 18);
        this.shapes[9].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[9].setTextureSize(128, 64);
        this.shapes[9].mirror = true;
        this.setRotation(this.shapes[9], 0.0f, 0.0f, 0.0f);
        (this.shapes[10] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-9.0f, -9.0f, -11.0f, 18, 18, 1);
        this.shapes[10].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[10].setTextureSize(128, 64);
        this.shapes[10].mirror = true;
        this.setRotation(this.shapes[10], 0.0f, 0.0f, 0.0f);
        (this.shapes[11] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-9.0f, -9.0f, -11.0f, 18, 18, 1);
        this.shapes[11].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[11].setTextureSize(128, 64);
        this.shapes[11].mirror = true;
        this.setRotation(this.shapes[11], 0.0f, 0.0f, 0.0f);
        (this.shapes[12] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-9.0f, -11.0f, -9.0f, 18, 1, 18);
        this.shapes[12].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.shapes[12].setTextureSize(128, 64);
        this.shapes[12].mirror = true;
        this.setRotation(this.shapes[12], 0.0f, 0.0f, 0.0f);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        for (final ModelRenderer shape : this.shapes) {
            shape.render(f5);
        }
    }
    
    public void renderBlock(final float f) {
        for (final ModelRenderer shape : this.shapes) {
            shape.render(f);
        }
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
