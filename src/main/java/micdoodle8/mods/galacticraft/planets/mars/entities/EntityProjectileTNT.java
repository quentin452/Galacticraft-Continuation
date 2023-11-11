package micdoodle8.mods.galacticraft.planets.mars.entities;

import net.minecraft.entity.projectile.*;
import net.minecraft.world.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class EntityProjectileTNT extends EntityFireball
{
    public EntityProjectileTNT(final World par1World) {
        super(par1World);
        this.setSize(1.0f, 1.0f);
    }
    
    public EntityProjectileTNT(final World par1World, final EntityLivingBase par2EntityLivingBase, final double par3, final double par5, final double par7) {
        super(par1World, par2EntityLivingBase, par3, par5, par7);
        this.setSize(1.0f, 1.0f);
    }
    
    @SideOnly(Side.CLIENT)
    public EntityProjectileTNT(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.setSize(0.3125f, 0.3125f);
    }
    
    public boolean isBurning() {
        return false;
    }
    
    protected void onImpact(final MovingObjectPosition movingObjectPosition) {
        if (!this.worldObj.isRemote) {
            if (movingObjectPosition.entityHit != null && !(movingObjectPosition.entityHit instanceof EntityCreeper)) {
                movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage((EntityFireball)this, (Entity)this.shootingEntity), ConfigManagerCore.hardMode ? 12.0f : 6.0f);
            }
            this.worldObj.newExplosion((Entity)null, this.posX, this.posY, this.posZ, 1.0f, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
            this.setDead();
        }
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
}
