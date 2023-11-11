package micdoodle8.mods.galacticraft.core.client.model.block;

import net.minecraft.client.model.*;

public class ModelParaChestTile extends ModelChest
{
    public ModelRenderer[] keyParts;
    
    public ModelParaChestTile() {
        this.keyParts = new ModelRenderer[3];
        (this.keyParts[0] = new ModelRenderer((ModelBase)this, 60, 61)).setTextureSize(64, 64);
        this.keyParts[0].addBox(-1.0f, -6.0f, 0.0f, 1, 1, 1);
        this.keyParts[0].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[0].mirror = true;
        this.keyParts[0].rotationPointX = 7.0f;
        this.keyParts[0].rotationPointY = 7.0f;
        this.keyParts[0].rotationPointZ = 7.5f;
        (this.keyParts[1] = new ModelRenderer((ModelBase)this, 53, 57)).setTextureSize(64, 64);
        this.keyParts[1].addBox(-1.0f, -6.0f, 0.0f, 4, 1, 1);
        this.keyParts[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[1].mirror = true;
        this.keyParts[1].rotationPointX = 7.0f;
        this.keyParts[1].rotationPointY = 6.0f;
        this.keyParts[1].rotationPointZ = 7.5f;
        (this.keyParts[2] = new ModelRenderer((ModelBase)this, 60, 61)).setTextureSize(64, 64);
        this.keyParts[2].addBox(-1.0f, -6.0f, 0.0f, 1, 1, 1);
        this.keyParts[2].setRotationPoint(0.0f, 0.0f, 0.0f);
        this.keyParts[2].mirror = true;
        this.keyParts[2].rotationPointX = 10.0f;
        this.keyParts[2].rotationPointY = 7.0f;
        this.keyParts[2].rotationPointZ = 7.5f;
    }
    
    public void renderAll(final boolean lidUp) {
        if (lidUp) {
            for (final ModelRenderer m : this.keyParts) {
                m.render(0.0625f);
            }
        }
        super.renderAll();
    }
}
