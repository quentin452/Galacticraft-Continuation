package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.*;
import org.lwjgl.*;

public class ItemRendererBeamReflector implements IItemRenderer
{
    private void renderBeamReflector(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        this.transform(type);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityBeamReflectorRenderer.reflectorTexture);
        TileEntityBeamReflectorRenderer.reflectorModel.renderAll();
        GL11.glPopMatrix();
    }
    
    public void transform(final IItemRenderer.ItemRenderType type) {
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.6f, 0.45f, 0.6f);
            GL11.glRotatef(185.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(40.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(2.2f, 2.2f, 2.2f);
            GL11.glTranslatef(0.291f, 0.2f, 0.3f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        }
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.0f, 1.45f, 0.0f);
                GL11.glScalef(2.0f, 2.0f, 2.0f);
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                GL11.glRotatef(Sys.getTime() / 90.0f % 360.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(2.0f, -2.0f, 2.0f);
            }
            GL11.glScalef(1.3f, 1.3f, 1.3f);
        }
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
                this.renderBeamReflector(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderBeamReflector(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderBeamReflector(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderBeamReflector(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
}
