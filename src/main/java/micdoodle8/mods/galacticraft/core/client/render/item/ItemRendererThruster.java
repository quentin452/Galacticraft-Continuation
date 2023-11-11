package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.client.render.tile.*;

public class ItemRendererThruster implements IItemRenderer
{
    private void renderThruster(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TileEntityThrusterRenderer.thrusterTexture);
        switch (type) {
            case INVENTORY: {
                GL11.glTranslatef(-0.4f, -0.1f, 0.0f);
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                break;
            }
            case EQUIPPED: {
                GL11.glTranslatef(0.5f, 0.5f, 0.5f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                GL11.glTranslatef(1.3f, 0.9f, 0.6f);
                GL11.glRotatef(150.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(0.7f, 0.7f, 0.7f);
                break;
            }
        }
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        TileEntityThrusterRenderer.thrusterModel.renderAll();
        GL11.glPopMatrix();
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
                this.renderThruster(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderThruster(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderThruster(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderThruster(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
}
