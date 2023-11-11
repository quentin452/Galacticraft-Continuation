package micdoodle8.mods.galacticraft.core.client;

import micdoodle8.mods.galacticraft.core.wrappers.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.*;

public class FootprintRenderer
{
    public Map<Long, List<Footprint>> footprints;
    private static final ResourceLocation footprintTexture;
    
    public FootprintRenderer() {
        this.footprints = new HashMap<Long, List<Footprint>>();
    }
    
    public void renderFootprints(final EntityPlayer player, final float partialTicks) {
        GL11.glPushMatrix();
        final double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(FootprintRenderer.footprintTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(2884);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        final Tessellator tessellator = Tessellator.instance;
        final float f7 = 1.0f;
        final float f8 = 0.0f;
        final float f9 = 0.0f;
        final float f10 = 1.0f;
        final float f11 = 0.4f;
        GL11.glAlphaFunc(516, 0.1f);
        for (final List<Footprint> footprintList : this.footprints.values()) {
            for (final Footprint footprint : footprintList) {
                if (footprint.dimension == player.worldObj.provider.dimensionId) {
                    GL11.glPushMatrix();
                    final float ageScale = footprint.age / 3200.0f;
                    tessellator.startDrawingQuads();
                    final float f12 = (float)(footprint.position.x - interpPosX);
                    final float f13 = (float)(footprint.position.y - interpPosY) + 0.001f;
                    final float f14 = (float)(footprint.position.z - interpPosZ);
                    GL11.glTranslatef(f12, f13, f14);
                    tessellator.setBrightness((int)(100.0f + ageScale * 155.0f));
                    GL11.glColor4f(1.0f - ageScale, 1.0f - ageScale, 1.0f - ageScale, 1.0f - ageScale);
                    final double footprintScale = 0.5;
                    tessellator.addVertexWithUV(0.0 + Math.sin((45.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, 0.0, 0.0 + Math.cos((45.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, (double)f7, (double)f10);
                    tessellator.addVertexWithUV(0.0 + Math.sin((135.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, 0.0, 0.0 + Math.cos((135.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, (double)f7, (double)f9);
                    tessellator.addVertexWithUV(0.0 + Math.sin((225.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, 0.0, 0.0 + Math.cos((225.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, (double)f8, (double)f9);
                    tessellator.addVertexWithUV(0.0 + Math.sin((315.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, 0.0, 0.0 + Math.cos((315.0f - footprint.rotation) * 3.141592653589793 / 180.0) * footprintScale, (double)f8, (double)f10);
                    tessellator.draw();
                    GL11.glPopMatrix();
                }
            }
        }
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glPopMatrix();
    }
    
    public void addFootprint(final long chunkKey, final Footprint footprint) {
        List<Footprint> footprintList = this.footprints.get(chunkKey);
        if (footprintList == null) {
            footprintList = new ArrayList<Footprint>();
        }
        footprintList.add(new Footprint(footprint.dimension, footprint.position, footprint.rotation, footprint.owner));
        this.footprints.put(chunkKey, footprintList);
    }
    
    public void addFootprint(final long chunkKey, final int dimension, final Vector3 position, final float rotation, final String owner) {
        this.addFootprint(chunkKey, new Footprint(dimension, position, rotation, owner));
    }
    
    public void setFootprints(final long chunkKey, final List<Footprint> prints) {
        List<Footprint> footprintList = this.footprints.get(chunkKey);
        if (footprintList == null) {
            footprintList = new ArrayList<Footprint>();
        }
        final Iterator<Footprint> i = footprintList.iterator();
        while (i.hasNext()) {
            final Footprint print = i.next();
            if (!print.owner.equals(FMLClientHandler.instance().getClient().thePlayer.getCommandSenderName())) {
                i.remove();
            }
        }
        footprintList.addAll(prints);
        this.footprints.put(chunkKey, footprintList);
    }
    
    static {
        footprintTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/footprint.png");
    }
}
