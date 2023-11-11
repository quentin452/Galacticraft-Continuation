package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;

public class ItemRendererMeteorChunk implements IItemRenderer
{
    private static final ResourceLocation meteorChunkTexture;
    private static final ResourceLocation meteorChunkHotTexture;
    private final IModelCustom meteorChunkModel;
    
    public ItemRendererMeteorChunk() {
        this.meteorChunkModel = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/meteorChunk.obj"));
    }
    
    private void renderMeteorChunk(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        GL11.glScalef(0.7f, 0.7f, 0.7f);
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(1.4f, 1.0f, 0.0f);
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(1.4f, 1.0f, 0.5f);
        }
        if (item.getItemDamage() == 0) {
            FMLClientHandler.instance().getClient().getTextureManager().bindTexture(ItemRendererMeteorChunk.meteorChunkTexture);
        }
        else {
            FMLClientHandler.instance().getClient().getTextureManager().bindTexture(ItemRendererMeteorChunk.meteorChunkHotTexture);
        }
        this.meteorChunkModel.renderAll();
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
                this.renderMeteorChunk(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderMeteorChunk(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderMeteorChunk(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderMeteorChunk(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
    
    static {
        meteorChunkTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/meteorChunk.png");
        meteorChunkHotTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/meteorChunkHot.png");
    }
}
