package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.TileEntityBeamReflectorRenderer;

public class ItemRendererBeamReflector implements IItemRenderer {

    private void renderBeamReflector(ItemRenderType type) {
        GL11.glPushMatrix();
        this.transform(type);
        FMLClientHandler.instance()
            .getClient().renderEngine.bindTexture(TileEntityBeamReflectorRenderer.reflectorTexture);
        TileEntityBeamReflectorRenderer.reflectorModel.renderAll();
        GL11.glPopMatrix();
    }

    public void transform(ItemRenderType type) {
        if (type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.6F, 0.45F, 0.6F);
            GL11.glRotatef(185, 1, 0, 0);
            GL11.glRotatef(40, 0, 1, 0);
            GL11.glRotatef(0, 0, 0, 1);
            GL11.glScalef(2.0F, 2.0F, 2.0F);
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(2.2F, 2.2F, 2.2F);
            GL11.glTranslatef(0.291F, 0.2F, 0.3F);
            GL11.glRotatef(180.0F, 0.0F, 0F, 1F);
        }

        GL11.glScalef(-0.4F, -0.4F, 0.4F);

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) {
            if (type == ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.0F, 1.45F, 0.0F);
                GL11.glScalef(2.0F, 2.0F, 2.0F);
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glRotatef(180, 0, 1, 0);
            } else {
                GL11.glRotatef(Sys.getTime() / 90F % 360F, 0F, 1F, 0F);
                GL11.glScalef(2F, -2F, 2F);
            }

            GL11.glScalef(1.3F, 1.3F, 1.3F);
        }
    }

    /**
     * IItemRenderer implementation *
     */
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        switch (type) {
            case ENTITY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case INVENTORY:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case EQUIPPED:
                this.renderBeamReflector(type);
                break;
            case EQUIPPED_FIRST_PERSON:
                this.renderBeamReflector(type);
                break;
            case INVENTORY:
                this.renderBeamReflector(type);
                break;
            case ENTITY:
                this.renderBeamReflector(type);
                break;
            default:
                break;
        }
    }
}
