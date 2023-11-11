package micdoodle8.mods.galacticraft.planets.mars.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import org.lwjgl.opengl.*;
import net.minecraft.tileentity.*;

@SideOnly(Side.CLIENT)
public class TileEntityCryogenicChamberRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation chamberTexture0;
    private static final ResourceLocation chamberTexture1;
    private final IModelCustom model;
    
    public TileEntityCryogenicChamberRenderer(final IModelCustom model) {
        this.model = model;
    }
    
    public void renderCryogenicChamber(final TileEntityCryogenicChamber chamber, final double par2, final double par4, final double par6, final float par8) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef((float)par2 + 0.5f, (float)par4, (float)par6 + 0.5f);
        float rotation = 0.0f;
        switch (chamber.getBlockMetadata() - 4) {
            case 0: {
                rotation = 180.0f;
                break;
            }
            case 1: {
                rotation = 0.0f;
                break;
            }
            case 2: {
                rotation = 270.0f;
                break;
            }
            case 3: {
                rotation = 90.0f;
                break;
            }
        }
        GL11.glScalef(0.5f, 0.6f, 0.5f);
        GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, 0.0f, 0.0f);
        this.bindTexture(TileEntityCryogenicChamberRenderer.chamberTexture0);
        this.model.renderPart("Main_Cylinder");
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(0.1f, 0.6f, 0.5f, 0.4f);
        this.model.renderPart("Shield_Torus");
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void renderTileEntityAt(final TileEntity par1TileEntity, final double par2, final double par4, final double par6, final float par8) {
        this.renderCryogenicChamber((TileEntityCryogenicChamber)par1TileEntity, par2, par4, par6, par8);
    }
    
    static {
        chamberTexture0 = new ResourceLocation("galacticraftmars", "textures/model/chamber_dark.png");
        chamberTexture1 = new ResourceLocation("galacticraftmars", "textures/model/chamber2_dark.png");
    }
}
