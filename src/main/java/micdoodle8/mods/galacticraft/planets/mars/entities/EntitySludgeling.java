package micdoodle8.mods.galacticraft.planets.mars.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;

public class EntitySludgeling extends EntityMob implements IEntityBreathable {

    public EntitySludgeling(World par1World) {
        super(par1World);
        this.setSize(0.3F, 0.2F);
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 0.25F, true));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, true));
        this.targetTasks
            .addTask(2, new EntityAINearestAttackableTarget(this, EntityEvolvedZombie.class, 0, false, true));
        this.targetTasks
            .addTask(2, new EntityAINearestAttackableTarget(this, EntityEvolvedSkeleton.class, 0, false, true));
        this.targetTasks
            .addTask(2, new EntityAINearestAttackableTarget(this, EntityEvolvedSpider.class, 0, false, true));
        this.targetTasks
            .addTask(2, new EntityAINearestAttackableTarget(this, EntityEvolvedCreeper.class, 0, false, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntitySlimeling.class, 200, false));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(7.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(1.0F);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected Entity findPlayerToAttack() {
        final double var1 = 8.0D;
        return this.worldObj.getClosestVulnerablePlayerToEntity(this, var1);
    }

    @Override
    protected String getLivingSound() {
        return "mob.silverfish.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.silverfish.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfish.kill";
    }

    public EntityPlayer getClosestEntityToAttack(double par1, double par3, double par5, double par7) {
        double var9 = -1.0D;
        EntityPlayer var11 = null;

        for (final Object element : this.worldObj.loadedEntityList) {
            final EntityPlayer var13 = (EntityPlayer) element;
            final double var14 = var13.getDistanceSq(par1, par3, par5);

            if ((par7 < 0.0D || var14 < par7 * par7) && (var9 == -1.0D || var14 < var9)) {
                var9 = var14;
                var11 = var13;
            }
        }

        return var11;
    }

    @Override
    protected void attackEntity(Entity par1Entity, float par2) {
        if (this.attackTime <= 0 && par2 < 1.2F
            && par1Entity.boundingBox.maxY > this.boundingBox.minY
            && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), par2);
        }
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        this.worldObj.playSoundAtEntity(this, "mob.silverfish.step", 1.0F, 1.0F);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.air);
    }

    @Override
    public void onUpdate() {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (super.getCanSpawnHere()) {
            final EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity(this, 5.0D);
            return var1 == null;
        }
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean canBreath() {
        return true;
    }
}
