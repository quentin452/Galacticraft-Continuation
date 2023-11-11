package micdoodle8.mods.galacticraft.planets.mars.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class ModelSlimeling extends ModelBase
{
    ModelRenderer tail3;
    ModelRenderer tail2;
    ModelRenderer tail1;
    ModelRenderer bodyMain;
    ModelRenderer neck;
    ModelRenderer head;
    
    public ModelSlimeling(final float scale) {
        this.textureWidth = 256;
        this.textureHeight = 128;
        (this.head = new ModelRenderer((ModelBase)this, 196, 25)).addBox(-4.5f, -15.7f, 0.5f, 9, 9, 9);
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.setTextureSize(256, 128);
        this.head.mirror = true;
        this.setRotation(this.head, 0.0f, 0.0f, 0.0f);
        (this.tail3 = new ModelRenderer((ModelBase)this, 0, 25)).addBox(-3.5f, 1.0f, -17.0f, 7, 5, 7);
        this.tail3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tail3.setTextureSize(256, 128);
        this.tail3.mirror = true;
        this.setRotation(this.tail3, 0.0f, 0.0f, 0.0f);
        (this.tail2 = new ModelRenderer((ModelBase)this, 28, 25)).addBox(-4.5f, -1.0f, -15.0f, 9, 7, 9);
        this.tail2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tail2.setTextureSize(256, 128);
        this.tail2.mirror = true;
        this.setRotation(this.tail2, 0.0f, 0.0f, 0.0f);
        (this.tail1 = new ModelRenderer((ModelBase)this, 64, 25)).addBox(-5.5f, -3.0f, -11.0f, 11, 9, 10);
        this.tail1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tail1.setTextureSize(256, 128);
        this.tail1.mirror = true;
        this.setRotation(this.tail1, 0.0f, 0.0f, 0.0f);
        (this.bodyMain = new ModelRenderer((ModelBase)this, 106, 25)).addBox(-6.0f, -6.0f, -6.0f, 12, 12, 12);
        this.bodyMain.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bodyMain.setTextureSize(256, 128);
        this.bodyMain.mirror = true;
        this.setRotation(this.bodyMain, 0.0f, 0.0f, 0.0f);
        (this.neck = new ModelRenderer((ModelBase)this, 154, 25)).addBox(-5.5f, -10.5f, -3.0f, 11, 11, 10);
        this.neck.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.neck.setTextureSize(256, 128);
        this.neck.mirror = true;
        this.setRotation(this.neck, 0.0f, 0.0f, 0.0f);
        if (scale > 0.0f) {
            (this.head = new ModelRenderer((ModelBase)this, 156, 0)).addBox(-3.5f, -14.7f, 1.5f, 7, 7, 7);
            this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.head.setTextureSize(256, 128);
            this.head.mirror = true;
            this.setRotation(this.head, 0.0f, 0.0f, 0.0f);
            (this.neck = new ModelRenderer((ModelBase)this, 122, 0)).addBox(-4.5f, -9.5f, -2.0f, 9, 9, 8);
            this.neck.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.neck.setTextureSize(256, 128);
            this.neck.mirror = true;
            this.setRotation(this.neck, 0.0f, 0.0f, 0.0f);
            (this.bodyMain = new ModelRenderer((ModelBase)this, 82, 0)).addBox(-5.0f, -5.0f, -5.0f, 10, 10, 10);
            this.bodyMain.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bodyMain.setTextureSize(256, 128);
            this.bodyMain.mirror = true;
            this.setRotation(this.bodyMain, 0.0f, 0.0f, 0.0f);
            (this.tail1 = new ModelRenderer((ModelBase)this, 48, 0)).addBox(-4.5f, -2.0f, -10.0f, 9, 7, 8);
            this.tail1.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.tail1.setTextureSize(256, 128);
            this.tail1.mirror = true;
            this.setRotation(this.tail1, 0.0f, 0.0f, 0.0f);
            (this.tail2 = new ModelRenderer((ModelBase)this, 20, 0)).addBox(-3.5f, 0.0f, -14.0f, 7, 5, 7);
            this.tail2.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.tail2.setTextureSize(256, 128);
            this.tail2.mirror = true;
            this.setRotation(this.tail2, 0.0f, 0.0f, 0.0f);
            (this.tail3 = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-2.5f, 2.0f, -16.0f, 5, 3, 5);
            this.tail3.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.tail3.setTextureSize(256, 128);
            this.tail3.mirror = true;
            this.setRotation(this.tail3, 0.0f, 0.0f, 0.0f);
        }
        this.bodyMain.addChild(this.tail1);
        this.neck.addChild(this.head);
        this.tail1.addChild(this.tail2);
        this.tail2.addChild(this.tail3);
        this.bodyMain.addChild(this.neck);
    }
    
    public void render(final Entity entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        super.render(entity, par2, par3, par4, par5, par6, par7);
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        this.bodyMain.render(par7);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity par7Entity) {
        this.tail1.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2;
        this.tail2.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2;
        this.tail3.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2;
        this.tail1.offsetZ = MathHelper.cos(0.5f * par1 * 0.6662f) * 0.2f * par2;
        this.tail2.offsetZ = MathHelper.cos(0.5f * par1 * 0.6662f) * 0.2f * par2;
        this.tail3.offsetZ = MathHelper.cos(0.5f * par1 * 0.6662f) * 0.2f * par2;
        this.neck.offsetZ = -MathHelper.cos(0.5f * par1 * 0.6662f) * 0.1f * par2;
        this.head.offsetZ = -MathHelper.cos(0.5f * par1 * 0.6662f) * 0.1f * par2;
    }
}
