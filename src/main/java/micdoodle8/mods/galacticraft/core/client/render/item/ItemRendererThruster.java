package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityThrusterRenderer;

public class ItemRendererThruster implements IItemRenderer {

    private void renderThruster(ItemRenderType type) {
        GL11.glPushMatrix();

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(TileEntityThrusterRenderer.thrusterTexture);

        switch (type) {
            case INVENTORY:
                GL11.glTranslatef(-0.4F, -0.1F, 0.0F);
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                break;
            case EQUIPPED:
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(1.3F, 0.9F, 0.6F);
                GL11.glRotatef(150, 0, 1, 0);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                break;
            default:
                break;
        }

        GL11.glRotatef(180, 1, 0, 0);
        TileEntityThrusterRenderer.thrusterModel.renderAll();

        GL11.glPopMatrix();
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
                this.renderThruster(type);
                break;
            case EQUIPPED_FIRST_PERSON:
                this.renderThruster(type);
                break;
            case INVENTORY:
                this.renderThruster(type);
                break;
            case ENTITY:
                this.renderThruster(type);
                break;
            default:
        }
    }
}
