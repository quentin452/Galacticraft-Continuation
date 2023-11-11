package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.items.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;

public class BlockTelemetry extends BlockAdvancedTile implements ItemBlockDesc.IBlockShiftDesc
{
    private IIcon iconFront;
    private IIcon iconSide;
    
    protected BlockTelemetry(final String assetName) {
        super(Material.iron);
        this.setHardness(1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName("iron_block");
        this.setBlockName(assetName);
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.iconFront = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "cargo_pad");
        this.iconSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "cargo_pad");
    }
    
    public IIcon getIcon(final int side, final int metadata) {
        if (side == (metadata & 0x7)) {
            return this.iconSide;
        }
        return this.iconFront;
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        final int metadata = 0;
        final int angle = MathHelper.floor_double(entityLiving.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        int change = 0;
        switch (angle) {
            case 0: {
                change = 3;
                break;
            }
            case 1: {
                change = 4;
                break;
            }
            case 2: {
                change = 2;
                break;
            }
            case 3: {
                change = 5;
                break;
            }
        }
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
    }
    
    public boolean onUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final int facing = metadata & 0x3;
        int change = 0;
        switch (facing) {
            case 0: {
                change = 1;
                break;
            }
            case 1: {
                change = 3;
                break;
            }
            case 2: {
                change = 5;
                break;
            }
            case 3: {
                change = 4;
                break;
            }
            case 4: {
                change = 2;
                break;
            }
            case 5: {
                change = 0;
                break;
            }
        }
        change += (0xC & metadata);
        world.setBlockMetadataWithNotify(x, y, z, change, 2);
        return true;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityTelemetry();
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_) {
        if (!world.isRemote) {
            final TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityTelemetry) {
                final ItemStack held = player.inventory.getCurrentItem();
                if (held != null && held.getItem() == GCItems.basicItem && held.getItemDamage() == 19) {
                    NBTTagCompound fmData = held.stackTagCompound;
                    if (fmData != null && fmData.hasKey("linkedUUIDMost") && fmData.hasKey("linkedUUIDLeast")) {
                        final UUID uuid = new UUID(fmData.getLong("linkedUUIDMost"), fmData.getLong("linkedUUIDLeast"));
                        ((TileEntityTelemetry)tile).addTrackedEntity(uuid);
                        player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.telemetrySucceed.message")));
                    }
                    else {
                        player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.telemetryFail.message")));
                        if (fmData == null) {
                            fmData = new NBTTagCompound();
                            held.setTagCompound(fmData);
                        }
                    }
                    fmData.setInteger("teCoordX", x);
                    fmData.setInteger("teCoordY", y);
                    fmData.setInteger("teCoordZ", z);
                    fmData.setInteger("teDim", world.provider.dimensionId);
                    return true;
                }
                final ItemStack wearing = GCPlayerStats.get((EntityPlayerMP)player).frequencyModuleInSlot;
                if (wearing != null) {
                    if (wearing.hasTagCompound() && wearing.getTagCompound().hasKey("teDim")) {
                        return false;
                    }
                    player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.telemetryFailWearingIt.message")));
                }
                else {
                    player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.telemetryFailNoFrequencyModule.message")));
                }
            }
        }
        return false;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
