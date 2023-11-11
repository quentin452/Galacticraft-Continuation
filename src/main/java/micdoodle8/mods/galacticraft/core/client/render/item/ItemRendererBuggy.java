package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import cpw.mods.fml.client.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;

public class ItemRendererBuggy implements IItemRenderer
{
    private static final ResourceLocation buggyTextureBody;
    private static final ResourceLocation buggyTextureWheel;
    private static final ResourceLocation buggyTextureStorage;
    EntityBuggy spaceship;
    private final IModelCustom modelBuggy;
    private final IModelCustom modelBuggyWheelRight;
    private final IModelCustom modelBuggyWheelLeft;
    
    public ItemRendererBuggy() {
        this.spaceship = new EntityBuggy((World)FMLClientHandler.instance().getClient().theWorld);
        this.modelBuggy = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/buggy.obj"));
        this.modelBuggyWheelRight = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/buggyWheelRight.obj"));
        this.modelBuggyWheelLeft = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/buggyWheelLeft.obj"));
    }
    
    private void renderPipeItem(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        long var10 = this.spaceship.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        final float var11 = (((var10 >> 16 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var12 = (((var10 >> 20 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var13 = (((var10 >> 24 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        GL11.glScalef(0.75f, 0.75f, 0.75f);
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glRotatef(150.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(2.2f, 2.2f, 2.2f);
            GL11.glTranslatef(0.0f, -0.65f, 0.9f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.0f, 1.0f, 0.0f);
        }
        GL11.glTranslatef(var11, var12 - 0.1f, var13);
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.5f, 0.35f, 0.5f);
            }
            else {
                GL11.glTranslatef(0.0f, -0.9f, 0.0f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
            }
            GL11.glScalef(1.5f, 1.5f, 1.5f);
            GL11.glTranslatef(0.0f, 1.6f, 0.0f);
            GL11.glRotatef(-45.0f, 0.0f, 1.0f, 0.0f);
        }
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(ItemRendererBuggy.buggyTextureWheel);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, -2.6f);
        GL11.glTranslatef(1.4f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRightCover_Cover");
        GL11.glTranslatef(-2.8f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeftCover_Cover");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, 3.7f);
        GL11.glTranslatef(2.0f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRightCover_Cover");
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeftCover_Cover");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, -2.7f);
        GL11.glTranslatef(1.4f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRight_Wheel");
        GL11.glTranslatef(-2.8f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeft_Wheel");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, 3.6f);
        GL11.glTranslatef(2.0f, 0.0f, 0.0f);
        this.modelBuggyWheelRight.renderPart("WheelRight_Wheel");
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        this.modelBuggyWheelLeft.renderPart("WheelLeft_Wheel");
        GL11.glPopMatrix();
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(ItemRendererBuggy.buggyTextureBody);
        this.modelBuggy.renderPart("MainBody");
        GL11.glPushMatrix();
        GL11.glTranslatef(-1.178f, 4.1f, -2.397f);
        this.modelBuggy.renderPart("RadarDish_Dish");
        GL11.glPopMatrix();
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(ItemRendererBuggy.buggyTextureStorage);
        if (item.getItemDamage() > 0) {
            this.modelBuggy.renderPart("CargoLeft");
            if (item.getItemDamage() > 1) {
                this.modelBuggy.renderPart("CargoMid");
                if (item.getItemDamage() > 2) {
                    this.modelBuggy.renderPart("CargoRight");
                }
            }
        }
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
                this.renderPipeItem(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderPipeItem(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderPipeItem(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderPipeItem(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
    
    static {
        buggyTextureBody = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/buggyMain.png");
        buggyTextureWheel = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/buggyWheels.png");
        buggyTextureStorage = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/buggyStorage.png");
    }
}
