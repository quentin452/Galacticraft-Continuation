package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class RenderBuggy extends Render
{
    private static final ResourceLocation buggyTextureBody;
    private static final ResourceLocation buggyTextureWheel;
    private static final ResourceLocation buggyTextureStorage;
    private final IModelCustom modelBuggy;
    private final IModelCustom modelBuggyWheelRight;
    private final IModelCustom modelBuggyWheelLeft;
    
    public RenderBuggy() {
        this.modelBuggy = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/buggy.obj"));
        this.modelBuggyWheelRight = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/buggyWheelRight.obj"));
        this.modelBuggyWheelLeft = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/buggyWheelLeft.obj"));
        this.shadowSize = 2.0f;
    }
    
    protected ResourceLocation func_110779_a(final EntityBuggy par1EntityArrow) {
        return RenderBuggy.buggyTextureBody;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityBuggy)par1Entity);
    }
    
    public void renderBuggy(final EntityBuggy entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        GL11.glTranslatef((float)par2, (float)par4 - 2.5f, (float)par6);
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f);
        GL11.glScalef(0.41f, 0.41f, 0.41f);
        this.bindTexture(RenderBuggy.buggyTextureWheel);
        final float rotation = entity.wheelRotationX;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, -2.6f);
        GL11.glRotatef(entity.wheelRotationZ, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(1.4f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRightCover_Cover");
        GL11.glTranslatef(-2.8f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeftCover_Cover");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, 3.7f);
        GL11.glRotatef(-entity.wheelRotationZ, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(2.0f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRightCover_Cover");
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeftCover_Cover");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, -2.7f);
        GL11.glRotatef(entity.wheelRotationZ, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(rotation, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(1.4f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRight_Wheel");
        GL11.glTranslatef(-2.8f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeft_Wheel");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, 3.6f);
        GL11.glRotatef(-entity.wheelRotationZ, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(rotation, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(2.0f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRight_Wheel");
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeft_Wheel");
        GL11.glPopMatrix();
        this.bindTexture(RenderBuggy.buggyTextureBody);
        this.modelBuggy.renderPart("MainBody");
        GL11.glPushMatrix();
        GL11.glTranslatef(-1.178f, 4.1f, -2.397f);
        GL11.glRotatef((float)Math.sin(entity.ticksExisted * 0.05) * 50.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef((float)Math.cos(entity.ticksExisted * 0.1) * 50.0f, 0.0f, 0.0f, 1.0f);
        this.modelBuggy.renderPart("RadarDish_Dish");
        GL11.glPopMatrix();
        this.bindTexture(RenderBuggy.buggyTextureStorage);
        if (entity.buggyType > 0) {
            this.modelBuggy.renderPart("CargoLeft");
            if (entity.buggyType > 1) {
                this.modelBuggy.renderPart("CargoMid");
                if (entity.buggyType > 2) {
                    this.modelBuggy.renderPart("CargoRight");
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderBuggy((EntityBuggy)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        buggyTextureBody = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/buggyMain.png");
        buggyTextureWheel = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/buggyWheels.png");
        buggyTextureStorage = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/buggyStorage.png");
    }
}
