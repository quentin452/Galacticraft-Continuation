package micdoodle8.mods.galacticraft.planets.mars.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelTier2Rocket extends ModelBase
{
    ModelRenderer[] inside;
    ModelRenderer[][] fins;
    ModelRenderer[] top;
    ModelRenderer[][] boosters;
    ModelRenderer[] base;
    ModelRenderer[] sides;
    
    public ModelTier2Rocket() {
        this(0.0f);
    }
    
    public ModelTier2Rocket(final float var1) {
        this.inside = new ModelRenderer[3];
        this.fins = new ModelRenderer[4][5];
        this.top = new ModelRenderer[8];
        this.boosters = new ModelRenderer[4][3];
        this.base = new ModelRenderer[3];
        this.sides = new ModelRenderer[7];
        this.textureWidth = 256;
        this.textureHeight = 256;
        final float halfPI = 1.5707964f;
        final float fullPI = 3.1415927f;
        (this.inside[0] = new ModelRenderer((ModelBase)this, 0, 59)).addBox(-9.0f, -57.0f, -9.0f, 18, 1, 18, var1);
        this.inside[0].setRotationPoint(0.0f, 23.0f, 0.0f);
        this.inside[0].setTextureSize(256, 256);
        this.inside[0].mirror = true;
        this.setStartingAngles(this.inside[0], 0.0f, 0.0f, 0.0f);
        (this.inside[1] = new ModelRenderer((ModelBase)this, 0, 78)).addBox(-8.5f, -16.0f, -8.5f, 17, 1, 17, var1);
        this.inside[1].setRotationPoint(0.0f, 23.0f, 0.0f);
        this.inside[1].setTextureSize(256, 256);
        this.inside[1].mirror = true;
        this.setStartingAngles(this.inside[1], 0.0f, 0.0f, 0.0f);
        (this.inside[2] = new ModelRenderer((ModelBase)this, 0, 40)).addBox(-9.0f, -4.0f, -9.0f, 18, 1, 18, var1);
        this.inside[2].setRotationPoint(0.0f, 23.0f, 0.0f);
        this.inside[2].setTextureSize(256, 256);
        this.inside[2].mirror = true;
        this.setStartingAngles(this.inside[2], 0.0f, 0.0f, 0.0f);
        (this.fins[0][1] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -9.0f, -19.4f, 2, 8, 2, var1);
        this.fins[0][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[0][1].setTextureSize(256, 256);
        this.fins[0][1].mirror = true;
        this.setStartingAngles(this.fins[0][1], 0.0f, 0.7853982f, 0.0f);
        (this.fins[0][2] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -12.0f, -17.4f, 2, 8, 2, var1);
        this.fins[0][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[0][2].setTextureSize(256, 256);
        this.fins[0][2].mirror = true;
        this.setStartingAngles(this.fins[0][2], 0.0f, 0.7853982f, 0.0f);
        (this.fins[0][3] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -14.0f, -15.4f, 2, 8, 2, var1);
        this.fins[0][3].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[0][3].setTextureSize(256, 256);
        this.fins[0][3].mirror = true;
        this.setStartingAngles(this.fins[0][3], 0.0f, 0.7853982f, 0.0f);
        (this.fins[0][4] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -15.0f, -13.5f, 2, 8, 2, var1);
        this.fins[0][4].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[0][4].setTextureSize(256, 256);
        this.fins[0][4].mirror = true;
        this.setStartingAngles(this.fins[0][4], 0.0f, 0.7853982f, 0.0f);
        (this.fins[0][0] = new ModelRenderer((ModelBase)this, 60, 0)).addBox(-1.0f, -14.0f, -20.4f, 2, 15, 1, var1);
        this.fins[0][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[0][0].setTextureSize(256, 256);
        this.fins[0][0].mirror = true;
        this.setStartingAngles(this.fins[0][0], 0.0f, 0.7853982f, 0.0f);
        (this.fins[1][0] = new ModelRenderer((ModelBase)this, 74, 0)).addBox(-20.4f, -14.0f, -1.0f, 1, 15, 2, var1);
        this.fins[1][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[1][0].setTextureSize(256, 256);
        this.fins[1][0].mirror = true;
        this.setStartingAngles(this.fins[1][0], 0.0f, 0.7853982f, 0.0f);
        (this.fins[1][1] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-19.4f, -9.0f, -1.0f, 2, 8, 2, var1);
        this.fins[1][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[1][1].setTextureSize(256, 256);
        this.fins[1][1].mirror = true;
        this.setStartingAngles(this.fins[1][1], 0.0f, 0.7853982f, 0.0f);
        (this.fins[1][2] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-17.4f, -12.0f, -1.0f, 2, 8, 2, var1);
        this.fins[1][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[1][2].setTextureSize(256, 256);
        this.fins[1][2].mirror = true;
        this.setStartingAngles(this.fins[1][2], 0.0f, 0.7853982f, 0.0f);
        (this.fins[1][3] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-15.4f, -14.0f, -1.0f, 2, 8, 2, var1);
        this.fins[1][3].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[1][3].setTextureSize(256, 256);
        this.fins[1][3].mirror = true;
        this.setStartingAngles(this.fins[1][3], 0.0f, 0.7853982f, 0.0f);
        (this.fins[1][4] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-13.5f, -15.0f, -1.0f, 2, 8, 2, var1);
        this.fins[1][4].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[1][4].setTextureSize(256, 256);
        this.fins[1][4].mirror = true;
        this.setStartingAngles(this.fins[1][4], 0.0f, 0.7853982f, 0.0f);
        (this.fins[2][0] = new ModelRenderer((ModelBase)this, 60, 0)).addBox(-1.0f, -14.0f, 19.5f, 2, 15, 1, var1);
        this.fins[2][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[2][0].setTextureSize(256, 256);
        this.fins[2][0].mirror = true;
        this.setStartingAngles(this.fins[2][0], 0.0f, 0.7853982f, 0.0f);
        (this.fins[2][1] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -9.0f, 17.5f, 2, 8, 2, var1);
        this.fins[2][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[2][1].setTextureSize(256, 256);
        this.fins[2][1].mirror = true;
        this.setStartingAngles(this.fins[2][1], 0.0f, 0.7853982f, 0.0f);
        (this.fins[2][2] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -12.0f, 15.5f, 2, 8, 2, var1);
        this.fins[2][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[2][2].setTextureSize(256, 256);
        this.fins[2][2].mirror = true;
        this.setStartingAngles(this.fins[2][2], 0.0f, 0.7853982f, 0.0f);
        (this.fins[2][3] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -14.0f, 13.5f, 2, 8, 2, var1);
        this.fins[2][3].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[2][3].setTextureSize(256, 256);
        this.fins[2][3].mirror = true;
        this.setStartingAngles(this.fins[2][3], 0.0f, 0.7853982f, 0.0f);
        (this.fins[2][4] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(-1.0f, -15.0f, 11.6f, 2, 8, 2, var1);
        this.fins[2][4].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[2][4].setTextureSize(256, 256);
        this.fins[2][4].mirror = true;
        this.setStartingAngles(this.fins[2][4], 0.0f, 0.7853982f, 0.0f);
        (this.fins[3][0] = new ModelRenderer((ModelBase)this, 74, 0)).addBox(19.5f, -14.0f, -1.0f, 1, 15, 2, var1);
        this.fins[3][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[3][0].setTextureSize(256, 256);
        this.fins[3][0].mirror = true;
        this.setStartingAngles(this.fins[3][0], 0.0f, 0.7853982f, 0.0f);
        (this.fins[3][1] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(17.5f, -9.0f, -1.0f, 2, 8, 2, var1);
        this.fins[3][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[3][1].setTextureSize(256, 256);
        this.fins[3][1].mirror = true;
        this.setStartingAngles(this.fins[3][1], 0.0f, 0.7853982f, 0.0f);
        (this.fins[3][2] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(15.5f, -12.0f, -1.0f, 2, 8, 2, var1);
        this.fins[3][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[3][2].setTextureSize(256, 256);
        this.fins[3][2].mirror = true;
        this.setStartingAngles(this.fins[3][2], 0.0f, 0.7853982f, 0.0f);
        (this.fins[3][3] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(13.5f, -14.0f, -1.0f, 2, 8, 2, var1);
        this.fins[3][3].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[3][3].setTextureSize(256, 256);
        this.fins[3][3].mirror = true;
        this.setStartingAngles(this.fins[3][3], 0.0f, 0.7853982f, 0.0f);
        (this.fins[3][4] = new ModelRenderer((ModelBase)this, 66, 0)).addBox(11.6f, -15.0f, -1.0f, 2, 8, 2, var1);
        this.fins[3][4].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.fins[3][4].setTextureSize(256, 256);
        this.fins[3][4].mirror = true;
        this.setStartingAngles(this.fins[3][4], 0.0f, 0.7853982f, 0.0f);
        (this.top[0] = new ModelRenderer((ModelBase)this, 192, 60)).addBox(-8.0f, -60.0f, -8.0f, 16, 2, 16, var1);
        this.top[0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[0].setTextureSize(256, 256);
        this.top[0].mirror = true;
        this.setStartingAngles(this.top[0], 0.0f, 0.0f, 0.0f);
        (this.top[1] = new ModelRenderer((ModelBase)this, 200, 78)).addBox(-7.0f, -62.0f, -7.0f, 14, 2, 14, var1);
        this.top[1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[1].setTextureSize(256, 256);
        this.top[1].mirror = true;
        this.setStartingAngles(this.top[1], 0.0f, 0.0f, 0.0f);
        (this.top[2] = new ModelRenderer((ModelBase)this, 208, 94)).addBox(-6.0f, -64.0f, -6.0f, 12, 2, 12, var1);
        this.top[2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[2].setTextureSize(256, 256);
        this.top[2].mirror = true;
        this.setStartingAngles(this.top[2], 0.0f, 0.0f, 0.0f);
        (this.top[3] = new ModelRenderer((ModelBase)this, 216, 108)).addBox(-5.0f, -66.0f, -5.0f, 10, 2, 10, var1);
        this.top[3].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[3].setTextureSize(256, 256);
        this.top[3].mirror = true;
        this.setStartingAngles(this.top[3], 0.0f, 0.0f, 0.0f);
        (this.top[4] = new ModelRenderer((ModelBase)this, 224, 120)).addBox(-4.0f, -68.0f, -4.0f, 8, 2, 8, var1);
        this.top[4].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[4].setTextureSize(256, 256);
        this.top[4].mirror = true;
        this.setStartingAngles(this.top[4], 0.0f, 0.0f, 0.0f);
        (this.top[5] = new ModelRenderer((ModelBase)this, 232, 130)).addBox(-3.0f, -70.0f, -3.0f, 6, 2, 6, var1);
        this.top[5].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[5].setTextureSize(256, 256);
        this.top[5].mirror = true;
        this.setStartingAngles(this.top[5], 0.0f, 0.0f, 0.0f);
        (this.top[6] = new ModelRenderer((ModelBase)this, 240, 138)).addBox(-2.0f, -72.0f, -2.0f, 4, 2, 4, var1);
        this.top[6].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[6].setTextureSize(256, 256);
        this.top[6].mirror = true;
        this.setStartingAngles(this.top[6], 0.0f, 0.0f, 0.0f);
        (this.top[7] = new ModelRenderer((ModelBase)this, 248, 144)).addBox(-1.0f, -88.0f, -1.0f, 2, 18, 2, var1);
        this.top[7].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.top[7].setTextureSize(256, 256);
        this.top[7].mirror = true;
        this.setStartingAngles(this.top[7], 0.0f, 0.0f, 0.0f);
        (this.base[0] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-7.0f, -1.0f, -7.0f, 14, 1, 14, var1);
        this.base[0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.base[0].setTextureSize(256, 256);
        this.base[0].mirror = true;
        this.setStartingAngles(this.base[0], 0.0f, 0.0f, 0.0f);
        (this.base[1] = new ModelRenderer((ModelBase)this, 0, 15)).addBox(-6.0f, -2.0f, -6.0f, 12, 1, 12, var1);
        this.base[1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.base[1].setTextureSize(256, 256);
        this.base[1].mirror = true;
        this.setStartingAngles(this.base[1], 0.0f, 0.0f, 0.0f);
        (this.base[2] = new ModelRenderer((ModelBase)this, 0, 28)).addBox(-5.0f, -4.0f, -5.0f, 10, 2, 10, var1);
        this.base[2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.base[2].setTextureSize(256, 256);
        this.base[2].mirror = true;
        this.setStartingAngles(this.base[2], 0.0f, 0.0f, 0.0f);
        (this.sides[0] = new ModelRenderer((ModelBase)this, 85, 0)).addBox(-3.9f, -58.0f, -8.9f, 8, 17, 1, var1);
        this.sides[0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[0].setTextureSize(256, 256);
        this.sides[0].mirror = true;
        this.setStartingAngles(this.sides[0], 0.0f, 0.0f, 0.0f);
        (this.sides[1] = new ModelRenderer((ModelBase)this, 103, 0)).addBox(3.9f, -58.0f, -8.9f, 5, 54, 1, var1);
        this.sides[1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[1].setTextureSize(256, 256);
        this.sides[1].mirror = true;
        this.setStartingAngles(this.sides[1], 0.0f, 0.0f, 0.0f);
        (this.sides[2] = new ModelRenderer((ModelBase)this, 85, 18)).addBox(-3.9f, -34.0f, -8.9f, 8, 30, 1, var1);
        this.sides[2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[2].setTextureSize(256, 256);
        this.sides[2].mirror = true;
        this.setStartingAngles(this.sides[2], 0.0f, 0.0f, 0.0f);
        (this.sides[3] = new ModelRenderer((ModelBase)this, 103, 55)).addBox(-8.9f, -58.0f, -8.9f, 5, 54, 1, var1);
        this.sides[3].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[3].setTextureSize(256, 256);
        this.sides[3].mirror = true;
        this.setStartingAngles(this.sides[3], 0.0f, 0.0f, 0.0f);
        (this.sides[4] = new ModelRenderer((ModelBase)this, 120, 0)).addBox(-8.9f, -58.0f, -7.9f, 1, 54, 16, var1);
        this.sides[4].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[4].setTextureSize(256, 256);
        this.sides[4].mirror = true;
        this.setStartingAngles(this.sides[4], 0.0f, 0.0f, 0.0f);
        (this.sides[5] = new ModelRenderer((ModelBase)this, 120, 141)).addBox(-8.9f, -58.0f, 8.1f, 17, 54, 1, var1);
        this.sides[5].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[5].setTextureSize(256, 256);
        this.sides[5].mirror = true;
        this.setStartingAngles(this.sides[5], 0.0f, 0.0f, 0.0f);
        (this.sides[6] = new ModelRenderer((ModelBase)this, 119, 70)).addBox(8.1f, -58.0f, -7.9f, 1, 54, 17, var1);
        this.sides[6].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.sides[6].setTextureSize(256, 256);
        this.sides[6].mirror = true;
        this.setStartingAngles(this.sides[6], 0.0f, 0.0f, 0.0f);
        (this.boosters[0][0] = new ModelRenderer((ModelBase)this, 154, 19)).addBox(-10.9f, -10.0f, -0.5f, 3, 5, 1, var1);
        this.boosters[0][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[0][0].setTextureSize(256, 256);
        this.boosters[0][0].mirror = true;
        this.setStartingAngles(this.boosters[0][0], 0.0f, -halfPI, 0.0f);
        (this.boosters[0][1] = new ModelRenderer((ModelBase)this, 154, 6)).addBox(-14.9f, -11.0f, -2.5f, 5, 8, 5, var1);
        this.boosters[0][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[0][1].setTextureSize(256, 256);
        this.boosters[0][1].mirror = true;
        this.setStartingAngles(this.boosters[0][1], 0.0f, -halfPI, 0.0f);
        (this.boosters[0][2] = new ModelRenderer((ModelBase)this, 154, 0)).addBox(-14.4f, -13.0f, -2.0f, 4, 2, 4, var1);
        this.boosters[0][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[0][2].setTextureSize(256, 256);
        this.boosters[0][2].mirror = true;
        this.setStartingAngles(this.boosters[0][2], 0.0f, -halfPI, 0.0f);
        (this.boosters[1][0] = new ModelRenderer((ModelBase)this, 154, 19)).addBox(-10.9f, -10.0f, -0.5f, 3, 5, 1, var1);
        this.boosters[1][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[1][0].setTextureSize(256, 256);
        this.boosters[1][0].mirror = true;
        this.setStartingAngles(this.boosters[1][0], 0.0f, 0.0f, 0.0f);
        (this.boosters[1][1] = new ModelRenderer((ModelBase)this, 154, 6)).addBox(-14.9f, -11.0f, -2.5f, 5, 8, 5, var1);
        this.boosters[1][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[1][1].setTextureSize(256, 256);
        this.boosters[1][1].mirror = true;
        this.setStartingAngles(this.boosters[1][1], 0.0f, 0.0f, 0.0f);
        (this.boosters[1][2] = new ModelRenderer((ModelBase)this, 154, 0)).addBox(-14.4f, -13.0f, -2.0f, 4, 2, 4, var1);
        this.boosters[1][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[1][2].setTextureSize(256, 256);
        this.boosters[1][2].mirror = true;
        this.setStartingAngles(this.boosters[1][2], 0.0f, 0.0f, 0.0f);
        (this.boosters[2][0] = new ModelRenderer((ModelBase)this, 154, 19)).addBox(-10.9f, -10.0f, -0.5f, 3, 5, 1, var1);
        this.boosters[2][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[2][0].setTextureSize(256, 256);
        this.boosters[2][0].mirror = true;
        this.setStartingAngles(this.boosters[2][0], 0.0f, halfPI, 0.0f);
        (this.boosters[2][1] = new ModelRenderer((ModelBase)this, 154, 6)).addBox(-14.9f, -11.0f, -2.5f, 5, 8, 5, var1);
        this.boosters[2][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[2][1].setTextureSize(256, 256);
        this.boosters[2][1].mirror = true;
        this.setStartingAngles(this.boosters[2][1], 0.0f, halfPI, 0.0f);
        (this.boosters[2][2] = new ModelRenderer((ModelBase)this, 154, 0)).addBox(-14.4f, -13.0f, -2.0f, 4, 2, 4, var1);
        this.boosters[2][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[2][2].setTextureSize(256, 256);
        this.boosters[2][2].mirror = true;
        this.setStartingAngles(this.boosters[2][2], 0.0f, halfPI, 0.0f);
        (this.boosters[3][0] = new ModelRenderer((ModelBase)this, 154, 19)).addBox(-10.9f, -10.0f, -0.5f, 3, 5, 1, var1);
        this.boosters[3][0].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[3][0].setTextureSize(256, 256);
        this.boosters[3][0].mirror = true;
        this.setStartingAngles(this.boosters[3][0], 0.0f, fullPI, 0.0f);
        (this.boosters[3][1] = new ModelRenderer((ModelBase)this, 154, 6)).addBox(-14.9f, -11.0f, -2.5f, 5, 8, 5, var1);
        this.boosters[3][1].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[3][1].setTextureSize(256, 256);
        this.boosters[3][1].mirror = true;
        this.setStartingAngles(this.boosters[3][1], 0.0f, fullPI, 0.0f);
        (this.boosters[3][2] = new ModelRenderer((ModelBase)this, 154, 0)).addBox(-14.4f, -13.0f, -2.0f, 4, 2, 4, var1);
        this.boosters[3][2].setRotationPoint(0.0f, 24.0f, 0.0f);
        this.boosters[3][2].setTextureSize(256, 256);
        this.boosters[3][2].mirror = true;
        this.setStartingAngles(this.boosters[3][2], 0.0f, fullPI, 0.0f);
    }
    
    public void render(final Entity entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        for (final ModelRenderer model : this.inside) {
            model.render(par7);
        }
        for (final ModelRenderer model : this.top) {
            model.render(par7);
        }
        for (final ModelRenderer model : this.base) {
            model.render(par7);
        }
        for (final ModelRenderer model : this.sides) {
            model.render(par7);
        }
        int var1 = 0;
        int var2 = 0;
        for (var1 = 0; var1 < this.fins.length; ++var1) {
            for (var2 = 0; var2 < this.fins[var1].length; ++var2) {
                this.fins[var1][var2].render(par7);
            }
        }
        for (var1 = 0; var1 < this.boosters.length; ++var1) {
            for (var2 = 0; var2 < this.boosters[var1].length; ++var2) {
                this.boosters[var1][var2].render(par7);
            }
        }
    }
    
    private void setStartingAngles(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
    }
}
