package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelAlienVillager extends ModelVillager
{
    public ModelRenderer brain;
    
    public ModelAlienVillager(final float par1) {
        this(par1, 0.0f, 64, 64);
    }
    
    public ModelAlienVillager(final float par1, final float par2, final int par3, final int par4) {
        super(par1, par2, 0, 0);
        (this.villagerHead = new ModelRenderer((ModelBase)this).setTextureSize(par3, par4)).setRotationPoint(0.0f, 0.0f + par2, 0.0f);
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, par1 + 0.001f);
        (this.villagerNose = new ModelRenderer((ModelBase)this).setTextureSize(par3, par4)).setRotationPoint(0.0f, par2 - 2.0f, 0.0f);
        this.villagerNose.setTextureOffset(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2, 4, 2, par1 + 0.002f);
        this.villagerHead.addChild(this.villagerNose);
        (this.villagerBody = new ModelRenderer((ModelBase)this).setTextureSize(par3, par4)).setRotationPoint(0.0f, 0.0f + par2, 0.0f);
        this.villagerBody.setTextureOffset(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, par1 + 0.003f);
        this.villagerBody.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, par1 + 0.5f + 0.004f);
        (this.villagerArms = new ModelRenderer((ModelBase)this).setTextureSize(par3, par4)).setRotationPoint(0.0f, 0.0f + par2 + 2.0f, 0.0f);
        this.villagerArms.setTextureOffset(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4, 8, 4, par1 + 0.005f);
        this.villagerArms.setTextureOffset(44, 22).addBox(4.0f, -2.0f, -2.0f, 4, 8, 4, par1 + 1.0E-4f);
        this.villagerArms.setTextureOffset(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8, 4, 4, par1 + 4.0E-4f);
        (this.rightVillagerLeg = new ModelRenderer((ModelBase)this, 0, 22).setTextureSize(par3, par4)).setRotationPoint(-2.0f, 12.0f + par2, 0.0f);
        this.rightVillagerLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, par1 + 6.0E-4f);
        this.leftVillagerLeg = new ModelRenderer((ModelBase)this, 0, 22).setTextureSize(par3, par4);
        this.leftVillagerLeg.mirror = true;
        this.leftVillagerLeg.setRotationPoint(2.0f, 12.0f + par2, 0.0f);
        this.leftVillagerLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, par1 + 2.0E-4f);
        (this.brain = new ModelRenderer((ModelBase)this).setTextureSize(par3, par4)).setRotationPoint(0.0f, 0.0f + par2, 0.0f);
        this.brain.setTextureOffset(32, 0).addBox(-4.0f, -16.0f, -4.0f, 8, 8, 8, par1 + 0.5f);
    }
    
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
        this.brain.render(par7);
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity par7Entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        this.brain.rotateAngleX = this.villagerHead.rotateAngleX;
        this.brain.rotateAngleY = this.villagerHead.rotateAngleY;
        this.brain.rotateAngleZ = this.villagerHead.rotateAngleZ;
    }
}
