package micdoodle8.mods.galacticraft.planets.mars.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class ModelSludgeling extends ModelBase
{
    ModelRenderer tail4;
    ModelRenderer body;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;
    
    public ModelSludgeling() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.tail4 = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, 0.3f, 4.5f, 1, 1, 1);
        this.tail4.setRotationPoint(0.0f, 23.5f, -2.0f);
        this.tail4.setTextureSize(64, 32);
        this.tail4.mirror = true;
        this.setRotation(this.tail4, 0.0f, 0.0f, 0.0f);
        (this.body = new ModelRenderer((ModelBase)this, 4, 0)).addBox(-1.0f, -0.5f, -1.5f, 2, 1, 3);
        this.body.setRotationPoint(0.0f, 23.5f, -2.0f);
        this.body.setTextureSize(64, 32);
        this.body.mirror = true;
        this.setRotation(this.body, 0.0f, 0.0f, 0.0f);
        (this.tail1 = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.3f, 1.5f, 1, 1, 1);
        this.tail1.setRotationPoint(0.0f, 23.5f, -2.0f);
        this.tail1.setTextureSize(64, 32);
        this.tail1.mirror = true;
        this.setRotation(this.tail1, 0.0f, 0.0f, 0.0f);
        (this.tail2 = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.1f, 2.5f, 1, 1, 1);
        this.tail2.setRotationPoint(0.0f, 23.5f, -2.0f);
        this.tail2.setTextureSize(64, 32);
        this.tail2.mirror = true;
        this.setRotation(this.tail2, 0.0f, 0.0f, 0.0f);
        (this.tail3 = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, 0.1f, 3.5f, 1, 1, 1);
        this.tail3.setRotationPoint(0.0f, 23.5f, -2.0f);
        this.tail3.setTextureSize(64, 32);
        this.tail3.mirror = true;
        this.setRotation(this.tail3, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.body.render(f5);
        this.tail1.render(f5);
        this.tail2.render(f5);
        this.tail3.render(f5);
        this.tail4.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5, final Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.tail1.rotateAngleY = MathHelper.cos(f2 * 0.3f + 0.0f) * 3.1415927f * 0.025f * (1 + Math.abs(-2));
        this.tail2.rotateAngleY = MathHelper.cos(f2 * 0.3f + 0.47123894f) * 3.1415927f * 0.025f * (1 + Math.abs(-1));
        this.tail3.rotateAngleY = MathHelper.cos(f2 * 0.3f + 0.9424779f) * 3.1415927f * 0.025f * (1 + Math.abs(-1));
        this.tail4.rotateAngleY = MathHelper.cos(f2 * 0.3f + 1.4137168f) * 3.1415927f * 0.025f * (1 + Math.abs(-1));
    }
}
