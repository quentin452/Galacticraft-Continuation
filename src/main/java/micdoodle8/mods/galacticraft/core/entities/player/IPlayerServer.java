package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public interface IPlayerServer
{
    void clonePlayer(final EntityPlayerMP p0, final EntityPlayer p1, final boolean p2);
    
    void updateRiddenPre(final EntityPlayerMP p0);
    
    void updateRiddenPost(final EntityPlayerMP p0);
    
    boolean mountEntity(final EntityPlayerMP p0, final Entity p1);
    
    void moveEntity(final EntityPlayerMP p0, final double p1, final double p2, final double p3);
    
    boolean wakeUpPlayer(final EntityPlayerMP p0, final boolean p1, final boolean p2, final boolean p3);
    
    float attackEntityFrom(final EntityPlayerMP p0, final DamageSource p1, final float p2);
    
    void knockBack(final EntityPlayerMP p0, final Entity p1, final float p2, final double p3, final double p4);
}
