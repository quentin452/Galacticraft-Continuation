package micdoodle8.mods.galacticraft.planets.mars.inventory;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;

public class SlotSchematicTier2Rocket extends Slot
{
    private final int index;
    private final int x;
    private final int y;
    private final int z;
    private final EntityPlayer player;

    public SlotSchematicTier2Rocket(final IInventory par2IInventory, final int par3, final int par4, final int par5, final int x, final int y, final int z, final EntityPlayer player) {
        super(par2IInventory, par3, par4, par5);
        this.index = par3;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    public void onSlotChanged() {
        if (this.player instanceof EntityPlayerMP) {
            final Object[] toSend = { this.x, this.y, this.z };
            for (int var12 = 0; var12 < this.player.worldObj.playerEntities.size(); ++var12) {
                final EntityPlayerMP var13 = (EntityPlayerMP)  this.player.worldObj.playerEntities.get(var12);
                if (var13.dimension == this.player.worldObj.provider.dimensionId) {
                    final double var14 = this.x - var13.posX;
                    final double var15 = this.y - var13.posY;
                    final double var16 = this.z - var13.posZ;
                    if (var14 * var14 + var15 * var15 + var16 * var16 < 400.0) {
                        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_SPAWN_SPARK_PARTICLES, new Object[] { this.x, this.y, this.z }), var13);
                    }
                }
            }
        }
    }

    public boolean isItemValid(final ItemStack par1ItemStack) {
        switch (this.index) {
            case 1: {
                return par1ItemStack.getItem() == GCItems.partNoseCone;
            }
            case 2: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 3: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 4: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 5: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 6: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 7: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 8: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 9: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 10: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 11: {
                return par1ItemStack.getItem() == MarsItems.marsItemBasic && par1ItemStack.getItemDamage() == 3;
            }
            case 12: {
                return par1ItemStack.getItem() == GCItems.rocketEngine && par1ItemStack.getItemDamage() == 1;
            }
            case 13: {
                return par1ItemStack.getItem() == GCItems.partFins;
            }
            case 14: {
                return par1ItemStack.getItem() == GCItems.partFins;
            }
            case 15: {
                return par1ItemStack.getItem() == GCItems.rocketEngine && par1ItemStack.getItemDamage() == 0;
            }
            case 16: {
                return par1ItemStack.getItem() == GCItems.rocketEngine && par1ItemStack.getItemDamage() == 1;
            }
            case 17: {
                return par1ItemStack.getItem() == GCItems.partFins;
            }
            case 18: {
                return par1ItemStack.getItem() == GCItems.partFins;
            }
            case 19: {
                return true;
            }
            case 20: {
                return true;
            }
            case 21: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public int getSlotStackLimit() {
        return 1;
    }
}
