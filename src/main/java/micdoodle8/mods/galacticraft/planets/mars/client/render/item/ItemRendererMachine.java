package micdoodle8.mods.galacticraft.planets.mars.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.entity.player.*;

public class ItemRendererMachine implements IItemRenderer
{
    private static final ResourceLocation chamberTexture0;
    private static final ResourceLocation chamberTexture1;
    private IModelCustom model;
    
    public ItemRendererMachine(final IModelCustom model) {
        this.model = model;
    }
    
    private void renderCryogenicChamber(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        this.transform(type);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemRendererMachine.chamberTexture0);
        this.model.renderPart("Main_Cylinder");
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(0.1f, 0.6f, 0.5f, 0.4f);
        this.model.renderPart("Shield_Torus");
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    
    public void transform(final IItemRenderer.ItemRenderType type) {
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glRotatef(70.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-10.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(50.0f, 0.0f, 1.0f, 1.0f);
            GL11.glScalef(3.8f, 4.1f, 3.8f);
            GL11.glTranslatef(0.25f, 1.2f, 0.0f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.0f, -0.9f, 0.0f);
            GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(5.5f, 7.0f, -8.5f);
            GL11.glScalef(6.2f, 8.2f, 6.2f);
        }
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.0f, -1.9f, 0.0f);
                GL11.glScalef(0.7f, 0.6f, 0.7f);
                GL11.glRotatef(225.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glTranslatef(0.0f, -3.9f, 0.0f);
                GL11.glDisable(3042);
            }
            GL11.glScalef(1.3f, 1.3f, 1.3f);
        }
    }
    
    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        if (item.getItemDamage() < 4 || item.getItemDamage() >= 8) {
            return false;
        }
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
        if (item.getItemDamage() >= 4 && item.getItemDamage() < 8) {
            switch (type) {
                case EQUIPPED: {
                    this.renderCryogenicChamber(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                    break;
                }
                case EQUIPPED_FIRST_PERSON: {
                    this.renderCryogenicChamber(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                    break;
                }
                case INVENTORY: {
                    this.renderCryogenicChamber(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                    break;
                }
                case ENTITY: {
                    this.renderCryogenicChamber(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                    break;
                }
            }
        }
    }
    
    static {
        chamberTexture0 = new ResourceLocation("galacticraftmars", "textures/model/chamber_dark.png");
        chamberTexture1 = new ResourceLocation("galacticraftmars", "textures/model/chamber2_dark.png");
    }
}
