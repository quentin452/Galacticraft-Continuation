package micdoodle8.mods.galacticraft.planets.mars.schematic;

import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.planets.mars.client.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;

public class SchematicCargoRocket implements ISchematicPage
{
    public int getPageID() {
        return ConfigManagerMars.idSchematicCargoRocket;
    }
    
    public int getGuiID() {
        return 0 + "GalacticraftMars".hashCode();
    }
    
    public ItemStack getRequiredItem() {
        return new ItemStack(MarsItems.schematic, 1, 1);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getResultScreen(final EntityPlayer player, final int x, final int y, final int z) {
        return (GuiScreen)new GuiSchematicCargoRocket(player.inventory, x, y, z);
    }
    
    public Container getResultContainer(final EntityPlayer player, final int x, final int y, final int z) {
        return (Container)new ContainerSchematicCargoRocket(player.inventory, x, y, z);
    }
    
    public int compareTo(final ISchematicPage o) {
        if (this.getPageID() > o.getPageID()) {
            return 1;
        }
        return -1;
    }
}
