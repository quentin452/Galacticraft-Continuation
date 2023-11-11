package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.*;
import net.minecraft.entity.player.*;

public class ItemRendererTier1Rocket implements IItemRenderer
{
    protected static final ResourceLocation chestTexture;
    protected EntitySpaceshipBase spaceship;
    protected ModelBase modelSpaceship;
    protected final ModelChest chestModel;
    protected static RenderItem drawItems;
    protected ResourceLocation texture;
    
    public ItemRendererTier1Rocket(final EntitySpaceshipBase spaceship, final ModelBase model, final ResourceLocation texture) {
        this.chestModel = new ModelChest();
        this.spaceship = spaceship;
        this.modelSpaceship = model;
        this.texture = texture;
    }
    
    protected void renderSpaceship(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        this.transform(item, type);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.texture);
        this.modelSpaceship.render((Entity)this.spaceship, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            final int index = Math.min(Math.max(item.getItemDamage(), 0), IRocketType.EnumRocketType.values().length - 1);
            if (IRocketType.EnumRocketType.values()[index].getInventorySpace() > 3) {
                final ModelChest modelChest = this.chestModel;
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemRendererTier1Rocket.chestTexture);
                GL11.glPushMatrix();
                GL11.glDisable(2929);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glScalef(0.5f, -0.5f, -0.5f);
                GL11.glTranslatef(1.5f, 1.95f, 1.7f);
                final short short1 = 0;
                GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(-1.5f, -1.5f, -1.5f);
                float f1 = 0.0f;
                f1 = 1.0f - f1;
                f1 = 1.0f - f1 * f1 * f1;
                modelChest.chestLid.rotateAngleX = -(f1 * 3.1415927f / 2.0f);
                modelChest.chestBelow.render(0.0625f);
                modelChest.chestLid.render(0.0625f);
                modelChest.chestKnob.render(0.0625f);
                GL11.glEnable(2929);
                GL11.glPopMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }
    
    public void transform(final ItemStack itemstack, final IItemRenderer.ItemRenderType type) {
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        long var10 = this.spaceship.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        final float var11 = (((var10 >> 16 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var12 = (((var10 >> 20 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var13 = (((var10 >> 24 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glRotatef(70.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-10.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(50.0f, 0.0f, 1.0f, 1.0f);
            GL11.glTranslatef(0.0f, 2.0f, 0.0f);
            GL11.glScalef(5.2f, 5.2f, 5.2f);
            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityTier1Rocket) {
                GL11.glScalef(0.0f, 0.0f, 0.0f);
            }
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(-0.5f, 4.2f, 0.0f);
            GL11.glRotatef(28.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(230.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(73.0f, 1.0f, 0.0f, 0.0f);
            GL11.glScalef(5.2f, 5.2f, 5.2f);
            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityTier1Rocket) {
                GL11.glScalef(0.0f, 0.0f, 0.0f);
            }
        }
        GL11.glTranslatef(var11, var12 - 0.1f, var13);
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glRotatef(85.0f, 1.0f, 0.0f, 1.0f);
                GL11.glRotatef(20.0f, 1.0f, 0.0f, 0.0f);
                GL11.glScalef(0.9f, 0.9f, 0.9f);
            }
            else {
                GL11.glTranslatef(0.0f, -0.9f, 0.0f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
            }
            GL11.glScalef(1.3f, 1.3f, 1.3f);
            GL11.glTranslatef(0.0f, -0.6f, 0.0f);
            GL11.glRotatef(Sys.getTime() / 30.0f % 360.0f, 0.0f, 1.0f, 0.0f);
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
                this.renderSpaceship(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderSpaceship(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderSpaceship(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderSpaceship(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
    
    static {
        chestTexture = new ResourceLocation("textures/entity/chest/normal.png");
        ItemRendererTier1Rocket.drawItems = new RenderItem();
    }
}
