package micdoodle8.mods.galacticraft.planets.mars.blocks;

import net.minecraftforge.fluids.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.planets.*;

public class BlockSludge extends BlockFluidClassic
{
    @SideOnly(Side.CLIENT)
    IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    IIcon flowingIcon;
    
    public void onEntityCollidedWithBlock(final World world, final int x, final int y, final int z, final Entity entity) {
        if (!world.isRemote) {
            if ((entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying) || entity instanceof EntitySludgeling) {
                return;
            }
            final int range = 5;
            final List<?> l = (List<?>)world.getEntitiesWithinAABB((Class)EntitySludgeling.class, AxisAlignedBB.getBoundingBox((double)(x - range), (double)(y - range), (double)(z - range), (double)(x + range), (double)(y + range), (double)(z + range)));
            if (l.size() < 3) {
                final EntitySludgeling sludgeling = new EntitySludgeling(world);
                sludgeling.setPosition((double)(x + world.rand.nextInt(5) - 2), (double)y, (double)(z + world.rand.nextInt(5) - 2));
                world.spawnEntityInWorld((Entity)sludgeling);
            }
        }
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
    }
    
    public BlockSludge() {
        super(MarsModule.sludge, MarsModule.sludgeMaterial);
        this.setQuantaPerBlock(9);
        this.setRenderPass(1);
        this.setLightLevel(1.0f);
        this.needsRandomTick = true;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, final int par2) {
        return (par1 != 0 && par1 != 1) ? this.flowingIcon : this.stillIcon;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.stillIcon = par1IconRegister.registerIcon("galacticraftmars:sludge_still");
        this.flowingIcon = par1IconRegister.registerIcon("galacticraftmars:sludge_flow");
        MarsModule.sludge.setStillIcon(this.stillIcon);
        MarsModule.sludge.setFlowingIcon(this.flowingIcon);
    }
    
    public boolean canDisplace(final IBlockAccess world, final int x, final int y, final int z) {
        return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.canDisplace(world, x, y, z);
    }
    
    public boolean displaceIfPossible(final World world, final int x, final int y, final int z) {
        return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.displaceIfPossible(world, x, y, z);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);
        if (rand.nextInt(1200) == 0) {
            world.playSound((double)(x + 0.5f), (double)(y + 0.5f), (double)(z + 0.5f), "liquid.lava", rand.nextFloat() * 0.25f + 0.75f, 1.0E-5f + rand.nextFloat() * 0.5f, false);
        }
        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface((IBlockAccess)world, x, y - 1, z) && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {
            GalacticraftPlanets.spawnParticle("bacterialDrip", new Vector3((double)(x + rand.nextFloat()), y - 1.05, (double)(z + rand.nextFloat())), new Vector3(0.0, 0.0, 0.0), new Object[0]);
        }
    }
}
