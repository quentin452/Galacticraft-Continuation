package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.server.*;
import net.minecraft.world.*;
import com.mojang.authlib.*;
import net.minecraft.server.management.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.world.*;

public class GCEntityPlayerMP extends EntityPlayerMP
{
    public GCEntityPlayerMP(final MinecraftServer server, final WorldServer world, final GameProfile profile, final ItemInWorldManager itemInWorldManager) {
        super(server, WorldUtil.getStartWorld(world), profile, itemInWorldManager);
        if (this.worldObj != world) {
            GCPlayerStats.get(this).startAdventure(WorldUtil.getDimensionName(this.worldObj.provider));
        }
    }
    
    public void clonePlayer(final EntityPlayer oldPlayer, final boolean keepInv) {
        super.clonePlayer(oldPlayer, keepInv);
        GalacticraftCore.proxy.player.clonePlayer(this, oldPlayer, keepInv);
        TileEntityTelemetry.updateLinkedPlayer((EntityPlayerMP)oldPlayer, this);
    }
    
    public void updateRidden() {
        GalacticraftCore.proxy.player.updateRiddenPre(this);
        super.updateRidden();
        GalacticraftCore.proxy.player.updateRiddenPost(this);
    }
    
    public void mountEntity(final Entity par1Entity) {
        if (!GalacticraftCore.proxy.player.mountEntity(this, par1Entity)) {
            super.mountEntity(par1Entity);
        }
    }
    
    public void moveEntity(final double par1, final double par3, final double par5) {
        super.moveEntity(par1, par3, par5);
        GalacticraftCore.proxy.player.moveEntity(this, par1, par3, par5);
    }
    
    public void wakeUpPlayer(final boolean par1, final boolean par2, final boolean par3) {
        if (!GalacticraftCore.proxy.player.wakeUpPlayer(this, par1, par2, par3)) {
            super.wakeUpPlayer(par1, par2, par3);
        }
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, float par2) {
        par2 = GalacticraftCore.proxy.player.attackEntityFrom(this, par1DamageSource, par2);
        return par2 != -1.0f && super.attackEntityFrom(par1DamageSource, par2);
    }
    
    public void knockBack(final Entity p_70653_1_, final float p_70653_2_, final double impulseX, final double impulseZ) {
        GalacticraftCore.proxy.player.knockBack(this, p_70653_1_, p_70653_2_, impulseX, impulseZ);
    }
    
    public void setInPortal() {
        if (!(this.worldObj.provider instanceof IGalacticraftWorldProvider)) {
            super.setInPortal();
        }
    }
}
