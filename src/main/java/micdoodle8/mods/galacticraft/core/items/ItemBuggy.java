package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraftforge.fluids.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemBuggy extends Item implements IHoldableItem
{
    public ItemBuggy(final String assetName) {
        this.setUnlocalizedName(assetName);
        this.setTextureName("arrow");
        this.setMaxStackSize(1);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < 4; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        final float var4 = 1.0f;
        final float var5 = par3EntityPlayer.prevRotationPitch + (par3EntityPlayer.rotationPitch - par3EntityPlayer.prevRotationPitch) * 1.0f;
        final float var6 = par3EntityPlayer.prevRotationYaw + (par3EntityPlayer.rotationYaw - par3EntityPlayer.prevRotationYaw) * 1.0f;
        final double var7 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * 1.0;
        final double var8 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * 1.0 + 1.62 - par3EntityPlayer.yOffset;
        final double var9 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * 1.0;
        final Vec3 var10 = Vec3.createVectorHelper(var7, var8, var9);
        final float var11 = MathHelper.cos(-var6 * 0.017453292f - 3.1415927f);
        final float var12 = MathHelper.sin(-var6 * 0.017453292f - 3.1415927f);
        final float var13 = -MathHelper.cos(-var5 * 0.017453292f);
        final float var14 = MathHelper.sin(-var5 * 0.017453292f);
        final float var15 = var12 * var13;
        final float var16 = var11 * var13;
        final double var17 = 5.0;
        final Vec3 var18 = var10.addVector(var15 * 5.0, var14 * 5.0, var16 * 5.0);
        final MovingObjectPosition var19 = par2World.rayTraceBlocks(var10, var18, true);
        if (var19 == null) {
            return par1ItemStack;
        }
        final Vec3 var20 = par3EntityPlayer.getLook(1.0f);
        boolean var21 = false;
        final float var22 = 1.0f;
        final List<?> var23 = (List<?>)par2World.getEntitiesWithinAABBExcludingEntity((Entity)par3EntityPlayer, par3EntityPlayer.boundingBox.addCoord(var20.xCoord * 5.0, var20.yCoord * 5.0, var20.zCoord * 5.0).expand(1.0, 1.0, 1.0));
        for (int var24 = 0; var24 < var23.size(); ++var24) {
            final Entity var25 = (Entity)var23.get(var24);
            if (var25.canBeCollidedWith()) {
                final float var26 = var25.getCollisionBorderSize();
                final AxisAlignedBB var27 = var25.boundingBox.expand((double)var26, (double)var26, (double)var26);
                if (var27.isVecInside(var10)) {
                    var21 = true;
                }
            }
        }
        if (var21) {
            return par1ItemStack;
        }
        if (var19.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final int var24 = var19.blockX;
            int var28 = var19.blockY;
            final int var29 = var19.blockZ;
            if (par2World.getBlock(var24, var28, var29) == Blocks.snow) {
                --var28;
            }
            final EntityBuggy var30 = new EntityBuggy(par2World, (double)(var24 + 0.5f), (double)(var28 + 1.0f), (double)(var29 + 0.5f), par1ItemStack.getItemDamage());
            if (!par2World.getCollidingBoundingBoxes((Entity)var30, var30.boundingBox.expand(-0.1, -0.1, -0.1)).isEmpty()) {
                return par1ItemStack;
            }
            if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("BuggyFuel")) {
                var30.buggyFuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, par1ItemStack.getTagCompound().getInteger("BuggyFuel")));
            }
            if (!par2World.isRemote) {
                par2World.spawnEntityInWorld((Entity)var30);
            }
            if (!par3EntityPlayer.capabilities.isCreativeMode) {
                --par1ItemStack.stackSize;
            }
        }
        return par1ItemStack;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer player, final List par2List, final boolean b) {
        if (par1ItemStack.getItemDamage() != 0) {
            par2List.add(GCCoreUtil.translate("gui.buggy.storageSpace") + ": " + par1ItemStack.getItemDamage() * 18);
        }
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("BuggyFuel")) {
            par2List.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound().getInteger("BuggyFuel") + " / " + 1000);
        }
    }
    
    public boolean shouldHoldLeftHandUp(final EntityPlayer player) {
        return true;
    }
    
    public boolean shouldHoldRightHandUp(final EntityPlayer player) {
        return true;
    }
    
    public boolean shouldCrouch(final EntityPlayer player) {
        return true;
    }
}
