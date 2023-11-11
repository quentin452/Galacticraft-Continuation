package micdoodle8.mods.galacticraft.core.client.model;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.smart.render.ModelRotationRenderer;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;

public class ModelRotationRendererGC extends ModelRotationRenderer
{
    private int type;
    private static RenderPlayer playerRenderer;
    private Method getEntityTextureMethod;

    public ModelRotationRendererGC(final ModelBase modelBase, final int i, final int j, final ModelRenderer baseRenderer, final int type) {
        super(modelBase, i, j, (ModelRotationRenderer)baseRenderer);
        this.type = type;
        ModelPlayerBaseGC.frequencyModule = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/frequencyModule.obj"));
    }

    public boolean preRender(final float f) {
        final boolean b = super.preRender(f);
        if (ModelPlayerBaseGC.currentGearData == null) {
            return false;
        }
        if (b) {
            switch (this.type) {
                case 0: {
                    return ModelPlayerBaseGC.currentGearData.getMask() > -1;
                }
                case 1: {
                    return ModelPlayerBaseGC.currentGearData.getParachute() != null;
                }
                case 2: {
                    return ModelPlayerBaseGC.currentGearData.getGear() > -1;
                }
                case 3: {
                    return ModelPlayerBaseGC.currentGearData.getLeftTank() == 0;
                }
                case 4: {
                    return ModelPlayerBaseGC.currentGearData.getRightTank() == 0;
                }
                case 5: {
                    return ModelPlayerBaseGC.currentGearData.getLeftTank() == 1;
                }
                case 6: {
                    return ModelPlayerBaseGC.currentGearData.getRightTank() == 1;
                }
                case 7: {
                    return ModelPlayerBaseGC.currentGearData.getLeftTank() == 2;
                }
                case 8: {
                    return ModelPlayerBaseGC.currentGearData.getRightTank() == 2;
                }
                case 9: {
                    return ModelPlayerBaseGC.currentGearData.getFrequencyModule() > -1;
                }
            }
        }
        return b;
    }

    public void doRender(final float f, final boolean useParentTransformations) {
        if (this.preRender(f)) {
            final int saveTex = GL11.glGetInteger(32873);
            switch (this.type) {
                case 0: {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.oxygenMaskTexture);
                    break;
                }
                case 1: {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerBaseGC.currentGearData.getParachute());
                    break;
                }
                case 9: {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.frequencyModuleTexture);
                    break;
                }
                default: {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture);
                    break;
                }
            }
            if (this.type != 9) {
                super.doRender(f, useParentTransformations);
            }
            else {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.frequencyModuleTexture);
                GL11.glPushMatrix();
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GL11.glScalef(0.3f, 0.3f, 0.3f);
                GL11.glTranslatef(-1.1f, 1.2f, 0.0f);
                ModelPlayerBaseGC.frequencyModule.renderPart("Main");
                GL11.glTranslatef(0.0f, 1.2f, 0.0f);
                GL11.glRotatef((float)(Math.sin(ModelPlayerBaseGC.playerRendering.ticksExisted * 0.05) * 50.0), 1.0f, 0.0f, 0.0f);
                GL11.glRotatef((float)(Math.cos(ModelPlayerBaseGC.playerRendering.ticksExisted * 0.1) * 50.0), 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(0.0f, -1.2f, 0.0f);
                ModelPlayerBaseGC.frequencyModule.renderPart("Radar");
                GL11.glPopMatrix();
            }
            GL11.glBindTexture(3553, saveTex);
        }
    }
}
