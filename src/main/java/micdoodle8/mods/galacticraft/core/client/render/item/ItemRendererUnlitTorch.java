package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;

public class ItemRendererUnlitTorch implements IItemRenderer
{
    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        return false;
    }
    
    public boolean shouldUseRenderHelper(final IItemRenderer.ItemRenderType type, final ItemStack item, final IItemRenderer.ItemRendererHelper helper) {
        switch (helper) {
            case INVENTORY_BLOCK: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        switch (type) {
            case INVENTORY: {
                this.renderTorchInInv();
                break;
            }
        }
    }
    
    public void renderTorchInInv() {
        GL11.glPushMatrix();
        GL11.glPopMatrix();
    }
}
