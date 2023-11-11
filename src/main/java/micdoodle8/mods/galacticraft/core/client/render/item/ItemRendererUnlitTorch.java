package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class ItemRendererUnlitTorch implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return false;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return Objects.requireNonNull(helper) == ItemRendererHelper.INVENTORY_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (Objects.requireNonNull(type) == ItemRenderType.INVENTORY) {
            this.renderTorchInInv();
        }
    }

    public void renderTorchInInv() {
        GL11.glPushMatrix();

        GL11.glPopMatrix();
    }
}
