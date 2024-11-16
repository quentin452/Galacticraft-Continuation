package micdoodle8.mods.galacticraft.planets.mars.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;

public class SlotSchematicCargoRocket extends Slot {

    private final int index;
    private final int x, y, z;
    private final EntityPlayer player;

    public SlotSchematicCargoRocket(IInventory par2IInventory, int par3, int par4, int par5, int x, int y, int z,
        EntityPlayer player) {
        super(par2IInventory, par3, par4, par5);
        this.index = par3;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    @Override
    public void onSlotChanged() {
        if (this.player instanceof EntityPlayerMP) {
            for (final Object element : this.player.worldObj.playerEntities) {
                final EntityPlayerMP var13 = (EntityPlayerMP) element;

                if (var13.dimension == this.player.worldObj.provider.dimensionId) {
                    final double var14 = this.x - var13.posX;
                    final double var16 = this.y - var13.posY;
                    final double var18 = this.z - var13.posZ;

                    if (var14 * var14 + var16 * var16 + var18 * var18 < 20 * 20) {
                        GalacticraftCore.packetPipeline.sendTo(
                            new PacketSimple(
                                EnumSimplePacket.C_SPAWN_SPARK_PARTICLES,
                                new Object[] { this.x, this.y, this.z }),
                            var13);
                    }
                }
            }
        }
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (this.index == 1) {
            return itemStack.getItem() == GCItems.basicItem && itemStack.getItemDamage() == 14;
        }
        if (this.index == 2 && GalacticraftCore.isGalaxySpaceLoaded) {
            return itemStack.getItem()
                == GameRegistry.findItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer")
                && itemStack.getItemDamage() == 101;
        }
        if (this.index >= 3 && this.index <= 5 && GalacticraftCore.isGalaxySpaceLoaded) {
            return itemStack.getItem()
                == GameRegistry.findItem(Constants.MOD_ID_GALAXYSPACE, "item.ModuleSmallFuelCanister");
        } else if (this.index == 7) {
            return itemStack.getItem() == GCItems.partNoseCone;
        } else if (this.index >= 8 && this.index <= 15) {
            return itemStack.getItem() == MarsItems.marsItemBasic && itemStack.getItemDamage() == 3;
        } else if (this.index == 16) {
            return itemStack.getItem() == GCItems.rocketEngine && itemStack.getItemDamage() == 0;
        } else if (this.index >= 17 && this.index <= 20) {
            return itemStack.getItem() == GCItems.partFins;
        } else if (this.index == 21) {
            return itemStack.getItem() == Item.getItemFromBlock(RecipeUtil.getChestBlock())
                && (itemStack.getItemDamage() == 0 || itemStack.getItemDamage() == 1 || itemStack.getItemDamage() == 3);
        } else {
            return false;
        }
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
