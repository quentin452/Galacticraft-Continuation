package micdoodle8.mods.galacticraft.planets.mars.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.util.*;

public class ModelCreeperBoss extends ModelBase
{
    ModelRenderer headMain;
    ModelRenderer bodyMain;
    ModelRenderer rightLegFront;
    ModelRenderer leftLegFront;
    ModelRenderer rightLeg;
    ModelRenderer leftLeg;
    ModelRenderer oxygenTank;
    ModelRenderer headLeft;
    ModelRenderer headRight;
    ModelRenderer neckRight;
    ModelRenderer neckLeft;
    
    public ModelCreeperBoss() {
        this(0.0f);
    }
    
    public ModelCreeperBoss(final float scale) {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.neckRight = new ModelRenderer((ModelBase)this, 16, 20);
        this.neckRight.mirror = true;
        this.neckRight.addBox(-2.5f, -9.0f, -1.5f, 5, 9, 3, scale);
        this.neckRight.setRotationPoint(-3.0f, 10.0f, 0.0f);
        this.neckRight.setTextureSize(128, 64);
        this.neckRight.mirror = true;
        this.setRotation(this.neckRight, 0.0f, 0.0f, -1.169371f);
        this.neckRight.mirror = false;
        (this.neckLeft = new ModelRenderer((ModelBase)this, 16, 20)).addBox(-2.5f, -9.0f, -1.5f, 5, 9, 3, scale);
        this.neckLeft.setRotationPoint(3.0f, 10.0f, 0.0f);
        this.neckLeft.setTextureSize(128, 64);
        this.neckLeft.mirror = true;
        this.setRotation(this.neckLeft, 0.0f, 0.0f, 1.169371f);
        (this.headMain = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, scale);
        this.headMain.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.headMain.setTextureSize(128, 64);
        this.headMain.mirror = true;
        this.setRotation(this.headMain, 0.0f, 0.0f, 0.0f);
        (this.bodyMain = new ModelRenderer((ModelBase)this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, scale);
        this.bodyMain.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.bodyMain.setTextureSize(128, 64);
        this.bodyMain.mirror = true;
        this.setRotation(this.bodyMain, 0.0f, 0.0f, 0.0f);
        (this.rightLegFront = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, scale);
        this.rightLegFront.setRotationPoint(-2.0f, 18.0f, -4.0f);
        this.rightLegFront.setTextureSize(128, 64);
        this.rightLegFront.mirror = true;
        this.setRotation(this.rightLegFront, 0.0f, 0.0f, 0.0f);
        (this.leftLegFront = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, scale);
        this.leftLegFront.setRotationPoint(2.0f, 18.0f, -4.0f);
        this.leftLegFront.setTextureSize(128, 64);
        this.leftLegFront.mirror = true;
        this.setRotation(this.leftLegFront, 0.0f, 0.0f, 0.0f);
        (this.rightLeg = new ModelRenderer((ModelBase)this, 0, 16)).addBox(0.0f, 0.0f, -2.0f, 4, 6, 4, scale);
        this.rightLeg.setRotationPoint(-4.0f, 18.0f, 4.0f);
        this.rightLeg.setTextureSize(128, 64);
        this.rightLeg.mirror = true;
        this.setRotation(this.rightLeg, 0.0f, 0.0f, 0.0f);
        (this.leftLeg = new ModelRenderer((ModelBase)this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, scale);
        this.leftLeg.setRotationPoint(2.0f, 18.0f, 4.0f);
        this.leftLeg.setTextureSize(128, 64);
        this.leftLeg.mirror = true;
        this.setRotation(this.leftLeg, 0.0f, 0.0f, 0.0f);
        (this.oxygenTank = new ModelRenderer((ModelBase)this, 40, 0)).addBox(-5.0f, -9.0f, -5.0f, 10, 10, 10, scale);
        this.oxygenTank.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.oxygenTank.setTextureSize(128, 64);
        this.oxygenTank.mirror = true;
        this.setRotation(this.oxygenTank, 0.0f, 0.0f, 0.0f);
        (this.headLeft = new ModelRenderer((ModelBase)this, 0, 0)).addBox(1.0f, -9.0f, -4.0f, 8, 8, 8, scale);
        this.headLeft.setRotationPoint(3.0f, 6.0f, 0.1f);
        this.headLeft.setTextureSize(128, 64);
        this.headLeft.mirror = true;
        this.setRotation(this.headLeft, 0.0f, 0.0f, 0.7853982f);
        (this.headRight = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-9.0f, -9.0f, -4.0f, 8, 8, 8, scale);
        this.headRight.setRotationPoint(-3.0f, 6.0f, -0.1f);
        this.headRight.setTextureSize(128, 64);
        this.headRight.mirror = true;
        this.setRotation(this.headRight, 0.0f, 0.0f, -0.7853982f);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        final EntityCreeperBoss creeper = (EntityCreeperBoss)entity;
        if (creeper.headsRemaining > 2) {
            this.headLeft.render(f5);
            this.neckLeft.render(f5);
            this.headRight.render(f5);
            this.neckRight.render(f5);
            this.headMain.render(f5);
            this.oxygenTank.render(f5);
        }
        else if (creeper.headsRemaining > 1) {
            this.headRight.render(f5);
            this.neckRight.render(f5);
            this.headMain.render(f5);
            this.oxygenTank.render(f5);
        }
        else if (creeper.headsRemaining > 0) {
            this.headMain.render(f5);
            this.oxygenTank.render(f5);
        }
        this.bodyMain.render(f5);
        this.rightLegFront.render(f5);
        this.leftLegFront.render(f5);
        this.rightLeg.render(f5);
        this.leftLeg.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5, final Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.headMain.rotateAngleY = f3 / 57.295776f;
        this.headMain.rotateAngleX = f4 / 57.295776f;
        this.oxygenTank.rotateAngleY = f3 / 57.295776f;
        this.oxygenTank.rotateAngleX = f4 / 57.295776f;
        this.rightLegFront.rotateAngleX = MathHelper.cos(f * 0.6662f) * 1.4f * f1;
        this.leftLegFront.rotateAngleX = MathHelper.cos(f * 0.6662f + 3.1415927f) * 2.0f * f1;
        this.leftLeg.rotateAngleX = MathHelper.cos(f * 0.6662f + 3.1415927f) * 2.0f * f1;
        this.rightLeg.rotateAngleX = MathHelper.cos(f * 0.6662f) * 2.0f * f1;
    }
}
