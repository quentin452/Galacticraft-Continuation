package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.client.render.tile.*;
import net.minecraft.client.renderer.*;
import net.minecraftforge.client.model.obj.*;

public class ItemRendererArclamp implements IItemRenderer
{
    private void renderArclamp(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        switch (type) {
            case INVENTORY: {
                GL11.glScalef(0.9f, 0.9f, 0.9f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                GL11.glTranslatef(0.8f, 0.8f, 0.5f);
                GL11.glRotatef(150.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(0.7f, 0.7f, 0.7f);
                break;
            }
            case EQUIPPED: {
                GL11.glTranslatef(0.6f, 0.8f, 0.6f);
                GL11.glRotatef(150.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(0.9f, 0.9f, 0.9f);
                break;
            }
        }
        GL11.glScalef(0.07f, 0.07f, 0.07f);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TileEntityArclampRenderer.lampTexture);
        TileEntityArclampRenderer.lampMetal.renderAll();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TileEntityArclampRenderer.lightTexture);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(7);
        tessellator.setColorRGBA(255, 255, 255, 255);
        GL11.glDisable(2896);
        ((WavefrontObject)TileEntityArclampRenderer.lampLight).tessellateAll(tessellator);
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glEnable(2896);
    }
    
    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        switch (type) {
            case ENTITY: {
                return true;
            }
            case EQUIPPED: {
                return true;
            }
            case EQUIPPED_FIRST_PERSON: {
                return true;
            }
            case INVENTORY: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean shouldUseRenderHelper(final IItemRenderer.ItemRenderType type, final ItemStack item, final IItemRenderer.ItemRendererHelper helper) {
        return true;
    }
    
    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        switch (type) {
            case EQUIPPED: {
                this.renderArclamp(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderArclamp(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderArclamp(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderArclamp(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
}
