package micdoodle8.mods.galacticraft.planets.mars.blocks;

import net.minecraft.block.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.core.items.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.potion.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockCavernousVine extends Block implements IShearable, ItemBlockDesc.IBlockShiftDesc
{
    @SideOnly(Side.CLIENT)
    private IIcon[] vineIcons;
    
    public BlockCavernousVine() {
        super(Material.vine);
        this.setLightLevel(1.0f);
        this.setTickRandomly(true);
        this.setStepSound(BlockCavernousVine.soundTypeGrass);
    }
    
    public MovingObjectPosition collisionRayTrace(final World world, final int x, final int y, final int z, final Vec3 vec3d, final Vec3 vec3d1) {
        return super.collisionRayTrace(world, x, y, z, vec3d, vec3d1);
    }
    
    public boolean removedByPlayer(final World world, final EntityPlayer player, final int x, final int y, final int z, final boolean willHarvest) {
        if (world.setBlockToAir(x, y, z)) {
            for (int y2 = y - 1; world.getBlock(x, y2, z) == this; --y2) {
                world.setBlockToAir(x, y2, z);
            }
            return true;
        }
        return false;
    }
    
    public boolean canBlockStay(final World world, final int x, final int y, final int z) {
        final Block blockAbove = world.getBlock(x, y + 1, z);
        return blockAbove == this || blockAbove.getMaterial().isSolid();
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        if (!this.canBlockStay(world, x, y, z)) {
            world.setBlockToAir(x, y, z);
        }
    }
    
    public void onEntityCollidedWithBlock(final World world, final int x, final int y, final int z, final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying) {
                return;
            }
            entity.motionY = 0.05999999865889549;
            entity.rotationYaw += 0.4f;
            if (!((EntityLivingBase)entity).getActivePotionEffects().contains(Potion.poison)) {
                ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.poison.id, 5, 20, false));
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        this.vineIcons = new IIcon[3];
        for (int i = 0; i < 3; ++i) {
            this.vineIcons[i] = iconRegister.registerIcon("galacticraftmars:vine_" + i);
        }
    }
    
    public int getLightValue(final IBlockAccess world, final int x, final int y, final int z) {
        return this.getVineLight(world, x, y, z);
    }
    
    public IIcon getIcon(final int side, final int meta) {
        if (meta < 3) {
            return this.vineIcons[meta];
        }
        return super.getIcon(side, meta);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public int getRenderType() {
        return GalacticraftPlanets.getBlockRenderID((Block)this);
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        return null;
    }
    
    public boolean canPlaceBlockOnSide(final World world, final int x, final int y, final int z, final int side) {
        return ForgeDirection.getOrientation(side) == ForgeDirection.DOWN && this.isBlockSolid((IBlockAccess)world, x, y + 1, z, side);
    }
    
    public int getVineLength(final IBlockAccess world, final int x, final int y, final int z) {
        int vineCount = 0;
        for (int y2 = y; world.getBlock(x, y2, z) == MarsBlocks.vine; ++y2) {
            ++vineCount;
        }
        return vineCount;
    }
    
    public int getVineLight(final IBlockAccess world, final int x, final int y, final int z) {
        int vineCount = 0;
        for (int y2 = y; world.getBlock(x, y2, z) == MarsBlocks.vine; --y2) {
            vineCount += 4;
        }
        return Math.max(19 - vineCount, 0);
    }
    
    public int tickRate(final World par1World) {
        return 50;
    }
    
    public void updateTick(final World world, final int x, final int y, final int z, final Random rand) {
        if (!world.isRemote) {
            for (int y2 = y - 1; y2 >= y - 2; --y2) {
                final Block blockID = world.getBlock(x, y2, z);
                if (blockID == null || !blockID.isAir((IBlockAccess)world, x, y, z)) {
                    return;
                }
            }
            world.setBlock(x, y - 1, z, (Block)this, this.getVineLength((IBlockAccess)world, x, y, z) % 3, 2);
            world.func_147451_t(x, y, z);
        }
    }
    
    public void onBlockAdded(final World world, final int x, final int y, final int z) {
        if (!world.isRemote) {}
    }
    
    public Item getItemDropped(final int par1, final Random par2Random, final int par3) {
        return Item.getItemFromBlock(Blocks.air);
    }
    
    public int quantityDropped(final Random par1Random) {
        return 0;
    }
    
    public void harvestBlock(final World par1World, final EntityPlayer par2EntityPlayer, final int par3, final int par4, final int par5, final int par6) {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }
    
    public boolean isShearable(final ItemStack item, final IBlockAccess world, final int x, final int y, final int z) {
        return true;
    }
    
    public ArrayList<ItemStack> onSheared(final ItemStack item, final IBlockAccess world, final int x, final int y, final int z, final int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack((Block)this, 1, 0));
        return ret;
    }
    
    public boolean isLadder(final IBlockAccess world, final int x, final int y, final int z, final EntityLivingBase entity) {
        return true;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
