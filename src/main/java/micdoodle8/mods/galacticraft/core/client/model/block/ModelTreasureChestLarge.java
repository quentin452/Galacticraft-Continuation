package micdoodle8.mods.galacticraft.core.client.model.block;

import net.minecraft.client.model.*;

public class ModelTreasureChestLarge extends ModelLargeChest
{
    public ModelRenderer[] keyParts;
    
    public ModelTreasureChestLarge() {
        this.keyParts = new ModelRenderer[6];
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.keyParts[4] = new ModelRenderer((ModelBase)this, 50, 43)).addBox(7.0f, 2.0f, -0.5f, 3, 1, 1);
        this.keyParts[4].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[4].setTextureSize(128, 64);
        this.keyParts[4].mirror = true;
        (this.keyParts[3] = new ModelRenderer((ModelBase)this, 39, 43)).addBox(6.0f, 1.0f, -0.5f, 4, 1, 1);
        this.keyParts[3].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[3].setTextureSize(128, 64);
        this.keyParts[3].mirror = true;
        (this.keyParts[2] = new ModelRenderer((ModelBase)this, 14, 43)).addBox(-0.5f, 0.0f, -0.5f, 11, 1, 1);
        this.keyParts[2].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[2].setTextureSize(128, 64);
        this.keyParts[2].mirror = true;
        (this.keyParts[1] = new ModelRenderer((ModelBase)this, 9, 43)).addBox(-1.5f, -0.5f, -0.5f, 1, 2, 1);
        this.keyParts[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[1].setTextureSize(128, 64);
        this.keyParts[1].mirror = true;
        (this.keyParts[0] = new ModelRenderer((ModelBase)this, 0, 43)).addBox(-4.5f, -1.0f, -0.5f, 3, 3, 1);
        this.keyParts[0].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[0].setTextureSize(128, 64);
        this.keyParts[0].mirror = true;
        (this.keyParts[5] = new ModelRenderer((ModelBase)this, 0, 0).setTextureSize(128, 64)).addBox(-2.0f, -2.05f, -15.1f, 4, 4, 1, 0.0f);
        this.keyParts[5].rotationPointX = 8.0f;
        this.keyParts[5].rotationPointY = 7.0f;
        this.keyParts[5].rotationPointZ = 15.0f;
    }
    
    public void renderAll(final boolean withKey) {
        if (withKey) {
            for (final ModelRenderer nmtmr : this.keyParts) {
                if (!nmtmr.equals(this.keyParts[5])) {
                    nmtmr.rotationPointX = 16.0f;
                    nmtmr.rotationPointY = 7.0f;
                    nmtmr.rotationPointZ = -2.0f;
                    nmtmr.rotateAngleY = 4.712389f;
                    nmtmr.rotateAngleX = -this.chestLid.rotateAngleX;
                    nmtmr.render(0.0625f);
                }
            }
        }
        this.keyParts[5].rotationPointX = 16.0f;
        this.keyParts[5].rotationPointY = 7.0f;
        this.keyParts[5].rotationPointZ = 15.0f;
        this.keyParts[5].rotateAngleX = 0.0f;
        this.keyParts[5].rotateAngleY = 0.0f;
        this.keyParts[5].render(0.0625f);
        super.renderAll();
    }
}
