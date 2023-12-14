package micdoodle8.mods.galacticraft.core.mixins.itemphysicfull;

import com.creativemd.itemphysic.ItemDummyContainer;
import com.creativemd.itemphysic.physics.ServerPhysic;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(ServerPhysic.class)
public class PhysicFullCompatMixin {
    @Shadow
    public static Random random = new Random();

    @Shadow
    public static ArrayList swimItem = new ArrayList();
    @Shadow
    public static ArrayList burnItem = new ArrayList();
    @Shadow
    public static double lastPosY;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void update(EntityItem item) {
        ItemStack stack = item.getDataWatcher().getWatchableObjectItemStack(10);

        if (stack == null || stack.getItem() == null || !stack.getItem().onEntityItemUpdate(item)) {
            if (item.getEntityItem() == null) {
                item.setDead();
            } else {
                item.onEntityUpdate();
                if (item.delayBeforeCanPickup > 0) {
                    --item.delayBeforeCanPickup;
                }

                item.prevPosX = item.posX;
                item.prevPosY = item.posY;
                item.prevPosZ = item.posZ;
                float f = 0.98F;
                Fluid fluid = getFluid(item);
                if (fluid == null) {
                    item.motionY -= 0.04;
                } else {
                    double density = (double) fluid.getDensity() / 1000.0;
                    double speed = -1.0 / density * 0.01;
                    if (canItemSwim(stack)) {
                        speed = 0.05;
                    }

                    double speedreduction = (speed - item.motionY) / 2.0;
                    double maxSpeedReduction = 0.05;
                    if (speedreduction < -maxSpeedReduction) {
                        speedreduction = -maxSpeedReduction;
                    }

                    if (speedreduction > maxSpeedReduction) {
                        speedreduction = maxSpeedReduction;
                    }

                    item.motionY += speedreduction;
                    f = (float) (1.0 / density / 1.2);
                }

                item.noClip = func_145771_j(item, item.posX, (item.boundingBox.minY + item.boundingBox.maxY) / 2.0, item.posZ);
                item.moveEntity(item.motionX, item.motionY, item.motionZ);
                boolean flag = (int) item.prevPosX != (int) item.posX || (int) item.prevPosY != (int) item.posY || (int) item.prevPosZ != (int) item.posZ;
                if (flag || item.ticksExisted % 25 == 0) {
                    if (item.worldObj.getBlock(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.posY), MathHelper.floor_double(item.posZ)).getMaterial() == Material.lava && canItemBurn(stack)) {
                        item.playSound("random.fizz", 0.4F, 2.0F + random.nextFloat() * 0.4F);

                        for (int zahl = 0; zahl < 100; ++zahl) {
                            item.worldObj.spawnParticle("smoke", item.posX, item.posY, item.posZ, (double) random.nextFloat() * 0.1 - 0.05, 0.2 * random.nextDouble(), (double) random.nextFloat() * 0.1 - 0.05);
                        }
                    }

                    if (!item.worldObj.isRemote) {
                        searchForOtherItemsNearby(item);
                    }
                }

                if (item.onGround) {
                    f = item.worldObj.getBlock(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.boundingBox.minY) - 1, MathHelper.floor_double(item.posZ)).slipperiness * 0.98F;
                }

                item.motionX *= (double) f;
                item.motionZ *= (double) f;
                if (fluid == null) {
                    item.motionY *= 0.98;
                    if (item.onGround) {
                        item.motionY *= -0.5;
                    }
                }

                if (item.age < 1 && item.lifespan == 6000) {
                    item.lifespan = ItemDummyContainer.despawnItem;
                }

                ++item.age;
                if (!item.worldObj.isRemote && item.age >= item.lifespan) {
                    if (stack != null) {
                        ItemExpireEvent event = new ItemExpireEvent(item, stack.getItem() == null ? 6000 : stack.getItem().getEntityLifespan(stack, item.worldObj));
                        if (MinecraftForge.EVENT_BUS.post(event)) {
                            item.lifespan += event.extraLife;
                        } else {
                            item.setDead();
                        }
                    } else {
                        item.setDead();
                    }
                }

                if (stack != null && stack.stackSize <= 0) {
                    item.setDead();
                }
            }
        }

        // Gravity calculation
        double gravity = galacticraft_Continuation$getItemGravity(item);
        item.motionY -= gravity;

        if (item.onGround) {
            item.motionY = 0;
        }
    }

    @Unique
    private static double galacticraft_Continuation$getItemGravity(EntityItem e) {
        if (e.worldObj.provider instanceof IGalacticraftWorldProvider) {
            IGalacticraftWorldProvider customProvider = (IGalacticraftWorldProvider) e.worldObj.provider;
            double gravity = 0.03999999910593033D;

            if (customProvider instanceof IOrbitDimension) {
                gravity = Math.max(0.002D, gravity - 0.05999999910593033D / 1.75D);
            } else {
                gravity = Math.max(0.002D, gravity - customProvider.getGravity() / 1.75D);
            }

            return gravity;
        }

        return 0.03999999910593033D;
    }
    @Shadow
    private static void searchForOtherItemsNearby(EntityItem item) {
        Iterator iterator = item.worldObj.getEntitiesWithinAABB(EntityItem.class, item.boundingBox.expand(0.5, 0.0, 0.5)).iterator();

        while(iterator.hasNext()) {
            EntityItem entityitem = (EntityItem)iterator.next();
            item.combineItems(entityitem);
        }

    }

    @Shadow
    public static boolean func_145771_j(EntityItem item, double p_145771_1_, double p_145771_3_, double p_145771_5_) {
        int i = MathHelper.floor_double(p_145771_1_);
        int j = MathHelper.floor_double(p_145771_3_);
        int k = MathHelper.floor_double(p_145771_5_);
        double d3 = p_145771_1_ - (double)i;
        double d4 = p_145771_3_ - (double)j;
        double d5 = p_145771_5_ - (double)k;
        List list = item.worldObj.func_147461_a(item.boundingBox);
        if (list.isEmpty() && !item.worldObj.func_147469_q(i, j, k)) {
            return false;
        } else {
            boolean flag = !item.worldObj.func_147469_q(i - 1, j, k);
            boolean flag1 = !item.worldObj.func_147469_q(i + 1, j, k);
            boolean flag2 = !item.worldObj.func_147469_q(i, j - 1, k);
            boolean flag3 = !item.worldObj.func_147469_q(i, j + 1, k);
            boolean flag4 = !item.worldObj.func_147469_q(i, j, k - 1);
            boolean flag5 = !item.worldObj.func_147469_q(i, j, k + 1);
            byte b0 = 3;
            double d6 = 9999.0;
            if (flag && d3 < d6) {
                d6 = d3;
                b0 = 0;
            }

            if (flag1 && 1.0 - d3 < d6) {
                d6 = 1.0 - d3;
                b0 = 1;
            }

            if (flag3 && 1.0 - d4 < d6) {
                d6 = 1.0 - d4;
                b0 = 3;
            }

            if (flag4 && d5 < d6) {
                d6 = d5;
                b0 = 4;
            }

            if (flag5 && 1.0 - d5 < d6) {
                d6 = 1.0 - d5;
                b0 = 5;
            }

            float f = random.nextFloat() * 0.2F + 0.1F;
            if (b0 == 0) {
                item.motionX = (double)(-f);
            }

            if (b0 == 1) {
                item.motionX = (double)f;
            }

            if (b0 == 2) {
                item.motionY = (double)(-f);
            }

            if (b0 == 3) {
                item.motionY = (double)f;
            }

            if (b0 == 4) {
                item.motionZ = (double)(-f);
            }

            if (b0 == 5) {
                item.motionZ = (double)f;
            }

            return true;
        }
    }
    @Shadow
    public static Fluid getFluid(EntityItem item) {
        return getFluid(item, false);
    }
    @Shadow
    public static Fluid getFluid(EntityItem item, boolean below) {
        double d0 = item.posY + (double)item.getEyeHeight();
        int i = MathHelper.floor_double(item.posX);
        int j = MathHelper.floor_float((float)MathHelper.floor_double(d0));
        if (below) {
            --j;
        }

        int k = MathHelper.floor_double(item.posZ);
        Block block = item.worldObj.getBlock(i, j, k);
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid == null && block instanceof IFluidBlock) {
            fluid = ((IFluidBlock)block).getFluid();
        } else if (block instanceof BlockLiquid) {
            fluid = FluidRegistry.WATER;
        }

        if (below) {
            return fluid;
        } else {
            double filled = 1.0;
            if (block instanceof IFluidBlock) {
                filled = (double)((IFluidBlock)block).getFilledPercentage(item.worldObj, i, j, k);
            }

            if (filled < 0.0) {
                filled *= -1.0;
                if (d0 > (double)j + (1.0 - filled)) {
                    return fluid;
                }
            } else if (d0 < (double)j + filled) {
                return fluid;
            }

            return null;
        }
    }
    @Shadow
    public static boolean canItemSwim(ItemStack stack) {
        return contains(swimItem, stack);
    }

   @Shadow
   public static boolean contains(ArrayList list, ItemStack stack) {
       if (stack != null && stack.getItem() != null) {
           Object object = stack.getItem();
           Material material = null;
           if (object instanceof ItemBlock) {
               object = Block.getBlockFromItem((Item)object);
               material = ((Block)object).getMaterial();
           }

           int[] ores = OreDictionary.getOreIDs(stack);

           for(int i = 0; i < list.size(); ++i) {
               if (list.get(i) instanceof ItemStack && ItemStack.areItemStacksEqual(stack, (ItemStack)list.get(i))) {
                   return true;
               }

               if (list.get(i) == object) {
                   return true;
               }

               if (list.get(i) == material) {
                   return true;
               }

               if (list.get(i) instanceof String) {
                   for(int j = 0; j < ores.length; ++j) {
                       if (OreDictionary.getOreName(ores[j]).contains((CharSequence)list.get(i))) {
                           return true;
                       }
                   }
               }
           }

           return false;
       } else {
           return false;
       }
   }
   @Shadow
   public static boolean canItemBurn(ItemStack stack) {
       return TileEntityFurnace.isItemFuel(stack) ? true : contains(burnItem, stack);
   }
}
