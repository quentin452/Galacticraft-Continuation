package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.client.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import org.lwjgl.*;
import net.minecraft.entity.player.*;

public class ItemRendererGrappleHook implements IItemRenderer
{
    public static final ResourceLocation grappleTexture;
    public static IModelCustom modelGrapple;
    
    public ItemRendererGrappleHook(final IModelCustom modelGrapple) {
        ItemRendererGrappleHook.modelGrapple = modelGrapple;
    }
    
    private void renderGrappleGun(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glPushMatrix();
            GL11.glScalef(0.7f, 0.75f, 0.5f);
            GL11.glTranslatef(0.5f, -0.2f, -0.5f);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glEnable(3042);
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            RenderManager.instance.itemRenderer.renderItem((EntityLivingBase)FMLClientHandler.instance().getClientPlayerEntity(), new ItemStack(Items.string, 1), 0, IItemRenderer.ItemRenderType.INVENTORY);
            GL11.glEnable(32826);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
        GL11.glPushMatrix();
        this.transform(type);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemRendererGrappleHook.grappleTexture);
        ItemRendererGrappleHook.modelGrapple.renderAll();
        GL11.glPopMatrix();
    }
    
    public void transform(final IItemRenderer.ItemRenderType type) {
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.5f, 0.6f, 0.5f);
            GL11.glRotatef(185.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(40.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-70.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(3.2f, 3.2f, 3.2f);
            GL11.glTranslatef(0.0f, 0.2f, 0.0f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(8.2f, 8.2f, 8.2f);
            GL11.glTranslatef(0.291f, 0.1f, -0.4f);
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(136.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-5.0f, 0.0f, 0.0f, 1.0f);
        }
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(1.55f, 1.55f, 1.55f);
                GL11.glRotatef(170.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(95.0f, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(-0.5f, 0.0f, 0.0f);
            }
            else {
                GL11.glTranslatef(0.0f, -0.5f, 0.0f);
                GL11.glRotatef(Sys.getTime() / 90.0f % 360.0f, 0.0f, 1.0f, 0.0f);
            }
            GL11.glScalef(1.3f, 1.3f, 1.3f);
        }
        GL11.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        GL11.glTranslatef(-0.4f, 0.0f, 0.0f);
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
                this.renderGrappleGun(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderGrappleGun(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderGrappleGun(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderGrappleGun(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
    
    static {
        grappleTexture = new ResourceLocation("galacticraftasteroids", "textures/model/grapple.png");
    }
}
