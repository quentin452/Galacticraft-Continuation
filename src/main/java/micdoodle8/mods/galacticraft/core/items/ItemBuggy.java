package micdoodle8.mods.galacticraft.core.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class ItemBuggy extends Item implements IHoldableItem {

    public ItemBuggy(String assetName) {
        this.setUnlocalizedName(assetName);
        this.setTextureName("arrow");
        this.setMaxStackSize(1);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        for (int i = 0; i < 4; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        final float var4 = 1.0F;
        final float var5 = par3EntityPlayer.prevRotationPitch
            + (par3EntityPlayer.rotationPitch - par3EntityPlayer.prevRotationPitch) * var4;
        final float var6 = par3EntityPlayer.prevRotationYaw
            + (par3EntityPlayer.rotationYaw - par3EntityPlayer.prevRotationYaw) * var4;
        final double var7 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * var4;
        final double var9 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * var4
            + 1.62D
            - par3EntityPlayer.yOffset;
        final double var11 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * var4;
        final Vec3 var13 = Vec3.createVectorHelper(var7, var9, var11);
        final float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
        final float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
        final float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        final float var17 = MathHelper.sin(-var5 * 0.017453292F);
        final float var18 = var15 * var16;
        final float var20 = var14 * var16;
        final double var21 = 5.0D;
        final Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20 * var21);
        final MovingObjectPosition var24 = par2World.rayTraceBlocks(var13, var23, true);

        if (var24 == null) {
            return par1ItemStack;
        }
        final Vec3 var25 = par3EntityPlayer.getLook(var4);
        boolean var26 = false;
        final float var27 = 1.0F;
        final List<?> var28 = par2World.getEntitiesWithinAABBExcludingEntity(
            par3EntityPlayer,
            par3EntityPlayer.boundingBox.addCoord(var25.xCoord * var21, var25.yCoord * var21, var25.zCoord * var21)
                .expand(var27, var27, var27));
        int var29;

        for (var29 = 0; var29 < var28.size(); ++var29) {
            final Entity var30 = (Entity) var28.get(var29);

            if (var30.canBeCollidedWith()) {
                final float var31 = var30.getCollisionBorderSize();
                final AxisAlignedBB var32 = var30.boundingBox.expand(var31, var31, var31);

                if (var32.isVecInside(var13)) {
                    var26 = true;
                }
            }
        }

        if (!var26 && var24.typeOfHit == MovingObjectType.BLOCK) {
            var29 = var24.blockX;
            int var33 = var24.blockY;
            final int var34 = var24.blockZ;

            if (par2World.getBlock(var29, var33, var34) == Blocks.snow) {
                --var33;
            }

            final EntityBuggy var35 = new EntityBuggy(
                par2World,
                var29 + 0.5F,
                var33 + 1.0F,
                var34 + 0.5F,
                par1ItemStack.getItemDamage());

            if (!par2World.getCollidingBoundingBoxes(var35, var35.boundingBox.expand(-0.1D, -0.1D, -0.1D))
                .isEmpty()) {
                return par1ItemStack;
            }

            if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound()
                .hasKey("BuggyFuel")) {
                var35.buggyFuelTank.setFluid(
                    new FluidStack(
                        GalacticraftCore.fluidFuel,
                        par1ItemStack.getTagCompound()
                            .getInteger("BuggyFuel")));
            }

            if (!par2World.isRemote) {
                par2World.spawnEntityInWorld(var35);
            }

            if (!par3EntityPlayer.capabilities.isCreativeMode) {
                --par1ItemStack.stackSize;
            }
        }
        return par1ItemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer player, List<String> par2List, boolean b) {
        if (par1ItemStack.getItemDamage() != 0) {
            par2List.add(GCCoreUtil.translate("gui.buggy.storageSpace") + ": " + par1ItemStack.getItemDamage() * 18);
        }

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound()
            .hasKey("BuggyFuel")) {
            par2List.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": "
                    + par1ItemStack.getTagCompound()
                        .getInteger("BuggyFuel")
                    + " / "
                    + EntityBuggy.tankCapacity);
        }
    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player) {
        return true;
    }
}
