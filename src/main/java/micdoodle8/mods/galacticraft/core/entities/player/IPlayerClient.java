package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.client.entity.*;

public interface IPlayerClient
{
    void moveEntity(final EntityPlayerSP p0, final double p1, final double p2, final double p3);
    
    void onUpdate(final EntityPlayerSP p0);
    
    void onLivingUpdatePre(final EntityPlayerSP p0);
    
    void onLivingUpdatePost(final EntityPlayerSP p0);
    
    float getBedOrientationInDegrees(final EntityPlayerSP p0, final float p1);
    
    boolean isEntityInsideOpaqueBlock(final EntityPlayerSP p0, final boolean p1);
    
    boolean wakeUpPlayer(final EntityPlayerSP p0, final boolean p1, final boolean p2, final boolean p3);
    
    void onBuild(final int p0, final EntityPlayerSP p1);
}
