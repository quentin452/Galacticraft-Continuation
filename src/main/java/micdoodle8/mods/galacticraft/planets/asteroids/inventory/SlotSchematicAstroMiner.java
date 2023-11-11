package micdoodle8.mods.galacticraft.planets.asteroids.inventory;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class SlotSchematicAstroMiner extends Slot
{
    private final int index;
    private final int x;
    private final int y;
    private final int z;
    private final EntityPlayer player;

    public SlotSchematicAstroMiner(final IInventory par2IInventory, final int par3, final int par4, final int par5, final int x, final int y, final int z, final EntityPlayer player) {
        super(par2IInventory, par3, par4, par5);
        this.index = par3;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    public void onSlotChanged() {
        if (this.player instanceof EntityPlayerMP) {
            for (int var12 = 0; var12 < this.player.worldObj.playerEntities.size(); ++var12) {
                final EntityPlayerMP var13 = (EntityPlayerMP) this.player.worldObj.playerEntities.get(var12);
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
            case 1:
            case 3:
            case 5:
            case 11: {
                return par1ItemStack.getItem() == GCItems.heavyPlatingTier1;
            }
            case 2:
            case 4:
            case 9:
            case 10:
            case 12: {
                return par1ItemStack.getItem() == AsteroidsItems.orionDrive;
            }
            case 6: {
                return par1ItemStack.getItem() == GCItems.basicItem && par1ItemStack.getItemDamage() == 14;
            }
            case 7:
            case 8: {
                return par1ItemStack.getItem() == Item.getItemFromBlock((Block)Blocks.chest);
            }
            case 13: {
                return par1ItemStack.getItem() == AsteroidsItems.basicItem && par1ItemStack.getItemDamage() == 8;
            }
            case 14: {
                return par1ItemStack.getItem() == GCItems.flagPole;
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
