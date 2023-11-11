package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.client.model.*;

public class ModelKey extends ModelBase
{
    public ModelRenderer[] keyParts;
    
    public ModelKey() {
        this.keyParts = new ModelRenderer[5];
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.keyParts[4] = new ModelRenderer((ModelBase)this, 50, 43)).addBox(7.0f, 2.0f, -0.5f, 3, 1, 1);
        this.keyParts[4].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[4].setTextureSize(64, 64);
        this.keyParts[4].mirror = true;
        (this.keyParts[3] = new ModelRenderer((ModelBase)this, 39, 43)).addBox(6.0f, 1.0f, -0.5f, 4, 1, 1);
        this.keyParts[3].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[3].setTextureSize(64, 64);
        this.keyParts[3].mirror = true;
        (this.keyParts[2] = new ModelRenderer((ModelBase)this, 14, 43)).addBox(-0.5f, 0.0f, -0.5f, 11, 1, 1);
        this.keyParts[2].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[2].setTextureSize(64, 64);
        this.keyParts[2].mirror = true;
        (this.keyParts[1] = new ModelRenderer((ModelBase)this, 9, 43)).addBox(-1.5f, -0.5f, -0.5f, 1, 2, 1);
        this.keyParts[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[1].setTextureSize(64, 64);
        this.keyParts[1].mirror = true;
        (this.keyParts[0] = new ModelRenderer((ModelBase)this, 0, 43)).addBox(-4.5f, -1.0f, -0.5f, 3, 3, 1);
        this.keyParts[0].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[0].setTextureSize(64, 64);
        this.keyParts[0].mirror = true;
    }
    
    public void renderAll() {
        for (final ModelRenderer nmtmr : this.keyParts) {
            nmtmr.rotationPointX = -4.0f;
            nmtmr.rotationPointY = 0.0f;
            nmtmr.rotationPointZ = -2.0f;
            nmtmr.render(0.0625f);
        }
    }
}
