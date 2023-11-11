package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.*;
import net.minecraft.entity.player.*;

public class ItemRendererShortRangeTelepad implements IItemRenderer
{
    private void renderBeamReceiver(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        this.transform(type);
        GL11.glDisable(3042);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityShortRangeTelepadRenderer.telepadTexture);
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("Base");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("Top");
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityShortRangeTelepadRenderer.telepadTexture0);
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxNegz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxNegz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxNegz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxMidz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxMidz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxMidz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxPosz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxPosz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxPosz");
        }
        else {
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("Base");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("Top");
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityShortRangeTelepadRenderer.telepadTexture0);
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxNegz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxNegz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxNegz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxMidz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxMidz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxMidz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxPosz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxPosz");
            TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxPosz");
        }
        GL11.glEnable(3042);
        GL11.glPopMatrix();
    }
    
    public void transform(final IItemRenderer.ItemRenderType type) {
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.55f, 0.45f, 0.6f);
            GL11.glRotatef(185.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(40.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(0.6f, 0.6f, 0.6f);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glRotatef(180.0f, 0.9f, 0.0f, 0.0f);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glTranslatef(1.0f, -0.6f, -0.5f);
        }
        GL11.glScalef(-0.4f, -0.4f, 0.4f);
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.0f, 1.6f, -0.0f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(10.0f, 1.0f, 0.0f, 1.0f);
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-100.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
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
                this.renderBeamReceiver(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                this.renderBeamReceiver(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case INVENTORY: {
                this.renderBeamReceiver(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
            case ENTITY: {
                this.renderBeamReceiver(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
}
