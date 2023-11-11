package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.client.render.tile.*;

public class ItemRendererScreen implements IItemRenderer
{
    private void renderScreen(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TileEntityScreenRenderer.blockTexture);
        switch (type) {
            case INVENTORY: {
                GL11.glTranslatef(-0.5f, 0.525f, -0.5f);
                break;
            }
            case EQUIPPED: {
                GL11.glTranslatef(1.0f, 1.0f, 0.0f);
                GL11.glRotatef(90.0f, 0.0f, -1.0f, 0.0f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                GL11.glTranslatef(0.2f, 0.9f, 1.2f);
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(1.3f, 1.3f, 1.3f);
                break;
            }
            default: {
                GL11.glTranslatef(-0.5f, 0.525f, -0.5f);
                break;
            }
        }
        GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
        TileEntityScreenRenderer.screenModel0.renderAll();
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
                this.renderScreen(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderScreen(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderScreen(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderScreen(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
}
