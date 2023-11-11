package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraftforge.client.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.util.*;

public class ItemRendererHeavyNoseCone implements IItemRenderer
{
    private void renderHeavyNoseCone(final IItemRenderer.ItemRenderType type, final RenderBlocks render, final ItemStack item, final float translateX, final float translateY, final float translateZ) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        for (int i = 0; i < 2; ++i) {
            GL11.glPushMatrix();
            if (i == 1) {
                final SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(FMLClientHandler.instance().getClientPlayerEntity().getGameProfile().getName());
                Vector3 color = null;
                if (race != null) {
                    color = race.getTeamColor();
                }
                if (color == null) {
                    color = new Vector3(1.0, 1.0, 1.0);
                }
                GL11.glColor4f(color.floatX(), color.floatY(), color.floatZ(), 1.0f);
            }
            final IIcon iicon = FMLClientHandler.instance().getClientPlayerEntity().getItemIcon(item, i);
            if (iicon == null) {
                GL11.glPopMatrix();
                return;
            }
            FMLClientHandler.instance().getClient().getTextureManager().bindTexture(FMLClientHandler.instance().getClient().getTextureManager().getResourceLocation(item.getItemSpriteNumber()));
            VersionUtil.setMipMap(false, false);
            final Tessellator tessellator = Tessellator.instance;
            final float f = iicon.getMinU();
            final float f2 = iicon.getMaxU();
            final float f3 = iicon.getMinV();
            final float f4 = iicon.getMaxV();
            final float f5 = 0.0f;
            final float f6 = 1.0f;
            GL11.glEnable(32826);
            GL11.glScalef(1.0f, -1.0f, 1.0f);
            final float f7 = 16.0f;
            GL11.glScalef(f7, f7, f7);
            GL11.glTranslatef(-f5, -f6, 0.0f);
            ItemRenderer.renderItemIn2D(tessellator, f2, f3, f, f4, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625f);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }
    
    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        switch (type) {
            case ENTITY: {
                return false;
            }
            case EQUIPPED: {
                return false;
            }
            case EQUIPPED_FIRST_PERSON: {
                return false;
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
        switch (helper) {
            case INVENTORY_BLOCK: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        switch (type) {
            case INVENTORY: {
                this.renderHeavyNoseCone(type, (RenderBlocks)data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            }
        }
    }
}
