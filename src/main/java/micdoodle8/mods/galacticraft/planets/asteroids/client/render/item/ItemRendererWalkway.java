package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;

public class ItemRendererWalkway implements IItemRenderer
{
    private static final ResourceLocation textureMain;
    private static final ResourceLocation textureWire;
    private static final ResourceLocation texturePipe;
    public static IModelCustom modelWalkway;
    
    public ItemRendererWalkway() {
        ItemRendererWalkway.modelWalkway = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/walkway.obj"));
    }
    
    private void renderWalkway(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        this.transform(type);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemRendererWalkway.textureMain);
        ItemRendererWalkway.modelWalkway.renderPart("Walkway");
        if (item.getItem() == Item.getItemFromBlock(AsteroidBlocks.blockWalkway)) {
            ItemRendererWalkway.modelWalkway.renderPart("WalkwayBase");
        }
        else if (item.getItem() == Item.getItemFromBlock(AsteroidBlocks.blockWalkwayWire)) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemRendererWalkway.textureWire);
            ItemRendererWalkway.modelWalkway.renderPart("Wire");
        }
        else if (item.getItem() == Item.getItemFromBlock(AsteroidBlocks.blockWalkwayOxygenPipe)) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemRendererWalkway.texturePipe);
            ItemRendererWalkway.modelWalkway.renderPart("Pipe");
        }
        GL11.glPopMatrix();
    }
    
    public void transform(final IItemRenderer.ItemRenderType type) {
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.6f, 0.2f, 0.6f);
            GL11.glRotatef(185.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(40.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(-0.1f, 0.5f, 0.6f);
            GL11.glScalef(1.6f, 1.6f, 1.6f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        }
        GL11.glScalef(-0.2f, -0.2f, 0.2f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.0f, 4.0f, 0.1f);
                GL11.glScalef(2.1f, 2.1f, 2.1f);
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glScalef(1.8f, 1.8f, 1.8f);
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
                this.renderWalkway(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderWalkway(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderWalkway(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderWalkway(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
    
    static {
        textureMain = new ResourceLocation("galacticraftasteroids", "textures/blocks/walkway.png");
        textureWire = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/aluminumWire.png");
        texturePipe = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/pipe_oxygen_white.png");
    }
}
