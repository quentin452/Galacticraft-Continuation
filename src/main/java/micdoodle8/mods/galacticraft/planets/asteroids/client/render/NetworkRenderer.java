package micdoodle8.mods.galacticraft.planets.asteroids.client.render;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import java.util.*;
import net.minecraft.client.entity.*;

public class NetworkRenderer
{
    public static void renderNetworks(final World world, final float partialTicks) {
        final List<TileEntityBeamOutput> nodes = new ArrayList<TileEntityBeamOutput>();
        for (final Object o : new ArrayList<Object>(world.loadedTileEntityList)) {
            if (o instanceof TileEntityBeamOutput) {
                nodes.add((TileEntityBeamOutput)o);
            }
        }
        if (nodes.isEmpty()) {
            return;
        }
        final Tessellator tess = Tessellator.instance;
        final EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
        final double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        for (final TileEntityBeamOutput tileEntity : nodes) {
            if (tileEntity.getTarget() == null) {
                continue;
            }
            GL11.glPushMatrix();
            final Vector3 outputPoint = tileEntity.getOutputPoint(true);
            final Vector3 targetInputPoint = tileEntity.getTarget().getInputPoint();
            final Vector3 direction = Vector3.subtract(outputPoint, targetInputPoint);
            final float directionLength = (float)direction.getMagnitude();
            final float posX = (float)(tileEntity.xCoord - interpPosX);
            final float posY = (float)(tileEntity.yCoord - interpPosY);
            final float posZ = (float)(tileEntity.zCoord - interpPosZ);
            GL11.glTranslatef(posX, posY, posZ);
            GL11.glTranslatef(outputPoint.floatX() - tileEntity.xCoord, outputPoint.floatY() - tileEntity.yCoord, outputPoint.floatZ() - tileEntity.zCoord);
            GL11.glRotatef(tileEntity.yaw + 180.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-tileEntity.pitch, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef((float)(tileEntity.ticks * 10), 0.0f, 0.0f, 1.0f);
            GL11.glColor4f(tileEntity.getColor().floatX(), tileEntity.getColor().floatY(), tileEntity.getColor().floatZ(), 1.0f);
            tess.startDrawing(1);
            for (final ForgeDirection dir : ForgeDirection.values()) {
                tess.addVertex((double)(dir.offsetX / 40.0f), (double)(dir.offsetY / 40.0f), (double)(dir.offsetZ / 40.0f));
                tess.addVertex((double)(dir.offsetX / 40.0f), (double)(dir.offsetY / 40.0f), (double)(directionLength + dir.offsetZ / 40.0f));
            }
            tess.draw();
            GL11.glPopMatrix();
        }
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
