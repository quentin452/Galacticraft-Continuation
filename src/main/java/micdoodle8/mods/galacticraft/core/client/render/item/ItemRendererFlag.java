package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import cpw.mods.fml.client.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.render.entities.*;
import net.minecraft.entity.*;

public class ItemRendererFlag implements IItemRenderer
{
    private EntityFlag entityFlagDummy;
    private ModelFlag modelFlag;
    
    public ItemRendererFlag() {
        this.entityFlagDummy = new EntityFlag((World)FMLClientHandler.instance().getClient().theWorld);
        this.modelFlag = new ModelFlag();
    }
    
    private void renderFlag(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ, final Object... data) {
        GL11.glPushMatrix();
        long var10 = this.entityFlagDummy.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        final float var11 = (((var10 >> 16 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var12 = (((var10 >> 20 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var13 = (((var10 >> 24 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        this.entityFlagDummy.worldObj = (World)FMLClientHandler.instance().getClient().theWorld;
        this.entityFlagDummy.ticksExisted = (int)FMLClientHandler.instance().getWorldClient().getTotalWorldTime();
        this.entityFlagDummy.setType(item.getItemDamage());
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            final EntityLivingBase entityHolding = (EntityLivingBase)data[1];
            if (entityHolding instanceof EntityPlayer) {
                final String playerName = ((EntityPlayer)entityHolding).getGameProfile().getName();
                if (!playerName.equals(this.entityFlagDummy.getOwner())) {
                    this.entityFlagDummy.setOwner(playerName);
                    this.entityFlagDummy.flagData = ClientUtil.updateFlagData(this.entityFlagDummy.getOwner(), true);
                }
            }
        }
        else {
            final String playerName2 = FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName();
            if (!playerName2.equals(this.entityFlagDummy.getOwner()) || this.entityFlagDummy.ticksExisted % 100 == 0) {
                this.entityFlagDummy.setOwner(playerName2);
                this.entityFlagDummy.flagData = ClientUtil.updateFlagData(this.entityFlagDummy.getOwner(), true);
            }
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(7.0f, 7.0f, 7.0f);
            GL11.glTranslatef(0.0f, 0.7f, 0.1f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glRotatef(170.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-10.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(-0.25f, 1.3f, 0.15f);
            GL11.glRotatef(-145.0f, 0.0f, 1.0f, 0.0f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON && FMLClientHandler.instance().getClient().thePlayer.getItemInUseCount() > 0) {
            final float var13b = item.getMaxItemUseDuration() - (FMLClientHandler.instance().getClient().thePlayer.getItemInUseCount() + 1.0f);
            float var14b = var13b / 20.0f;
            var14b = (var14b * var14b + var14b * 2.0f) / 3.0f;
            if (var14b > 1.0f) {
                var14b = 1.0f;
            }
            GL11.glRotatef(MathHelper.sin((var13b - 0.1f) * 0.3f) * 0.01f * (var14b - 0.1f) * 60.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(var14b * 60.0f, 1.0f, 0.0f, 1.0f);
            GL11.glTranslatef(0.0f, -(var14b * 0.2f), 0.0f);
        }
        GL11.glTranslatef(var11, var12 - 0.1f, var13);
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(1.1f, 1.137f, 1.1f);
                GL11.glRotatef(30.0f, 1.0f, 0.0f, 1.0f);
                GL11.glRotatef(110.0f, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(-0.5f, 0.3f, 0.0f);
            }
            else {
                GL11.glTranslatef(0.0f, -0.9f, 0.0f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
            }
            GL11.glScalef(1.3f, 1.3f, 1.3f);
            GL11.glTranslatef(0.0f, -0.6f, 0.0f);
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderFlag.flagTexture);
        this.modelFlag.render((Entity)this.entityFlagDummy, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
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
                this.renderFlag(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderFlag(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
            case INVENTORY: {
                this.renderFlag(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
            case ENTITY: {
                this.renderFlag(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f, data);
                break;
            }
        }
    }
}
