package micdoodle8.mods.galacticraft.core.entities.player;

import api.player.server.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class GCPlayerBaseMP extends ServerPlayerBase
{
    public GCPlayerBaseMP(final ServerPlayerAPI playerAPI) {
        super(playerAPI);
    }
    
    private IPlayerServer getClientHandler() {
        return GalacticraftCore.proxy.player;
    }
    
    public void clonePlayer(final EntityPlayer oldPlayer, final boolean keepInv) {
        super.clonePlayer(oldPlayer, keepInv);
        this.getClientHandler().clonePlayer(this.player, oldPlayer, keepInv);
    }
    
    public void moveEntity(final double par1, final double par3, final double par5) {
        super.moveEntity(par1, par3, par5);
        this.getClientHandler().moveEntity(this.player, par1, par3, par5);
    }
    
    public void wakeUpPlayer(final boolean par1, final boolean par2, final boolean par3) {
        if (!this.getClientHandler().wakeUpPlayer(this.player, par1, par2, par3)) {
            super.wakeUpPlayer(par1, par2, par3);
        }
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, float par2) {
        par2 = this.getClientHandler().attackEntityFrom(this.player, par1DamageSource, par2);
        return par2 != -1.0f && super.attackEntityFrom(par1DamageSource, par2);
    }
    
    public void knockBack(final Entity p_70653_1_, final float p_70653_2_, final double impulseX, final double impulseZ) {
        this.getClientHandler().knockBack(this.player, p_70653_1_, p_70653_2_, impulseX, impulseZ);
    }
}
