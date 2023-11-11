package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.item.*;
import org.lwjgl.*;
import net.minecraft.util.*;
import cpw.mods.fml.client.*;

public class ItemRendererKey implements IItemRenderer
{
    private final ResourceLocation treasureChestTexture;
    ModelKey keyModel;
    
    public ItemRendererKey(final ResourceLocation resourceLocation) {
        this.keyModel = new ModelKey();
        this.treasureChestTexture = resourceLocation;
    }
    
    private void renderKey(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ, final Object... data) {
        GL11.glPushMatrix();
        EntityItem entityItem = null;
        if (data.length == 2 && data[1] instanceof EntityItem) {
            entityItem = (EntityItem)data[1];
        }
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glTranslatef(8.0f, 8.0f, 0.0f);
            GL11.glRotatef((MathHelper.sin(Sys.getTime() / 90.0f / 20.0f) - 55.0f) * 50.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(5.0f, 5.0f, 5.0f);
            GL11.glScalef(1.5f, 1.5f, 1.5f);
        }
        else if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glTranslatef(0.0f, 2.0f, 0.0f);
            GL11.glScalef(3.0f, 3.0f, 3.0f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glRotatef(100.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(60.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-10.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(0.4f, 0.1f, 0.5f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glRotatef(-4.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(2.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(3.0f, 2.0f, -0.6f);
            GL11.glScalef(3.0f, 3.0f, 3.0f);
        }
        GL11.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
        if (entityItem != null) {
            final float f2 = MathHelper.sin((entityItem.age + 1.0f) / 10.0f + entityItem.hoverStart) * 0.1f + 0.1f;
            GL11.glRotatef(f2 * 90.0f - 45.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef((float)(Math.sin((entityItem.age + 1) / 100.0f) * 180.0), 0.0f, 1.0f, 0.0f);
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.treasureChestTexture);
        this.keyModel.renderAll();
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
        return false;
    }
    
    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        switch (type) {
            case EQUIPPED: {
                this.renderKey(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderKey(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
            case INVENTORY: {
                this.renderKey(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
            case ENTITY: {
                this.renderKey(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
        }
    }
}
