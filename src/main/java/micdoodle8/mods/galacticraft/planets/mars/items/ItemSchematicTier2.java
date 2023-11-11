package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.util.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemSchematicTier2 extends ItemSchematic implements ISchematicItem
{
    protected IIcon[] schematicIcons;
    public static final String[] names;
    
    public ItemSchematicTier2() {
        super("schematic");
        this.schematicIcons = new IIcon[1];
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemSchematicTier2.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.schematicIcons = new IIcon[ItemSchematicTier2.names.length];
        for (int i = 0; i < ItemSchematicTier2.names.length; ++i) {
            this.schematicIcons[i] = iconRegister.registerIcon("galacticraftmars:" + ItemSchematicTier2.names[i]);
        }
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.schematicIcons.length > damage) {
            return this.schematicIcons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (par2EntityPlayer.worldObj.isRemote) {
            switch (par1ItemStack.getItemDamage()) {
                case 0: {
                    par3List.add(GCCoreUtil.translate("schematic.rocketT3.name"));
                    break;
                }
                case 1: {
                    par3List.add(GCCoreUtil.translate("schematic.cargoRocket.name"));
                    break;
                }
                case 2: {
                    par3List.add(GCCoreUtil.translate("schematic.astroMiner.name"));
                    break;
                }
            }
        }
    }
    
    static {
        names = new String[] { "schematic_rocketT3", "schematic_rocket_cargo", "schematic_astroMiner" };
    }
}
