package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraftforge.client.*;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.*;
import net.minecraft.entity.player.*;

public class ItemRendererAstroMiner implements IItemRenderer
{
    protected IModelCustom modelMiner;
    protected IModelCustom modellasergl;
    protected IModelCustom modellasergr;
    protected static RenderItem drawItems;
    protected ResourceLocation texture;
    
    public ItemRendererAstroMiner() {
        this.texture = new ResourceLocation("galacticraftasteroids", "textures/model/astroMiner_off.png");
        this.modelMiner = RenderAstroMiner.modelObj;
        this.modellasergl = RenderAstroMiner.modellasergl;
        this.modellasergr = RenderAstroMiner.modellasergr;
    }
    
    protected void renderMiner(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        final boolean saveCullState = GL11.glIsEnabled(2884);
        GL11.glEnable(2884);
        GL11.glPushMatrix();
        this.transform(item, type);
        GL11.glScalef(0.06f, 0.06f, 0.06f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.texture);
        this.modelMiner.renderAll();
        GL11.glTranslatef(1.875f, 0.0f, 0.0f);
        this.modellasergl.renderAll();
        GL11.glTranslatef(-3.75f, 0.0f, 0.0f);
        this.modellasergr.renderAll();
        GL11.glPopMatrix();
        if (!saveCullState) {
            GL11.glDisable(2884);
        }
    }
    
    public void transform(final ItemStack itemstack, final IItemRenderer.ItemRenderType type) {
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glRotatef(25.0f, 0.0f, 0.0f, -1.0f);
            GL11.glRotatef(-31.6f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(15.4f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(1.1f, -1.4000001f, 0.31999993f);
            GL11.glScalef(4.6f, 4.6f, 4.6f);
            if (player != null && player.ridingEntity != null && (player.ridingEntity instanceof EntityAutoRocket || player.ridingEntity instanceof EntityLanderBase)) {
                GL11.glScalef(0.0f, 0.0f, 0.0f);
            }
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(-1.0f, 4.0f, 0.0f);
            GL11.glRotatef(30.0f, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(5.2f, 5.2f, 5.2f);
            if (player != null && player.ridingEntity != null && (player.ridingEntity instanceof EntityAutoRocket || player.ridingEntity instanceof EntityLanderBase)) {
                GL11.glScalef(0.0f, 0.0f, 0.0f);
            }
        }
        GL11.glTranslatef(0.0f, 0.1f, 0.0f);
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glRotatef(Sys.getTime() / 30.0f % 360.0f + 45.0f, 0.0f, 1.0f, 0.0f);
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.8f, 0.8f, 0.8f);
                GL11.glTranslatef(0.0f, 1.6f, -0.2f);
            }
            else {
                GL11.glTranslatef(0.0f, -0.9f, 0.0f);
                GL11.glScalef(0.7f, 0.7f, 0.7f);
            }
            GL11.glScalef(1.3f, 1.3f, 1.3f);
            GL11.glTranslatef(0.0f, -0.6f, 0.25f);
        }
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
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
                this.renderMiner(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderMiner(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderMiner(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderMiner(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
    
    static {
        ItemRendererAstroMiner.drawItems = new RenderItem();
    }
}
