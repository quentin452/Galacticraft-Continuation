package micdoodle8.mods.galacticraft.core.client.model;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class ModelEvolvedCreeper extends ModelBase
{
    ModelRenderer leftOxygenTank;
    ModelRenderer rightOxygenTank;
    ModelRenderer tubeRight2;
    ModelRenderer tubeLeft1;
    ModelRenderer tubeRight3;
    ModelRenderer tubeRight4;
    ModelRenderer tubeRight5;
    ModelRenderer tubeLeft6;
    ModelRenderer tubeRight7;
    ModelRenderer tubeRight1;
    ModelRenderer tubeLeft2;
    ModelRenderer tubeLeft3;
    ModelRenderer tubeLeft4;
    ModelRenderer tubeLeft5;
    ModelRenderer tubeLeft7;
    ModelRenderer tubeRight6;
    ModelRenderer tubeLeft8;
    ModelRenderer oxygenMask;
    public ModelRenderer head;
    public ModelRenderer field_78133_b;
    public ModelRenderer body;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    
    public ModelEvolvedCreeper() {
        this(0.0f);
    }
    
    public ModelEvolvedCreeper(final float par1) {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.leftOxygenTank = new ModelRenderer((ModelBase)this, 40, 20)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, par1);
        this.leftOxygenTank.setRotationPoint(2.0f, 5.0f, 3.8f);
        this.leftOxygenTank.setTextureSize(128, 64);
        this.leftOxygenTank.mirror = true;
        this.setRotation(this.leftOxygenTank, 0.0f, 0.0f, 0.0f);
        (this.rightOxygenTank = new ModelRenderer((ModelBase)this, 40, 20)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, par1);
        this.rightOxygenTank.setRotationPoint(-2.0f, 5.0f, 3.8f);
        this.rightOxygenTank.setTextureSize(128, 64);
        this.rightOxygenTank.mirror = true;
        this.setRotation(this.rightOxygenTank, 0.0f, 0.0f, 0.0f);
        (this.tubeRight2 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight2.setRotationPoint(-2.0f, 5.0f, 6.8f);
        this.tubeRight2.setTextureSize(128, 64);
        this.tubeRight2.mirror = true;
        this.setRotation(this.tubeRight2, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft1 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft1.setRotationPoint(2.0f, 6.0f, 5.8f);
        this.tubeLeft1.setTextureSize(128, 64);
        this.tubeLeft1.mirror = true;
        this.setRotation(this.tubeLeft1, 0.0f, 0.0f, 0.0f);
        (this.tubeRight3 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight3.setRotationPoint(-2.0f, 4.0f, 6.8f);
        this.tubeRight3.setTextureSize(128, 64);
        this.tubeRight3.mirror = true;
        this.setRotation(this.tubeRight3, 0.0f, 0.0f, 0.0f);
        (this.tubeRight4 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight4.setRotationPoint(-2.0f, 3.0f, 6.8f);
        this.tubeRight4.setTextureSize(128, 64);
        this.tubeRight4.mirror = true;
        this.setRotation(this.tubeRight4, 0.0f, 0.0f, 0.0f);
        (this.tubeRight5 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight5.setRotationPoint(-2.0f, 2.0f, 6.8f);
        this.tubeRight5.setTextureSize(128, 64);
        this.tubeRight5.mirror = true;
        this.setRotation(this.tubeRight5, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft6 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft6.setRotationPoint(2.0f, 1.0f, 5.8f);
        this.tubeLeft6.setTextureSize(128, 64);
        this.tubeLeft6.mirror = true;
        this.setRotation(this.tubeLeft6, 0.0f, 0.0f, 0.0f);
        (this.tubeRight7 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight7.setRotationPoint(-2.0f, 0.0f, 4.8f);
        this.tubeRight7.setTextureSize(128, 64);
        this.tubeRight7.mirror = true;
        this.setRotation(this.tubeRight7, 0.0f, 0.0f, 0.0f);
        (this.tubeRight1 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight1.setRotationPoint(-2.0f, 6.0f, 5.8f);
        this.tubeRight1.setTextureSize(128, 64);
        this.tubeRight1.mirror = true;
        this.setRotation(this.tubeRight1, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft2 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft2.setRotationPoint(2.0f, 5.0f, 6.8f);
        this.tubeLeft2.setTextureSize(128, 64);
        this.tubeLeft2.mirror = true;
        this.setRotation(this.tubeLeft2, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft3 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft3.setRotationPoint(2.0f, 4.0f, 6.8f);
        this.tubeLeft3.setTextureSize(128, 64);
        this.tubeLeft3.mirror = true;
        this.setRotation(this.tubeLeft3, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft4 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft4.setRotationPoint(2.0f, 3.0f, 6.8f);
        this.tubeLeft4.setTextureSize(128, 64);
        this.tubeLeft4.mirror = true;
        this.setRotation(this.tubeLeft4, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft5 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft5.setRotationPoint(2.0f, 2.0f, 6.8f);
        this.tubeLeft5.setTextureSize(128, 64);
        this.tubeLeft5.mirror = true;
        this.setRotation(this.tubeLeft5, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft7 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeLeft7.setRotationPoint(2.0f, 0.0f, 4.8f);
        this.tubeLeft7.setTextureSize(128, 64);
        this.tubeLeft7.mirror = true;
        this.setRotation(this.tubeLeft7, 0.0f, 0.0f, 0.0f);
        (this.tubeRight6 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1);
        this.tubeRight6.setRotationPoint(-2.0f, 1.0f, 5.8f);
        this.tubeRight6.setTextureSize(128, 64);
        this.tubeRight6.mirror = true;
        this.setRotation(this.tubeRight6, 0.0f, 0.0f, 0.0f);
        (this.tubeLeft8 = new ModelRenderer((ModelBase)this, 40, 30)).addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, par1);
        this.tubeLeft8.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.tubeLeft8.setTextureSize(128, 64);
        this.tubeLeft8.mirror = true;
        this.setRotation(this.tubeLeft8, 0.0f, 0.0f, 0.0f);
        (this.oxygenMask = new ModelRenderer((ModelBase)this, 40, 0)).addBox(-5.0f, -9.0f, -5.0f, 10, 10, 10, par1);
        this.oxygenMask.setRotationPoint(0.0f, 4.0f, 0.0f);
        this.oxygenMask.setTextureSize(128, 64);
        this.oxygenMask.mirror = true;
        this.setRotation(this.oxygenMask, 0.0f, 0.0f, 0.0f);
        final byte var2 = 4;
        (this.head = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, par1);
        this.head.setRotationPoint(0.0f, 4.0f, 0.0f);
        this.head.setTextureSize(128, 64);
        (this.field_78133_b = new ModelRenderer((ModelBase)this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, par1 + 0.5f);
        this.field_78133_b.setRotationPoint(0.0f, 4.0f, 0.0f);
        this.field_78133_b.setTextureSize(128, 64);
        (this.body = new ModelRenderer((ModelBase)this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, par1);
        this.body.setRotationPoint(0.0f, 4.0f, 0.0f);
        this.body.setTextureSize(128, 64);
        (this.leg1 = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1);
        this.leg1.setRotationPoint(-2.0f, 16.0f, 4.0f);
        this.leg1.setTextureSize(128, 64);
        (this.leg2 = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1);
        this.leg2.setRotationPoint(2.0f, 16.0f, 4.0f);
        this.leg2.setTextureSize(128, 64);
        (this.leg3 = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1);
        this.leg3.setRotationPoint(-2.0f, 16.0f, -4.0f);
        this.leg3.setTextureSize(128, 64);
        (this.leg4 = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1);
        this.leg4.setRotationPoint(2.0f, 16.0f, -4.0f);
        this.leg4.setTextureSize(128, 64);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        if (this.isChild) {
            final float f6 = 2.0f;
            GL11.glPushMatrix();
            GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6);
            GL11.glTranslatef(0.0f, 16.0f * par7, 0.0f);
            this.head.render(par7);
            this.oxygenMask.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6);
            GL11.glTranslatef(0.0f, 24.0f * par7, 0.0f);
            this.leftOxygenTank.render(par7);
            this.rightOxygenTank.render(par7);
            this.tubeRight2.render(par7);
            this.tubeLeft1.render(par7);
            this.tubeRight3.render(par7);
            this.tubeRight4.render(par7);
            this.tubeRight5.render(par7);
            this.tubeLeft6.render(par7);
            this.tubeRight7.render(par7);
            this.tubeRight1.render(par7);
            this.tubeLeft2.render(par7);
            this.tubeLeft3.render(par7);
            this.tubeLeft4.render(par7);
            this.tubeLeft5.render(par7);
            this.tubeLeft7.render(par7);
            this.tubeRight6.render(par7);
            this.tubeLeft8.render(par7);
            this.body.render(par7);
            this.leg1.render(par7);
            this.leg2.render(par7);
            this.leg3.render(par7);
            this.leg4.render(par7);
            GL11.glPopMatrix();
        }
        else {
            this.leftOxygenTank.render(par7);
            this.rightOxygenTank.render(par7);
            this.tubeRight2.render(par7);
            this.tubeLeft1.render(par7);
            this.tubeRight3.render(par7);
            this.tubeRight4.render(par7);
            this.tubeRight5.render(par7);
            this.tubeLeft6.render(par7);
            this.tubeRight7.render(par7);
            this.tubeRight1.render(par7);
            this.tubeLeft2.render(par7);
            this.tubeLeft3.render(par7);
            this.tubeLeft4.render(par7);
            this.tubeLeft5.render(par7);
            this.tubeLeft7.render(par7);
            this.tubeRight6.render(par7);
            this.tubeLeft8.render(par7);
            this.oxygenMask.render(par7);
            this.head.render(par7);
            this.body.render(par7);
            this.leg1.render(par7);
            this.leg2.render(par7);
            this.leg3.render(par7);
            this.leg4.render(par7);
        }
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6) {
        this.oxygenMask.rotateAngleY = par4 / 57.295776f;
        this.oxygenMask.rotateAngleX = par5 / 57.295776f;
        this.head.rotateAngleY = par4 / 57.295776f;
        this.head.rotateAngleX = par5 / 57.295776f;
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2;
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2;
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
    }
}
