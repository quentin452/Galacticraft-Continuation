package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.util.*;
import net.minecraft.client.model.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.*;

public class ModelParaChest extends ModelChest
{
    private static final ResourceLocation grayParachuteTexture;
    public ModelRenderer[] parachute;
    public ModelRenderer[] parachuteStrings;
    
    public ModelParaChest() {
        this(0.0f);
    }
    
    public ModelParaChest(final float par1) {
        this.parachute = new ModelRenderer[3];
        this.parachuteStrings = new ModelRenderer[4];
        (this.parachute[0] = new ModelRenderer((ModelBase)this, 0, 0).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, par1);
        this.parachute[0].setRotationPoint(15.0f, 4.0f, 0.0f);
        (this.parachute[1] = new ModelRenderer((ModelBase)this, 0, 42).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 40, 2, 40, par1);
        this.parachute[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachute[2] = new ModelRenderer((ModelBase)this, 0, 0).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, par1);
        this.parachute[2].setRotationPoint(11.0f, -11.0f, 0.0f);
        (this.parachuteStrings[0] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1);
        this.parachuteStrings[0].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachuteStrings[1] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1);
        this.parachuteStrings[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachuteStrings[2] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1);
        this.parachuteStrings[2].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachuteStrings[3] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1);
        this.parachuteStrings[3].setRotationPoint(0.0f, 0.0f, 0.0f);
    }
    
    public void renderAll() {
        super.renderAll();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelParaChest.grayParachuteTexture);
        this.chestLid.rotateAngleX = 3.1415927f;
        this.chestBelow.rotateAngleX = 3.1415927f;
        this.chestKnob.rotateAngleX = 3.1415927f;
        this.chestLid.rotationPointX = 2.0f;
        this.chestLid.rotationPointY = 7.0f;
        this.chestLid.rotationPointZ = -6.0f;
        this.chestKnob.rotationPointX = 9.0f;
        this.chestKnob.rotationPointY = 7.0f;
        this.chestKnob.rotationPointZ = -6.0f;
        this.chestBelow.rotationPointX = 2.0f;
        this.chestBelow.rotationPointY = 8.0f;
        this.chestBelow.rotationPointZ = 8.0f;
        for (int i = 0; i < this.parachute.length; ++i) {
            this.parachute[i].render(0.0625f);
        }
        for (int i = 0; i < this.parachuteStrings.length; ++i) {
            this.parachuteStrings[i].render(0.0625f);
        }
        this.parachute[0].rotateAngleY = 0.0f;
        this.parachute[2].rotateAngleY = -0.0f;
        this.parachuteStrings[0].rotateAngleY = 0.0f;
        this.parachuteStrings[1].rotateAngleY = 0.0f;
        this.parachuteStrings[2].rotateAngleY = -0.0f;
        this.parachuteStrings[3].rotateAngleY = -0.0f;
        this.parachute[0].setRotationPoint(-5.85f, -11.0f, 2.0f);
        this.parachute[1].setRotationPoint(9.0f, -7.0f, 2.0f);
        this.parachute[2].setRotationPoint(-2.15f, 4.0f, 2.0f);
        this.parachute[0].rotateAngleZ = 3.6651914f;
        this.parachute[1].rotateAngleZ = 3.1415927f;
        this.parachute[2].rotateAngleZ = -3.6651914f;
        this.parachuteStrings[0].rotateAngleZ = 5.846853f;
        this.parachuteStrings[0].rotateAngleX = 0.40142572f;
        this.parachuteStrings[0].setRotationPoint(9.0f, 3.0f, 2.0f);
        this.parachuteStrings[1].rotateAngleZ = 5.846853f;
        this.parachuteStrings[1].rotateAngleX = -0.40142572f;
        this.parachuteStrings[1].setRotationPoint(9.0f, 3.0f, 2.0f);
        this.parachuteStrings[2].rotateAngleZ = -5.846853f;
        this.parachuteStrings[2].rotateAngleX = 0.40142572f;
        this.parachuteStrings[2].setRotationPoint(9.0f, 3.0f, 2.0f);
        this.parachuteStrings[3].rotateAngleZ = -5.846853f;
        this.parachuteStrings[3].rotateAngleX = -0.40142572f;
        this.parachuteStrings[3].setRotationPoint(9.0f, 3.0f, 2.0f);
    }
    
    public void renderParachute() {
    }
    
    static {
        grayParachuteTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/parachute/gray.png");
    }
}
