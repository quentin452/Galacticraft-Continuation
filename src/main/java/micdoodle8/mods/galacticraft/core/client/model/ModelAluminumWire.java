package micdoodle8.mods.galacticraft.core.client.model;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;

@SideOnly(Side.CLIENT)
public class ModelAluminumWire extends ModelBase
{
    ModelRenderer middle;
    ModelRenderer right;
    ModelRenderer left;
    ModelRenderer back;
    ModelRenderer front;
    ModelRenderer top;
    ModelRenderer bottom;
    
    public ModelAluminumWire() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.middle = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-1.0f, -1.0f, -1.0f, 4, 4, 4);
        this.middle.setRotationPoint(-1.0f, 15.0f, -1.0f);
        this.middle.setTextureSize(64, 32);
        this.middle.mirror = true;
        this.setRotation(this.middle, 0.0f, 0.0f, 0.0f);
        (this.right = new ModelRenderer((ModelBase)this, 21, 0)).addBox(0.0f, 0.0f, 0.0f, 6, 4, 4);
        this.right.setRotationPoint(2.0f, 14.0f, -2.0f);
        this.right.setTextureSize(64, 32);
        this.right.mirror = true;
        this.setRotation(this.right, 0.0f, 0.0f, 0.0f);
        (this.left = new ModelRenderer((ModelBase)this, 21, 0)).addBox(0.0f, 0.0f, 0.0f, 6, 4, 4);
        this.left.setRotationPoint(-8.0f, 14.0f, -2.0f);
        this.left.setTextureSize(64, 32);
        this.left.mirror = true;
        this.setRotation(this.left, 0.0f, 0.0f, 0.0f);
        (this.back = new ModelRenderer((ModelBase)this, 0, 11)).addBox(0.0f, 0.0f, 0.0f, 4, 4, 6);
        this.back.setRotationPoint(-2.0f, 14.0f, 2.0f);
        this.back.setTextureSize(64, 32);
        this.back.mirror = true;
        this.setRotation(this.back, 0.0f, 0.0f, 0.0f);
        (this.front = new ModelRenderer((ModelBase)this, 0, 11)).addBox(0.0f, 0.0f, 0.0f, 4, 4, 6);
        this.front.setRotationPoint(-2.0f, 14.0f, -8.0f);
        this.front.setTextureSize(64, 32);
        this.front.mirror = true;
        this.setRotation(this.front, 0.0f, 0.0f, 0.0f);
        (this.top = new ModelRenderer((ModelBase)this, 21, 11)).addBox(0.0f, 0.0f, 0.0f, 4, 6, 4);
        this.top.setRotationPoint(-2.0f, 8.0f, -2.0f);
        this.top.setTextureSize(64, 32);
        this.top.mirror = true;
        this.setRotation(this.top, 0.0f, 0.0f, 0.0f);
        (this.bottom = new ModelRenderer((ModelBase)this, 21, 11)).addBox(0.0f, 0.0f, 0.0f, 4, 6, 4);
        this.bottom.setRotationPoint(-2.0f, 18.0f, -2.0f);
        this.bottom.setTextureSize(64, 32);
        this.bottom.mirror = true;
        this.setRotation(this.bottom, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.renderMiddle();
        this.renderBottom();
        this.renderTop();
        this.renderLeft();
        this.renderRight();
        this.renderBack();
        this.renderFront();
    }
    
    public void renderMiddle() {
        this.middle.render(0.0625f);
    }
    
    public void renderBottom() {
        this.bottom.render(0.0625f);
    }
    
    public void renderTop() {
        this.top.render(0.0625f);
    }
    
    public void renderLeft() {
        this.left.render(0.0625f);
    }
    
    public void renderRight() {
        this.right.render(0.0625f);
    }
    
    public void renderBack() {
        this.back.render(0.0625f);
    }
    
    public void renderFront() {
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float x, final float y, final float z, final float f3, final float f4, final float f5, final Entity entity) {
        super.setRotationAngles(x, y, z, f3, f4, f5, entity);
    }
}
